package com.example.fooddeliveryfujitsu;

import com.example.fooddeliveryfujitsu.models.WeatherData;
import com.example.fooddeliveryfujitsu.repositories.WeatherDataRepository;
import com.example.fooddeliveryfujitsu.services.WeatherDataService;
import com.example.fooddeliveryfujitsu.util.WeatherXmlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WeatherDataServiceTest {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private WeatherXmlParser weatherXmlParser;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherDataService weatherDataService;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        now = LocalDateTime.now();

        try {
            java.lang.reflect.Field field = WeatherDataService.class.getDeclaredField("weatherApiUrl");
            field.setAccessible(true);
            field.set(weatherDataService, "https://test-api.example.com/weather");
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    void testFetchAndSaveWeatherData() {
        byte[] mockXmlData = "<observations>test data</observations>".getBytes();
        when(restTemplate.getForObject(anyString(), eq(byte[].class))).thenReturn(mockXmlData);

        WeatherData weatherData1 = new WeatherData("Tallinn-Harku", "26038", 5.0, 4.0, "Clear", now);
        WeatherData weatherData2 = new WeatherData("Tartu-Tõravere", "26242", 3.0, 3.0, "Cloudy", now);
        List<WeatherData> parsedData = Arrays.asList(weatherData1, weatherData2);

        when(weatherXmlParser.parseWeatherData(any(ByteArrayInputStream.class))).thenReturn(parsedData);
        when(weatherDataRepository.saveAll(parsedData)).thenReturn(parsedData);

        weatherDataService.fetchAndSaveWeatherData();

        verify(restTemplate).getForObject(anyString(), eq(byte[].class));
        verify(weatherXmlParser).parseWeatherData(any(ByteArrayInputStream.class));
        verify(weatherDataRepository).saveAll(parsedData);
    }

    @Test
    void testGetLatestWeatherDataForCity() {
        WeatherData weatherData = new WeatherData("Tallinn-Harku", "26038", 5.0, 4.0, "Clear", now);
        List<WeatherData> dataList = List.of(weatherData);

        when(weatherDataRepository.findLatestByStationName("Tallinn-Harku")).thenReturn(dataList);

        Optional<WeatherData> result = weatherDataService.getLatestWeatherDataForCity("TALLINN");

        assertTrue(result.isPresent());
        assertEquals("Tallinn-Harku", result.get().getStationName());
        verify(weatherDataRepository).findLatestByStationName("Tallinn-Harku");
    }

    @Test
    void testGetLatestWeatherDataForCityUnknownCity() {
        Optional<WeatherData> result = weatherDataService.getLatestWeatherDataForCity("UNKNOWN_CITY");

        assertTrue(result.isEmpty());
        verify(weatherDataRepository, never()).findLatestByStationName(anyString());
    }

    @Test
    void testGetWeatherDataForCityAtTime() {
        WeatherData weatherData = new WeatherData("Tartu-Tõravere", "26242", 3.0, 3.0, "Cloudy", now);

        when(weatherDataRepository.findClosestByStationNameAndDateTime(eq("Tartu-Tõravere"), eq(now)))
                .thenReturn(Optional.of(weatherData));

        Optional<WeatherData> result = weatherDataService.getWeatherDataForCityAtTime("TARTU", now);

        assertTrue(result.isPresent());
        assertEquals("Tartu-Tõravere", result.get().getStationName());
        verify(weatherDataRepository).findClosestByStationNameAndDateTime("Tartu-Tõravere", now);
    }
}