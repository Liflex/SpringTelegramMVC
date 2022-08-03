package org.arthur.telegram.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.arthur.telegram.bpp.SelectHandle;
import org.arthur.telegram.user.TelegramUser;

import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class LogicFilter implements TelegramFilter {

    private final SelectHandle selectHandle;

    @Override
    public List<PartialBotApiMethod<?>> process(Update update, TelegramUser telegramUser) {
        try {
            if (telegramUser.isInProgress()) {
                return getPartialBotApiMethodsInProgress(update, telegramUser);
            } else {
                return getPartialBotApiMethods(update);
            }
        } catch (NullPointerException e) {
            stateNotFoundExceptionHandler(update, telegramUser);
        }
        return null;
    }

    private List<PartialBotApiMethod<?>> getPartialBotApiMethods(Update update) {
        if (update.hasCallbackQuery()) {
            return getProcessWithCallback(update, update.getCallbackQuery().getData());
        } else {
            return getProcess(update);
        }
    }

    private List<PartialBotApiMethod<?>> getPartialBotApiMethodsInProgress(Update update, TelegramUser telegramUser) {
        if (telegramUser.isCallbackQuery()) {
            return getProcessWithCallback(update, telegramUser.getCallbackData());
        } else {
            return getProcessInProgress(update, telegramUser);
        }
    }

    private List<PartialBotApiMethod<?>> getProcessInProgress(Update update, TelegramUser telegramUser) {
        return selectHandle.getApiMethodController(telegramUser.getCurrentState()).process(update);
    }

    private List<PartialBotApiMethod<?>> getProcessWithCallback(Update update, String data) {
        return selectHandle.getApiMethodController(data.split("_")[0]).process(update);
    }

    private List<PartialBotApiMethod<?>> getProcess(Update update) {
        String method = update.getMessage().getText();
        String[] split = method.split(" ");
        if(split.length > 1) method = split[0];
        return selectHandle.getApiMethodController(method).process(update);
    }

    private void stateNotFoundExceptionHandler(Update update, TelegramUser telegramUser) {
        log.info("handle " + (update.hasCallbackQuery() ? update.getCallbackQuery().getData() : update.getMessage().getText()) + " not found, user " + telegramUser.getTelegramId());
    }
}
