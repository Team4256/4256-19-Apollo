package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Encoder;
import com.cyborgcats.reusable.phoenix.Talon;
import com.cyborgcats.reusable.phoenix.Victor;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class GroundIntake {
    private static final double MINIMUM_ANGLE = 0.0;
    private static final double TRANSFER_ANGLE = 10.0;
    private static final double MAXIMUM_ANGLE = 105.0;
    private static final double MINIMUM_ANGLE_THRESHOLD = 0.5;
    private static final double MAXIMUM_ANGLE_THRESHOLD = 15.0;
    private static final double SLURP_SPEED = 1.0;
    private static final double SPIT_SPEED = -0.5;
    private static final double STOP_SPEED = 0.0;
    private final Talon liftMotor;
    private final Victor intakeMotor;
    private final boolean isLiftMotorFlipped;
    private final boolean isIntakeMotorFlipped;
    private final DigitalInput limitSwitch;
    private double desiredDegrees;
    private boolean wasLimitSwitchPressed = false;
    private boolean isOverride = false;

    public GroundIntake(final int liftMotorID, final double gearRatio, final boolean isLiftSensorFlipped, final boolean isLiftMotorFlipped, final int intakeMotorID, final boolean isIntakeMotorFlipped, int limitSwitchID) {
        liftMotor = new Talon(liftMotorID, gearRatio, ControlMode.Position, Encoder.CTRE_MAG_ABSOLUTE, isLiftSensorFlipped);
        intakeMotor = new Victor(intakeMotorID, ControlMode.PercentOutput);
        limitSwitch = new DigitalInput(limitSwitchID);
        this.isLiftMotorFlipped = isLiftMotorFlipped;
        this.isIntakeMotorFlipped = isIntakeMotorFlipped;
    }

    public void init() {
        liftMotor.init();
        liftMotor.setInverted(isLiftMotorFlipped);
        liftMotor.config_kP(0, 1.5);
        liftMotor.config_kI(0, 0.0);
        liftMotor.config_kD(0, 0.0);
        liftMotor.configClosedLoopPeakOutput(0, 0.3);
        liftMotor.configContinuousCurrentLimit(40, Talon.TIMEOUT_MS);
	    liftMotor.configPeakCurrentLimit(45, Talon.TIMEOUT_MS);
        liftMotor.configPeakCurrentDuration(250, Talon.TIMEOUT_MS);
        setDisabled();
        resetPosition();
        intakeMotor.init();
        intakeMotor.setInverted(isIntakeMotorFlipped);
    }

    /**
     * <p><h3>Checks the value of the limit switch from the last time the function was called,
     * if it was previously false and is now true, the encoder and position will be reset.</h3></p>
     */
    public void checkLimitSwitchUpdate() {
        boolean isLimitSwitchPressed = isLimitSwitchOn();
        if (isLimitSwitchPressed) {
            resetPosition();
            if (!wasLimitSwitchPressed) {
                setAngle(MINIMUM_ANGLE);//Technically sets to minimum angle not the position it was reset to
            }   
        }
        wasLimitSwitchPressed = isLimitSwitchPressed;
    }

    /**
     * Disables the <code>liftMotor</code> temporarily to keep PID at bay.
     */
    public void setDisabled() {
        isOverride = false;
        liftMotor.set(ControlMode.Disabled, 0.0);
    }

    /**
     * Sets the <code>liftMotor</code>'s encoder position to zero.
     */
    public void resetPosition() {
        liftMotor.setSelectedSensorPosition(0, 0, Talon.TIMEOUT_MS);
    }

    /**
     * Ensures the <code>requestedAngle</code> is within predefined bounds set by the constants {@link #MINIMUM_ANGLE} and {@link #MAXIMUM_ANGLE}.
     */
    private boolean validateRequestedAngle(double requestedAngle) {//In degrees
        return ((requestedAngle >= MINIMUM_ANGLE) && (requestedAngle <= MAXIMUM_ANGLE));
    }
    
    /**
     * Checks the the {@link #MINIMUM_ANGLE}, {@link #MAXIMUM_ANGLE_THRESHOLD}, {@link #MINIMUM_ANGLE}, {@link #MINIMUM_ANGLE_THRESHOLD}, {@link #desiredDegrees}, and {@link #getCurrentAngle()} with one another to monitor if the <code>liftMotor</code> should be disabled.
     */
    public boolean checkAngle() {
        if (isOverride) {
            return true;
        }
        if (((getCurrentAngle() < (MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD)) && 
             (desiredDegrees < (MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD))) ||
             ((getCurrentAngle() > (MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD)) &&
             (desiredDegrees > (MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD)))) { 
            setDisabled(); 
            return false;
        }else {
            return true;
        }
    }

    /**
     * Sets the angle of the <code>liftMotor</code> after running {@link #validateRequestedAngle(double)} and {@link #checkAngle()}.
     * @param degrees
     * inteded angle in degrees for the <code>liftMotor</code> to be set to.
     */
    public void setAngle(double degrees) {
        isOverride = false;
        if (validateRequestedAngle(degrees)) {
            desiredDegrees = degrees;
            if (checkAngle()) {
                liftMotor.setDegreesLifter(desiredDegrees);
            }
        }
    }

    public void setOverrideUp() {
        if (!isLimitSwitchOn()) {
            isOverride = true;
            liftMotor.set(ControlMode.PercentOutput, -0.3);
        } else {
            isOverride = false;
            setDisabled();
        }        
    }

    /**
     * @return
     * Current angle in degrees of the <code>liftMotor</code>.
     */
    public double getCurrentAngle() {
        return liftMotor.getCurrentAngle(false);
    }

    public boolean isLimitSwitchOn() {
        return !limitSwitch.get();
    }

    public boolean isDisabled() {
        return (liftMotor.getControlMode() == ControlMode.Disabled);
    }
    
    public void transferHatch(HatchIntake hatchIntake, IntakeLifter intakeLifter) {
        if (!isOverride && !isLimitSwitchOn()) {
            setAngle(TRANSFER_ANGLE);
        }
        if ((Math.abs(getCurrentAngle() - TRANSFER_ANGLE) < 2.5) && (intakeLifter.getDesiredDegrees() <= 14.0)) {
            hatchIntake.latch();
            spit();
            intakeLifter.setAngle(15.0);
        } else if ((Math.abs(getCurrentAngle() - TRANSFER_ANGLE) < 2.5) && (Math.abs(intakeLifter.getCurrentAngle() - 15.0) < 2.5) && (intakeLifter.getDesiredDegrees() > 14.0)) {
            stop();
            setOverrideUp();
        } else if (isLimitSwitchOn()) {
            intakeLifter.setAngle(0.0);
        }
    }

    /**
     * @return
     * Desired angle in degrees of the <code>liftMotor</code>.
     */
    public double getDesiredDegrees() {
        return desiredDegrees;
    }

    public Talon getLiftMotor() {
        return liftMotor;
    }

    public void slurp() {
        intakeMotor.quickSet(SLURP_SPEED);
    }

    public void spit() {
        intakeMotor.quickSet(SPIT_SPEED);
    }

    public void stop() {
        intakeMotor.quickSet(STOP_SPEED);
    }

    public Victor getIntakeMotor() {
        return intakeMotor;
    }

    public void outputToSmartDashboard() {
        SmartDashboard.putBoolean("GroundIntake On Limit Switch", isLimitSwitchOn());
        SmartDashboard.putBoolean("GroundIntake Is Disabled", isDisabled());
        SmartDashboard.putBoolean("GroundIntake Is Override", isOverride);
        SmartDashboard.putNumber("GroundIntake Desired Degrees", desiredDegrees);
    }

}
