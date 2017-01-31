package org.usfirst.frc.team500.robot.utils;

import org.usfirst.frc.team500.robot.utils.Constants.teleopModes;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class UI {
	//This class controls the I/O of the rhinoDriver station, basically the back-end of the user interface
    //It takes in inputs such as button presses and the x and y of the joystick; does not drive anything
    //Depending on the method used, it outputs either a boolean or an x and y value that should be passed on to another class

    //Constants may need to be changed

    public static Constants.shootHeightOptions teleopShootHeightOption;
    public static boolean shooterRunning = false, assistShoot = false;

    public static int pos[] = new int[5];

    public static SmartDashboardChooser choosers;

    private static int green = 0, orange = 0, red = 0;

    //Constructor
    public UI() {
    }

    public static void createUI()
    {
        NetworkTable.initialize();

        //Defense at position 0 is always low bar (ID 0)
        pos[0] = 0;

        choosers = new SmartDashboardChooser();
        choosers.initChoosers();
        //choosers.autoInitChoosers();
    }
   
//    public static int getSelectedDefensePosition()
//    {
//        int position = -1;
//
//        //Defense at position 0 is always low bar (ID 0)
//        pos[0] = 0;
//        pos[1] = (int) SmartDashboard.getNumber("Defense at Position 2", -1);
//        pos[2] = (int) SmartDashboard.getNumber("Defense at Position 3", -1);
//        pos[3] = (int) SmartDashboard.getNumber("Defense at Position 4", -1);
//        pos[4] = (int) SmartDashboard.getNumber("Defense at Position 5", -1);
//
//        int[] defensePositions = new int[9];
//
//        for (int i = 1; i < 9; i++)
//        {
//            defensePositions[i] = -1;
//        }
//
//        for (int i = 1; i < 9; i++)
//        {
//            for (int c = 1; c < 5; c++)
//            {
//                if (pos[c] == i) {
//                    defensePositions[i] = c;
//                }
//            }
//        }
//
//        //If auto is running, get position from SmartDashboard
//        if (StrongholdRobot.teleopNotRunning)
//        {
//            switch ((defenseTypeOptions) SmartDashboardChooser.defenseChooser.getSelected())
//            {
//                case LOW_BAR:
//                    position = 0; //Position of low bar is always 0
//                    break;
//                case PORTCULLIS:
//                    position = defensePositions[1];
//                    break;
//                case CHIVAL_DE_FRISE:
//                    position = defensePositions[2];
//                    break;
//                case MOAT:
//                    position = defensePositions[3];
//                    break;
//                case RAMPARTS:
//                    position = defensePositions[4];
//                    break;
//                case DRAWBRIDGE:
//                    position = defensePositions[5];
//                    break;
//                case SALLYPORT:
//                    position = defensePositions[6];
//                    break;
//                case ROCK_WALL:
//                    position = defensePositions[7];
//                    break;
//                case ROUGH_TERRAIN:
//                    position = defensePositions[8];
//                    break;
//                case NONE:
//                    position = -1;
//                    break;
//            }//End switch
//        }
//        System.out.println("Defense type " + (defenseTypeOptions) SmartDashboardChooser.defenseChooser.getSelected() + " at position " + position);
//        return position;
//    }//End method

   
    public static boolean createTestChoosers() {
        teleopModes mode = (teleopModes) choosers.modeChooser.getSelected();
        if (mode == teleopModes.TEST) {
            choosers.initTestMotorChooser();
            choosers.testInitChoosers();

            return true;
        }
        else return false;
    }
}