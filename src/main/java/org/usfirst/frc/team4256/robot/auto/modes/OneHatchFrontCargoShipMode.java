package org.usfirst.frc.team4256.robot.auto.modes;

import java.util.Arrays;

import org.usfirst.frc.team4256.robot.D_Swerve;
import org.usfirst.frc.team4256.robot.Robot;
import org.usfirst.frc.team4256.robot.auto.AutoMode;
import org.usfirst.frc.team4256.robot.auto.AutoModeEndedException;
import org.usfirst.frc.team4256.robot.auto.StartingPosition;
import org.usfirst.frc.team4256.robot.auto.actions.*;

public class OneHatchFrontCargoShipMode extends AutoMode {

    private final StartingPosition startingPosition;

    public OneHatchFrontCargoShipMode(final StartingPosition startingPosition) {
        this.startingPosition = startingPosition;
    }

    @Override
    public void done() {
        super.done();
    }

    @Override
    protected void routine() throws AutoModeEndedException {
        switch (startingPosition) {
            case LEFT:
                System.out.println("Running One Hatch Cargo Ship From Left");
                break;
            case CENTER:
                System.out.println("Running One Hatch Cargo Ship From Center");
                break;
            case RIGHT:
                System.out.println("Running One Hatch Cargo Ship From Right");
                break;
            default:
                System.out.println("There Was An Error...");
                System.out.println("But Ian Has Your Back And Is Running Front Cargo Ship Mode From The Left");
                break;
        }
    }
}
