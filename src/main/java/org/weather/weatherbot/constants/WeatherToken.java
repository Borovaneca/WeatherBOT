package org.weather.weatherbot.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WeatherToken {

    public static String TOKEN;

    public WeatherToken(@Value("${WEATHER_API_TOKEN}") String token) {
        WeatherToken.TOKEN = token;
    }
}
