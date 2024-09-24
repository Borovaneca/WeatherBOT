package org.weather.weatherbot.utils;

public enum Utils {
    ;


    public static String getWeatherInfoURL(String city) {
        return "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + System.getenv("WEATHER_TOKEN") + "&units=metric";
    }

    public static String getDailyForecastURL(String city, int days) {
        String baseUrl = "https://api.openweathermap.org/data/2.5/forecast/daily";

        return String.format("%s?q=%s&cnt=%d&appid=%s", baseUrl, city, days, System.getenv("WEATHER_TOKEN"));
    }

    public static String getHourlyForecastURL(String city) {
        String baseUrl = "https://api.openweathermap.org/data/2.5/forecast";

        return String.format("%s?q=%s&appid=%s", baseUrl, city, System.getenv("WEATHER_TOKEN"));
    }
}