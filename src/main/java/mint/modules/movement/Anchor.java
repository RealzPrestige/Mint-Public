package mint.modules.movement;

import com.google.common.collect.Sets;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.EntityUtil;
import mint.utils.NullUtil;
import mint.utils.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.HashSet;

@ModuleInfo(name = "Anchor", category = Module.Category.Movement, description = "Slows down when ur close to a hole.")
public class Anchor extends Module {

    public FloatSetting holeDistance =new FloatSetting("Hole Distance", 1.0f, 0.0f, 3.0f, this);
    public FloatSetting speed = new FloatSetting("Speed", 1.0f, 0.0f, 10.0f, this);
    public BooleanSetting onGround = new BooleanSetting("On Ground Only", false, this);
    public IntegerSetting exitHoleDelay = new IntegerSetting("Exit Hole Delay", 1000, 0, 2000, this);

    public int updates;

    HashSet<BlockPos> bedrockholes = Sets.newHashSet();
    HashSet<BlockPos> obsidianholes = Sets.newHashSet();
    Timer timer = new Timer();

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (updates > 5) {
            obsidianholes.clear();
            bedrockholes.clear();
            findHoles();
        }

        if (EntityUtil.isPlayerSafe(mc.player))
            timer.reset();

        for (BlockPos pos : bedrockholes) {

            if (!timer.passedMs(exitHoleDelay.getValue()))
                continue;

            if (onGround.getValue() && !mc.player.onGround)
                continue;

            if (EntityUtil.isPlayerSafe(mc.player))
                continue;

            if (mc.player.getDistance(pos.up().getX() + 0.5f, pos.getY() + 1.0f, pos.up().getZ() + 0.5f) > holeDistance.getValue())
                continue;

            if (mc.player.posX < pos.up().getZ() + 0.5f) {
                mc.player.motionX = speed.getValue() / 10;
            } else if (mc.player.posX > pos.up().getZ() + 0.5f) {
                mc.player.motionX = -speed.getValue() / 10;
            }

            if (!onGround.getValue() && !mc.player.onGround)
                mc.player.motionY = -0.5f;

            if (mc.player.posZ < pos.up().getZ() + 0.5f) {
                mc.player.motionZ = speed.getValue() / 10;
            } else if (mc.player.posZ > pos.up().getZ() + 0.5f) {
                mc.player.motionZ = -speed.getValue() / 10;
            }

        }
        for (BlockPos pos : obsidianholes) {

            if (!timer.passedMs(exitHoleDelay.getValue()))
                continue;

            if (onGround.getValue() && !mc.player.onGround)
                continue;

            if (EntityUtil.isPlayerSafe(mc.player))
                continue;

            if (mc.player.getDistance(pos.up().getX() + 0.5f, pos.getY() + 1.0f, pos.up().getZ() + 0.5f) > holeDistance.getValue())
                continue;

            if (mc.player.posX < pos.up().getX() + 0.5f) {
                mc.player.motionX = speed.getValue() / 10;
            } else if (mc.player.posX > pos.up().getX() + 0.5f) {
                mc.player.motionX = -speed.getValue() / 10;
            }

            if (!onGround.getValue() && !mc.player.onGround)
                mc.player.motionY = -0.5f;

            if (mc.player.posZ < pos.up().getZ() + 0.5f) {
                mc.player.motionZ = speed.getValue() / 10;
            } else if (mc.player.posZ > pos.up().getZ() + 0.5f) {
                mc.player.motionZ = -speed.getValue() / 10;
            }
        }
    }

    public void onTick() {
        if (updates > 5) {
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

    public void findHoles() {
        assert (mc.renderViewEntity != null);
        Vec3i playerPos = new Vec3i(mc.renderViewEntity.posX, mc.renderViewEntity.posY, mc.renderViewEntity.posZ);
        for (int x = playerPos.getX() - 3; x < playerPos.getX() + 3; ++x) {
            for (int z = playerPos.getZ() - 3; z < playerPos.getZ() + 3; ++z) {
                for (int y = playerPos.getY() + 3; y > playerPos.getY() - 3; --y) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (updates > 5) {
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
}
