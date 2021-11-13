package mint.modules.player;

import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.awt.*;

@ModuleInfo(name = "Auto Mine", category = Module.Category.Player, description = "Automatically mines stuff.")
public class AutoMine extends Module {

    public EnumSetting mineMode = new EnumSetting("Mine Mode", MineMode.Normal, this);
    public enum MineMode {Normal, Combat}

    public EnumSetting minePriority = new  EnumSetting("Mine Priority", Priority.Surrounds, this, v -> mineMode.getValueEnum().equals(MineMode.Combat));
    public enum Priority {Surrounds, City, Dynamic}

    public FloatSetting targetRange = new FloatSetting("Target Range", 9.0f, 0.0f, 15.0f, this, v -> mineMode.getValueEnum().equals(MineMode.Combat));
    public ParentSetting boxParent = new ParentSetting("Box", false, this, v -> mineMode.getValueEnum().equals(MineMode.Combat));
    public BooleanSetting boxSetting = new BooleanSetting("Box Setting", false, this, v -> boxParent.getValue() && mineMode.getValueEnum().equals(MineMode.Combat));
    public ColorSetting boxColor = new ColorSetting("Box Color", new Color(-1), this, v -> boxParent.getValue() && mineMode.getValueEnum().equals(MineMode.Combat));
    public ParentSetting outlineParent = new ParentSetting("Outline", false, this, v -> mineMode.getValueEnum().equals(MineMode.Combat));
    public BooleanSetting outlineSetting = new BooleanSetting("Outline Setting", false, this, v -> outlineParent.getValue() && mineMode.getValueEnum().equals(MineMode.Combat));
    public ColorSetting outlineColor = new ColorSetting("Outline Color", new Color(-1), this, v -> outlineParent.getValue() && outlineSetting.getValue() && mineMode.getValueEnum().equals(MineMode.Combat));

    BlockPos targetBlock = null;
    Timer timer = new Timer();

    public void onDisable() {
        if (mineMode.getValueEnum().equals(MineMode.Normal)) {
            mc.gameSettings.keyBindAttack.pressed = false;
        }
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;


        EntityPlayer target = EntityUtil.getTarget(targetRange.getValue());
        if (mineMode.getValueEnum() == MineMode.Normal) {
            mc.gameSettings.keyBindAttack.pressed = true;
        }

        if (mineMode.getValueEnum().equals(MineMode.Combat)) {

            if (target == null) return;

            BlockPos pos = PlayerUtil.getPlayerPos(target);

            if (EntityUtil.isPlayerSafe(target)) {
                if (minePriority.getValueEnum().equals(Priority.Surrounds)) {
                    if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.north();
                    } else if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.east();
                    } else if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.south();
                    } else if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.west();
                    } else {
                        targetBlock = null;
                    }
                }
                if (minePriority.getValueEnum() == Priority.City) {
                    if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north().north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().north().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.north();
                    } else if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.east().east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().east().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.east();
                    } else if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.south().south()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().south().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.south();
                    } else if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west().west()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().west().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.west();
                    } else {
                        targetBlock = null;
                    }
                }
                if (minePriority.getValueEnum() == Priority.Dynamic) {
                    if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north().north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().north().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.north();
                    } else if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.east().east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().east().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.east();
                    } else if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.south().south()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().south().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.south();
                    } else if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west().west()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().west().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.BEDROCK))) {
                        targetBlock = pos.west();
                    } else if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.north();
                    } else if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.east();
                    } else if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.south();
                    } else if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN)) {
                        targetBlock = pos.west();
                    } else {
                        targetBlock = null;
                    }
                }
            }
            if (targetBlock != null && mc.world.getBlockState(targetBlock).getBlock() == Blocks.AIR){
                targetBlock = null;
            }
        }

        if (targetBlock != null) {
            timer.reset();
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, targetBlock, EnumFacing.UP));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, targetBlock, EnumFacing.UP));
        }
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (targetBlock != null && !mc.world.getBlockState(targetBlock).getBlock().equals(Blocks.AIR)) {
            RenderUtil.drawBoxESP(targetBlock, boxColor.getColor(), true, outlineColor.getColor(), 1, outlineSetting.getValue(), boxSetting.getValue(), boxColor.getColor().getAlpha(), true);
        }
    }
}