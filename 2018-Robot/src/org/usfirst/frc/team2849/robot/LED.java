package org.usfirst.frc.team2849.robot;

import org.usfirst.frc.team2849.controls.ControlLayout;

import edu.wpi.first.wpilibj.Solenoid;

public class LED extends Thread {

	private ControlLayout cont;
	private Solenoid  rLED;
	private Solenoid  gLED;
	private Solenoid  bLED;
	
	public LED(ControlLayout cont) {
		this.cont = cont;
		rLED = new Solenoid(0);
		gLED = new Solenoid(1);
		bLED = new Solenoid(2);
		this.start();
	}
	
	public void run() {
		while (true) {
			rLED.set(cont.getR());
			gLED.set(cont.getG());
			bLED.set(cont.getB());
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
