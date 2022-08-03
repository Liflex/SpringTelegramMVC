package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;

public interface PhotoMessageBuild {
    InlineReplyMarkupPhotoBuild addInlineReplyMarkup();
    ReplyMarkupPhotoMessageBuild addReplyMarkup();
    PhotoMessageBuild setText(String text);
    PhotoMessageBuild setPhoto(InputFile photo);
    PhotoMessageBuild setInlineMarkup(InlineKeyboardMarkup inlineMarkup);
    SendPhoto build();
}
