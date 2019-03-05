package org.usfirst.frc.team4256.robot;

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

    private static final double SPEED_CONSTANT = 0.02;
    private static final double MAX_SPEED_CONSTANT = 0.15;
    private double commandedDirection = 0.0;
    private double commandedSpeed = 0.0;
    private boolean hasValidTarget = false;
    private boolean isAlignedWithTarget = false;

    public Limelight() {

    }

    public void updateVisionTracking() {

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

        if (!isAlignedWithTarget) {
            commandedDirection = (Math.signum(tx) > 0.0) ? (270.0) : (90.0);
            commandedSpeed = (tx*SPEED_CONSTANT < MAX_SPEED_CONSTANT) ? (tx*SPEED_CONSTANT) : (MAX_SPEED_CONSTANT);
        }else {
            commandedSpeed = 0.0;
        }
    }

    public void updateVisionTracking2() {

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

        commandedDirection = tx+180.0;
        commandedSpeed = 0.22;
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
}
