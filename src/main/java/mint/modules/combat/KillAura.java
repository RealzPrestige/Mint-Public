package mint.modules.combat;

import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.InventoryUtil;
import mint.utils.NullUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

@ModuleInfo(name = "Kill Aura", category = Module.Category.Combat, description = "Automatically attacks entities.")
public class KillAura extends Module {

    //delay
    public BooleanSetting delayParent = new BooleanSetting("Delay", true, this);
    public BooleanSetting attackDelay =new BooleanSetting("AttackDelay", true, this, v -> delayParent.getValue());
    public IntegerSetting attackSpeed = new IntegerSetting("AttackSpeed", 10, 2, 18, this, v -> delayParent.getValue());

    //target
    public ParentSetting targetParent = new ParentSetting("Targets", true, this);
    public BooleanSetting players = new BooleanSetting("Players", true,this, v -> targetParent.getValue());
    public BooleanSetting mobs = new BooleanSetting("Mobs", true,this, v -> targetParent.getValue());
    public BooleanSetting animals = new BooleanSetting("Animals", true,this, v -> targetParent.getValue());

    //render
    public BooleanSetting renderParent = new BooleanSetting("Render", true, this);
    public BooleanSetting render = new BooleanSetting("Render", true, this, v -> renderParent.getValue());
    public EnumSetting renderMode = new EnumSetting("RenderMode", RenderMode.BOTH, this, v -> renderParent.getValue() && render.getValue());

    public enum RenderMode {BOTH, OUTLINE, FILL}

    public ColorSetting color = new ColorSetting("Color", new Color(-1), this, v -> renderParent.getValue() && render.getValue());
    public IntegerSetting lineWidth = new IntegerSetting("LineWidth", 1, 0, 3, this, v -> renderParent.getValue() && render.getValue());
    //misc
    public BooleanSetting miscParent = new BooleanSetting("Misc", true, this);
    public BooleanSetting onlySword = new BooleanSetting("OnlySword", false, this, v -> miscParent.getValue());
    public IntegerSetting range = new IntegerSetting("Range", 4, 1, 6, this, v -> miscParent.getValue());
    public BooleanSetting rotate = new BooleanSetting("Rotate", false, this, v -> miscParent.getValue());
    public BooleanSetting switchToSword = new BooleanSetting("SwitchToSword", true, this, v -> miscParent.getValue());
    public Entity target = null;

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        for (Entity e : mc.world.loadedEntityList) {
            int swordSlot = InventoryUtil.getItemSlot(Items.DIAMOND_SWORD);
            if (shouldAttack(e)) {
                if (swordSlot != -1 && switchToSword.getValue() && mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD) {
                    InventoryUtil.switchToSlot(swordSlot);
                }
                if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && onlySword.getValue()) {
                    if (attackDelay.getValue()) {
                        if (mc.player.getCooledAttackStrength(0.0f) >= 1.0f) {
                            mc.playerController.attackEntity(mc.player, e);
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            target = e;
                        }
                    } else {
                        if (mc.player.ticksExisted % attackSpeed.getValue() == 0.0) {
                            mc.playerController.attackEntity(mc.player, e);
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            target = e;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        boolean fill = false;
        boolean outline = false;
        if (renderMode.getValueEnum().equals(RenderMode.BOTH)) {
            fill = true;
            outline = true;
        } else if (renderMode.getValueEnum().equals(RenderMode.FILL)) {
            fill = true;
            outline = false;
        } else if (renderMode.getValueEnum().equals(RenderMode.OUTLINE)) {
            fill = false;
            outline = true;
        }
        if (render.getValue() && target != null) {
            AxisAlignedBB bb = target.getEntityBoundingBox().offset(-mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ);
            RenderUtil.prepare();
            if (fill)
                RenderGlobal.renderFilledBox(bb, color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue(), color.getColor().getAlpha());
            if (outline) {
                GlStateManager.glLineWidth(lineWidth.getValue());
                RenderGlobal.drawSelectionBoundingBox(bb, color.getColor().getRed(), color.getColor().getGreen(), color.getColor().getBlue(), color.getColor().getAlpha());
            }
            RenderUtil.release();
        }
    }

    public void onLogin() {
        disable();
    }

    @Override
    public void onDisable() {
        target = null;
    }

    public boolean shouldAttack(Entity entity) {
        if (entity.equals(mc.player)) return false;
        if (!(entity instanceof EntityLivingBase)) return false;
        if (entity.isDead || !entity.isEntityAlive() || ((EntityLivingBase) entity).getHealth() < 0) return false;
        if ((entity instanceof EntityPlayer) && !players.getValue()) return false;
        if ((entity instanceof EntityMob || entity instanceof EntitySlime) && !mobs.getValue()) return false;
        if ((entity instanceof EntityAnimal) && !animals.getValue()) return false;
        return entity.getDistance(mc.player) <= range.getValue();
    }
}