package mint.modules.miscellaneous;

import mint.events.RenderWorldEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.utils.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

@ModuleInfo(name = "Auto Ender Chest", category = Module.Category.Miscellaneous, description = "Farms Ender Chests automatically for you.")
public class AutoEnderChest extends Module {

    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    public BooleanSetting packet = new BooleanSetting("PacketPlace", false, this);
    public BooleanSetting swing = new BooleanSetting("Swing", false, this);
    public EnumSetting enumHand = new EnumSetting("Hand", Hand.Mainhand, this, v -> swing.getValue());

    public enum Hand {Mainhand, Offhand}

    public BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", false, this);
    public BooleanSetting safeOnly = new BooleanSetting("Only Safe", false, this);
    Timer timer = new Timer();
    Timer startTimer = new Timer();

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        int enderChestSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
        int pickaxeSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        int lastSlot = mc.player.inventory.currentItem;
        if (safeOnly.getValue() && !EntityUtil.isPlayerSafe(mc.player)) {
            disable();
        }
        if (startTimer.passedMs(1200)) {
            if (enderChestSlot == -1) {
                MessageManager.sendError("No enderchests in hotbar found.");
                disable();
            }
            timer.reset();
            startTimer.reset();
            InventoryUtil.SilentSwitchToSlot(enderChestSlot);
            if (getFace() == 1) {

                BlockUtil.placeBlock(pos.north().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), enumHand.getValueEnum().equals(Hand.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                mc.player.inventory.currentItem = lastSlot;
                mc.playerController.updateController();
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos.north().up(), EnumFacing.UP));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos.north().up(), EnumFacing.UP));
                if (mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.ENDER_CHEST) && autoSwitch.getValue()) {
                    InventoryUtil.SilentSwitchToSlot(pickaxeSlot);
                } else if (mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR)) {
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
                }
            } else if (getFace() == 2) {

                BlockUtil.placeBlock(pos.east().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), enumHand.getValueEnum().equals(Hand.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                mc.player.inventory.currentItem = lastSlot;
                mc.playerController.updateController();
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos.east().up(), EnumFacing.UP));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos.east().up(), EnumFacing.UP));
                if (mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.ENDER_CHEST) && autoSwitch.getValue()) {
                    InventoryUtil.SilentSwitchToSlot(pickaxeSlot);
                } else if (mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR)) {
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
                }
            } else if (getFace() == 3) {

                BlockUtil.placeBlock(pos.south().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), enumHand.getValueEnum().equals(Hand.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                mc.player.inventory.currentItem = lastSlot;
                mc.playerController.updateController();
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos.south().up(), EnumFacing.UP));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos.south().up(), EnumFacing.UP));
                if (mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.ENDER_CHEST) && autoSwitch.getValue()) {
                    InventoryUtil.SilentSwitchToSlot(pickaxeSlot);
                } else if (mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR)) {
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
                }
            } else if (getFace() == 4) {

                BlockUtil.placeBlock(pos.west().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), enumHand.getValueEnum().equals(Hand.Mainhand) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                mc.player.inventory.currentItem = lastSlot;
                mc.playerController.updateController();
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos.west().up(), EnumFacing.UP));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos.west().up(), EnumFacing.UP));
                if (mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.ENDER_CHEST) && autoSwitch.getValue()) {
                    InventoryUtil.SilentSwitchToSlot(pickaxeSlot);
                } else if (mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR)) {
                    mc.player.inventory.currentItem = lastSlot;
                    mc.playerController.updateController();
                }
            }
        }
    }

    public int getFace() {
        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        if (!mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR)) {
            return 1;
        } else if (!mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR)) {
            return 2;
        } else if (!mc.world.getBlockState(pos.east().south()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR)) {
            return 3;
        } else if (!mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR)) {
            return 4;
        }
        return 0;
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (NullUtil.fullNullCheck())
            return;

        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        if (getFace() == 1) {
            RenderUtil.drawBlockOutline(pos.north().up(), new Color(255, 255, 255, 255), 1, true);
        } else if (getFace() == 2) {
            RenderUtil.drawBlockOutline(pos.east().up(), new Color(255, 255, 255, 255), 1, true);
        } else if (getFace() == 3) {
            RenderUtil.drawBlockOutline(pos.south().up(), new Color(255, 255, 255, 255), 1, true);
        } else if (getFace() == 2) {
            RenderUtil.drawBlockOutline(pos.west().up(), new Color(255, 255, 255, 255), 1, true);
        }
    }
}
