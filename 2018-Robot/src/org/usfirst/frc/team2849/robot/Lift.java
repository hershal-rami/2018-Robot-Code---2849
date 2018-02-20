package org.usfirst.frc.team2849.robot;

import org.usfirst.frc.team2849.controls.ControlLayout;
import org.usfirst.frc.team2849.diagnostics.Logger;
import org.usfirst.frc.team2849.diagnostics.Logger.LogLevel;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Spark;

public class Lift extends Thread implements UrsaRobot {

	private ControlLayout cont;
	private static Spark motor = new Spark(LIFT);
	Encoder liftEnc;

	private double desiredHeight;
	private double currentHeight;

	//TODO set this value
	private double inchesPerTick = 1.0d;

	// acceptable error may need to be adjusted; if so change this value
	private double acceptableRange = 4;

	public Lift(ControlLayout control) {
		cont = control;
		this.start();
//		liftEnc = new Encoder(UrsaRobot.LIFT_ENCODER_CHANNEL_A, UrsaRobot.LIFT_ENCODER_CHANNEL_B);
//		liftEnc.setDistancePerPulse(inchesPerTick);
	}

	public void run() {
//		liftEnc.reset();
		int count = 0;
		while (true) {
			count = (count + 1) % 100;
//			if(count == 0) System.out.println(liftEnc.getDistance());
			cont.getLift().setCurrentHeight(getLiftHeight());
			desiredHeight = cont.getLift().getDesiredHeight();
			currentHeight = cont.getLift().getCurrentHeight();
//			if (checkReached()) {
//				motor.set(.25);
//			} else 
			if (desiredHeight > currentHeight) {
				motor.set(1.00);
			} else if (desiredHeight < currentHeight) {
				//increase power until arms are on TODO
				motor.set(-0.20);
			} else {
				motor.set(.25);
			}

			try {
				Thread.sleep(20);
			} catch (Exception e) {
				e.printStackTrace();
				Logger.log("Lift.java thread.sleep call, printStackTrace", LogLevel.ERROR);
			}
		}
	}

	public void setControlScheme(ControlLayout cont) {
		this.cont = cont;
	}

	public double getLiftHeight() {
//		return liftEnc.getDistance();
				return 0;
	}

	/**
	 * Tells cont if we are within some amount of our desiredHeight and returns
	 * true or false.
	 * 
	 * @return Returns true if we are within the acceptableRange, returns false
	 *         otherwise
	 */
	public boolean checkReached() {
		boolean hasReached = Math.abs(desiredHeight - currentHeight) < acceptableRange;
		cont.getLift().setReached(hasReached);
		return hasReached;

	}
}