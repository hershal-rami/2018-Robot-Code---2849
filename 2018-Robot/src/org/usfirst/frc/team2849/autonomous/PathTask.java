package org.usfirst.frc.team2849.autonomous;

import org.usfirst.frc.team2849.controls.ControlLayout;
import org.usfirst.frc.team2849.path.Path;
import org.usfirst.frc.team2849.path.Pathfollower;
import org.usfirst.frc.team2849.robot.Drive;

public class PathTask extends AutoTask {

	private Path leftPath;
	private Path rightPath;
	private Pathfollower follower;
	
	public PathTask(ControlLayout cont, Path[] paths) {
		super(cont);
		leftPath = paths[0];
		rightPath = paths[1];
		follower = new Pathfollower(1.5, 0, 0, 1.0/paths[0].getMaxVel(), 1.0/34, 3.0/80);
	}

	@Override
	public void run() {
		double leftPower;
		double rightPower;
		double steer;
		while (!leftPath.isFinished() && !rightPath.isFinished()) {
			leftPower = follower.getCorrection(leftPath, Drive.getLeftEncoder());
			rightPower = follower.getCorrection(rightPath, Drive.getRightEncoder());
			
			steer = follower.getSteering(leftPath.findNextPoint(Drive.getLeftEncoder()), Drive.getHeading());
			
			cont.setPower(leftPower + steer, rightPower - steer);
			try {
				Thread.sleep((long) (leftPath.getDt() * 100));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
