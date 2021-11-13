package mint;

import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import mint.commands.CommandManager;
import mint.managers.*;
import mint.newgui.hud.HudComponentManager;
import mint.settingsrewrite.SettingRewriteInitializer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.Display;

@Mod(modid = "mint", name = "Mint", version = "0.1.1")
public final class Mint {

    public Minecraft mc = Minecraft.getMinecraft();
    private final Logger LOGGER = LogManager.getLogger("Mint ExceptionCatcher: ");


    @NotNull
    public static Mint INSTANCE = new Mint();

    public static EventManager eventManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static TextManager textManager;
    public static ServerManager serverManager;
    public static HudComponentManager hudComponentManager;
    public static SettingRewriteInitializer settingsRewrite;
    private static boolean unloaded = false;

    public final Logger getLOGGER() {
        return this.LOGGER;
    }

    @EventHandler
    public final void init(FMLInitializationEvent event) {
        Display.setTitle("Mint 0.1.1");
        load();
    }

    public static void load() {
        unloaded = false;
        textManager = new TextManager();
        serverManager = new ServerManager();
        commandManager = new CommandManager();
        friendManager = new FriendManager();
        moduleManager = new ModuleManager();
        eventManager = new EventManager();
        configManager = new ConfigManager();
        hudComponentManager = new HudComponentManager();
        settingsRewrite = new SettingRewriteInitializer();
        ModuleManager var10000 = Mint.moduleManager;
        if (var10000 == null) {
            Intrinsics.throwNpe();
        }

        var10000.init();
        EventManager var1 = Mint.eventManager;
        if (var1 == null) {
            Intrinsics.throwNpe();
        }

        var1.init();
        TextManager var2 = Mint.textManager;
        if (var2 == null) {
            Intrinsics.throwNpe();
        }

        var2.init();
        ConfigManager var3 = Mint.configManager;
        if (var3 == null) {
            Intrinsics.throwNpe();
        }

        var3.init();
        var10000 = Mint.moduleManager;
        if (var10000 == null) {
            Intrinsics.throwNpe();
        }

        var10000.onLoad();
        HudComponentManager var4 = Mint.hudComponentManager;
        if (var4 == null) {
            Intrinsics.throwNpe();
        }

        var4.load();
    }


    public static void onUnload() {
        if (!Mint.unloaded) {
            EventManager var10000 = Mint.eventManager;
            if (var10000 == null) {
                Intrinsics.throwNpe();
            }

            var10000.onUnload();
            ModuleManager var7 = Mint.moduleManager;
            if (var7 == null) {
                Intrinsics.throwNpe();
            }

            var7.onUnload();
            ConfigManager var8 = Mint.configManager;
            if (var8 == null) {
                Intrinsics.throwNpe();
            }

            ConfigManager var10001 = Mint.configManager;
            Intrinsics.throwNpe();

            String var9 = var10001.config;
            Intrinsics.checkExpressionValueIsNotNull(var9, "configManager!!.config");
            CharSequence var1 = var9;
            String var2 = "mint/";
            Regex var5 = new Regex(var2);
            String var6 = "";
            var8.saveConfig(var5.replaceFirst(var1, var6));
            var7 = Mint.moduleManager;
            if (var7 == null) {
                Intrinsics.throwNpe();
            }

            var7.onUnloadPost();
            HudComponentManager var10 = Mint.hudComponentManager;
            if (var10 == null) {
                Intrinsics.throwNpe();
            }

            var10.unload();
            Mint.unloaded = true;
        }
    }
}
