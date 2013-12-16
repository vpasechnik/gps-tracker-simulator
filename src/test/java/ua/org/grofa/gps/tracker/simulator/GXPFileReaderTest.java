package ua.org.grofa.gps.tracker.simulator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ua.org.grofa.gps.tracker.simulator.dto.GPSLocationMessage;
import ua.org.grofa.gps.tracker.simulator.trackers.TrackerSimulator;

public class GXPFileReaderTest {
    private static final double epsilon=0.00000001;
    @Test
    public void testStAX() throws Exception {
        TrackerSimulator mockedTrackerSimulator = mock(TrackerSimulator.class);
        InputStream fileStream = GXPFileReaderTest.class.getClassLoader()
                .getResourceAsStream("test-data-1.gpx ");
        GXPFileReader gxpFileReader = new GXPFileReader(fileStream);

        ArgumentCaptor<GPSLocationMessage> argumentCaptor = ArgumentCaptor
                .forClass(GPSLocationMessage.class);
        gxpFileReader.process(mockedTrackerSimulator);
        verify(mockedTrackerSimulator, times(3)).sendGPSLocationMessage(
                argumentCaptor.capture());

        List<GPSLocationMessage> points = argumentCaptor.getAllValues();
        Assert.assertEquals(10.00001001, points.get(0).getLatitude().doubleValue(),epsilon);
        Assert.assertEquals(10.00002001, points.get(1).getLatitude().doubleValue(),epsilon);
        Assert.assertEquals(10.00003002, points.get(2).getLatitude().doubleValue(),epsilon);
        Assert.assertEquals(-100.00040002, points.get(2).getLongitude().doubleValue(),epsilon);
        Assert.assertEquals(1001.3, points.get(2).getElevation().doubleValue(),epsilon);
    }
}
