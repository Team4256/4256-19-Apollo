package org.usfirst.frc.team4256.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.cyborgcats.reusable.phoenix.Encoder;
import com.cyborgcats.reusable.phoenix.Talon;
import com.cyborgcats.reusable.phoenix.Victor;

public final class GroundIntake {
    private static final double MINIMUM_ANGLE = 0.0;//TODO TEST
    private static final double MAXIMUM_ANGLE = 85.0;//TODO TEST
    private static final double MINIMUM_ANGLE_THRESHOLD = 2.0;//TOSO TEST
    private static final double MAXIMUM_ANGLE_THRESHOLD = 2.0;//TODO TEST
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
        liftMotor.configClosedLoopPeakOutput(0, 0.3);//TODO TEST
        liftMotor.configContinuousCurrentLimit(40, Talon.TIMEOUT_MS);//TODO TEST
	    liftMotor.configPeakCurrentLimit(45, Talon.TIMEOUT_MS);//TODO TEST
        liftMotor.configPeakCurrentDuration(250, Talon.TIMEOUT_MS);//TODO TEST
        setDisabled();
        resetPosition();
        intakeMotor.init();
        intakeMotor.setInverted(isIntakeMotorFlipped);
    }

    public void setDisabled() {
        liftMotor.set(ControlMode.Disabled, 0.0);
    }

    public void resetPosition() {
        liftMotor.setSelectedSensorPosition(0, 0, Talon.TIMEOUT_MS);
    }

    private boolean validateRequestedAngle(double requestedAngle) {//In degrees
        return ((requestedAngle >= MINIMUM_ANGLE) && (requestedAngle <= MAXIMUM_ANGLE));
    }

    public boolean checkAngle() {
        if (getCurrentAngle() < MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD && desiredDegrees < MINIMUM_ANGLE + MINIMUM_ANGLE_THRESHOLD || getCurrentAngle() > MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD && desiredDegrees > MAXIMUM_ANGLE - MAXIMUM_ANGLE_THRESHOLD) { 
            setDisabled(); 
            return false;
        }else {
            return true;
        }
    }

    public void setAngle(double degrees) {
        if (validateRequestedAngle(degrees)) {
            desiredDegrees = degrees;
            if (checkAngle()) {
                liftMotor.setDegreesLifter(desiredDegrees);
            }
        }
    }

    public double getCurrentAngle() {
        return liftMotor.getCurrentAngle(false);
    }

    public double getDesiredDegrees() {
        return desiredDegrees;
    }

    public Talon getLiftMotor() {
        return liftMotor;
    }

    public Victor getIntakeMotor() {
        return intakeMotor;
    }

}
