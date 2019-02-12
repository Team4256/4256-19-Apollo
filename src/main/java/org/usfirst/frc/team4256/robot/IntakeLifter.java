package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Encoder;
import com.cyborgcats.reusable.phoenix.Talon;
import com.cyborgcats.reusable.phoenix.Victor;

import edu.wpi.first.wpilibj.DigitalInput;

public final class IntakeLifter {

    private static final double GEAR_RATIO = 84.0/18.0;  //shaft gear teeth / motor gear teeth
    //ANGLE INCREASES STARTING ON TOP OF ROBOT
    private static final double MINIMUM_ANGLE = 0.0;
    private static final double MAXIMUM_ANGLE = 170.0;
    private static final double MINIMUM_ANGLE_THRESHOLD = 2.0;
    private static final double MAXIMUM_ANGLE_THRESHOLD = 2.0;

    //Instance
    private final Talon master;
    private final Victor followerOne;
    private final Talon followerTwo;
    private final Victor followerThree;
    private final DigitalInput limitSwitch;

    private final boolean followerOneFlippedMotor;
    private final boolean followerTwoFlippedMotor;
    private final boolean followerThreeFlippedMotor;

    private boolean isClimbMode;
    private boolean wasLimitSwitchPressed;
    private double desiredDegrees = 0.0;
    private int previousEncoderCount = 0;

    public IntakeLifter(int masterID, int followerOneID, int followerTwoID, int followerThreeID, boolean masterFlippedSensor, boolean followerOneFlippedMotor, boolean followerTwoFlippedSensor, boolean followerTwoFlippedMotor, boolean followerThreeFlippedMotor, int limitSwitchID) {
        master = new Talon(masterID, GEAR_RATIO, ControlMode.Position, Encoder.CTRE_MAG_ABSOLUTE, masterFlippedSensor);
        followerOne = new Victor(followerOneID, ControlMode.Follower);
        followerTwo = new Talon(followerTwoID, GEAR_RATIO, ControlMode.Follower, Encoder.CTRE_MAG_ABSOLUTE, followerTwoFlippedSensor);
        followerThree = new Victor(followerThreeID, ControlMode.Follower);
        limitSwitch = new DigitalInput(limitSwitchID);

        this.followerOneFlippedMotor = followerOneFlippedMotor;
        this.followerTwoFlippedMotor = followerTwoFlippedMotor;
        this.followerThreeFlippedMotor = followerThreeFlippedMotor;
        isClimbMode = false;
        wasLimitSwitchPressed = false;
    }

    public void init() {
        master.init();
        followerOne.init(master);
        followerTwo.init(master);
        followerThree.init(master);
        followerOne.setInverted(followerOneFlippedMotor);
        followerTwo.setInverted(followerTwoFlippedMotor);
        followerThree.setInverted(followerThreeFlippedMotor);
        master.config_kP(0, 0.25);
        master.config_kI(0, 0.0);
        master.config_kD(0, 10.0);
        master.configClosedLoopPeakOutput(0, 0.3);
        master.configContinuousCurrentLimit(40, Talon.TIMEOUT_MS);
	    master.configPeakCurrentLimit(45, Talon.TIMEOUT_MS);
        master.configPeakCurrentDuration(250, Talon.TIMEOUT_MS);
        setDisabled();
        resetPosition();
        wasLimitSwitchPressed = getLimitSwitch();
    }

    public void calibratePosition() {
        if (getLimitSwitch()) {
            resetPosition();
        }
    }

    /**
     * <p><h3>Checks the value of the limit switch from the last time the function was called,
     * if it was previously false and is now true, the encoder and position will be reset.</h3></p>
     */
    public void checkLimitSwitchUpdate() {
        boolean isLimitSwitchPressed = getLimitSwitch();
        if (isLimitSwitchPressed && !wasLimitSwitchPressed) {
            resetPosition();
            setAngle(MINIMUM_ANGLE);//Technically sets to minimum angle not the position it was reset to
        }
        wasLimitSwitchPressed = isLimitSwitchPressed;
    }

    /**
     * <p>Resets the encoder on the <b>Master Talon</b> back to zero</p>
     * <p><b>Zero</b> is intended to be for when the <b>intakeLifter</b>
     * is in the up position</p> 
     */
    private void resetPosition() {
        master.setSelectedSensorPosition(0, 0, Talon.TIMEOUT_MS);
    }

