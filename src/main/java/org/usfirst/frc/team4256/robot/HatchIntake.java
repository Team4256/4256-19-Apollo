package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public final class HatchIntake {

    private final DoubleSolenoid hatchSolenoid;

    public HatchIntake(int forwardChannel, int reverseChannel) {
        hatchSolenoid = new DoubleSolenoid(forwardChannel, reverseChannel);
    }

    public void open() {
        hatchSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    public void close() {
        hatchSolenoid.set(DoubleSolenoid.Value.kForward);
    }
}