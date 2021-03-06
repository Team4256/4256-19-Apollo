package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.Xbox;

import org.usfirst.frc.team4256.robot.controllers.Driver;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The Cyborg Cats' 2019 Limelight Vision Code.
 * @author Ian Woodard
 */
public class Limelight {

    private static final double ANGLE_THRESHOLD = 10.0;
    private static final Driver driver = Driver.getInstance();
    private static Limelight instance = null;

    private double commandedDirection = 0.0;
    private double commandedSpeed = 0.0;
    private double commandedSpin = 0.0;

    private double previousDirection = 0.0;//updateVisionTrackingSticky
    private boolean hasPreviousDirection = false;//updateVisionTrackingSticky

    private boolean hasDirection = false;//updateVisionTrackingStickier

    private Limelight() {
    }

    public synchronized static Limelight getInstance() {
        if (instance == null) {
            instance = new Limelight();
        }

        return instance;
    }

    /**
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public synchronized void updateVisionTracking2() {
    
        double tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0.0);
        
        if (tv < 1.0) {
            commandedSpeed = 0.0;
            return;
        }

        double tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0.0);

        commandedDirection = tx + Robot.GYRO_OFFSET;
        commandedSpeed = 0.22;//TODO possibly increase (TEST)
        commandedSpin = 0.0;

    }

    /**
     * New and Improved
     */
    public synchronized void updateVisionTracking() {
        if (!hasTarget()) {
            commandedSpeed = 0.0;
            commandedSpin = 0.0;
            return;
        }

        commandedDirection = getTargetOffsetDegrees() + Robot.GYRO_OFFSET;
        commandedSpeed = 0.22;
        commandedSpin = 0.0;
    }

    /**
     * Normal vision tracking with driver control when no target is found
     */
    public synchronized void updateVisionTrackingAssist() {
        double driverCommandedSpeed = 0.6 * driver.getCurrentRadius(Xbox.STICK_LEFT, true);
        double driverCommandedSpin = 0.5 * driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);
        commandedDirection = (hasTarget()) ? (getTargetOffsetDegrees() + Robot.GYRO_OFFSET) : driver.getCurrentAngle(Xbox.STICK_LEFT, true);
        commandedSpeed = (hasTarget()) ? 0.22 : (driverCommandedSpeed*driverCommandedSpeed);
        commandedSpin = (hasTarget()) ? 0.0 : ((driverCommandedSpin*driverCommandedSpin)*Math.signum(driverCommandedSpin));
    }

    /**
     * New version of vision to test
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public synchronized void updateStickyVisionTracking() {
        if (!hasTarget()) {
            commandedSpeed = 0.0;
            hasPreviousDirection = false;
            return;
        }
        
        if (!hasPreviousDirection) {
            commandedDirection = getTargetOffsetDegrees() + Robot.GYRO_OFFSET;
            previousDirection = commandedDirection;
            hasPreviousDirection = true;
        }else {
            commandedDirection = (Math.abs((getTargetOffsetDegrees() + Robot.GYRO_OFFSET) - previousDirection) > ANGLE_THRESHOLD) ? previousDirection : (getTargetOffsetDegrees() + Robot.GYRO_OFFSET);
            previousDirection = commandedDirection;
        }

        commandedSpeed = 0.22;
        commandedSpin = 0.0;
    }

    /**
     * Another new version of vision to test
     * A periodically run function that uses vison to compute direction, speed, and spin for swerve in order to score autonomously.
     */
    public synchronized void updateStickierVisionTracking() {
        if (!hasTarget()) {
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

    public synchronized boolean hasTarget() {
        return NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getNumber(0.0).intValue() == 1;
    }

    private synchronized double getTargetOffsetDegrees() {
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
    private synchronized void setLEDMode(LedMode ledMode) {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(ledMode.getValue());
    }

    /**
     * Forces the LED off.
     */
    public synchronized void turnLEDOff() {
        setLEDMode(LedMode.FORCE_OFF);
    }

    /**
     * Forces the LED on.
     */
    public synchronized void turnLEDOn() {
        setLEDMode(LedMode.FORCE_ON);
    }

    /**
     * Forces the LED to blink.
     */
    public synchronized void makeLEDBlink() {
        setLEDMode(LedMode.FORCE_BLINK);
    }

    /**
     * Sets the LED to their default value.
     */
    public synchronized void makeLEDDefault() {
        setLEDMode(LedMode.PIPELINE);
    }



    /**
     * Changes the pipeline of the vision.
     * @param pipeline desired pipeline [0, 9].
     */
    public synchronized void setPipeline(int pipeline) {
        pipeline = (pipeline >= 0 && pipeline <= 9) ? (pipeline) : (0);
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(pipeline);
    }

    public synchronized int getPipeline() {
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
    public synchronized void setCamMode(CamMode camMode) {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(camMode.getValue());
    }

    /**
     * Enables vision by changing the camMode (Disables Driver Camera)
     */
    public synchronized void enableVision() {
        setCamMode(CamMode.VISION);
    }

    /**
     * Disables vision by changing the camMode (Enables Driver Camera)
     */
    public synchronized void disableVision() {
        setCamMode(CamMode.DRIVER);
    }

    /**
     * @return
     * <b>True<b> if the current camMode is zero and the NetworkTable is able to be accessed.
     */
    public synchronized boolean isVisionEnabled() {
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

    public synchronized void setStreamView(StreamMode streamMode) {
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(streamMode.getValue());
    }

    /**
     * Sets the stream to display both camera views in a split screen format
     */
    public synchronized void setSplitView() {
        setStreamView(StreamMode.STANDARD);
    }

    /**
     * Sets the stream to display the vision camera as the main camera and the other camera to be in the right corner.
     */
    public synchronized void setVisionView() {
        setStreamView(StreamMode.PIP_MAIN);
    }

    /**
     * Sets the stream to display the other camera as the main camera and the vision camera to be in the right corner.
     */
    public synchronized void setOtherCameraView() {
        setStreamView(StreamMode.PIP_SECONDARY);
    }

    public synchronized boolean isSplitView() {
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
    public synchronized double getCommandedDirection() {
        return commandedDirection;
    }

    /**
     * @return the <code>commandedSpeed</code> for swerve computed in {@link #updateVisionTracking()}.
     */
    public synchronized double getCommandedSpeed() {
        return commandedSpeed;
    }

    /**
     * @return the <code>commandedSpin</code> for swerve computed in {@link #updateVisionTracking()}.
     */
    public synchronized double getCommandedSpin() {
        return commandedSpin;
    }

    /**
     * Outputs relevant information to the SmartDashboard.
     */
    public synchronized void outputToSmartDashboard() {
        SmartDashboard.putBoolean("Limelight Is Vision Enabled", isVisionEnabled());
        SmartDashboard.putBoolean("Limelight Is Split View", isSplitView());
        SmartDashboard.putBoolean("Limelight Has Target", hasTarget());
    }
}
