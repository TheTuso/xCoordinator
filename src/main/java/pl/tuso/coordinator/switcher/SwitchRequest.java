package pl.tuso.coordinator.switcher;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.util.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import pl.tuso.coordinator.XCoordinator;
import pl.tuso.coordinator.util.SwitcherUtil;

import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

@TestOnly
public class SwitchRequest {
    private final XCoordinator xCoordinator;

    public SwitchRequest(XCoordinator xCoordinator) {
        this.xCoordinator = xCoordinator;
    }

    @Subscribe
    public void onGameProfileRequest(@NotNull GameProfileRequestEvent event) throws ExecutionException, InterruptedException {
        String virtualHostStr = event.getConnection().getVirtualHost().map(InetSocketAddress::getHostString)
                .orElse("")
                .toLowerCase(Locale.ROOT);
        String extractedUsername = SwitcherUtil.getPlayerNameFromSubdomain(virtualHostStr);
        if (extractedUsername == null ||
                extractedUsername.equals(event.getUsername()) ||
                !extractedUsername.matches("^\\w{3,16}$")) return;
        if (!this.xCoordinator.getLuckPerms().getUserManager().loadUser(event.getOriginalProfile().getId()).get()
                .getCachedData().getPermissionData().checkPermission("xcoordinator.switch").asBoolean()) return;
        if (event.isOnlineMode()) {
            String playerOnlineUniqueID = SwitcherUtil.getOnlineUuid(extractedUsername.toLowerCase())
                    .flatMap(jsonObject -> SwitcherUtil.parseJson(jsonObject.get("id"), String.class)).orElse(null);
            String playerOnlinePlayerName = SwitcherUtil.getOnlineUuid(extractedUsername.toLowerCase())
                    .flatMap(jsonObject -> SwitcherUtil.parseJson(jsonObject.get("name"), String.class)).orElse(null);
            GameProfile profile;
            if (playerOnlineUniqueID != null || playerOnlinePlayerName != null) {
                profile = new GameProfile(playerOnlineUniqueID, playerOnlinePlayerName, ImmutableList.of());
                this.xCoordinator.getLogger().info(event.getUsername() + " is logging in as the user " + playerOnlinePlayerName);

            } else {
                profile = GameProfile.forOfflinePlayer(extractedUsername);
                this.xCoordinator.getLogger().info(event.getUsername() + " is logging in as the user " + extractedUsername);
            }
            event.setGameProfile(profile);
        } else {
            event.setGameProfile(GameProfile.forOfflinePlayer(extractedUsername));
            this.xCoordinator.getLogger().info(event.getUsername() + " is logging in as the user " + extractedUsername);
        }
    }
}
