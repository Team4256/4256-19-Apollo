package org.usfirst.frc.team4256.robot.auto;

public class AutoModeExecutor {
    private AutoMode currentAutoMode;
    private Thread thread = null;

    /**
     * Queues up a new auto mode to be run whenever {@link #start()} is run
     */
    public void setAutoMode(AutoMode autoMode) {
        currentAutoMode = autoMode;
    }

    /**
     * Runs the selected <code>autoMode</code> set by {@link #setAutoMode(AutoMode)}
     * <p>
     * Note: You must set an <code>autoMode</code> before starting
     */
    public void start() {
        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (currentAutoMode != null) {
                        currentAutoMode.run();
                    } else {
                        System.out.println("Null Auto Mode...");
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * Stops the current <code>autoMode</code> if one is currently running
     */
    public void stop() {
        if (currentAutoMode != null) {
            currentAutoMode.stop();
        }

        thread = null;
    }

    /**
     * @return currently set <code>autoMode</code>
     */
    public AutoMode getAutoMode() {
        return currentAutoMode; 
    }
}
