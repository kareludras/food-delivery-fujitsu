package com.example.fooddeliveryfujitsu;

import com.example.fooddeliveryfujitsu.models.WeatherData;
import com.example.fooddeliveryfujitsu.util.WeatherXmlParser;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeatherXmlParserTest {

    private final WeatherXmlParser parser = new WeatherXmlParser();

    @Test
    void testParseWeatherDataWithValidXml() throws IOException {
        InputStream xmlStream = new ClassPathResource("sample-weather-data.xml").getInputStream();

        List<WeatherData> results = parser.parseWeatherData(xmlStream);

        assertFalse(results.isEmpty());
        assertEquals(3, results.size());
        assertTrue(results.stream().anyMatch(data -> "Tallinn-Harku".equals(data.getStationName())));
        assertTrue(results.stream().anyMatch(data -> "Tartu-Tõravere".equals(data.getStationName())));
        assertTrue(results.stream().anyMatch(data -> "Pärnu".equals(data.getStationName())));
    }

    @Test
    void testParseWeatherDataWithEmptyXml() throws IOException {
        InputStream xmlStream = new ClassPathResource("empty-weather-data.xml").getInputStream();

        List<WeatherData> results = parser.parseWeatherData(xmlStream);
        assertTrue(results.isEmpty());
    }

    @Test
    void testParseDoubleOrNull() throws Exception {
        Method parseDoubleMethod = WeatherXmlParser.class.getDeclaredMethod("parseDoubleOrNull", String.class);
        parseDoubleMethod.setAccessible(true);

        WeatherXmlParser parser = new WeatherXmlParser();

        assertEquals(10.5, parseDoubleMethod.invoke(parser, "10.5"));
        assertEquals(-5.2, parseDoubleMethod.invoke(parser, "-5.2"));
        assertNull(parseDoubleMethod.invoke(parser, (String)null));
        assertNull(parseDoubleMethod.invoke(parser, ""));
        assertNull(parseDoubleMethod.invoke(parser, "not-a-number"));
    }
}