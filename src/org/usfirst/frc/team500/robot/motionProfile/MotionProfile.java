package org.usfirst.frc.team500.robot.motionProfile;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Notifier;

public class MotionProfile {

	private CANTalon.MotionProfileStatus status = new CANTalon.MotionProfileStatus();
	private CANTalon talon;
	private double [][] profilePoints;

	//state machine stuff
	private int state = 0;
	private int loopTimeout = -1;
	private boolean start = false;
	private CANTalon.SetValueMotionProfile setValue = CANTalon.SetValueMotionProfile.Disable;
	
	//used to maintain timing
	private static final int kMinPointsInTalon = 15;
	private static final int kNumLoopsTimeout = 10;
	
	//nested class for thread which continually pushes points to MPB
	class PeriodicRunnable implements java.lang.Runnable {
	    public void run() {
	    	talon.processMotionProfileBuffer();    
	    }
	}
	
	Notifier notifer = new Notifier(new PeriodicRunnable());

	
	public MotionProfile(CANTalon talon) {
		this.talon = talon;
		this.talon.configEncoderCodesPerRev(360);
	
		profilePoints = new double[1][1];
		profilePoints[0][0] = 0;
		
		this.talon.changeMotionControlFramePeriod(5);
		notifer.startPeriodic(0.005);
		
	}
	
	public void setProfile(double [][] points) {
		profilePoints = points;
	}
	
	public void reset() {
		talon.clearMotionProfileTrajectories();
		setValue = CANTalon.SetValueMotionProfile.Disable;
		state = 0;
		loopTimeout = -1;
		start = false;
	}

	
	public void control() {	
		talon.getMotionProfileStatus(status);

		if (loopTimeout < 0) {
			//do nothing
		}
		 
		else {
		
		//	if (_loopTimeout == 0) 
				//Instrumentation.OnNoProgress();
			if(loopTimeout != 0) 
				loopTimeout--;	
		}
		
		if (talon.getControlMode() != TalonControlMode.MotionProfile) {
			state = 0;
			loopTimeout = -1;
		} 
		
		else {
			//enter state machine
			switch (state) {
				case 0:
					if (start) {
						start = false;
						setValue = CANTalon.SetValueMotionProfile.Disable;
						startFilling();
						state = 1;
						loopTimeout = kNumLoopsTimeout;
					}
					break;
				case 1:
					if (status.btmBufferCnt > kMinPointsInTalon) {
						setValue = CANTalon.SetValueMotionProfile.Enable;
						state = 2;
						loopTimeout = kNumLoopsTimeout;
					}
					break;
				case 2:
					
					if (status.isUnderrun == false) {
						loopTimeout = kNumLoopsTimeout;
					}
				
					if (status.activePointValid && status.activePoint.isLastPoint) {
						setValue = CANTalon.SetValueMotionProfile.Hold;
						state = 0;
						loopTimeout = -1;
					}
					break;
			}
		}

	}
	
	private void startFilling() {
		startFilling(profilePoints, profilePoints.length);
	}

	private void startFilling(double[][] profile, int totalCnt) {
		CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();
		System.out.println("total length:" + totalCnt);
		if (status.hasUnderrun) {
			talon.clearMotionProfileHasUnderrun();
		}
	
		talon.clearMotionProfileTrajectories();

	
		for (int i = 0; i < totalCnt; ++i) {
			point.position = profile[i][0];
			point.velocity = profile[i][1];
			point.timeDurMs = (int) profile[i][2];
			point.profileSlotSelect = 0; 
			point.velocityOnly = false; 
										
			/**WE DO NOT USE ZEROPOS, because we do not want to continually reset encoders**/

			point.isLastPoint = (i + 1) == totalCnt;
			talon.pushMotionProfileTrajectory(point);
		}
	}

	public void startMotionProfile() {
		start = true;
	}
	
	public void stopMotionProfile() {
		start = false;
	}
	
	public CANTalon.SetValueMotionProfile getSetValue() {
		return setValue;
	}
}

