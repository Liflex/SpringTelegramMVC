package org.arthur.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.arthur.telegram.bfpp.UserScope;
import org.arthur.telegram.filters.TelegramFilter;
import org.arthur.telegram.service.TelegramUserService;
import org.arthur.telegram.user.TelegramUser;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Pipe {

    private final List<TelegramFilter> logic;
    private final TelegramUserService userService;


    public List<PartialBotApiMethod<?>> line (Update update) {
        TelegramUser telegramUser;
        List<PartialBotApiMethod<?>> botApiMethod;

        if(update.hasCallbackQuery()) {
            telegramUser = updateUserStateWithCallback(update);
        } else {
            telegramUser = updateUserState(update);
        }
        UserScope.setUser(telegramUser);

        for (TelegramFilter telegramFilter : logic) {
            botApiMethod = telegramFilter.process(update, telegramUser);
            if(botApiMethod != null) return botApiMethod;
        }

        return null;
    }

    private TelegramUser updateUserState(Update update) {
        TelegramUser telegramUser = userService.getTelegramUser(update);
        telegramUser.setCallbackQuery(false);
        return telegramUser;
    }

    private TelegramUser updateUserStateWithCallback(Update update) {
        TelegramUser telegramUser = userService.getTelegramUser(update);
        telegramUser.setCallbackData(update.getCallbackQuery().getData());
        telegramUser.setCallbackQuery(true);
        return telegramUser;
    }

}
