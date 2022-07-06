package pl.tuso.coordinator;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import pl.tuso.coordinator.connection.ServerConnectionHandler;
import pl.tuso.coordinator.info.ServerInfoHandler;
import pl.tuso.coordinator.lettuce.messaging.MessagingService;
import pl.tuso.coordinator.players.AllPlayersHandler;
import pl.tuso.coordinator.players.UniqueIdHandler;
import pl.tuso.coordinator.registration.ServiceCommand;
import pl.tuso.coordinator.switcher.SwitchRequest;

import java.util.logging.Logger;

@Plugin(
        id = "xcoordinator",
        name = "xCoordinator",
        version = "1.0-SNAPSHOT",
        description = "A coordinating plugin.",
        authors = {"tuso"}
)
public class XCoordinator {
    private static XCoordinator xCoordinator;
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final RedisClient redisClient;
    private final MessagingService messagingService;
    private LuckPerms luckPerms;

    @Inject
    public XCoordinator(ProxyServer proxyServer, Logger logger) {
        xCoordinator = this;
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.redisClient = RedisClient.create(RedisURI.create("172.18.0.1", 6379));
        this.messagingService = new MessagingService(this);
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        if (this.proxyServer.getPluginManager().isLoaded("luckperms")) {
            this.luckPerms = LuckPermsProvider.get();
        }

        this.proxyServer.getEventManager().register(this, new SwitchRequest(this));

        this.getProxyServer().getCommandManager().register("service", new ServiceCommand(this));

        this.messagingService.registerListener(new ServerInfoHandler(this));
        this.messagingService.registerListener(new ServerConnectionHandler(this));
        this.messagingService.registerListener(new AllPlayersHandler(this));
        this.messagingService.registerListener(new UniqueIdHandler(this));
        ServerInfoHandler.provide();

        this.logger.info("Heyo! I'm ready to work!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        try {
            this.messagingService.terminate();
            this.redisClient.connect().sync().flushall();
            this.redisClient.shutdown();
        } catch (Exception exception) {
            // Ignore
        }
        this.logger.info("Bayo! Time to rest!");
    }

    public ProxyServer getProxyServer() {
        return this.proxyServer;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public RedisClient getRedisClient() {
        return this.redisClient;
    }

    public MessagingService getMessagingService() {
        return this.messagingService;
    }

    public LuckPerms getLuckPerms() {
        return this.luckPerms;
    }
}