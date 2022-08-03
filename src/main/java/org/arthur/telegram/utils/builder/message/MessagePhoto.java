package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessagePhoto implements InlineReplyMarkupPhotoBuild, ReplyMarkupPhotoMessageBuild, PhotoMessageCallbackEditBuild, PhotoMessageBuild {
    private Update update;
    private SendPhoto sendPhoto;
    private EditMessageCaption editMessageText;
    private List<InlineKeyboardButton> currentInlineButton;
    private List<List<InlineKeyboardButton>> currentRowsInline;
    private InlineKeyboardMarkup inlineKeyboardMarkup;
    private ReplyKeyboardMarkup replyKeyboardMarkup;
    private List<KeyboardRow> keyboardRowList;
    private KeyboardRow keyboardButtons;


    public static PhotoMessageBuild buildPhotoMessage(Update update) {
        return new MessagePhoto(update);
    }

    public static PhotoMessageCallbackEditBuild builderCallbackEdit(Update update) {
        return new MessagePhoto(update, update.hasCallbackQuery());
    }


    private MessagePhoto(Update update) {
        this.sendPhoto = new SendPhoto();
        if(update.hasCallbackQuery()) {
            this.sendPhoto.setChatId(update.getCallbackQuery().getMessage().getChatId());
        } else {
            this.sendPhoto.setChatId(update.getMessage().getChatId());
        }
    }

    private MessagePhoto(Update update, boolean hasCallbackQuery) {
        if(!hasCallbackQuery) throw new UnsupportedOperationException("callback not found");
        this.editMessageText = new EditMessageCaption();
        this.update = update;
        this.editMessageText.setReplyMarkup(this.update.getCallbackQuery().getMessage().getReplyMarkup());
        this.editMessageText.setCaption(this.update.getCallbackQuery().getMessage().getText());
        this.editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        this.editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        this.editMessageText.setParseMode("Markdown");
    }

    @Override
    public PhotoMessageBuild setText(String text) {
        this.sendPhoto.setCaption(text);
        return this;
    }

    @Override
    public PhotoMessageBuild setPhoto(InputFile photo) {
        this.sendPhoto.setPhoto(photo);
        return this;
    }

    @Override
    public PhotoMessageBuild setInlineMarkup(InlineKeyboardMarkup inlineMarkup) {
        this.sendPhoto.setReplyMarkup(inlineMarkup);
        return this;
    }

    @Override
    public InlineReplyMarkupPhotoBuild addInlineReplyMarkup() {
        this.inlineKeyboardMarkup = new InlineKeyboardMarkup();
        this.currentRowsInline = new ArrayList<>();
        this.currentInlineButton = new ArrayList<>();
        this.inlineKeyboardMarkup.setKeyboard(this.currentRowsInline);
        this.currentRowsInline.add(this.currentInlineButton);
        this.sendPhoto.setReplyMarkup(this.inlineKeyboardMarkup);
        return this;
    }

    @Override
    public ReplyMarkupPhotoMessageBuild addReplyMarkup() {
        this.replyKeyboardMarkup = new ReplyKeyboardMarkup();
        this.replyKeyboardMarkup.setResizeKeyboard(true);
        this.replyKeyboardMarkup.setOneTimeKeyboard(true);
        this.keyboardRowList = new ArrayList<>();
        this.keyboardButtons = new KeyboardRow();
        this.keyboardRowList.add(this.keyboardButtons);
        this.replyKeyboardMarkup.setKeyboard(this.keyboardRowList);
        this.sendPhoto.setReplyMarkup(this.replyKeyboardMarkup);
        return this;
    }

    @Override
    public ReplyMarkupPhotoMessageBuild addLineButton() {
        this.keyboardButtons = new KeyboardRow();
        this.keyboardRowList.add(this.keyboardButtons);
        return this;
    }

    @Override
    public ReplyMarkupPhotoMessageBuild addButton(String text) {
        this.keyboardButtons.add(text);
        return this;
    }

    @Override
    public ReplyMarkupPhotoMessageBuild changeResize(boolean b) {
        this.replyKeyboardMarkup.setResizeKeyboard(b);
        return this;
    }

    @Override
    public ReplyMarkupPhotoMessageBuild changeOneTimeKeyboard(boolean b) {
        this.replyKeyboardMarkup.setOneTimeKeyboard(b);
        return this;
    }

    @Override
    public InlineReplyMarkupPhotoBuild addInlineButton() {
        this.currentInlineButton = new ArrayList<>();
        this.currentRowsInline.add(this.currentInlineButton);
        return this;
    }

    @Override
    public InlineReplyMarkupPhotoBuild addButtonIn(InlineKeyboardButton inlineKeyboardButton) {
        this.currentInlineButton.add(inlineKeyboardButton);
        return this;
    }

    @Override
    public PhotoMessageBuild done() {
        return this;
    }

    @Override
    public PhotoMessageCallbackEditBuild changeText(String text) {
        this.editMessageText.setCaption(text);
        return this;
    }

    @Override
    public PhotoMessageCallbackEditBuild changeButtonText(String text) {
        InlineKeyboardMarkup replyMarkup = this.update.getCallbackQuery().getMessage().getReplyMarkup();
        replyMarkup.getKeyboard().forEach(kek -> kek.forEach(kek1 -> {
            if(kek1.getCallbackData().equals(this.update.getCallbackQuery().getData())) {
                kek1.setText(text);
            }
        }));
        return this;
    }

    @Override
    public EditMessageCaption buildEditMessage() {
        return this.editMessageText;
    }

    @Override
    public SendPhoto build() {
        return this.sendPhoto;
    }

}
