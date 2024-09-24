package org.weather.weatherbot.model.response.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Coord {

    private double lon;
    private double lat;
}
