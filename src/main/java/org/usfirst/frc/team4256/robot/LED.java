package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Spark;

public class LED {
    private final Spark ledStrip;
    public static LED instance = null;
    public static final double RAINBOW_RAINBOW_PALETTE = -0.99;
    public static final double RAINBOW_PARTY_PALETTE = -0.97;
    public static final double RAINBOW_OCEAN_PALETTE = -0.95;
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
        CLIMB;
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

    public void setLEDState(LEDState state) {
        desiredLEDState = state;
    }

    public void update() {
        switch (desiredLEDState) {
            case HAS_BALL:
                ledStrip.set(SOLID_RED_ORANGE);
                break;
            case WANTS_BALL:
                ledStrip.set(SOLID_YELLOW);
                break;
            case VALID_TARGET:
                ledStrip.set(SOLID_DARK_GREEN);
                break;
            case NO_VALID_TARGET:
                ledStrip.set(SOLID_DARK_RED);
                break;
            case DRIVER_CONTROL:
                ledStrip.set(SOLID_SKY_BLUE);
                break;
            case CLIMB:
                ledStrip.set(RAINBOW_OCEAN_PALETTE);
                break;
            default:
                ledStrip.set(STROBE_RED);
                break;
        }
    }
}
