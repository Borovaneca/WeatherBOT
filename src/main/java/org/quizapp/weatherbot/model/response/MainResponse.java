package org.quizapp.weatherbot.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MainResponse {

    private double temp;
    private double feels_like;
    private double temp_min;
    private double temp_max;
    private int pressure;
    private int humidity;
    private int sea_level;
    private int grnd_level;
}
