/**
 * 
 */
package org.usfirst.frc.team2849.controls.lift;

import org.usfirst.frc.team2849.autonomous.LiftTask.*;
/**
 * @author kingeinstein
 *
 */
import org.usfirst.frc.team2849.controls.XboxController;

public class TriggerLift implements LiftControl {

	private XboxController xbox;
	private double currentHeight;
	private boolean hasReached;
	LiftType liftType;

	public TriggerLift(XboxController xbox, LiftType liftType) {
		this.xbox = xbox;
		this.liftType = liftType;
	}
	
	public TriggerLift(XboxController xbox) {
		this.xbox = xbox;
	}

	public int getTriggerValue() {
		if (xbox.getAxisGreaterThan(XboxController.AXIS_LEFTTRIGGER, 0.5)) {
			return Integer.MIN_VALUE;
		}
		if (xbox.getAxisGreaterThan(XboxController.AXIS_RIGHTTRIGGER, 0.5)) {
			return Integer.MAX_VALUE;
		} else {
			return 0;
		}
	}

	@Override
	public void setDesiredHeight(double liftHeight) {
	}

	@Override
	public double getDesiredHeight() {
		return getTriggerValue();
	}

	@Override
	public void setCurrentHeight(double liftHeight) {
		currentHeight = liftHeight;
	}

	@Override
	public double getCurrentHeight() {
		return currentHeight;
	}

	@Override
	public void setReached(boolean hasReached) {
		this.hasReached = hasReached;
	}

	@Override
	public boolean getReached() {
		return hasReached;
	}

}
