package ua.org.grofa.gps.tracker.simulator.trackers;

import ua.org.grofa.gps.tracker.simulator.dto.GPSLocationMessage;

public interface TrackerSimulator {
    void sendGPSLocationMessage(GPSLocationMessage message);
}
