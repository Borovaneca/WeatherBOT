package org.weather.weatherbot.model.response.weather;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Wind {
    private double speed;
    private int deg;
    private double gust;
}
