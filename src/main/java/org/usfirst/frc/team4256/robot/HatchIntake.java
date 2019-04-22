package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class HatchIntake {

    private final DoubleSolenoid hatchSolenoid;
    private static HatchIntake instance = null;

    /**
     * A pneumatic based intake used to latch and hold on to hatches as well as release them.
     */
    private HatchIntake() {
        hatchSolenoid = new DoubleSolenoid(Parameters.HATCH_SOLENOID_FORWARD_CHANNEL, Parameters.HATCH_SOLENOID_REVERSE_CHANNEL);
    }

    /**
     * @return
     * A static <code>HatchIntake</code> instance
     */
    public static HatchIntake getInstance() {
        if (instance == null) {
            instance = new HatchIntake();
        }

        return instance;
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
     * <b>True</b> if the <code>HatchIntake</code> is in latched position.
     * <p>
     * <b>False</b> if the <code>HatchIntake</code> is in released position 
     * (does not necessarily mean released, could potentially be set to kOff).
     */
    public boolean isLatched() {
        return hatchSolenoid.get() == Value.kForward;
    }

    /**
     * Outputs relevant information to the SmartDashboard.
     */
    public void outputToSmartDashboard() {
        SmartDashboard.putBoolean("HatchIntake Is Latched", isLatched());
    }

}