package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public final class Climber {
    
    private final DoubleSolenoid leftSolenoid;
    private final DoubleSolenoid rightSolenoid;
    
    /**
     * 
     * @param leftForwardChannel <p>The <code>leftSolenoids</code>'s forward channel number on the PCM (0..7).</p>
     * @param leftReverseChannel <p>The <code>leftSolenoid</code>'s reverse channel number on the PCM (0..7).</p>
     * @param rightForwardChannel <p>The <code>rightSolenoids</code>'s forward channel number on the PCM (0..7).</p>
     * @param rightReverseChannel <p>The <code>rightSolenoid</code>'s reverse channel number on the PCM (0..7).</p>
     */
    public Climber(final int leftForwardChannel, final int leftReverseChannel, final int rightForwardChannel, final int rightReverseChannel) {
        leftSolenoid = new DoubleSolenoid(leftForwardChannel, leftReverseChannel);
        rightSolenoid = new DoubleSolenoid(rightForwardChannel, rightReverseChannel);
    }

    /**
     * <p>Extends the <code>leftSolenoid</code> if the <code>rightSolenoid</code> is not currently extended.</p>
     * @see #isLeftExtended()
     */
    public void extendLeft() {
        if(!isRightExtended()) {
            leftSolenoid.set(Value.kForward); 
        }        
    }

    /**
     * <p>Retracts the <code>leftSolenoid</code>.</p>
     */
    public void retractLeft() {
        leftSolenoid.set(Value.kReverse);
    }

    /**
     * <p>Extends the <code>rightSolenoid</code> if the <code>leftSolenoid</code> is not currently extended.</p>
     * @see #isRightExtended()
     */
    public void extendRight() {
        if(!isLeftExtended()) {
            rightSolenoid.set(Value.kForward);
        }
    }

     /**
     * <p>Retracts the <code>rightSolenoid</code>.<p>
     */
    public void retractRight() {
        rightSolenoid.set(Value.kReverse);
    }

    /**
     * <p>Whether the left cylinder is extended.</p>
     * @return
     * <p><b>True</b> if the left cylinder is extended</p>
     * <p><b>False</b> if the left cylinder is retracted</p>
     */
    public boolean isLeftExtended() {
        return leftSolenoid.get() == Value.kForward;
    }

    /**
     * <p>Whether the right cylinder is extended.</p>
     * @return
     * <p><b>True</b> if the right cylinder is extended</p>
     * <p><b>False</b> if the right cylinder is retracted</p>
     */
    public boolean isRightExtended() {
        return rightSolenoid.get() == Value.kForward;
    }



}
