package org.usfirst.frc.team2849.controls;

import org.usfirst.frc.team2849.robot.Drive;
import org.usfirst.frc.team2849.robot.Drive.DriveControl;

import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


public class TankDrive extends XboxController implements ControlLayout {
 public double intakePower;
	public TankDrive(int port) {
		super(port);
	}

	@Override
	public double getLeftPower() {
		return super.getAxis(AXIS_LEFTSTICK_Y);
	}

	@Override
	public double getRightPower() {
		return super.getAxis(AXIS_RIGHTSTICK_Y);
	}

	@Override
	public DriveControl getDrive(Spark frontLeft, Spark frontRight, Spark rearLeft, Spark rearRight) {
		SpeedControllerGroup leftSide = new SpeedControllerGroup(frontLeft, rearLeft);
		SpeedControllerGroup rightSide = new SpeedControllerGroup(frontRight, rearRight);
		DifferentialDrive drive = new DifferentialDrive(leftSide, rightSide);
		return () -> { drive.tankDrive(getLeftPower(), getRightPower(), true); };
	}

	@Override
	public boolean runningElevatorUp() {
		return super.getDPad(POV_UP);
	}

	@Override
	public boolean runningElevatorDown() {
		return super.getDPad(POV_DOWN);
	}

	@Override
	public double runIntake() {
	return intakePower;
	}

	@Override
	public void setPower(double leftPower, double rightPower) {}

	@Override
	public void setIntakeValue( double intakeValue) {
		// TODO Auto-generated method stub
		intakePower=intakeValue;
	}

}
