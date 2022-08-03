package org.arthur.telegram.bpp.container;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.arthur.telegram.TelegramPostProcessorConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.arthur.telegram.bfpp.UserScope;
import org.arthur.telegram.bpp.wrapper.BotApiMethodController;
import org.arthur.telegram.user.TelegramUser;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class BotApiMethodContainer {

    @Autowired
    @Lazy
    private ApplicationContext applicationContext;
    private final Map<Long, Map<String, BotApiMethodController>> controllerMap;

    public void addBotController(String path, BotApiMethodController controller) {
        long currentTelegramId = UserScope.getUser().getTelegramId();
        if(!controllerMap.containsKey(UserScope.getUser().getTelegramId())) {
            controllerMap.put(currentTelegramId, new HashMap<>());
        } else if(controllerMap.get(currentTelegramId).containsKey(path)) return;
        log.trace("add org.arthur.telegram bot controller for path: " +  path);
        controllerMap.get(currentTelegramId).put(path, controller);
    }

    public BotApiMethodController getBotApiMethodController(String path) {
        if(path == null || path.equals("null")) return null;
        TelegramUser currentUser = UserScope.getUser();
        BotApiMethodController botApiMethodController;
        Class<?> aClass = TelegramPostProcessorConfiguration.paths.get(path);
        if(aClass == null) {
            log.info("path not register " + path);
            throw new NullPointerException();
        }
        if(controllerMap.get(currentUser.getTelegramId()) == null || controllerMap.get(currentUser.getTelegramId()).get(path) == null) {
            applicationContext.getBean(aClass);
        }
        botApiMethodController = controllerMap.get(currentUser.getTelegramId()).get(path);
        return botApiMethodController;
    }

    public Map<Long, Map<String, BotApiMethodController>> getControllerMap () {
        return controllerMap;
    }

    private BotApiMethodContainer() {
        controllerMap = new HashMap<>();
    }
}