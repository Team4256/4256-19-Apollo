package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.Compass;
import com.cyborgcats.reusable.PID;

import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {

    private enum LED_MODE {
        DEFAULT(0),
        OFF(1),
        BLINK(2),
        ON(3);

        private final int value;

        private LED_MODE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static final double MAX_SPIN_CONSTANT = 0.25;
    private double commandedDirection = 0.0;
    private double commandedSpeed = 0.0;
    private double commandedSpin = 0.0;
    private boolean hasValidTarget = false;
    private boolean isAlignedWithTarget = false;
    private boolean isGyroAligned = false;

    public Limelight() {

    }

    public void updateVisionTrackingGyroIgnorant() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        
        if (tv < 1.0) {
            hasValidTarget = false;
            commandedSpeed = 0.0;
            return;
        }

        hasValidTarget = true;

        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);
        double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0.0);

        isAlignedWithTarget = Math.abs(tx) < 1.5;

        commandedDirection = tx + 180.0;
        commandedSpeed = 0.22;
    }

    public void updateVisionTracking(double gyroHeading) {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        
        if (tv < 1.0) {
            hasValidTarget = false;
            commandedSpeed = 0.0;
            return;
        }

        hasValidTarget = true;

        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);
        double ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0.0);

        isAlignedWithTarget = Math.abs(tx) < 1.5;
        
        isGyroAligned = Math.abs(gyroHeading % 90.0) <= 2.0;

        double orientation = 0.0;

        if (gyroHeading >= 45.0 && gyroHeading < 135.0) {
            orientation = 90.0;
        }else if (gyroHeading >= 135.0 && gyroHeading < 225.0) {
            orientation = 180.0;
        }else if (gyroHeading >= 225.0 && gyroHeading < 315.0) {
            orientation = 270.0;
        }

        double spinError = Compass.path(gyroHeading, orientation);

        if (isGyroAligned) {
            PID.clear("spin");  
        }

        commandedDirection = tx + 180.0;
        commandedSpeed = 0.22;
        commandedSpin = isGyroAligned ? (0.0) : (Math.max(-MAX_SPIN_CONSTANT, Math.min(PID.get("spin", spinError), MAX_SPIN_CONSTANT)));

    }

    private void changeLEDMode(int ledMode) {
        ledMode = (ledMode >= 0 && ledMode <= 3) ? (ledMode) : (0);
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(ledMode);
    }

    public void turnLEDOff() {
        changeLEDMode(LED_MODE.OFF.getValue());
    }

    public void turnLEDOn() {
        changeLEDMode(LED_MODE.ON.getValue());
    }

    public void makeLEDBlink() {
        changeLEDMode(LED_MODE.BLINK.getValue());
    }

    public void makeLEDDefault() {
        changeLEDMode(LED_MODE.DEFAULT.getValue());
    }

    public void changePipeline(int pipeline) {
        pipeline = (pipeline >= 0 && pipeline <= 9) ? (pipeline) : (0);
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(pipeline);
    }

    public boolean hasValidTarget() {
        return hasValidTarget;
    }

    public boolean isAlignedWithTarget() {
        return isAlignedWithTarget;
    }

    public double getCommandedDirection() {
        return commandedDirection;
    }

    public double getCommandedSpeed() {
        return commandedSpeed;
    }

    public double getCommandedSpin() {
        return commandedSpin;
    }
}
