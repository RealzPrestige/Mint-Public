package mint.modules.player;

import mint.events.PacketEvent;
import mint.events.RenderWorldEvent;
import mint.managers.MessageManager;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.ColorSetting;
import mint.settingsrewrite.impl.ParentSetting;
import mint.utils.NullUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;


/**
 * @author zPrestige_
 * Inspired by KamiV
 * @since 24/09/2021
 */

@ModuleInfo(name = "Chorus Manipulator", category = Module.Category.Player, description = "Manipulates your Chorus Fruits.")
public class ChorusManipulator extends Module {
    public static ChorusManipulator INSTANCE = new ChorusManipulator();
    public BooleanSetting cancel = new BooleanSetting("Cancel", false, this);
    public BooleanSetting tpOnSwitch = new BooleanSetting("Tp on Switch", true, this);
    public ParentSetting solidParent = new ParentSetting("Solid", false, this);
    public BooleanSetting solidSetting = new BooleanSetting("Render Solid", true, this, v -> solidParent.getValue());
    public ColorSetting solidColor = new ColorSetting("Solid Red", new Color(-1), this, v -> solidParent.getValue() && solidSetting.getValue());
    public ParentSetting wireFrameParent = new ParentSetting("Wire Frame", false, this);
    public BooleanSetting wireFrameSetting = new BooleanSetting("Render Wire", true, this, v -> wireFrameParent.getValue());
    public ColorSetting wireColor = new ColorSetting("Wire Color", new Color(-1), this, v -> wireFrameParent.getValue() && wireFrameSetting.getValue());

    Queue<CPacketPlayer> packets;
    Queue<CPacketConfirmTeleport> tpPackets;
    public EntityOtherPlayerMP fakeEntity;

    double xPos;
    double yPos;
    double zPos;

    public boolean isCancelled = false;

    public HashMap<EntityPlayer, Integer> playerCham = new HashMap<>();

    public ChorusManipulator() {
        packets = new LinkedList<>();
        tpPackets = new LinkedList<>();
    }

    public void onLogout() {
        disable();
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;

        if (event.getPacket() instanceof SPacketPlayerPosLook && cancel.getValue()) {
            xPos = ((SPacketPlayerPosLook) event.getPacket()).getX();
            yPos = ((SPacketPlayerPosLook) event.getPacket()).getY();
            zPos = ((SPacketPlayerPosLook) event.getPacket()).getZ();
            playerCham.clear();
            onChorus();
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            packets.add(event.getPacket());
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketConfirmTeleport) {
            tpPackets.add(event.getPacket());
            event.setCanceled(true);
        }
    }

    public void onTick() {
        if (isCancelled && mc.player.getHeldItemMainhand().getItem() != Items.CHORUS_FRUIT && tpOnSwitch.getValue()) {
            while (!packets.isEmpty()) {
                Objects.requireNonNull(mc.getConnection()).sendPacket(Objects.requireNonNull(packets.poll()));
            }
            while (!tpPackets.isEmpty()) {
                Objects.requireNonNull(mc.getConnection()).sendPacket(Objects.requireNonNull(tpPackets.poll()));
            }
            playerCham.clear();
            isCancelled = false;
        }
    }

    @Override
    public void onDisable() {
        while (!packets.isEmpty()) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(Objects.requireNonNull(packets.poll()));
        }
        while (!tpPackets.isEmpty()) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(Objects.requireNonNull(tpPackets.poll()));
        }
        playerCham.clear();
        isCancelled = false;
    }

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldEvent event) {
        for (Map.Entry<EntityPlayer, Integer> pop : playerCham.entrySet()) {
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
                GL11.glColor4f(wireColor.getColor().getRed() / 255f, wireColor.getColor().getGreen() / 255f, wireColor.getColor().getBlue() / 255f, wireColor.getColor().getAlpha() / 255f);
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
                GL11.glColor4f(solidColor.getColor().getRed() / 255f, solidColor.getColor().getGreen() / 255f, solidColor.getColor().getBlue()/ 255f, solidColor.getColor().getAlpha() / 255f);
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


    public void onChorus() {
        if (mc.world.getEntityByID(mc.player.getEntityId()) != null) {
            final EntityPlayer entity = mc.player;
            if (entity != null) {
                fakeEntity = new EntityOtherPlayerMP(mc.world, entity.getGameProfile());
                fakeEntity.posX = xPos;
                fakeEntity.posY = yPos;
                fakeEntity.posZ = zPos;
                fakeEntity.rotationYawHead = entity.rotationYawHead;
                fakeEntity.prevRotationYawHead = entity.rotationYawHead;
                fakeEntity.rotationYaw = entity.rotationYaw;
                fakeEntity.prevRotationYaw = entity.rotationYaw;
                fakeEntity.rotationPitch = entity.rotationPitch;
                fakeEntity.prevRotationPitch = entity.rotationPitch;
                fakeEntity.cameraYaw = fakeEntity.rotationYaw;
                fakeEntity.cameraPitch = fakeEntity.rotationPitch;
                playerCham.put(fakeEntity, 255);
                if (isCancelled = false) {
                    MessageManager.sendMessage("TEST isCancelled has been set [True] TEST");
                    isCancelled = true;
                }
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
        if (entityIn.isBurning()) {
            i = 15728880;
        }
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        mc.getRenderManager().renderEntity(entityIn, d0 - mc.getRenderManager().viewerPosX, d1 - mc.getRenderManager().viewerPosY, d2 - mc.getRenderManager().viewerPosZ, f, partialTicks, p_188388_3_);
    }
}
