package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public final class HatchIntake {

    private final DoubleSolenoid hatchSolenoid;

    /**
     * A pneumatic based intake used to "grab" and hold on to hatches.
     * <p>
     * <code>forwardChannel</code> and <code>reverseChannel</code> correlate to the channels
     * on the <code>DoubleSolenoid</code>.
     */
    public HatchIntake(int forwardChannel, int reverseChannel) {
        hatchSolenoid = new DoubleSolenoid(forwardChannel, reverseChannel);
    }


    /**
     * <b>Used to release a hatch.</b>
     */
    public void release() {
        hatchSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    /**
     * <b>Used to latch onto a hatch.</b>
     */
    public void latch() {
        hatchSolenoid.set(DoubleSolenoid.Value.kForward);
    }

    /**
     * @return
     * <b>True</b> if the <code>HatchIntake</code> is open.
     * <p>
     * <b>False</b> if the <code>HatchIntake</code> is not open 
     * (does not necessarily mean closed, could potentially be set to kOff).
     */
    public boolean isOpen() {
        return hatchSolenoid.get() == Value.kReverse;
    }

}