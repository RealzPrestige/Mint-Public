package mint.modules.visual;

import mint.events.PopEvent;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.NullUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

@ModuleInfo(name = "Pop ESP", category = Module.Category.Visual, description = "Renders an Entity where a player pops/dies.")
public class PopESP extends Module {

    /**
     * @author zPrestige & kambing & gerald
     */

    private static PopESP INSTANCE = new PopESP();

    public EntityOtherPlayerMP fakeEntity;

    public ParentSetting solidParent = new ParentSetting("Solid", false, this);
    public BooleanSetting solidSetting = new BooleanSetting("RenderSolid", true, this, v -> solidParent.getValue());
    public ColorSetting solidColor = new ColorSetting("Solid Color", new Color(-1), this, v -> solidSetting.getValue() && solidParent.getValue());

    public ParentSetting wireFrameParent = new ParentSetting("WireFrame", false, this);
    public BooleanSetting wireFrameSetting = new BooleanSetting("RenderWire", true, this, v -> wireFrameParent.getValue());
    public ColorSetting wireColor = new ColorSetting("Wire Color", new Color(-1), this, v -> wireFrameSetting.getValue() && wireFrameParent.getValue());

    public ParentSetting fadeParent = new ParentSetting("Fade", false, this);
    public IntegerSetting startAlpha = new IntegerSetting("StartAlpha", 255, 0, 255, this, v -> fadeParent.getValue());
    public IntegerSetting endAlpha = new IntegerSetting("EndAlpha", 0, 0, 255, this, v -> fadeParent.getValue());
    public IntegerSetting fadeStep = new IntegerSetting("FadeStep", 10, 10, 100, this, v -> fadeParent.getValue());

    public ParentSetting yTravelParent = new ParentSetting("YMovement", false, this);
    public BooleanSetting yTravel = new BooleanSetting("YTravel", false, this, v -> yTravelParent.getValue());
    public EnumSetting yTravelMode = new EnumSetting("TravelMode", YTravelMode.UP, this, v -> yTravelParent.getValue() && yTravel.getValue());

    public enum YTravelMode {UP, DOWN}

    public DoubleSetting yTravelSpeed = new DoubleSetting("TravelSpeed", 0.1, 0.0, 2.0, this, v -> yTravel.getValue());

    public ParentSetting miscParent = new ParentSetting("Misc", false, this);
    public BooleanSetting onDeath = new BooleanSetting("OnDeath", false, this, v -> miscParent.getValue());
    public BooleanSetting clearListOnPop = new BooleanSetting("ClearListOnPop", false, this, v -> miscParent.getValue());
    public BooleanSetting clearListOnDeath = new BooleanSetting("ClearListOnDeath", false, this, v -> miscParent.getValue());
    public BooleanSetting antiSelf = new BooleanSetting("AntiSelf", false, this, v -> miscParent.getValue());

    public HashMap<EntityPlayer, Integer> poppedPlayers = new HashMap<>();

    public PopESP() {
        this.setInstance();
    }

    public static PopESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if (NullUtil.fullNullCheck())
            return;

        for (Map.Entry<EntityPlayer, Integer> pop : poppedPlayers.entrySet()) {
            poppedPlayers.put(pop.getKey(), pop.getValue() - ((fadeStep.getValue() + 10) / 20));
            if (pop.getValue() <= endAlpha.getValue()) {
                poppedPlayers.remove(pop.getKey());
                return;
            }
            if (yTravel.getValue()) {
                if (yTravelMode.getValueEnum().equals(PopESP.YTravelMode.UP)) {
                    pop.getKey().posY = pop.getKey().posY + (yTravelSpeed.getValue() / 20);
                } else if (yTravelMode.getValueEnum().equals(PopESP.YTravelMode.DOWN)) {
                    pop.getKey().posY = pop.getKey().posY - (yTravelSpeed.getValue() / 20);
                }
            }
            if (wireFrameSetting.getValue()) {
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(1032, 6913);
                glDisable(3553);
                glDisable(2896);
                glDisable(2929);
                glEnable(2848);
                glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(wireColor.getColor().getRed() / 255f, wireColor.getColor().getGreen() / 255f, wireColor.getColor().getBlue() / 255f, pop.getValue() / 255f);
                renderEntityStatic(pop.getKey(), event.getPartialTicks(), false);
                GL11.glLineWidth(1f);
                glEnable(2896);
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
            if (solidSetting.getValue()) {
                GL11.glPushMatrix();
                GL11.glDepthRange(0.01, 1.0f);
                GL11.glPushAttrib(GL11.GL_ALL_CLIENT_ATTRIB_BITS);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(false);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GL11.glLineWidth(1f);
                GL11.glColor4f(solidColor.getColor().getRed() / 255f, solidColor.getColor().getGreen() / 255f, solidColor.getColor().getBlue() / 255f, pop.getValue() / 255f);
                renderEntityStatic(pop.getKey(), event.getPartialTicks(), false);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(true);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                GL11.glPopAttrib();
                GL11.glDepthRange(0.0, 1.0f);
                GL11.glPopMatrix();
            }
        }
    }

