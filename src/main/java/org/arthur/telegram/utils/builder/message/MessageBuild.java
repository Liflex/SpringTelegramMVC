package org.arthur.telegram.utils.builder.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface MessageBuild {
    InlineReplyMarkupBuild addInlineReplyMarkup();
    ReplyMarkupBuild addReplyMarkup();
    MessageBuild setText(String text);
    SendMessage build();
}
