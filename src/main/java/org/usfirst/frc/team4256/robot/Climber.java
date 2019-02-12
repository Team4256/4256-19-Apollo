package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public final class Climber {
    
    private final DoubleSolenoid leftSolenoid;
    private final DoubleSolenoid rightSolenoid;
    
    public Climber(final int leftForwardChannel, final int leftReverseChannel, final int rightForwardChannel, final int rightReverseChannel) {
        leftSolenoid = new DoubleSolenoid(leftForwardChannel, leftReverseChannel);
        rightSolenoid = new DoubleSolenoid(rightForwardChannel, rightReverseChannel);
    }

    public void toggleLeft() {
        Value toggleTo = isLeftActuated() ? Value.kReverse : Value.kForward; 
    }

    public void toggleRight() {
        Value toggleTo = isRightActuated() ? Value.kReverse : Value.kForward;
    }

    public boolean isLeftActuated() {
        return leftSolenoid.get() == Value.kForward;
    }

    public boolean isRightActuated() {
        return rightSolenoid.get() == Value.kForward;
    }



}
