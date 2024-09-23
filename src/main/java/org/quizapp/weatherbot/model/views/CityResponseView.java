package org.quizapp.weatherbot.model.views;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.quizapp.weatherbot.model.response.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class CityResponseView {
    private Coord coord;
    private List<Weather> weather;
    private String base;
    private MainResponse main;
    private Wind wind;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;
}
