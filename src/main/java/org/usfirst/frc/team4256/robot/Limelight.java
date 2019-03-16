package org.usfirst.frc.team4256.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

    private double commandedDirection = 0.0;
    private double commandedSpeed = 0.0;
    private double commandedSpin = 0.0;
    private boolean hasValidTarget = false;
    private boolean isAlignedWithTarget = false;

    public Limelight() {

    }

    /**
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public void updateVisionTracking() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        
        if (tv < 1.0) {
            hasValidTarget = false;
            commandedSpeed = 0.0;
            return;
        }

        hasValidTarget = true;

        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);

        isAlignedWithTarget = Math.abs(tx) < 1.5;

        commandedDirection = tx + 180.0;
        commandedSpeed = 0.22;//TODO possibly increase (TEST)
        commandedSpin = 0.0;
    }

    /**
     * Changes the LED mode.
     * @param ledMode the desired ledMode [0, 3].
     */
    private void changeLEDMode(int ledMode) {
        ledMode = (ledMode >= 0 && ledMode <= 3) ? (ledMode) : (0);
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(ledMode);
    }

    /**
     * Forces the LED off.
     */
    public void turnLEDOff() {
        changeLEDMode(LED_MODE.OFF.getValue());
    }

    /**
     * Forces the LED on.
     */
    public void turnLEDOn() {
        changeLEDMode(LED_MODE.ON.getValue());
    }

    /**
     * Forces the LED to blink.
     */
    public void makeLEDBlink() {
        changeLEDMode(LED_MODE.BLINK.getValue());
    }

    /**
     * Sets the LED to their default value.
     */
    public void makeLEDDefault() {
        changeLEDMode(LED_MODE.DEFAULT.getValue());
    }

    /**
     * Changes the pipeline of the vision.
     * @param pipeline desired pipeline [0, 9].
     */
    public void changePipeline(int pipeline) {
        pipeline = (pipeline >= 0 && pipeline <= 9) ? (pipeline) : (0);
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(pipeline);
    }

    /**
     * @return
     * <p><code>True</code> if a valid target is found</p>
     * <p>and</p>
     * <p><code>False</code> if a valid target is not found</p>
     */
    public boolean hasValidTarget() {
        return hasValidTarget;
    }

    /**
     * @return
     * <p><code>True</code> if we are aligned with the target</p>
     * <p>and</p>
     * <p><code>False</code> if we are not aligned with the target</p>
     */
    public boolean isAlignedWithTarget() {
        return isAlignedWithTarget;
    }

    /**
     * @return the <code>commandedDirection</code> for swerve computed in {@link #updateVisionTracking()}.
     */
    public double getCommandedDirection() {
        return commandedDirection;
    }

    /**
     * @return the <code>commandedSpeed</code> for swerve computed in {@link #updateVisionTracking()}.
     */
    public double getCommandedSpeed() {
        return commandedSpeed;
    }

    /**
     * @return the <code>commandedSpin</code> for swerve computed in {@link #updateVisionTracking()}.
     */
    public double getCommandedSpin() {
        return commandedSpin;
    }

    public void outputToSmartDashboard() {
        SmartDashboard.putBoolean("Limelight Has Target", hasValidTarget());
        SmartDashboard.putBoolean("Limelight Is Aligned With Target", isAlignedWithTarget());
    }
}
