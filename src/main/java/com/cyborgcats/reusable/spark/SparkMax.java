package com.cyborgcats.reusable.spark;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;

public class SparkMax extends CANSparkMax {

    private final boolean hasEncoder;
    private final CANEncoder encoder;
    private final IdleMode idleMode;
 
    private boolean updated = false;
    private double lastSetpoint = 0.0;

    //Main Constructor
    public SparkMax(final int deviceID, final MotorType type, final boolean hasEncoder, final IdleMode idleMode) {
        super(deviceID, type);
        this.hasEncoder = (type == MotorType.kBrushless) ? true : hasEncoder;
        encoder = this.hasEncoder ? getEncoder() : null;
        this.idleMode = idleMode;
    }

    //This constructor is intended for use with a Brushless Motor
    public SparkMax(final int deviceID, final IdleMode idleMode) {
        this(deviceID, MotorType.kBrushless, true, idleMode);
    }

    //This constructor is intended for use with 2019 Brushless Neo (2019 Season)
    public SparkMax(final int deviceID) {
        this(deviceID, MotorType.kBrushless, true, IdleMode.kCoast);
    }

    public void init() {
        if (clearFaults() != CANError.kOK) {
        }
        if(setIdleMode(idleMode) != CANError.kOK) {
        }
        set(0.0);
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

    public double getRPS() {
        return (getRPM() / 60.0);
    }

    public boolean hasEncoder() {
        return hasEncoder;
    }

    //Set Speed
    public void set(final double speed) {
        super.set(speed);
        lastSetpoint = speed;
        updated = true;
    }

    public void completeLoopUpdate() {
        if (!updated) {
            super.set(lastSetpoint);
        }
        updated = false;
    }
}
