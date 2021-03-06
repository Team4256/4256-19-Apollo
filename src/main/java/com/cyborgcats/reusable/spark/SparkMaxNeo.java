package com.cyborgcats.reusable.spark;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANError;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SparkMax Motor Controller Used With a Neo Brushless Motor.
 * <p>
 * <i>Do not attempt to use followers with this class as it is not intended to be used in such a way and may cause errors.</i>
 * @author Ian Woodard
 */
public class SparkMaxNeo extends CANSparkMax {
    private static final int TIMEOUT_MS = 10;
    private static final double RAMP_RATE = 1.0;
    private static final int STALL_CURRENT_LIMIT = 90;
    private static final int FREE_CURRENT_LIMIT = 50;
    protected final int deviceID;
    private final IdleMode defaultIdleMode;
    private final boolean isInverted;
    private IdleMode currentIdleMode;
    private boolean updated = false;
    private double lastSetpoint = 0.0;
    private Logger logger;

    /**
     * Offers a simple way of initializing and using NEO Brushless motors with a SparkMax motor controller.
     * @param deviceID CAN ID of the SparkMax
     * @param defaultIdleMode IdleMode (Coast or Brake)
     * @param isInverted Indication of whether the SparkMax's motor is inverted
     */
    public SparkMaxNeo(final int deviceID, final IdleMode defaultIdleMode, final boolean isInverted) {
        super(deviceID, MotorType.kBrushless);
        this.deviceID = deviceID;
        this.defaultIdleMode = defaultIdleMode;
        currentIdleMode = defaultIdleMode;
        this.isInverted = isInverted;
        logger = Logger.getLogger("SparkMax " + Integer.toString(deviceID));
    }

    /**
     * Offers a simple way of initializing and using NEO Brushless motors with a SparkMax motor controller.
     * <p>
     * This constructor is for NEO Brushless motors set by default to coast <code>IdleMode</code>. 
     * @param deviceID CAN ID of the SparkMax
     * @param isInverted Indication of whether the SparkMax's motor is inverted
     */
    public SparkMaxNeo(final int deviceID, final boolean isInverted) {
        this(deviceID, IdleMode.kCoast, isInverted);
    }

    /**
     * Performs necessary initialization
     */
    public void init() {
        if (clearFaults() != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not clear faults.", false);
        }
        if (setIdleMode(defaultIdleMode) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not set idle mode.", false);
        }
        if (setOpenLoopRampRate(RAMP_RATE) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not set open loop ramp rate.", false);
        }
        if (setClosedLoopRampRate(RAMP_RATE) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not set closed loop ramp rate.", false);
        }
        if (setCANTimeout(TIMEOUT_MS) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not set can timeout.", false);
        }
        if (setSmartCurrentLimit(STALL_CURRENT_LIMIT, FREE_CURRENT_LIMIT) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not set smart current limit.", false);
        }
        setInverted(isInverted);
        set(0.0);
    }

    public void resetIdleMode() {
        if (setIdleMode(defaultIdleMode) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not set idle mode.", false);
            return;
        }
        currentIdleMode = defaultIdleMode;
    }

    public void enableBrakeMode() {
        if (setIdleMode(IdleMode.kBrake) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not set idle mode.", false);
            return;
        }
        currentIdleMode = IdleMode.kBrake;
    }

    public void disableBrakeMode() {
        if (setIdleMode(IdleMode.kCoast) != CANError.kOK) {
            DriverStation.reportError("SparkMax " + deviceID + " could not set idle mode.", false);
            return;
        }
        currentIdleMode = IdleMode.kCoast;
    }

    public IdleMode getDefaultIdleMode() {
        return defaultIdleMode;
    }

    public IdleMode getCurrentIdleMode() {
        return currentIdleMode;
    }

    //Set Speed
    @Override
    public void set(final double speed) {
        super.set(speed);
        lastSetpoint = speed;
        updated = true;
        logger.log(Level.FINE, Double.toString(speed));
    }

    public void completeLoopUpdate() {
        if (!updated) {
            super.set(lastSetpoint);
        }
        updated = false;
    }

    public void setParentLogger(final Logger logger) {this.logger = logger;}
}
