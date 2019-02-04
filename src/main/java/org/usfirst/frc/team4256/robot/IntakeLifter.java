package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Talon;

import edu.wpi.first.wpilibj.DigitalInput;

public final class IntakeLifter {

    public enum IntakeLifterPosition {
        DOWN, CARGO_SHIP, ROCKET, UP 
    }


    //Instance
    private final Talon masterOne;//TODO rename
    private final Talon followerOne;//TODO rename
    private final Talon followerTwo;//TODO rename
    private final Talon followerThree;//TODO rename
    
    private final DigitalInput limitSwitchUp;//TODO rename
    private final DigitalInput limitSwitchDown;//TODO rename
    
    private IntakeLifterPosition currentIntakeLifterPosition; 

    public IntakeLifter(int masterOneID, int followerOneID, int followerTwoID, int followerThreeID, int limitSwitchUpID, int limitSwitchDownID) {
        masterOne = new Talon(masterOneID, ControlMode.Position);
        followerOne = new Talon(followerOneID, ControlMode.Follower);
        followerTwo = new Talon(followerTwoID, ControlMode.Follower);
        followerThree = new Talon(followerThreeID, ControlMode.Follower);
        limitSwitchUp = new DigitalInput(limitSwitchUpID);
        limitSwitchDown= new DigitalInput(limitSwitchDownID);

        currentIntakeLifterPosition = IntakeLifterPosition.UP;
    }
    
}