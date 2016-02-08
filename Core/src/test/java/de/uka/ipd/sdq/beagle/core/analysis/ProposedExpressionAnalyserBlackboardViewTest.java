package de.uka.ipd.sdq.beagle.core.analysis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.theInstance;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;

import de.uka.ipd.sdq.beagle.core.Blackboard;
import de.uka.ipd.sdq.beagle.core.BlackboardStorer;
import de.uka.ipd.sdq.beagle.core.ExternalCallParameter;
import de.uka.ipd.sdq.beagle.core.ResourceDemandingInternalAction;
import de.uka.ipd.sdq.beagle.core.SeffBranch;
import de.uka.ipd.sdq.beagle.core.SeffLoop;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.EvaluableExpression;
import de.uka.ipd.sdq.beagle.core.judge.EvaluableExpressionFitnessFunction;
import de.uka.ipd.sdq.beagle.core.measurement.BranchDecisionMeasurementResult;
import de.uka.ipd.sdq.beagle.core.measurement.LoopRepetitionCountMeasurementResult;
import de.uka.ipd.sdq.beagle.core.measurement.ParameterChangeMeasurementResult;
import de.uka.ipd.sdq.beagle.core.measurement.ResourceDemandMeasurementResult;
import de.uka.ipd.sdq.beagle.core.testutil.factories.BlackboardFactory;
import de.uka.ipd.sdq.beagle.core.testutil.factories.EvaluableExpressionFitnessFunctionFactory;
import de.uka.ipd.sdq.beagle.core.testutil.factories.ExternalCallParameterFactory;
import de.uka.ipd.sdq.beagle.core.testutil.factories.ResourceDemandingInternalActionFactory;
import de.uka.ipd.sdq.beagle.core.testutil.factories.SeffBranchFactory;
import de.uka.ipd.sdq.beagle.core.testutil.factories.SeffLoopFactory;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Testing the {@link ProposedExpressionAnalyserBlackboardView}.
 * 
 * @author Joshua Gleitze
 * @author Ansgar Spiegler
 */
public class ProposedExpressionAnalyserBlackboardViewTest {

	/**
	 * A {@link SeffBranch} factory to easily obtain new instances from.
	 */
	private static final SeffBranchFactory SEFF_BRANCH_FACTORY = new SeffBranchFactory();

	/**
	 * A {@link SeffLoop} factory to easily obtain new instances from.
	 */
	private static final SeffLoopFactory SEFF_LOOP_FACTORY = new SeffLoopFactory();

	/**
	 * An {@link ExternalCallParameter} factory to easily obtain new instances from.
	 */
	private static final ExternalCallParameterFactory EXTERNAL_CALL_PARAMETER_FACTORY =
		new ExternalCallParameterFactory();

	/**
	 * A {@link ResourceDemandingInternalAction} factory to easily obtain new instances
	 * from.
	 */
	private static final ResourceDemandingInternalActionFactory RDIA_FACTORY =
		new ResourceDemandingInternalActionFactory();

	/**
	 * A {@link SeffBranch} factory to easily obtain new instances from.
	 */
	private static final EvaluableExpressionFitnessFunctionFactory FITNESS_FUNCTION_FACTORY =
		new EvaluableExpressionFitnessFunctionFactory();

	/**
	 * A {@link SeffBranch} factory to easily obtain new instances from.
	 */
	private static final BlackboardFactory BLACKBOARD_FACTORY = new BlackboardFactory();

	/**
	 * Blackboard mock-up that handles method calls.
	 */
	private Blackboard mockedBlackboard;

	/**
	 * The referenced view by this test.
	 */
	private ProposedExpressionAnalyserBlackboardView testedView;

	/**
	 * Initializes {@link #mockedBlackboard} with Mockit and creates a new
	 * {@link #testedView} of it.
	 * 
	 * @throws Exception Non specified exception because every exception in the following
	 *             test cases result in
	 */
	@Before
	public void createView() {
		this.mockedBlackboard = mock(Blackboard.class);
		this.testedView = new ProposedExpressionAnalyserBlackboardView(this.mockedBlackboard);
	}

