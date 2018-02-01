<<<<<<< HEAD

package org.usfirst.frc.team2849.controls;

import org.usfirst.frc.team2849.autonomous.IntakeTask.IntakeType;
import org.usfirst.frc.team2849.robot.Drive.DriveControl;
import org.usfirst.frc.team2849.robot.Intake.IntakeControl;

import edu.wpi.first.wpilibj.Spark;

public interface ControlLayout {
	
	public double getLeftPower();
	public double getRightPower();
	
	public void setPower(double leftPower, double rightPower);
	
	public DriveControl getDrive(Spark frontLeft, Spark frontRight, Spark rearLeft, Spark rearRight);
	
	public IntakeControl getIntake(Spark left, Spark right);
	
	public void runIntake();
	
    public void setIntakeValue( double intakeValue);
    
    public double getIntakeValue();
    
    public void setLiftPower(double liftPower);
    
    public double getLiftPower();
    
    public void setLiftHeight(double liftHeight);
    
    /**
     * 
     * @return lift height in inches
     */
    public double getLiftHeight();
    
    public void runLift();
=======
package org.usfirst.frc.team2849.controls;

import org.usfirst.frc.team2849.autonomous.IntakeTask.IntakeType;
import org.usfirst.frc.team2849.robot.Drive.DriveControl;
import org.usfirst.frc.team2849.robot.Intake.IntakeControl;

import edu.wpi.first.wpilibj.Spark;

public interface ControlLayout {
	
	public double getLeftPower();
	public double getRightPower();
	
	public void setPower(double leftPower, double rightPower);
	
	public DriveControl getDrive(Spark frontLeft, Spark frontRight, Spark rearLeft, Spark rearRight);
	
	//Intake methods
	public IntakeControl getIntake(Spark left, Spark right);
	
	public void runIntake();
	
    public void setIntakeValue(double intakeValue);
    
    public double getIntakeValue();
    
    //Lift Methods    
    public void setLiftPower(double liftPower);
    
    public double getLiftPower();
    
    public void setLiftHeight(double liftHeight);
 
    public double getLiftHeight();
>>>>>>> branch 'master' of https://github.com/teamursamajor/2018-Robot-Code---2849
}