package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface InlineReplyMarkupPhotoBuild {

    InlineReplyMarkupPhotoBuild addInlineButton();

    InlineReplyMarkupPhotoBuild addButtonIn(InlineKeyboardButton inlineKeyboardButton);

    PhotoMessageBuild done();

}
