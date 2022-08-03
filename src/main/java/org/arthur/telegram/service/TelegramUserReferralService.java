package org.arthur.telegram.service;

import org.arthur.telegram.user.TelegramUser;

/**
 * Реализация пользователем интерфейса в случае необходимости
 * Сохранение новой рефералки при входе по ссылке
 */
public interface TelegramUserReferralService {

    void checkAndUpdateReferralStatistic(TelegramUser user, String referralLink);

}