	/**
	 * Test method for {@link ProposedExpressionAnalyserBlackboardView#hashCode()} .
	 * Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The hash code is the same for equal view.
	 *
	 * </ul>
	 */
	@Test
	public void hashCodeT() {
		// Creating equal view to the existing one
		final ProposedExpressionAnalyserBlackboardView secondView =
			new ProposedExpressionAnalyserBlackboardView(this.mockedBlackboard);
		assertThat("Two equal ProposedExpressionAnalyserBlackboardViews should have the same hashCode!",
			secondView.hashCode(), is(equalTo(this.testedView.hashCode())));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#equals(ProposedExpressionAnalyserBlackboardView)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> Two different views containing the same Blackboard reference are equal
	 * 
	 * <li> Two different views containing equal Blackboards that have not the same
	 * reference, are not equal.
	 *
	 * </ul>
	 */
	@Test
	public void equalsT() {
		// Creating equal view to the existing one
		final ProposedExpressionAnalyserBlackboardView secondView =
			new ProposedExpressionAnalyserBlackboardView(this.mockedBlackboard);
		assertThat(
			"Two ProposedExpressionAnalyserBlackboardViews should be equal when they have the same blackboard instance!",
			secondView, is(equalTo(this.testedView)));

		// Equals for two views should only return true, if the views have exactly the
		// same blackboard reference!
		final Blackboard blackboardEmptyOne = BLACKBOARD_FACTORY.getEmpty();
		final Blackboard blackboardEmptyTwo = BLACKBOARD_FACTORY.getEmpty();
		final ProposedExpressionAnalyserBlackboardView emptyViewOne =
			new ProposedExpressionAnalyserBlackboardView(blackboardEmptyOne);
		final ProposedExpressionAnalyserBlackboardView emptyViewTwo =
			new ProposedExpressionAnalyserBlackboardView(blackboardEmptyTwo);

		assertThat("Two ProposedExpressionAnalyserBlackboardViews should not be equal,"
			+ "if they have not exact the same Blackboard reference!", emptyViewOne, not(equalTo(emptyViewTwo)));
	}

	/**
	 * Test method for {@link ProposedExpressionAnalyserBlackboardView#toString} . Asserts
	 * that:
	 *
	 * <ul>
	 *
	 * <li> toString does not return the standard String as defined in Object.class.
	 *
	 * </ul>
	 */
	@Test
	public void toStringT() {
		final String standardRepresentation = this.testedView.getClass().getName() + "@" + this.testedView.hashCode();
		assertThat("toString should be overwritten by a meaningful representation of this object!",
			standardRepresentation, not(equalTo(this.testedView.toString())));
	}
	
	/**
	 * Test method for {@link ProposedExpressionAnalyserBlackboardView#getAllRdias()}.
	 * Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard
	 *
	 * </ul>
	 *
	 */
	@Test
	public void getAllRdias() {
		final Set<ResourceDemandingInternalAction> testInstance = new HashSet<>();
		given(this.mockedBlackboard.getAllRdias()).willReturn(testInstance);

		final Set<ResourceDemandingInternalAction> result = this.testedView.getAllRdias();
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#getAllSeffBranches()}. Asserts
	 * that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard
	 *
	 * </ul>
	 *
	 */
	@Test
	public void getAllSeffBranches() {
		final Set<SeffBranch> testInstance = new HashSet<>();
		given(this.mockedBlackboard.getAllSeffBranches()).willReturn(testInstance);

		final Set<SeffBranch> result = this.testedView.getAllSeffBranches();
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));
	}

