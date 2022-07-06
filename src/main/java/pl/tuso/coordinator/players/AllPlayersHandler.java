package pl.tuso.coordinator.players;

import com.google.gson.Gson;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import pl.tuso.coordinator.XCoordinator;
import pl.tuso.coordinator.lettuce.messaging.Message;
import pl.tuso.coordinator.lettuce.messaging.MessagingListener;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class AllPlayersHandler implements MessagingListener {
    private final XCoordinator xCoordinator;
    private final Gson gson;

    public AllPlayersHandler(XCoordinator xCoordinator) {
        this.xCoordinator = xCoordinator;
        this.gson = new Gson();
    }

    @Override
    public void action(@NotNull Message message) {
        Message feedback = new Message(message.getType());
        Collection<String> names;
        String serializedNames;
        if (message.containsParam("server")) {
            Optional<RegisteredServer> server = this.xCoordinator.getProxyServer().getServer(message.getParam("server"));
            if (server.isEmpty()) return;
            names = server.get().getPlayersConnected().stream().map(Player::getUsername).collect(Collectors.toList());

        } else {
            names = this.xCoordinator.getProxyServer().getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toList());
        }
        serializedNames = this.gson.toJson(names);
        this.xCoordinator.getMessagingService().sendOutgoingMessage(feedback.setParam("players", serializedNames));
    }

    @Override
    public @NotNull String getType() {
        return "ALL_PLAYERS";
    }
}
