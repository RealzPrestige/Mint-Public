package mint.modules.combat;

import com.google.common.collect.Sets;
import mint.Mint;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.List;
import java.util.*;

@ModuleInfo(name = "Hole Filler", category = Module.Category.Combat, description = "Fills holes enemies want to go inside ;)")
public class HoleFiller extends Module {
    /**
     * @author zPrestige
     */

    public static final List<net.minecraft.block.Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    public static final List<net.minecraft.block.Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    int blockSlot;

    HashMap<BlockPos, Integer> filledFadeHoles = new HashMap();
    HashSet<BlockPos> fillableHoles = Sets.newHashSet();
    public EnumSetting mode = new EnumSetting("FillMode", Mode.NORMAL, this);

    public enum Mode {NORMAL, SMART}

    public EnumSetting placeMode = new EnumSetting("PlaceMode", PlaceMode.VANILLA, this);

    public enum PlaceMode {VANILLA, PACKET}

    public EnumSetting swingMode = new EnumSetting("SwingMode", SwingMode.MAINHAND, this);

    public enum SwingMode {MAINHAND, OFFHAND, NONE}

    public EnumSetting block = new EnumSetting("Block", Block.OBSIDIAN, this);

    public enum Block {OBSIDIAN, ECHEST, WEB}

    public EnumSetting onGroundChecks = new EnumSetting("OnGroundChecks", OnGroundChecks.NONE, this);

