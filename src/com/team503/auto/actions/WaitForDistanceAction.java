package com.team503.auto.actions;

import org.usfirst.frc.team503.robot.subsystems.DriveTrainSubSystem;

/**
 * This action completes when the robot moves past a specified distance
 */
public class WaitForDistanceAction implements Action {
    private final double mMinDistance;
    private final DriveTrainSubSystem mDrive = DriveTrainSubSystem.getInstance();

    public WaitForDistanceAction(double minDistance) {
        mMinDistance = minDistance;
    }

    @Override
    public boolean isFinished() {
        return getCurrentDistance() >= mMinDistance;
    }

    @Override
    public void update() {
    }

    @Override
    public void done() {
    }

    @Override
    public void start() {
    }

    private double getCurrentDistance() {
        return (mDrive.getLeftDistanceInches() + mDrive.getRightDistanceInches()) / 2;
    }
}
