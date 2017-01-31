package org.usfirst.frc.team500.robot.subsystems;

import org.usfirst.frc.team500.robot.*;
import org.usfirst.frc.team500.robot.motionProfile.TrapezoidThread;


import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DrivetrainSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	static CANTalon leftMaster, leftSlave, rightMaster, rightSlave;
	private double talonLeftPos, talonRightPos, talonLeftRPM, talonRightRPM, rightSpeed, leftSpeed;
	private long curTime, difTime, lastTime;         
	protected TrapezoidThread trapThread;
	private static int currentProfileID;
	public boolean profileHasFinished = false;
	public boolean moveTrapezoidal = true;
	private boolean firstLogFileRun = true;
   
    public DrivetrainSubsystem() {
    	leftMaster = Robot.bot.getCANTalonObj(0);
		rightMaster = Robot.bot.getCANTalonObj(1);
		leftSlave = Robot.bot.getCANTalonObj(2);
		rightSlave = Robot.bot.getCANTalonObj(3);
		
		leftMaster.reverseSensor(true);
		rightMaster.reverseSensor(true);
		
		leftMaster.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		rightMaster.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		
		leftMaster.configEncoderCodesPerRev(360);
		rightMaster.configEncoderCodesPerRev(360);
		
		leftMaster.setF(1.50220264);
		rightMaster.setF(1.51780415);
		
		rightMaster.reverseOutput(true);
		leftMaster.reverseOutput(false);
		
		leftSlave.changeControlMode(TalonControlMode.Follower);
    	rightSlave.changeControlMode(TalonControlMode.Follower);
    	leftSlave.set(RobotMap.ProgrammingBot.leftMasterID);
    	rightSlave.set(RobotMap.ProgrammingBot.rightMasterID);
		
    	rightMaster.enableBrakeMode(false);
    	leftMaster.enableBrakeMode(false);	
    	    	
    	trapThread = new TrapezoidThread(leftMaster, rightMaster);	
    }
    
    private void waitForTrapezoidalFinish() {
		while(true){
			String tempString = getTrapStatus();
			int id = getTrapID();
		//	System.out.println("[Nav} navID "+ currentProfileID +":" + tempString+ " netID:" + id);
			if(tempString.equals("finished") && (id == currentProfileID)){
				//currentProfileID++;
				profileHasFinished = true;
				break;
			}
		}
		
	}
    public CANTalon getLeftMaster(){
    	return leftMaster;
    }
    public CANTalon getRightMaster(){
    	return rightMaster;
    }
    
    public void runProfileLeftRight(double[][] leftPoints, double[][] rightPoints){
		profileHasFinished = false;
		currentProfileID++;
		startTrapezoidControl(leftPoints,rightPoints,currentProfileID);
		waitForTrapezoidalFinish();
		System.out.println("Time: " + Timer.getFPGATimestamp());		
	}
    
	public void startTrapezoidControl(double[][] leftPoints, double[][] rightPoints,int trapID) {	
		trapThread.activateTrap(leftPoints, rightPoints, trapID);
	}

	
	public void stopTrapezoidControl() {
		trapThread.resetTrapezoid();
	}
	
	public synchronized  int getTrapID(){
		return trapThread.getID();
	}

	public synchronized String getTrapStatus(){
		return trapThread.getStatus();
	}
    
    public void percentVoltageMode(){
    	leftMaster.changeControlMode(TalonControlMode.PercentVbus);
    	rightMaster.changeControlMode(TalonControlMode.PercentVbus);
    	//leftSlave.changeControlMode(TalonControlMode.PercentVbus);
    	//rightSlave.changeControlMode(TalonControlMode.PercentVbus);
    }
     public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
     public void resetEncoders(){
 		leftMaster.setPosition(0);
 		rightMaster.setPosition(0);
 	}
     
	private static DrivetrainSubsystem instance = new DrivetrainSubsystem();
	
	public static DrivetrainSubsystem getInstance(){
		return instance;
	}
	
	private static void setMotorOutputs(double leftSpeed, double rightSpeed, boolean sensitivity){
		if(sensitivity){
			//leftSpeed = setDriveSensitivity(leftSpeed);
			//rightSpeed = setDriveSensitivity(rightSpeed);
		}		
		leftMaster.set(-leftSpeed);   // front Left 
		rightMaster.set(rightSpeed);  // front Right 
	}
	
	/*private static double setDriveSensitivity(double input){
		if (!Robot.bot.getName().equals("ProgrammingBot")){
			input = RobotMap.DRIVE_SENSITIVITY*Math.pow(input, 3) + (1-RobotMap.DRIVE_SENSITIVITY)*input;
			return input;
		}
		else{
			return (Double) null;
		}
		
	}*/
	
	public void shiftGears(boolean gear){
		/*
		SmartDashboard.putString("Drivetrain ShiftGears","True");
		if(gear){
			SmartDashboard.putString("Drivetrain Shift to high","True");
			drivetrainSolenoid.set(true);
			RobotMap.currentGear = true;
		}
		else{
			SmartDashboard.putString("Drivetrain Shift to high","false");
			drivetrainSolenoid.set(false);
			RobotMap.currentGear = false;
		}*/
	}
	
    private static double limit(double num) {
        if (num > 1.0) {
            num= 1.0;
        }
        else if (num < -1.0) {
            num= -1.0;
        }
        	return num;
    }
    
    public void backwardsFullVelocity(){
		setMotorOutputs(1,1,false);
	}
	public void forwardsFullVelocity(){
		setMotorOutputs(-1,-1,false);
	}
	
    	
	public void arcadeDrive(double moveValue, double rotateValue, boolean sensitivity) {
        double leftMotorSpeed;
        double rightMotorSpeed;

        moveValue = limit(moveValue);
        rotateValue = limit(rotateValue);

        if (moveValue > 0.0) {
            if (rotateValue > 0.0) {
                leftMotorSpeed = moveValue - rotateValue;
                rightMotorSpeed = Math.max(moveValue, rotateValue);
            } else {
                leftMotorSpeed = Math.max(moveValue, -rotateValue);
                rightMotorSpeed = moveValue + rotateValue;
            }
        } else {
            if (rotateValue > 0.0) {
                leftMotorSpeed = -Math.max(-moveValue, rotateValue);
                rightMotorSpeed = moveValue + rotateValue;
            } else {
                leftMotorSpeed = moveValue - rotateValue;
                rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
            }
        }
        setMotorOutputs(leftMotorSpeed, rightMotorSpeed,sensitivity);
    }
	
    public void tankDrive(double leftValue, double rightValue, boolean sensitivity) {

        // square the inputs (while preserving the sign) to increase fine control while permitting full power
        leftValue = limit(leftValue);
        rightValue = limit(rightValue);

        setMotorOutputs(leftValue, rightValue, sensitivity);
    }    
    
    public void populateLog(long startTime){
    	if (firstLogFileRun){
    		lastTime = startTime;
    		firstLogFileRun = false;
    	}
    	curTime = System.currentTimeMillis();
    	difTime = curTime - lastTime;
    	
    	if (difTime >= 100){
        	talonLeftPos = leftMaster.getEncPosition();
    		talonLeftRPM = leftMaster.getSpeed();
    		talonRightPos = rightMaster.getEncPosition();
    		talonRightRPM = rightMaster.getSpeed();
    		leftSpeed = leftMaster.getEncVelocity();
    		rightSpeed = rightMaster.getEncVelocity();
    	
        	System.out.format("%s\n", "Time," + (curTime-startTime)/1000);
    		System.out.format("%s\n", "Talon left position: " + talonLeftPos);
    		System.out.format("%s\n", "Talon left rpm: " +  talonLeftRPM);
    		System.out.format("%s\n", "Talon right position: " + talonRightPos);
    		System.out.format("%s\n", "Talon right rpm: " +  talonRightRPM);
    		System.out.format("%s\n", "Talon right velocity: " + rightSpeed);
    		System.out.format("%s\n", "Talon left velocity: " +  leftSpeed);
    		lastTime = curTime;
    	}
    	
		SmartDashboard.putNumber("Talon right velocity", rightSpeed);
		SmartDashboard.putNumber("Talon left velocity", leftSpeed);
		SmartDashboard.putNumber("Talon left Position", talonLeftPos);
		SmartDashboard.putNumber("Talon left rpm", talonLeftRPM);
		SmartDashboard.putNumber("Talon right Position", -talonRightPos);
		SmartDashboard.putNumber("Talon right rpm", -talonRightRPM);
		SmartDashboard.putNumber("Time", (curTime-startTime)/1000);
    	
    }
}

