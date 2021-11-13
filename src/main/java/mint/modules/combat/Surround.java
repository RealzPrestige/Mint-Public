package mint.modules.combat;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.modules.movement.Step;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.settingsrewrite.impl.ParentSetting;
import mint.utils.*;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(name = "Surround", category = Module.Category.Combat, description = "Surrounds you with Obsidian yuh.")
public class Surround extends Module {
    public int maxBlocks;
    public int itemSlot;
    public ParentSetting modesParent = new ParentSetting("Modes", true, this);
    public EnumSetting placeMode = new EnumSetting("Place Mode", PlaceMode.Vanilla, this, v -> modesParent.getValue());
    public EnumSetting disableMode = new EnumSetting("Disable Mode", DisableMode.Smart, this, v -> modesParent.getValue());
    public EnumSetting blocks = new EnumSetting("Blocks", BlockSelection.Obsidian, this, v -> modesParent.getValue());
    public ParentSetting miscParent = new ParentSetting("Misc", true, this);
    public IntegerSetting placeDelay = new IntegerSetting("Place Delay", 50, 0, 500, this, v -> miscParent.getValue());
    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this, v -> miscParent.getValue());
    public BooleanSetting cancelOnChorus = new BooleanSetting("Cancel On Chorus", false, this, v -> miscParent.getValue());
    public BooleanSetting bottomBlocks = new BooleanSetting("Bottom Blocks", false, this, v -> miscParent.getValue());
    public BooleanSetting bottomBlocksExtend = new BooleanSetting("Bottom Blocks Extend", false, this, v -> bottomBlocks.getValue() && miscParent.getValue());
    public BooleanSetting maxBlock = new BooleanSetting("Max Blocks", false, this, v -> miscParent.getValue());
    public IntegerSetting maxBlocksAmount = new IntegerSetting("Max Blocks Amount", 10, 0, 20, this, v -> maxBlock.getValue() && miscParent.getValue());

    public enum PlaceMode {Vanilla, Packet}

    public enum DisableMode {OnComplete, Motion, Onground, Smart, StepHeight}

    public enum BlockSelection {Obsidian, Echest, Auto}

    Timer timer = new Timer();

    public void onLogin() {
        if (isEnabled())
            disable();
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        BlockPos center = PlayerUtil.getCenterPos(pos.getX(), pos.getY(), pos.getZ());

        if(mc.player.getHeldItemMainhand().getItem().equals(Items.CHORUS_FRUIT) && mc.player.isHandActive() && cancelOnChorus.getValue())
            disable();

        if (blocks.getValueEnum().equals(BlockSelection.Obsidian))
            itemSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        else if (blocks.getValueEnum().equals(BlockSelection.Echest))
            itemSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        else if (blocks.getValueEnum().equals(BlockSelection.Auto))
            if (InventoryUtil.findHotbarBlock(BlockObsidian.class) != -1)
                itemSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            else if (InventoryUtil.findHotbarBlock(BlockEnderChest.class) != -1)
                itemSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            else disable();

        if (itemSlot == -1)
            disable();

        if (disableMode.getValueEnum().equals(DisableMode.Smart))
            if (mc.player.motionY > 0.2 && !mc.player.onGround && mc.player.stepHeight > 0.6 || Step.getInstance().isEnabled())
                disable();
            else if (disableMode.getValueEnum().equals(DisableMode.Motion))
                if (mc.player.motionY > 0.2)
                    disable();
                else if (disableMode.getValueEnum().equals(DisableMode.Onground))
                    if (!mc.player.onGround)
                        disable();
                    else if (disableMode.getValueEnum().equals(DisableMode.OnComplete))
                        if (mc.world.getBlockState(center.north()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(center.east()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(center.south()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(center.west()).getBlock().equals(Blocks.OBSIDIAN))
                            disable();
                        else if (disableMode.getValueEnum().equals(DisableMode.StepHeight))
                            if (mc.player.stepHeight > 1)
                                disable();

        if (!timer.passedMs(placeDelay.getValue()))
            return;

        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down().north()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down().north(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down().east()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down().east(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down().south()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down().south(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (bottomBlocksExtend.getValue() && !(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down().west()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down().west(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.down()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.down(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.north()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.north(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.east()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.east(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.south()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.south(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
        if (!(maxBlock.getValue() && maxBlocks < maxBlocksAmount.getValue()) && mc.world.getBlockState(center.west()).getBlock().equals(Blocks.AIR)) {
            placeSurroundBlocks(center.west(), rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.Packet));
            timer.reset();
        }
    }

    void placeSurroundBlocks(BlockPos pos, boolean rotate, boolean packet) {
        if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
            return;
        int currentItem = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = itemSlot;
        mc.playerController.updateController();
        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate, packet, false, false, EnumHand.MAIN_HAND);
        mc.player.inventory.currentItem = currentItem;
        mc.playerController.updateController();
        ++maxBlocks;
    }
}
