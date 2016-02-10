package de.uka.ipd.sdq.beagle.core;

import static de.uka.ipd.sdq.beagle.core.testutil.ExceptionThrownMatcher.throwsException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.uka.ipd.sdq.beagle.core.analysis.MeasurementResultAnalyser;
import de.uka.ipd.sdq.beagle.core.analysis.MeasurementResultAnalyserBlackboardView;
import de.uka.ipd.sdq.beagle.core.analysis.ProposedExpressionAnalyser;
import de.uka.ipd.sdq.beagle.core.analysis.ProposedExpressionAnalyserBlackboardView;
import de.uka.ipd.sdq.beagle.core.analysis.ReadOnlyMeasurementResultAnalyserBlackboardView;
import de.uka.ipd.sdq.beagle.core.analysis.ReadOnlyProposedExpressionAnalyserBlackboardView;
import de.uka.ipd.sdq.beagle.core.measurement.MeasurementTool;
import de.uka.ipd.sdq.beagle.core.measurement.order.MeasurementOrder;
import de.uka.ipd.sdq.beagle.core.testutil.ThrowingMethod;
import de.uka.ipd.sdq.beagle.core.testutil.factories.BlackboardFactory;
import de.uka.ipd.sdq.beagle.core.testutil.factories.ExtensionPointToolsFactory;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests for {@link AnalysisController}.
 *
 * @author Roman Langrehr
 */
public class AnalysisControllerTest {

	/**
	 * A default {@link BlackboardFactory} for the tests.
	 */
	private static final BlackboardFactory BLACKBOARD_FACTORY = new BlackboardFactory();

