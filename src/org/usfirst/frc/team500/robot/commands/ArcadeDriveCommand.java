package org.usfirst.frc.team500.robot.commands;

import org.usfirst.frc.team500.robot.OI;
import org.usfirst.frc.team500.robot.RobotMap;
import org.usfirst.frc.team500.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class ArcadeDriveCommand extends Command {
	private long startTime;
    public ArcadeDriveCommand() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(DrivetrainSubsystem.getInstance());
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	startTime = System.currentTimeMillis();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    	DrivetrainSubsystem.getInstance().populateLog(startTime);
    	if (OI.aPressed()){
    		DrivetrainSubsystem.getInstance().forwardFullVelocity();
    	}
    	else if (OI.bPressed()){
    		DrivetrainSubsystem.getInstance().backwardsFullVelocity();
    	}
    	else {
    		
    		DrivetrainSubsystem.getInstance().arcadeDrive(OI.getLeftYValue(), OI.getLeftXValue(), RobotMap.Cyber.DRIVE_SENSITIVITY);
    		
    		
    		
    		
    	
    	
    	
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !DriverStation.getInstance().isOperatorControl();
    }

    // Called once after isFinished returns true
    protected void end() {
    	DrivetrainSubsystem.getInstance().tankDrive(0, 0, false);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
