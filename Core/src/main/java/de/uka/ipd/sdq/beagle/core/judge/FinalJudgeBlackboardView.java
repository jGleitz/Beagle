package de.uka.ipd.sdq.beagle.core.judge;

import de.uka.ipd.sdq.beagle.core.Blackboard;
import de.uka.ipd.sdq.beagle.core.BlackboardStorer;
import de.uka.ipd.sdq.beagle.core.ResourceDemandingInternalAction;
import de.uka.ipd.sdq.beagle.core.SeffBranch;
import de.uka.ipd.sdq.beagle.core.SeffLoop;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.EvaluableExpression;
import de.uka.ipd.sdq.beagle.core.measurement.BranchDecisionMeasurementResult;
import de.uka.ipd.sdq.beagle.core.measurement.ResourceDemandMeasurementResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * View of the {@link Blackboard} designed to be used by {@link FinalJudge}, therefore
 * allowing reading access, remeasuring {@link ResourceDemandingInternalAction}s,
 * {@link SeffBranch}es, and {@link SeffLoop}s, and setting final expressions.
 *
 * @author Christoph Michelbach
 * @author Roman Langrehr
 *
 * @see Blackboard for information about Blackboard views.
 */
public class FinalJudgeBlackboardView {

	/**
	 * Creates a new {@code FinalJudgeBlackboardView} for {@code blackboard}.
	 *
	 * @param blackboard The {@link Blackboard} to be accessed through the
	 *            {@code FinalJudgeBlackboardView}.
	 */
	public FinalJudgeBlackboardView(final Blackboard blackboard) {
	}

	/**
	 * Delegates to {@link de.uka.ipd.sdq.beagle.core.Blackboard#getAllRdias()}.
	 *
	 * @return all {@linkplain ResourceDemandingInternalAction resource demanding internal
	 *         actions} known to Beagle. Changes to the returned set will not modify the
	 *         blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getAllRdias()
	 */
	public Set<ResourceDemandingInternalAction> getAllRDIAs() {
		return null;
	}

	/**
	 * Delegates to {@link de.uka.ipd.sdq.beagle.core.Blackboard#getAllSeffBranches()}.
	 *
	 * @return all {@linkplain SeffBranch SEFF branches} known to Beagle. Changes to the
	 *         returned set will not modify the blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getAllSeffBranches()
	 */
	public Set<SeffBranch> getAllSEFFBranches() {
		return null;
	}

