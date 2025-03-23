package com.example.fooddeliveryfujitsu.util;

import com.example.fooddeliveryfujitsu.models.WeatherData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class WeatherXmlParser {

    private static final Logger logger = LoggerFactory.getLogger(WeatherXmlParser.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Set<String> TARGET_STATIONS = new HashSet<>();

    static {
        // Tallinn-Harku, Tartu-Tõravere, Pärnu
        TARGET_STATIONS.add("26038"); // Tallinn-Harku
        TARGET_STATIONS.add("26242"); // Tartu-Tõravere
        TARGET_STATIONS.add("41803"); // Pärnu
    }

    public List<WeatherData> parseWeatherData(InputStream xmlStream) {
        List<WeatherData> weatherDataList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlStream);
            document.getDocumentElement().normalize();

            String observationsTimestampStr = document.getDocumentElement().getAttribute("timestamp");
            logger.info("Raw timestamp from XML: {}", observationsTimestampStr);

            LocalDateTime timestamp;
            try {
                long unixTimestamp = Long.parseLong(observationsTimestampStr);
                timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp),
                        ZoneId.systemDefault());
                logger.info("Parsed Unix timestamp to: {}", timestamp);
            } catch (NumberFormatException e) {
                try {
                    timestamp = LocalDateTime.parse(observationsTimestampStr, TIMESTAMP_FORMATTER);
                    logger.info("Parsed string timestamp to: {}", timestamp);
                } catch (DateTimeParseException dtpe) {
                    logger.error("Failed to parse timestamp: {}", observationsTimestampStr, dtpe);
                    timestamp = LocalDateTime.now();
                    logger.info("Using current time as fallback: {}", timestamp);
                }
            }

            NodeList stationList = document.getElementsByTagName("station");
            logger.info("Found {} stations in XML", stationList.getLength());

            for (int i = 0; i < stationList.getLength(); i++) {
                Element stationElement = (Element) stationList.item(i);
                String wmoCode = getElementTextContent(stationElement, "wmocode");

                if (!TARGET_STATIONS.contains(wmoCode)) {
                    continue;
                }

                String stationName = getElementTextContent(stationElement, "name");
                Double airTemperature = parseDoubleOrNull(getElementTextContent(stationElement, "airtemperature"));
                Double windSpeed = parseDoubleOrNull(getElementTextContent(stationElement, "windspeed"));
                String phenomenon = getElementTextContent(stationElement, "phenomenon");

                logger.info("Processing station: {} ({}), temp: {}, wind: {}, phenomenon: {}",
                        stationName, wmoCode, airTemperature, windSpeed, phenomenon);

                WeatherData weatherData = new WeatherData(
                        stationName,
                        wmoCode,
                        airTemperature,
                        windSpeed,
                        phenomenon,
                        timestamp
                );

                weatherDataList.add(weatherData);
                logger.info("Added weather data: {}", weatherData);
            }

            logger.info("Total weather data records parsed: {}", weatherDataList.size());

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Error parsing weather XML data", e);
        }

        return weatherDataList;
    }

    private String getElementTextContent(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

    private Double parseDoubleOrNull(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse value as double: {}", value);
            return null;
        }
    }
}