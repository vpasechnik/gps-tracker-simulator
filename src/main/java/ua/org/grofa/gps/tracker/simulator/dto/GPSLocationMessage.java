package ua.org.grofa.gps.tracker.simulator.dto;

import java.math.BigDecimal;

public class GPSLocationMessage {
    private BigDecimal elevation;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private long time;

    public BigDecimal getElevation() {
        return elevation;
    }

    public void setElevation(BigDecimal elevation) {
        this.elevation = elevation;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof GPSLocationMessage)) {
            return false;
        }
        GPSLocationMessage message = (GPSLocationMessage) obj;
        return compareAtrtibutes(elevation, message.elevation)
                && compareAtrtibutes(latitude, message.latitude)
                && compareAtrtibutes(longitude, message.longitude)
                && compareAtrtibutes(time, message.time);
    }

    private <T> boolean compareAtrtibutes(T one, T another) {
        if (one == null) {
            return another == null;
        } else {
            return one.equals(another);
        }
    }
}
