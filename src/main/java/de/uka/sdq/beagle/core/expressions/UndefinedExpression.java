package de.uka.sdq.beagle.core.expressions;

/**
 * Thrown if a {@link EvaluableExpression} is to be evaluated, but insufficient variable
 * assignments are provided.
 *
 * @author Joshua Gleitze
 * @see EvaluableExpression
 */
public class UndefinedExpression extends RuntimeException {

	/**
	 * Serialisation version UID, see {@link java.io.Serializable}.
	 */
	private static final long serialVersionUID = -80875322029735423L;

	/**
	 * Creates an exception for an encountered undefined variable. Describes the situation
	 * that an {@link EvaluableExpression} containing {@code undefinedVariable} was tried
	 * to be {@link EvaluableExpression#evaluate}d, but the passed {@code assignment} did
	 * not contain a valid assignment for {@code undefinedVariable}.
	 *
	 * @param assignment
	 *            The assignment raising the exception.
	 * @param undefinedVariable
	 *            The variable missing in {@code assignment}.
	 */
	public UndefinedExpression(final EvaluableVariableAssignment assignment,
			final EvaluableVariable undefinedVariable) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * The assignment that caused this exception by not assigning a value to
	 * {@link} #getMissingVariable()}.
	 *
	 * @return the causing assignment.
	 */
	public EvaluableVariableAssignment getCausingAssignment() {
		// TODO implement method
		return null;
	}

	/**
	 * The variable having no assignment in {@link #getCausingAssignment()} while beink
	 * needed.
	 *
	 * @return the missing variable.
	 */
	public EvaluableVariable getMissingVariable() {
		// TODO implement method
		return null;

	}
}
