package org.usfirst.frc.team500.robot.subsystems;

import org.usfirst.frc.team500.robot.Robot;
import org.usfirst.frc.team500.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import motion_profiling.Instrumentation;
import motion_profiling.MotionProfile.PeriodicRunnable;

/**
 *
 */
public class DrivetrainSubsystem extends Subsystem {
	
	static CANTalon leftMaster;
	static CANTalon leftSlave;
	static CANTalon rightMaster;
	static CANTalon rightSlave;
	
	private CANTalon.SetValueMotionProfile setValue = CANTalon.SetValueMotionProfile.Disable;
	private CANTalon.MotionProfileStatus statusRight = new CANTalon.MotionProfileStatus();
	
	
	private CANTalon.MotionProfileStatus statusLeft = new CANTalon.MotionProfileStatus();
	
	private long curTime, difTime, lastTime;         
	private double talonLeftPos, talonRightPos, talonLeftRPM, talonRightRPM, rightSpeed, leftSpeed;
	private boolean firstLogFileRun = true;
	private int state = 0;
	private static final int kMinPointsInTalon = 10;
	static final int kNumLoopsTimeout = 10;
	
	private int loopTimeout = -1;
	private double[][] pointsRight;
	private double[][] pointsLeft;

	private boolean isFinished = false;  
	
	public class PeriodicRunnableRight implements Runnable {
	    public void run() {  
	    	rightMaster.processMotionProfileBuffer();
	    }
	}
	Notifier _notifierRight = new Notifier(new PeriodicRunnableRight());
	
