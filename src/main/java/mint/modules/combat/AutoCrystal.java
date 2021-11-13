package mint.modules.combat;

import mint.events.CrystalAttackEvent;
import mint.events.PacketEvent;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;


/**
 * @author zPrestige_
 * @since 05/10/21
 */
@SuppressWarnings("unchecked")
@ModuleInfo(name = "Auto Crystal", category = Module.Category.Combat, description = "Places and breaks crystals")
public class AutoCrystal extends Module {

    public static AutoCrystal INSTANCE = new AutoCrystal();
    public ParentSetting rangesParent = new ParentSetting("Ranges", false, this);
    public FloatSetting placeRange = new FloatSetting("Place Range", 5f, 0f, 6f, this, v -> rangesParent.getValue());
    public FloatSetting breakRange = new FloatSetting("Break Range", 5f, 0f, 6f, this, v -> rangesParent.getValue());
    public FloatSetting targetRange = new FloatSetting("Target Range", 10f, 0f, 15f, this, v -> rangesParent.getValue());

    public ParentSetting damagesParent = new ParentSetting("Damages", false, this);
    public FloatSetting minimumDamage = new FloatSetting("Minimum Damage", 6f, 0f, 16f, this, v -> damagesParent.getValue());
    public FloatSetting maximumSelfDamage = new FloatSetting("Maximum Self Damage", 6f, 0f, 16f, this, v -> damagesParent.getValue());
    public BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", false, this, v -> damagesParent.getValue());

    public ParentSetting predictParent = new ParentSetting("Predicts", false, this);
    public BooleanSetting soundPredict = new BooleanSetting("Sound Predict", false, this, v -> predictParent.getValue());
    public BooleanSetting breakPredict = new BooleanSetting("Break Predict", false, this, v -> predictParent.getValue());
    public BooleanSetting breakPredictCalc = new BooleanSetting("Break Predict Calc", false, this, v -> predictParent.getValue() && breakPredict.getValue());
    public BooleanSetting globalEntitySpawnPredict = new BooleanSetting("Global Entity Spawn Predict", false, this, v -> predictParent.getValue());
    public BooleanSetting spawnObject = new BooleanSetting("Spawn Object", false, this, v -> predictParent.getValue());

    public ParentSetting delayParent = new ParentSetting("Delays", false, this);
    public IntegerSetting placeDelay = new IntegerSetting("Place Delay", 100, 0, 500, this, v -> delayParent.getValue());
    public IntegerSetting breakDelay = new IntegerSetting("Break Delay", 100, 0, 500, this, v -> delayParent.getValue());
    public IntegerSetting predictDelay = new IntegerSetting("Break Delay", 100, 0, 500, this, v -> delayParent.getValue());

    public ParentSetting raytraceParent = new ParentSetting("Raytrace", false, this);
    public BooleanSetting placeRaytrace = new BooleanSetting("Place Raytrace", false, this, v -> raytraceParent.getValue());
    public FloatSetting placeRaytraceRange = new FloatSetting("Place Raytrace Range", 5f, 0f, 6f, this, v -> raytraceParent.getValue() && placeRaytrace.getValue());
    public BooleanSetting breakRaytrace = new BooleanSetting("Break Raytrace", false, this, v -> raytraceParent.getValue());
    public FloatSetting breakRaytraceRange = new FloatSetting("Break Raytrace Range", 5f, 0f, 6f, this, v -> raytraceParent.getValue() && breakRaytrace.getValue());

    public ParentSetting miscParent = new ParentSetting("Misc", false, this);
    public BooleanSetting preparePlace = new BooleanSetting("Prepare Place", true, this, v -> miscParent.getValue());
    public BooleanSetting updatedPlacements = new BooleanSetting("1.13+ Placements", false, this, v -> miscParent.getValue());
    public BooleanSetting limitAttack = new BooleanSetting("Limit Attack", false, this, v -> miscParent.getValue());
    public BooleanSetting packetBreak = new BooleanSetting("Packet Break", false, this, v -> miscParent.getValue());
    public BooleanSetting allowCollision = new BooleanSetting("Allow Collision", false, this, v -> miscParent.getValue());
    public BooleanSetting cancelVelocity = new BooleanSetting("Cancel Velocity", false, this, v -> miscParent.getValue());
    public BooleanSetting cancelExplosion = new BooleanSetting("Cancel Explosion", false, this, v -> miscParent.getValue());
    public BooleanSetting silentSwitch = new BooleanSetting("Silent Switch", false, this, v -> miscParent.getValue());

