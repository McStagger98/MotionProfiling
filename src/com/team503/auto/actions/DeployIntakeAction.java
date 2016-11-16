package com.team503.auto.actions;

import org.usfirst.frc.team503.robot.subsystems.Superstructure;

/**
 * DeployIntakeAction deploys the intake in autonomous mode. This Action is an
 * example of an action that only executes code once.
 *
 * @see Action
 * @see Superstructure
 */
public class DeployIntakeAction implements Action {

    private boolean mIsDone = false;
    private final Superstructure mSuperstructure = Superstructure.getInstance();

    @Override
    public boolean isFinished() {
        return mIsDone;
    }

    @Override
    public void update() {
    }

    @Override
    public void done() {
    }

    @Override
    public void start() {
//        mSuperstructure.deployIntake();
        mIsDone = true;
    }
}
