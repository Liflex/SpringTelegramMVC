package org.arthur.telegram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.arthur.telegram.bfpp.UserScope;
import org.arthur.telegram.store.Store;
import org.arthur.telegram.user.TelegramUser;

/**
 * Вспомогательный сервис позволяющий работать с сущностью пользователя телеграм
 *
 * @see TelegramUser
 */

@Service
@RequiredArgsConstructor
@Log4j2
public class TelegramUserService {

    @Autowired(required = false)
    private TelegramUserCrudService telegramUserCrudService;

    @Autowired(required = false)
    private TelegramUserReferralService telegramUserReferralService;


    private final Store store;

    public TelegramUser getTelegramUser(Update update) {
        User telegramUser = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getFrom() : update.getMessage().getFrom();
        Long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();

        TelegramUser user = store.getUserStore().get(telegramUser.getId());
        if (user == null) {
            try {
                if (telegramUserCrudService == null || (user = telegramUserCrudService.findByTelegramIdTelegramUser(telegramUser.getId())) == null) {
                    user = new TelegramUser(telegramUser.getId());
                    user.setLocale(telegramUser.getLanguageCode());
                    user.setChatId(chatId);
                    user.setFirstName(telegramUser.getFirstName());
                    user.setLastName(telegramUser.getLastName());
                    user = telegramUserCrudService != null ? telegramUserCrudService.saveTelegramUser(user) : user;

                    String referralLink;
                    if (telegramUserReferralService != null && (referralLink = checkReferralLink(update)) != null) {
                        telegramUserReferralService.checkAndUpdateReferralStatistic(user, referralLink);
                    }
                }
            } catch (NoSuchBeanDefinitionException e) {
                log.info("Ok, you not implemented optional service, {}", e.getResolvableType());
            }
            store.getUserStore().put(telegramUser.getId(), user);
        }
        return user;
    }

    private String checkReferralLink(Update update) {
        String link;
        if((link = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getText() : update.getMessage().getText()) != null) {
            String[] split = link.split(" ");
            if(split.length > 1) {
                return split[1];
            }
        }
        return null;
    }

    public TelegramUser getCurrentTelegramUser() {
        return UserScope.getUser();
    }
}
