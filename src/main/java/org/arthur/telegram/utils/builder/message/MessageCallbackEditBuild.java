package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface MessageCallbackEditBuild {
    MessageCallbackEditBuild changeText(String text);
    MessageCallbackEditBuild changeButtonText(String text);
    EditMessageText buildEditMessage();
}
