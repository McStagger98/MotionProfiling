
package org.usfirst.frc.team503.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import org.usfirst.frc.team503.robot.commands.ExampleCommand;
import org.usfirst.frc.team503.robot.subsystems.DriveTrainSubSystem;
import org.usfirst.frc.team503.robot.subsystems.ExampleSubsystem;
import org.usfirst.frc.team503.robot.subsystems.Superstructure;

import com.team503.auto.AutoModeExecuter;
import com.team503.lib.Loops.Looper;
import com.team503.lib.Loops.RobotStateEstimator;
import com.team503.lib.util.CrashTracker;
import com.team503.lib.util.DriveSignal;
import com.team503.lib.util.RigidTransform2d;
import com.team503.lib.util.Rotation2d;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;

    // Subsystems
    DriveTrainSubSystem mDrive = DriveTrainSubSystem.getInstance();
    Superstructure mSuperstructure = Superstructure.getInstance();
//    UtilityArm mUtilityArm = UtilityArm.getInstance();
//    Compressor mCompressor = new Compressor(1);
//    RevRoboticsAirPressureSensor mAirPressureSensor = new RevRoboticsAirPressureSensor(3);
    AutoModeExecuter mAutoModeExecuter = null;
//    DigitalOutput mHasBallLightOutput = new DigitalOutput(0);

    // Other parts of the robot
    CheesyDriveHelper mCheesyDriveHelper = new CheesyDriveHelper();
//    ControlBoard mControls = ControlBoard.getInstance();
//    VisionServer mVisionServer = VisionServer.getInstance();
    RobotState mRobotState = RobotState.getInstance();

    // Enabled looper is called at 100Hz whenever the robot is enabled
    Looper mEnabledLooper = new Looper();
    // Disabled looper is called at 100Hz whenever the robot is disabled
    Looper mDisabledLooper = new Looper();

    SmartDashboardInteractions mSmartDashboardInteractions = new SmartDashboardInteractions();

    boolean mLogToSmartdashboard = true;
    boolean mHoodTuningMode = false;

    boolean mGetDown = false;

    public Robot() {
        CrashTracker.logRobotConstruction();
    }

    public void stopAll() {
        mDrive.stop();
        mSuperstructure.stop();
//        mUtilityArm.stop();
    }

    public void outputAllToSmartDashboard() {
        if (mLogToSmartdashboard) {
            mDrive.outputToSmartDashboard();
            mSuperstructure.outputToSmartDashboard();
            mRobotState.outputToSmartDashboard();
//            mUtilityArm.outputToSmartDashboard();
            mEnabledLooper.outputToSmartDashboard();
        }
//        SmartDashboard.putNumber("Air Pressure psi", mAirPressureSensor.getAirPressurePsi());
    }

    public void zeroAllSensors() {
        mDrive.zeroSensors();
        mSuperstructure.zeroSensors();
//        mUtilityArm.zeroSensors();
        mRobotState.reset(Timer.getFPGATimestamp(), new RigidTransform2d(), new Rotation2d());
    }
    
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
		oi = new OI();
