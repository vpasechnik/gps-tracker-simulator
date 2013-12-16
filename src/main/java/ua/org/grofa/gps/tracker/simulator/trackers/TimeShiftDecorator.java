package ua.org.grofa.gps.tracker.simulator.trackers;

import java.util.Date;

import ua.org.grofa.gps.tracker.simulator.dto.GPSLocationMessage;

public class TimeShiftDecorator implements TrackerSimulator {
    private static final int DEFAULT_PAUSE_IN_MILISECONDS = 1000;
    private long lastTrackerTime = 0;
    private long lastMessageTime = 0;
    private TrackerSimulator decoratedSimulator;

    public TimeShiftDecorator(TrackerSimulator decoratedSimulator) {
        this.decoratedSimulator = decoratedSimulator;
    }

    public void sendGPSLocationMessage(GPSLocationMessage message) {
        long pause = DEFAULT_PAUSE_IN_MILISECONDS;
        long newMessageTime = message.getTime();
        if (lastMessageTime != 0 && newMessageTime > lastMessageTime) {
            pause = message.getTime() - lastMessageTime;
        }
        lastMessageTime = newMessageTime;
        if (lastTrackerTime != 0
                && new Date().getTime() < lastTrackerTime + pause) {
            try {
                Thread.sleep(pause);
            } catch (InterruptedException e) {
                // skip
            }
        }
        lastTrackerTime = new Date().getTime();
        message.setTime(lastTrackerTime);
        decoratedSimulator.sendGPSLocationMessage(message);
    }
}
