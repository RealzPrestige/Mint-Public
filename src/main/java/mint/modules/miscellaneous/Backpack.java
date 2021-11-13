package mint.modules.miscellaneous;

import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.newgui.NewGui;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.ParentSetting;
import mint.utils.NullUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Backpack", category = Module.Category.Miscellaneous, description = "Manipulates container packets.")
public class Backpack extends Module {

    public ParentSetting containerParent = new ParentSetting("Containers", true, this);
    public BooleanSetting chest = new BooleanSetting("Chest", false, this, v -> containerParent.getValue());
    public BooleanSetting furnace = new BooleanSetting("Furnace", false, this, v -> containerParent.getValue());
    public BooleanSetting inventory = new BooleanSetting("Inventory", false, this, v -> containerParent.getValue());

    public ParentSetting miscParent =new ParentSetting("Misc", true, this);
    public BooleanSetting open = new BooleanSetting("Open", false, this, v -> miscParent.getValue());
    public BooleanSetting close = new BooleanSetting("Close", false, this, v -> miscParent.getValue());
    private GuiScreen cancelledGui = null;

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        if (e.getPacket() instanceof CPacketCloseWindow && close.getValue()) {
            e.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        if (mc.currentScreen instanceof GuiContainer && open.getValue()) {
            if (mc.currentScreen instanceof NewGui ||
                    //chest
                    mc.currentScreen instanceof GuiChest && !chest.getValue() ||
                    //furnace
                    mc.currentScreen instanceof GuiFurnace && !furnace.getValue() ||
                    //inventory
                    mc.currentScreen instanceof GuiInventory && !inventory.getValue()) {
                return;
            }
            cancelledGui = mc.currentScreen;
            mc.currentScreen = null;
        }
    }

    @Override
    public void onDisable() {
        if (!NullUtil.fullNullCheck() && cancelledGui != null && open.getValue()) {
            mc.displayGuiScreen(cancelledGui);
        }
        cancelledGui = null;
    }
}
