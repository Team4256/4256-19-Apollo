package org.usfirst.frc.team4256.robot.auto.actions;

public interface Action {

    boolean isFinished();

    void update();

    void done();

    void start();
}