//        chooser = new SendableChooser();
//        chooser.addDefault("Default Auto", new ExampleCommand());
//        chooser.addObject("My Auto", new MyAutoCommand());
//        SmartDashboard.putData("Auto mode", chooser);
        
        try {
            CrashTracker.logRobotInit();
//            mVisionServer.addVisionUpdateReceiver(VisionProcessor.getInstance());

            // Reset all state
            zeroAllSensors();

//            mUtilityArm.setWantedState(UtilityArm.WantedState.STAY_IN_SIZE_BOX);

            // Configure loopers
//            mEnabledLooper.register(new TurretResetter());
//            mEnabledLooper.register(VisionProcessor.getInstance());
            mEnabledLooper.register(RobotStateEstimator.getInstance());
            mEnabledLooper.register(Superstructure.getInstance().getLoop());
            mEnabledLooper.register(mDrive.getLoop());
//            mEnabledLooper.register(mUtilityArm.getLoop());
//            mDisabledLooper.register(new GyroCalibrator());

            mSmartDashboardInteractions.initWithDefaults();

 //           mCompressor.start();

//            VisionServer.getInstance();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){
    	try {
            CrashTracker.logDisabledInit();
            if (mAutoModeExecuter != null) {
                mAutoModeExecuter.stop();
            }
            mAutoModeExecuter = null;

            // Configure loopers
            mEnabledLooper.stop();
            mDisabledLooper.start();

            mDrive.setOpenLoop(DriveSignal.NEUTRAL);
            mDrive.setBrakeMode(true);
            // Stop all actuators
            stopAll();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }
	
	public void disabledPeriodic() {
		 try {
	            stopAll();

	            mDrive.resetEncoders();

	            outputAllToSmartDashboard();

	            mHoodTuningMode = mSmartDashboardInteractions.isInHoodTuningMode();
	            mLogToSmartdashboard = mSmartDashboardInteractions.shouldLogToSmartDashboard();
//	            mRobotState.reset(Timer.getFPGATimestamp(), new RigidTransform2d(), mSuperstructure.getTurret().getAngle());

	            updateDriverFeedback();

	            System.gc();
	        } catch (Throwable t) {
	            CrashTracker.logThrowableCrash(t);
	            throw t;
	        }
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {
    	  try {
              CrashTracker.logAutoInit();
              if (mAutoModeExecuter != null) {
                  mAutoModeExecuter.stop();
              }
              mAutoModeExecuter = null;

              // Reset all sensors
              zeroAllSensors();

              // Shift to high
              mDrive.setHighGear(true);
              mDrive.setBrakeMode(true);
              mSuperstructure.setTuningMode(false);
              mSuperstructure.setHoodAdjustment(mSmartDashboardInteractions.areAutoBallsWorn()
                      ? Constants.kOldBallHoodAdjustment : Constants.kNewBallHoodAdjustment);

//              maybeResetUtilityArmState();

              // Configure loopers
              mDisabledLooper.stop();
              mEnabledLooper.start();

              mAutoModeExecuter = new AutoModeExecuter();
              mAutoModeExecuter.setAutoMode(mSmartDashboardInteractions.getSelectedAutonMode());
              mAutoModeExecuter.start();
          } catch (Throwable t) {
              CrashTracker.logThrowableCrash(t);
              throw t;
          }
    	
//    	autonomousCommand = (Command) chooser.getSelected();
        
		/* String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		switch(autoSelected) {
		case "My Auto":
			autonomousCommand = new MyAutoCommand();
			break;
		case "Default Auto":
		default:
			autonomousCommand = new ExampleCommand();
			break;
		} */
    	
    	// schedule the autonomous command (example)
//        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	 try {
             outputAllToSmartDashboard();
             updateDriverFeedback();
             Scheduler.getInstance().run();
         } catch (Throwable t) {
             CrashTracker.logThrowableCrash(t);
             throw t;
         }
    	
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
//        if (autonomousCommand != null) autonomousCommand.cancel();
        try {
            CrashTracker.logTeleopInit();
            if (mAutoModeExecuter != null) {
                mAutoModeExecuter.stop();
            }
            mAutoModeExecuter = null;

            // Reset drive
            mDrive.resetEncoders();

 //           maybeResetUtilityArmState();

            // Configure loopers
            mDisabledLooper.stop();
            mEnabledLooper.start();
            mDrive.setOpenLoop(DriveSignal.NEUTRAL);
            mDrive.setBrakeMode(false);

            mGetDown = false;
            mSuperstructure.setWantedState(Superstructure.WantedState.WANT_TO_DEPLOY);
            mSuperstructure.stowIntake();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	try {
//            double throttle = mControls.getThrottle();
//            double turn = mControls.getTurn();
//            if (mControls.getTractionControl()) {
//                Rotation2d heading_setpoint = mDrive.getGyroAngle();
//                if (mDrive.getControlState() == Drive.DriveControlState.VELOCITY_HEADING_CONTROL) {
//                    heading_setpoint = mDrive.getVelocityHeadingSetpoint().getHeading();
//                }
//                mDrive.setVelocityHeadingSetpoint(
//                        mCheesyDriveHelper.handleDeadband(throttle, CheesyDriveHelper.kThrottleDeadband)
//                                * Constants.kDriveLowGearMaxSpeedInchesPerSec,
//                        heading_setpoint);
//            } else {
//                mDrive.setBrakeMode(false);
//               mDrive.setHighGear(!mControls.getLowGear());
//                mDrive.setOpenLoop(mCheesyDriveHelper.cheesyDrive(throttle, turn, mControls.getQuickTurn()));
//            }

//            if (mControls.getIntakeButton()) {
//                mSuperstructure.deployIntake();
//                mSuperstructure.setWantsToRunIntake();
//            } else if (mControls.getStowIntakeButton()) {
//                mSuperstructure.stowIntake();
//                mSuperstructure.setWantsToStopIntake();
//            } else if (mControls.getExhaustButton()) {
//                mSuperstructure.setWantsToExhaust();
//            } else {
//                mSuperstructure.setWantsToStopIntake();
//            }

//            Superstructure.WantedState idle_state = mControls.getKeepWheelRunning()
//                    ? Superstructure.WantedState.WANT_TO_KEEP_SPINNING : Superstructure.WantedState.WANT_TO_DEPLOY;
//            if (mControls.getAutoAimNewBalls()) {
//                mSuperstructure.setHoodAdjustment(Constants.kNewBallHoodAdjustment);
//                mSuperstructure.setWantedState(Superstructure.WantedState.WANT_TO_AIM);
//                mGetDown = false;
//            } else if (mControls.getAutoAimOldBalls()) {
//                mSuperstructure.setHoodAdjustment(Constants.kOldBallHoodAdjustment);
//                mSuperstructure.setWantedState(Superstructure.WantedState.WANT_TO_AIM);
//                mGetDown = false;
//            } else if (mControls.getHoodUpButton()) {
//                mSuperstructure.setWantedState(idle_state);
//                mGetDown = false;
//            } else if (mControls.getHoodDownButton()) {
//                mSuperstructure.setWantedState(Superstructure.WantedState.WANT_TO_STOW);
//                mGetDown = true;
//            } else {
//                mSuperstructure.setWantedState(mGetDown ? Superstructure.WantedState.WANT_TO_STOW : idle_state);
//            }

//            mSuperstructure.setTurretManualScanOutput(mControls.getTurretManual() * .66);

//            if (mControls.getFireButton()) {
//               mSuperstructure.setWantsToFireWhenReady();
//            } else {
//                mSuperstructure.setWantsToHoldFire();
//            }

//            if (mControls.getPortcullisButton()) {
//                mSuperstructure.deployIntake();
//                mGetDown = true;
//                mUtilityArm.setWantedState(UtilityArm.WantedState.LOW_BAR);
//            } else if (mControls.getCdfButton()) {
//                mUtilityArm.setWantedState(UtilityArm.WantedState.LOW_BAR);
//            } else if (mControls.getBailButton()) {
//                mUtilityArm.setWantedState(UtilityArm.WantedState.DRIVING);
//            } else if (mControls.getDeployHangerButton()) {
//                mSuperstructure.stowIntake();
//                mUtilityArm.setWantedState(UtilityArm.WantedState.PREPARE_FOR_HANG);
//            }

//            if (mUtilityArm.isAllowedToHang()) {
//                if (mControls.getHang()) {
//                    mUtilityArm.setWantedState(UtilityArm.WantedState.PULL_UP_HANG);
//                    mCompressor.stop();
//                } else {
//                    mUtilityArm.setWantedState(UtilityArm.WantedState.PREPARE_FOR_HANG);
//                    mCompressor.start();
//                }
//            }

//            if (mHoodTuningMode) {
//                mSuperstructure.setTuningMode(true);
//                if (mControls.getHoodTuningPositiveButton()) {
//                    mSuperstructure.setHoodManualScanOutput(0.05);
//                } else if (mControls.getHoodTuningNegativeButton()) {
//                    mSuperstructure.setHoodManualScanOutput(-0.05);
//                } else {
//                    mSuperstructure.setHoodManualScanOutput(0.0);
//                }
//            } else {
//                mSuperstructure.setTuningMode(false);
//            }

//            if (mControls.getHoodTuningPositiveButton()) {
//                mSuperstructure.setTestServoSpeed(1.0);
//            } else if (mControls.getHoodTuningNegativeButton()) {
//                mSuperstructure.setTestServoSpeed(-1.0);
//            } else {
//                mSuperstructure.setTestServoSpeed(0.0);
//            }

//            if (mControls.getRestartCameraAppButton()) {
//                mVisionServer.requestAppRestart();
//            }

//            outputAllToSmartDashboard();
//            updateDriverFeedback();
        } catch (Throwable t) {
            CrashTracker.logThrowableCrash(t);
            throw t;
        }
    	
    	Scheduler.getInstance().run();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    
    private static final String COLOR_BOX_COLOR_KEY = "color_box_color";
    private static final String COLOR_BOX_TEXT_KEY = "color_box_text";

    private void updateDriverFeedback() {
 //       boolean hasBall = mSuperstructure.getIntake().hasBall();
 //       mHasBallLightOutput.set(hasBall);
    	boolean hasBall = false; //added by 503 - not in original code 
        if (hasBall) {
            SmartDashboard.putString(COLOR_BOX_COLOR_KEY, "#00ff00");
            SmartDashboard.putString(COLOR_BOX_TEXT_KEY, "HAVE BALL");
        } else {
            SmartDashboard.putString(COLOR_BOX_COLOR_KEY, "#ff0000");
            SmartDashboard.putString(COLOR_BOX_TEXT_KEY, "NO BALL");
        }
    }
}
