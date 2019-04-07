package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Spark;

public class LED {
    private final Spark ledStrip;
    private static LED instance = null;
    private static final double STROBE_RED = -0.11;
    private static final double STROBE_BLUE = -0.09;
    private static final double SOLID_RED = 0.61;
    private static final double SOLID_ORANGE = 0.65;
    private static final double SOLID_YELLOW = 0.69;
    private static final double SOLID_GREEN = 0.77;
    private static final double SOLID_BLUE = 0.87;
    private LEDState desiredLEDState = LEDState.DRIVER_CONTROL;

    public enum LEDState {
        HAS_BALL,
        WANTS_BALL,
        VISION,
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
            case VISION:
                setVisionState();
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
        ledStrip.set(SOLID_YELLOW);
    }

    private void setWantsBallState() {
        ledStrip.set(SOLID_RED);
    }

    private void setVisionState() {
        ledStrip.set(SOLID_GREEN);
    }

    private void setDriverControlState() {
        ledStrip.set(STROBE_BLUE);
    }

    private void setClimberLeftState() {
        ledStrip.set(SOLID_ORANGE);
    }

    private void setClimberRightState() {
        ledStrip.set(SOLID_BLUE);
    }
    
    private void setDefaultState() {
        ledStrip.set(STROBE_RED);
    }
}
