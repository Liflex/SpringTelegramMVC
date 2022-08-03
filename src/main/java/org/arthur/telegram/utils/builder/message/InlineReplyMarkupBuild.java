package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface InlineReplyMarkupBuild {

    InlineReplyMarkupBuild addInlineButton();

    InlineReplyMarkupBuild addButtonIn(InlineKeyboardButton inlineKeyboardButton);

    MessageBuild done();

}
