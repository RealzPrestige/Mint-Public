package mint.modules.movement;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.EntityUtil;
import mint.utils.NullUtil;

@ModuleInfo(name = "Reverse Step", category = Module.Category.Movement, description = "Fall down faster.")
public class ReverseStep extends Module {

    public EnumSetting mode = new EnumSetting("Mode", Mode.Vanilla, this);

    public enum Mode {Vanilla, Strict}

    public IntegerSetting height = new IntegerSetting("Height", 10, 1, 25, this);
    public FloatSetting vanillaSpeed = new FloatSetting("VanillaSpeed", 9.0f, 0.1f, 9.0f, this, v -> mode.getValueEnum().equals(Mode.Vanilla));
    public FloatSetting strictSpeed = new FloatSetting("StrictSpeed", 17.5f, 10.0f, 30.0f, this, v -> mode.getValueEnum().equals(Mode.Strict));

    //todo if someone is verie smart then rewrite this cuz i just pasted for() and collisionboxes
    @Override
    public void onUpdate() {
        if(NullUtil.fullNullCheck())
            return;
        if (mc.player != null && !EntityUtil.isInLiquid() && mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
            if (mode.getValueEnum().equals(Mode.Vanilla)) {
                for (double y = 0.0; y < height.getValue() + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY = -vanillaSpeed.getValue();
                        break;
                    }
                }
            } else {
                for (double y = 0.0; y < height.getValue() + 0.5; y += 0.01) {
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                        mc.player.motionY *= strictSpeed.getValue() / 10;
                        break;
                    }
                }
            }
        }
    }
}