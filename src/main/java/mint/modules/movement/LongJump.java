package mint.modules.movement;

import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.EntityUtil;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "LongJump", category = Module.Category.Movement, description = "Jump farther lad.")
public class LongJump extends Module {

    public EnumSetting mode = new EnumSetting("Mode", Mode.Factor, this);

    public enum Mode {Factor}

    public FloatSetting acceleration = new FloatSetting("Acceleration", 30.0f, 0.0f, 100.0f, this, v -> mode.getValueEnum().equals(Mode.Factor));
    public BooleanSetting autoJump = new BooleanSetting("Auto Jump", true, this);
    public IntegerSetting jumpHeight = new IntegerSetting("Jump Height", 390, 350, 420, this);
    public BooleanSetting disableOnLag = new BooleanSetting("Disable On Lag", true, this);
    double playerSpeed;
    float jumpFactor;

    @Override
    public void onEnable() {
        playerSpeed = EntityUtil.getDefaultSpeed();
        jumpFactor = mc.player.jumpMovementFactor;
        if (mc.player.onGround && autoJump.getValue()) {
            mc.player.motionY = jumpHeight.getValue() / 1000.0f;
        }
    }

    @Override
    public void onDisable() {
        mc.player.jumpMovementFactor = jumpFactor;
    }

    @Override
    public void onUpdate() {
        if (mode.getValueEnum().equals(Mode.Factor)) {
            if (mc.player.onGround) {
                mc.player.jumpMovementFactor = jumpFactor;
            } else if (!(mc.player.jumpMovementFactor > 0.1f)) {
                mc.player.jumpMovementFactor += acceleration.getValue() / 100;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook && disableOnLag.getValue() && isEnabled()) {
            disable();
        }
    }
}