package mint.modules.miscellaneous;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.NullUtil;
import mint.utils.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

@ModuleInfo(name = "Hotbar Refiller", category = Module.Category.Miscellaneous, description = "Automatically completes stacks in ur fat hotbar.")
public class HotbarRefiller extends Module {

    public IntegerSetting delay = new IntegerSetting("Delay", 100, 0, 1000, this);
    public IntegerSetting fillPoint = new IntegerSetting("Fill Point", 50, 0, 64, this);
    public Timer timer = new Timer();
    public ArrayList<Item> hotbarItems = new ArrayList<>();

    public void onUpdate() {
        if (NullUtil.fullNullCheck() || mc.currentScreen != null)
            return;

        if (!timer.passedMs(delay.getValue()))
            return;

        for (int i = 0; i < 9; ++i)
            if (getFillableSlot(i))
                timer.reset();

    }

    public void onToggle() {
        hotbarItems.clear();
    }

    public void onEnable() {
        if (NullUtil.fullNullCheck())
            return;

        hotbarItems.clear();
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if(itemStack.isEmpty())
                hotbarItems.add(Items.AIR);
            if (!hotbarItems.contains(itemStack.getItem()))
                hotbarItems.add(itemStack.getItem());
        }
    }

    boolean getFillableSlot(int slot) {
        ItemStack itemStack = mc.player.inventory.getStackInSlot(slot);
        if (itemStack.isEmpty() || itemStack.getItem() == Items.AIR)
            return false;

        if (!itemStack.isStackable())
            return false;

        if (itemStack.getCount() >= itemStack.getMaxStackSize())
            return false;

        if (itemStack.getCount() >= fillPoint.getValue())
            return false;

        for (int i = 9; i < 36; ++i) {
            ItemStack inventoryItemStack = mc.player.inventory.getStackInSlot(i);
            if (!inventoryItemStack.isEmpty() && itemStack.getItem() == inventoryItemStack.getItem() && itemStack.getDisplayName().equals(inventoryItemStack.getDisplayName())) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                mc.playerController.updateController();
                return true;
            }
        }
        return false;
    }
}
