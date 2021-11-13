package mint.modules.core;

import mint.Mint;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.IntegerSetting;

@ModuleInfo(name = "Font", category = Module.Category.Core, description = "Changes the font.")
public class FontChanger extends Module {
    private static FontChanger INSTANCE = new FontChanger();
    public BooleanSetting override = new BooleanSetting("Full", false, this);
    public IntegerSetting fontSize = new IntegerSetting("Size", 17, 15, 20, this);
    public EnumSetting style = new EnumSetting("Style", Style.ITALICBOLD, this);

    public enum Style {NORMAL, ITALIC, BOLD, ITALICBOLD}

    private boolean reloadFont = false;

    public FontChanger() {
        setInstance();
    }

    public static FontChanger getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FontChanger();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onEnable() {
        reloadFont = true;
    }

    @Override
    public void onTick() {
        if (reloadFont) {
            Mint.textManager.init();
            reloadFont = false;
        }
    }
}

