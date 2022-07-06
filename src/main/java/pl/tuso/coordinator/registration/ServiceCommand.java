package pl.tuso.coordinator.registration;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import pl.tuso.coordinator.XCoordinator;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@TestOnly
public class ServiceCommand implements SimpleCommand {
    private final XCoordinator xCoordinator;

    public ServiceCommand(XCoordinator xCoordinator) {
        this.xCoordinator = xCoordinator;
    }

    @Override
    public void execute(@NotNull Invocation invocation) {
        if (invocation.arguments().length != 3) {
            invocation.source().sendMessage(Component.text("This command doesn't exist, or does it?").color(TextColor.color(220, 38, 37)));
            return;
        }
        String serverName = invocation.arguments()[0];
        String hostName = invocation.arguments()[1];
        int port;
        if (!this.isIp(hostName)) {
            invocation.source().sendMessage(Component.text("Wrong hostname!").color(TextColor.color(220, 38, 37)));
            return;
        }
        if (this.isNumeric(invocation.arguments()[2])) {
            port = Integer.parseInt(invocation.arguments()[2]);
        } else {
            invocation.source().sendMessage(Component.text("Port must contain only numbers!").color(TextColor.color(220, 38, 37)));
            return;
        }
        ServerInfo serverInfo = new ServerInfo(serverName, new InetSocketAddress(hostName, port));
        this.xCoordinator.getProxyServer().registerServer(serverInfo);
        invocation.source().sendMessage(Component.text("Registered temporary server!").color(TextColor.color(133, 204, 21)));
        return;
    }

    @Override
    public List<String> suggest(@NotNull Invocation invocation) {
        List<String> arguments = new ArrayList<>();
        if (invocation.arguments().length == 2) {
            arguments.add("172.18.0.1"); // Node address
        }
        return arguments;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        return invocation.source().hasPermission("xcoordinator.service");
    }

    @Contract(pure = true)
    private boolean isNumeric(@NotNull String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isDecimal(@NotNull String string) {
        if (string.startsWith("0")) {
            return string.length() == 1;
        }
        for(char c : string.toCharArray()) {
            if(c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private boolean isIp(@NotNull String string) {
        String[] parts = string.split("\\.", -1);
        return parts.length == 4
                && Arrays.stream(parts)
                .filter(ServiceCommand::isDecimal)
                .map(Integer::parseInt)
                .filter(i -> i <= 255 && i >= 0)
                .count() == 4;
    }
}
