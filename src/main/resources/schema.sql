CREATE TABLE IF NOT EXISTS weather_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(100) NOT NULL,
    wmo_code VARCHAR(10) NOT NULL,
    air_temperature DOUBLE,
    wind_speed DOUBLE,
    weather_phenomenon VARCHAR(100),
    timestamp TIMESTAMP NOT NULL,
    INDEX idx_station_timestamp (station_name, timestamp)
    );

CREATE TABLE IF NOT EXISTS regional_base_fee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP,
    UNIQUE KEY uk_city_vehicle_valid_from (city, vehicle_type, valid_from)
    );

CREATE TABLE IF NOT EXISTS weather_extra_fee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fee_type VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    min_value DOUBLE,
    max_value DOUBLE,
    phenomenon_category VARCHAR(50),
    fee DECIMAL(10, 2) NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP,
    UNIQUE KEY uk_type_vehicle_value_valid_from (fee_type, vehicle_type, min_value, max_value, phenomenon_category, valid_from)
    );