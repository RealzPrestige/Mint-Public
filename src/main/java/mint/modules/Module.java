package mint.modules;

import mint.events.ModuleToggleEvent;
import mint.events.RenderOverlayEvent;
import mint.events.RenderWorldEvent;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.KeySetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Module {
    public static Minecraft mc = Minecraft.getMinecraft();

    public String name = getModuleInfo().name();
    public String description = getModuleInfo().description();
    public Category category = getModuleInfo().category();
    public List<SettingRewrite> settings = new ArrayList<>();

    public KeySetting bind = new KeySetting("Keybind", Keyboard.KEY_NONE, this);

    public boolean isOpened = false;
    public BooleanSetting enabled = new BooleanSetting("Enabled",false,this, v -> false);

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onToggle() {
    }

    public void onLoad() {
    }

    public void onTick() {
    }

    public void onLogin() {
    }

    public void onLogout() {
    }

    public void onUpdate() {
    }

    public void renderOverlayEvent(RenderOverlayEvent event) {
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
    }

    public void enable() {
        enabled.setValue(Boolean.valueOf(String.valueOf(new String(ByteBuffer.wrap(String.valueOf(true).getBytes(StandardCharsets.UTF_8)).array()).toCharArray())));
        onToggle();
        onEnable();
        MinecraftForge.EVENT_BUS.post(new ModuleToggleEvent.Enable(this));
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        enabled.setValue(Boolean.valueOf(String.valueOf(new String(ByteBuffer.wrap(String.valueOf(false).getBytes(StandardCharsets.UTF_8)).array()).toCharArray())));
        onToggle();
        onDisable();
        MinecraftForge.EVENT_BUS.post(new ModuleToggleEvent.Disable(this));
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isEnabled() {
        return enabled.getValue();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.setValue(enabled);
    }

    public int getBind() {
        return this.bind.getKey();
    }

    public void setBind(int key) {
        this.bind.setValue(key);
    }

    public List<SettingRewrite> getSettings() {
        return settings;
    }

    public ModuleInfo getModuleInfo() {
        return getClass().getAnnotation(ModuleInfo.class);
    }

    public enum Category {
        Combat("Combat"),
        Miscellaneous("Miscellaneous"),
        Movement("Movement"),
        Player("Player"),
        Visual("Visual"),
        Core("Core");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}

