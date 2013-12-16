package ua.org.grofa.gps.tracker.simulator.trackers;

import ua.org.grofa.gps.tracker.simulator.dto.GPSLocationMessage;

public class LogToConsoletrackerSimulator implements TrackerSimulator {

    public void sendGPSLocationMessage(GPSLocationMessage message) {
        System.out.println(String.format("time: %s, lat: %s, lon:%s, elev: %s",
                message.getTime(), message.getLatitude(),
                message.getLongitude(), message.getElevation()));
    }

}