    public ParentSetting swingParent = new ParentSetting("Swings", false, this);
    public BooleanSetting placeSwing = new BooleanSetting("Place Swing", false, this, v -> swingParent.getValue());
    public EnumSetting placeSwingHand = new EnumSetting("PlaceSwingHand", PlaceSwingHand.MAINHAND, this, v -> placeSwing.getValue() && swingParent.getValue());

    public enum PlaceSwingHand {MAINHAND, OFFHAND, PACKET}

    public BooleanSetting breakSwing = new BooleanSetting("Break Swing", false, this, v -> swingParent.getValue());
    public EnumSetting breakSwingHand = new EnumSetting("BreakSwingHand", BreakSwingHand.MAINHAND, this, v -> breakSwing.getValue() && swingParent.getValue());

    public enum BreakSwingHand {MAINHAND, OFFHAND, PACKET}

    public ParentSetting facePlaceParent = new ParentSetting("Face Placing", false, this);
    public EnumSetting facePlaceMode = new EnumSetting("FacePlaceMode", FacePlaceMode.Never, this, v -> facePlaceParent.getValue());

    public enum FacePlaceMode {Never, Health, Bind, Always}

    public FloatSetting facePlaceHp = new FloatSetting("Face Place Health", 15f, 0f, 36f, this, v -> facePlaceMode.getValueEnum().equals(FacePlaceMode.Health) && facePlaceParent.getValue());
    public KeySetting facePlaceBind = new KeySetting("Face Place Bind", Keyboard.KEY_NONE, this, v -> facePlaceMode.getValueEnum().equals(FacePlaceMode.Bind) && facePlaceParent.getValue());

    public ParentSetting pauseParent = new ParentSetting("Pause Parent", false, this);
    public BooleanSetting pauseOnGapple = new BooleanSetting("Gapple", false, this, v -> pauseParent.getValue());
    public BooleanSetting pauseOnSword = new BooleanSetting("Pause On Sword", false, this, v -> pauseParent.getValue());
    public BooleanSetting pauseOnHealth = new BooleanSetting("Pause On Health", false, this, v -> pauseParent.getValue());
    public FloatSetting pauseHealth = new FloatSetting("Pause Health", 15f, 0f, 36f, this, v -> pauseParent.getValue() && pauseOnHealth.getValue());
    public BooleanSetting pauseOnExp = new BooleanSetting("Pause On Exp", false, this, v -> pauseParent.getValue());

    public ParentSetting renderParent = new ParentSetting("Renders", false, this);
    public BooleanSetting render = new BooleanSetting("Render", false, this, v -> renderParent.getValue());
    public BooleanSetting fade = new BooleanSetting("Fade", false, this, v -> render.getValue() && renderParent.getValue());
    public IntegerSetting startAlpha = new IntegerSetting("Start Alpha", 255, 0, 255, this, v -> render.getValue() && fade.getValue() && renderParent.getValue());
    public IntegerSetting endAlpha = new IntegerSetting("End Alpha", 0, 0, 255, this, v -> render.getValue() && fade.getValue() && renderParent.getValue());
    public IntegerSetting fadeSpeed = new IntegerSetting("Fade Speed", 20, 0, 100, this, v -> render.getValue() && fade.getValue() && renderParent.getValue());

    public BooleanSetting box = new BooleanSetting("Box", false, this, v -> render.getValue() && renderParent.getValue());
    public ColorSetting boxColor = new ColorSetting("Box Color", new Color(-1), this, v -> render.getValue() && box.getValue() && renderParent.getValue());

