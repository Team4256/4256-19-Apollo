package org.usfirst.frc.team4256.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Ian Woodard
 * The Cyborg Cats' 2019 Limelight Vision Code.
 */
public class Limelight {

    private static final double ANGLE_THRESHOLD = 10.0;

    private double commandedDirection = 0.0;
    private double commandedSpeed = 0.0;
    private double commandedSpin = 0.0;

    private double previousDirection = 0.0;//updateVisionTrackingSticky
    private boolean hasPreviousDirection = false;//updateVisionTrackingSticky

    private boolean hasDirection = false;//updateVisionTrackingStickier

    public Limelight() {

    }

    /**
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public void updateVisionTracking2() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        
        if (tv < 1.0) {
            commandedSpeed = 0.0;
            return;
        }

        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);

        commandedDirection = tx + 180.0;
        commandedSpeed = 0.22;//TODO possibly increase (TEST)
        commandedSpin = 0.0;
        

    }

    /**
     * New and Improved
     */
    public void updateVisionTracking() {

        if (hasTarget()) {
            commandedSpeed = 0.0;
            commandedSpin = 0.0;
            return;
        }

        commandedDirection = getTargetOffsetDegrees() + 180.0;
        commandedSpeed = 0.22;
        commandedSpin = 0.0;
        
    }

    /**
     * New version of vision to test
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public void updateStickyVisionTracking() {

        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        
        if (tv < 1.0) {
            commandedSpeed = 0.0;
            hasPreviousDirection = false;
            return;
        }
        
        if (!hasPreviousDirection) {
            commandedDirection = getTargetOffsetDegrees() + 180.0;
            previousDirection = commandedDirection;
            hasPreviousDirection = true;
        }else {
            commandedDirection = (Math.abs((getTargetOffsetDegrees() + 180.0) - previousDirection) > ANGLE_THRESHOLD) ? previousDirection : (getTargetOffsetDegrees() + 180.0);
            previousDirection = commandedDirection;
        }

        commandedSpeed = 0.22;
        commandedSpin = 0.0;
        
    }

    /**
     * Another new version of vision to test
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public void updateStickierVisionTracking() {

        
        if (hasTarget()) {
            commandedSpeed = 0.0;
            commandedSpin = 0.0;
            hasDirection = false;
            return;
        }
        
        commandedDirection = hasDirection ? commandedDirection : (getTargetOffsetDegrees() + 180.0);
        commandedSpeed = 0.22;
        commandedSpin = 0.0;
        hasDirection = true;
        
    }

    private boolean hasTarget() {
        return NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getNumber(0.0).intValue() == 1;
    }

    private double getTargetOffsetDegrees() {
        return NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);
    }

    public static enum LedMode {
        PIPELINE(0),
        FORCE_OFF(1),
        FORCE_BLINK(2),
        FORCE_ON(3);

        private final int value;

        LedMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Changes the LED mode.
     * @param ledMode the desired ledMode [0, 3].
     */
    private void setLEDMode(LedMode ledMode) {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(ledMode.getValue());
    }

    /**
     * Forces the LED off.
     */
    public void turnLEDOff() {
        setLEDMode(LedMode.FORCE_OFF);
    }

    /**
     * Forces the LED on.
     */
    public void turnLEDOn() {
        setLEDMode(LedMode.FORCE_ON);
    }

    /**
     * Forces the LED to blink.
     */
    public void makeLEDBlink() {
        setLEDMode(LedMode.FORCE_BLINK);
    }

    /**
     * Sets the LED to their default value.
     */
    public void makeLEDDefault() {
        setLEDMode(LedMode.PIPELINE);
    }



    /**
     * Changes the pipeline of the vision.
     * @param pipeline desired pipeline [0, 9].
     */
    public void setPipeline(int pipeline) {
        pipeline = (pipeline >= 0 && pipeline <= 9) ? (pipeline) : (0);
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(pipeline);
    }

    public int getPipeline() {
        return NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").getNumber(0).intValue();
    }
    


    public static enum CamMode {
        VISION(0),
        DRIVER(1);

        private final int value;

        CamMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Changes the camMode.
     * @param camMode desired camMode [0, 1].
     */
    public void setCamMode(CamMode camMode) {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(camMode.getValue());
    }

    /**
     * Enables vision by changing the camMode (Disables Driver Camera)
     */
    public void enableVision() {
        setCamMode(CamMode.VISION);
    }

    /**
     * Disables vision by changing the camMode (Enables Driver Camera)
     */
    public void disableVision() {
        setCamMode(CamMode.DRIVER);
    }

    /**
     * @return
     * <b>True<b> if the current camMode is zero and the NetworkTable is able to be accessed.
     */
    public boolean isVisionEnabled() {
        return (NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").getNumber(-1).intValue() == CamMode.VISION.getValue());
    }



    public static enum StreamMode {
        STANDARD(0),
        PIP_MAIN(1),
        PIP_SECONDARY(2);

        private final int value;

        StreamMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public void setStreamView(StreamMode streamMode) {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(streamMode.getValue());
    }

    /**
     * Sets the stream to display both camera views in a split screen format
     */
    public void setSplitView() {
        setStreamView(StreamMode.STANDARD);
    }

    /**
     * Sets the stream to display the vision camera as the main camera and the other camera to be in the right corner.
     */
    public void setVisionView() {
        setStreamView(StreamMode.PIP_MAIN);
    }

    /**
     * Sets the stream to display the other camera as the main camera and the vision camera to be in the right corner.
     */
    public void setOtherCameraView() {
        setStreamView(StreamMode.PIP_SECONDARY);
    }

    public boolean isSplitView() {
        return (NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").getNumber(-1).intValue() == StreamMode.STANDARD.getValue());
    }


    public static enum SnapshotMode {
        STOP(0),
        TWO_PER_SECOND(1);

        private final int value;

        SnapshotMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
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
        SmartDashboard.putBoolean("Limelight Has Target", hasTarget());
    }
}
