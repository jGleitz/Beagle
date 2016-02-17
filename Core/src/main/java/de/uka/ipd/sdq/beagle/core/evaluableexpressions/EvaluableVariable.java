package de.uka.ipd.sdq.beagle.core.evaluableexpressions;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An {@link EvaluableExpression} representing a named variable.
 *
 * @author Annika Berger
 * @author Christoph Michelbach
 */
public class EvaluableVariable implements EvaluableExpression {

	/**
	 * States how long it takes to evaluate the expression for a computer. The bigger the
	 * number, the harder it is. The norm is addition which means that the
	 * {@code COMPUTATINOAL_COMPLEXITY} of addition is {@code 1}. Scaling is linear.
	 */
	public static final double COMPUTATINOAL_COMPLEXITY = 1d;

	/**
	 * States how hard it is for educated humans to understand the expression. The bigger
	 * the number, the harder it is. The norm is addition which means that the
	 * {@code HUMAN_UNDERSTANDABILITY_COMPXELITY} of addition is {@code 1}. Scaling is
	 * linear.
	 */
	public static final double HUMAN_UNDERSTANDABILITY_COMPXELITY = 4d;

	/**
	 * The name of the evaluable variable.
	 */
	private final String name;

	/**
	 * Builds an evaluable variable with the given name.
	 *
	 * @param name The name of the variable.
	 */
	public EvaluableVariable(final String name) {
		Validate.notNull(name);
		Validate.notEmpty(name);
		this.name = name;
	}

	/**
	 * Get this evaluable varibale's name.
	 *
	 * @return The variable's name. Is never {@code null}.
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.uka.ipd.sdq.beagle.core.expressions.EvaluableExpression#receive(de.uka.sdq.
	 * beagle. core.expressions.EvaluableExpressionVisitor)
	 */
	@Override
	public void receive(final EvaluableExpressionVisitor visitor) {
		Validate.notNull(visitor);
		visitor.visit(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uka.ipd.sdq.beagle.core.expressions.EvaluableExpression#evaluate(de.uka.sdq.
	 * beagle. core.expressions.EvaluableVariableAssignment)
	 */
	@Override
	public double evaluate(final EvaluableVariableAssignment variableAssignments) {
		Validate.notNull(variableAssignments);
		if (!variableAssignments.isValueAssignedFor(this)) {
			throw new UndefinedExpressionException(variableAssignments, this);
		}
		return variableAssignments.getValueFor(this);
	}

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(final Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object.getClass() != this.getClass()) {
			return false;
		}
		final EvaluableVariable other = (EvaluableVariable) object;
		return new EqualsBuilder().append(this.name, other.name).isEquals();
	}

	@Override
	public int hashCode() {
		// you pick a hard-coded, randomly chosen, non-zero, odd number
		// ideally different for each class
		return new HashCodeBuilder(197, 199).append(this.name).toHashCode();
	}

}
