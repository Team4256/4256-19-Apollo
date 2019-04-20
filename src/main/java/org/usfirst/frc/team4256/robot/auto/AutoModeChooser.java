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

    enum DesiredAutoMode {
        DRIVER_CONTROL,
        ONE_HATCH_CARGO_SHIP_FRONT,
        TWO_HATCH_CARGO_SHIP_FRONT,
        CENTER_ONLY_ONE_POINT_FIVE_HATCH,
        DRIVER_ASSISTED_ONE_POINT_FIVE_HATCH,
        TEST_MODE;
    }

    private StartingPosition startingPosition = null;
    private DesiredAutoMode desiredAutoMode = null;

    private SendableChooser<StartingPosition> startingPositionChooser;
    private SendableChooser<DesiredAutoMode> desiredAutoModeChooser;

    public AutoModeChooser() {
        startingPositionChooser = new SendableChooser<>();
        startingPositionChooser.setDefaultOption("Center", StartingPosition.CENTER);
        startingPositionChooser.addOption("Left", StartingPosition.LEFT);
        startingPositionChooser.addOption("Right", StartingPosition.RIGHT);
        SmartDashboard.putData("Starting Position", startingPositionChooser);

        desiredAutoModeChooser = new SendableChooser<>();
        desiredAutoModeChooser.setDefaultOption("Driver Control", DesiredAutoMode.DRIVER_CONTROL);
        desiredAutoModeChooser.addOption("One Hatch Front Cargoship", DesiredAutoMode.ONE_HATCH_CARGO_SHIP_FRONT);
        desiredAutoModeChooser.addOption("(Do Not Use Without Ian) Two Hatch Front Cargoship", DesiredAutoMode.TWO_HATCH_CARGO_SHIP_FRONT);
        desiredAutoModeChooser.addOption("Center Only One Hatch And Drive", DesiredAutoMode.CENTER_ONLY_ONE_POINT_FIVE_HATCH);
        desiredAutoModeChooser.addOption("Center Only Driver Assisted", DesiredAutoMode.DRIVER_ASSISTED_ONE_POINT_FIVE_HATCH);
        desiredAutoModeChooser.addOption("(Do Not Use Without Ian) Test", DesiredAutoMode.TEST_MODE);
        SmartDashboard.putData("Auto Mode", desiredAutoModeChooser);
    }

    public void update() {
        startingPosition = startingPositionChooser.getSelected();
        desiredAutoMode = desiredAutoModeChooser.getSelected();
    }
    
    public Optional<AutoMode> getSelectedAutoMode() {
        switch (desiredAutoMode) {
            case DRIVER_CONTROL:
                return Optional.of(new DriverControlMode());
            case ONE_HATCH_CARGO_SHIP_FRONT:
                return Optional.of(new OneHatchFrontCargoShipMode(startingPosition));
            case TWO_HATCH_CARGO_SHIP_FRONT:
                return Optional.of(new TwoHatchFrontCargoShipMode(startingPosition));
            case CENTER_ONLY_ONE_POINT_FIVE_HATCH:
                return Optional.of(new CenterOnlyOnePointFiveHatchMode());
            case DRIVER_ASSISTED_ONE_POINT_FIVE_HATCH:
                return Optional.of(new DriverAssistedCenterOnlyMode());
            case TEST_MODE:
                return Optional.of(new TestMode());
            default:
                return Optional.of(new DriverControlMode());
        }
    }

    public void reset() {
        startingPosition = null;
        desiredAutoMode = null;
    }

    public String[] getRawSelections() {
        return new String[] {
            startingPositionChooser.getSelected().name(),
            desiredAutoModeChooser.getSelected().name(),
        };
    }
}
