package org.usfirst.frc.team4256.robot.auto;

public class AutoModeExecutor {
    private AutoMode currentAutoMode;
    private Thread thread = null;

    public void setAutoMode(AutoMode autoMode) {
        currentAutoMode = autoMode;
        thread = new Thread() {
            public void run() {
                currentAutoMode.run();
            }
        };
    }

    public void start() {
        if (thread != null) {
            thread.start();
        }
    }

    public void stop() {
        if (currentAutoMode != null) {
            currentAutoMode.stop();
        }

        thread = null;
    }

    public AutoMode getAutoMode() {
        return currentAutoMode; 
    }
}
