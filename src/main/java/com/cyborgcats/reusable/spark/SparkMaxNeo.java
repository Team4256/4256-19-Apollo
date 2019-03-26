package com.cyborgcats.reusable.spark;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANEncoder;
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
    private static final double RAMP_RATE = 0.5;
    private static final int STALL_CURRENT_LIMIT = 90;
    private static final int FREE_CURRENT_LIMIT = 50;
    private static final int NEO_COUNTS_PER_REV = 42;
    private final CANEncoder encoder;
    private final int deviceID;
    private final IdleMode idleMode;
    private final boolean isInverted;
    private boolean updated = false;
    private double lastSetpoint = 0.0;
    private Logger logger;

    /**
     * Offers a simple way of initializing and using NEO Brushless motors with a SparkMax motor controller.
     * @param deviceID CAN ID of the SparkMax
     * @param idleMode IdleMode (Coast or Brake)
     * @param isInverted Indication of whether the SparkMax's motor is inverted
     */
    public SparkMaxNeo(final int deviceID, final IdleMode idleMode, final boolean isInverted) {
        super(deviceID, MotorType.kBrushless);
        encoder = getEncoder();
        this.deviceID = deviceID;
        this.idleMode = idleMode;
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
        if (setIdleMode(idleMode) != CANError.kOK) {
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

    /**
     * @return
     * Counts of the motor
     */
    public int getCounts() {
        return (int)(encoder.getPosition()*NEO_COUNTS_PER_REV);
    }
    
    /**
     * @return
     * Rotations of the motor
     */
    public double getPosition() {
        return encoder.getPosition();
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
