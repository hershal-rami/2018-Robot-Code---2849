package org.usfirst.frc.team2849.autonomous;

import java.util.ArrayList;

import org.usfirst.frc.team2849.controls.AutoControl;

public abstract class GroupTask extends AutoTask {
	protected ArrayList<AutoTask> tasks = new ArrayList<AutoTask>();
	public GroupTask(AutoControl cont) {
		super(cont);
	}
	
	public void addTask(AutoTask t) {
		tasks.add(t);
	}
	
	public ArrayList<AutoTask> getTaskGroup() {
		return tasks;
	}
	
	public String toString() {
		String ret = new String();
		for(AutoTask t:tasks) {
			ret += t.toString();
		}
		return ret + "--\n";
	}
}
