package mint.modules.core;

import mint.Mint;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.newgui.NewGui;
import mint.settingsrewrite.impl.ColorSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "NewGui", category = Module.Category.Core, description = "Displays new Gui")
public class NewGuiModule extends Module {
    static NewGuiModule INSTANCE = new NewGuiModule();
    public ColorSetting color = new ColorSetting("Color", new Color(0, 255, 255, 255), this);
    public ColorSetting backgroundColor = new ColorSetting("Background Color", new Color(0, 0, 0, 50), this);

    public NewGuiModule() {
        setBind(Keyboard.KEY_O);
        this.setInstance();
    }

    public static NewGuiModule getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewGuiModule();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onEnable() {
        mc.displayGuiScreen(NewGui.getInstance());
    }

    @Override
    public void onDisable() {
        assert Mint.configManager != null;
        Mint.configManager.saveConfig("Default");
    }


    public void onUpdate() {
        if (!(mc.currentScreen instanceof NewGui))
            disable();
    }
}
