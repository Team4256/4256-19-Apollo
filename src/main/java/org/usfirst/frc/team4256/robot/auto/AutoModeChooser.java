package org.usfirst.frc.team4256.robot.auto;

import java.util.Optional;

import org.usfirst.frc.team4256.robot.auto.modes.*;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoModeChooser {

    enum DesiredAutoMode {
        DRIVER_CONTROL,
        ONE_HATCH_CARGO_SHIP_FRONT,
        TWO_HATCH_CARGO_SHIP_FRONT,
        TEST_MODE;
    }

    private StartingPosition startingPosition = null;
    private DesiredAutoMode desiredAutoMode = null;

    private SendableChooser<StartingPosition> startingPositionChooser;
    private SendableChooser<DesiredAutoMode> desiredAutoModeChooser;

    public AutoModeChooser() {
        startingPositionChooser = new SendableChooser<>();
        startingPositionChooser.setDefaultOption("Left", StartingPosition.LEFT);
        startingPositionChooser.addOption("Center", StartingPosition.CENTER);
        startingPositionChooser.addOption("Right", StartingPosition.RIGHT);
        SmartDashboard.putData("Starting Position", startingPositionChooser);

        desiredAutoModeChooser = new SendableChooser<>();
        desiredAutoModeChooser.setDefaultOption("Driver Control", DesiredAutoMode.DRIVER_CONTROL);
        desiredAutoModeChooser.addOption("One Hatch Cargoship Front", DesiredAutoMode.ONE_HATCH_CARGO_SHIP_FRONT);
        desiredAutoModeChooser.addOption("Two Hatch Cargoship Front", DesiredAutoMode.TWO_HATCH_CARGO_SHIP_FRONT);
        desiredAutoModeChooser.addOption("Test", DesiredAutoMode.TEST_MODE);
        SmartDashboard.putData("Auto Mode", desiredAutoModeChooser);
    }

    public void update() {
        startingPosition = startingPositionChooser.getSelected();
        desiredAutoMode = desiredAutoModeChooser.getSelected();
    }
    
    public Optional<AutoMode> getSelectedAutoMode() {
        System.out.println("You Have Selected : StartingPosition (" + startingPositionChooser.getSelected().name() + ") : AutoMode (" + desiredAutoModeChooser.getSelected().name() + ")");

        switch (desiredAutoMode) {
            case DRIVER_CONTROL:
                return Optional.of(new DriverControlMode());
            case ONE_HATCH_CARGO_SHIP_FRONT:
                return Optional.of(new OneHatchFrontCargoShipMode(startingPosition));
            case TWO_HATCH_CARGO_SHIP_FRONT:
                return Optional.of(new TwoHatchFrontCargoShipMode(startingPosition));
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
