package de.uka.ipd.sdq.beagle.core.pcmsourcestatementlink;

import de.uka.ipd.sdq.beagle.core.failurehandling.FailureHandler;
import de.uka.ipd.sdq.beagle.core.failurehandling.FailureReport;

import org.apache.commons.lang3.Validate;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Reads in instances of the PCM Source Statement Links Model given in a XML file.
 * Performs integrity checks on the input.
 *
 * @author Joshua Gleitze
 */
public class PcmSourceStatementLinkReader {

	/**
	 * The handler of failures.
	 */
	private static final FailureHandler FAILURE_HANDLER =
		FailureHandler.getHandler("Source Statement Links Model Reader");

	/**
	 * The file that will be read in.
	 */
	private final File inputFile;

	/**
	 * The repository as it was read in from the {@link inputFile}.
	 */
	private PcmSourceStatementLinkRepository linkRepository;

	/**
	 * Creates a reader to read the provided {@code linkRepositoryFile}.
	 *
	 * @param linkRepositoryFile The file to read in.
	 */
	public PcmSourceStatementLinkReader(final File linkRepositoryFile) {
		Validate.notNull(linkRepositoryFile);
		Validate.isTrue(linkRepositoryFile.exists());
		this.inputFile = linkRepositoryFile;
	}

	/**
	 * Gets the source link repository that was read in from the input file. If reading in
	 * the input file fails, the method will report to the {@linkplain FailureHandler
	 * failure API}.
	 *
	 * @return The repository that was read in from the input file.
	 */
	public PcmSourceStatementLinkRepository getPcmSourceLinkRepository() {
		this.readIn();
		return this.linkRepository;
	}

	/**
	 * Reads the {@link #inputFile} and populates {@link #linkRepository}. Reports a
	 * failure to the failure handler if reading in fails.
	 */
	private void readIn() {
		if (this.linkRepository != null) {
			return;
		}

		final Object result;
		try {
			final JAXBContext context = JAXBContext.newInstance("de.uka.ipd.sdq.beagle.core.pcmsourcestatementlink");
			final Unmarshaller unmarshaller = context.createUnmarshaller();

			result = unmarshaller.unmarshal(this.inputFile);
		} catch (final JAXBException unmarshalError) {
			final FailureReport<Void> failure = new FailureReport<Void>().cause(unmarshalError).retryWith(this::readIn);
			FAILURE_HANDLER.handle(failure);
			return;
		}

		this.linkRepository = (PcmSourceStatementLinkRepository) result;
	}
}
