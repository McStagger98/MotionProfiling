/**
 * Example logic for firing and managing motion profiles.
 * This example sends MPs, waits for them to finish
 * Although this code uses a CANTalon, nowhere in this module do we changeMode() or call set() to change the output.
 * This is done in Robot.java to demonstrate how to change control modes on the fly.
 * 
 * The only routines we call on Talon are....
 * 
 * changeMotionControlFramePeriod
 * 
 * getMotionProfileStatus		
 * clearMotionProfileHasUnderrun     to get status and potentially clear the error flag.
 * 
 * pushMotionProfileTrajectory
 * clearMotionProfileTrajectories
 * processMotionProfileBuffer,   to push/clear, and process the trajectory points.
 * 
 * getControlMode, to check if we are in Motion Profile Control mode.
 * 
 * Example of advanced features not demonstrated here...
 * [1] Calling pushMotionProfileTrajectory() continuously while the Talon executes the motion profile, thereby keeping it going indefinitely.
 * [2] Instead of setting the sensor position to zero at the start of each MP, the program could offset the MP's position based on current position. 
 */
package motion_profiling;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


//THIS CLASS IS NOW DEPRECATED!
/**
 * @deprecated  Use methods built into drivetrainsubsystem {@link #getPreferredSize()}
 */
@Deprecated
public class MotionProfile {
	/**
	 * The status of the motion profile executer and buffer inside the Talon.
	 * Instead of creating a new one every time we call getMotionProfileStatus,
	 * keep one copy.
	 */
	private CANTalon.MotionProfileStatus status = new CANTalon.MotionProfileStatus();
	
	/**
	 * reference to the talon we plan on manipulating. We will not changeMode()
	 * or call set(), just get motion profile status and make decisions based on
	 * motion profile.
	 */
	private CANTalon talon; 
	
	/**
	 * The trajectory points 
	 */
	private double[][] points;
	/**
	 * State machine to make sure we let enough of the motion profile stream to
	 * talon before we fire it.
	 */
	private int state = 0;
	/**
	 * Any time you have a state machine that waits for external events, its a
	 * good idea to add a timeout. Set to -1 to disable. Set to nonzero to count
	 * down to '0' which will print an error message. Counting loops is not a
	 * very accurate method of tracking timeout, but this is just conservative
	 * timeout. Getting time-stamps would certainly work too, this is just
	 * simple (no need to worry about timer overflows).
	 */
	private int loopTimeout = -1;


	/**
	 * This flag will be used to identify when the profile path has ended  
	 */
	private boolean isFinished = false;  
	/**
	 * Since the CANTalon.set() routine is mode specific, deduce what we want
	 * the set value to be and let the calling module apply it whenever we
	 * decide to switch to MP mode.
	 */
	private CANTalon.SetValueMotionProfile setValue = CANTalon.SetValueMotionProfile.Disable;
	/**
	 * How many trajectory points do we wait for before firing the motion
	 * profile.
	 */
	private static final int kMinPointsInTalon = 10;
	/**
	 * Just a state timeout to make sure we don't get stuck anywhere. Each loop
	 * is about 20ms.
	 */
	private static final int kNumLoopsTimeout = 10;
	
	/**
	 * Lets create a periodic task to funnel our trajectory points into our talon.
	 * It doesn't need to be very accurate, just needs to keep pace with the motion
	 * profiler executer.  Now if you're trajectory points are slow, there is no need
	 * to do this, just call _talon.processMotionProfileBuffer() in your teleop loop.
	 * Generally speaking you want to call it at least twice as fast as the duration
	 * of your trajectory points.  So if they are firing every 20ms, you should call 
	 * every 10ms.
	 */
	public class PeriodicRunnable implements Runnable {
	    public void run() {  
	    	talon.processMotionProfileBuffer();
	    }
	}
	
	Notifier _notifier = new Notifier(new PeriodicRunnable());
	
