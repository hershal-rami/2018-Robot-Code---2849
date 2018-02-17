package org.usfirst.frc.team2849.autonomous;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.usfirst.frc.team2849.autonomous.IntakeTask.IntakeType;
import org.usfirst.frc.team2849.autonomous.LiftTask.LiftType;
import org.usfirst.frc.team2849.autonomous.TurnTask.Turntype;
import org.usfirst.frc.team2849.controls.ControlLayout;
import org.usfirst.frc.team2849.diagnostics.Logger;
import org.usfirst.frc.team2849.diagnostics.Logger.LogLevel;
import org.usfirst.frc.team2849.path.Path;
import org.usfirst.frc.team2849.path.PathReader;
import org.usfirst.frc.team2849.robot.Drive;

import edu.wpi.first.wpilibj.DriverStation;

//TODO add comments
/**
 * Check the AutoModes folder for a file named Auto Builder Syntax.txt It
 * contains all the syntax
 * 
 * @author Evan + Sheldon wrote this on 1/16/18
 */
public class AutoBuilder {
	interface Token {
	}

	ControlLayout cont;
	private Drive drive;

	public AutoBuilder(ControlLayout cont, Drive drive) {
		this.cont = cont;
		this.drive = drive;
	}

	class ExecuteToken implements Token {
		private String scriptName;

		public ExecuteToken(String scriptName) {
			this.scriptName = "/home/lvuser/automodes/" + scriptName.trim();
		}
	}

	/**
	 * PrintToken: A token that prints all arguments passed to it
	 * 
	 * @param str String that you want to print
	 */
	class PrintToken implements Token {
		private String str; // String to be printed

		// Instantiate PrintToken class
		public PrintToken(String str) {
			this.str = str; // Set variable str to argument string
		}

		// Creates a new instance of PrintTask class
		public PrintTask makeTask(ControlLayout cont) {
			return new PrintTask(cont, str);
		}
	}

	class PathToken implements Token {
		private Path[] paths;

		public PathToken(String filename) {
			filename = filename.replace(" ", "");
			//put all paths into /AutoModes/Paths
			paths = new PathReader("/home/lvuser/automodes/paths/" + filename + ".txt", false).getPaths();
		}

		public PathTask makeTask(ControlLayout cont) {
			return new PathTask(cont, paths, drive);
		}
	}

	class IntakeToken implements Token {
		private IntakeType intake;

		public IntakeToken(String intakeType) {
			intakeType = intakeType.replace(" ", "");
			if (intakeType.equalsIgnoreCase("IN")) {
				intake = IntakeType.IN;
			} else if (intakeType.equalsIgnoreCase("OUT")) {
				intake = IntakeType.OUT;
			} else if (intakeType.equalsIgnoreCase("RUN")) {
				intake = IntakeType.RUN;
			} else if (intakeType.equalsIgnoreCase("STOP")) {
				intake = IntakeType.STOP;
			} else if (intakeType.equalsIgnoreCase("DEPLOY")) {
				intake = IntakeType.DEPLOY;
			} else if (intakeType.equalsIgnoreCase("HOLD")) {
				intake = IntakeType.HOLD;
			} else {
				intake = IntakeType.STOP;
			}
		}

		public IntakeTask makeTask(ControlLayout cont) {
			return new IntakeTask(cont, intake);
		}
	}

	class LiftToken implements Token {
		private LiftType lift;

		public LiftToken(String liftType) {
			liftType = liftType.replace(" ", "");

			if (liftType.equalsIgnoreCase("BOTTOM")) {
				lift = LiftType.BOTTOM;
			} else if (liftType.equalsIgnoreCase("VAULT")) {
				lift = LiftType.VAULT;
			} else if (liftType.equalsIgnoreCase("SWITCH")) {
				lift = LiftType.SWITCH;
			} else if (liftType.equalsIgnoreCase("SCALE")) {
				lift = LiftType.SCALE;
			} else {
				lift = LiftType.BOTTOM;
			}
		}

		public LiftTask makeTask(ControlLayout cont) {
			return new LiftTask(cont, LiftTask.presetToHeight(lift));
		}
	}

	class WaitToken implements Token {
		private double wait;

		public WaitToken(String waitTime) {
			waitTime = waitTime.replace(" ", "");
			if (Double.parseDouble(waitTime) >= 0) {
				wait = Double.parseDouble(waitTime);
			}
		}

		public WaitTask makeTask(ControlLayout cont) {
			return new WaitTask(cont, (long) (wait * 1000.0d));
		}
	}

	class BundleToken implements Token {
		public BundleToken() {
		}
	}

	class SerialToken implements Token {
		public SerialToken() {
		}
	}

	class RightBraceToken implements Token {
		public RightBraceToken() {
		}
	}

	class TurnToken implements Token {
		private double turnAmount;
		private Turntype turnType;

		public TurnToken(String turn) {
			turn = turn.toLowerCase();
			if (turn.contains("to")) {
				turnType = Turntype.TURN_TO;
				turnAmount = Double.valueOf(turn.substring(turn.indexOf("to") + "TO".length()));
			} else {
				turnType = Turntype.TURN_BY;
				turnAmount = Double.valueOf(turn.substring(turn.indexOf("by") + "BY".length()));
			}
		}

		public TurnTask makeTask(ControlLayout cont) {
			return new TurnTask(cont, turnType, turnAmount, drive);
		}
	}

	class DriveToken implements Token {
		private double dist;

		public DriveToken(String distance) {
			distance = distance.replace(" ", "");
			if (Math.abs(Double.parseDouble(distance)) >= 0) {
				dist = Double.parseDouble(distance);
			}
		}

		public DriveTask makeTask(ControlLayout cont) {
			return new DriveTask(cont, (int) dist, drive);
		}
	}

