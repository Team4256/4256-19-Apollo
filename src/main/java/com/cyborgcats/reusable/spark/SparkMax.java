package com.cyborgcats.reusable.spark;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DriverStation;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <b>As of now, this class is meant for use with a NEO Brushless motor, do not attempt to use any other type of motor with this class.</b>
 */
public class SparkMax extends CANSparkMax {

    private static final int TIMEOUT_MS = 10;
    private static final double RAMP_RATE = 0.5;
    private static final int STALL_CURRENT_LIMIT = 90;
    private static final int FREE_CURRENT_LIMIT = 50;
    private static final int NEO_COUNTS_PER_REV = 42;
    private final boolean hasEncoder;
    private final boolean isInverted;
    private final CANEncoder encoder;
    private final IdleMode idleMode;
    private final int deviceID;
    private final int countsPerRev;
    private boolean updated = false;
    private double lastSetpoint = 0.0;
    private Logger logger;

    //NOTE: do not attempt to use followers with this class as it is not intended to be used in such a way and may cause errors.
    //Main Constructor
    /**
     * <h2>CAUTION: This is only inteded for use with a NEO Brushless motor</h2>
     * <h3>For use with other motors please use the <b>CANSparkMax<b> class</h3>
     * @param deviceID CAN ID of the SparkMax 
     * @param type MotorType (Brushed or Brushless)
     * @param hasEncoder Indication of whether SparkMax utilizes an external encoder
     * @param idleMode IdleMode (Coast or Brake)
     * @param isInverted Indication of whether the SparkMax's motor is inverted
     */
    public SparkMax(final int deviceID, final MotorType type, final boolean hasEncoder, final IdleMode idleMode, final boolean isInverted) {
        super(deviceID, type);
        countsPerRev = (type == MotorType.kBrushless) ? NEO_COUNTS_PER_REV : 0;//not setup for non-neo encoders
        this.deviceID = deviceID;
        this.hasEncoder = (type == MotorType.kBrushless) ? true : hasEncoder;
        encoder = this.hasEncoder ? getEncoder() : null;
        this.idleMode = idleMode;
        this.isInverted = isInverted;
        logger = Logger.getLogger("SparkMax " + Integer.toString(deviceID));
    }

    //This constructor is intended for use with a Brushless Motor
    /**
     * 
     * @param deviceID CAN ID of the SparkMax
     * @param idleMode IdleMode (Coast or Brake)
     * @param isInverted Indication of whether the SparkMax's motor is inverted
     */
    public SparkMax(final int deviceID, final IdleMode idleMode, final boolean isInverted) {
        this(deviceID, MotorType.kBrushless, true, idleMode, isInverted);
    }

    //This constructor is intended for use with Coast Mode Only
    /**
     * 
     * @param deviceID CAN ID of the SparkMax
     * @param isInverted Indication of whether the SparkMax's motor is inverted
     */
    public SparkMax(final int deviceID, final boolean isInverted) {
        this(deviceID, MotorType.kBrushless, true, IdleMode.kCoast, isInverted);
    }

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

    public int getCounts() {
        if (hasEncoder) {
            return (int)(encoder.getPosition()*countsPerRev);
        }else {
            throw new IllegalStateException("SparkMax" + deviceID + " does not have an encoder.");
        }
    }
    
    public double getRevs() {
        if (hasEncoder) {
            return encoder.getPosition();
        }else {
            throw new IllegalStateException("SparkMax" + deviceID + " does not have an encoder.");
        }
    }

    /**
     * 
     * @return
     * <p>Revolutions per minute of the motor.</p>
     */
    public double getRPM() {
        if (hasEncoder) {
            return encoder.getVelocity();
        }else {
            throw new IllegalStateException("SparkMax" + deviceID + " does not have an encoder.");
        }
    }

    /**
     * 
     * @return
     * <p>Revolutions per second of the motor.</p>
     */
    public double getRPS() {
        return (getRPM() / 60.0);
    }

    public boolean hasEncoder() {
        return hasEncoder;
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