	/**
	 * Delegates to {@link de.uka.ipd.sdq.beagle.core.Blackboard#getAllSeffLoops()}.
	 *
	 * @return all {@linkplain SeffLoop SEFF loops} known to Beagle. Changes to the
	 *         returned set will not modify the blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getAllSeffLoops()
	 */
	public Set<SeffLoop> getAllSEFFLoops() {
		return null;
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#writeFor(Class, Serializable)} .
	 *
	 * @param writer The class the data should be written for. Must not be {@code null}.
	 * @param written The data to write.
	 * @param <WRITTEN_TYPE> {@code written}’s type.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#writeFor(Class, Serializable)
	 */
	public <WRITTEN_TYPE extends Serializable> void writeFor(
		final Class<? extends BlackboardStorer<WRITTEN_TYPE>> writer, final WRITTEN_TYPE written) {
	}

	/**
	 * Delegates to {@link de.uka.ipd.sdq.beagle.core.Blackboard#readFor(Class)} .
	 *
	 * @param writer The class the desired data was written for. Must not be {@code null}.
	 * @param <WRITTEN_TYPE> The type of the data to be read.
	 * @return The data written in the last call to
	 *         {@linkplain de.uka.ipd.sdq.beagle.core.Blackboard#writeFor(Class, Serializable)}
	 *         for {@code writer}. {@code null} if no data has been written for
	 *         {@code writer} yet.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#readFor(Class)
	 */
	public <WRITTEN_TYPE extends Serializable> WRITTEN_TYPE readFor(
		final Class<? extends BlackboardStorer<WRITTEN_TYPE>> writer) {
		return null;
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#getMeasurementResultsFor(SeffLoop)}.
	 *
	 * @param loop A SEFF Loop to get the measurement results of. Must not be {@code null}
	 *            .
	 * @return All measurement results reported for {@code loop}. Changes to the returned
	 *         set will not modify the blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getMeasurementResultsFor(SeffLoop)
	 */
	public Set<ResourceDemandMeasurementResult> getMeasurementResultsFor(final SeffLoop loop) {
		return null;
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#getMeasurementResultsFor(SeffBranch)}.
	 *
	 * @param branch A SEFF Branch to get the measurement results of. Must not be
	 *            {@code null}.
	 * @return All measurement results reported for {@code branch}. Changes to the
	 *         returned set will not modify the blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getMeasurementResultsFor(SeffBranch)
	 */
	public Set<BranchDecisionMeasurementResult> getMeasurementResultsFor(final SeffBranch branch) {
		return null;
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#getMeasurementResultsFor(ResourceDemandingInternalAction)}
	 * .
	 *
	 * @param rdia A resource demanding internal action to get the measurement results of.
	 *            Must not be {@code null}.
	 * @return All measurement results reported for {@code rdia}. Changes to the returned
	 *         set will not modify the blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getMeasurementResultsFor(ResourceDemandingInternalAction)
	 */
	public Set<ResourceDemandMeasurementResult> getMeasurementResultsFor(final ResourceDemandingInternalAction rdia) {
		return null;
	}

	/**
	 * Delegates to
	 * {@link Blackboard#proposeExpressionForRDIA(ResourceDemandingInternalAction, EvaluableExpression)}
	 * .
	 *
	 * @param rdia A resource demanding internal action. Must not be {@code null}.
	 * @param expression An evaluable expression proposed to describe {@code rdia}’s
	 *            measurement results. Must not be {@code null}.
	 * @see Blackboard#proposeExpressionForRDIA(ResourceDemandingInternalAction,
	 *      EvaluableExpression)
	 */
	public void proposeExpressionFor(final ResourceDemandingInternalAction rdia, final EvaluableExpression expression) {
	}

	/**
	 * Delegates to
	 * {@link Blackboard#proposeExpressionForSEFFLoop(SeffLoop, EvaluableExpression)} .
	 *
	 * @param loop A SEFF Loop. Must not be {@code null}.
	 * @param expression An evaluable expression proposed to describe {@code loop}’s
	 *            measurement results. Must not be {@code null}.
	 * @see Blackboard#proposeExpressionForSEFFLoop(SeffLoop, EvaluableExpression)
	 */
	public void proposeExpressionFor(final SeffLoop loop, final EvaluableExpression expression) {
	}

	/**
	 * Delegates to
	 * {@link Blackboard#proposeExpressionForSEFFBranch(SeffBranch, EvaluableExpression)}
	 * .
	 *
	 * @param branch A SEFF Branch. Must not be {@code null}.
	 * @param expression An evaluable expression proposed to describe {@code branch}’s
	 *            measurement results. Must not be {@code null}.
	 * @see Blackboard#proposeExpressionForSEFFBranch(SeffBranch, EvaluableExpression)
	 */
	public void proposeExpressionFor(final SeffBranch branch, final EvaluableExpression expression) {
	}

	/**
	 * Delegates to {@link de.uka.ipd.sdq.beagle.core.Blackboard#getRdiasToBeMeasured()}.
	 *
	 * @return All {@linkplain ResourceDemandingInternalAction resource demanding internal
	 *         actions} to be measured. Changes to the returned set will not modify the
	 *         blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getRdiasToBeMeasured()
	 */
	public Set<ResourceDemandingInternalAction> getRDIAsToBeMeasured() {
		return null;
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#getSeffBranchesToBeMeasured()}.
	 *
	 * @return All {@linkplain SeffBranch SEFF branches} to be measured. Changes to the
	 *         returned set will not modify the blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getSeffBranchesToBeMeasured()
	 */
	public Set<SeffBranch> getSEFFBranchesToBeMeasured() {
		return null;
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#getSeffLoopsToBeMeasured()}.
	 *
	 * @return All {@linkplain SeffLoop SEFF loops} to be measured. Changes to the
	 *         returned set will not modify the blackboard content. Is never {@code null}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#getSeffLoopsToBeMeasured()
	 */
	public Set<SeffLoop> getSEFFLoopsToBeMeasured() {
		return null;
	}

	/**
	 * Delegates to
	 * {@link Blackboard#setFinalExpressionForRDIA(ResourceDemandingInternalAction, EvaluableExpression)}
	 * .
	 *
	 * @param rdia A resource demanding internal action. Must not be {@code null}.
	 * @param expression An evaluable expression describing {@code rdia}’s measurement
	 *            results. May be {@code null} to describe that no suitable expression was
	 *            found.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#setFinalExpressionForRDIA(ResourceDemandingInternalAction,
	 *      EvaluableExpression)
	 */
	public void setFinalExpressionFor(final ResourceDemandingInternalAction rdia,
		final EvaluableExpression expression) {
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#setFinalExpressionForSEFFBranch(SeffBranch, EvaluableExpression)}
	 * .
	 *
	 * @param branch A SEFF Branch. Must not be {@code null}.
	 * @param expression An evaluable expression describing {@code branch}’s measurement
	 *            results. May be {@code null} to describe that no suitable expression was
	 *            found.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#setFinalExpressionForSEFFBranch(SeffBranch,
	 *      EvaluableExpression)
	 */
	public void setFinalExpressionFor(final SeffBranch branch, final EvaluableExpression expression) {
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#setFinalExpressionForSEFFLoop(SeffLoop, EvaluableExpression)}
	 * .
	 *
	 * @param loop A SEFF Loop. Must not be {@code null}.
	 * @param expression An evaluable expression describing {@code loop}’s measurement
	 *            results. May be {@code null} to describe that no suitable expression was
	 *            found.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#setFinalExpressionForSEFFLoop(SeffLoop,
	 *      EvaluableExpression)
	 */
	public void setFinalExpressionFor(final SeffLoop loop, final EvaluableExpression expression) {
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredRdias(ResourceDemandingInternalAction...)}
	 * .
	 *
	 * @param rdias Resource demanding internal actions that shall be measured. Must not
	 *            be {@code null} and must be known to the {@link Blackboard}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredRdias(ResourceDemandingInternalAction...)
	 * @see #remeasureRDIAs(Collection)
	 */
	public void remeasureRDIAs(final ResourceDemandingInternalAction... rdias) {
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredRdias(Collection)} .
	 *
	 * @param rdias Resource demanding internal actions that shall be measured. Must not
	 *            be {@code null} and must be known to the {@link Blackboard}.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredRdias(Collection)
	 * @see #remeasureRDIAs(ResourceDemandingInternalAction...)
	 */
	public void remeasureRDIAs(final Collection<ResourceDemandingInternalAction> rdias) {
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredSeffBranches(SeffBranch...)}
	 * .
	 *
	 * @param branches SEFF branches that shall be measured. Must not be {@code null} and
	 *            must be known to the Blackboard.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredSeffBranches(SeffBranch...)
	 * @see #remeasureSEFFBranches(Collection)
	 */
	public void remeasureSEFFBranches(final SeffBranch... branches) {
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredSeffBranches(Collection)} .
	 *
	 * @param branches SEFF branches that shall be measured. Must not be {@code null} and
	 *            must be known to the Blackboard.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredSeffBranches(Collection)
	 * @see #remeasureSEFFBranches(SeffBranch...)
	 */
	public void remeasureSEFFBranches(final Collection<SeffBranch> branches) {
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredSeffLoops(SeffLoop...)} .
	 *
	 * @param loops SEFF Loops that shall be measured. Must not be {@code null} and must
	 *            be known to the Blackboard.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredSeffLoops(SeffLoop...)
	 * @see #remeasureSEFFLoops(Collection)
	 */
	public void remeasureSEFFLoops(final SeffLoop... loops) {
	}

	/**
	 * Delegates to
	 * {@link de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredSeffLoops(Collection)} .
	 *
	 * @param loops SEFF Loops that shall be measured. Must not be {@code null} and must
	 *            be known to the Blackboard.
	 * @see de.uka.ipd.sdq.beagle.core.Blackboard#addToBeMeasuredSeffLoops(Collection)
	 * @see #remeasureSEFFLoops(SeffLoop...)
	 */
	public void remeasureSEFFLoops(final Collection<SeffLoop> loops) {
	}
}
