package org.arthur.telegram;

import org.arthur.telegram.bot.Bot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
public class LongPollingBot extends Bot {

    private static DefaultBotOptions defaultBotOptions = new DefaultBotOptions();
    private String token;
    private String name;

    public LongPollingBot(Pipe pipe, TelegramConfigurationProperties telegramConfigurationProperties) {
        super(pipe, defaultBotOptions);
        this.token = telegramConfigurationProperties.getToken();
        this.name = telegramConfigurationProperties.getName();
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
