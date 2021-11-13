package mint.modules.combat;

import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.utils.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

@ModuleInfo(name = "Self Fill", category = Module.Category.Combat, description = "Rubberbands you in a block.")
public class SelfFill extends Module {

    public EnumSetting prefer = new EnumSetting("Prefer", Block.EChest, this);

    public enum Block {EChest, Obsidian}

    public FloatSetting height = new FloatSetting("Height", 1.0f, 1.0f, 5.0f, this);
    public EnumSetting lagBack = new EnumSetting("LagBack", LagMode.Teleport, this);

    public enum LagMode {Packet, YMotion, Teleport, LagFall, DoubleJump}

    public BooleanSetting packetJump = new BooleanSetting("PacketJump", true, this, v -> lagBack.getValueEnum().equals(LagMode.DoubleJump));
    public BlockPos startPos = null;
    Timer timer = new Timer();

    @Override
    public void onEnable() {
        if (NullUtil.fullNullCheck()) {
            disable();
            return;
        }
        startPos = new BlockPos(mc.player.getPositionVector());
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            disable();
            return;
        }

        int originalSlot = mc.player.inventory.currentItem;
        int ecSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
        int obbySlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (ecSlot == -1 && obbySlot == -1) {
            MessageManager.sendMessage("Out of blocks, disabling");
            disable(); //how did no1 notice this oml
        }

        if (prefer.getValueEnum().equals(Block.EChest) && ecSlot != -1) {
            InventoryUtil.SilentSwitchToSlot(ecSlot);
        }
        if (prefer.getValueEnum().equals(Block.Obsidian)) {
            InventoryUtil.SilentSwitchToSlot(obbySlot);
            if (obbySlot == -1 && ecSlot != -1) {
                InventoryUtil.SilentSwitchToSlot(ecSlot);
            }
        }

        EntityUtil.startSneaking();
        EntityUtil.packetJump(true);
        BlockUtil.placeBlock(startPos, EnumHand.MAIN_HAND, false, true, false, true, EnumHand.MAIN_HAND);

        if (lagBack.getValueEnum().equals(LagMode.Packet))
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ, true));
        if (lagBack.getValueEnum().equals(LagMode.YMotion))
            mc.player.motionY = height.getValue();
        if (lagBack.getValueEnum().equals(LagMode.Teleport))
            mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ);
        if (lagBack.getValueEnum().equals(LagMode.LagFall)) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
            mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }
        if (lagBack.getValueEnum().equals(LagMode.DoubleJump))
            if (packetJump.getValue())
                EntityUtil.packetJump(true);
            else mc.player.jump();

        EntityUtil.stopSneaking(false);

        if (originalSlot != -1) {
            mc.player.inventory.currentItem = originalSlot;
            mc.playerController.updateController();
        }
        timer.reset();
        disable();
    }
}