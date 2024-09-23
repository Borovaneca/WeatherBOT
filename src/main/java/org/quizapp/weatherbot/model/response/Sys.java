package org.quizapp.weatherbot.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sys {
    private int type;
    private int id;
    private String country;
    private long sunrise;
    private long sunset;
}
