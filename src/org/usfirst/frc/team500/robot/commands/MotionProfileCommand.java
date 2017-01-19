package org.usfirst.frc.team500.robot.commands;

import org.usfirst.frc.team500.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import motion_profiling.MotionProfile;

/**
 *
 */
public class MotionProfileCommand extends Command {
	MotionProfile leftProfile, rightProfile;
	
    public MotionProfileCommand(MotionProfile leftProfile, MotionProfile rightProfile) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.leftProfile = leftProfile;
    	this.rightProfile = rightProfile;
    	requires(DrivetrainSubsystem.getInstance());
    }
    

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	leftProfile.control();
    	rightProfile.control();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return leftProfile.isFinished() && rightProfile.isFinished(); 	
    }

    // Called once after isFinished returns true
    protected void end() {
    	SmartDashboard.putBoolean("finished motion profile", isFinished());
    	leftProfile.getTalon().set(0);
    	rightProfile.getTalon().set(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
