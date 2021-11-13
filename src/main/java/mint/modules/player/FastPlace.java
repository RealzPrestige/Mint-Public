package mint.modules.player;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import net.minecraft.init.Items;

@ModuleInfo(name = "Fast Place", category = Module.Category.Player, description = "Allows you to do things faster.")
public class FastPlace extends Module {

    public BooleanSetting exp = new BooleanSetting("Exp", false, this);
    public BooleanSetting crystal = new BooleanSetting("Crystals", false, this);

    @Override
    public void onUpdate(){
        if (NullUtil.fullNullCheck())
            return;
        if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both) && exp.getValue())
            mc.rightClickDelayTimer = 0;
        if (InventoryUtil.heldItem(Items.END_CRYSTAL, InventoryUtil.Hand.Both) && crystal.getValue())
            mc.rightClickDelayTimer = 0;
    }
}
