package org.usfirst.frc.team500.robot.commands;

import org.usfirst.frc.team500.robot.motionProfile.PathPlanner;
import org.usfirst.frc.team500.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team500.robot.utils.Constants;

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
    
    public MotionProfileCommand(double[][] waypoints, double maxTime){
    	// Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	PathPlanner planner = new PathPlanner(waypoints);
    	planner.calculate(maxTime, Constants.ITP, Constants.WHEEL_BASE);
    	this.leftProfile = planner.getLeftProfile();
    	this.rightProfile = planner.getRightProfile();
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
