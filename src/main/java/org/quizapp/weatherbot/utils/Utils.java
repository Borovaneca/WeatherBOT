package org.quizapp.weatherbot.utils;

public enum Utils {
    ;


    public static String getWeatherInfoURL(String city) {
        return "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + System.getenv("WEATHER_TOKEN") + "&units=metric";
    }
}
