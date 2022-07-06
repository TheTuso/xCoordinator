package pl.tuso.coordinator.players;

import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import pl.tuso.coordinator.XCoordinator;
import pl.tuso.coordinator.lettuce.messaging.Message;
import pl.tuso.coordinator.lettuce.messaging.MessagingListener;

import java.util.Optional;

public class UniqueIdHandler implements MessagingListener {
    private final XCoordinator xCoordinator;

    public UniqueIdHandler(XCoordinator xCoordinator) {
        this.xCoordinator = xCoordinator;
    }

    @Override
    public void action(@NotNull Message message) {
        if (!message.containsParam("username")) return;
        Optional<Player> user = this.xCoordinator.getProxyServer().getAllPlayers().stream()
                .filter(player -> player.getUsername().equals(message.getParam("username")))
                .findFirst();
        if (user.isEmpty()) return;
        this.xCoordinator.getMessagingService().sendOutgoingMessage(
                new Message(message.getType())
                        .setParam("uuid", user.get().getUniqueId().toString())
        );

    }

    @Override
    public @NotNull String getType() {
        return "UNIQUE_ID";
    }
}
