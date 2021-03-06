package de.uka.ipd.sdq.beagle.core.facade;

import de.uka.ipd.sdq.beagle.core.LaunchConfiguration;
import de.uka.ipd.sdq.beagle.core.timeout.AdaptiveTimeout;
import de.uka.ipd.sdq.beagle.core.timeout.Timeout;

import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.core.IJavaProject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Configures a whole execution of Beagle. Therefore contains all values needed to set up
 * Beagle. The class defines meaningful default values for all its settings.
 *
 * <p>The configuration has two states: A <em>set up</em> state, during which values may
 * be modified and a <em>finalised</em> state, in which the configuration is immutable and
 * can only be used to obtain values. The transition from the <em>set up</em> state to the
 * <em>finalised</em> state is done through {@link #finalise()}. The inverse transition is
 * not possible. Trying to perform an action that is not allowed for the momentary state
 * results in an {@link IllegalStateException} being thrown.
 *
 * @author Christoph Michelbach
 * @author Joshua Gleitze
 * @author Michael Vogt
 * @author Roman Langrehr
 */
public class BeagleConfiguration {

	/**
	 * All elements to measure or {@code null} to indicate that everything in
	 * {@code repositoryFile} should be analysed.
	 */
	private List<String> elements;

	/**
	 * The repository file.
	 */
	private File repositoryFile;

	/**
	 * The timeout to be used.
	 */
	private Timeout timeout;

	/**
	 * The {@link IJavaProject} to analyse.
	 */
	private final IJavaProject javaProject;

	/**
	 * Whether this configuration is in the <em>finalised</em> state.
	 */
	private boolean finalised;

	/**
	 * The {@linkplain LaunchConfiguration LaunchConfigurations} to use for the analysis.
	 */
	private Set<LaunchConfiguration> launchConfigurations;

	/**
	 * The {@link File} containing the source statement link model.
	 */
	private File sourceStatementLinkFile;

	/**
	 * Constructs a new {@link BeagleConfiguration} using {@code elements} as the default
	 * elements to be measured.
	 *
	 * @param elements The elements to be measured or {@code null} to indicate that
	 *            everything in {@code repositoryFile} should be analysed.
	 * @param repositoryFile The repository file to use. Must not be {@code null}.
	 * @param sourceStatementLinkFile The xml file containing the PCM Element Source
	 *            Statement Link Model.
	 * @param javaProject the {@link IJavaProject} to analyse. Must not be {@code null}.
	 */
	public BeagleConfiguration(final List<String> elements, final File repositoryFile,
		final File sourceStatementLinkFile, final IJavaProject javaProject) {
		Validate.notNull(repositoryFile);
		Validate.notNull(sourceStatementLinkFile);
		Validate.notNull(javaProject);

		if (!repositoryFile.exists()) {
			throw new IllegalArgumentException("Repository file must exist. Path was: " + repositoryFile.getPath());
		}
		if (!sourceStatementLinkFile.exists()) {
			throw new IllegalArgumentException(
				"Source code link file must exist. Path was: " + sourceStatementLinkFile.getPath());
		}

		if (elements != null) {
			this.elements = new LinkedList<>(elements);
		}

		this.repositoryFile = repositoryFile;
		this.timeout = new AdaptiveTimeout();
		this.javaProject = javaProject;
		this.sourceStatementLinkFile = sourceStatementLinkFile;
	}

	/**
	 * Gives the {@link IJavaProject} to analyse.
	 *
	 * @return the {@link IJavaProject} to analyse.
	 */
	public IJavaProject getJavaProject() {
		return this.javaProject;
	}

	/**
	 * Returns the elements to be measured or {@code null} to indicate that everything in
	 * the {@linkplain #getRepositoryFile() repository file} should be analysed.
	 *
	 * @return The elements to be measured or {@code null} to indicate that everything in
	 *         the {@linkplain #getRepositoryFile() repository file} should be analysed.
	 */
	public List<String> getElements() {
		return this.elements;
	}

	/**
	 * Sets the elements to be measured to {@code elements}. {@code null} indicates that
	 * everything in the {@linkplain #getRepositoryFile() repository file} should be
	 * analysed. This operation is only allowed in the <em>set up</em> state.
	 *
	 *
	 * @param elements The elements to be measured or {@code null} to indicate that
	 *            everything in the {@linkplain #getRepositoryFile() repository file}
	 *            should be analysed.
	 * @throws IllegalStateException If this configuration is not in the <em>set up</em>
	 *             state.
	 * @see #getElements()
	 */
	public void setElements(final List<String> elements) {
		Validate.validState(!this.finalised,
			"setting values is only allowed if this configuration is not yet finalised");
		if (elements == null) {
			this.elements = null;
		} else {
			this.elements = new LinkedList<>(elements);
		}
	}

	/**
	 * Returns the timeout to be used. The timeout describes the minimum time Beagle shall
	 * keep trying to find results while no perfect results were found.
	 *
	 * @return The timeout that will be used by Beagle.
	 */
	public Timeout getTimeout() {
		return this.timeout;
	}

	/**
	 * Sets the timeout to be used to {@code timeout}. The timeout describes the minimum
	 * time Beagle shall keep trying to find results while no perfect results were found.
	 * This operation is only allowed in the <em>set up</em> state.
	 *
	 * @param timeout The timeout to be used by Beagle.
	 * @throws IllegalStateException If this configuration is not in the <em>set up</em>
	 *             state.
	 */
	public void setTimeout(final Timeout timeout) {
		Validate.validState(!this.finalised,
			"setting values is only allowed if this configuration is not yet finalised");
		this.timeout = timeout;
	}

	/**
	 * Returns the {@linkplain LaunchConfiguration LaunchConfigurations} to use for the
	 * analysis.
	 *
	 * @return The {@linkplain LaunchConfiguration LaunchConfigurations} to use for the
	 *         analysis.
	 */
	public Set<LaunchConfiguration> getLaunchConfigurations() {
		return this.launchConfigurations;
	}

	/**
	 * Sets the {@linkplain LaunchConfiguration LaunchConfigurations} to use for the
	 * analysis. This operation is only allowed in the <em>set up</em> state.
	 *
	 * @param launchConfigurations The {@linkplain LaunchConfiguration
	 *            LaunchConfigurations} to use for the analysis.
	 * @throws IllegalStateException If this configuration is not in the <em>set up</em>
	 *             state.
	 */
	public void setLaunchConfigurations(final Set<LaunchConfiguration> launchConfigurations) {
		Validate.validState(!this.finalised,
			"setting values is only allowed if this configuration is not yet finalised");
		this.launchConfigurations = launchConfigurations;
	}

	/**
	 * Returns the repository file that contains all elements that shall be analysed.
	 *
	 * @return The repository file Beagle will operate on.
	 */
	public File getRepositoryFile() {
		return this.repositoryFile;
	}

	/**
	 * Sets the repository file containing the elements Beagle shall analyse. This
	 * operation is only allowed in the <em>set up</em> state.
	 *
	 *
	 * @param repositoryFile The pcm repository file containing all elements Beagle shall
	 *            analyse. Must not be {@code null}.
	 * @throws IllegalStateException If this configuration is not in the <em>set up</em>
	 *             state.
	 */
	public void setRepositoryFile(final File repositoryFile) {
		Validate.validState(!this.finalised,
			"setting values is only allowed if this configuration is not yet finalised");
		Validate.notNull(repositoryFile);
		this.repositoryFile = repositoryFile;
	}

	/**
	 * Sets the file containing the model linking PCM elements to their positions in the
	 * source code.
	 *
	 * @param sourceStatementLinkFile The XML file containing the PCM Source Statement
	 *            Links Model.
	 */
	public void setSourceStatementLinkFile(final File sourceStatementLinkFile) {
		Validate.validState(!this.finalised,
			"setting values is only allowed if this configuration is not yet finalised");
		Validate.notNull(sourceStatementLinkFile);
		this.sourceStatementLinkFile = sourceStatementLinkFile;
	}

	/**
	 * Returns the file containing the model linking PCM elements to their positions in
	 * the source code.
	 *
	 * @return The XML file containing the PCM Source Statement Links Model.
	 */
	public File getSourceStatementLinkFile() {
		return this.sourceStatementLinkFile;
	}

	/**
	 * Queries whether this configuration is in the <em>finalised</em> state.
	 *
	 * @return {@code true} if this configuration is in the <em>finalised</em> state,
	 *         {@code false} if it’s in the <em>set up</em> state.
	 */
	public boolean isFinal() {
		return this.finalised;
	}

	/**
	 * Finalises this configuration, thus transitioning it into the <em>finalised</em>
	 * state. Calling this method when this configuration already is in the
	 * <em>finalised</em> state has no effect.
	 */
	public void finalise() {
		this.finalised = true;
	}

}
