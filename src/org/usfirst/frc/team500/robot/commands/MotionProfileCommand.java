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
	
    public MotionProfileCommand(MotionProfile leftProfile, MotionProfile rightProfile) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	
   
    	requires(DrivetrainSubsystem.getInstance());
    }
    

    // Called just before this Command runs the first time
    protected void initialize() {
    	DrivetrainSubsystem.getInstance().setPathRightLeft(rightProfile, leftProfile);
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
    	DrivetrainSubsystem.getInstance().getRightMaster().set(0);
    	DrivetrainSubsystem.getInstance().getLeftMaster().set(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
