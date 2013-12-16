package ua.org.grofa.gps.tracker.simulator;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import ua.org.grofa.gps.tracker.simulator.dto.GPSLocationMessage;
import ua.org.grofa.gps.tracker.simulator.trackers.TrackerSimulator;

public class GXPFileReader {
    private static final String XML_ELEMENT_NAME_ELEVATION = "ele";
    private static final String XML_ELEMENT_NAME_TIME = "time";
    private static final String XML_ELEMENT_NAME_POINT = "trkpt";
    private final DecimalFormat decimalFormat;
    private final QName latAttribute = new QName("lat");
    private final QName lonAttribute = new QName("lon");
    private final XMLInputFactory xmlInputFactory = XMLInputFactory
            .newInstance();
    private final XMLEventReader eventReader;
    private DateFormat xmlDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");
    private XMLEvent currentEvent;

    public GXPFileReader(InputStream inputStream) throws XMLStreamException {
        eventReader = xmlInputFactory.createXMLEventReader(inputStream);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("###0.#", symbols);
        decimalFormat.setParseBigDecimal(true);

    }

    public void process(TrackerSimulator tracker) throws XMLStreamException,
            ParseException {
        while (eventReader.hasNext()) {
            currentEvent = eventReader.nextEvent();
            if (currentEvent.isStartElement()) {
                StartElement startElement = currentEvent.asStartElement();
                if (XML_ELEMENT_NAME_POINT.equals(startElement.getName()
                        .getLocalPart())) {
                    processPoint(tracker, startElement);
                }
            }
        }
    }

    private void processPoint(TrackerSimulator tracker,
            StartElement currentElement) throws ParseException,
            XMLStreamException {
        GPSLocationMessage message = new GPSLocationMessage();
        message.setLatitude(parseAttributeAsBigDecimal(currentElement,
                latAttribute));
        message.setLongitude(parseAttributeAsBigDecimal(currentElement,
                lonAttribute));
        while (!(currentEvent.isEndElement() && XML_ELEMENT_NAME_POINT
                .equals(currentEvent.asEndElement().getName().getLocalPart()))) {
            currentEvent = eventReader.nextEvent();
            if (currentEvent.isStartElement()) {
                currentElement = currentEvent.asStartElement();
                processTime(currentElement, message);
                processElevation(currentElement, message);
            }
        }
        tracker.sendGPSLocationMessage(message);
    }

    private void processElevation(StartElement startElement,
            GPSLocationMessage message) throws XMLStreamException,
            ParseException {
        if (XML_ELEMENT_NAME_ELEVATION.equals(startElement.getName()
                .getLocalPart())) {
            String ele = eventReader.nextEvent().asCharacters().getData();
            BigDecimal elevation = (BigDecimal) decimalFormat.parse(ele);
            message.setElevation(elevation);
        }
    }

    private void processTime(StartElement startElement,
            GPSLocationMessage message) throws XMLStreamException,
            ParseException {
        if (XML_ELEMENT_NAME_TIME.equals(startElement.getName().getLocalPart())) {
            String time = eventReader.nextEvent().asCharacters().getData();
            Date date = xmlDateFormat.parse(time);
            message.setTime(date.getTime());
        }
    }

    private BigDecimal parseAttributeAsBigDecimal(StartElement element,
            QName attribute) throws ParseException {
        String valString = element.getAttributeByName(attribute).getValue();
        BigDecimal valueDecimal = (BigDecimal) decimalFormat.parse(valString);
        return valueDecimal;
    }
}
