
package org.usfirst.frc.team500.robot;

import org.usfirst.frc.team500.robot.commands.ArcadeDriveCommand;
import org.usfirst.frc.team500.robot.commands.MotionProfileCommand;
import org.usfirst.frc.team500.robot.subsystems.DrivetrainSubsystem;

import cyber_commands.CyberTeleopArmCommand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import motion_profiling.GeneratedMotionProfile;
import motion_profiling.MotionProfile;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	public static RobotHardware bot = null;
	public static DrivetrainSubsystem drivetrain = null; 
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	bot = new RobotHardwareProgrammingBot();		//change for different robots
    	bot.initialize();
    	OI.initialize();
    	drivetrain = DrivetrainSubsystem.getInstance();	//Instantiate DriveTrain subsystem 
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){
    	
    }
	
	public void disabledPeriodic() {
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
    	// if (autonomousCommand != null) autonomousCommand.start();
    	
    	MotionProfile leftProfile = new MotionProfile(DrivetrainSubsystem.getLeftMaster(), GeneratedMotionProfile.Points);
    	MotionProfile rightProfile = new MotionProfile(DrivetrainSubsystem.getRightMaster(), GeneratedMotionProfile.Points);
    
    	(new MotionProfileCommand(leftProfile, rightProfile)).start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
       // if (autonomousCommand != null) autonomousCommand.cancel();
    	DrivetrainSubsystem.getInstance().percentVoltageMode();
    	(new ArcadeDriveCommand()).start();
    	
    	if (bot.getName() == "Cyber"){
    		(new CyberTeleopArmCommand()).start();
    	}
    	
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//SmartDashboard.putNumber("pressure", CyberShooterSubsystem.getPressure());
    	Scheduler.getInstance().run();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
