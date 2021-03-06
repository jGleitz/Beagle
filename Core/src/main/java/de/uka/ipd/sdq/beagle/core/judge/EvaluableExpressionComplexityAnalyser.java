package de.uka.ipd.sdq.beagle.core.judge;

import de.uka.ipd.sdq.beagle.core.evaluableexpressions.AdditionExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.ComparisonExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.ConstantExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.DivisionExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.EvaluableExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.EvaluableVariable;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.ExponentationExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.ExponentialFunctionExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.IfThenElseExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.LogarithmExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.MultiplicationExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.NaturalLogarithmExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.SineExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.SubtractionExpression;
import de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.RecursiveEvaluableExpressionVisitor;

import org.apache.commons.lang3.Validate;

/*
 * ATTENTION: Checkstyle is turned off where numbers with obvious meanings are used.
 */

/**
 * EvaluableExpressionComplexityAnalyser object for an {@link EvaluableExpression}.
 *
 * <p>{@link #determineComplexity(EvaluableExpression)} must be called before
 * {@link #getComputationalComplexitySum()} or
 * {@link #getHumanComprehensibilityComplexitySum()} are called.
 *
 *
 * @author Christoph Michelbach
 */
public class EvaluableExpressionComplexityAnalyser {

	/**
	 * Every expression with depth larger than this will receive {@link #DEPTH_PENALTY} of
	 * penalty in human-comprehensibility.
	 */
	private static final int DEPTH_PENALTY_THRESHOLD = 2;

	/**
	 * If the maximum depth exceeds {@link #DEPTH_PENALTY_THRESHOLD}, the maximum depth to
	 * the power of {@link PENALTY_MAX_DEPTH_EXPONTENT} times
	 * {@link PENALTY_MAX_DEPTH_FACTOR} will be added to
	 * {@link #humanComprehensibilityComplexitySum}.
	 */
	private static final double PENALTY_MAX_DEPTH_EXPONTENT = 1.4d;

	/**
	 * If the maximum depth exceeds {@link #DEPTH_PENALTY_THRESHOLD}, the maximum depth to
	 * the power of {@link PENALTY_MAX_DEPTH_EXPONTENT} times
	 * {@link PENALTY_MAX_DEPTH_FACTOR} will be added to
	 * {@link #humanComprehensibilityComplexitySum}.
	 */
	private static final double PENALTY_MAX_DEPTH_FACTOR = 2.7d;

	/**
	 * The factor for the depth penalty of every expression branch. Every expression with
	 * depth larger than {@link #DEPTH_PENALTY_THRESHOLD} will receive this much penalty
	 * in human-comprehensibility in combination with {@link #DEPTH_PENALTY_EXPONENT}.
	 */
	private static final double DEPTH_PENATLY_FACOTOR = .5d;

	/**
	 * The exponent for the depth penalty of every expression branch. Every expression
	 * with depth larger than {@link #DEPTH_PENALTY_THRESHOLD} will receive this much
	 * penalty in human-comprehensibility in combination with
	 * {@link #DEPTH_PENATLY_FACOTOR}.
	 */
	private static final double DEPTH_PENALTY_EXPONENT = 1.3d;

	/**
	 * The visitor this class uses.
	 */
	private Visitor visitor;

	/**
	 * Determines the computational and human-readability complexity of {@code expression}
	 * .
	 *
	 * @param expression The {@link EvaluableExpression} to determine the complexity
	 *            values for.
	 */
	public void determineComplexity(final EvaluableExpression expression) {
		this.visitor = new Visitor();
		this.visitor.visitRecursively(expression);

		if (this.visitor.maxDepth > DEPTH_PENALTY_THRESHOLD) {
			this.visitor.humanComprehensibilityComplexitySum +=
				PENALTY_MAX_DEPTH_FACTOR * Math.pow(this.visitor.maxDepth, PENALTY_MAX_DEPTH_EXPONTENT);
		}
	}

	/**
	 * Returns the computational complexity.
	 *
	 * <p>{@link #determineComplexity(EvaluableExpression)} must be called before this
	 * method or an {@link IllegalStateException} will be thrown.
	 *
	 * @return The computationalComplexitySum.
	 */
	public double getComputationalComplexitySum() {
		Validate.validState(this.visitor != null);

		return this.visitor.computationalComplexitySum;
	}

	/**
	 * Returns the human-comprehensibility complexity.
	 *
	 * <p>{@link #determineComplexity(EvaluableExpression)} must be called before this
	 * method or an {@link IllegalStateException} will be thrown.
	 *
	 * @return The humanComprehensibilityComplexitySum.
	 */
	public double getHumanComprehensibilityComplexitySum() {
		Validate.validState(this.visitor != null);

		return this.visitor.humanComprehensibilityComplexitySum;
	}

	/**
	 * Private class for hiding the visitor pattern.
	 *
	 * @author Christoph Michelbach
	 */
	private class Visitor extends RecursiveEvaluableExpressionVisitor {

