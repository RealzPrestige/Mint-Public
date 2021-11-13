package mint.modules.combat;

import mint.Mint;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Objects;

@ModuleInfo(name = "City Anvil", category = Module.Category.Combat, description = "Breaks ppl surround using anvil")
public class CityAnvil extends Module {

    public FloatSetting targetRange = new FloatSetting("Target Range", 5.0f, 0.0f, 6.0f, this);
    public BooleanSetting silentSwitch = new BooleanSetting("Silent Switch", false, this);
    public BooleanSetting packet = new BooleanSetting("Packet", false, this);
    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    public BooleanSetting swing = new BooleanSetting("Swing", false, this);
    public EnumSetting swingMode = new EnumSetting("Swing Mode", EnumHand.MAIN_HAND, this, v -> swing.getValue());
    public BooleanSetting autoMine = new BooleanSetting("Auto Mine", false, this);
    public IntegerSetting mineDelay = new IntegerSetting("Mine Delay", 700, 0, 1000, this);
    public BooleanSetting render = new BooleanSetting("Render", false, this);
    public ColorSetting color = new ColorSetting("Color", new Color(-1), this, v -> render.getValue());
    Timer timer = new Timer();
    BlockPos currentPos;
    BlockPos currentPos2;

    public void onUpdate() {
        EntityPlayer target = EntityUtil.getTarget(targetRange.getValue());
        if (target != null && !Mint.friendManager.isFriend(target.getName())) {
            BlockPos pos = PlayerUtil.getPlayerPos(target);
            int anvilSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ANVIL));
            int crystalSlot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
            int oldSlot = mc.player.inventory.currentItem;

            //North
            if (mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.north().north().up()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.down()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.south()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR) {
                if (anvilSlot != -1) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(anvilSlot);
                    }
                    BlockUtil.placeBlock(pos.north(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), (EnumHand) swingMode.getValue());
                    currentPos = pos.north();
                    currentPos2 = pos.north().north();
                    if (silentSwitch.getValue()) {
                        mc.player.inventory.currentItem = oldSlot;
                        mc.playerController.updateController();
                    }
                }
                if (silentSwitch.getValue() && crystalSlot != -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }

                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.north().north().down(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                if (silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }

                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal) {
                        if (crystal.getDistance(mc.player) > MathUtil.square(5)) continue;

                        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                    }
                }
            }

            //East
            if (mc.world.getBlockState(pos.east()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.east().east()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.east().east().up()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.down()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.south()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR) {
                if (anvilSlot != -1) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(anvilSlot);
                    }
                    BlockUtil.placeBlock(pos.east(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), (EnumHand) swingMode.getValue());
                    currentPos = pos.east();
                    currentPos2 = pos.east().east();
                    if (silentSwitch.getValue()) {
                        mc.player.inventory.currentItem = oldSlot;
                        mc.playerController.updateController();
                    }
                }

                if (silentSwitch.getValue() && crystalSlot != -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }

                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.east().east().down(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                if (silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }

                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal) {
                        if (crystal.getDistance(mc.player) > MathUtil.square(5)) continue;

                        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                    }
                }
            }

            //South
            if (mc.world.getBlockState(pos.south()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.south().south()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.south().south().up()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.down()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR) {
                if (anvilSlot != -1) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(anvilSlot);
                    }
                    BlockUtil.placeBlock(pos.south(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), (EnumHand) swingMode.getValue());
                    currentPos = pos.south();
                    currentPos2 = pos.south().south();
                    if (silentSwitch.getValue()) {
                        mc.player.inventory.currentItem = oldSlot;
                        mc.playerController.updateController();
                    }
                }

                if (silentSwitch.getValue() && crystalSlot != -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }

                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.south().south().down(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                if (silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }

                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal) {

                        if (crystal.getDistance(mc.player) > MathUtil.square(5)) continue;

                        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));

                    }
                }

            }

            //West
            if (mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.west().west().up()).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(pos.down()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.south()).getBlock() != Blocks.AIR
                    && mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR) {
                if (anvilSlot != -1) {
                    if (silentSwitch.getValue()) {
                        InventoryUtil.SilentSwitchToSlot(anvilSlot);
                    }
                    BlockUtil.placeBlock(pos.west(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), false, swing.getValue(), (EnumHand) swingMode.getValue());
                    currentPos = pos.west();
                    currentPos2 = pos.west().west();
                    if (silentSwitch.getValue()) {
                        mc.player.inventory.currentItem = oldSlot;
                        mc.playerController.updateController();
                    }
                }

                if (silentSwitch.getValue() && crystalSlot != -1 && mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                    InventoryUtil.SilentSwitchToSlot(crystalSlot);
                }

                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos.west().west().down(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

                if (silentSwitch.getValue()) {
                    mc.player.inventory.currentItem = oldSlot;
                    mc.playerController.updateController();
                }

                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal) {

                        if (crystal.getDistance(mc.player) > MathUtil.square(5)) continue;

                        mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
                    }
                }
            }
        }
        if (currentPos != null && mc.world.getBlockState(currentPos).getBlock() == Blocks.ANVIL) {
            if (autoMine.getValue()) {
                if (timer.passedMs(mineDelay.getValue())) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, currentPos, EnumFacing.UP));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentPos, EnumFacing.UP));
                    timer.reset();
                }
            }

        }
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (currentPos != null && render.getValue())
            RenderUtil.drawBlockOutline(currentPos, color.getColor(), 1, true);
        if (currentPos2 != null && render.getValue())
            RenderUtil.drawBlockOutline(currentPos2, color.getColor(), 1, true);

    }
}
