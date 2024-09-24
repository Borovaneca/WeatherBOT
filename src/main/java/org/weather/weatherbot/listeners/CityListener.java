package org.weather.weatherbot.listeners;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.weather.weatherbot.model.views.CityResponseView;
import org.weather.weatherbot.utils.Utils;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class CityListener extends ListenerAdapter {

    private String city;
    private Gson gson = new Gson();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith("!weather")) return;

        city = event.getMessage().getContentRaw().split("\\s+")[1];

        try {

            HttpURLConnection connection = (HttpURLConnection) new URL(Utils.getWeatherInfoURL(city)).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine.trim());
            }
            in.close();
            CityResponseView cityResponseView = gson.fromJson(response.toString(), CityResponseView.class);

            double temp = cityResponseView.getMain().getTemp();
            double feelsLike = cityResponseView.getMain().getFeels_like();
            double minTemp = cityResponseView.getMain().getTemp_min();
            double maxTemp = cityResponseView.getMain().getTemp_max();
            int humidity = cityResponseView.getMain().getHumidity();

            String description = cityResponseView.getWeather().get(0).getDescription();
            String country = cityResponseView.getSys().getCountry();
            String windSpeed = String.valueOf(cityResponseView.getWind().getSpeed());

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("🌤️ Current Weather in " + city + ", " + country)
                    .setDescription("**Weather:** " + description)
                    .setColor(Color.CYAN)
                    .addField("🌡️ Temperature", temp + "°C", true)
                    .addField("🌬️ Feels Like", feelsLike + "°C", true)
                    .addField("📉 Min Temp", minTemp + "°C", true)
                    .addField("📈 Max Temp", maxTemp + "°C", true)
                    .addField("💧 Humidity", humidity + "%", true)
                    .addField("🍃 Wind Speed", windSpeed + " m/s", true)
                    .setFooter("Weather data provided by OpenWeather", null)
                    .setTimestamp(java.time.Instant.now());

            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
