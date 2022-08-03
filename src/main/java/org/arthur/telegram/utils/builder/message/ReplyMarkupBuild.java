package org.arthur.telegram.utils.builder.message;

public interface ReplyMarkupBuild {

    ReplyMarkupBuild addLineButton();

    ReplyMarkupBuild addButton(String text);

    ReplyMarkupBuild changeResize(boolean b);

    ReplyMarkupBuild changeOneTimeKeyboard(boolean b);

    MessageBuild done();

}