	/**
	 * A default {@link ExtensionPointToolsFactory} for the tests.
	 */
	private static final ExtensionPointToolsFactory EXTENSION_POINT_FACTORY = new ExtensionPointToolsFactory();

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedMeasurementTool1)}.
	 */
	private final MeasurementTool mockedMeasurementTool1 = mock(MeasurementTool.class);

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedMeasurementTool2)}.
	 */
	private final MeasurementTool mockedMeasurementTool2 = mock(MeasurementTool.class);

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedMeasurementTool3)}.
	 */
	private final MeasurementTool mockedMeasurementTool3 = mock(MeasurementTool.class);

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedMeasurementResultAnalyser1)}.
	 */
	private final MeasurementResultAnalyser mockedMeasurementResultAnalyser1 = mock(MeasurementResultAnalyser.class);

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedMeasurementResultAnalyser2)}.
	 */
	private final MeasurementResultAnalyser mockedMeasurementResultAnalyser2 = mock(MeasurementResultAnalyser.class);

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedMeasurementResultAnalyser3)}.
	 */
	private final MeasurementResultAnalyser mockedMeasurementResultAnalyser3 = mock(MeasurementResultAnalyser.class);

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedProposedExpressionAnalyser1)}.
	 */
	private final ProposedExpressionAnalyser mockedProposedExpressionAnalyser1 = mock(ProposedExpressionAnalyser.class);

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedProposedExpressionAnalyser2)}.
	 */
	private final ProposedExpressionAnalyser mockedProposedExpressionAnalyser2 = mock(ProposedExpressionAnalyser.class);

	/**
	 * A mock for {@link MeasurementTool}. Should be reseted before each use with
	 * {@code reset(mockedProposedExpressionAnalyser3)}.
	 */
	private final ProposedExpressionAnalyser mockedProposedExpressionAnalyser3 = mock(ProposedExpressionAnalyser.class);

	/**
	 * Tests
	 * {@link AnalysisController#AnalysisController(Blackboard, java.util.Set, java.util.Set, java.util.Set)}
	 * .
	 *
	 */
	@Test
	public void constructor() {
		new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(),
			EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(),
			EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(),
			EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());

		// Empty input should make no problem
		new AnalysisController(BLACKBOARD_FACTORY.getEmpty(), EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(),
			EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(),
			EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());
		new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(), new HashSet<>(),
			EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(),
			EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());
		new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(),
			EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(), new HashSet<>(),
			EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());
		new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(),
			EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(),
			EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(), new HashSet<>());

		// Null tests
		final ThrowingMethod method = () -> {
			new AnalysisController(null, EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(),
				EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(),
				EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());
		};
		assertThat("Blackboard must not be null.", method, throwsException(NullPointerException.class));
		final ThrowingMethod method2 = () -> {
			new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(), null,
				EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(),
				EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());
		};
		assertThat("Measurement tools set must not be null.", method2, throwsException(NullPointerException.class));
		final ThrowingMethod method3 = () -> {
			new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(),
				EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(), null,
				EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());
		};
		assertThat("Mesasurement result analysers set must not be null.", method3,
			throwsException(NullPointerException.class));
		final ThrowingMethod method4 = () -> {
			new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(),
				EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(),
				EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(), null);
		};
		assertThat("Proposed expression analysers set must not be null.", method4,
			throwsException(NullPointerException.class));

		final Set<MeasurementTool> measurementToolsWithNull = EXTENSION_POINT_FACTORY.createNewMeasurementToolSet();
		measurementToolsWithNull.add(null);
		final Set<MeasurementResultAnalyser> measurementResultAnalyserWithNull =
			EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet();
		measurementResultAnalyserWithNull.add(null);
		final Set<ProposedExpressionAnalyser> proposedExpressionAnalysersWithNull =
			EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet();
		proposedExpressionAnalysersWithNull.add(null);
		final ThrowingMethod method5 = () -> {
			new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(), measurementToolsWithNull,
				EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(),
				EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());
		};
		assertThat("Set must not contain null.", method5, throwsException(IllegalArgumentException.class));
		final ThrowingMethod method6 = () -> {
			new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(),
				EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(),
				EXTENSION_POINT_FACTORY.createNewMeasurementResultAnalysersSet(), proposedExpressionAnalysersWithNull);
		};
		assertThat("Set must not contain null.", method6, throwsException(IllegalArgumentException.class));
		final ThrowingMethod method7 = () -> {
			new AnalysisController(BLACKBOARD_FACTORY.getWithToBeMeasuredContent(),
				EXTENSION_POINT_FACTORY.createNewMeasurementToolSet(), measurementResultAnalyserWithNull,
				EXTENSION_POINT_FACTORY.createNewProposedExpressionAnalyserSet());
		};
		assertThat("Set must not contain null.", method7, throwsException(IllegalArgumentException.class));

		// Immutable tests
		// Immutability is verified by calls to {@link
		final Blackboard blackboard = BLACKBOARD_FACTORY.getWithToBeMeasuredContent();
		// AnalysisController#performAnalysis()}.
		this.resetMocks();
		final Set<MeasurementTool> allMeasurementTools = new HashSet<>();
		allMeasurementTools.add(this.mockedMeasurementTool1);
		allMeasurementTools.add(this.mockedMeasurementTool2);
		allMeasurementTools.add(this.mockedMeasurementTool3);
		final Set<MeasurementResultAnalyser> allMeasurementResultAnalysers = new HashSet<>();
		allMeasurementResultAnalysers.add(this.mockedMeasurementResultAnalyser1);
		allMeasurementResultAnalysers.add(this.mockedMeasurementResultAnalyser2);
		allMeasurementResultAnalysers.add(this.mockedMeasurementResultAnalyser3);
		final Set<ProposedExpressionAnalyser> allProposedExpressionAnalysers = new HashSet<>();
		allProposedExpressionAnalysers.add(this.mockedProposedExpressionAnalyser1);
		allProposedExpressionAnalysers.add(this.mockedProposedExpressionAnalyser2);
		allProposedExpressionAnalysers.add(this.mockedProposedExpressionAnalyser3);
		new AnalysisController(blackboard, allMeasurementTools, allMeasurementResultAnalysers,
			allProposedExpressionAnalysers);
		final Set<MeasurementTool> originalMeasurementTools = new HashSet<>(allMeasurementTools);
		final Set<MeasurementResultAnalyser> originalMeasurementResultAnalysers =
			new HashSet<>(allMeasurementResultAnalysers);
		final Set<ProposedExpressionAnalyser> originalProposedExpressionAnalysers =
			new HashSet<>(allProposedExpressionAnalysers);
		allMeasurementTools.clear();
		allMeasurementResultAnalysers.clear();
		allProposedExpressionAnalysers.clear();

		// Assert that the measurement tools were asked.
		for (final MeasurementTool measurementTool : originalMeasurementTools) {
			verify(measurementTool).measure((MeasurementOrder) notNull());
		}
		for (final MeasurementResultAnalyser measurementResultAnalyser : originalMeasurementResultAnalysers) {
			verify(measurementResultAnalyser)
				.canContribute(Matchers.eq(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard)));
		}
		for (final ProposedExpressionAnalyser proposedExpressionAnalyser : originalProposedExpressionAnalysers) {
			verify(proposedExpressionAnalyser)
				.canContribute(Matchers.eq(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard)));
		}
	}

	/**
	 * Tests {@link AnalysisController#performAnalysis()}. Asserts that all tools are used
	 * in the correct order.
	 *
	 */
	@Test
	public void performAnalysis() {
		final Set<MeasurementTool> oneMeasurementTool = new HashSet<>();
		oneMeasurementTool.add(this.mockedMeasurementTool1);
		final Set<MeasurementResultAnalyser> oneMeasurementResultAnalyser = new HashSet<>();
		oneMeasurementResultAnalyser.add(this.mockedMeasurementResultAnalyser1);
		final Set<ProposedExpressionAnalyser> oneProposedExpressionAnalyser = new HashSet<>();
		oneProposedExpressionAnalyser.add(this.mockedProposedExpressionAnalyser1);

		// Test, where no one wants to contribute.
		this.resetMocks();
		final Blackboard blackboard = BLACKBOARD_FACTORY.getWithToBeMeasuredContent();
		when(this.mockedMeasurementResultAnalyser1
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard))).thenReturn(false);
		when(this.mockedProposedExpressionAnalyser1
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard))).thenReturn(false);

		final AnalysisController analysisController = new AnalysisController(blackboard, oneMeasurementTool,
			oneMeasurementResultAnalyser, oneProposedExpressionAnalyser);
		analysisController.performAnalysis();
		verify(this.mockedMeasurementResultAnalyser1, never()).contribute(anyObject());
		verify(this.mockedProposedExpressionAnalyser1, never()).contribute(anyObject());

		this.resetMocks();
		final Set<MeasurementTool> allMeasurementTools = new HashSet<>();
		allMeasurementTools.add(this.mockedMeasurementTool1);
		allMeasurementTools.add(this.mockedMeasurementTool2);
		allMeasurementTools.add(this.mockedMeasurementTool3);
		final Set<MeasurementResultAnalyser> allMeasurementResultAnalysers = new HashSet<>();
		allMeasurementResultAnalysers.add(this.mockedMeasurementResultAnalyser1);
		allMeasurementResultAnalysers.add(this.mockedMeasurementResultAnalyser2);
		allMeasurementResultAnalysers.add(this.mockedMeasurementResultAnalyser3);
		final Set<ProposedExpressionAnalyser> allProposedExpressionAnalysers = new HashSet<>();
		allProposedExpressionAnalysers.add(this.mockedProposedExpressionAnalyser1);
		allProposedExpressionAnalysers.add(this.mockedProposedExpressionAnalyser2);
		allProposedExpressionAnalysers.add(this.mockedProposedExpressionAnalyser3);
		final Blackboard blackboard2 = BLACKBOARD_FACTORY.getWithToBeMeasuredContent();
		when(this.mockedMeasurementResultAnalyser1
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard2))).thenReturn(false);
		when(this.mockedMeasurementResultAnalyser2
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard2))).thenReturn(false);
		when(this.mockedMeasurementResultAnalyser3
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard2))).thenReturn(false);
		when(this.mockedProposedExpressionAnalyser1
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard2))).thenReturn(false);
		when(this.mockedProposedExpressionAnalyser2
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard2))).thenReturn(false);
		when(this.mockedProposedExpressionAnalyser3
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard2))).thenReturn(false);

		final AnalysisController analysisController2 = new AnalysisController(blackboard2, allMeasurementTools,
			allMeasurementResultAnalysers, allProposedExpressionAnalysers);
		analysisController2.performAnalysis();
		verify(this.mockedMeasurementResultAnalyser1, never()).contribute(anyObject());
		verify(this.mockedMeasurementResultAnalyser2, never()).contribute(anyObject());
		verify(this.mockedMeasurementResultAnalyser3, never()).contribute(anyObject());
		verify(this.mockedProposedExpressionAnalyser1, never()).contribute(anyObject());
		verify(this.mockedProposedExpressionAnalyser2, never()).contribute(anyObject());
		verify(this.mockedProposedExpressionAnalyser3, never()).contribute(anyObject());

		// Assert that MeasurementTool#measure is not called, when the blackboard is empty
		// (-> nothing to measure).
		this.resetMocks();
		final AnalysisController analysisController3 = new AnalysisController(BLACKBOARD_FACTORY.getEmpty(),
			allMeasurementTools, allMeasurementResultAnalysers, allProposedExpressionAnalysers);
		analysisController3.performAnalysis();
		verify(this.mockedMeasurementTool1, never()).measure(anyObject());
		verify(this.mockedMeasurementTool2, never()).measure(anyObject());
		verify(this.mockedMeasurementTool3, never()).measure(anyObject());

		// Verify that everybody can contribute, if he wants.
		this.resetMocks();
		final Blackboard blackboard3 = BLACKBOARD_FACTORY.getWithToBeMeasuredContent();
		when(this.mockedMeasurementResultAnalyser1
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard3))).thenReturn(true);
		when(this.mockedMeasurementResultAnalyser2
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard3))).thenReturn(true);
		when(this.mockedMeasurementResultAnalyser3
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard3))).thenReturn(true);
		when(this.mockedProposedExpressionAnalyser1
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard3))).thenReturn(true);
		when(this.mockedProposedExpressionAnalyser2
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard3))).thenReturn(true);
		when(this.mockedProposedExpressionAnalyser3
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard3))).thenReturn(true);

		final AnalysisController analysisController4 = new AnalysisController(blackboard3, allMeasurementTools,
			allMeasurementResultAnalysers, allProposedExpressionAnalysers);
		analysisController4.performAnalysis();
		verify(this.mockedMeasurementResultAnalyser1, atLeastOnce())
			.contribute(eq(new MeasurementResultAnalyserBlackboardView(blackboard3)));
		verify(this.mockedMeasurementResultAnalyser2, atLeastOnce())
			.contribute(eq(new MeasurementResultAnalyserBlackboardView(blackboard3)));
		verify(this.mockedMeasurementResultAnalyser3, atLeastOnce())
			.contribute(eq(new MeasurementResultAnalyserBlackboardView(blackboard3)));
		verify(this.mockedProposedExpressionAnalyser1, atLeastOnce())
			.contribute(eq(new ProposedExpressionAnalyserBlackboardView(blackboard3)));
		verify(this.mockedProposedExpressionAnalyser2, atLeastOnce())
			.contribute(eq(new ProposedExpressionAnalyserBlackboardView(blackboard3)));
		verify(this.mockedProposedExpressionAnalyser3, atLeastOnce())
			.contribute(eq(new ProposedExpressionAnalyserBlackboardView(blackboard3)));

		this.resetMocks();
		final Blackboard blackboard4 = BLACKBOARD_FACTORY.getWithToBeMeasuredContent();
		when(this.mockedMeasurementResultAnalyser1
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard4))).thenReturn(true);
		when(this.mockedMeasurementResultAnalyser2
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard4))).thenReturn(false);
		when(this.mockedMeasurementResultAnalyser3
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard4))).thenReturn(true);
		when(this.mockedProposedExpressionAnalyser1
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard4))).thenReturn(true);
		when(this.mockedProposedExpressionAnalyser2
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard4))).thenReturn(false);
		when(this.mockedProposedExpressionAnalyser3
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard4))).thenReturn(true);

		final AnalysisController analysisController5 = new AnalysisController(blackboard4, allMeasurementTools,
			allMeasurementResultAnalysers, allProposedExpressionAnalysers);
		analysisController5.performAnalysis();
		verify(this.mockedMeasurementResultAnalyser1, atLeastOnce())
			.contribute(eq(new MeasurementResultAnalyserBlackboardView(blackboard4)));
		verify(this.mockedMeasurementResultAnalyser2, never()).contribute(anyObject());
		verify(this.mockedMeasurementResultAnalyser3, atLeastOnce())
			.contribute(eq(new MeasurementResultAnalyserBlackboardView(blackboard4)));
		verify(this.mockedProposedExpressionAnalyser1, atLeastOnce())
			.contribute(eq(new ProposedExpressionAnalyserBlackboardView(blackboard4)));
		verify(this.mockedProposedExpressionAnalyser2, never()).contribute(anyObject());
		verify(this.mockedProposedExpressionAnalyser3, atLeastOnce())
			.contribute(eq(new ProposedExpressionAnalyserBlackboardView(blackboard4)));

		// Verify correct execution order
		this.resetMocks();
		final Blackboard blackboard5 = BLACKBOARD_FACTORY.getWithToBeMeasuredContent();
		when(this.mockedMeasurementResultAnalyser1
			.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard5))).thenReturn(true);
		doAnswer(invocation -> {
			when(AnalysisControllerTest.this.mockedMeasurementResultAnalyser1
				.canContribute(new ReadOnlyMeasurementResultAnalyserBlackboardView(blackboard5))).thenReturn(false);
			return null;
		}).when(this.mockedMeasurementResultAnalyser1)
			.contribute(new MeasurementResultAnalyserBlackboardView(blackboard5));
		when(this.mockedProposedExpressionAnalyser1
			.canContribute(new ReadOnlyProposedExpressionAnalyserBlackboardView(blackboard5))).thenReturn(true);

		final AnalysisController analysisController6 = new AnalysisController(blackboard5, oneMeasurementTool,
			oneMeasurementResultAnalyser, oneProposedExpressionAnalyser);
		analysisController6.performAnalysis();
		final InOrder inOrder = Mockito.inOrder(this.mockedMeasurementTool1, this.mockedMeasurementResultAnalyser1,
			this.mockedProposedExpressionAnalyser1);
		inOrder.verify(this.mockedMeasurementTool1).measure((MeasurementOrder) notNull());
		inOrder.verify(this.mockedMeasurementResultAnalyser1)
			.contribute(eq(new MeasurementResultAnalyserBlackboardView(blackboard5)));
		inOrder.verify(this.mockedProposedExpressionAnalyser1)
			.contribute(eq(new ProposedExpressionAnalyserBlackboardView(blackboard5)));

	}

	/**
	 * Resets the following mocks.
	 *
	 * <li>
	 *
	 * <ul> {@link #mockedMeasurementTool1} </ul>
	 *
	 * <ul> {@link #mockedMeasurementTool2} </ul>
	 *
	 * <ul> {@link #mockedMeasurementTool3} </ul>
	 *
	 * <ul> {@link #mockedMeasurementResultAnalyser1} </ul>
	 *
	 * <ul> {@link #mockedMeasurementResultAnalyser2} </ul>
	 *
	 * <ul> {@link #mockedMeasurementResultAnalyser3} </ul>
	 *
	 * <ul> {@link #mockedProposedExpressionAnalyser1} </ul>
	 *
	 * <ul> {@link #mockedProposedExpressionAnalyser2} </ul>
	 *
	 * <ul> {@link #mockedProposedExpressionAnalyser3} </ul>
	 *
	 * </li>
	 *
	 */
	private void resetMocks() {
		reset(this.mockedMeasurementTool1);
		reset(this.mockedMeasurementTool2);
		reset(this.mockedMeasurementTool3);
		reset(this.mockedMeasurementResultAnalyser1);
		reset(this.mockedMeasurementResultAnalyser2);
		reset(this.mockedMeasurementResultAnalyser3);
		reset(this.mockedProposedExpressionAnalyser1);
		reset(this.mockedProposedExpressionAnalyser2);
		reset(this.mockedProposedExpressionAnalyser3);
	}
}
