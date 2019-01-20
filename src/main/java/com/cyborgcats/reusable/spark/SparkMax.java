package com.cyborgcats.reusable.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;

public class SparkMax extends CANSparkMax {

    private final boolean hasEncoder;
    private final CANEncoder encoder;

    //TODO IMPLEMENTATIONS TO ADD 
    //TODO RAMP RATE 
    //TODO SMART CURRENT LIMIT
    //TODO THROW EXCEPTIONS
    //TODO GEAR RATIO?
    //TODO INVERTED MOTOR AND SENSOR

    //Main Constructor
    public SparkMax(int deviceID, MotorType type, boolean hasEncoder, IdleMode idleMode, boolean isInverted) {
        super(deviceID, type);
        this.hasEncoder = (type == MotorType.kBrushless) ? true : hasEncoder;
        encoder = this.hasEncoder ? getEncoder() : null;
        if(setIdleMode(idleMode) != CANError.kOK) {
        }
    }

    //Constructor for Brushless Motor
    public SparkMax(int deviceID, IdleMode idleMode, boolean isInverted) {
        this(deviceID, MotorType.kBrushless, true, idleMode, isInverted);
    }

    public double getRevs() {
        if (hasEncoder) {
            return encoder.getPosition();
        }else {
            return -1;
        }
    }

    public double getRPM() {
        if (hasEncoder) {
            return encoder.getVelocity();
        }else {
            return -1;
        }
    }

}