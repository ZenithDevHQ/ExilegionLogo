package com.exilegion.logo;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

/**
 * Custom HUD that displays the Exilegion logo in the bottom-left corner.
 */
public class ExilegionLogoHud extends CustomUIHud {

    public ExilegionLogoHud(@NonNullDecl PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
        uiCommandBuilder.append("Hud/Logo/ExilegionLogo.ui");
    }
}
