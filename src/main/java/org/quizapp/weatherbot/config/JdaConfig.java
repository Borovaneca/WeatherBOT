package org.quizapp.weatherbot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.quizapp.weatherbot.listeners.CityListener;
import org.quizapp.weatherbot.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdaConfig {

    @Value("${jda.api.token}")
    private String token;

    @Bean
    public JDA startJda(CityListener listener) {
        return JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(listener)
                .build();
    }
}