    public BooleanSetting outline = new BooleanSetting("Outline", false, this, v -> render.getValue() && renderParent.getValue());
    public ColorSetting outlineColor = new ColorSetting("Outline Color", new Color(-1), this, v -> render.getValue() && outline.getValue() && renderParent.getValue());
    public FloatSetting lineWidth = new FloatSetting("Line Width", 1f, 0f, 5f, this, v -> render.getValue() && outline.getValue() && renderParent.getValue());

    EntityPlayer targetPlayer;
    BlockPos finalPos;
    Timer placeTimer = new Timer();
    Timer breakTimer = new Timer();
    Timer predictTimer = new Timer();
    HashMap<BlockPos, Integer> possesToFade = new HashMap();
    bestPlacePos bestCrystalPos = new bestPlacePos(BlockPos.ORIGIN, 0);
    HashMap<Integer, Entity> attemptedEntityId = new HashMap();

    float mainTargetDamage;
    float mainTargetHealth;
    float mainMinimumDamageValue;
    float mainSelfHealth;
    int mainSlot;
    int mainOldSlot;

    public AutoCrystal() {
        this.setInstance();
    }

    public static AutoCrystal getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoCrystal();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public void onLogin() {
        if (isEnabled())
            disable();
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if ((pauseOnGapple.getValue() && mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE) && mc.gameSettings.keyBindUseItem.isKeyDown())
                || (pauseOnSword.getValue() && mc.player.getHeldItemMainhand().equals(Items.DIAMOND_SWORD))
                || (pauseOnExp.getValue() && mc.player.getHeldItemMainhand().equals(Items.EXPERIENCE_BOTTLE) && mc.gameSettings.keyBindUseItem.isKeyDown()
                || (pauseOnHealth.getValue() && mc.player.getHealth() + mc.player.getAbsorptionAmount() < pauseHealth.getValue())))
            return;

        targetPlayer = EntityUtil.getTarget(targetRange.getValue());

        if (targetPlayer == null || targetPlayer.isDead || targetPlayer.getHealth() == 0.0f)
            return;
        if (preparePlace.getValue() && breakTimer.passedMs((long) breakDelay.getValue()) && needsPlacePreparation()) {
            doBreak();
            breakTimer.reset();
        }

        if (placeTimer.passedMs((long) placeDelay.getValue())) {
            doPlace();
            placeTimer.reset();
        }

