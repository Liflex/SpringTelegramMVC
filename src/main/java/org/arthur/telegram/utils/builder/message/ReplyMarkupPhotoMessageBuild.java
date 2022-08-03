package org.arthur.telegram.utils.builder.message;

public interface ReplyMarkupPhotoMessageBuild {

    ReplyMarkupPhotoMessageBuild addLineButton();

    ReplyMarkupPhotoMessageBuild addButton(String text);

    ReplyMarkupPhotoMessageBuild changeResize(boolean b);

    ReplyMarkupPhotoMessageBuild changeOneTimeKeyboard(boolean b);

    PhotoMessageBuild done();

}
