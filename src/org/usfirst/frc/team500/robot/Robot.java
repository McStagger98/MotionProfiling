
package org.usfirst.frc.team500.robot;

import org.usfirst.frc.team500.robot.commands.ArcadeDriveCommand;
import org.usfirst.frc.team500.robot.commands.RunMotionProfileCommand;
import org.usfirst.frc.team500.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team500.robot.utils.Constants.diagnosticPOSTOptions;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	// static final DriveSubsystem drive = new DriveSubsystem();
	public static OI oi;
	public static RobotHardware bot = null;
	public static Timer timer;
	private long startTime;

   public static diagnosticPOSTOptions diagnosticTestSelected;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
        //NetworkTable.globalDeleteAll(); //Removes unused garbage from SmartDashboard
		
		//chooser.addDefault("Default Auto", new ExampleCommand());
		bot = new RobotHardwareProgrammingBot();
		bot.initialize();
		OI.initialize();
		timer = new Timer();
	}
 
	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		//autonomousCommand = chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)\
		startTime = System.currentTimeMillis();
		(new RunMotionProfileCommand()).start();
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		DrivetrainSubsystem.getInstance().populateLog(startTime);
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		DrivetrainSubsystem.getInstance().percentVoltageMode();
    	DrivetrainSubsystem.getInstance().resetEncoders();
    	startTime = System.currentTimeMillis();
    	(new ArcadeDriveCommand()).start();
    	
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		DrivetrainSubsystem.getInstance().populateLog(startTime);
		Scheduler.getInstance().run();
	}
	
	/**
	 * This function is called to initialize the test mode 
	 */
	@Override
	public void testInit(){/*
		System.out.println("In Roborio Test Mode...initiating Power On Self Test (POST) Diagnostics ...");
		
		diagnosticTestSelected = null;
		UI.choosers.testInitChoosers();

		while (diagnosticTestSelected == null) {
			diagnosticTestSelected = (diagnosticPOSTOptions) SmartDashboardChooser.testChooser.getSelected();
		}
		
        switch (diagnosticTestSelected) {
            case TEST_GYRO:
                System.out.println("Testing Gyro");
                break;

            case TEST_ULTRASONIC:
                System.out.println("Testing Ultrasonic");
                break;

            case TEST_IR:
                System.out.println("Testing IR");
                break;

            case TEST_TALON_LEFT_MASTER:
                System.out.println("Testing Left Master Talon");
                break;

            case TEST_TALON_RIGHT_MASTER:
                System.out.println("Testing Right Master Talon");
                break;

            case TEST_CHASSIS_DRIVE:
                System.out.println("Testing the chassis drive");
                break;

            case TEST_SHOOTER:
                System.out.println("Testing the shooter");
                //shooterSubsystem.shoot(StrongholdConstants.FULL_THROTTLE);                
                break;

            case TEST_GRAPPLER:
                System.out.println("Testing the grappler");
             //   grapplerSubsystem.grappleToCastle();
                break;

            case TEST_ALIGN_TO_CASTLE:
                break;

            case TEST_LIFTER:
                System.out.println("Testing the lifter");
                break;
    		case TEST_MOTION_PROFILE:
    			System.out.println("Testing motion profile");
    			break;
    		case TEST_GLOBAL_POSITIONING:
    			System.out.println("Testing global positioning");
    		//	navigatorSubsystem.driveTo(0, 60);
    	//		navigatorSubsystem.driveTo(-20, 80);
    //			navigatorSubsystem.driveTo(0, 0);
    //			navigatorSubsystem.driveTo(0, 0);
    //			navigatorSubsystem.driveTo(0, 0);
    	//		navigatorSubsystem.driveTo(Math.PI/2);
    			break;

            default:
                break;
        }
*/
	}
	
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		//LiveWindow.run();
		Scheduler.getInstance().run();
	}
	

//    public FireTrapezoid() {
//        //Declare talons
//        masterTalon[0] = new CANTalon(StrongholdConstants.TALON_DRIVE_LEFT_MASTER);
//        masterTalon[1] = new CANTalon(StrongholdConstants.TALON_DRIVE_RIGHT_MASTER);

//        masterTalon[0].reverseOutput(true);
//        masterTalon[1].reverseOutput(true);
        
//        trapThread = new TrapezoidThread(masterTalon[0], masterTalon[1]);
        
//        for (int i = 0; i < 2; i++) {
//        	masterTalon[i].setFeedbackDevice(FeedbackDevice.QuadEncoder);
//        	masterTalon[i].configEncoderCodesPerRev(2048);
//        	masterTalon[i].configNominalOutputVoltage(+0.0f, -0.0f);
//        	masterTalon[i].setPID(StrongholdConstants.OPEN_DRIVE_P, StrongholdConstants.OPEN_DRIVE_I, StrongholdConstants.OPEN_DRIVE_D);
//        	masterTalon[i].setF(StrongholdConstants.OPEN_DRIVE_F);
//        }

//        slaveTalon[0] = new CANTalon(StrongholdConstants.TALON_DRIVE_LEFT_SLAVE);
//        slaveTalon[1] = new CANTalon(StrongholdConstants.TALON_DRIVE_RIGHT_SLAVE);
        
//        slaveTalon[0].reverseOutput(false);
//        slaveTalon[1].reverseOutput(false);

//        slaveTalon[0].changeControlMode(TalonControlMode.Follower);
//        slaveTalon[1].changeControlMode(TalonControlMode.Follower);
        
//        masterTalon[0].setEncPosition(0);
//        masterTalon[1].setEncPosition(0);
        
//        slaveTalon[0].set(StrongholdConstants.TALON_DRIVE_LEFT_MASTER);
//        slaveTalon[1].set(StrongholdConstants.TALON_DRIVE_RIGHT_MASTER);
//    }

}
