package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Encoder;
import com.cyborgcats.reusable.phoenix.Talon;

import edu.wpi.first.wpilibj.DigitalInput;

public final class IntakeLifter {

    public enum IntakeLifterPosition {
        DOWN, CARGO_SHIP, ROCKET, UP 
    }


    private static final double maxPercent = 1.0;
    private static final double gearRatio = 84/18;

    //Instance
    private final Talon master;//TODO rename
    private final Talon followerOne;//TODO rename
    private final Talon followerTwo;//TODO rename
    private final Talon followerThree;//TODO rename
    
    private final DigitalInput limitSwitchUp;//TODO rename
    private final DigitalInput limitSwitchDown;//TODO rename

    private final int masterID;
    
    private IntakeLifterPosition currentIntakeLifterPosition; 

    public IntakeLifter(int masterID, int followerOneID, int followerTwoID, int followerThreeID, int limitSwitchUpID, int limitSwitchDownID, boolean masterFlippedSensor) {
        master = new Talon(masterID, gearRatio, ControlMode.Position, Encoder.CTRE_MAG_ABSOLUTE, masterFlippedSensor);
        followerOne = new Talon(followerOneID, ControlMode.Follower);
        followerTwo = new Talon(followerTwoID, ControlMode.Follower);
        followerThree = new Talon(followerThreeID, ControlMode.Follower);
        limitSwitchUp = new DigitalInput(limitSwitchUpID);
        limitSwitchDown= new DigitalInput(limitSwitchDownID);

        this.masterID = masterID;

        currentIntakeLifterPosition = IntakeLifterPosition.UP;
    }

    public void init() {
        master.init();
        followerOne.init(masterID, maxPercent);
        followerTwo.init(masterID, maxPercent);
        followerThree.init(masterID, maxPercent);
    }
    
}