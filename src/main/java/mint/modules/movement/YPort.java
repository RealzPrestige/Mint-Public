package mint.modules.movement;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.DoubleSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.utils.EntityUtil;
import mint.utils.NullUtil;

@ModuleInfo(name = "YPort", category = Module.Category.Movement, description = "idk")
public class YPort extends Module {

    public DoubleSetting speed = new DoubleSetting("YPortSpeed", 0.1d, 0.0d, 1.0d, this);
    public FloatSetting fallSpeed = new FloatSetting("FallSpeed", 0.8f, 0.1f, 9.0f, this);
    public IntegerSetting yMotion = new IntegerSetting("YMotion", 390, 350, 420, this);

    @Override
    public void onToggle() {
        mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            disable();

        if (mc.player.isSneaking() || EntityUtil.isInLiquid() || mc.player.isOnLadder())
            return;

        mc.player.stepHeight = 2.0f;
        if (mc.player.onGround) {
            EntityUtil.setSpeed(mc.player, EntityUtil.getDefaultMoveSpeed() + speed.getValue());
            mc.player.motionY = yMotion.getValue() / 1000.0f;
        } else {
            for (double y = 0.0; y < 2.5 + 0.5; y += 0.01) {
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                    mc.player.motionY = -fallSpeed.getValue();
                    break;
                }
            }
        }
    }
}