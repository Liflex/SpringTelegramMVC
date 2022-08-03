package org.arthur.telegram.service;

import org.arthur.telegram.user.TelegramUser;

/**
 * Реализация пользователем интерфейса в случае необходимости
 * Сохранять и загружать пользователей с БД
 */
public interface TelegramUserCrudService {

    TelegramUser saveTelegramUser(TelegramUser telegramUser);

    TelegramUser findByTelegramIdTelegramUser(long telegramId);

}
