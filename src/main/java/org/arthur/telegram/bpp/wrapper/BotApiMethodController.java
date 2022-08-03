package org.arthur.telegram.bpp.wrapper;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.arthur.telegram.bfpp.UserScope;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public abstract class BotApiMethodController {
    private Object bean;
    @Setter
    private Object keyboardBean;
    private Method method;
    @Setter
    private Method keyboard;
    private BotApiMethodController next;
    private BotApiMethodController prev;
    private boolean isOrdered = false;
    private boolean isInvoke = false;
    private String currentState;
    private Process processUpdate;

    public BotApiMethodController(Object bean, Method method, String currentState) {
        this.bean = bean;
        this.method = method;
        this.currentState = currentState;
        processUpdate = this::processFull;
    }

    public BotApiMethodController(Object bean,
                                  Method method,
                                  String currentState,
                                  boolean isOrdered) {
        this.bean = bean;
        this.method = method;
        this.isOrdered = true;
        this.currentState = currentState;
        processUpdate = this::processFull;
    }

    public BotApiMethodController getPrev() {
        return prev;
    }

    public void setPrev(BotApiMethodController prev) {
        this.prev = prev;
    }

    public void setNext(BotApiMethodController next) {
        this.next = next;
    }

    public abstract boolean successUpdatePredicate(Update update);

    public List<PartialBotApiMethod<?>> process(Update update) {
        if(!successUpdatePredicate(update)) return null;
        try {
            return processUpdate.accept(update);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("bad invoke method", e);
        }

        return null;
    }

    boolean typeListReturnDetect() {
        return List.class.equals(method.getReturnType());
    }

    private void processCheck() {
        if(isOrdered && UserScope.getUser().isSuccess()) {
            if(next == null && prev != null) {
                UserScope.getUser().setInProgress(false);
                if(next == null) reset();
            }
            isInvoke = true;
        }
        if(prev == null && next != null) {
            UserScope.getUser().setInProgress(true);
        }
        if(!UserScope.getUser().isInProgress()) {
            UserScope.getUser().setCurrentState(currentState);
        }

    }

    private List<PartialBotApiMethod<?>> processFull(Update update) throws InvocationTargetException, IllegalAccessException {
        if(isOrdered && isInvoke && next != null) return next.process(update);
        List<PartialBotApiMethod<?>> botApiMethods;
        if(typeListReturnDetect()) {
            botApiMethods = (List<PartialBotApiMethod<?>>) method.invoke(bean, update);
        } else {
            botApiMethods = new ArrayList<>();
            botApiMethods.add((PartialBotApiMethod) method.invoke(bean, update));
        }

        if(keyboard != null) {
            botApiMethods.add((PartialBotApiMethod<?>) keyboard.invoke(keyboardBean, update));
        }
        processCheck();
        return botApiMethods != null ? botApiMethods : new ArrayList<>(0);
    }

    private void reset () {
        isInvoke = false;
        if(prev != null) prev.reset();
    }

    private interface Process{
        List<PartialBotApiMethod<?>> accept(Update update) throws InvocationTargetException, IllegalAccessException;
    }
}
