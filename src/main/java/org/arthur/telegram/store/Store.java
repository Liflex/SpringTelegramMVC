package org.arthur.telegram.store;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.arthur.telegram.user.TelegramUser;

import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
public class Store {
    private ConcurrentHashMap<Long, TelegramUser> userStore = new ConcurrentHashMap<>();
}
