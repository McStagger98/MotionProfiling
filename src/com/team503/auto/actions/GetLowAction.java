package com.team503.auto.actions;

//import org.usfirst.frc.team503.robot.subsystems.UtilityArm;

/**
 * Action for lowering the utility arm
 */
public class GetLowAction implements Action {

  //  private final UtilityArm mUtilityArm = UtilityArm.getInstance();

    @Override
    public boolean isFinished() {
 //       return mUtilityArm.isSafeToDriveThroughPortcullis();
    	return true;  // added by 503 
    }

    @Override
    public void start() {
  //      mUtilityArm.setWantedState(UtilityArm.WantedState.LOW_BAR);
    }

    @Override
    public void update() {
    }

    @Override
    public void done() {
    }
}
