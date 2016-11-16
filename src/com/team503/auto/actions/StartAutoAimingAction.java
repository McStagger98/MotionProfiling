package com.team503.auto.actions;

import org.usfirst.frc.team503.robot.subsystems.Superstructure;

/**
 * Action to begin aiming if the superstructure has a ball
 */
public class StartAutoAimingAction implements Action {

    private final Superstructure mSuperstructure = Superstructure.getInstance();
    private boolean mIsDone = false;

    @Override
    public void start() {
    }

    @Override
    public boolean isFinished() {
        return mIsDone;
    }

    @Override
    public void update() {
   //     if (mSuperstructure.getIntake().hasBall()) {
   //         mSuperstructure.setWantedState(Superstructure.WantedState.WANT_TO_AIM);
            mIsDone = true;
   //     }
    }

    @Override
    public void done() {
    }

}