    public enum OnGroundChecks {SELF, TARGET, BOTH, NONE}

    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this);
    public BooleanSetting autoDisable = new BooleanSetting("AutoDisable", false, this);
    public BooleanSetting autoSwitch = new BooleanSetting("AutoSwitch", false, this);
    public BooleanSetting silentSwitch = new BooleanSetting("SilentSwitch", false, this, v -> autoSwitch.getValue());
    public BooleanSetting doubles = new BooleanSetting("DoubleHoles", false, this);
    public BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", false, this);
    public BooleanSetting swordCheck = new BooleanSetting("SwordCheck", false, this);
    public BooleanSetting targetUnSafe = new BooleanSetting("TargetUnSafe", false, this, v -> mode.getValueEnum().equals(Mode.SMART));
    public IntegerSetting smartRange = new IntegerSetting("Smart-Range", 5, 0, 6, this, v -> mode.getValueEnum().equals(Mode.SMART));
    public IntegerSetting targetRange = new IntegerSetting("TargetRange", 9, 1, 15, this, v -> mode.getValueEnum().equals(Mode.SMART));
    public IntegerSetting rangeX = new IntegerSetting("X-Range", 5, 1, 6, this);
    public IntegerSetting rangeY = new IntegerSetting("Y-Range", 5, 1, 6, this);
    public BooleanSetting render = new BooleanSetting("Render", false, this);
    public EnumSetting renderMode = new EnumSetting("RenderMode", RenderMode.STATIC, this, v -> render.getValue());

    public enum RenderMode {STATIC, FADE}

    public BooleanSetting box = new BooleanSetting("Box", false, this, v -> render.getValue());
    public ColorSetting boxColor = new ColorSetting("Box Color", new Color(-1), this, v -> render.getValue() && box.getValue());

    public BooleanSetting outline = new BooleanSetting("Outline", false, this, v -> render.getValue());
    public ColorSetting outlineColor = new ColorSetting("Outline Color", new Color(-1), this, v -> render.getValue() && outline.getValue());

    public FloatSetting lineWidth = new FloatSetting("LineWidth", 1.0f, 0.0f, 5.0f, this, v -> render.getValue() && outline.getValue());
    public IntegerSetting startAlpha = new IntegerSetting("StartAlpha", 255, 0, 255, this, v -> render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE));
    public IntegerSetting endAlpha = new IntegerSetting("EndAlpha", 0, 0, 255, this, v -> render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE));
    public IntegerSetting fadeStep = new IntegerSetting("FadeStep", 20, 10, 100, this, v -> render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE));

    public void onTick() {
        fillableHoles.clear();
        findFillableHoles();
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (render.getValue()) {
            if (renderMode.getValueEnum().equals(RenderMode.FADE)) {
                for (Map.Entry<BlockPos, Integer> entry : filledFadeHoles.entrySet()) {
                    filledFadeHoles.put(entry.getKey(), entry.getValue() - (fadeStep.getValue() / 10));
                    if (entry.getValue() <= endAlpha.getValue()) {
                        filledFadeHoles.remove(entry.getKey());
                        return;
                    }
                    RenderUtil.drawBoxESP(entry.getKey(), new Color(boxColor.getColor().getRed(), boxColor.getColor().getBlue(), boxColor.getColor().getGreen(), entry.getValue()), true, new Color(outlineColor.getColor().getRed(), outlineColor.getColor().getGreen(), outlineColor.getColor().getBlue(), entry.getValue()), lineWidth.getValue(), outline.getValue(), box.getValue(), entry.getValue(), true);
                }
            } else {
                for (BlockPos pos : fillableHoles) {
                    RenderUtil.drawBoxESP(pos, boxColor.getColor(), true, outlineColor.getColor(), lineWidth.getValue(), outline.getValue(), box.getValue(), boxColor.getColor().getAlpha(), true);
                }
            }
        }
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;
        for (BlockPos pos : fillableHoles) {
            if (block.getValueEnum().equals(Block.OBSIDIAN))
                blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
            if (block.getValueEnum().equals(Block.ECHEST))
                blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
            if (block.getValueEnum().equals(Block.WEB))
                blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.WEB));
            if (onGroundChecks.getValueEnum().equals(OnGroundChecks.SELF) && !mc.player.onGround)
                return;

            if (onGroundChecks.getValueEnum().equals(OnGroundChecks.TARGET) && !getPlayerTarget(targetRange.getValue()).onGround)
                return;
            if (onGroundChecks.getValueEnum().equals(OnGroundChecks.BOTH) && (getPlayerTarget(targetRange.getValue()).onGround || !mc.player.onGround))
                return;

            if (mode.getValueEnum() == Mode.NORMAL) {
                if (swordCheck.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                    return;
                }
                if (this.blockSlot == -1) {
                    return;
                }
                int lastSlot = mc.player.inventory.currentItem;
                if (throughWalls.getValue()) {
                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                        if (autoSwitch.getValue()) {
                            if (silentSwitch.getValue()) {
                                InventoryUtil.SilentSwitchToSlot(blockSlot);
                            } else {
                                mc.player.inventory.currentItem = blockSlot;
                            }
                        }
                        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.PACKET), false, swingMode.getValue() != SwingMode.NONE, swingMode.getValueEnum().equals(SwingMode.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                        if (autoSwitch.getValue() && silentSwitch.getValue()) {
                            mc.player.inventory.currentItem = lastSlot;
                            mc.playerController.updateController();
                        }
                        if (render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE)) {
                            if (!filledFadeHoles.containsKey(pos)) {
                                filledFadeHoles.put(pos, startAlpha.getValue());
                            }
                        }
                    }
                } else if (canBlockBeSeen(pos)) {
                    if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                        if (autoSwitch.getValue()) {
                            if (silentSwitch.getValue()) {
                                InventoryUtil.SilentSwitchToSlot(blockSlot);
                            } else {
                                mc.player.inventory.currentItem = blockSlot;
                            }
                        }
                        BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.PACKET), false, swingMode.getValue() != SwingMode.NONE, swingMode.getValueEnum().equals(SwingMode.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                        if (autoSwitch.getValue() && silentSwitch.getValue()) {
                            mc.player.inventory.currentItem = lastSlot;
                            mc.playerController.updateController();
                        }
                        if (render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE)) {
                            if (!filledFadeHoles.containsKey(pos)) {
                                filledFadeHoles.put(pos, startAlpha.getValue());
                            }
                        }
                    }
                }
                if (autoDisable.getValue()) {
                    disable();
                }
            }

            if (mode.getValueEnum().equals(Mode.SMART)) {
                if (swordCheck.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                    return;
                }
                if (blockSlot == -1) {
                    return;
                }
                int lastSlot = mc.player.inventory.currentItem;
                blockSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                if (getPlayerTarget(targetRange.getValue()) != null && Objects.requireNonNull(getPlayerTarget(targetRange.getValue())).getDistanceSq(pos) < MathUtil.square(smartRange.getValue())) {
                    if (targetUnSafe.getValue()) {
                        if (getPlayerTarget(targetRange.getValue()) != null && !EntityUtil.isSafe(getPlayerTarget(targetRange.getValue()))) {
                            if (throughWalls.getValue()) {
                                if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                    if (autoSwitch.getValue()) {
                                        if (silentSwitch.getValue()) {
                                            InventoryUtil.SilentSwitchToSlot(blockSlot);
                                        } else {
                                            mc.player.inventory.currentItem = blockSlot;
                                        }
                                    }
                                    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.PACKET), false, swingMode.getValue() != SwingMode.NONE, swingMode.getValueEnum().equals(SwingMode.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                    if (autoSwitch.getValue() && silentSwitch.getValue()) {
                                        mc.player.inventory.currentItem = lastSlot;
                                        mc.playerController.updateController();
                                    }
                                    if (render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE)) {
                                        if (!filledFadeHoles.containsKey(pos)) {
                                            filledFadeHoles.put(pos, startAlpha.getValue());
                                        }
                                    }
                                }
                            } else if (canBlockBeSeen(pos)) {
                                if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                    if (autoSwitch.getValue()) {
                                        if (silentSwitch.getValue()) {
                                            InventoryUtil.SilentSwitchToSlot(blockSlot);
                                        } else {
                                            mc.player.inventory.currentItem = blockSlot;
                                        }
                                    }
                                    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.PACKET), false, swingMode.getValue() != SwingMode.NONE, swingMode.getValueEnum().equals(SwingMode.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                    if (autoSwitch.getValue() && silentSwitch.getValue()) {
                                        mc.player.inventory.currentItem = lastSlot;
                                        mc.playerController.updateController();
                                    }
                                    if (render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE)) {
                                        if (!filledFadeHoles.containsKey(pos)) {
                                            filledFadeHoles.put(pos, startAlpha.getValue());
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (throughWalls.getValue()) {
                            if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                if (autoSwitch.getValue()) {
                                    if (silentSwitch.getValue()) {
                                        InventoryUtil.SilentSwitchToSlot(blockSlot);
                                    } else {
                                        mc.player.inventory.currentItem = blockSlot;
                                    }
                                }
                                BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.PACKET), false, swingMode.getValue() != SwingMode.NONE, swingMode.getValueEnum().equals(SwingMode.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                if (autoSwitch.getValue() && silentSwitch.getValue()) {
                                    mc.player.inventory.currentItem = lastSlot;
                                    mc.playerController.updateController();
                                }
                                if (render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE)) {
                                    if (!filledFadeHoles.containsKey(pos)) {
                                        filledFadeHoles.put(pos, startAlpha.getValue());
                                    }
                                }
                            }
                        } else if (canBlockBeSeen(pos)) {
                            if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty()) {
                                if (autoSwitch.getValue()) {
                                    if (silentSwitch.getValue()) {
                                        InventoryUtil.SilentSwitchToSlot(blockSlot);
                                    } else {
                                        mc.player.inventory.currentItem = blockSlot;
                                    }
                                }
                                BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, rotate.getValue(), placeMode.getValueEnum().equals(PlaceMode.PACKET), false, swingMode.getValue() != SwingMode.NONE, swingMode.getValueEnum().equals(SwingMode.MAINHAND) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
                                if (autoSwitch.getValue() && silentSwitch.getValue()) {
                                    mc.player.inventory.currentItem = lastSlot;
                                    mc.playerController.updateController();
                                }
                                if (render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE)) {
                                    if (!filledFadeHoles.containsKey(pos)) {
                                        filledFadeHoles.put(pos, startAlpha.getValue());
                                    }
                                }
                            } else {
                                if (render.getValue() && renderMode.getValueEnum().equals(RenderMode.FADE)) {
                                    if (!filledFadeHoles.containsKey(pos)) {
                                        filledFadeHoles.put(pos, startAlpha.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
                if (autoDisable.getValue()) {
                    disable();
                }
            }
        }

    }


    public void findFillableHoles() {
        assert (mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - rangeX.getValue(); x < playerPos.getX() + rangeX.getValue(); ++x) {
            for (int z = playerPos.getZ() - rangeX.getValue(); z < playerPos.getZ() + rangeX.getValue(); ++z) {
                for (int y = playerPos.getY() + rangeY.getValue(); y > playerPos.getY() - rangeY.getValue(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                        fillableHoles.add(pos);
                    } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)) {
                        fillableHoles.add(pos);
                    }
                    if (doubles.getValue()) {
                        if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            fillableHoles.add(pos);
                            fillableHoles.add(pos.north());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK)) {
                            fillableHoles.add(pos);
                            fillableHoles.add(pos.north());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK) {
                            fillableHoles.add(pos);
                            fillableHoles.add(pos.west());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.OBSIDIAN)) {
                            fillableHoles.add(pos);
                            fillableHoles.add(pos.west());
                        }
                    }
                }
            }
        }
    }

    public static EntityPlayer getPlayerTarget(int targetRange) {
        EntityPlayer target = EntityUtil.getTarget(targetRange);
        return target;
    }

    public static boolean canBlockBeSeen(final BlockPos blockPos) {
        return Mint.INSTANCE.mc.world.rayTraceBlocks(new Vec3d(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + Mint.INSTANCE.mc.player.getEyeHeight(), Mint.INSTANCE.mc.player.posZ), new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), false, true, false) == null;
    }
}
