package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Encoder;
import com.cyborgcats.reusable.phoenix.Talon;
import com.cyborgcats.reusable.phoenix.Victor;

public final class GroundIntake {
    private static final double MINIMUM_ANGLE = 0.0;//TODO TEST
    private static final double MAXIMUM_ANGLE = 85.0;//TODO TEST
    private static final double MINIMUM_ANGLE_THRESHOLD = 2.0;//TODO TEST
    private static final double MAXIMUM_ANGLE_THRESHOLD = 2.0;//TODO TEST
    private static final double SLURP_SPEED = -0.5;
    private static final double SPIT_SPEED = 0.5;
    private static final double STOP_SPEED = 0.0;
    private final Talon liftMotor;
    private final Victor intakeMotor;
    private final boolean isLiftMotorFlipped;
    private final boolean isIntakeMotorFlipped;
    private double desiredDegrees;

    public GroundIntake(final int liftMotorID, final double gearRatio, final boolean isLiftSensorFlipped, final boolean isLiftMotorFlipped, final int intakeMotorID, final boolean isIntakeMotorFlipped) {
        liftMotor = new Talon(liftMotorID, gearRatio, ControlMode.Position, Encoder.CTRE_MAG_ABSOLUTE, isLiftSensorFlipped);
        intakeMotor = new Victor(intakeMotorID, ControlMode.PercentOutput);
        this.isLiftMotorFlipped = isLiftMotorFlipped;
        this.isIntakeMotorFlipped = isIntakeMotorFlipped;
    }

    public void init() {
        liftMotor.init();
        liftMotor.setInverted(isLiftMotorFlipped);
        liftMotor.config_kP(0, 0.01);//TODO TEST
        liftMotor.config_kI(0, 0.0);
        liftMotor.config_kD(0, 0.0);//TODO TEST
        liftMotor.configClosedLoopPeakOutput(0, 0.3);//TODO TEST
        liftMotor.configContinuousCurrentLimit(40, Talon.TIMEOUT_MS);//TODO TEST
	    liftMotor.configPeakCurrentLimit(45, Talon.TIMEOUT_MS);//TODO TEST
        liftMotor.configPeakCurrentDuration(250, Talon.TIMEOUT_MS);//TODO TEST
        setDisabled();
        resetPosition();
        intakeMotor.init();
        intakeMotor.setInverted(isIntakeMotorFlipped);
    }

    /**
     * Disables the <code>liftMotor</code> temporarily to keep PID at bay.
     */
    public void setDisabled() {
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
        if (((getCurrentAngle() < (MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD)) && (desiredDegrees < (MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD))) || ((getCurrentAngle() > (MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD)) && (desiredDegrees > (MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD)))) { 
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
        if (validateRequestedAngle(degrees)) {
            desiredDegrees = degrees;
            if (checkAngle()) {
                liftMotor.setDegreesLifter(desiredDegrees);
            }
        }
    }

    /**
     * @return
     * Current angle in degrees of the <code>liftMotor</code>.
     */
    public double getCurrentAngle() {
        return liftMotor.getCurrentAngle(false);
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

}