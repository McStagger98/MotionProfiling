package com.team503.auto.actions;

import org.usfirst.frc.team503.robot.subsystems.DriveTrainSubSystem;

/**
 * Sets the robot to stop at the next detected line
 */
public class WaitUntilLineAction implements Action {
    DriveTrainSubSystem mDrive = DriveTrainSubSystem.getInstance();
    int mCount = 0;

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void update() {
    }

    @Override
    public void done() {
    }

    @Override
    public void start() {
        mDrive.setStopOnNextLine();
    }

}
