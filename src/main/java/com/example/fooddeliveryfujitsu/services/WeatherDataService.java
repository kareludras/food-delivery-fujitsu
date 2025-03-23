package com.example.fooddeliveryfujitsu.services;


import com.example.fooddeliveryfujitsu.models.WeatherData;
import com.example.fooddeliveryfujitsu.repositories.WeatherDataRepository;
import com.example.fooddeliveryfujitsu.util.WeatherXmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WeatherDataService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherDataService.class);

    private static final Map<String, String> CITY_TO_STATION_MAP = new HashMap<>();

    static {
        CITY_TO_STATION_MAP.put("TALLINN", "Tallinn-Harku");
        CITY_TO_STATION_MAP.put("TARTU", "Tartu-Tõravere");
        CITY_TO_STATION_MAP.put("PARNU", "Pärnu");
    }

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    private final WeatherDataRepository weatherDataRepository;
    private final WeatherXmlParser weatherXmlParser;
    private final RestTemplate restTemplate;

    @Autowired
    public WeatherDataService(WeatherDataRepository weatherDataRepository,
                              WeatherXmlParser weatherXmlParser,
                              RestTemplate restTemplate) {
        this.weatherDataRepository = weatherDataRepository;
        this.weatherXmlParser = weatherXmlParser;
        this.restTemplate = restTemplate;
    }

    public void fetchAndSaveWeatherData() {
        logger.info("Starting manual weather data fetch from API: {}", weatherApiUrl);

        try {
            logger.info("Sending request to weather API");
            byte[] xmlBytes = restTemplate.getForObject(weatherApiUrl, byte[].class);

            if (xmlBytes == null || xmlBytes.length == 0) {
                logger.error("No data received from weather API");
                return;
            }

            logger.info("Received {} bytes of XML data", xmlBytes.length);

            List<WeatherData> weatherDataList = weatherXmlParser.parseWeatherData(new ByteArrayInputStream(xmlBytes));

            logger.info("Parsed {} weather data records", weatherDataList.size());
            for (WeatherData data : weatherDataList) {
                logger.info("Parsed data: station={}, temp={}, wind={}, phenomenon={}",
                        data.getStationName(), data.getAirTemperature(),
                        data.getWindSpeed(), data.getWeatherPhenomenon());
            }

            if (!weatherDataList.isEmpty()) {
                List<WeatherData> savedData = weatherDataRepository.saveAll(weatherDataList);
                logger.info("Saved {} weather data records to database", savedData.size());
            } else {
                logger.warn("No weather data parsed from the API response");
            }

        } catch (Exception e) {
            logger.error("Error fetching or processing weather data", e);
            e.printStackTrace();
        }
    }

    public Optional<WeatherData> getLatestWeatherDataForCity(String cityName) {
        String stationName = CITY_TO_STATION_MAP.get(cityName.toUpperCase());

        if (stationName == null) {
            logger.error("Unknown city: {}", cityName);
            return Optional.empty();
        }

        List<WeatherData> latestData = weatherDataRepository.findLatestByStationName(stationName);

        if (latestData.isEmpty()) {
            logger.warn("No weather data found for station: {}", stationName);
            return Optional.empty();
        }

        return Optional.of(latestData.get(0));
    }

    public Optional<WeatherData> getWeatherDataForCityAtTime(String cityName, LocalDateTime dateTime) {
        String stationName = CITY_TO_STATION_MAP.get(cityName.toUpperCase());

        if (stationName == null) {
            logger.error("Unknown city: {}", cityName);
            return Optional.empty();
        }

        return weatherDataRepository.findClosestByStationNameAndDateTime(stationName, dateTime);
    }
}
