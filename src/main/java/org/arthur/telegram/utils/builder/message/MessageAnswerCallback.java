package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class MessageAnswerCallback extends AnswerCallbackQuery {
    public MessageAnswerCallback(String text, boolean alert, CallbackQuery callbackQuery) {
        setText(text);
        setCallbackQueryId(callbackQuery.getId());
        setShowAlert(alert);
    }
}