	public class PeriodicRunnableLeft implements Runnable {
	    public void run() {  
	    	leftMaster.processMotionProfileBuffer();
	    }
	}
	Notifier _notifierLeft = new Notifier(new PeriodicRunnableLeft());
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
	public DrivetrainSubsystem(){
		leftMaster = Robot.bot.getCANTalonObj(0);
		rightMaster = Robot.bot.getCANTalonObj(1);
		leftSlave = Robot.bot.getCANTalonObj(2);
		rightSlave = Robot.bot.getCANTalonObj(3);
		
		leftMaster.reverseSensor(false);
		rightMaster.reverseSensor(false);
		
		leftMaster.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		rightMaster.setFeedbackDevice(CANTalon.FeedbackDevice.QuadEncoder);
		
		leftMaster.configEncoderCodesPerRev(360);
		rightMaster.configEncoderCodesPerRev(360);
		
		leftMaster.setF(1.511524822);
		rightMaster.setF(1.065625);
		
		leftMaster.setP(0);
		//rightMaster.setP(.04);
		rightMaster.setP(0);
		rightMaster.reverseOutput(true);
		leftMaster.reverseOutput(false);
		
		leftSlave.changeControlMode(TalonControlMode.Follower);
    	rightSlave.changeControlMode(TalonControlMode.Follower);
    	leftSlave.set(RobotMap.ProgrammingBot.leftMasterID);
    	rightSlave.set(RobotMap.ProgrammingBot.rightMasterID);
		
    	rightMaster.enableBrakeMode(false);
    	leftMaster.enableBrakeMode(false);
    	
    	rightMaster.changeMotionControlFramePeriod(5);
		rightMaster.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
		rightMaster.clearMotionProfileTrajectories();
		rightMaster.set(setValue.value);
		leftMaster.changeMotionControlFramePeriod(5);
		leftMaster.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
		leftMaster.clearMotionProfileTrajectories();
		leftMaster.set(setValue.value);
		_notifierRight.startPeriodic(.005);
		_notifierLeft.startPeriodic(.005);
		isFinished = false; 
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
    
    public void control() {
		/* Get the motion profile status every loop */
		rightMaster.getMotionProfileStatus(statusRight);
		leftMaster.getMotionProfileStatus(statusLeft);
		System.out.format("%s\n", "Got Status");
		
  		/*
		 * track time, this is rudimentary but that's okay, we just want to make
		 * sure things never get stuck.
		 */
		
		if (loopTimeout < 0) {
			/* do nothing, timeout is disabled */
		} else {
			/* our timeout is nonzero */
			if (loopTimeout == 0) {
				/*
				 * something is wrong. Talon is not present, unplugged, breaker tripped
				 */
				Instrumentation.OnNoProgress();
			} else {
				loopTimeout--;
			}
		}

		/* first check if we are in MP mode */
		if ((rightMaster.getControlMode() != TalonControlMode.MotionProfile)&&(leftMaster.getControlMode() != TalonControlMode.MotionProfile)) {
			/*
			 * we are not in MP mode. We are probably driving the robot around
			 * using gamepads or some other mode.
			 */
			System.out.format("%s\n", "Not in Motion Profile Mode");
			state = 0;
			loopTimeout = -1;                      
		} else {
			/*
			 * we are in MP control mode. That means: starting Mps, checking Mp
			 * progress, and possibly interrupting MPs if thats what you want to
			 * do.
			 */
			System.out.format("%s\n", "State = " + state);
			switch (state) {
				case 0: /* wait for application to tell us to start an MP */
						setValue = CANTalon.SetValueMotionProfile.Disable;
						startFilling();
						/*
						 * MP is being sent to CAN bus, wait a small amount of time
						 */
						state = 1;
						loopTimeout = kNumLoopsTimeout;
					break;
				case 1: /*
						 * wait for MP to stream to Talon, really just the first few
						 * points
						 */
					/* do we have a minimum numberof points in Talon */
					System.out.format("%s\n", "Waiing for Stream to build on Talon");
					if ((statusRight.btmBufferCnt > kMinPointsInTalon)&&(statusLeft.btmBufferCnt > kMinPointsInTalon)) {
						/* start (once) the motion profile */
						setValue = CANTalon.SetValueMotionProfile.Enable;
						/* MP will start once the control frame gets scheduled */
						state = 2;
						loopTimeout = kNumLoopsTimeout;
					}
					break;
				case 2: /* check the status of the MP */
					/*
					 * if talon is reporting things are good, keep adding to our
					 * timeout. Really this is so that you can unplug your talon in
					 * the middle of an MP and react to it.
					 */
					if ((statusRight.isUnderrun == false)&&(statusLeft.isUnderrun == false)) {
						loopTimeout = kNumLoopsTimeout;
						System.out.format("%s\n", "Things as fine-noting to do!!");
					}
					
					/*
					 * If we are executing an MP and the MP finished, start loading
					 * another. We will go into hold state so robot servo's
					 * position.
					 */
					if ((statusRight.activePointValid && statusRight.activePoint.isLastPoint)&&(statusLeft.activePointValid && statusLeft.activePoint.isLastPoint)) {
						/*
						 * because we set the last point's isLast to true, we will
						 * get here when the MP is done
						 */
						System.out.format("%s\n", "Finished!! time:" + System.currentTimeMillis());
						setValue = CANTalon.SetValueMotionProfile.Hold;
						state = 3;
						loopTimeout = -1;
						isFinished = true;
						
					}
					break;
			}
		}

		System.out.format("%s\n", "Set value.value: " + setValue.value);
		System.out.format("%s\n", "mode Right:" + rightMaster.getControlMode());
		System.out.format("%s\n", "mode Left:" + leftMaster.getControlMode());
		
		rightMaster.set(setValue.value);
		leftMaster.set(setValue.value);
		
		/* printfs and/or logging */
		Instrumentation.process(statusLeft);
		Instrumentation.process(statusRight);
	}

    public void reset() {
		/*
		 * Let's clear the buffer just in case user decided to disable in the
		 * middle of an MP, and now we have the second half of a profile just
		 * sitting in memory.
		 */
		rightMaster.clearMotionProfileTrajectories();
		leftMaster.clearMotionProfileTrajectories();
			
		/* When we do re-enter motionProfile control mode, stay disabled. */
		setValue = CANTalon.SetValueMotionProfile.Disable;
		/* When we do start running our state machine start at the beginning. */
		state = 0;
		loopTimeout = -1;
		/*
		 * If application wanted to start an MP before, ignore and wait for next
		 * button press
		 */
		isFinished = false; 
		rightMaster.set(setValue.value);
		leftMaster.set(setValue.value);
	}
    private void startFilling() {
		System.out.format("%s\n", "Start Filling Buffer");
		/* create an empty point */
		CANTalon.TrajectoryPoint pointRight = new CANTalon.TrajectoryPoint();
		CANTalon.TrajectoryPoint pointLeft = new CANTalon.TrajectoryPoint();

		/* did we get an underrun condition since last time we checked ? */
		if (statusRight.hasUnderrun) {
			/* better log it so we know about it */
			Instrumentation.OnUnderrun();
			/*
			 * clear the error. This flag does not auto clear, this way 
			 * we never miss logging it.
			 */
			rightMaster.clearMotionProfileHasUnderrun();
			
		}
		if (statusLeft.hasUnderrun){
			Instrumentation.OnUnderrun();
			leftMaster.clearMotionProfileHasUnderrun();
		}
		/*
		 * just in case we are interrupting another MP and there is still buffer
		 * points in memory, clear it.
		 */
		rightMaster.clearMotionProfileTrajectories();
		leftMaster.clearMotionProfileTrajectories();

		/* This is fast since it's just into our TOP buffer */
		for (int i = 0; i < pointsRight.length; ++i) {
			/* for each point, fill our structure and pass it to API */
			pointRight.position = pointsRight[i][0];
			pointRight.velocity = pointsRight[i][1];
			pointRight.timeDurMs = (int) pointsRight[i][2];
			pointRight.profileSlotSelect = 0; /* which set of gains would you like to use? */
			pointRight.velocityOnly = false; /* set true to not do any position
										 * servo, just velocity feedforward
										 */
			pointRight.zeroPos = false;
			if (i == 0)
				pointRight.zeroPos = true; /* set this to true on the first point */

			pointRight.isLastPoint = false;
			if ((i + 1) == pointsRight.length)
				pointRight.isLastPoint = true; /* set this to true on the last point  */

				
			leftMaster.pushMotionProfileTrajectory(pointRight);
		}
		for (int i = 0; i < pointsLeft.length; ++i) {
			/* for each point, fill our structure and pass it to API */
			pointLeft.position = pointsLeft[i][0];
			pointLeft.velocity = pointsLeft[i][1];
			pointLeft.timeDurMs = (int) pointsLeft[i][2];
			pointLeft.profileSlotSelect = 0; /* which set of gains would you like to use? */
			pointLeft.velocityOnly = false; /* set true to not do any position
										 * servo, just velocity feedforward
										 */
			pointLeft.zeroPos = false;
			if (i == 0)
				pointLeft.zeroPos = true; /* set this to true on the first point */

			pointLeft.isLastPoint = false;
			if ((i + 1) == pointsRight.length)
				pointLeft.isLastPoint = true; /* set this to true on the last point  */

				
			leftMaster.pushMotionProfileTrajectory(pointLeft);
		}
		System.out.format("%s\n", "Points Path has been loaded !!");
	}
    // THIS METHOD MUST BE CALLED BEFORE RUNNING A PROFILE. 
    public void setPathRightLeft(double[][] pointsRight, double[][] pointsLeft){
    	this.pointsLeft = pointsLeft;
    	this.pointsRight = pointsRight;
    }

    
    public void populateLog(long startTime){
    	if (firstLogFileRun){
    		lastTime = startTime;
    		firstLogFileRun = false;
    	}
    	curTime = System.currentTimeMillis();
    	difTime = curTime - lastTime;
    	
    	if (difTime >= 100){
    		SmartDashboard.putNumber("talon right throttle", rightSpeed);
    		SmartDashboard.putNumber("Talon left Throttle", leftSpeed);
    		SmartDashboard.putNumber("Talon left Position", talonLeftPos);
    		SmartDashboard.putNumber("Talon left rpm", talonLeftRPM);
    		SmartDashboard.putNumber("Talon right Position", -talonRightPos);
    		SmartDashboard.putNumber("Talon right rpm", -talonRightRPM);
    		SmartDashboard.putNumber("Time", (curTime-startTime)/1000);
        	talonLeftPos = DrivetrainSubsystem.getLeftMaster().getEncPosition();
    		talonLeftRPM = DrivetrainSubsystem.getLeftMaster().getSpeed();
    		talonRightPos = DrivetrainSubsystem.getRightMaster().getEncPosition();
    		talonRightRPM = DrivetrainSubsystem.getRightMaster().getSpeed();
        	System.out.format("%s\n", "Time," + (curTime-startTime)/1000);
    		System.out.format("%s\n", "Talon left position: " + talonLeftPos);
    		System.out.format("%s\n", "Talon left rpm: " +  talonLeftRPM);
    		System.out.format("%s\n", "Talon right position: " + talonRightPos);
    		System.out.format("%s\n", "Talon right rpm: " +  talonRightRPM);
    		lastTime = curTime;
    	}
    	
    }
}

