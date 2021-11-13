package mint.modules.movement;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.settingsrewrite.impl.ParentSetting;
import mint.utils.EntityUtil;
import mint.utils.MathUtil;
import mint.utils.NullUtil;
import net.minecraft.entity.item.EntityBoat;

@ModuleInfo(name = "Boat Fly", category = Module.Category.Movement, description = "Fly using boats.")
public class BoatFly extends Module {

    public ParentSetting spoofParent = new ParentSetting("Spoof", false, this);
    public BooleanSetting noClip = new BooleanSetting("NoClip", true, this, v -> spoofParent.getValue());
    public BooleanSetting onGround = new BooleanSetting("OnGround", false, this, v -> spoofParent.getValue());
    public BooleanSetting cancelGravity = new BooleanSetting("CancelGravity", true, this, v -> spoofParent.getValue());

    public ParentSetting flightParent = new ParentSetting("Flight", false, this);
    public BooleanSetting stopUnloaded = new BooleanSetting("Pause In Unloaded", true, this);
    public FloatSetting hSpeed = new FloatSetting("Horizontal speed", 2.0f, 0.1f, 2.5f, this, v -> flightParent.getValue());
    public FloatSetting vSpeed = new FloatSetting("Vertical speed", 2.0f, 0.1f, 2.5f, this, v -> flightParent.getValue());

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (!(mc.player.ridingEntity instanceof EntityBoat)) {
            return;
        }

        if (EntityUtil.isBorderingChunk(mc.player.ridingEntity, mc.player.ridingEntity.motionX, mc.player.ridingEntity.motionZ) && stopUnloaded.getValue()) {
            return;
        }

        mc.player.ridingEntity.noClip = noClip.getValue();
        mc.player.ridingEntity.setNoGravity(cancelGravity.getValue());
        mc.player.ridingEntity.onGround = onGround.getValue();

        final double[] normalDir = MathUtil.directionSpeed(hSpeed.getValue() / 0.4f);
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.ridingEntity.motionX = normalDir[0];
            mc.player.ridingEntity.motionZ = normalDir[1];
        } else {
            EntityUtil.setMotion(0.0);
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.ridingEntity.motionY = vSpeed.getValue() / 2;
        }

        if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.ridingEntity.motionY = -vSpeed.getValue() / 2;
        }
    }
}