package mint.modules.miscellaneous;

import com.mojang.authlib.GameProfile;
import mint.Mint;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.modules.core.Notifications;
import mint.modules.visual.PopESP;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.StringSetting;
import mint.utils.NullUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.UUID;

@ModuleInfo(name = "Fake Player", category = Module.Category.Miscellaneous, description = "Spawns a fake entity to test modules on.")
public class FakePlayer extends Module {
    public BooleanSetting inv = new BooleanSetting("Inv", false, this);
    public BooleanSetting pop = new BooleanSetting("Pop", false, this);
    public BooleanSetting moving = new BooleanSetting("Moving", false, this);
    public StringSetting name = new StringSetting("Name", "MintClient", this);
    private EntityOtherPlayerMP fake_player;

    @Override
    public void onEnable() {
        if (NullUtil.fullNullCheck()) {
            return;
        }
        fake_player = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("ee11ee92-8148-47e8-b416-72908a6a2275"), name.getValue()));
        fake_player.copyLocationAndAnglesFrom(mc.player);
        fake_player.rotationYawHead = mc.player.rotationYawHead;
        if (inv.getValue()) {
            fake_player.inventory = mc.player.inventory;
        }
        fake_player.setHealth(36);
        mc.world.addEntityToWorld(-100, fake_player);
    }

    public void onLogout() {
        if (isEnabled()) {
            disable();
        }
    }
    public void onLogin() {
        if (isEnabled()) {
            disable();
        }
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck()) {
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (NullUtil.fullNullCheck()) {
                return;
            }
            mc.world.removeEntity(fake_player);
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (pop.getValue() && isEnabled()) {
            if (event.getPacket() instanceof SPacketDestroyEntities) {
                final SPacketDestroyEntities packet = event.getPacket();
                for (int id : packet.getEntityIDs()) {
                    final Entity entity = mc.world.getEntityByID(id);
                    if (entity instanceof EntityEnderCrystal) {
                        if (entity.getDistanceSq(fake_player) < 144) {
                            final float rawDamage = calculateDamage(entity.posX, entity.posY, entity.posZ, fake_player);
                            final float absorption = fake_player.getAbsorptionAmount() - rawDamage;
                            final boolean hasHealthDmg = absorption < 0;
                            final float health = fake_player.getHealth() + absorption;

                            if (hasHealthDmg && health > 0) {
                                fake_player.setHealth(health);
                                fake_player.setAbsorptionAmount(0);
                            } else if (health > 0) {
                                fake_player.setAbsorptionAmount(absorption);
                            } else {
                                fake_player.setHealth(2);
                                fake_player.setAbsorptionAmount(8);
                                fake_player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 5));
                                fake_player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1));

                                try {
                                    mc.player.connection.handleEntityStatus(new SPacketEntityStatus(fake_player, (byte) 35));
                                } catch (Exception ignored) {
                                }
                                PopESP.getInstance().handlePopESP(fake_player.entityId);
                                if (Notifications.TotemPopCounter.containsKey(fake_player)) {
                                    int times = Notifications.TotemPopCounter.get(fake_player) + 1;
                                    Notifications.TotemPopCounter.remove(fake_player);
                                    Notifications.TotemPopCounter.put(fake_player.getName(), times);
                                } else {
                                    Notifications.TotemPopCounter.put(fake_player.getName(), 1);
                                }
                            }

                            fake_player.hurtTime = 5;
                        }
                    }
                }
            }
        }
    }
    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception exception) {}
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int) ((v * v + v) / 2.0 * 7.0 * (double) doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(Mint.INSTANCE.mc.world, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float) finald;
    }
    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception exception) {}
            float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = Mint.INSTANCE.mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    public void onTick() {
        if (fake_player != null) {
            Random random = new Random();
            fake_player.moveForward = mc.player.moveForward + (random.nextInt(5) / 10F);
            fake_player.moveStrafing = mc.player.moveStrafing + (random.nextInt(5) / 10F);
            if (moving.getValue()) {
                moveFakePlayer(fake_player.moveStrafing, fake_player.moveVertical, fake_player.moveForward);
            }
        }
    }

    public void moveFakePlayer(float strafe, float vertical, float forward) {
        double moveFactor0 = fake_player.posY;
        float moveFactor1 = 0.8F;
        float moveFactor2 = 0.02F;
        float moveFactor3 = (float) EnchantmentHelper.getDepthStriderModifier(fake_player);

        if (moveFactor3 > 3.0F) {
            moveFactor3 = 3.0F;
        }

        if (!fake_player.onGround) {
            moveFactor3 *= 0.5F;
        }

        if (moveFactor3 > 0.0F) {
            moveFactor1 += (0.54600006F - moveFactor1) * moveFactor3 / 3.0F;
            moveFactor2 += (fake_player.getAIMoveSpeed() - moveFactor2) * moveFactor3 / 4.0F;
        }

        fake_player.moveRelative(strafe, vertical, forward, moveFactor2);
        fake_player.move(MoverType.SELF, fake_player.motionX, fake_player.motionY, fake_player.motionZ);
        fake_player.motionX *= moveFactor1;
        fake_player.motionY *= 0.800000011920929D;
        fake_player.motionZ *= moveFactor1;

        if (!fake_player.hasNoGravity()) {
            fake_player.motionY -= 0.03D;
        }

        if (fake_player.collidedHorizontally && fake_player.isOffsetPositionInLiquid(fake_player.motionX, fake_player.motionY + 0.6000000238418579D - fake_player.posY + moveFactor0, fake_player.motionZ)) {
            fake_player.motionY = 0.30000001192092896D;
        }
    }
}