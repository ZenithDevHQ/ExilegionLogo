package com.exilegion.logo;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginBase;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Main plugin class for displaying the Exilegion server logo.
 * Uses MultipleHUD (via reflection) to allow the logo to coexist with other HUDs.
 */
public class ExilegionLogoPlugin extends JavaPlugin {

    private static final String HUD_IDENTIFIER = "exilegion_logo";
    private static ExilegionLogoPlugin instance;

    // Reflection cache for MultipleHUD
    private Class<?> multipleHudClass;
    private Method getInstanceMethod;
    private Method setCustomHudMethod;
    private Method hideCustomHudMethod;
    private boolean multipleHudAvailable = false;

    public static ExilegionLogoPlugin getInstance() {
        return instance;
    }

    public ExilegionLogoPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        super.setup();
        getLogger().at(Level.INFO).log("ExilegionLogo plugin setup complete");
    }

    @Override
    protected void start() {
        // Check if MultipleHUD plugin is loaded first
        PluginBase multipleHudPlugin = PluginManager.get().getPlugin(PluginIdentifier.fromString("Buuz135:MultipleHUD"));

        if (multipleHudPlugin != null) {
            // Plugin is loaded, now try to access the class
            initMultipleHudReflection();
        } else {
            getLogger().at(Level.WARNING).log("MultipleHUD plugin is not present - logo HUD may conflict with other HUDs");
        }

        if (!multipleHudAvailable) {
            getLogger().at(Level.SEVERE).log("MultipleHUD not available! ExilegionLogo requires MultipleHUD to function.");
            return;
        }

        // Register event listeners
        getEventRegistry().register(PlayerConnectEvent.class, this::onPlayerConnect);

        getLogger().at(Level.INFO).log("ExilegionLogo v%s enabled!", getManifest().getVersion());
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("ExilegionLogo disabled");
    }

    /**
     * Initializes reflection for MultipleHUD API calls.
     */
    private void initMultipleHudReflection() {
        try {
            multipleHudClass = Class.forName("com.buuz135.mhud.MultipleHUD");
            getInstanceMethod = multipleHudClass.getMethod("getInstance");

            setCustomHudMethod = multipleHudClass.getMethod("setCustomHud",
                Player.class, PlayerRef.class, String.class, CustomUIHud.class);
            hideCustomHudMethod = multipleHudClass.getMethod("hideCustomHud",
                Player.class, PlayerRef.class, String.class);

            multipleHudAvailable = true;
            getLogger().at(Level.INFO).log("MultipleHUD integration initialized");
        } catch (ClassNotFoundException e) {
            multipleHudAvailable = false;
            getLogger().at(Level.SEVERE).log("MultipleHUD plugin is loaded but the class cannot be accessed!");
        } catch (Exception e) {
            multipleHudAvailable = false;
            getLogger().at(Level.WARNING).withCause(e).log("Failed to initialize MultipleHUD integration");
        }
    }

    /**
     * Gets the MultipleHUD instance via reflection (called fresh each time).
     */
    private Object getMultipleHudInstance() {
        try {
            return getInstanceMethod.invoke(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Shows the Exilegion logo HUD when a player connects.
     */
    private void onPlayerConnect(PlayerConnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        Player player = event.getPlayer();

        if (player == null || !multipleHudAvailable) {
            return;
        }

        // Create and show the logo HUD using MultipleHUD via reflection
        ExilegionLogoHud logoHud = new ExilegionLogoHud(playerRef);

        try {
            Object mhudInstance = getMultipleHudInstance();
            if (mhudInstance != null) {
                setCustomHudMethod.invoke(mhudInstance, player, playerRef, HUD_IDENTIFIER, logoHud);
                getLogger().at(Level.FINE).log("Showing Exilegion logo to player: %s", playerRef.getUsername());
            }
        } catch (Exception e) {
            getLogger().at(Level.WARNING).withCause(e).log("Failed to show logo to player: %s", playerRef.getUsername());
        }
    }

    /**
     * Hides the logo HUD for a specific player.
     */
    public void hideLogoForPlayer(Player player, PlayerRef playerRef) {
        if (!multipleHudAvailable) return;

        try {
            Object mhudInstance = getMultipleHudInstance();
            if (mhudInstance != null) {
                hideCustomHudMethod.invoke(mhudInstance, player, playerRef, HUD_IDENTIFIER);
            }
        } catch (Exception e) {
            getLogger().at(Level.WARNING).withCause(e).log("Failed to hide logo");
        }
    }

    /**
     * Shows the logo HUD for a specific player.
     */
    public void showLogoForPlayer(Player player, PlayerRef playerRef) {
        if (!multipleHudAvailable) return;

        ExilegionLogoHud logoHud = new ExilegionLogoHud(playerRef);
        try {
            Object mhudInstance = getMultipleHudInstance();
            if (mhudInstance != null) {
                setCustomHudMethod.invoke(mhudInstance, player, playerRef, HUD_IDENTIFIER, logoHud);
            }
        } catch (Exception e) {
            getLogger().at(Level.WARNING).withCause(e).log("Failed to show logo");
        }
    }
}
