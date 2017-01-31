package org.usfirst.frc.team500.robot.motionProfile;


import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class TrapezoidThread implements Runnable{

	private CANTalon leftTalon;
	private CANTalon rightTalon;
	
	private double[][] leftPoints;
	private double[][] rightPoints;	
	
	private boolean hasTrapTask = false;
	
	private Notifier trapLoop;
	
	private MotionProfile leftProfile;
	private MotionProfile rightProfile;
	
	//network table stuff
	public NetworkTable trapTable = NetworkTable.getTable("Trapezoid");
	private int id;
	private String status = "";

	public TrapezoidThread(CANTalon leftTalon, CANTalon rightTalon) {
		this.leftTalon = leftTalon;
		this.rightTalon = rightTalon;
		
		leftProfile = new MotionProfile(this.leftTalon);
		rightProfile = new MotionProfile(this.rightTalon);
		
		trapLoop = new Notifier(this);
		trapLoop.startPeriodic(0.01);
	}
	
	public void run() {	
		if(hasTrapTask) {
				//start the motion profile(Teleop periodic)	
				if(leftProfile.getSetValue() != CANTalon.SetValueMotionProfile.Hold) 
					leftProfile.control();
				
				if(rightProfile.getSetValue() != CANTalon.SetValueMotionProfile.Hold) 
					rightProfile.control();
					
				if(leftProfile.getSetValue() != CANTalon.SetValueMotionProfile.Hold) 
					leftProfile.startMotionProfile();
				
				if(rightProfile.getSetValue() != CANTalon.SetValueMotionProfile.Hold) 
					rightProfile.startMotionProfile();
			
				CANTalon.SetValueMotionProfile setOutputLeft = leftProfile.getSetValue();
				CANTalon.SetValueMotionProfile setOutputRight = rightProfile.getSetValue();
							
				leftTalon.set(setOutputLeft.value);
				rightTalon.set(setOutputRight.value);
		
				
			if((leftProfile.getSetValue() == CANTalon.SetValueMotionProfile.Hold) && (rightProfile.getSetValue() == CANTalon.SetValueMotionProfile.Hold)) {
					hasTrapTask = false;
					leftProfile.stopMotionProfile();
					rightProfile.stopMotionProfile();
					resetTrapezoid();
					status = "finished";
			}
			
			else {
				status = "running";
			}
			
			trapTable.putString("Trap Status", status);
			trapTable.putNumber("Trap ID", id);
		}
	}
	
	public synchronized void activateTrap(double[][] leftPoints, double[][] rightPoints, int id) {
		
		this.leftPoints = leftPoints;
		this.rightPoints = rightPoints;
		
		//use this for trap network table
		this.id = id;
		
		status = "initialized";
		trapTable.putString("Trap Status", status);
		trapTable.putNumber("Trap ID", id);
		
		resetTrapezoid();
		initializeTalons();		
		setProfiles();
	
		hasTrapTask = true;
		
		System.out.println("trap activated. ID: " + id);
	}

	private void setProfiles() {
		//leftExample.setProfile(TrapezoidControl.motionProfileUtility(leftRotations, leftVel, leftTalon.getEncPosition()));
		//rightExample.setProfile(TrapezoidControl.motionProfileUtility(rightRotations, rightVel, rightTalon.getEncPosition()));
		leftProfile.setProfile(leftPoints);
		rightProfile.setProfile(rightPoints);
	}
	
	
	
	private void initializeTalons() {		
	 	leftTalon.changeControlMode(TalonControlMode.MotionProfile);
	 	rightTalon.changeControlMode(TalonControlMode.MotionProfile);
	}
	
	public void resetTrapezoid() {
		leftProfile.reset();
		rightProfile.reset();
	}
	
	public String getStatus(){
		return status;
	}
	
	public int getID(){
		return id;
	}
	
	

}
