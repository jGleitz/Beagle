package de.uka.ipd.sdq.beagle.core.failurehandling;

/**
 * Handler for any failures that may occur while running Beagle. Clients may obtain an
 * instance of this class to report that an exceptional situation occurred.
 *
 * <p>Failures reported through this API may be <em>recoverable</em>. This means that
 * Beagle’s execution does not have to be stopped and that there is a way to continue
 * without or to retry the failed action. Recoverable failures are handled by recover
 * functions that may optionally return a value to continue with after the failure.
 *
 * <p>The reaction to failures can be configured by providing a
 * {@link FailureHandlerProvider} at {@linkplain #setProvider(FailureHandlerProvider)}. If
 * no provider is configured, a {@link ExceptionThrowingFailureHandler} will be returned
 * by {@link #getHandler}.
 *
 * <p>Given that the class has a failure handler defined in the constant
 * {@code FAILURE_HANDLER}, these are some usage examples:
 *
 * <pre>
 * <code>
 * public ReturnType myMethod(ParameterType1 param1, ParameterType2 param2) {
 * 	// code
 * 	try {
 * 		// code that may fail
 * 	} catch (MyExceptionType exception) {
 * 		final FailureReport&lt;ReturnTyp&gt; failure = new FailureReport&lt;&gt;()
 * 			.message("doing something with %s and %s failed", param1, param2)
 * 			.cause(exception)
 * 			.recoverable()
 * 			.retryWith(() -> myMethod(param1, param2));
 * 		return FAILURE_HANDLER.handle(failure);
 * 	}
 * 	// more code
 * }
 * </code>
 * </pre>
 * 
 * <pre>
 * <code>
 * public ReturnType myMethod(ParameterType1 param1, ParameterType2 param2) {
 * 	// code
 * 	if (failed) {
 * 		final FailureReport&lt;Void&gt; failure = new FailureReport&lt;&gt;()
 * 			.message("doing something with %s and %s failed", param1, param2)
 * 			.details("We tried this and that, but doing so was not possible")
 * 			.continueWith(this::otherMethod);
 * 		FAILURE_HANDLER.handle(failure);
 * 		return null;
 * 	}
 * 	// more code
 * }
 * </code>
 * </pre>
 *
 * @author Joshua Gleitze
 */
public abstract class FailureHandler {

	/**
	 * The provider of handlers.
	 */
	private static FailureHandlerProvider provider = (name) -> new ExceptionThrowingFailureHandler(name);

	/**
	 * Creates a handler a client identified by {@code clientName} may report failures to.
	 *
	 * @param clientName A name identifying the client that may report failures to this
	 *            handler.
	 * @return A handler for failures of a client called {@code clientName}.
	 */
	public static FailureHandler getHandler(final String clientName) {
		return provider.getFor(clientName);
	}

	/**
	 * Creates a handler instances of {@code clientType} may report failures to.
	 *
	 * @param clientType Class of the clients that may report failures to this handler.
	 * @return A handler for failures of clients assignable to {@code clientType}.
	 */
	public static FailureHandler getHandler(final Class<?> clientType) {
		return getHandler(clientType.getName());
	}

	/**
	 * Sets the provider of Failure Handlers. The provider will be used to generate all
	 * future handlers until the next call to this method.
	 *
	 * @param provider A factory for handlers.
	 */
	public static void setProvider(final FailureHandlerProvider provider) {
		FailureHandler.provider = provider;
	}

	/**
	 * Reports a failure to this handler and makes it take action. This may include
	 * calling the recover functions potentially provided through {@code report}. This
	 * method will return the value generated by the recover functions. If the
	 * {@code report} describes a non-recoverable failure, this method will not return.
	 * This method might also not return if the handler thinks it’s not appropraite to
	 * continue.
	 *
	 * @param <RECOVER_TYPE> The recover value’s type.
	 * @param report Information about the failure.
	 * @return The value the reporter should use instead of the one that was expected to
	 *         be generated by the failed action. Is created by the recover functions in
	 *         {@code report}.
	 */
	public abstract <RECOVER_TYPE> RECOVER_TYPE handle(final FailureReport<RECOVER_TYPE> report);

	/**
	 * Interface for factories creating the handlers for failures that shall be used by
	 * Beagle.
	 *
	 * @author Joshua Gleitze
	 */
	public interface FailureHandlerProvider {

		/**
		 * Creates a handler for failures of a client called {@code clientName}.
		 *
		 * @param clientName A name identifying the client that may report failures
		 *            through the returned handler.
		 * @return A failure handler for {@code clientName}.
		 */
		FailureHandler getFor(String clientName);
	}

}
