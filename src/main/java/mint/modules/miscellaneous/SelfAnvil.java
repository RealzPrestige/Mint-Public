package mint.modules.miscellaneous;

import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

@ModuleInfo(name = "Self Anvil", category = Module.Category.Miscellaneous, description = "Lets an anvil fall on top of you to compensate for burrow patch...")
public class SelfAnvil extends Module {

    public EnumSetting baseBlock = new EnumSetting("Base Block", BaseBlock.Obsidian, this);

    public enum BaseBlock {Obsidian, Anvils}

    public IntegerSetting startDelay = new IntegerSetting("Start Delay", 50, 0, 200, this);
    public IntegerSetting placeDelay = new IntegerSetting("Place Delay", 20, 0, 200, this);
    public BooleanSetting smart = new BooleanSetting("Smart", false, this);
    public IntegerSetting smartRange = new IntegerSetting("Smart Range", 10, 0, 15, this, v -> smart.getValue());
    public BooleanSetting packet = new BooleanSetting("Packet", false, this);
    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    public BooleanSetting swing = new BooleanSetting("Swing", false, this);
    public EnumSetting swingMode = new EnumSetting("Swing Mode", EnumHand.MAIN_HAND, this, v -> swing.getValue());

    int blockSlot;
    BlockPos pos;
    Timer timer = new Timer();
    Timer startTimer = new Timer();
    BlockPos playerPos;
    BlockPos upperBlockPos = null;
    BlockPos baseBlockPos = null;
    BlockPos anvilBlockPos = null;

    public void onEnable() {
        startTimer.reset();
    }

    public void onDisable() {
        upperBlockPos = null;
        baseBlockPos = null;
        anvilBlockPos = null;
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;
        playerPos = PlayerUtil.getPlayerPos(mc.player);
        EntityPlayer target = EntityUtil.getTarget(smartRange.getValue());
        if (target == null)
            return;

        if (smart.getValue())
            if (target.getDistance(mc.player) > smartRange.getValue())
                return;

        if (startTimer.passedMs(startDelay.getValue()) && !mc.world.getBlockState(playerPos).getBlock().equals(Blocks.ANVIL)) {
            doAnvil();
        }
    }

