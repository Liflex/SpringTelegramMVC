package org.arthur.telegram.bfpp;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.arthur.telegram.user.TelegramUser;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Log4j2
public class UserScope implements Scope {
    public static final String SCOPE = "USER";
    private static final ThreadLocal<TelegramUser> USER = new ThreadLocal<>();

    private final Object lock = new Object();
    private final ConfigurableListableBeanFactory beanFactory;
    private final LoadingCache<String, ConcurrentHashMap<String, Object>> conversations;

    public UserScope(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        conversations = CacheBuilder
                .newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .removalListener(notification -> {
                    if (notification.wasEvicted()) {
                        log.debug("Evict session for key {}", notification.getKey());
                        Map<String, Object> userScope = (Map<String, Object>) notification.getValue();
                        if (userScope != null) {
                            userScope.values().forEach(this::removeBean);
                        }
                    }
                })
                .build(new CacheLoader<String, ConcurrentHashMap<String, Object>>() {
                    @Override
                    public ConcurrentHashMap<String, Object> load(@NonNull String key) {
                        log.debug("Create session for key = {}", key);
                        return new ConcurrentHashMap<>();
                    }
                });
    }

    public static TelegramUser getUser() {
        return USER.get();
    }

    public static void setUser(TelegramUser telegramUser) {
        USER.set(telegramUser);
    }

    @Override
    public @NotNull Object get(@NotNull String name, @NotNull ObjectFactory<?> objectFactory) {
        final String userId = getConversationId();
        if (userId != null) {
            final String userName = String.valueOf(getUser().getTelegramId());
            ConcurrentHashMap<String, Object> beans = conversations.getIfPresent(userId);
            if (beans == null) {
                synchronized (lock) {
                    beans = conversations.getIfPresent(userId);
                    if (beans == null) {
                        beans = new ConcurrentHashMap<>();
                        conversations.put(userId, beans);
                        log.debug("Bean storage for user '{}' is initialized", userName);
                    }
                }
            }
            Object bean = beans.get(name);
            if (bean == null) {
                bean = objectFactory.getObject();
                beans.put(name, bean);
                log.debug("Bean {} is created for user '{}'", bean, userName);
            }
            return bean;
        }
        //return null;
        throw new RuntimeException("There is no current user");
    }

    @Override
    public Object remove(@NotNull String name) {
        final String userId = getConversationId();
        if (userId != null) {
            final Map<String, Object> userBeans = conversations.getIfPresent(userId);
            if (userBeans != null) {
                return userBeans.remove(name);
            }
        }
        return null;
    }

    @Override
    public void registerDestructionCallback(@NotNull String name, @NotNull Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(@NotNull String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        final TelegramUser telegramUser = getUser();
        return telegramUser == null ? null : String.valueOf(telegramUser.getTelegramId());
    }

    public void removeConversation() {
        final String userId = getConversationId();
        if (userId != null) {
            final String userName = String.valueOf(getUser().getTelegramId());
            final Map<String, Object> beans = conversations.getIfPresent(userId);
            if (beans != null && !beans.isEmpty()) {
                beans.values().forEach(this::removeBean);
                synchronized (lock) {
                    conversations.invalidate(userId);
                    log.debug("Bean storage for user '{}' is invalidated", userName);
                }
            }
        }
    }

    private void removeBean(Object bean) {
        try {
            beanFactory.destroyBean(bean);
            log.debug("Bean destroy {0}", bean);
        } catch (Exception ex) {
            log.error("An error has occurred during destroying bean {}", bean, ex);
        }
    }
}