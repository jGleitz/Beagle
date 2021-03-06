package de.uka.ipd.sdq.beagle.measurement.kieker.instrumentation;

import de.uka.ipd.sdq.beagle.core.CodeSection;
import de.uka.ipd.sdq.beagle.core.failurehandling.FailureHandler;
import de.uka.ipd.sdq.beagle.core.failurehandling.FailureReport;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Instruments code sections using Eclipse’s JDT Abstract Syntax Tree. The instrumentor is
 * configured through {@linkplain EclipseAstInstrumentationStrategy instrumentation
 * strategies}, providing the statements that shall be inserted.
 *
 * <p>Instrumentation takes place on a best effort base. It is performed as follows:
 *
 * <ul>
 *
 * <li>The instrumentor queries the {@linkplain EclipseAstInstrumentationStrategy
 * instrumentation strategy} reported through {@link #useStrategy} for instrumentation
 * instructions to instrument the code sections provided through the same method call.
 *
 * <li>{@linkplain EclipseAstInstrumentationStrategy#instrumentStart(CodeSection, AST)
 * start instrumentation instructions} will be placed before the instrumented code
 * section. There are no guarantees for the number of statements executed between the
 * instrumentation instruction and the instrumented code section. However, the
 * instrumentor tries to keep this number as small as possible.
 *
 * <li>{@linkplain EclipseAstInstrumentationStrategy#instrumentEnd(CodeSection, AST) end
 * instrumentation instructions} will be placed after the instrumented code section. There
 * are no guarantees for the number of statements executed between the instrumented code
 * section and the instrumentation instruction. However, the instrumentor tries to keep
 * this number as small as possible.
 *
 * <li>After successful instrumentation, the instrumentor queries the
 * {@linkplain InstrumentationResultFileProvider} for each compilation unit it manipulated
 * to get a file to write the result to. It then writes the instrumented source code to
 * that file.
 *
 * <li>The instrumentor will report failures through the {@linkplain FailureHandler
 * Failure API}. It will report failures in batches instead for every file. Such failures
 * may be:
 *
 * <ul>
 *
 * <li>Input/Output errors while trying to read a source file.
 *
 * <li>Input/Output errors while trying to write to an output file.
 *
 * <li>The input source file doesn’t define a Java type.
 *
 * </ul>
 *
 * </ul>
 *
 * <p>The result of the instrumentation is undefiend for a source file referenced by a
 * code section that is not a valid Java file defining a class.
 *
 * <p>The instrumentation is executed using multible threads. However, the class itself is
 * not thread safe and may thus not be called from multible threads.
 *
 * @author Joshua Gleitze
 */
public class EclipseAstInstrumentor {

	/**
	 * Handler of failures.
	 */
	private static final FailureHandler FAILURE_HANDLER = FailureHandler.getHandler("Source code instrumentor");

	/**
	 * The number of milliseconds in a minute.
	 */
	private static final int MILLISECONDS_IN_A_MINUTE = 1000 * 60;

	/**
	 * Stores what we need to instrument. For each file, we store the character positions
	 * of the statements that shall be instrumented. For these statement, we store
	 * information about how to instrument at this statement.startInstrumentors
	 */
	private final Map<Path, Map<Integer, InstrumentationInformation>> toInstrument = new HashMap<>();

	/**
	 * The appointer of source files to write the instrumentation results to.
	 */
	private final InstrumentationResultFileProvider fileProvider;

	/**
	 * The charset to use.
	 */
	private Charset charset = Charset.defaultCharset();

	/**
	 * How long to wait for the whole instrumentation to finish, before aborting it.
	 */
	private final int instrumentationTimeoutInMinutes = 60;

	/**
	 * Creates an instrumentor that writes its resulting instrumented code to the files
	 * provided by {@code instrumentedFileProvider}.
	 *
	 * @param instrumentedFileProvider A function taking a fully qualified path of a Java
	 *            type and returning a file to write the instrumented code of this type
	 *            to. Must not be {@code null}.
	 */
	public EclipseAstInstrumentor(final InstrumentationResultFileProvider instrumentedFileProvider) {
		Validate.notNull(instrumentedFileProvider);
		this.fileProvider = instrumentedFileProvider;
	}

	/**
	 * Makes this instrumentor use the provided strategy to instrument the provided code
	 * sections.
	 *
	 * @param instrumentationStrategy The strategy to instrument with.
	 * @param codeSections The sections that shall be instrumented using the provided
	 *            strategy.
	 * @return {@code this}.
	 */
	public EclipseAstInstrumentor useStrategy(final EclipseAstInstrumentationStrategy instrumentationStrategy,
		final Collection<CodeSection> codeSections) {
		Validate.notNull(instrumentationStrategy);
		Validate.noNullElements(codeSections);

		for (final CodeSection codeSection : codeSections) {
			this.forCharacterPosition(codeSection.getStartFile().toPath(), codeSection.getStartSectionIndex())
				.addBeforeStatementFunction((factory) -> instrumentationStrategy.instrumentStart(codeSection, factory));
			this.forCharacterPosition(codeSection.getEndFile().toPath(), codeSection.getEndSectionIndex())
				.addAfterStatementFunction((factory) -> instrumentationStrategy.instrumentEnd(codeSection, factory));
		}
		return this;
	}

	/**
	 * Makes this instrumentor use the provided strategy to instrument the provided code
	 * sections.
	 *
	 * @param instrumentationStrategy The strategy to instrument with.
	 * @param codeSections The sections that shall be instrumented using the provided
	 *            strategy.
	 * @return {@code this}.
	 */
	public EclipseAstInstrumentor useStrategy(final EclipseAstInstrumentationStrategy instrumentationStrategy,
		final CodeSection... codeSections) {
		Validate.notNull(instrumentationStrategy);
		return this.useStrategy(instrumentationStrategy, new HashSet<>(Arrays.asList(codeSections)));
	}

	/**
	 * Makes the instrumentor use {@code charset} to read and write source code. It
	 * defaults to using {@link Charset#defaultCharset()} if no charset was set.
	 *
	 * @param ioCharset The charset to be used by this Instrumentor for input and output.
	 * @return {@code this}.
	 */
	public EclipseAstInstrumentor useCharset(final Charset ioCharset) {
		Validate.notNull(ioCharset);
		this.charset = ioCharset;
		return this;
	}

	/**
	 * Executes the instrumentation based on the settings set on this instrumentor.
	 */
	public void instrument() {
		this.instrumentFiles(this.toInstrument.keySet());
	}

	/**
	 * Performs the instrumentation. Creates a task executing
	 * {@link #instrumentFile(File, Map)} for each file that is to be instrumented.
	 *
	 * @param files The files to instrument. Must be a subset of {@link #toInstrument}’s
	 *            key set.
	 */
	private void instrumentFiles(final Collection<Path> files) {
		final ExecutorService executionService = Executors.newCachedThreadPool();
		final List<Path> todoFiles = new ArrayList<>(files);
		final List<Future<?>> promises = new ArrayList<>(todoFiles.size());

		// submit all files to be instrumented
		for (final Path todoFile : todoFiles) {
			final Map<Integer, InstrumentationInformation> statementInformation = this.toInstrument.get(todoFile);
			promises.add(executionService.submit(() -> this.instrumentFile(todoFile, statementInformation)));
		}

		executionService.shutdown();
		final long timeIWentToSleep = System.currentTimeMillis();
		long minutesISlept = 0;
		while (minutesISlept < this.instrumentationTimeoutInMinutes && !executionService.isTerminated()) {
			try {
				// wait for instrumentation to finish
				executionService.awaitTermination(this.instrumentationTimeoutInMinutes, TimeUnit.MINUTES);
			} catch (final InterruptedException interruption) {
				minutesISlept = (System.currentTimeMillis() - timeIWentToSleep) / MILLISECONDS_IN_A_MINUTE;
			}
		}

		executionService.shutdownNow();

		this.verifyInstrumentationResults(todoFiles, promises);
	}

	/**
	 * Verifies that executing the instrumentation succeeded for all files. Takes
	 * according action if a failure is detected.
	 *
	 * @param promisedFiles The list of files that were to be instrumented.
	 * @param promises The list of futures for the successful instrumentation. Must be
	 *            ordered such that the the ith future refers to the instrumentation of
	 *            the ith file in {@code promisedFiles}.
	 */
	private void verifyInstrumentationResults(final List<Path> promisedFiles, final List<Future<?>> promises) {
		final Map<Path, String> instrumentationFailures = new HashMap<>();

		// a task might still be running at this point. We furthermore don’t know if it
		// succeeded.
		final Iterator<Path> fileIterator = promisedFiles.iterator();
		for (final Future<?> promise : promises) {
			final Path promisedFile = fileIterator.next();

			if (promise.cancel(true)) {
				// the task failed to finish
				instrumentationFailures.put(promisedFile,
					String.format("The instrumentation was aborted because it took to long. (%d minutes allowed)",
						this.instrumentationTimeoutInMinutes));
			} else {
				// has finished. Let’s see if there was an error.
				try {
					promise.get();
				} catch (final ExecutionException executionException) {
					instrumentationFailures.put(promisedFile, toFailureString(executionException.getCause()));
				} catch (final InterruptedException interruption) {
					instrumentationFailures.put(promisedFile, toFailureString(interruption));
				}
			}

		}

		if (!instrumentationFailures.isEmpty()) {
			final FailureReport<Void> readFailure = new FailureReport<Void>()
				.message("Could not instrument %d of %d file%s.", instrumentationFailures.size(), promisedFiles.size(),
					promisedFiles.size() == 1 ? "" : "s")
				.recoverable()
				.retryWith(() -> this.instrumentFiles(instrumentationFailures.keySet()));

			for (final Entry<Path, String> readFailEntry : instrumentationFailures.entrySet()) {
				readFailure.details("%s: %s\n", readFailEntry.getKey().toString(), readFailEntry.getValue());
			}

			FAILURE_HANDLER.handle(readFailure);
		}
	}

	/*
	 * This method contains the class’ core logic. The author thinks that splitting it
	 * would lead to more confusion and less understanding of the algorithm. Therefore, he
	 * thinks the high cyclomatic complexity of 12 is acceptable. But he’ll gladly accept
	 * suggestions for simplification.
	 */
	/**
	 * The core instrumentation method. Reads in one file, instruments it, and writes the
	 * result back.
	 *
	 * @param sourceCodeFile The source code file to instrument.
	 * @param statementInformation Information about what is to be instrumented.
	 * @return {@code null}.
	 * @throws IOException If reading from the input or writing to the output file causes
	 *             an input/output error.
	 * @throws InstrumentationImpossibleException If correct instrumentation is logically
	 *             impossible.
	 */
	// CHECKSTYLE:IGNORE CyclomaticComplexity
	private Void instrumentFile(final Path sourceCodeFile,
		final Map<Integer, InstrumentationInformation> statementInformation)
			throws IOException, InstrumentationImpossibleException {

		// This method should have only been called if this is the case.
		assert !statementInformation.isEmpty();

		final EclipseAstBridge astIO = new EclipseAstBridge(sourceCodeFile, this.charset);
		final CompilationUnit compilationUnit = astIO.getAst();

		if ((compilationUnit.getFlags() & ASTNode.MALFORMED) == ASTNode.MALFORMED) {
			throw new InstrumentationImpossibleException("The file contains syntax errors");
		}

		final Map<Integer, ASTNode> astStatements =
			new InstrumentableAstNodeLister().getStatementIndicesIn(compilationUnit);
		final List<Integer> sortedAstNodeIndeces = new ArrayList<>(astStatements.keySet());
		final List<Integer> sortedNodesToInstrumentIndeces = new ArrayList<>(statementInformation.keySet());
		Collections.sort(sortedAstNodeIndeces);
		Collections.sort(sortedNodesToInstrumentIndeces);

		if (sortedNodesToInstrumentIndeces.get(sortedNodesToInstrumentIndeces.size() - 1) > compilationUnit
			.getLength()) {
			throw new InstrumentationImpossibleException(
				"There was an attempt to insert a statement after the end of the compilation unit.");
		}
		if (sortedAstNodeIndeces.size() < 1) {
			throw new InstrumentationImpossibleException("There are no executable statements in the file.");
		}

		// To catch the last target statements.
		sortedAstNodeIndeces.add(compilationUnit.getLength());

		final Iterator<Integer> toFind = sortedNodesToInstrumentIndeces.iterator();
		final Iterator<Integer> available = sortedAstNodeIndeces.iterator();

		// whether we touched the AST at all
		boolean modified = false;
		final int noMore = -1;
		int momentaryCharIndex = available.next();
		int lowerBound = 0;
		int upperBound = 0;
		int lookingFor = toFind.next();

		/*
		 * In the following, the (integer) interval [lowerBound, upperBound) contains all
		 * char indeces that are closest to the start index of
		 * astStatements.get(momentaryCharIndex) (ignoring the off-by-one errors caused by
		 * integer division). We thus instrument using all instructions given for char
		 * indeces in this interval.
		 */
		do {
			final int nextCharIndex = available.next();
			// The addition is divided by 2 to get the “middle” between momentaryCharIndex
			// and nextCharIndex
			// CHECKSTYLE:IGNORE MagicNumber
			upperBound = (momentaryCharIndex + nextCharIndex) / 2;

			InstrumentationInformation instructions = null;

			// see if the next instrumentation we’re looking for belongs to the node we’re
			// looking at. If yes, see if there are others.
			while (lookingFor != noMore && lookingFor >= lowerBound && lookingFor < upperBound) {
				instructions = InstrumentationInformation.merge(instructions, statementInformation.get(lookingFor));
				lookingFor = toFind.hasNext() ? toFind.next() : noMore;
			}
			if (instructions != null) {
				// we have something to instrument for the node we’re looking at. So let’s
				// insert!
				final ASTNode astNodeToInstrument = astStatements.get(momentaryCharIndex);
				modified = true;

				final AstStatementInserter inserter = new AstStatementInserter(astNodeToInstrument);
				final AST nodeFactory = astNodeToInstrument.getAST();

				if (!inserter.setUp()) {
					throw new InstrumentationImpossibleException(
						"The following code is to be instrumented, but is not within code that statements could be added to: %s",
						astNodeToInstrument);
				}

				instructions.getBeforeStatements(nodeFactory).forEachOrdered(inserter::insertBefore);
				instructions.getAfterStatements(nodeFactory).forEachOrdered(inserter::insertAfter);
			}

			lowerBound = upperBound;
			momentaryCharIndex = nextCharIndex;
		} while (available.hasNext() && lookingFor != noMore);

		if (modified) {
			final String fullyQualifedTypeName = astIO.getFullyQualifiedName();
			final Path writeBackFile = this.fileProvider.getFileFor(fullyQualifedTypeName);
			astIO.writeToFile(writeBackFile);
		}

		return null;
	}

	/**
	 * Gets the instrumentation info for the given {@code character} in the given
	 * {@code sourceCodeFile}. Create a new information object if none yet exists.
	 *
	 * @param sourceCodeFile The file to obtain the information for.
	 * @param character The character in {@code sourceCodeFile} to obtain the information
	 *            for.
	 * @return The instrumentation information for the {@code character} in a
	 *         {@code sourceCodeFile}.
	 */
	private InstrumentationInformation forCharacterPosition(final Path sourceCodeFile, final int character) {
		Map<Integer, InstrumentationInformation> fileMap = this.toInstrument.get(sourceCodeFile);
		if (fileMap == null) {
			fileMap = new HashMap<>();
			this.toInstrument.put(sourceCodeFile, fileMap);
		}
		InstrumentationInformation info = fileMap.get(character);
		if (info == null) {
			info = new InstrumentationInformation();
			fileMap.put(character, info);
		}
		return info;
	}

	/**
	 * Converts an exception into a string that can be used to report a failure in
	 * {@link #instrumentationFailures}.
	 *
	 * @param exception An exception indicating a failure.
	 * @return A string representing {@code exception}.
	 */
	private static String toFailureString(final Throwable exception) {
		final String message = exception.getMessage() != null ? exception.getMessage() : "No message provided.";
		return String.format("%s (%s)", message, exception.getClass().getSimpleName());
	}

	/**
	 * Appoints a file the instrumented source code of a Java type shall be written to.
	 *
	 * @author Joshua Gleitze
	 */
	public interface InstrumentationResultFileProvider {

		/**
		 * Appoints a file the instrumented source code of the Java type denoted by
		 * {@code fullyQualifiedName} shall be written to.
		 *
		 * @param fullyQualifiedName The fully qualified path of a Java type. Must not be
		 *            {@code null}.
		 * @return A file the write the instrumented code to. The file does not need to
		 *         exist. Will never be {@code null}.
		 */
		Path getFileFor(final String fullyQualifiedName);
	}
}