	/**
	 * C'tor
	 * 
	 * @param talon
	 *            reference to Talon object to fetch motion profile status from.
	 */
	public MotionProfile(CANTalon talon, double[][] points){
		this.talon = talon; 
		this.points = points;
		
		/*
		 * since our MP is 10ms per point, set the control frame rate and the
		 * notifer to half that
		 */
		talon.changeMotionControlFramePeriod(5);
		talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
		talon.clearMotionProfileTrajectories();
		talon.set(setValue.value);
		_notifier.startPeriodic(.005);
		isFinished = false; 
	}

	/**
	 * Called to clear Motion profile buffer and reset state info during
	 * disabled and when Talon is not in MP control mode.
	 */
	public void reset() {
		/*
		 * Let's clear the buffer just in case user decided to disable in the
		 * middle of an MP, and now we have the second half of a profile just
		 * sitting in memory.
		 */
		talon.clearMotionProfileTrajectories();
			
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
		talon.set(setValue.value);
	}

	/**
	 * Called every loop.
	 */
	public void control() {
		/* Get the motion profile status every loop */
		talon.getMotionProfileStatus(status);
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
		if (talon.getControlMode() != TalonControlMode.MotionProfile) {
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
					if (status.btmBufferCnt > kMinPointsInTalon) {
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
					if (status.isUnderrun == false) {
						loopTimeout = kNumLoopsTimeout;
						System.out.format("%s\n", "Things as fine-noting to do!!");
					}
					
					/*
					 * If we are executing an MP and the MP finished, start loading
					 * another. We will go into hold state so robot servo's
					 * position.
					 */
					if (status.activePointValid && status.activePoint.isLastPoint) {
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
		System.out.format("%s\n", "mode:" + talon.getControlMode());
		
		talon.set(setValue.value);
		
		/* printfs and/or logging */
		Instrumentation.process(status);
	}

	/** Start filling the MPs to all of the involved Talons. */

	private void startFilling() {
		System.out.format("%s\n", "Start Filling Buffer");
		/* create an empty point */
		CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();

		/* did we get an underrun condition since last time we checked ? */
		if (status.hasUnderrun) {
			/* better log it so we know about it */
			Instrumentation.OnUnderrun();
			/*
			 * clear the error. This flag does not auto clear, this way 
			 * we never miss logging it.
			 */
			talon.clearMotionProfileHasUnderrun();
		}
		/*
		 * just in case we are interrupting another MP and there is still buffer
		 * points in memory, clear it.
		 */
		talon.clearMotionProfileTrajectories();

		/* This is fast since it's just into our TOP buffer */
		for (int i = 0; i < points.length; ++i) {
			/* for each point, fill our structure and pass it to API */
			point.position = points[i][0];
			point.velocity = points[i][1];
			point.timeDurMs = (int) points[i][2];
			point.profileSlotSelect = 0; /* which set of gains would you like to use? */
			point.velocityOnly = false; /* set true to not do any position
										 * servo, just velocity feedforward
										 */
			point.zeroPos = false;
			if (i == 0)
				point.zeroPos = true; /* set this to true on the first point */

			point.isLastPoint = false;
			if ((i + 1) == points.length)
				point.isLastPoint = true; /* set this to true on the last point  */

			talon.pushMotionProfileTrajectory(point);			
		}
		System.out.format("%s\n", "Points Path has been loaded !!");
	}

	/**
	 * Called by application to signal Talon to start the buffered MP (when it's
	 * able to).
	 * @throws Exception 
	 */
	
	
	public int getState(){
		return state;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public CANTalon getTalon(){
		return talon;
	}
	
	/**
	 * 
	 * @return the output value to pass to Talon's set() routine. 0 for disable
	 *         motion-profile output, 1 for enable motion-profile, 2 for hold
	 *         current motion profile trajectory point.
	 */
	CANTalon.SetValueMotionProfile getSetValue() {
		return setValue;
	}
	
}
