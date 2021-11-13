package mint.utils;

import mint.Mint;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int getItemSlot(Item item) {
        int itemSlot = -1;
        for (int i = 45; i > 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }

    public static int getStackCount(Item item) {
        int count = 0;
        for (int size = mc.player.inventory.mainInventory.size(), i = 0; i < size; ++i) {
            final ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
            if (itemStack.getItem() == item) {
                count += itemStack.getCount();
            }
        }
        final ItemStack offhandStack = mc.player.getHeldItemOffhand();
        if (offhandStack.getItem() == item) {
            count += offhandStack.getCount();
        }
        return count;
    }

    public static int getItemFromHotbar(final Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Mint.INSTANCE.mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                slot = i;
            }
        }
        return slot;
    }


    public static void SilentSwitchToSlot(int slot) {
        if (Mint.INSTANCE.mc.player.inventory.currentItem == slot || slot == -1) {
            return;
        }
        Mint.INSTANCE.mc.player.inventory.currentItem = slot;
        Mint.INSTANCE.mc.playerController.updateController();
    }

    public static void switchToSlot(int slot) {
        if (Mint.INSTANCE.mc.player.inventory.currentItem == slot || slot == -1) {
            return;
        }
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }

    public static boolean heldItem(Item item, Hand hand) {
        switch (hand) {
            case Main:
                if (mc.player.getHeldItemMainhand().getItem() == item) {
                    return true;
                }
                break;

            case Off:
                if (mc.player.getHeldItemOffhand().getItem() == item) {
                    return true;
                }
                break;
            case Both:
                if (mc.player.getHeldItemOffhand().getItem() == item ||  mc.player.getHeldItemMainhand().getItem() == item) {
                    return true;
                }
                break;
        }
        return false;
    }

    public enum Hand {Main, Off, Both}
}