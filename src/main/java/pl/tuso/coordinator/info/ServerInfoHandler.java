package pl.tuso.coordinator.info;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import pl.tuso.coordinator.XCoordinator;
import pl.tuso.coordinator.lettuce.messaging.Message;
import pl.tuso.coordinator.lettuce.messaging.MessagingListener;

import java.util.Optional;

public class ServerInfoHandler implements MessagingListener {
    private static XCoordinator xCoordinator;

    public ServerInfoHandler(XCoordinator xCoordinator) {
        this.xCoordinator = xCoordinator;
    }

    @Override
    public void action(@NotNull Message message) {
        if (message.containsParam("name") || !message.containsParam("address") || !message.containsParam("port")) return;
        String address = message.getParam("address");
        int port = Integer.parseInt(message.getParam("port"));
        Optional<RegisteredServer> server = xCoordinator.getProxyServer().getAllServers()
                .stream().filter(
                registeredServer -> registeredServer.getServerInfo().getAddress().getHostString().equals(address)
                            && registeredServer.getServerInfo().getAddress().getPort() == port
        ).findFirst();
        if (server.isEmpty()) return;
        xCoordinator.getMessagingService().sendOutgoingMessage(
                new Message(message.getType())
                        .setParam("address", address)
                        .setParam("port", String.valueOf(port))
                        .setParam("name", server.get().getServerInfo().getName())
        );
    }

    @Override
    public @NotNull String getType() {
        return "SERVER_INFO";
    }

    public static void provide() {
        xCoordinator.getProxyServer().getAllServers().forEach(registeredServer -> {
            xCoordinator.getMessagingService().sendOutgoingMessage(new Message("SERVER_INFO")
            .setParam("address", registeredServer.getServerInfo().getAddress().getHostString())
            .setParam("port", String.valueOf(registeredServer.getServerInfo().getAddress().getPort()))
            .setParam("name", registeredServer.getServerInfo().getName()));
        });
    }
}
