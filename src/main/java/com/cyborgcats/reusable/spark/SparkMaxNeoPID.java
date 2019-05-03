package com.cyborgcats.reusable.spark;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Add your docs here.
 */
public class SparkMaxNeoPID extends SparkMaxNeo {

    private static final int NEO_COUNTS_PER_REV = 42; 
    private final CANPIDController pidController;
    private final CANEncoder encoder;
    private final double gearRatio;
    private double minOutput, maxOutput; 

    public SparkMaxNeoPID(final int deviceID, final IdleMode defaultIdleMode, final boolean isInverted, final double gearRatio, final double minOutput, final double maxOutput) {
        super(deviceID, defaultIdleMode, isInverted);
        pidController = getPIDController();
        encoder = getEncoder();
        this.gearRatio = gearRatio;
        this.minOutput = minOutput;
        this.maxOutput = maxOutput;
    }

    public SparkMaxNeoPID(final int deviceID, final IdleMode defaultIdleMode, final boolean isInverted, final double gearRatio) {
        this(deviceID, defaultIdleMode, isInverted, gearRatio, 0.0, 1.0);
    }

    public SparkMaxNeoPID(final int deviceID, final boolean isInverted, final double gearRatio) {
        this(deviceID, IdleMode.kCoast, isInverted, gearRatio);
    }

    public SparkMaxNeoPID(final int deviceID, final boolean isInverted) {
        this(deviceID, isInverted, 1.0);
    }

    public void init() {
        super.init();
        pidController.setOutputRange(minOutput, maxOutput);
        encoder.setPositionConversionFactor(gearRatio);
        encoder.setVelocityConversionFactor(gearRatio);
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
    
    /**
     * @return
     * Rotations of the motor
     */
    public double getRotations() {
        return encoder.getPosition();
    }

    /**
     * @return
     * Counts of the motor
     */
    public int getCounts() {
        return (int)(getRotations()*NEO_COUNTS_PER_REV);
    }

    /**
     * @return
     * Revolutions per minute of the motor
     */
    public double getRPM() {
        return encoder.getVelocity();
    }

    /**
     * @return
     * Revolutions per second of the motor
     */
    public double getRPS() {
        return (getRPM() / 60.0);
    }

    public void setRotations(double rotations) {
        pidController.setReference(rotations, ControlType.kPosition);
    }

    public void setCounts(int counts) {
        setRotations(counts/NEO_COUNTS_PER_REV);
    }

    public void resetEncoder() {
        encoder.setPosition(0.0);
    }

}
