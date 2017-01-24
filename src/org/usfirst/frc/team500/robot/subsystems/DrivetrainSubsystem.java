package org.usfirst.frc.team500.robot.subsystems;

import org.usfirst.frc.team500.robot.Robot;
import org.usfirst.frc.team500.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DrivetrainSubsystem extends Subsystem {
	
	static CANTalon leftMaster;
	static CANTalon leftSlave;
	static CANTalon rightMaster;
	static CANTalon rightSlave;
	
	private long curTime, difTime, lastTime;         
	private double talon2Pos, talon4Pos, talon2RPM, talon4RPM, rightSpeed, leftSpeed;
	private boolean firstLogFileRun = true;
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
	public DrivetrainSubsystem(){
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
		
		leftMaster.setF(1.60244);
		rightMaster.setF(1.09295);
		
		rightMaster.reverseOutput(true);
		leftMaster.reverseOutput(false);
		
		leftSlave.changeControlMode(TalonControlMode.Follower);
    	rightSlave.changeControlMode(TalonControlMode.Follower);
    	leftSlave.set(RobotMap.ProgrammingBot.leftMasterID);
    	rightSlave.set(RobotMap.ProgrammingBot.rightMasterID);
		
	}
	
	
	public static CANTalon getLeftMaster(){
		return leftMaster;
	}
	public static CANTalon getRightMaster(){
		return rightMaster;
	}
    private static DrivetrainSubsystem instance = new DrivetrainSubsystem();
    
    public static DrivetrainSubsystem getInstance(){
    	return instance;
    }
    
    private void setMotorOutputs(double leftSpeed, double rightSpeed, boolean scaledInputs){
    	if(scaledInputs==true){
    		//Robot.bot.getCANTalonObj(0).set(scaleInput(-leftSpeed));
    		//Robot.bot.getCANTalonObj(1).set(scaleInput(rightSpeed));
    		//Robot.bot.getCANTalonObj(2).set(scaleInput(-leftSpeed));
    		//Robot.bot.getCANTalonObj(3).set(scaleInput(rightSpeed));
    	}
    	else{
    		this.leftSpeed = -leftSpeed;
    		this.rightSpeed = rightSpeed;
    		leftMaster.set(-leftSpeed);
	    	rightMaster.set(rightSpeed);
	    	//leftSlave.set(-leftSpeed);
	    	//rightSlave.set(rightSpeed);
    	}
    }
    
    public void motionProfileMode(){
    	leftMaster.changeControlMode(TalonControlMode.MotionProfile);
    	rightMaster.changeControlMode(TalonControlMode.MotionProfile);
    	
    }
    
    public void percentVoltageMode(){
    	leftMaster.changeControlMode(TalonControlMode.PercentVbus);
    	rightMaster.changeControlMode(TalonControlMode.PercentVbus);
    	//leftSlave.changeControlMode(TalonControlMode.PercentVbus);
    	//rightSlave.changeControlMode(TalonControlMode.PercentVbus);
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
    
    //doesn't work
	private double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.0, 0.03, 0.06, 0.09, 0.13, 0.17, 0.21,
				0.26, 0.31, 0.36, 0.41, 0.47, 0.53, 0.61, .80, 1.00 };
		
		// get the corresponding index for the scaleInput array.
		boolean neg = false;
		if(dVal<0){
			neg = true;
		}
		/*	//joystick position function
		dVal = Math.abs(dVal);
		int index = (int) (dVal * 16.0);
		if (index > 16){
			index = 16;
		}
		
		double dScale = 0.0;
		if (neg==true) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}*/
		
		//time function
		double dScale = 0;
		if(dVal>RobotMap.Cyber.driveValue || dVal == 1){
			RobotMap.Cyber.driveCounter++;
			RobotMap.Cyber.driveValue = dVal;
		}
		else if(dVal<RobotMap.Cyber.driveValue || dVal == -1){
			RobotMap.Cyber.driveCounter--;
			RobotMap.Cyber.driveValue = dVal;
		}
		else{
			RobotMap.Cyber.driveValue = dVal;
		}
		if(neg==true){
			dScale = -scaleArray[RobotMap.Cyber.driveCounter];
		}
		else if(neg == false){
			dScale = scaleArray[RobotMap.Cyber.driveCounter];
		}
		return dScale;
	}
    
	public void arcadeDrive(double moveValue, double rotateValue, boolean scaledInputs) {
        double leftMotorSpeed;
        double rightMotorSpeed;
        
        moveValue = limit(moveValue);
        rotateValue = limit(rotateValue);
        percentVoltageMode();

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
       
        setMotorOutputs(leftMotorSpeed, rightMotorSpeed, scaledInputs);
    }
	
	public void forwardFullVelocity(){
		setMotorOutputs(1,1,false);
	}
	public void backwardsFullVelocity(){
		setMotorOutputs(-1,-1,false);
	}
	
    public void tankDrive(double leftValue, double rightValue, boolean scaledInputs) {
        leftValue = limit(leftValue);
        rightValue = limit(rightValue);
        setMotorOutputs(leftValue, rightValue, scaledInputs);
    }
    
    public void populateLog(long startTime){
    	if (firstLogFileRun){
    		lastTime = startTime;
    		firstLogFileRun = false;
    	}
    	curTime = System.currentTimeMillis();
    	difTime = curTime - lastTime;
    	
    	if (difTime >= 100){
    		SmartDashboard.putNumber("talon 4 throttle", rightSpeed * 100);
    		SmartDashboard.putNumber("Talon 2 Throttle", leftSpeed  * 100);
    		SmartDashboard.putNumber("Talon 2 Position", talon2Pos);
    		SmartDashboard.putNumber("Talon 2 rpm", talon2RPM);
    		SmartDashboard.putNumber("Talon 4 Position", talon4Pos);
    		SmartDashboard.putNumber("Talon 4 rpm", talon4RPM);
    		SmartDashboard.putNumber("Time", (curTime-startTime)/1000);
        	talon2Pos = DrivetrainSubsystem.getLeftMaster().getEncPosition();
    		talon2RPM = DrivetrainSubsystem.getLeftMaster().getSpeed();
    		talon4Pos = DrivetrainSubsystem.getRightMaster().getEncPosition();
    		talon4RPM = DrivetrainSubsystem.getRightMaster().getSpeed();
        	System.out.format("%s\n", "Time," + (curTime-startTime)/1000);
    		System.out.format("%s\n", "Talon 2 position:" + talon2Pos);
    		System.out.format("%s\n", "Talon 2 rpm" +  talon2RPM);
    		System.out.format("%s\n", "Talon 4 Position" + talon4Pos);
    		System.out.format("%s\n", "Talon 4 rpm" +  talon4RPM);
    		lastTime = curTime;
    	}
    	
    }
}

