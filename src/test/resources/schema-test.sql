CREATE TABLE IF NOT EXISTS weather_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_name VARCHAR(100) NOT NULL,
    wmo_code VARCHAR(10) NOT NULL,
    air_temperature DOUBLE,
    wind_speed DOUBLE,
    weather_phenomenon VARCHAR(100),
    timestamp TIMESTAMP NOT NULL
    );

-- Create index using H2 syntax
CREATE INDEX IF NOT EXISTS idx_station_timestamp ON weather_data(station_name, timestamp);

-- Regional base fee table
CREATE TABLE IF NOT EXISTS regional_base_fee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP,
    CONSTRAINT uk_city_vehicle_valid_from UNIQUE (city, vehicle_type, valid_from)
    );

-- Weather extra fee table
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
    CONSTRAINT uk_type_vehicle_value_valid_from UNIQUE (fee_type, vehicle_type, min_value, max_value, phenomenon_category, valid_from)
    );