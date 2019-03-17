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

    private static final double ANGLE_THRESHOLD = 10.0;

    private double commandedDirection = 0.0;
    private double commandedSpeed = 0.0;
    private double commandedSpin = 0.0;

    private double previousDirection = 0.0;//updateVisionTracking2
    private boolean hasPreviousDirection = false;//updateVisionTracking2

    private boolean hasDirection = false;//updateVisionTracking3

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
     * New version of vision to test
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public void updateVisionTracking2() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        
        if (tv < 1.0) {
            hasValidTarget = false;
            commandedSpeed = 0.0;
            hasPreviousDirection = false;
            return;
        }

        hasValidTarget = true;

        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);

        isAlignedWithTarget = Math.abs(tx) < 1.5;
        
        if (!hasPreviousDirection) {
            commandedDirection = tx + 180.0;
            previousDirection = commandedDirection;
            hasPreviousDirection = true;
        }else {
            commandedDirection = (Math.abs((tx + 180.0) - previousDirection) > ANGLE_THRESHOLD) ? previousDirection : (tx + 180.0);
            previousDirection = commandedDirection;
        }

        commandedSpeed = 0.22;//TODO possibly increase (TEST)
        commandedSpin = 0.0;
        
    }

    /**
     * Another new version of vision to test
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public void updateVisionTracking3() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        
        if (tv < 1.0) {
            hasValidTarget = false;
            commandedSpeed = 0.0;
            hasDirection = false;
            return;
        }

        hasValidTarget = true;

        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);

        isAlignedWithTarget = Math.abs(tx) < 1.5;
        
        
        commandedDirection = hasDirection ? commandedDirection : (tx + 180.0);
        commandedSpeed = 0.22;
        commandedSpin = 0.0;
        hasDirection = true;
        
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
     * Changes the camMode.
     * @param camMode desired camMode [0, 1].
     */
    public void changeCamMode(int camMode) {
        camMode = (camMode >= 0 && camMode <= 1) ? (camMode) : (0);
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(camMode);
    }

    /**
     * Enables vision by changing the camMode
     */
    public void enableVision() {
        changeCamMode(0);
    }

    /**
     * Disables vision by changing the camMode
     */
    public void disableVision() {
        changeCamMode(1);
    }

    /**
     * @return
     * <b>True<b> if the current camMode is zero and the NetworkTable is able to be accessed.
     */
    public boolean isVisionEnabled() {
        return (NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").getNumber(-1).intValue() == 0);
    }

    public void changeStream(int stream) {
        stream = (stream >= 0 && stream <= 2) ? (stream) : (0);
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(stream);
    }

    /**
     * Sets the stream to display both camera views in a split screen format
     */
    public void setSplitView() {
        changeStream(0);
    }

    /**
     * Sets the stream to display the vision camera as the main camera and the other camera to be in the right corner.
     */
    public void setVisionView() {
        changeStream(1);
    }

    /**
     * Sets the stream to display the other camera as the main camera and the vision camera to be in the right corner.
     */
    public void setOtherCameraView() {
        changeStream(2);
    }

    public boolean isSplitView() {
        return (NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").getNumber(-1).intValue() == 0);
    } 

    /**
     * @return
     * <p><code>True</code> if a valid target is found</p>
     */
    public boolean hasValidTarget() {
        return hasValidTarget;
    }

    /**
     * @return
     * <p><code>True</code> if we are aligned with the target</p>
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

    /**
     * Outputs relevant information to the SmartDashboard.
     */
    public void outputToSmartDashboard() {
        SmartDashboard.putBoolean("Limelight Is Vision Enabled", isVisionEnabled());
        SmartDashboard.putBoolean("Limelight Is Split View", isSplitView());
        SmartDashboard.putBoolean("Limelight Has Target", hasValidTarget());
        SmartDashboard.putBoolean("Limelight Is Aligned With Target", isAlignedWithTarget());
    }
}
