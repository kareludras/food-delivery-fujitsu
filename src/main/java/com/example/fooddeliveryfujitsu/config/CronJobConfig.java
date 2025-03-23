package com.example.fooddeliveryfujitsu.config;

import com.example.fooddeliveryfujitsu.services.WeatherDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class CronJobConfig {

    private static final Logger logger = LoggerFactory.getLogger(CronJobConfig.class);

    private final WeatherDataService weatherDataService;

    //Constructor with lazy-loaded WeatherDataService to break circular dependency.
    @Autowired
    public CronJobConfig(@Lazy WeatherDataService weatherDataService) {
        this.weatherDataService = weatherDataService;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    // Scheduled task to fetch weather data.
    // Runs at 15 minutes past every hour by default.
    @Scheduled(cron = "${weather.cron.expression:0 15 * * * ?}")
    public void fetchWeatherData() {
        logger.info("Executing scheduled weather data fetch");
        weatherDataService.fetchAndSaveWeatherData();
    }
}