		/**
		 * The total computational complexity. The values added up to this sum have been
		 * determined on a laptop with an Intel® Core™ i7-4720HQ CPU @ 2.60GHz × 8 (8
		 * cores with Hyper-Threading; 4 cores physically) processor running Linux
		 * 3.19.0-30-generic.
		 */
		private double computationalComplexitySum;

		/**
		 * The total human-readability complexity.
		 */
		private double humanComprehensibilityComplexitySum;

		/**
		 * The maximum depth of this expression.
		 */
		private int maxDepth;

		@Override
		protected void visitRecursively(final EvaluableExpression expression) {
			super.visitRecursively(expression);
		}

		@Override
		protected void atExpression(final EvaluableExpression expression) {
			if (this.getTraversalDepth() > DEPTH_PENALTY_THRESHOLD) {
				this.humanComprehensibilityComplexitySum +=
					DEPTH_PENATLY_FACOTOR * Math.pow(this.getTraversalDepth(), DEPTH_PENALTY_EXPONENT);
			}

			if (this.getTraversalDepth() > this.maxDepth) {
				this.maxDepth = this.getTraversalDepth();
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atAddition(de.uka.ipd.sdq.beagle.core.evaluableexpressions.AdditionExpression)
		 */
		@Override
		protected void atAddition(final AdditionExpression expression) {
			final int numberOfElements = expression.getSummands().size();

			this.computationalComplexitySum += 1d * (numberOfElements - 1);
			this.humanComprehensibilityComplexitySum += 1d * (numberOfElements - 1);

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atMultiplication(de.uka.ipd.sdq.beagle.core.evaluableexpressions.
		 * MultiplicationExpression)
		 */
		@Override
		protected void atMultiplication(final MultiplicationExpression expression) {
			final int numberOfElements = expression.getFactors().size();

			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 1.6492450638792102d * (numberOfElements - 1);
			this.humanComprehensibilityComplexitySum += 3d * (numberOfElements - 1);
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atVariable(de.uka.ipd.sdq.beagle.core.evaluableexpressions.EvaluableVariable)
		 */
		@Override
		protected void atVariable(final EvaluableVariable expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 1d;
			this.humanComprehensibilityComplexitySum += 4d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atComparison(de.uka.ipd.sdq.beagle.core.evaluableexpressions.
		 * ComparisonExpression)
		 */
		@Override
		protected void atComparison(final ComparisonExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 1d;
			this.humanComprehensibilityComplexitySum += 3d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atConstant(de.uka.ipd.sdq.beagle.core.evaluableexpressions.ConstantExpression)
		 */
		@Override
		protected void atConstant(final ConstantExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += .1d;
			this.humanComprehensibilityComplexitySum += .1d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atDivision(de.uka.ipd.sdq.beagle.core.evaluableexpressions.DivisionExpression)
		 */
		@Override
		protected void atDivision(final DivisionExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 3.2740998838559814d;
			this.humanComprehensibilityComplexitySum += 7d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atExponentation(de.uka.ipd.sdq.beagle.core.evaluableexpressions.
		 * ExponentationExpression)
		 */
		@Override
		protected void atExponentation(final ExponentationExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 2177.7277840269966d;
			this.humanComprehensibilityComplexitySum += 12d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atExponentialFunction(de.uka.ipd.sdq.beagle.core.evaluableexpressions.
		 * ExponentialFunctionExpression)
		 */
		@Override
		protected void atExponentialFunction(final ExponentialFunctionExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 941.1764705882353d;
			this.humanComprehensibilityComplexitySum += 20d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atIfThenElse(de.uka.ipd.sdq.beagle.core.evaluableexpressions.
		 * IfThenElseExpression)
		 */
		@Override
		protected void atIfThenElse(final IfThenElseExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 2d;
			this.humanComprehensibilityComplexitySum += 4d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atLogarithm(de.uka.ipd.sdq.beagle.core.evaluableexpressions.
		 * LogarithmExpression)
		 */
		@Override
		protected void atLogarithm(final LogarithmExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 126.78362573099415d;
			this.humanComprehensibilityComplexitySum += 25d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atNaturalLogarithm(de.uka.ipd.sdq.beagle.core.evaluableexpressions.
		 * NaturalLogarithmExpression)
		 */
		@Override
		protected void atNaturalLogarithm(final NaturalLogarithmExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 26.54729466718568d;
			this.humanComprehensibilityComplexitySum += 17d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atSine(de .uka.ipd.sdq.beagle.core.evaluableexpressions.SineExpression)
		 */
		@Override
		protected void atSine(final SineExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 205.03680743897714d;
			this.humanComprehensibilityComplexitySum += 15d;
			// CHECKSTYLE:ON

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see de.uka.ipd.sdq.beagle.core.evaluableexpressions.util.ExpressionTreeWalker#
		 * atSubstraction(de.uka.ipd.sdq.beagle.core.evaluableexpressions.
		 * SubtractionExpression)
		 */
		@Override
		protected void atSubtraction(final SubtractionExpression expression) {
			// CHECKSTYLE:OFF
			this.computationalComplexitySum += 1d;
			this.humanComprehensibilityComplexitySum += 1.2d;
			// CHECKSTYLE:ON

		}
	}
}