	private ArrayList<Token> tokenize(String filename) throws IOException {
		ArrayList<Token> ret = new ArrayList<Token>();
		BufferedReader buff;
		buff = new BufferedReader(new FileReader(filename));
		String line = null;
		while ((line = buff.readLine()) != null) {
			if (line.contains("#")) {
				continue;
			} else if (line.contains("follow")) {
				String current = line.substring(line.indexOf("follow") + "follow".length());
				ret.add(new PathToken(current));
			} else if (line.contains("execute")) {
				String current = line.substring(line.indexOf("execute") + "execute".length());
				ret.add(new ExecuteToken(current));
			} else if (line.contains("wait")) {
				String current = line.substring(line.indexOf("wait") + "wait".length());
				ret.add(new WaitToken(current));
			} else if (line.contains("drive")) {
				String current = line.substring(line.indexOf("drive") + "drive".length());
				ret.add(new DriveToken(current));
			} else if (line.contains("turn")) {
				String current = line.substring(line.indexOf("turn") + "turn".length());
				ret.add(new TurnToken(current));
			} else if (line.contains("lift")) {
				String current = line.substring(line.indexOf("lift") + "lift".length());
				ret.add(new LiftToken(current));
			} else if (line.contains("intake")) {
				String current = line.substring(line.indexOf("intake") + "intake".length());
				ret.add(new IntakeToken(current));
			} else if (line.contains("print")) { // If the line is a print token
				String current = line.substring(line.indexOf("print") + "print".length());
				// The data that should be printed is everything that comes
				// after the token "print"
				ret.add(new PrintToken(current)); // Adds new Print Token to the
													// ArrayList of all tokens
			} else if (line.contains("bundle")) {
				ret.add(new BundleToken());
			} else if (line.contains("serial")) {
				ret.add(new SerialToken());
			} else if (line.contains("}")) {
				ret.add(new RightBraceToken());
			}
		}
		buff.close();
		return ret;
	}

	private AutoTask parseAuto(ArrayList<Token> toks, GroupTask ret) {
		if (toks.size() == 0) {
			return new WaitTask(cont, 0);
		}

		while (toks.size() > 0) {
			Token t = toks.remove(0);
			if (t instanceof ExecuteToken) {
				AutoTask otherMode = buildAutoMode(((ExecuteToken) t).scriptName);
				ret.addTask(otherMode);
			} else if (t instanceof WaitToken) {
				ret.addTask(((WaitToken) t).makeTask(cont));
			} else if (t instanceof DriveToken) {
				ret.addTask(((DriveToken) t).makeTask(cont));
			} else if (t instanceof TurnToken) {
				ret.addTask(((TurnToken) t).makeTask(cont));
			} else if (t instanceof PathToken) {
				ret.addTask(((PathToken) t).makeTask(cont));
			} else if (t instanceof IntakeToken) {
				ret.addTask(((IntakeToken) t).makeTask(cont));
			} else if (t instanceof LiftToken) {
				ret.addTask(((LiftToken) t).makeTask(cont));
			} else if (t instanceof BundleToken) {
				ParallelTask p = new ParallelTask(cont);
				parseAuto(toks, p);
				ret.addTask(p);
			} else if (t instanceof SerialToken) {
				SerialTask p = new SerialTask(cont);
				parseAuto(toks, p);
				ret.addTask(p);
			} else if (t instanceof RightBraceToken) {
				return ret;
			} else if (t instanceof PrintToken) {
				ret.addTask(((PrintToken) t).makeTask(cont));
			}
		}

		return ret;
	}

	public AutoTask buildAutoMode(String filename) {
		try {
			return parseAuto(tokenize(filename), new SerialTask(cont));
		} catch (IOException e) {
			e.printStackTrace();
			Logger.log("AutoBuilder buildAutoMode parseAuto printStackTrace", LogLevel.ERROR);
			return null;
		}
	}

	/**
	 * Gets the switch/scale side from the FMS, and finds an Auto Mode file
	 * which finds robot's position and switch/scale ownership and performs the
	 * highest task in the ranked list of tasks from the SmartDashboard that
	 * matches the current setup.
	 * 
	 * @param robotPosition
	 *            The side our robot starts on. L, M, or R.
	 * @param autoPrefs
	 *            String array of ranked Auto Modes
	 * @param autoFiles
	 *            File array of all files in the AutoModes folder
	 * @return String name of the auto file to run
	 */
	public String pickAutoMode(char robotPosition, String[] autoPrefs, File[] autoFiles) {
		// Gets the ownership information from the FMS
		String switchPos = DriverStation.getInstance().getGameSpecificMessage().substring(0,1);
		String scalePos = DriverStation.getInstance().getGameSpecificMessage().substring(1,2);
		String mode;
		System.out.println(switchPos + scalePos);
		
		switch (switchPos + scalePos) {
		case "LL":
			mode = autoPrefs[0];
			break;
		case "LR":
			mode = autoPrefs[1];
			break;
		case "RL":
			mode = autoPrefs[2];
			break;
		case "RR":
			mode = autoPrefs[3];
			break;
		default:
			mode = "drive";
			break;
		}
		
		//for potential future use
		String oppSide = DriverStation.getInstance().getGameSpecificMessage().substring(2);
		
		String regex = "^[" + robotPosition + "0]_[" + switchPos + "0][" + scalePos + "0]_" + mode + "\\.auto$";
		
		System.out.println(regex);
		System.out.println("L_0L_scale.auto".matches(regex));
		
		for (File f: autoFiles) {
			if (f.getName().matches(regex)) {
				return "/home/lvuser/automodes/" + f.getName();
			}
		}
		return "/home/lvuser/automodes/0_00_drive.auto";
	}
}
