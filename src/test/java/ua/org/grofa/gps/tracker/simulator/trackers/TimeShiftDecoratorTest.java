package ua.org.grofa.gps.tracker.simulator.trackers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ua.org.grofa.gps.tracker.simulator.dto.GPSLocationMessage;

public class TimeShiftDecoratorTest {
    @Mock
    private TrackerSimulator decoratedSimulator;
    private TimeShiftDecorator decorator;
    private GPSLocationMessage message;

    @Before
    public void initTest() {
        MockitoAnnotations.initMocks(this);
        decorator = new TimeShiftDecorator(decoratedSimulator);
        message = new GPSLocationMessage();
    }

    @Test
    public void testDecorates() {
        decorator.sendGPSLocationMessage(message);
        verify(decoratedSimulator, only()).sendGPSLocationMessage(message);
    }

    @Test
    public void testFirstTimestampIsNow() {
        message.setTime(new GregorianCalendar(2000, 01, 01).getTimeInMillis());
        ArgumentCaptor<GPSLocationMessage> messageCaptor = ArgumentCaptor
                .forClass(GPSLocationMessage.class);
        long now = (new Date()).getTime();
        decorator.sendGPSLocationMessage(message);
        verify(decoratedSimulator, only()).sendGPSLocationMessage(
                messageCaptor.capture());
        assertTimestampsAreCloseEnough(now, messageCaptor.getValue().getTime());
    }

    @Test
    public void testTwoEmptyTimestampsMake1SecondDifference() {
        ArgumentCaptor<GPSLocationMessage> messageCaptor = ArgumentCaptor
                .forClass(GPSLocationMessage.class);
        long start = (new Date()).getTime();
        GPSLocationMessage message1 = new GPSLocationMessage();
        GPSLocationMessage message2 = new GPSLocationMessage();
        decorator.sendGPSLocationMessage(message1);
        long midde = (new Date()).getTime();
        decorator.sendGPSLocationMessage(message2);
        long end = (new Date()).getTime();
        verify(decoratedSimulator, times(2)).sendGPSLocationMessage(
                messageCaptor.capture());
        List<GPSLocationMessage> decoratedMessages = messageCaptor
                .getAllValues();
        long startByTracker = decoratedMessages.get(0).getTime();
        long endByTracker = decoratedMessages.get(1).getTime();
        assertTimestampsAreCloseEnough(start, startByTracker);
        assertTimestampsAreCloseEnough(start, midde);
        assertTimestampsAreCloseEnough(end, endByTracker);
        assertTimestampsAreCloseEnough(startByTracker + 1000, endByTracker);
    }

    @Test
    public void testTwoTimestampsHaveSameDifference() {
        ArgumentCaptor<GPSLocationMessage> messageCaptor = ArgumentCaptor
                .forClass(GPSLocationMessage.class);
        long start = (new Date()).getTime();
        GPSLocationMessage message1 = new GPSLocationMessage();
        message1.setTime(new GregorianCalendar(2012, 12, 12, 12, 12, 12)
                .getTimeInMillis());
        GPSLocationMessage message2 = new GPSLocationMessage();
        message2.setTime(new GregorianCalendar(2012, 12, 12, 12, 12, 14)
                .getTimeInMillis());
        decorator.sendGPSLocationMessage(message1);
        long midde = (new Date()).getTime();
        decorator.sendGPSLocationMessage(message2);
        long end = (new Date()).getTime();
        verify(decoratedSimulator, times(2)).sendGPSLocationMessage(
                messageCaptor.capture());
        List<GPSLocationMessage> decoratedMessages = messageCaptor
                .getAllValues();
        long startByTracker = decoratedMessages.get(0).getTime();
        long endByTracker = decoratedMessages.get(1).getTime();
        assertTimestampsAreCloseEnough(start, startByTracker);
        assertTimestampsAreCloseEnough(start, midde);
        assertTimestampsAreCloseEnough(end, endByTracker);
        assertTimestampsAreCloseEnough(startByTracker + 2000, endByTracker);
    }

    private void assertTimestampsAreCloseEnough(long timestamp1, long timestamp2) {
        assertTrue(String.format(
                "timestamps %s and %s are not close enough diff: %s",
                timestamp1, timestamp2, Math.abs(timestamp1 - timestamp2)),
                Math.abs(timestamp1 - timestamp2) < 50);
    }
}
