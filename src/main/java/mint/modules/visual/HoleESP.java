package mint.modules.visual;

import com.google.common.collect.Sets;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.NullUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.HashSet;

@ModuleInfo(name = "Hole ESP", category = Module.Category.Visual, description = "Renders safe holes.")
public class HoleESP extends Module {

    /**
     * @author zPrestige
     */

    public int updates;
    HashSet<BlockPos> bedrockholes = Sets.newHashSet();
    HashSet<BlockPos> obsidianholes = Sets.newHashSet();
    public ParentSetting rangesParent = new ParentSetting("Ranges", false, this);
    public IntegerSetting range = new IntegerSetting("X Range", 8, 1, 20, this, v -> rangesParent.getValue());
    public IntegerSetting rangeY = new IntegerSetting("Y Range", 6, 1, 20, this, v -> rangesParent.getValue());
    public ParentSetting othersParent = new ParentSetting("Others", false, this);
    public IntegerSetting updateDelay = new IntegerSetting("Update Delay", 1, 0, 30, this, v -> othersParent.getValue());
    public BooleanSetting gradient = new BooleanSetting("Gradient", false, this, v -> othersParent.getValue());
    public BooleanSetting dynamicHeights = new BooleanSetting("Dynamic Height", false, this, v -> gradient.getValue() && othersParent.getValue());
    public DoubleSetting height = new DoubleSetting("Height", 1.0, 0.0, 3.0, this, v -> gradient.getValue() && othersParent.getValue());
    public DoubleSetting value = new DoubleSetting("height Value", 10.0, 0.1, 30.0, this, v -> gradient.getValue() && othersParent.getValue());
    public BooleanSetting antiInverse = new BooleanSetting("Anti Inverse", false, this, v -> gradient.getValue() && othersParent.getValue());
    public ParentSetting bedrockParent = new ParentSetting("Bedrock", false, this);
    public BooleanSetting bedrockBox = new BooleanSetting("Bedrock Box", true, this, v -> bedrockParent.getValue());
    public ColorSetting bedrockBoxColor = new ColorSetting("Bedrock Box Color", new Color(-1), this, v -> bedrockBox.getValue() && bedrockParent.getValue());
    public BooleanSetting bedrockOutline = new BooleanSetting("Bedrock Outline", true, this, v -> bedrockParent.getValue());
    public ColorSetting bedrockOutlineColor = new ColorSetting("Bedrock Outline Color", new Color(-1), this, v -> bedrockOutline.getValue() && bedrockParent.getValue());
    public FloatSetting bedrockOutlineLineWidth = new FloatSetting("Bedrock Outline Line Width", 1f, 0f, 5f, this, v -> bedrockOutline.getValue() && bedrockParent.getValue());
    public ParentSetting obsidianParent = new ParentSetting("Obsidian", false, this);
    public BooleanSetting obsidianBox = new BooleanSetting("Obsidian Box", true, this, v -> obsidianParent.getValue());
    public ColorSetting obsidianBoxColor = new ColorSetting("Obsidian Box Color", new Color(-1), this, v -> obsidianBox.getValue() && obsidianParent.getValue());
    public BooleanSetting obsidianOutline = new BooleanSetting("Obsidian Outline", true, this, v -> obsidianParent.getValue());
    public ColorSetting obsidianOutlineColor = new ColorSetting("Obsidian Outline Color", new Color(-1), this, v -> obsidianOutline.getValue() && obsidianParent.getValue());
    public FloatSetting obsidianOutlineLineWidth = new FloatSetting("Obsidian Outline Line Width", 1f, 0f, 5f, this, v -> obsidianOutline.getValue() && obsidianParent.getValue());

    public void onTick() {
        if (updates > updateDelay.getValue()) {
            updates = 0;
        } else {
            ++updates;
        }
    }

    public void onToggle() {
        bedrockholes.clear();
        obsidianholes.clear();
    }

    public void onEnable() {
        updates = 0;
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (NullUtil.fullNullCheck())
            return;

        for (BlockPos pos : bedrockholes) {
            double dynamicHeight = height.getValue() - mc.player.getDistanceSq(pos) / (range.getValue() * value.getValue());
            double finalDynamicHeight = (antiInverse.getValue() && dynamicHeight < -1) ? -1 : dynamicHeight;
            if (gradient.getValue()) {
                RenderUtil.drawGlowBox(pos, bedrockBoxColor.getColor(), dynamicHeights.getValue() ? finalDynamicHeight : height.getValue() - 1);
            }
            RenderUtil.drawBoxESPFlat(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), bedrockBoxColor.getColor(), true, bedrockOutlineColor.getColor(), bedrockOutlineLineWidth.getValue(), bedrockOutline.getValue(), bedrockBox.getValue(), bedrockBoxColor.getColor().getAlpha(), true);
        }
        for (BlockPos pos : obsidianholes) {
            double dynamicHeight = height.getValue() - mc.player.getDistanceSq(pos) / (range.getValue() * value.getValue());
            double finalDynamicHeight = (antiInverse.getValue() && dynamicHeight < -1) ? -1 : dynamicHeight;
            if (gradient.getValue()) {
                RenderUtil.drawGlowBox(pos, obsidianBoxColor.getColor(), dynamicHeights.getValue() ? finalDynamicHeight : height.getValue() - 1);
            }
            RenderUtil.drawBoxESPFlat(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), obsidianBoxColor.getColor(), true, obsidianOutlineColor.getColor(), obsidianOutlineLineWidth.getValue(), obsidianOutline.getValue(), obsidianBox.getValue(), obsidianBoxColor.getColor().getAlpha(), true);
        }
        if (updates > updateDelay.getValue()) {
            obsidianholes.clear();
            bedrockholes.clear();
            findHoles();
        }
    }

    public void findHoles() {
        assert (mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - this.range.getValue(); x < playerPos.getX() + this.range.getValue(); ++x) {
            for (int z = playerPos.getZ() - this.range.getValue(); z < playerPos.getZ() + this.range.getValue(); ++z) {
                for (int y = playerPos.getY() + this.rangeY.getValue(); y > playerPos.getY() - this.rangeY.getValue(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (updates > updateDelay.getValue()) {
                        if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            bedrockholes.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)) {
                            obsidianholes.add(pos);
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                            bedrockholes.add(pos);
                            bedrockholes.add(pos.north());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) && (mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK)) {
                            obsidianholes.add(pos);
                            obsidianholes.add(pos.north());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK) {
                            bedrockholes.add(pos);
                            bedrockholes.add(pos.west());
                        } else if (mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.OBSIDIAN)) {
                            obsidianholes.add(pos);
                            obsidianholes.add(pos.west());
                        }
                    }
                }
            }
        }
    }

    public String hudInfoString() {
        return updates + " | " + Minecraft.getDebugFPS();
    }
}