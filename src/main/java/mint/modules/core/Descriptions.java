package mint.modules.core;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;

@ModuleInfo(name = "Descriptions", category = Module.Category.Core, description = "Shows descriptions when you hover over a Module.")
public class Descriptions extends Module {
    private static Descriptions INSTANCE = new Descriptions();
    public EnumSetting mode = new EnumSetting("Mode", Mode.Hover, this);

    public enum Mode {Bottomleft, Hover}

    public BooleanSetting rect = new BooleanSetting("Rectangle", false, this);
    public BooleanSetting outline = new BooleanSetting("Outline", false, this);

    public Descriptions() {
        this.setInstance();
    }

    public static Descriptions getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Descriptions();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
