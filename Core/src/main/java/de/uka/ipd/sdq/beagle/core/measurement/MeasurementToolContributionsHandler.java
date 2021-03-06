package de.uka.ipd.sdq.beagle.core.measurement;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import java.util.ArrayList;
import java.util.List;

/*
 * This class cannot reasonably be tested through unit tests. It uses an API that’s
 * offered through a static method of a final, byte-code signed class. Automatically
 * testing the class would require inadequate effort and would likely mainly test the
 * Eclipse API. This class is tested manually.
 *
 * COVERAGE:OFF
 */

/**
 * Handles creation searching, loading and instantiation of all available measurement
 * tools. They are loaded through Eclipse’s extension point API. The measurement tools
 * need a public zero argument constructor for the instantiation.
 *
 * @author Roman Langrehr
 */
public class MeasurementToolContributionsHandler {

	/**
	 * The extension point id for {@linkplain MeasurementTool MeasurementTools}.
	 */
	public static final String MEASUREMENT_TOOL_EXTENSION_POINT_ID = "de.uka.ipd.sdq.beagle.measurementtool";

	/**
	 * The property name of the extension point for {@linkplain MeasurementTool}
	 * implementations.
	 */
	private static final String MEASUREMENT_TOOL_EXTENSION_POINT_CLASS_PROPERTY_NAME = "MeasurementToolClass";

	/**
	 * Scans the measurement tools extension point for available measurement tools.
	 *
	 * @return All available measurement tools. Each call returns new instances of the
	 *         measurement tools.
	 * @throws RuntimeException If an instance of any measurement tool could not be
	 *             created for any reason, e.g. because the measurement tool has no public
	 *             zero argument constructor or because a {@code MeasurmentToolClass}
	 *             provided via the extension point is not implementing
	 *             {@link MeasurementTool}.
	 */
	public List<MeasurementTool> getAvailableMeasurmentTools() {
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final List<MeasurementTool> measurementTools = new ArrayList<>();
		final IConfigurationElement[] config =
			registry.getConfigurationElementsFor(MEASUREMENT_TOOL_EXTENSION_POINT_ID);
		try {
			for (final IConfigurationElement element : config) {
				final Object object =
					element.createExecutableExtension(MEASUREMENT_TOOL_EXTENSION_POINT_CLASS_PROPERTY_NAME);
				if (object instanceof MeasurementTool) {
					measurementTools.add((MeasurementTool) object);
				} else {
					throw new RuntimeException(
						"A class provided via the measurement tool extension point was not implementing the"
							+ "interface \"Measurement Tool\": " + object.getClass().getName() + " in package "
							+ object.getClass().getPackage().getName());
				}
			}
		} catch (final CoreException exception) {
			throw new RuntimeException(exception);
		}
		return measurementTools;
	}
}
