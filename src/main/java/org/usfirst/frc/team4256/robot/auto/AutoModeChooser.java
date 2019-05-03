package org.usfirst.frc.team4256.robot.auto;

import java.util.Optional;

import org.usfirst.frc.team4256.robot.auto.modes.*;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoModeChooser {

    public enum StartingPosition {
        LEFT,
        CENTER,
        RIGHT;
    }

    public enum DesiredDirection {
        LEFT,
        RIGHT;
    }

    enum DesiredAutoMode {
        DRIVER_CONTROL,
        ONE_HATCH_CARGO_SHIP_FRONT,
        CENTER_ONLY_TWO_HATCH_CARGO_SHIP,
        CENTER_ONLY_ONE_POINT_FIVE_HATCH,
        TEST_MODE;
    }

    private StartingPosition startingPosition = null;
    private DesiredDirection desiredDirection = null;
    private DesiredAutoMode desiredAutoMode = null;

    private SendableChooser<StartingPosition> startingPositionChooser;
    private SendableChooser<DesiredDirection> desiredDirectionChooser;
    private SendableChooser<DesiredAutoMode> desiredAutoModeChooser;

    public AutoModeChooser() {
        startingPositionChooser = new SendableChooser<>();
        startingPositionChooser.setDefaultOption("Left", StartingPosition.LEFT);
        startingPositionChooser.addOption("Center", StartingPosition.CENTER);
        startingPositionChooser.addOption("Right", StartingPosition.RIGHT);
        SmartDashboard.putData("Starting Position", startingPositionChooser);

        desiredDirectionChooser = new SendableChooser<>();
        desiredDirectionChooser.setDefaultOption("Left", DesiredDirection.LEFT);
        desiredDirectionChooser.addOption("Right", DesiredDirection.RIGHT);
        SmartDashboard.putData("Desired Direction", desiredDirectionChooser);

        desiredAutoModeChooser = new SendableChooser<>();
        desiredAutoModeChooser.setDefaultOption("Driver Control", DesiredAutoMode.DRIVER_CONTROL);
        desiredAutoModeChooser.addOption("One Hatch Cargoship", DesiredAutoMode.ONE_HATCH_CARGO_SHIP_FRONT);
        desiredAutoModeChooser.addOption("Center Only Two Hatch Cargoship", DesiredAutoMode.CENTER_ONLY_TWO_HATCH_CARGO_SHIP);
        desiredAutoModeChooser.addOption("Center Only One Point Five Hatch", DesiredAutoMode.CENTER_ONLY_ONE_POINT_FIVE_HATCH);
        desiredAutoModeChooser.addOption("Test", DesiredAutoMode.TEST_MODE);
        SmartDashboard.putData("Auto Mode", desiredAutoModeChooser);
    }

    public void update() {
        startingPosition = startingPositionChooser.getSelected();
        desiredDirection = desiredDirectionChooser.getSelected();
        desiredAutoMode = desiredAutoModeChooser.getSelected();
    }
    
    public Optional<AutoMode> getSelectedAutoMode() {
        switch (desiredAutoMode) {
            case DRIVER_CONTROL:
                return Optional.of(new DriverControlMode());
            case ONE_HATCH_CARGO_SHIP_FRONT:
                return Optional.of(new OneHatchFrontCargoShipMode(startingPosition));
            case CENTER_ONLY_TWO_HATCH_CARGO_SHIP:
                return Optional.of(new CenterOnlyTwoHatchCargoShipMode(desiredDirection));
            case CENTER_ONLY_ONE_POINT_FIVE_HATCH:
                return Optional.of(new CenterOnlyOnePointFiveHatchAuto());
            case TEST_MODE:
                return Optional.of(new TestMode());
            default:
                return Optional.of(new DriverControlMode());
        }
    }

    public void reset() {
        startingPosition = null;
        desiredDirection = null;
        desiredAutoMode = null;
    }

    public String[] getRawSelections() {
        return new String[] {
            startingPositionChooser.getSelected().name(),
            desiredDirectionChooser.getSelected().name(),
            desiredAutoModeChooser.getSelected().name(),
        };
    }
}