    public void doAnvil() {
        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        this.pos = pos;
        int anvilSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ANVIL));

        if (!EntityUtil.isPlayerSafe(mc.player)) return;

        if (!mc.player.onGround) return;

        if (anvilSlot == -1)
            return;
        if (baseBlock.getValueEnum().equals(BaseBlock.Obsidian))
            blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        else if (baseBlock.getValueEnum().equals(BaseBlock.Anvils))
            blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ANVIL));

        if (blockSlot == -1) return;

        if (timer.passedMs(placeDelay.getValue()) && !hasBaseBlocks()) {
            placeBaseBlocks(blockSlot, rotate.getValue(), packet.getValue(), swing.getValue(), (EnumHand) swingMode.getValue());
            timer.reset();
        }
        if (timer.passedMs(placeDelay.getValue())) {
            placeUpperBaseBlocks(blockSlot, rotate.getValue(), packet.getValue(), swing.getValue(), (EnumHand) swingMode.getValue());
            timer.reset();
        }
        if (!mc.world.getBlockState(pos.north().up().up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.east().up().up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.south().up().up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.west().up().up()).getBlock().equals(Blocks.AIR)) {
            placeAnvil(anvilSlot, rotate.getValue(), packet.getValue(), swing.getValue(), (EnumHand) swingMode.getValue());
            disable();
        }
    }

    boolean hasBaseBlocks() {
        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        if (!mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR)) {
            return true;
        } else if (!mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR)) {
            return true;
        } else if (!mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR)) {
            return true;
        } else return !mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR);
    }

    void placeAnvil(int slot, boolean rotate, boolean packet, boolean swing, EnumHand hand) {
        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        int oldSlot = mc.player.inventory.currentItem;
        anvilBlockPos = pos.up().up();
        InventoryUtil.SilentSwitchToSlot(slot);
        BlockUtil.placeBlock(pos.up().up(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
        mc.player.inventory.currentItem = oldSlot;
        mc.playerController.updateController();
    }

    void placeBaseBlocks(int slot, boolean rotate, boolean packet, boolean swing, EnumHand hand) {
        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        int oldSlot = mc.player.inventory.currentItem;
        if (getSide() == 1) {
            baseBlockPos = pos.up().north();
            InventoryUtil.SilentSwitchToSlot(slot);
            BlockUtil.placeBlock(pos.up().north(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        } else if (getSide() == 2) {
            baseBlockPos = pos.up().east();
            InventoryUtil.SilentSwitchToSlot(slot);
            BlockUtil.placeBlock(pos.up().east(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        } else if (getSide() == 3) {
            baseBlockPos = pos.up().south();
            InventoryUtil.SilentSwitchToSlot(slot);
            BlockUtil.placeBlock(pos.up().south(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        } else if (getSide() == 4) {
            baseBlockPos = pos.up().west();
            InventoryUtil.SilentSwitchToSlot(slot);
            BlockUtil.placeBlock(pos.up().west(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        }
    }

    void placeUpperBaseBlocks(int slot, boolean rotate, boolean packet, boolean swing, EnumHand hand) {
        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        int oldSlot = mc.player.inventory.currentItem;
        if (getUpperSide() == 1) {
            if (!mc.world.getBlockState(pos.up().north()).getBlock().equals(Blocks.AIR)) {
                upperBlockPos = pos.up().up().north();
                InventoryUtil.SilentSwitchToSlot(slot);
                BlockUtil.placeBlock(pos.up().up().north(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if (getUpperSide() == 2) {
            if (!mc.world.getBlockState(pos.up().east()).getBlock().equals(Blocks.AIR)) {
                upperBlockPos = pos.up().up().east();
                InventoryUtil.SilentSwitchToSlot(slot);
                BlockUtil.placeBlock(pos.up().up().east(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if (getUpperSide() == 3) {
            if (!mc.world.getBlockState(pos.up().south()).getBlock().equals(Blocks.AIR)) {
                upperBlockPos = pos.up().up().south();
                InventoryUtil.SilentSwitchToSlot(slot);
                BlockUtil.placeBlock(pos.up().up().south(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        } else if (getUpperSide() == 4) {
            if (!mc.world.getBlockState(pos.up().west()).getBlock().equals(Blocks.AIR)) {
                upperBlockPos = pos.up().up().west();
                InventoryUtil.SilentSwitchToSlot(slot);
                BlockUtil.placeBlock(pos.up().up().west(), EnumHand.MAIN_HAND, rotate, packet, false, swing, hand);
                mc.player.inventory.currentItem = oldSlot;
                mc.playerController.updateController();
            }
        }
    }

    int getSide() {
        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        boolean placedNorth = false;
        boolean placedEast = false;
        boolean placedSouth = false;
        boolean placedWest = false;
        if (!mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.north().up().up()).getBlock().equals(Blocks.AIR)) {
            placedNorth = true;
        }
        if (!mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.east().up().up()).getBlock().equals(Blocks.AIR)) {
            placedEast = true;
        }
        if (!mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.south().up().up()).getBlock().equals(Blocks.AIR)) {
            placedSouth = true;
        }
        if (!mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.west().up().up()).getBlock().equals(Blocks.AIR)) {
            placedWest = true;
        }
        if (mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().up().up()).getBlock().equals(Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 1;
        }
        if (mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().up().up()).getBlock().equals(Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 2;
        }
        if (mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().up().up()).getBlock().equals(Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 3;
        }
        if (mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().up().up()).getBlock().equals(Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 4;
        }
        return 0;
    }

    int getUpperSide() {
        BlockPos pos = PlayerUtil.getPlayerPos(mc.player);
        boolean placedNorth = false;
        boolean placedEast = false;
        boolean placedSouth = false;
        boolean placedWest = false;
        if (!mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.north().up().up()).getBlock().equals(Blocks.AIR)) {
            placedNorth = true;
        }
        if (!mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.east().up().up()).getBlock().equals(Blocks.AIR)) {
            placedEast = true;
        }
        if (!mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.south().up().up()).getBlock().equals(Blocks.AIR)) {
            placedSouth = true;
        }
        if (!mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.west().up().up()).getBlock().equals(Blocks.AIR)) {
            placedWest = true;
        }
        if (mc.world.getBlockState(pos.north().up().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 1;
        }
        if (mc.world.getBlockState(pos.east().up().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 2;
        }
        if (mc.world.getBlockState(pos.south().up().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 3;
        }
        if (mc.world.getBlockState(pos.west().up().up()).getBlock().equals(Blocks.AIR) && !mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR) && !placedNorth && !placedEast && !placedSouth && !placedWest) {
            return 4;
        }
        return 0;
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        //BlockPos upperBlockPos = null;
        //BlockPos baseBlockPos = null;
        //BlockPos anvilBlockPos = null;
        if (upperBlockPos != null) {
            RenderUtil.drawBlockOutline(upperBlockPos, new Color(255, 255, 255), 1f, true);
        }
        if (baseBlockPos != null) {
            RenderUtil.drawBlockOutline(baseBlockPos, new Color(255, 255, 255), 1f, true);
        }
        if (anvilBlockPos != null) {
            RenderUtil.drawBlockOutline(anvilBlockPos, new Color(255, 255, 255), 1f, true);
        }
    }
}
