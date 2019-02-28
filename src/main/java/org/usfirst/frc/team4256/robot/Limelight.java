package org.usfirst.frc.team4256.robot;

import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {

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

        isAlignedWithTarget = Math.abs(tx) < 2.0;

        if (!isAlignedWithTarget) {
            commandedDirection = (Math.signum(tx) > 0.0) ? (270.0) : (90.0);
            commandedSpeed = (tx*SPEED_CONSTANT < MAX_SPEED_CONSTANT) ? (tx*SPEED_CONSTANT) : (MAX_SPEED_CONSTANT);
        }else {
            commandedSpeed = 0.0;
        }
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
