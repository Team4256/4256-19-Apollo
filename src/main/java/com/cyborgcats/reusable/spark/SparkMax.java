package com.cyborgcats.reusable.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SparkMax extends CANSparkMax {

    private final boolean hasEncoder;
    private final boolean isInverted;
    private final CANEncoder encoder;
    private final IdleMode idleMode;
 
    private boolean updated = false;
    private final double RAMP_RATE = 0.5;
    private final int STALL_CURRENT_LIMIT = 90;
    private final int FREE_CURRENT_LIMIT = 50;
    private static final int TIMEOUT_MS = 10;
    private double lastSetpoint = 0.0;
    private Logger logger;

    //NOTE: do not attempt to use followers with this class as it is not intended to be used in such a way and may cause errors.
    //Main Constructor
    /**
     * 
     * @param deviceID CAN ID of the SparkMax 
     * @param type MotorType (Brushed or Brushless)
     * @param hasEncoder Indication of whether SparkMax utilizes an external encoder
     * @param idleMode IdleMode (Coast or Brake)
     * @param isInverted Indication of whether the SparkMax's motor is inverted
     */
    public SparkMax(final int deviceID, final MotorType type, final boolean hasEncoder, final IdleMode idleMode, final boolean isInverted) {
        super(deviceID, type);
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
        clearFaults();
        setIdleMode(idleMode);
        setOpenLoopRampRate(RAMP_RATE);
        setCANTimeout(TIMEOUT_MS);
        setSmartCurrentLimit(STALL_CURRENT_LIMIT, FREE_CURRENT_LIMIT);
        setInverted(isInverted);
        set(0.0);
    }
    
    public double getRevs() {
        if (hasEncoder) {
            return encoder.getPosition();
        }else {
            return -1;//TODO throw exception
        }
    }

    public double getRPM() {
        if (hasEncoder) {
            return encoder.getVelocity();
        }else {
            return -1.0;//TODO throw exception
        }
    }

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
