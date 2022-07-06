package pl.tuso.coordinator.lettuce.messaging;

import org.jetbrains.annotations.NotNull;

public interface MessagingListener {
    void action(@NotNull Message message);

    @NotNull String getType();
}
