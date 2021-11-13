package mint.modules.movement;

import mint.events.EntityCollisionEvent;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.utils.NullUtil;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Velocity", category = Module.Category.Movement, description = "Changes Velocity of stuff")
public class Velocity extends Module {
    public BooleanSetting entityVelocity = new BooleanSetting("Entity Velocity", false, this);
    public BooleanSetting entityCollisionPush = new BooleanSetting("Entity Push Collision", false, this);
    public BooleanSetting entityCollisionBlock = new BooleanSetting("Entity Block Collision", false, this);
    public BooleanSetting webCollision = new BooleanSetting("Entity Web Collision", false, this);
    public EnumSetting webCollisionMode = new EnumSetting("Entity Web Collision Mode", WebCollisionMode.Speed, this, v -> webCollision.getValue());

    public enum WebCollisionMode {Speed, Cancel}

    public FloatSetting webCollisionSpeed = new FloatSetting("Web Speed", 1.0f, 0.1f, 50.0f, this, v -> webCollisionMode.getValueEnum().equals(WebCollisionMode.Speed));

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;

        if (event.getPacket() instanceof SPacketEntityVelocity)
            if (isEnabled())
                event.setCanceled(entityVelocity.getValue() && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId());
    }

    @SubscribeEvent
    public void onEntityCollision(EntityCollisionEvent.Entity event) {
        if (isEnabled())
            event.setCanceled(entityCollisionPush.getValue());
    }

    @SubscribeEvent
    public void onEntityCollision(EntityCollisionEvent.Block event) {
        if (isEnabled())
            event.setCanceled(entityCollisionBlock.getValue());
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (webCollisionMode.getValueEnum().equals(WebCollisionMode.Speed) && mc.player != null && mc.player.isInWeb && !mc.player.onGround && mc.gameSettings.keyBindSneak.isKeyDown())
            mc.player.motionY *= webCollisionSpeed.getMaximum();

        if (webCollisionMode.getValueEnum().equals(WebCollisionMode.Cancel) && mc.player.isInWeb)
            mc.player.isInWeb = false;
    }
}
