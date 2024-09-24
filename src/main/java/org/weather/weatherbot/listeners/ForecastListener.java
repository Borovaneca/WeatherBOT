package org.weather.weatherbot.listeners;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.weather.weatherbot.model.response.forecast.ForecastResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.weather.weatherbot.utils.Utils.getHourlyForecastURL;

@Component
public class ForecastListener extends ListenerAdapter {

    @Autowired
    private Gson gson;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith("!forecast")) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            String city = args[1];
            if (args.length == 2) {
                event.getChannel().sendMessage("Please provide a valid number between 1 and 5").queue();
                return;
            }
            int days = Integer.parseInt(args[2]);

            if (days > 5) {
                event.getChannel().sendMessage("Sorry, I can only provide forecasts for up to 5 days.").queue();
                days = 5;
            }

            try {
                String forecastURL = getHourlyForecastURL(city);
                HttpURLConnection connection = (HttpURLConnection) new URL(forecastURL).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = in.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                in.close();

                ForecastResponse forecastResponse = gson.fromJson(response.toString(), ForecastResponse.class);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Weather Forecast for " + city);
                embed.setColor(Color.CYAN);

                List<ForecastResponse.Forecast> forecastList = forecastResponse.getList();
                List<ForecastResponse.Forecast> dailyForecasts = getMidDayForecasts(forecastList);

                for (int i = 0; i < Math.min(days, dailyForecasts.size()); i++) {
                    ForecastResponse.Forecast forecast = dailyForecasts.get(i);

                    String formattedWeather = getFormattedWeather(forecast);
                    String dayTitle = formatDayTitle(forecast.getDt());

                    embed.addField(dayTitle, formattedWeather, false);

                    String weatherIconUrl = getWeatherIconUrl(forecast.getWeather().get(0).getIcon());
                    embed.setThumbnail(weatherIconUrl);
                }

                event.getChannel().sendMessageEmbeds(embed.build()).queue();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getFormattedWeather(ForecastResponse.Forecast forecast) {
        ForecastResponse.Forecast.Main main = forecast.getMain();

        double temperature = main.getTemp() - 273.15;
        double minTemp = main.getTempMin() - 273.15;
        double maxTemp = main.getTempMax() - 273.15;
        int humidity = main.getHumidity();

        List<ForecastResponse.Forecast.Weather> weatherList = forecast.getWeather();
        String weatherCondition = weatherList.get(0).getMain();
        String weatherDescription = weatherList.get(0).getDescription();

        String weatherEmoji = getWeatherEmoji(weatherCondition);

        ForecastResponse.Forecast.Wind wind = forecast.getWind();
        double windSpeed = wind.getSpeed();

        return String.format(
                "%s **Weather**: %s (%s)\n" +
                        "🌡️ **Temperature**: %.1f°C (Min: %.1f°C, Max: %.1f°C)\n" +
                        "💧 **Humidity**: %d%%\n" +
                        "🌬️ **Wind Speed**: %.1f m/s",
                weatherEmoji, weatherCondition, weatherDescription,
                temperature, minTemp, maxTemp,
                humidity,
                windSpeed);
    }

    private String formatDayTitle(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
                .withZone(ZoneId.systemDefault());
        return formatter.format(instant);
    }

    public String getWeatherIconUrl(String iconCode) {
        return String.format("http://openweathermap.org/img/wn/%s@2x.png", iconCode);
    }

    public String getWeatherEmoji(String weatherCondition) {
        switch (weatherCondition.toLowerCase()) {
            case "clear":
                return "☀️";
            case "clouds":
                return "☁️";
            case "rain":
                return "🌧️";
            case "thunderstorm":
                return "⛈️";
            case "snow":
                return "❄️";
            case "mist":
            case "fog":
                return "🌫️";
            default:
                return "🌍";
        }
    }

    private List<ForecastResponse.Forecast> getMidDayForecasts(List<ForecastResponse.Forecast> forecasts) {
        List<ForecastResponse.Forecast> dailyForecasts = new ArrayList<>();
        LocalDate currentDay = null;

        for (ForecastResponse.Forecast forecast : forecasts) {
            Instant instant = Instant.ofEpochSecond(forecast.getDt());
            LocalDate forecastDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

            if (!forecastDate.equals(currentDay) && instant.atZone(ZoneId.systemDefault()).getHour() == 12) {
                dailyForecasts.add(forecast);
                currentDay = forecastDate;
            }
        }

        return dailyForecasts;
    }
}
