package mint.utils;

import mint.Mint;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.chunk.EmptyChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class EntityUtil {
    static Minecraft mc = Minecraft.getMinecraft();

    public static float calculateEntityDamage(EntityEnderCrystal crystal, EntityPlayer player) {
        return calculatePosDamage(crystal.posX, crystal.posY, crystal.posZ, player);
    }

    public static float calculatePosDamage(BlockPos position, EntityPlayer player) {
        return calculatePosDamage(position.getX() + 0.5, position.getY() + 1.0, position.getZ() + 0.5, player);
    }

    public static float calculatePosDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleSize = 12.0F;
        double size = entity.getDistance(posX, posY, posZ) / (double) doubleSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double value = (1.0D - size) * blockDensity;
        float damage = (float) ((int) ((value * value + value) / 2.0D * 7.0D * (double) doubleSize + 1.0D));
        double finalDamage = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finalDamage = getBlastReduction((EntityLivingBase) entity, getMultipliedDamage(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finalDamage;
    }


    private static float getMultipliedDamage(float damage) {
        return damage * (mc.world.getDifficulty().getId() == 0 ? 0.0F : (mc.world.getDifficulty().getId() == 2 ? 1.0F : (mc.world.getDifficulty().getId() == 1 ? 0.5F : 1.5F)));
    }

    public static double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (Mint.INSTANCE.mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            maxModifier *= 1.0 + 0.2 * (Objects.requireNonNull(Mint.INSTANCE.mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier() + 1);
        }
        return maxModifier;
    }


    public static double getDefaultMoveSpeed() {
        double baseSpeed = 0.2873;
        if (Mint.INSTANCE.mc.player != null && Mint.INSTANCE.mc.player.isPotionActive(Potion.getPotionById(1))) {
            final int amplifier = Mint.INSTANCE.mc.player.getActivePotionEffect(Potion.getPotionById(1)).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static double[] forward(final double speed) {
        float forward = Mint.INSTANCE.mc.player.movementInput.moveForward;
        float side = Mint.INSTANCE.mc.player.movementInput.moveStrafe;
        float yaw = Mint.INSTANCE.mc.player.prevRotationYaw + (Mint.INSTANCE.mc.player.rotationYaw - Mint.INSTANCE.mc.player.prevRotationYaw) * Mint.INSTANCE.mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static boolean isMoving(final EntityLivingBase entity) {
        return entity.moveForward != 0.0f || entity.moveStrafing != 0.0f;
    }

    public static float calculatePos(final BlockPos pos, final EntityPlayer entity) {
        return calculate(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, entity);
    }

    public static boolean isInFov(Entity entity) {
        return entity != null && (Mint.INSTANCE.mc.player.getDistanceSq(entity) < 4.0 || yawDist(entity) < (double) (getFov() + 2.0f));
    }

    public static float calculate(double posX, double posY, double posZ, EntityLivingBase entity) {
        final double v = (1.0D - entity.getDistance(posX, posY, posZ) / 12.0D) * getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
        return getBlastReduction(entity, getDamageMultiplied((float) ((v * v + v) / 2.0 * 85.0 + 1.0)), new Explosion(Mint.INSTANCE.mc.world, null, posX, posY, posZ, 6F, false, true));
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        final int k = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), ds);
        damage = damage * (1.0F - MathHelper.clamp(k, 0.0F, 20.0F) / 25.0F);

        if (entity.isPotionActive(MobEffects.RESISTANCE)) {
            damage = damage - (damage / 4);
        }

        return damage;
    }

    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        double d0 = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        double d1 = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        double d2 = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
        double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;

        if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
            int j2 = 0;
            int k2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float) ((double) f + d0)) {
                for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float) ((double) f1 + d1)) {
                    for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float) ((double) f2 + d2)) {
                        if (rayTraceBlocks(new Vec3d(bb.minX + (bb.maxX - bb.minX) * (double) f + d3, bb.minY + (bb.maxY - bb.minY) * (double) f1, bb.minZ + (bb.maxZ - bb.minZ) * (double) f2 + d4), vec, false, false, false) == null) {
                            ++j2;
                        }
                        ++k2;
                    }
                }
            }

            return (float) j2 / (float) k2;
        } else {
            return 0.0F;
        }
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        final int i = MathHelper.floor(vec32.x);
        final int j = MathHelper.floor(vec32.y);
        final int k = MathHelper.floor(vec32.z);
        int l = MathHelper.floor(vec31.x);
        int i1 = MathHelper.floor(vec31.y);
        int j1 = MathHelper.floor(vec31.z);
        BlockPos blockpos = new BlockPos(l, i1, j1);
        IBlockState iblockstate = Mint.INSTANCE.mc.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(Mint.INSTANCE.mc.world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
            return iblockstate.collisionRayTrace(Mint.INSTANCE.mc.world, blockpos, vec31, vec32);
        }

        RayTraceResult raytraceresult2 = null;
        int k1 = 200;

        while (k1-- >= 0) {
            if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
                return null;
            }

            if (l == i && i1 == j && j1 == k) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (i > l) {
                d0 = (double) l + 1.0D;
            } else if (i < l) {
                d0 = (double) l + 0.0D;
            } else {
                flag2 = false;
            }

            if (j > i1) {
                d1 = (double) i1 + 1.0D;
            } else if (j < i1) {
                d1 = (double) i1 + 0.0D;
            } else {
                flag = false;
            }

            if (k > j1) {
                d2 = (double) j1 + 1.0D;
            } else if (k < j1) {
                d2 = (double) j1 + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            final double d6 = vec32.x - vec31.x;
            final double d7 = vec32.y - vec31.y;
            final double d8 = vec32.z - vec31.z;

            if (flag2) {
                d3 = (d0 - vec31.x) / d6;
            }

            if (flag) {
                d4 = (d1 - vec31.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - vec31.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            EnumFacing enumfacing;

            if (d3 < d4 && d3 < d5) {
                enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
            } else {
                enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
            }

            l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
            j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            blockpos = new BlockPos(l, i1, j1);
            final IBlockState iblockstate1 = Mint.INSTANCE.mc.world.getBlockState(blockpos);
            final Block block1 = iblockstate1.getBlock();

            if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(Mint.INSTANCE.mc.world, blockpos) != Block.NULL_AABB) {
                if (block1.canCollideCheck(iblockstate1, stopOnLiquid) && block1 != Blocks.WEB) {
                    return iblockstate1.collisionRayTrace(Mint.INSTANCE.mc.world, blockpos, vec31, vec32);
                } else {
                    raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                }
            }
        }
        return returnLastUncollidableBlock ? raytraceresult2 : null;
    }


    public static float getDamageMultiplied(float damage) {
        final int diff = Mint.INSTANCE.mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static double yawDist(Entity e) {
        if (e != null) {
            Vec3d difference = e.getPositionVector().add(0.0, e.getEyeHeight() / 2.0f, 0.0).subtract(Mint.INSTANCE.mc.player.getPositionEyes(Mint.INSTANCE.mc.getRenderPartialTicks()));
            double d = Math.abs((double) Mint.INSTANCE.mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static float getFov() {
        return Mint.INSTANCE.mc.gameSettings.fovSetting;
    }


    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{Mint.INSTANCE.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - Mint.INSTANCE.mc.player.rotationYaw), Mint.INSTANCE.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - Mint.INSTANCE.mc.player.rotationPitch)};
    }

    public static EntityPlayer getTarget(final float range) {
        EntityPlayer currentTarget = null;
        for (int size = Mint.INSTANCE.mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = Mint.INSTANCE.mc.world.playerEntities.get(i);
            if (!EntityUtil.isntValid(player, range)) {
                if (currentTarget == null) {
                    currentTarget = player;
                } else if (Mint.INSTANCE.mc.player.getDistanceSq(player) < Mint.INSTANCE.mc.player.getDistanceSq(currentTarget)) {
                    currentTarget = player;
                }
            }
        }
        return currentTarget;
    }

    public static boolean isntValid(Entity entity, double range) {
        return entity == null || EntityUtil.isDead(entity) || entity.equals(Mint.INSTANCE.mc.player) || entity instanceof EntityPlayer && Mint.friendManager.isFriend(entity.getName()) || Mint.INSTANCE.mc.player.getDistanceSq(entity) > MathUtil.square(range);
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static boolean isSafe(Entity entity) {
        return EntityUtil.isSafe(entity, 0, false);
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + (double) Mint.INSTANCE.mc.player.getEyeHeight(), Mint.INSTANCE.mc.player.posZ);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<>();
        for (Vec3d vector : EntityUtil.getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = Mint.INSTANCE.mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow))
                continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = EntityUtil.getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }


    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isAlive(Entity entity) {
        return EntityUtil.isLiving(entity) && !entity.isDead && ((EntityLivingBase) entity).getHealth() >= 0.0f;
    }

    public static boolean isDead(Entity entity) {
        return !EntityUtil.isAlive(entity);
    }

    public static boolean isMoving() {
        return (double) Mint.INSTANCE.mc.player.moveForward != 0.0 || (double) Mint.INSTANCE.mc.player.moveStrafing != 0.0;
    }

    public static void packetJump(boolean offground) {
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + 0.4199999, Mint.INSTANCE.mc.player.posZ, offground));
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + 0.7531999, Mint.INSTANCE.mc.player.posZ, offground));
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + 1.0013359, Mint.INSTANCE.mc.player.posZ, offground));
        Mint.INSTANCE.mc.getConnection().sendPacket(new CPacketPlayer.Position(Mint.INSTANCE.mc.player.posX, Mint.INSTANCE.mc.player.posY + 1.1661092, Mint.INSTANCE.mc.player.posZ, offground));
    }

    public static Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D;
        return new Vec3d(x, y, z);
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(MathUtil.roundVec(entity.getPositionVector(), 0));
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static boolean isPlayerSafe(EntityPlayer target) {
        BlockPos playerPos = getPlayerPos(target);
        return (Mint.INSTANCE.mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.OBSIDIAN || Mint.INSTANCE.mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.BEDROCK) &&
                (Mint.INSTANCE.mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.OBSIDIAN || Mint.INSTANCE.mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.BEDROCK) &&
                (Mint.INSTANCE.mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.OBSIDIAN || Mint.INSTANCE.mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.BEDROCK) &&
                (Mint.INSTANCE.mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.OBSIDIAN || Mint.INSTANCE.mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.BEDROCK) &&
                (Mint.INSTANCE.mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.OBSIDIAN || Mint.INSTANCE.mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.BEDROCK);
    }

    public static boolean isBorderingChunk(final Entity boat, final Double mX, final Double mZ) {
        return Mint.INSTANCE.mc.world.getChunk((int) (boat.posX + mX) / 16, (int) (boat.posZ + mZ) / 16) instanceof EmptyChunk;
    }

    public static double getDefaultSpeed() {
        double defaultSpeed = 0.2873;
        if (Mint.INSTANCE.mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(Mint.INSTANCE.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        if (Mint.INSTANCE.mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            final int amplifier = Objects.requireNonNull(Mint.INSTANCE.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }
        return defaultSpeed;
    }

    public static double getBaseMotionSpeed() {
        double event = 0.272D;
        if (Mint.INSTANCE.mc.player.isPotionActive(MobEffects.SPEED)) {
            int var3 = Objects.requireNonNull(Mint.INSTANCE.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            event *= 1.0D + 0.2D * (double) var3;
        }
        return event;
    }

    public static boolean isInLiquid() {
        return Mint.INSTANCE.mc.player.isInWater() || Mint.INSTANCE.mc.player.isInLava();
    }

    public static boolean startSneaking() {
        if (mc.player != null) {
            if (mc.player.isSneaking()) {
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
        }
        return false;
    }

    public static boolean stopSneaking(boolean forceStop) {
        if (mc.player != null) {
            if (mc.player.isSneaking() || forceStop) {
                mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }
        }
        return false;
    }

    public static void setMotion(double motion) {
        if (mc.player != null) {
            if (mc.player.isRiding()) {
                mc.player.ridingEntity.motionX = motion;
                mc.player.ridingEntity.motionZ = motion;
            } else {
                mc.player.motionX = motion;
                mc.player.motionZ = motion;
            }
        }
    }

    public static void setMotion(double x, double y, double z){
        if (mc.player != null) {
            if (mc.player.isRiding()) {
                mc.player.ridingEntity.motionX = x;
                mc.player.ridingEntity.motionY = y;
                mc.player.ridingEntity.motionZ = z;
            } else {
                mc.player.motionX = x;
                mc.player.motionY = y;
                mc.player.motionZ = z;
            }
        }
    }

    //pasted
    public static void setSpeed(final EntityLivingBase entity, final double speed) {
        double[] dir = forward(speed);
        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }
}