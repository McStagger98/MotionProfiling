package org.usfirst.frc.team500.robot.commands;

import org.usfirst.frc.team500.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import motion_profiling.MotionProfile;

/**
 *
 */
public class MotionProfileCommand extends Command {
	double[][] leftProfile, rightProfile;
	
    public MotionProfileCommand(double[][] leftProfile, double[][] rightProfile) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	
    	this.leftProfile = leftProfile;
    	this.rightProfile = rightProfile;
    	requires(DrivetrainSubsystem.getInstance());
    	DrivetrainSubsystem.getInstance().profileInit(rightProfile, leftProfile);
    }
    

    // Called just before this Command runs the first time
    protected void initialize() {
   
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	DrivetrainSubsystem.getInstance().control();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return DrivetrainSubsystem.getInstance().profileIsFinished(); 	
    }

    // Called once after isFinished returns true
    protected void end() {
    	SmartDashboard.putBoolean("finished motion profile", isFinished());
    	DrivetrainSubsystem.getInstance().reset();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