    /**
     * @return 
     * <p><code>True</code> if both <code>getCurrentAngle</code> and <code>desiredDegrees</code> are
     * within witin a threshold of being at either the top or the bottom.</p>
     */
    public boolean checkAngle() {
        if (!isClimbMode) {
            if (getCurrentAngle() < MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD && desiredDegrees < MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD || 
            getCurrentAngle() > MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD && desiredDegrees > MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD) { 
            setDisabled(); 
                return false;
            }else {
                return true;
            }
        }else {
            return true;
        }
    }
    
    /**
     * <p>Puts the <code>Master Talon</code> in the <code>disabled</code> <code>ControlMode</code></p> 
     */
    public void setDisabled() {
        master.set(ControlMode.Disabled, 0);
    }

    /**
     * 
     * @return The <code>intakeLifter</code>'s <code>currentAngle</code>
     */
    public double getCurrentAngle() {
        return master.getCurrentAngle(false);
    }

    /**
     * 
     * @param requestedAngle
     * @return 
     */
    private boolean validateRequestedAngle(double requestedAngle) {//In degrees
        return ((requestedAngle >= MINIMUM_ANGLE) && (requestedAngle <= MAXIMUM_ANGLE));
    }

    /**
     * 
     * @param degrees <p>The desired angle in degrees for the <code>intakeLifter</code></p>
     * <p><b>NOTICE:</b> The angle is restricted by the variables
     * {@link #MINIMUM_ANGLE} and {@link #MAXIMUM_ANGLE} and is 
     * validated by the {@link #validateRequestedAngle(double)} to insure
     * the requested anlge fits between these angles.</p>
     */
    public void setAngle(double degrees) {
        if (!isClimbMode) {
            if (validateRequestedAngle(degrees)) {
                desiredDegrees = degrees;
                if (checkAngle()) {
                    master.setDegreesLifter(desiredDegrees);
                }
            }
        }
    }
    
    private void relativeChange(double deltaDegrees) {
        if (validateRequestedAngle(desiredDegrees + deltaDegrees)) {
            setAngle(desiredDegrees + deltaDegrees);
        }
    }
    
    public void increment(double incrementDegrees) {
        relativeChange(Math.abs(incrementDegrees));
    }

    public void decrement(double decrementDegrees) {
        relativeChange(-1.0*Math.abs(decrementDegrees));
    }

    /**
     * 
     * @param enableClimbMode 
     * <p><code>True</code> enables <b>Climb Mode</b></p>
     * <p>and</p>
     * <p><code>False</code> disables <b>Climb Mode</b></p>
     */
    public void enableClimbMode() {
        isClimbMode = true;
    }

    /**
     * @param percent <p>Value from -1 to 1</p>
     */
    public void climb(double percent) {
        if (isClimbMode) {//EXTRA SAFETY NET
            master.set(ControlMode.PercentOutput, percent);
        }
    }

    /**
     * 
     * @return
     * <p><code>True</code> if the <code>limitSwitch</code> is pressed down</p>
     * <p>and</p>
     * <p><code>False</code> if the <code>limitSwitch</code> is not pressed down</p> 
     */
    public boolean getLimitSwitch() {
        return !limitSwitch.get();
    }

    //ACCESSOR METHODS FOR INSTANCE VARIABLES
    public Talon getMaster() {
        return master;
    }

    /**
     * <p><b>Accessor Method</b></p>
     * @return <code>desiredDegrees</code>
     */
    public double getDesiredDegrees() {
        return desiredDegrees;
    }

    /**
     * <p><b>Accessor Method</b></p>
     * @return <code>isClimbMode</code>
     */
    public boolean isClimbMode() {
        return isClimbMode; 
    }

    /**
     * <p><h3>Checks for encoder value spikes based off the difference in encoder count between loops
     * and sets the encoder value to the previous encoder count if a spike is detected.</h3></p>
     */
    public void checkForEncoderSpike() {
        if (Math.abs(master.getSelectedSensorPosition(0) - previousEncoderCount) > 2000) {
            master.setSelectedSensorPosition(previousEncoderCount,0,Talon.TIMEOUT_MS);
        }
        previousEncoderCount = master.getSelectedSensorPosition(0);
    }

}