package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Spark;

public class LED {
    private final Spark ledStrip;
    public static LED instance = null;
    public static final double OCEAN = -0.41;
    public static final double FOREST = -0.45;
    public static final double STROBE_RED = -0.11;
    public static final double STROBE_BLUE = -0.09;
    public static final double SOLID_HOT_PINK = 0.57;
    public static final double SOLID_DARK_RED = 0.59;
    public static final double SOLID_RED = 0.61;
    public static final double SOLID_RED_ORANGE = 0.63;
    public static final double SOLID_ORANGE = 0.65;
    public static final double SOLID_GOLD = 0.67;
    public static final double SOLID_YELLOW = 0.69;
    public static final double SOLID_LAWN_GREEN = 0.71;
    public static final double SOLID_LIME = 0.73;
    public static final double SOLID_DARK_GREEN = 0.75;
    public static final double SOLID_GREEN = 0.77;
    public static final double SOLID_BLUE_GREEN = 0.79;
    public static final double SOLID_AQUA = 0.81;
    public static final double SOLID_SKY_BLUE = 0.83;
    public static final double SOLID_DARK_BLUE = 0.85;
    public static final double SOLID_BLUE = 0.87;
    public static final double SOLID_BLUE_VIOLET = 0.89;
    public static final double SOLID_VIOLET = 0.91;
    public static final double SOLID_WHITE = 0.93;
    public static final double SOLID_GRAY = 0.95;
    public static final double SOLID_DARK_GRAY = 0.97;
    public static final double SOLID_BLACK = 0.99;
    private LEDState desiredLEDState = LEDState.DRIVER_CONTROL;

    public enum LEDState {
        HAS_BALL,
        WANTS_BALL,
        VALID_TARGET,
        NO_VALID_TARGET,
        DRIVER_CONTROL,
        CLIMBER_LEFT,
        CLIMBER_RIGHT;
    }

    private LED() {
        ledStrip = new Spark(Parameters.LED_PORT);
    }

    public static synchronized LED getInstance() {
        if (instance == null) {
            instance = new LED();
        }

        return instance;
    }

    public synchronized void setLEDState(LEDState state) {
        desiredLEDState = state;
    }

    public synchronized void update() {
        switch (desiredLEDState) {
            case HAS_BALL:
                setHasBallState();
                break;
            case WANTS_BALL:
                setWantsBallState();
                break;
            case VALID_TARGET:
                setValidTargetState();
                break;
            case NO_VALID_TARGET:
                setNoValidTargetState();
                break;
            case DRIVER_CONTROL:
                setDriverControlState();
                break;
            case CLIMBER_LEFT:
                setClimberLeftState();
                break;
            case CLIMBER_RIGHT:
                setClimberRightState();
                break;
            default:
                setDefaultState();
                break;
        }
    }

    private void setHasBallState() {
        ledStrip.set(SOLID_ORANGE);
    }

    private void setWantsBallState() {
        ledStrip.set(SOLID_GOLD);
    }

    private void setValidTargetState() {
        ledStrip.set(SOLID_GREEN);
    }

    private void setNoValidTargetState() {
        ledStrip.set(SOLID_RED);
    }

    private void setDriverControlState() {
        ledStrip.set(SOLID_BLUE);
    }

    private void setClimberLeftState() {
        ledStrip.set(SOLID_VIOLET);
    }

    private void setClimberRightState() {
        ledStrip.set(SOLID_YELLOW);
    }
    
    private void setDefaultState() {
        ledStrip.set(STROBE_RED);
    }
}
