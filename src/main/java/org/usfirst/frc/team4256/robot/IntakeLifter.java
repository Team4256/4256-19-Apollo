package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Encoder;
import com.cyborgcats.reusable.phoenix.Talon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class IntakeLifter {

    public static final double INCREMENT = 5.0;
    public static final double DECREMENT = 5.0;
    
    public static final double POSITION_UP = 0.0;
    public static final double POSITION_CARGOSHIP = 25.0;
    public static final double POSITION_ROCKETSHIP = 108.0;
    public static final double POSITION_DOWN = 170.0;

    private static final double GEAR_RATIO = 84.0/18.0;  //shaft gear teeth / motor gear teeth
    //ANGLE INCREASES STARTING ON TOP OF ROBOT
    private static final double MINIMUM_ANGLE = 0.0;
    private static final double MAXIMUM_ANGLE = 170.0;
    private static final double MINIMUM_ANGLE_THRESHOLD = 3.0;
    private static final double MAXIMUM_ANGLE_THRESHOLD = 2.0;

    private static IntakeLifter instance = null;

    //Instance
    private final Talon master;
    private final Talon followerThree;
    private final DigitalInput limitSwitch;

    private final boolean isMasterMotorFlipped;
    private final boolean isFollowerThreeMotorFlipped;

    private boolean wasLimitSwitchPressed = false;
    private double desiredDegrees = 0.0;
    private int previousEncoderCount = 0;
    private int numberOfEncoderSpikes = 0;
    private boolean isInitialized = false;

    private IntakeLifter() {
        master = new Talon(Parameters.LIFTER_MASTER_ID, GEAR_RATIO, ControlMode.Position, Encoder.CTRE_MAG_ABSOLUTE, Parameters.IS_LIFTER_MASTER_SENSOR_FLIPPED);
        followerThree = new Talon(Parameters.LIFTER_FOLLOWER_3_ID, GEAR_RATIO, ControlMode.Follower, Encoder.CTRE_MAG_ABSOLUTE, Parameters.IS_LIFTER_FOLLOWER_3_SENSOR_FLIPPED);
        limitSwitch = new DigitalInput(Parameters.LIMIT_SWTICH_LIFTER);

        isMasterMotorFlipped = Parameters.IS_LIFTER_MASTER_MOTOR_FLIPPED;
        isFollowerThreeMotorFlipped = Parameters.IS_LIFTER_FOLLOWER_3_MOTOR_FLIPPED;
    }

    public synchronized static IntakeLifter getInstance() {
        if (instance == null) {
            instance = new IntakeLifter();
        }
        
        return instance;
    }

    public synchronized void init() {
        master.init();
        master.setInverted(isMasterMotorFlipped);
        followerThree.init(master);
        followerThree.follow(master);//needs this and don't know why
        followerThree.setInverted(isFollowerThreeMotorFlipped);
        master.config_kP(0, 1.2);
        master.config_kI(0, 0.0);
        master.config_kD(0, 7.0);
        master.configClosedLoopPeakOutput(0, 0.5);
        master.configContinuousCurrentLimit(40, Talon.TIMEOUT_MS);
	    master.configPeakCurrentLimit(45, Talon.TIMEOUT_MS);
        master.configPeakCurrentDuration(250, Talon.TIMEOUT_MS);
        setDisabled();
        resetPosition();
        wasLimitSwitchPressed = isLimitSwitch();
        isInitialized = true;
    }

    public synchronized boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Checks for encoder value spikes based off the difference in encoder count between loops
     * and sets the encoder value to the previous encoder count if a spike is detected.
     */
    public synchronized void checkForEncoderSpike() {
        if (Math.abs(master.getSelectedSensorPosition(0) - previousEncoderCount) > 2000) {
            master.setSelectedSensorPosition(previousEncoderCount, 0, Talon.TIMEOUT_MS);
            numberOfEncoderSpikes++;
        }
        previousEncoderCount = master.getSelectedSensorPosition(0);
    }

    /**
     * @return Number of times {@link #checkForEncoderSpike()} catches an encoder spike.
     */
    public synchronized int getNumberOfEncoderSpikes() {
        return numberOfEncoderSpikes;
    }

    /**
     * Checks the value of the limit switch from the last time the function was called,
     * if it was previously false and is now true, the encoder and position will be reset.
     */
    public synchronized void checkLimitSwitchUpdate() {
        boolean isLimitSwitchPressed = isLimitSwitch();
        if (isLimitSwitchPressed) {
            resetPosition();
            if (!wasLimitSwitchPressed) {
                setAngle(MINIMUM_ANGLE);//Technically sets to minimum angle not the position it was reset to
            }   
        }
        wasLimitSwitchPressed = isLimitSwitchPressed;
    }


    /**
     * <p>Resets the encoder on the <b>Master Talon</b> back to zero</p>
     * <p><b>Zero</b> is intended to be for when the <b>intakeLifter</b>
     * is in the up position</p> 
     */
    private synchronized void resetPosition() {
        master.setSelectedSensorPosition(0, 0, Talon.TIMEOUT_MS);
        followerThree.setSelectedSensorPosition(0, 0, Talon.TIMEOUT_MS);
    }

    /**
     * 
     * @return the difference between the two encoders on the lifter in degrees. (Absolute Value).
     */
    public synchronized double getEncoderDifferenceDegrees() {
        return Math.abs(Math.abs(master.getCurrentAngle(false) - Math.abs(followerThree.getCurrentAngle(false))));
    }

    /**
     * 
     * @return the difference between the two encoders on the lifter in encoder counts. (Absolute Value).
     */
    public synchronized int getEncoderDifferenceCounts() {
        return Math.abs(Math.abs(master.getSelectedSensorPosition(0)) - Math.abs(followerThree.getSelectedSensorPosition(0)));
    }

    /**
     * @return 
     * <p><code>True</code> if both <code>getCurrentAngle</code> and <code>desiredDegrees</code> are
     * within witin a threshold of being at either the top or the bottom.</p>
     */
    public synchronized boolean checkAngle() {
        if (((getCurrentAngle() < MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD) &&
            (desiredDegrees < MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD)) ||
            ((getCurrentAngle() > MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD) && 
            (desiredDegrees > MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD))) {
            setDisabled(); 
            return false;
        }else {
            return true;
        }
    }
    
    /**
     * <p>Puts the <code>Master Talon</code> in the <code>disabled</code> <code>ControlMode</code></p> 
     */
    public synchronized void setDisabled() {
        master.set(ControlMode.Disabled, 0.0);
    }

    /**
     * @return The <code>intakeLifter</code>'s <code>currentAngle</code>
     */
    public synchronized double getCurrentAngle() {
        return master.getCurrentAngle(false);
    }

     /**
     * Ensures the <code>requestedAngle</code> is within predefined bounds set by the constants {@link #MINIMUM_ANGLE} and {@link #MAXIMUM_ANGLE}.
     * @return
     * <p><code>True</code> if the <code>requestedAngle</code> is within the predefined bounds.</p>
     * <p>and</p>
     * <p><code>False</code> if the <code>requestedAngle</code> is not within the predefined bounds.</p>
     */
    private synchronized boolean validateRequestedAngle(double requestedAngle) {//In degrees
        return ((requestedAngle >= MINIMUM_ANGLE) && (requestedAngle <= MAXIMUM_ANGLE));
    }

    /**
     * Sets the angle of the lifter.
     * @param degrees <p>The desired angle in degrees for the <code>intakeLifter</code></p>
     * <p><b>NOTICE:</b> The angle is restricted by the variables
     * {@link #MINIMUM_ANGLE} and {@link #MAXIMUM_ANGLE} and is 
     * validated by the {@link #validateRequestedAngle(double)} to insure
     * the requested anlge fits between these angles.</p>
     */
    public synchronized void setAngle(double degrees) {
        if (validateRequestedAngle(degrees)) {
            desiredDegrees = degrees;
            if (checkAngle()) {
                master.setDegreesLifter(desiredDegrees);
            }
        }
    }

    public synchronized void setEncoderToMaxAngle() {
        master.setSelectedSensorPosition(((int)((MAXIMUM_ANGLE / 360.0)*GEAR_RATIO)*Encoder.CTRE_MAG_ABSOLUTE.countsPerRev()));
        master.setDegreesLifter(getCurrentAngle());
    }
    
    /**
     * Increments/Decrements (based off whether the <code>deltaDegrees</code> is positive or negative) the <code>desiredDegrees</code> of the <b>IntakeLifter</b>
     * @param deltaDegrees increment/decrement (positive/negative) amount in degrees.
     */
    private synchronized void relativeChange(double deltaDegrees) {
        if (validateRequestedAngle(desiredDegrees + deltaDegrees)) {
            setAngle(desiredDegrees + deltaDegrees);
        }
    }
    
    /**
     * Increments <code>desiredDegrees</code> by the <code>incrementDegrees</code> ammount.
     * @param incrementDegrees absolute value of degrees to increment by.
     */
    public synchronized void increment(double incrementDegrees) {
        relativeChange(Math.abs(incrementDegrees));
    }

    /**
     * Decrements <code>desiredDegrees</code> by the <code>decrementDegrees</code> ammount.
     * @param decrementDegrees absolute value of degrees to decrement by.
     */
    public synchronized void decrement(double decrementDegrees) {
        relativeChange(-1.0*Math.abs(decrementDegrees));
    }

    /**
     * @return
     * <p><code>True</code> if the <code>limitSwitch</code> is activated</p>
     * <p>and</p>
     * <p><code>False</code> if the <code>limitSwitch</code> is not activated</p> 
     */
    public synchronized boolean isLimitSwitch() {
        return !limitSwitch.get();
    }

    public synchronized boolean isDisabled() {
        return (master.getControlMode() == ControlMode.Disabled);
    }

    //ACCESSOR METHODS FOR INSTANCE VARIABLES
    public synchronized Talon getMaster() {
        return master;
    }

    public synchronized Talon getFollowerThree() {
        return followerThree;
    }

    /**
     * <p><b>Accessor Method</b></p>
     * @return <code>desiredDegrees</code>
     */
    public synchronized double getDesiredDegrees() {
        return desiredDegrees;
    }

    /**
     * Outputs relevant information to the SmartDashboard.
     */
    public synchronized void outputToSmartDashboard() {
        SmartDashboard.putBoolean("IntakeLifter Is LimitSwitch On", isLimitSwitch());
        SmartDashboard.putBoolean("IntakeLifter Is Disabled", isDisabled());
        SmartDashboard.putNumber("IntakeLifter Desired Degrees", desiredDegrees);
        SmartDashboard.putNumber("IntakeLifter Current Degrees", getCurrentAngle());
        SmartDashboard.putNumber("IntakeLifter Delta Encoder Degrees", getEncoderDifferenceDegrees());
        SmartDashboard.putNumber("IntakeLifter Current Counts", master.getSelectedSensorPosition());
    }

}