package org.usfirst.frc.team500.robot.commands;

import org.usfirst.frc.team500.robot.motionProfile.GeneratedMotionProfile;
import org.usfirst.frc.team500.robot.motionProfile.PathPlanner;
import org.usfirst.frc.team500.robot.subsystems.DrivetrainSubsystem;
import org.usfirst.frc.team500.robot.utils.Constants;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class RunMotionProfileCommand extends Command {
	double[][] leftProfile, rightProfile;
    public RunMotionProfileCommand(double[][] waypoints, double maxTime) {
    	PathPlanner planner = new PathPlanner(waypoints);
    	planner.calculate(maxTime, Constants.ITP, ((double)Constants.WHEEL_BASE/12));
    	this.leftProfile = planner.getLeftProfile();
    	this.rightProfile = planner.getRightProfile();
        requires(DrivetrainSubsystem.getInstance());
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	DrivetrainSubsystem.getInstance().runProfileLeftRight(rightProfile, leftProfile);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {	
    	return DrivetrainSubsystem.getInstance().profileHasFinished;
    }

    // Called once after isFinished returns true
    protected void end() {
    	DrivetrainSubsystem.getInstance().getRightMaster().set(0);
		DrivetrainSubsystem.getInstance().getLeftMaster().set(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
