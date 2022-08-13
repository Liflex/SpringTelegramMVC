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
        Long telegramUserId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getFrom().getId();
        Long chatId = update.hasCallbackQuery() ? update.getCallbackQuery().getMessage().getChatId() : update.getMessage().getChatId();

        TelegramUser user = store.getUserStore().get(telegramUserId);
        if (user == null) {
            try {
                if (telegramUserCrudService == null || (user = telegramUserCrudService.findByTelegramIdTelegramUser(telegramUserId)) == null) {
                    user = new TelegramUser(telegramUserId);
                    user.setChatId(chatId);
                    if(!update.hasCallbackQuery()) {
                        User from = update.getMessage().getFrom();
                        user.setLocale(from.getLanguageCode());
                        user.setFirstName(from.getFirstName());
                        user.setLastName(from.getLastName());
                    }
                    user = telegramUserCrudService != null ? telegramUserCrudService.saveTelegramUser(user) : user;

                    String referralLink;
                    if (telegramUserReferralService != null && (referralLink = checkReferralLink(update)) != null) {
                        telegramUserReferralService.checkAndUpdateReferralStatistic(user, referralLink);
                    }
                }
            } catch (NoSuchBeanDefinitionException e) {
                log.info("Ok, you not implemented optional service, {}", e.getResolvableType());
            }
            store.getUserStore().put(telegramUserId, user);
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

    public TelegramUser getTelegramUserById(long id) {
        TelegramUser telegramUser = store.getUserStore().get(id);
        if(telegramUser == null) {
            telegramUser = telegramUserCrudService.findByTelegramIdTelegramUser(id);
            store.getUserStore().put(id, telegramUser);
        }
        return telegramUser;
    }

    public TelegramUser getCurrentTelegramUser() {
        return UserScope.getUser();
    }

    public void setProgress(boolean b) {
        UserScope.getUser().setInProgress(b);
    }
    public void setState(String state) {
        UserScope.getUser().setCurrentState(state);
    }
}
