package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class Climber {
    
    private final DoubleSolenoid leftSolenoid;
    private final DoubleSolenoid rightSolenoid;
    private static Climber instance = null;
    private boolean isInitialized = false;
    
    /**
     * A pneumatic based climber with two (double) solenoids used to reach HAB level 2.
     */
    private Climber() {
        leftSolenoid = new DoubleSolenoid(Parameters.CLIMBER_SOLENOID_LEFT_FORWARD_CHANNEL, Parameters.CLIMBER_SOLENOID_LEFT_REVERSE_CHANNEL);
        rightSolenoid = new DoubleSolenoid(Parameters.CLIMBER_SOLENOID_RIGHT_FORWARD_CHANNEL, Parameters.CLIMBER_SOLENOID_RIGHT_REVERSE_CHANNEL);
    }

    public static Climber getInstance() {
        if (instance == null) {
            instance = new Climber();
        }

        return instance;
    }

    /**
     * Performs neccessary initialization of the <code>Climber</code> which is to be run when during <code>RobotInit</code>
     */
    public void init() {
        retractLeft();
        retractRight();
    }

    /**
     * @return
     * <b>True</b> if initialization has previously occured
     */
    public boolean isInitialized() {
        return isInitialized;
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

    /**
     * Outputs relevant information to the SmartDashboard.
     */
    public void outputToSmartDashboard() {
        SmartDashboard.putBoolean("Climber One Is Extended", isLeftExtended());
        SmartDashboard.putBoolean("Climber Two Is Extended", isRightExtended());
    }

}
