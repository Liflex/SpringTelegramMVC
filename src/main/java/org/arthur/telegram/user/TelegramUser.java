package org.arthur.telegram.user;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TelegramUser {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    protected long id;
    protected long chatId;
    @NonNull
    protected long telegramId;
    protected int page;
    protected String locale;
    protected String currentState;
    protected String callbackData;

    protected String lastName;
    protected String firstName;
    protected String phone;

    protected String password;

    @Transient
    protected boolean authorized;

    /**
     * Необходимо самостоятельно следить за состоянием успеха выполнения.
     * В случае сочетания параметра {@link #inProgress}  = true и isSuccess = false
     * Вы ловите циклическую зависимость на предыдущем событии, на котором действие завершилось неудачей.
     * 16.04.21 UPD. В основном используется при валидации вводимых данных
     */
    protected boolean isSuccess = true;
    protected boolean inProgress;
    protected boolean isCallbackQuery;

    public TelegramUser(long chatId, @NonNull int telegramId) {
        this.chatId = chatId;
        this.telegramId = telegramId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelegramUser that = (TelegramUser) o;
        return chatId == that.chatId && telegramId == that.telegramId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, telegramId);
    }
}
