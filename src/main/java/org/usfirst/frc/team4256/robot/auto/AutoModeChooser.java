package org.usfirst.frc.team4256.robot.auto;

import org.usfirst.frc.team4256.robot.auto.modes.OneHatchFrontCargoShipMode;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoModeChooser {

    enum DesiredAutoMode {
        DRIVER_CONTROL,
        ONE_HATCH_CARGO_SHIP_FRONT,
        TWO_HATCH_CARGO_SHIP_FRONT;
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
        SmartDashboard.putData("Auto Mode", desiredAutoModeChooser);
    }

    public AutoMode getSelectedAutoMode() {
        startingPosition = startingPositionChooser.getSelected();
        desiredAutoMode = desiredAutoModeChooser.getSelected();
        
        System.out.println("You Have Selected : StartingPosition (" + startingPositionChooser.getSelected().name() + ") : AutoMode (" + desiredAutoModeChooser.getSelected().name() + ")");

        switch (desiredAutoMode) {
            case DRIVER_CONTROL:
                return new OneHatchFrontCargoShipMode(startingPosition);
            case ONE_HATCH_CARGO_SHIP_FRONT:
                return new OneHatchFrontCargoShipMode(startingPosition);
            case TWO_HATCH_CARGO_SHIP_FRONT:
                return new OneHatchFrontCargoShipMode(startingPosition);
            default:
                return new OneHatchFrontCargoShipMode(startingPosition);
        }
    }

    public String[] getRawSelections() {
        return new String[] {
            startingPositionChooser.getSelected().name(),
            desiredAutoModeChooser.getSelected().name(),
        };
    }

    public void reset() {
        startingPosition = null;
        desiredAutoMode = null;
    }

}
