package de.uka.ipd.sdq.beagle.core;

/*
 * ATTENTION: Checkstyle turned off! remove this comment block when implementing this
 * class! CHECKSTYLE:OFF TODO
 */

import de.uka.ipd.sdq.beagle.analysis.ResultAnalyser;
import de.uka.ipd.sdq.beagle.judge.FinalJudge;
import de.uka.ipd.sdq.beagle.measurement.MeasurementTool;

/**
 * Controls whether a {@link MeasurementTool}, a {@link ResultAnalyser} or the
 * {@link FinalJudge} is working. If a {@link ResultAnalyser} is working, it also
 * controls, which one. There is always only at most one measurement tool or one result
 * analyser or the final judge working.
 *
 * @author Roman Langrehr
 * @see MeasurementController
 */
public class BeagleController {
	/**
	 * This controller's blackboard, passed to all interacting jobs to communicate. Any
	 * data exchange in any action invoked by this controller must happen exclusively via
	 * this blackboard instance.
	 */
	// private final Blackboard blackboard = new Blackboard();

	// TODO add args
	/**
	 * Runs the complete analysis, including measurements, result analysis and the final
	 * judging.
	 */
	public void main() {
	}
}