	/**
	 * Test method for {@link ProposedExpressionAnalyserBlackboardView#getAllSeffLoops()}.
	 * Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard
	 *
	 * </ul>
	 *
	 */
	@Test
	public void getAllSeffLoops() {
		final Set<SeffLoop> testInstance = new HashSet<>();
		given(this.mockedBlackboard.getAllSeffLoops()).willReturn(testInstance);

		final Set<SeffLoop> result = this.testedView.getAllSeffLoops();
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#getAllExternalCallParameters()}.
	 * Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard
	 *
	 * </ul>
	 *
	 */
	@Test
	public void getAllExternalCallParameters() {
		final Set<ExternalCallParameter> testInstance = new HashSet<>();
		given(this.mockedBlackboard.getAllExternalCallParameters()).willReturn(testInstance);

		final Set<ExternalCallParameter> result = this.testedView.getAllExternalCallParameters();
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#addToBeMeasuredRdias(java.util.Collection)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The call is delegated to the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void addToBeMeasuredRdias() {
		final Set<ResourceDemandingInternalAction> addedRdias = RDIA_FACTORY.getAllAsSet();

		this.testedView.addToBeMeasuredRdias(addedRdias);
		then(this.mockedBlackboard).should().addToBeMeasuredRdias(same(addedRdias));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#addToBeMeasuredRdias(java.util.Collection)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The call is delegated to the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void addToBeMeasuredSeffBranches() {
		final Set<SeffBranch> addedSeffBranches = SEFF_BRANCH_FACTORY.getAllAsSet();

		this.testedView.addToBeMeasuredSeffBranches(addedSeffBranches);
		then(this.mockedBlackboard).should().addToBeMeasuredSeffBranches(same(addedSeffBranches));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#addToBeMeasuredSeffLoops(java.util.Collection)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The call is delegated to the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void addToBeMeasuredSeffLoops() {
		final Set<SeffLoop> addedSeffLoops = SEFF_LOOP_FACTORY.getAllAsSet();

		this.testedView.addToBeMeasuredSeffLoops(addedSeffLoops);
		then(this.mockedBlackboard).should().addToBeMeasuredSeffLoops(same(addedSeffLoops));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#addToBeMeasuredExternalCallParameters(java.util.Collection)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The call is delegated to the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void addToBeMeasuredExternalCallParameters() {
		final Set<ExternalCallParameter> addedExternalCallParameter = EXTERNAL_CALL_PARAMETER_FACTORY.getAllAsSet();

		this.testedView.addToBeMeasuredExternalCallParameters(addedExternalCallParameter);
		then(this.mockedBlackboard).should().addToBeMeasuredExternalCallParameters(same(addedExternalCallParameter));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#getMeasurementResultsFor(ResourceDemandingInternalAction)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void getMeasurementResultsForResourceDemandingInternalAction() {
		final Set<ResourceDemandMeasurementResult> testInstance = new HashSet<>();
		final ResourceDemandingInternalAction rdia = RDIA_FACTORY.getOne();
		given(this.mockedBlackboard.getMeasurementResultsFor(rdia)).willReturn(testInstance);

		final Set<ResourceDemandMeasurementResult> result = this.testedView.getMeasurementResultsFor(rdia);
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#getMeasurementResultsFor(SeffBranch)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void getMeasurementResultsForSeffBranch() {
		final Set<BranchDecisionMeasurementResult> testInstance = new HashSet<>();
		final SeffBranch seffBranch = SEFF_BRANCH_FACTORY.getOne();
		given(this.mockedBlackboard.getMeasurementResultsFor(seffBranch)).willReturn(testInstance);

		final Set<BranchDecisionMeasurementResult> result = this.testedView.getMeasurementResultsFor(seffBranch);
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#getMeasurementResultsFor(SeffLoop)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void getMeasurementResultsForSeffLoop() {
		final Set<LoopRepetitionCountMeasurementResult> testInstance = new HashSet<>();
		final SeffLoop seffLoop = SEFF_LOOP_FACTORY.getOne();
		given(this.mockedBlackboard.getMeasurementResultsFor(seffLoop)).willReturn(testInstance);

		final Set<LoopRepetitionCountMeasurementResult> result = this.testedView.getMeasurementResultsFor(seffLoop);
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#getMeasurementResultsFor(ExternalCallParameter)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void getMeasurementResultsForExternalCallParameter() {
		final Set<ParameterChangeMeasurementResult> testInstance = new HashSet<>();
		final ExternalCallParameter externalCallParameter = EXTERNAL_CALL_PARAMETER_FACTORY.getOne();
		given(this.mockedBlackboard.getMeasurementResultsFor(externalCallParameter)).willReturn(testInstance);

		final Set<ParameterChangeMeasurementResult> result =
			this.testedView.getMeasurementResultsFor(externalCallParameter);
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));
	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#getProposedExpressionFor(SeffElement)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void getProposedExpressionFor() {
		final Set<EvaluableExpression> testInstance = new HashSet<>();
		final SeffBranch seffBranch = SEFF_BRANCH_FACTORY.getOne();
		given(this.mockedBlackboard.getProposedExpressionFor(seffBranch)).willReturn(testInstance);

		final Set<EvaluableExpression> result = this.testedView.getProposedExpressionFor(seffBranch);
		assertThat("The testedView should return the instance it obtained from the blackboad", result,
			is(theInstance(testInstance)));

	}

	/**
	 * Test method for
	 * {@link ProposedExpressionAnalyserBlackboardView#getGetFitnessFunction()} . Asserts
	 * that:
	 *
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void getFitnessFunction() {
		final EvaluableExpressionFitnessFunction fitnessFunction = FITNESS_FUNCTION_FACTORY.getOne();
		given(this.mockedBlackboard.getFitnessFunction()).willReturn(fitnessFunction);

		final EvaluableExpressionFitnessFunction result = this.testedView.getFitnessFunction();
		assertThat("The testedView should return the Fitness Function instance it obtained from the blackboad", result,
			is(theInstance(fitnessFunction)));
	}

	/**
	 * Test method for
	 * {@link MeasurementResultAnalyserBlackboardView#readFor()} . Asserts
	 * 
	 * <ul>
	 *
	 * <li> The tested view returns the instance it obtained from the blackboard.
	 *
	 * </ul>
	 *
	 */
	@Test
	public void readFor() {
		final String onBoard = new String();
		given(this.mockedBlackboard.readFor(TestStorer.class)).willReturn(onBoard);
		
		final String result = this.testedView.readFor(TestStorer.class);
		assertThat("The testedView should return the stored content it obtained from the blackboad", result,
			is(theInstance(onBoard)));
	}

	/**
	 * Test method for
	 * {@link MeasurementResultAnalyserBlackboardView#writeFor(writer, String)}
	 * . Asserts that:
	 *
	 * <ul>
	 *
	 * <li> The call is delegated to the blackboard.
	 *
	 * </ul>
	 */
	@Test
	public void writeFor() {
		final String writeOnBoard = new String();
		this.testedView.writeFor(TestStorer.class, writeOnBoard);
		then(this.mockedBlackboard).should().writeFor(same(TestStorer.class), same(writeOnBoard));
	}
	
	/**
	 * Test {@link BlackboardStorer}.
	 *
	 * @author Joshua Gleitze
	 */
	private final class TestStorer implements BlackboardStorer<String> {
	}



}
