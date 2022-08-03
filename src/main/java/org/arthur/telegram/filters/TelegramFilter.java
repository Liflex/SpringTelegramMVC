package org.arthur.telegram.filters;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.arthur.telegram.user.TelegramUser;

import java.util.List;

public interface TelegramFilter {
    List<PartialBotApiMethod<?>> process (Update update, TelegramUser telegramUser);
}
