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

    public void extendLeft() {
        leftSolenoid.set(Value.kForward); 
    }

    public void retractLeft() {
        leftSolenoid.set(Value.kReverse);
    }

    public void extendRight() {
        rightSolenoid.set(Value.kForward); 
    }

    public void retractRight() {
        rightSolenoid.set(Value.kReverse);
    }

    public boolean isLeftExtended() {
        return leftSolenoid.get() == Value.kForward;
    }

    public boolean isRightExtended() {
        return rightSolenoid.get() == Value.kForward;
    }



}
