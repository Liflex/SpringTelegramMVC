package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class Message implements InlineReplyMarkupBuild, MessageBuild, MessageCallbackEditBuild, ReplyMarkupBuild {
    private Update update;
    private SendMessage sendMessage;
    private EditMessageText editMessageText;
    private List<InlineKeyboardButton> currentInlineButton;
    private List<List<InlineKeyboardButton>> currentRowsInline;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
    private ReplyKeyboardMarkup replyKeyboardMarkup;
    private List<KeyboardRow> keyboardRowList;
    private KeyboardRow keyboardButtons;


    public static MessageBuild builderMessage(Update update) {
        Message message = new Message(update);
        return message;
    }

    public static MessageBuild builderMessage() {
        Message message = new Message();
        return message;
    }

    public static MessageCallbackEditBuild builderCallbackEdit(Update update) {
        return new Message(update, update.hasCallbackQuery());
    }


    private Message(Update update) {
        this.sendMessage = new SendMessage();
        if(update.hasCallbackQuery()) {
            this.sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        } else {
            this.sendMessage.setChatId(update.getMessage().getChatId());
        }
        this.sendMessage.setParseMode("Markdown");
    }

    private Message() {
        this.sendMessage = new SendMessage();
    }

    private Message(Update update, boolean hasCallbackQuery) {
        this.editMessageText = new EditMessageText();
        this.update = update;

        if(hasCallbackQuery)  {
            this.editMessageText.setReplyMarkup(this.update.getCallbackQuery().getMessage().getReplyMarkup());
            this.editMessageText.setText(this.update.getCallbackQuery().getMessage().getText());
            this.editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
            this.editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        } else {
            this.editMessageText.setReplyMarkup(this.update.getMessage().getReplyMarkup());
            this.editMessageText.setText(this.update.getMessage().getText());
            this.editMessageText.setChatId(update.getMessage().getChatId());
            this.editMessageText.setMessageId(update.getMessage().getMessageId());
        }


    }

    @Override
    public MessageBuild setText(String text) {
        this.sendMessage.setText(text);
        return this;
    }

    @Override
    public InlineReplyMarkupBuild addInlineReplyMarkup() {
        this.inlineKeyboardMarkup = new InlineKeyboardMarkup();
        this.currentRowsInline = new ArrayList<>();
        this.currentInlineButton = new ArrayList<>();
        this.inlineKeyboardMarkup.setKeyboard(this.currentRowsInline);
        this.currentRowsInline.add(this.currentInlineButton);
        this.sendMessage.setReplyMarkup(this.inlineKeyboardMarkup);
        return this;
    }

    @Override
    public ReplyMarkupBuild addReplyMarkup() {
        this.replyKeyboardMarkup = new ReplyKeyboardMarkup();
        this.replyKeyboardMarkup.setResizeKeyboard(true);
        this.replyKeyboardMarkup.setOneTimeKeyboard(true);
        this.keyboardRowList = new ArrayList<>();
        this.keyboardButtons = new KeyboardRow();
        this.keyboardRowList.add(this.keyboardButtons);
        this.replyKeyboardMarkup.setKeyboard(this.keyboardRowList);
        this.sendMessage.setReplyMarkup(this.replyKeyboardMarkup);
        return this;
    }

    @Override
    public ReplyMarkupBuild addLineButton() {
        this.keyboardButtons = new KeyboardRow();
        this.keyboardRowList.add(this.keyboardButtons);
        return this;
    }

    @Override
    public ReplyMarkupBuild addButton(String text) {
        this.keyboardButtons.add(text);
        return this;
    }

    @Override
    public ReplyMarkupBuild changeResize(boolean b) {
        this.replyKeyboardMarkup.setResizeKeyboard(b);
        return this;
    }

    @Override
    public ReplyMarkupBuild changeOneTimeKeyboard(boolean b) {
        this.replyKeyboardMarkup.setOneTimeKeyboard(b);
        return this;
    }

    @Override
    public InlineReplyMarkupBuild addInlineButton() {
        this.currentInlineButton = new ArrayList<>();
        this.currentRowsInline.add(this.currentInlineButton);
        return this;
    }

    @Override
    public InlineReplyMarkupBuild addButtonIn(InlineKeyboardButton inlineKeyboardButton) {
        this.currentInlineButton.add(inlineKeyboardButton);
        return this;
    }

    @Override
    public MessageBuild done() {
        return this;
    }

    @Override
    public MessageCallbackEditBuild changeText(String text) {
        this.editMessageText.setText(text);
        return this;
    }

    @Override
    public MessageCallbackEditBuild changeButtonText(String text) {
        InlineKeyboardMarkup replyMarkup = this.update.getCallbackQuery().getMessage().getReplyMarkup();
        replyMarkup.getKeyboard().forEach(kek -> kek.forEach(kek1 -> {
            if(kek1.getCallbackData().equals(this.update.getCallbackQuery().getData())) {
                kek1.setText(text);
            }
        }));
        return this;
    }

    @Override
    public EditMessageText buildEditMessage() {
        return this.editMessageText;
    }

    @Override
    public SendMessage build() {
        return this.sendMessage;
    }

    public static SendMessage getMainMenuMessage(Update update) {
        ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.forLanguageTag(update.getMessage().getFrom().getLanguageCode()));

        return Message.builderMessage(update)
                .setText("В процессе разработки")
                .addReplyMarkup()
                    .changeOneTimeKeyboard(false)
                    .addButton(messages.getString("state.getReferral.all"))
                    .addButton(messages.getString("state.getReferral.today"))
                    .addLineButton()
                    .addButton(messages.getString("state.getReferral.pay.success"))
                    .addLineButton()
                    .addButton(messages.getString("state.getReferral.pay.success.today"))
                    .addLineButton()
                    .addButton(messages.getString("state.getReferral"))
                    .done()
                .build();
    }

}
