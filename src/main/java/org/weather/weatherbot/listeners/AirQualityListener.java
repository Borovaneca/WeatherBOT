package org.weather.weatherbot.listeners;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.weather.weatherbot.model.response.airquality.AirQualityResponse;
import org.weather.weatherbot.model.response.airquality.GeocodingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.weather.weatherbot.constants.WeatherToken.TOKEN;

@Component
public class AirQualityListener extends ListenerAdapter {


    @Autowired
    private Gson gson;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith("!airquality")) {
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            String city = args[1];

            try {
                String geoCodeURL = "http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + TOKEN;
                HttpURLConnection geoConnection = (HttpURLConnection) new URL(geoCodeURL).openConnection();
                geoConnection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(geoConnection.getInputStream()));
                StringBuilder geoResponse = new StringBuilder();
                String geoResponseLine;
                while ((geoResponseLine = in.readLine()) != null) {
                    geoResponse.append(geoResponseLine.trim());
                }
                in.close();

                GeocodingResponse[] geocodingResponse = gson.fromJson(geoResponse.toString(), GeocodingResponse[].class);
                double lat = geocodingResponse[0].getLat();
                double lon = geocodingResponse[0].getLon();

                String airQualityURL = "http://api.openweathermap.org/data/2.5/air_pollution?lat=" + lat + "&lon=" + lon + "&appid=" + TOKEN;
                HttpURLConnection connection = (HttpURLConnection) new URL(airQualityURL).openConnection();
                connection.setRequestMethod("GET");

                BufferedReader airIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder airResponse = new StringBuilder();
                String airResponseLine;
                while ((airResponseLine = airIn.readLine()) != null) {
                    airResponse.append(airResponseLine.trim());
                }
                airIn.close();

                AirQualityResponse airQualityResponse = gson.fromJson(airResponse.toString(), AirQualityResponse.class);

                AirQualityResponse.Main mainData = airQualityResponse.getList().get(0).getMain();
                AirQualityResponse.Components components = airQualityResponse.getList().get(0).getComponents();

                int aqi = mainData.getAqi();
                Color color;
                switch (aqi) {
                    case 1:
                        color = Color.GREEN;
                        break;
                    case 2:
                        color = Color.YELLOW;
                        break;
                    case 3:
                        color = Color.ORANGE;
                        break;
                    case 4:
                        color = Color.RED;
                        break;
                    case 5:
                        color = new Color(128, 0, 128);
                        break;
                    default:
                        color = Color.GRAY;
                        break;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Air Quality for " + city);
                embed.setColor(color);

                embed.addField("🌫️ PM2.5", components.getPm2_5() + " µg/m³", false);
                embed.addField("🌪️ PM10", components.getPm10() + " µg/m³", false);
                embed.addField("🌞 O3 (Ozone)", components.getO3() + " µg/m³", false);
                embed.addField("🏭 NO2 (Nitrogen Dioxide)", components.getNo2() + " µg/m³", false);
                embed.addField("🌋 SO2 (Sulfur Dioxide)", components.getSo2() + " µg/m³", false);
                embed.addField("🚗 CO (Carbon Monoxide)", components.getCo() + " µg/m³", false);

                event.getChannel().sendMessageEmbeds(embed.build()).queue();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
