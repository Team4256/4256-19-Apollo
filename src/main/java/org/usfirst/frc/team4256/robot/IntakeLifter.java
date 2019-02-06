package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Encoder;
import com.cyborgcats.reusable.phoenix.Talon;
import com.cyborgcats.reusable.phoenix.Victor;

import edu.wpi.first.wpilibj.DigitalInput;

public final class IntakeLifter {

    public enum IntakeLifterPosition {
        DOWN, CARGO_SHIP, ROCKET, UP 
    }

    private static final double gearRatio = 84/18;

    //Instance
    private final Talon master;//TODO rename
    private final Victor followerOne;//TODO rename
    private final Victor followerTwo;//TODO rename
    private final Victor followerThree;//TODO rename

    private final boolean followerOneFlipped;
    private final boolean followerTwoFlipped;
    private final boolean followerThreeFlipped;
    
    private IntakeLifterPosition currentIntakeLifterPosition; 

    public IntakeLifter(int masterID, int followerOneID, int followerTwoID, int followerThreeID, boolean masterFlippedSensor, boolean followerOneFlipped, boolean followerTwoFlipped, boolean followerThreeFlipped) {
        master = new Talon(masterID, gearRatio, ControlMode.Position, Encoder.CTRE_MAG_ABSOLUTE, masterFlippedSensor);
        followerOne = new Victor(followerOneID, ControlMode.Follower);
        followerTwo = new Victor(followerTwoID, ControlMode.Follower);
        followerThree = new Victor(followerThreeID, ControlMode.Follower);

        this.followerOneFlipped = followerOneFlipped;
        this.followerTwoFlipped = followerTwoFlipped;
        this.followerThreeFlipped = followerThreeFlipped;

        currentIntakeLifterPosition = IntakeLifterPosition.UP;
    }

    public void init() {
        master.init();
        followerOne.init(master);
        followerTwo.init(master);
        followerThree.init(master);
        followerOne.setInverted(followerOneFlipped);
        followerTwo.setInverted(followerTwoFlipped);
        followerThree.setInverted(followerThreeFlipped);
        resetPosition();
    }

    public void setPositon(IntakeLifterPosition position) {
        double value;
        switch (position) {
            case DOWN:
                value = 0.0;
            break;
            case CARGO_SHIP:
                value = 45.0;
            break;
            case ROCKET:
                value = 55.0;
            break;
            case UP:
                value = 90.0;
            break;
            default:
                value = 0.0;
        }
        master.quickSet(value, true);
    }

    public void resetPosition() {
        master.setSelectedSensorPosition((int)master.convert.from.DEGREES.afterGears(90), 0, Talon.TIMEOUT_MS);
    }
    
}