package com.cyborgcats.reusable.spark;

import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Add your docs here.
 */
public class SparkMaxNeoPID extends SparkMaxNeo {

    private CANPIDController pidController;
    private double minOutput, maxOutput; 

    public SparkMaxNeoPID(final int deviceID, final IdleMode defaultIdleMode, final boolean isInverted, double minOutput, double maxOutput) {
        super(deviceID, defaultIdleMode, isInverted);
        pidController = getPIDController();
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
    }

    public SparkMaxNeoPID(final int deviceID, final IdleMode defaultIdleMode, final boolean isInverted) {
        this(deviceID, defaultIdleMode, isInverted, 0.0, 1.0);
    }

    public void init() {
        super.init();
        pidController.setOutputRange(minOutput, maxOutput);
    }

    public void setP(double kP) {
        if (pidController.setP(kP) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " failed to set a P value.", false);
            return;
        }
    }

    public void setI(double kI) {
        if (pidController.setP(kI) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " failed to set an I value.", false);
            return;
        }
    }

    public void setD(double kD) {
        if (pidController.setP(kD) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " failed to set a D value.", false);
            return;
        }
    }

    public void setPID(double kP, double kI, double kD) {
        setP(kP);
        setI(kI);
        setD(kD);
    }

    public void rotate(double rotations) {
        pidController.setReference(rotations, ControlType.kPosition);
    }

}