    @SubscribeEvent
    public void onPop(PopEvent event) {
        if (mc.world.getEntityByID(event.getEntityId()) != null) {
            if (antiSelf.getValue() && event.getEntityId() == mc.player.getEntityId()) {
                return;
            }
            final Entity entity = mc.world.getEntityByID(event.getEntityId());
            if (entity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) entity;
                fakeEntity = new EntityOtherPlayerMP(mc.world, player.getGameProfile());
                fakeEntity.copyLocationAndAnglesFrom(player);
                fakeEntity.rotationYawHead = player.rotationYawHead;
                fakeEntity.prevRotationYawHead = player.rotationYawHead;
                fakeEntity.rotationYaw = player.rotationYaw;
                fakeEntity.prevRotationYaw = player.rotationYaw;
                fakeEntity.rotationPitch = player.rotationPitch;
                fakeEntity.prevRotationPitch = player.rotationPitch;
                fakeEntity.cameraYaw = fakeEntity.rotationYaw;
                fakeEntity.cameraPitch = fakeEntity.rotationPitch;
                if (clearListOnPop.getValue()) {
                    poppedPlayers.clear();
                }
                poppedPlayers.put(fakeEntity, startAlpha.getValue());
            }
        }
    }

    public void onDeath(int entityId) {
        if (onDeath.getValue()) {
            if (mc.world.getEntityByID(entityId) != null) {
                final Entity entity = mc.world.getEntityByID(entityId);
                if (entity instanceof EntityPlayer) {
                    final EntityPlayer player = (EntityPlayer) entity;
                    fakeEntity = new EntityOtherPlayerMP(mc.world, player.getGameProfile());
                    fakeEntity.copyLocationAndAnglesFrom(player);
                    fakeEntity.rotationYawHead = player.rotationYawHead;
                    fakeEntity.prevRotationYawHead = player.rotationYawHead;
                    fakeEntity.rotationYaw = player.rotationYaw;
                    fakeEntity.prevRotationYaw = player.rotationYaw;
                    fakeEntity.rotationPitch = player.rotationPitch;
                    fakeEntity.prevRotationPitch = player.rotationPitch;
                    fakeEntity.cameraYaw = fakeEntity.rotationYaw;
                    fakeEntity.cameraPitch = fakeEntity.rotationPitch;
                    if (clearListOnDeath.getValue()) {
                        poppedPlayers.clear();
                    }
                    poppedPlayers.put(fakeEntity, startAlpha.getValue());
                }
            }
        }
    }

    public void handlePopESP(int entityId) {
        if (mc.world.getEntityByID(entityId) != null) {
            final Entity entity = mc.world.getEntityByID(entityId);
            if (entity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer) entity;
                fakeEntity = new EntityOtherPlayerMP(mc.world, player.getGameProfile());
                fakeEntity.copyLocationAndAnglesFrom(player);
                fakeEntity.rotationYawHead = player.rotationYawHead;
                fakeEntity.prevRotationYawHead = player.rotationYawHead;
                fakeEntity.rotationYaw = player.rotationYaw;
                fakeEntity.prevRotationYaw = player.rotationYaw;
                fakeEntity.rotationPitch = player.rotationPitch;
                fakeEntity.prevRotationPitch = player.rotationPitch;
                fakeEntity.cameraYaw = fakeEntity.rotationYaw;
                fakeEntity.cameraPitch = fakeEntity.rotationPitch;
                if (clearListOnDeath.getValue()) {
                    poppedPlayers.clear();
                }
                poppedPlayers.put(fakeEntity, startAlpha.getValue());
            }
        }
    }


    public void renderEntityStatic(Entity entityIn, float partialTicks, boolean p_188388_3_) {
        if (entityIn.ticksExisted == 0) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
        }
        double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
        double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
        double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
        float f = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
        int i = entityIn.getBrightnessForRender();
        if (entityIn.isBurning())
            i = 15728880;

        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);

        mc.getRenderManager().renderEntity(entityIn, d0 - mc.getRenderManager().viewerPosX, d1 - mc.getRenderManager().viewerPosY, d2 - mc.getRenderManager().viewerPosZ, f, partialTicks, p_188388_3_);
    }
}
