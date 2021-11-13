package mint.modules.core;

import mint.Mint;
import mint.events.RenderOverlayEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.ColorSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.utils.Timer;

import java.awt.*;

@ModuleInfo(name = "Hud", category = Module.Category.Core, description = "Draws hud stuff")
public class Hud extends Module {
    public EnumSetting test = new EnumSetting("Test Enum", Test.One, this);

    public enum Test {One, two, three}

    static Hud INSTANCE = new Hud();
    public ColorSetting color = new ColorSetting("Color", new Color(-1), this);

    public Hud() {
        setInstance();
    }

    public static Hud getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Hud();
        return INSTANCE;
    }

    void setInstance() {
        INSTANCE = this;
    }

    Timer timer = new Timer();

    @Override
    public void onEnable() {
        if (test.getValueEnum().equals(Test.One))
            MessageManager.sendMessage(test.getValueEnum().toString() + ".");
    }

    @Override
    public void renderOverlayEvent(RenderOverlayEvent event) {
        assert Mint.hudComponentManager != null;
        Mint.hudComponentManager.drawText();
    }
}
