package mint.modules.miscellaneous;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

@ModuleInfo(name = "Tab Tweaks", category = Module.Category.Miscellaneous, description = "Tweaks le tabbe.")
public class TabTweaks extends Module {
    static TabTweaks INSTANCE = new TabTweaks();
    public BooleanSetting pingDisplay = new BooleanSetting("Ping", false, this);
    public BooleanSetting coloredPing = new BooleanSetting("Colored", false, this);
    public IntegerSetting size = new IntegerSetting("Size", 250, 1, 1000, this);

    public TabTweaks() {
        this.setInstance();
    }

    //this getInstance shit is so chinese but the mixin needs this to be static :(
    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String name = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (getInstance().pingDisplay.getValue()) {
            if (getInstance().coloredPing.getValue()) {
                if (networkPlayerInfoIn.getResponseTime() <= 50)
                    return name + ChatFormatting.GREEN + " " + (getInstance().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");
                else if (networkPlayerInfoIn.getResponseTime() <= 100)
                    return name + ChatFormatting.GOLD + " " + (getInstance().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");
                else if (networkPlayerInfoIn.getResponseTime() <= 150)
                    return name + ChatFormatting.RED + " " + (getInstance().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");
                else if (networkPlayerInfoIn.getResponseTime() <= 1000)
                    return name + ChatFormatting.DARK_RED + " " + (getInstance().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");

            } else
                return name + ChatFormatting.GRAY + " " + (getInstance().pingDisplay.getValue() ? networkPlayerInfoIn.getResponseTime() : "");
        }
        return name;
    }

    public static TabTweaks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TabTweaks();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}

