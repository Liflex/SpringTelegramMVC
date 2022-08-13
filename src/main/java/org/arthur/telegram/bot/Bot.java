package org.arthur.telegram.bot;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.arthur.telegram.Pipe;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.arthur.telegram.utils.builder.message.MessageAnswerCallback;

import java.util.List;


@Log4j2
public abstract class Bot extends TelegramLongPollingBot {

    private final Pipe pipe;

    public Bot(Pipe pipe, DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
        this.pipe = pipe;
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<PartialBotApiMethod<?>> line = pipe.line(update);
        for (PartialBotApiMethod<?> botApiMethod : line) {
            selectExecute(botApiMethod);
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        updates.forEach(x -> {
            List<PartialBotApiMethod<?>> line = pipe.line(x);
            try {
                for (PartialBotApiMethod<?> botApiMethod : line) {
                    selectExecute(botApiMethod);
                }
            } catch (NullPointerException ignore){}
        });
    }

    @SneakyThrows
    private void selectExecute(PartialBotApiMethod<?> method) {
        if(method != null) {
            if (method.getClass().equals(SendMessage.class)) {
                execute((SendMessage) method);
            } else if (method.getClass().equals(SendPhoto.class)) {
                execute((SendPhoto) method);
            } else if (method.getClass().equals(EditMessageCaption.class)) {
                execute((BotApiMethod<?>) method);
            } else if (method.getClass().equals(EditMessageMedia.class)) {
                execute((EditMessageMedia) method);
            } else if (method.getClass().equals(DeleteMessage.class)) {
                execute((DeleteMessage) method);
            } else if (method.getClass().equals(EditMessageText.class)) {
                execute((EditMessageText) method);
            } else if (method.getClass().equals(MessageAnswerCallback.class)) {
                execute((AnswerCallbackQuery) method);
            } else if (method.getClass().equals(SendDocument.class)) {
                execute((SendDocument) method);
            } else if (method.getClass().equals(AnswerCallbackQuery.class)) {
                execute((AnswerCallbackQuery) method);
            }
        }
    }
}
