package org.quizapp.weatherbot.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Wind {
    private double speed;
    private int deg;
    private double gust;
}
