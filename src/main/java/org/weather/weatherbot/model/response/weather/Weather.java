package org.weather.weatherbot.model.response.weather;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Weather {

    private int id;
    private String main;
    private String description;
    private String icon;
}
