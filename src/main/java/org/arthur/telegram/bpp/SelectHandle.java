package org.arthur.telegram.bpp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.arthur.telegram.bpp.container.BotApiMethodContainer;
import org.arthur.telegram.bpp.wrapper.BotApiMethodController;

@Component
@RequiredArgsConstructor
public class SelectHandle {
    private final BotApiMethodContainer container;

    public BotApiMethodController getApiMethodController(String path) {
        BotApiMethodController controller;
        controller = container.getBotApiMethodController(path);
        return controller;
    }
}
