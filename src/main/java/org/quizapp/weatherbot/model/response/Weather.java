package org.quizapp.weatherbot.model.response;

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
