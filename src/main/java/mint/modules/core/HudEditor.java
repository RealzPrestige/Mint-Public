package mint.modules.core;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.newgui.hud.HudWindow;

@ModuleInfo(name = "Hud Editor", category = Module.Category.Core, description = "Edits the hud ye")
public class HudEditor extends Module {

    public void onEnable() {
        mc.displayGuiScreen(HudWindow.getInstance());
        disable();
    }
}
