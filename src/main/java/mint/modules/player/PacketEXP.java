package mint.modules.player;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

@ModuleInfo(name = "Packet EXP", category = Module.Category.Player,description = "Uses packets to throw Exp.")
public class PacketEXP extends Module {

    public IntegerSetting packets = new IntegerSetting("Packets", 1, 0, 10, this);
    public BooleanSetting rightClickOnly = new BooleanSetting("Right Click Only", false, this);

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (rightClickOnly.getValue() && !mc.gameSettings.keyBindUseItem.isKeyDown())
            return;

        if (InventoryUtil.heldItem(Items.EXPERIENCE_BOTTLE, InventoryUtil.Hand.Both)) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(HotbarEXP()));
            for (int i = 0; i < packets.getValue(); i++)
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));

            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
    }

    int HotbarEXP() {
        int slot = 0;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}
