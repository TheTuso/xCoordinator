package pl.tuso.coordinator.connection;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import pl.tuso.coordinator.XCoordinator;
import pl.tuso.coordinator.lettuce.messaging.Message;
import pl.tuso.coordinator.lettuce.messaging.MessagingListener;

import java.util.Optional;
import java.util.UUID;

public class ServerConnectionHandler implements MessagingListener {
    private final XCoordinator xCoordinator;

    public ServerConnectionHandler(XCoordinator xCoordinator) {
        this.xCoordinator = xCoordinator;
    }

    @Override
    public void action(@NotNull Message message) {
        if (!message.containsParam("player") || !message.containsParam("server")) return;
        Optional<Player> player = this.xCoordinator.getProxyServer().getPlayer(UUID.fromString(message.getParam("player")));
        Optional<RegisteredServer> server = this.xCoordinator.getProxyServer().getServer(message.getParam("server"));
        if (player.isEmpty() || server.isEmpty()) return;
        player.get().createConnectionRequest(server.get()).fireAndForget();
    }

    @Override
    public @NotNull String getType() {
        return "CONNECT";
    }
}
