package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;

public interface PhotoMessageCallbackEditBuild {
    PhotoMessageCallbackEditBuild changeText(String text);
    PhotoMessageCallbackEditBuild changeButtonText(String text);
    EditMessageCaption buildEditMessage();
}