        if (breakTimer.passedMs((long) breakDelay.getValue())) {
            doBreak();
            breakTimer.reset();
        }
    }

    boolean needsPlacePreparation() {
        if (NullUtil.fullNullCheck())
            return false;

        if (targetPlayer == null || targetPlayer.isDead || targetPlayer.getHealth() == 0.0f)
            return false;

        bestCrystalPos = getBestPlacePos();

        if (bestCrystalPos == null)
            return false;

        return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(bestCrystalPos.getBlockPos().up())).isEmpty();
    }

    void doPlace() {

        int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int oldSlot = mc.player.inventory.currentItem;
        mainSlot = slot;
        mainOldSlot = oldSlot;

        bestCrystalPos = getBestPlacePos();

        if (bestCrystalPos == null)
            return;

        if (silentSwitch.getValue() && (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL && mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL))
            InventoryUtil.switchToSlot(slot);

        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(bestCrystalPos.getBlockPos(), EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));

        finalPos = bestCrystalPos.getBlockPos();

        if (render.getValue() && fade.getValue())
            possesToFade.put(bestCrystalPos.getBlockPos(), startAlpha.getValue());

        if (placeSwing.getValue())
            swingArm(true);

        if (silentSwitch.getValue() && (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL || mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL)) {
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        }
    }

    void doBreak() {
        if (targetPlayer == null || targetPlayer.isDead || targetPlayer.getHealth() == 0.0f)
            return;

        java.util.List<Entity> loadedEntityList = mc.world.loadedEntityList;
        if (!loadedEntityList.isEmpty())
            for (Entity entity : loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (!isPosGoodForzPrestigeRequirements(entity))
                        continue;

                    if (packetBreak.getValue())
                        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
                    else mc.playerController.attackEntity(mc.player, entity);

                    MinecraftForge.EVENT_BUS.post(new CrystalAttackEvent(entity.getEntityId(), entity));

                    if (breakSwing.getValue())
                        swingArm(false);
                }
            }
    }

    bestPlacePos getBestPlacePos() {
        TreeMap<Float, bestPlacePos> posList = new TreeMap<>();
        if (targetPlayer != null) {
            for (BlockPos pos : BlockUtil.getSphereAutoCrystal(placeRange.getValue(), true)) {
                float targetDamage = EntityUtil.calculatePosDamage(pos, targetPlayer);
                float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                float targetHealth = targetPlayer.getHealth() + targetPlayer.getAbsorptionAmount();
                float minimumDamageValue = minimumDamage.getValue();
                mainTargetDamage = targetDamage;
                mainTargetHealth = targetHealth;
                mainSelfHealth = selfHealth;
                mainMinimumDamageValue = minimumDamageValue;
                if (BlockUtil.isPosValidForCrystal(pos, updatedPlacements.getValue())) {
                    if (mc.player.getDistanceSq(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) > MathUtil.square(placeRange.getValue()))
                        continue;

                    if (!allowCollision.getValue() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty())
                        continue;

                    if (BlockUtil.isPlayerSafe(targetPlayer) && (facePlaceMode.getValueEnum().equals(FacePlaceMode.Always) || (facePlaceMode.getValueEnum().equals(FacePlaceMode.Health) && targetHealth < facePlaceHp.getValue()) || (facePlaceMode.getValueEnum().equals(FacePlaceMode.Bind) && facePlaceBind.getKey() != -1 && Keyboard.isKeyDown(facePlaceBind.getKey()))))
                        minimumDamageValue = 2;

                    if (targetDamage < minimumDamageValue)
                        continue;

                    if (placeRaytrace.getValue() && rayTraceCheckPos(new BlockPos(pos.getX(), pos.getY(), pos.getZ())) && mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f) > placeRaytraceRange.getValue())
                        continue;

                    posList.put(targetDamage, new bestPlacePos(pos, targetDamage));
                }
            }
            if (!posList.isEmpty()) {
                return posList.lastEntry().getValue();
            }
        }
        return null;
    }

    public boolean isPosGoodForzPrestigeRequirements(Entity entity) {
        float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        float selfDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, mc.player);
        float targetDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, targetPlayer);
        float targetHealth = targetPlayer.getHealth() + targetPlayer.getAbsorptionAmount();
        float minimumDamageValue = minimumDamage.getValue();

        if (mc.player.getDistanceSq(entity.posX + 0.5f, entity.posY, entity.posZ + 0.5f) > MathUtil.square(breakRange.getValue()))
            return false;

        if (BlockUtil.isPlayerSafe(targetPlayer) && (facePlaceMode.getValueEnum().equals(FacePlaceMode.Always) || (facePlaceMode.getValueEnum().equals(FacePlaceMode.Health) && targetHealth < facePlaceHp.getValue()) || (facePlaceMode.getValueEnum().equals(FacePlaceMode.Bind) && facePlaceBind.getKey() != -1 && Keyboard.isKeyDown(facePlaceBind.getKey()))))
            minimumDamageValue = 2;

        if (antiSuicide.getValue() && selfDamage > selfHealth)
            return false;

        if (selfDamage > maximumSelfDamage.getValue())
            return false;

        if (targetDamage < minimumDamageValue)
            return false;

        if (limitAttack.getValue() && attemptedEntityId.containsValue(entity))
            return false;

        return !breakRaytrace.getValue() || !rayTraceCheckPos(new BlockPos(entity.posX, entity.posY, entity.posZ)) || !(mc.player.getDistance(entity.posX + 0.5f, entity.posY + 1, entity.posZ + 0.5f) > breakRaytraceRange.getValue());
    }

    public boolean isPosGoodForzPrestigeRequirements(BlockPos pos) {
        float targetDamage = EntityUtil.calculatePosDamage(pos, targetPlayer);
        float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        float targetHealth = targetPlayer.getHealth() + targetPlayer.getAbsorptionAmount();
        float minimumDamageValue = minimumDamage.getValue();
        float selfDamage = EntityUtil.calculatePosDamage(pos, mc.player);
        if (BlockUtil.isPosValidForCrystal(pos, updatedPlacements.getValue())) {
            if (mc.player.getDistanceSq(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) > MathUtil.square(placeRange.getValue()))
                return false;

            if (!allowCollision.getValue() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos).setMaxY(1)).isEmpty())
                return false;

            if (BlockUtil.isPlayerSafe(targetPlayer) && (facePlaceMode.getValueEnum().equals(FacePlaceMode.Always) || (facePlaceMode.getValueEnum().equals(FacePlaceMode.Health) && targetHealth < facePlaceHp.getValue()) || (facePlaceMode.getValueEnum().equals(FacePlaceMode.Bind) && facePlaceBind.getKey() != -1 && Keyboard.isKeyDown(facePlaceBind.getKey()))))
                minimumDamageValue = 2;

            if (targetDamage < minimumDamageValue)
                return false;

            if (antiSuicide.getValue() && selfDamage > selfHealth)
                return false;

            if (selfDamage > maximumSelfDamage.getValue())
                return false;

            if (placeRaytrace.getValue() && rayTraceCheckPos(new BlockPos(pos.getX(), pos.getY(), pos.getZ())) && mc.player.getDistance(pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f) > placeRaytraceRange.getValue())
                return false;

            return true;
        }
        return false;
    }


    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!isEnabled() || targetPlayer == null || targetPlayer.isDead || targetPlayer.getHealth() == 0.0f)
            return;
        if (event.getPacket() instanceof SPacketExplosion) {
            if (cancelExplosion.getValue())
                event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketSpawnGlobalEntity && globalEntitySpawnPredict.getValue() && predictTimer.passedMs(predictDelay.getValue())) {
            CPacketUseEntity predict = new CPacketUseEntity();
            SPacketSpawnGlobalEntity packet = new SPacketSpawnGlobalEntity();
            if (mc.player.getDistanceSq(new BlockPos(packet.getX(), packet.getY(), packet.getZ())) > MathUtil.square(breakRange.getValue()))
                return;
            predict.entityId = packet.getEntityId();
            predict.action = CPacketUseEntity.Action.ATTACK;
            Objects.requireNonNull(mc.getConnection()).sendPacket(predict);
            predictTimer.reset();
        }
        if (event.getPacket() instanceof SPacketSpawnObject && spawnObject.getValue() && predictTimer.passedMs(predictDelay.getValue())) {
            CPacketUseEntity predict = new CPacketUseEntity();
            SPacketSpawnObject sPacketSpawnObject = new SPacketSpawnObject();
            if (mc.player.getDistanceSq(new BlockPos(sPacketSpawnObject.getX(), sPacketSpawnObject.getY(), sPacketSpawnObject.getZ())) > MathUtil.square(breakRange.getValue()))
                return;
            predict.entityId = sPacketSpawnObject.getEntityID();
            predict.action = CPacketUseEntity.Action.ATTACK;
            Objects.requireNonNull(mc.getConnection()).sendPacket(predict);
            predictTimer.reset();
        }
        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velocity = event.getPacket();

            if (cancelVelocity.getValue() && velocity.getEntityID() == mc.player.getEntityId())
                event.setCanceled(true);
        }
        if (breakPredict.getValue() && event.getPacket() instanceof SPacketSpawnObject && predictTimer.passedMs(predictDelay.getValue())) {
            final SPacketSpawnObject packet = event.getPacket();
            if (packet.getType() == 51 && finalPos != null && EntityUtil.getTarget(targetRange.getValue()) != null) {
                final CPacketUseEntity predict = new CPacketUseEntity();
                predict.entityId = packet.getEntityID();
                predict.action = CPacketUseEntity.Action.ATTACK;

                if (breakPredictCalc.getValue() && !isPosGoodForzPrestigeRequirements(new BlockPos(packet.getX(), packet.getY(), packet.getZ())))
                    return;

                mc.getConnection().sendPacket(predict);

                MinecraftForge.EVENT_BUS.post(new CrystalAttackEvent(predict.entityId, predict.getEntityFromWorld(mc.world)));

                if (breakSwing.getValue())
                    swingArm(false);
                predictTimer.reset();
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                java.util.List<Entity> loadedEntityList = mc.world.loadedEntityList;
                if (!loadedEntityList.isEmpty())
                    for (Entity entity : loadedEntityList) {
                        if (entity == null)
                            continue;
                        if (!(entity instanceof EntityEnderCrystal))
                            continue;

                            if (limitAttack.getValue() && attemptedEntityId.containsValue(entity.getEntityId()))
                                attemptedEntityId.remove(entity, entity.getEntityId());

                            if (entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) <= MathUtil.square(breakRange.getValue()))
                                entity.setDead();

                    }
            }
        }
    }

    public void swingArm(boolean place) {
        if (place) {
            if (placeSwingHand.getValueEnum().equals(PlaceSwingHand.MAINHAND))
                mc.player.swingArm(EnumHand.MAIN_HAND);
            if (placeSwingHand.getValueEnum().equals(PlaceSwingHand.OFFHAND))
                mc.player.swingArm(EnumHand.OFF_HAND);
            if (placeSwingHand.getValueEnum().equals(PlaceSwingHand.PACKET))
                mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        } else {
            if (breakSwingHand.getValueEnum().equals(BreakSwingHand.MAINHAND))
                mc.player.swingArm(EnumHand.MAIN_HAND);
            if (breakSwingHand.getValueEnum().equals(BreakSwingHand.OFFHAND))
                mc.player.swingArm(EnumHand.OFF_HAND);
            if (breakSwingHand.getValueEnum().equals(BreakSwingHand.PACKET))
                mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
        }
    }

    public void onEnable() {
        targetPlayer = null;
        finalPos = null;
    }

    public void onDisable() {
        targetPlayer = null;
        finalPos = null;
    }

    @SubscribeEvent
    public void onCrystalAttacked(CrystalAttackEvent event) {
        if (limitAttack.getValue())
            attemptedEntityId.put(event.getEntityId(), event.getEntity());
    }

    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (render.getValue()) {
            if (fade.getValue()) {
                for (Map.Entry<BlockPos, Integer> entry : possesToFade.entrySet()) {
                    possesToFade.put(entry.getKey(), entry.getValue() - (fadeSpeed.getValue() / 10));
                    if (entry.getValue() <= endAlpha.getValue()) {
                        possesToFade.remove(entry.getKey());
                        return;
                    }
                    RenderUtil.drawBoxESP(entry.getKey(), new Color(boxColor.getColor().getRed() / 255f, boxColor.getColor().getGreen() / 255f, boxColor.getColor().getBlue() / 255f, entry.getValue() / 255f), true, new Color(outlineColor.getColor().getRed() / 255f, outlineColor.getColor().getGreen() / 255f, outlineColor.getColor().getBlue() / 255f, entry.getValue() / 255f), lineWidth.getValue(), outline.getValue(), box.getValue(), entry.getValue(), true);
                }
            } else if (finalPos != null) {
                RenderUtil.drawBoxESP(finalPos, boxColor.getColor(), true, outlineColor.getColor(), lineWidth.getValue(), outline.getValue(), box.getValue(), boxColor.getColor().getAlpha(), true);
            }
        }
    }

    boolean rayTraceCheckPos(BlockPos pos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY(), pos.getZ()), false, true, false) != null;
    }

    static class bestPlacePos {
        BlockPos blockPos;
        float targetDamage;

        public bestPlacePos(BlockPos blockPos, float targetDamage) {
            this.blockPos = blockPos;
            this.targetDamage = targetDamage;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }
    }
}
