package mint.modules.movement;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.utils.EntityUtil;
import mint.utils.NullUtil;
import net.minecraft.network.play.client.CPacketPlayer;

@ModuleInfo(name = "Clip", category = Module.Category.Movement, description = "VClip bypass for crystalpvp cc.")
public class Clip extends Module {

    public FloatSetting offset = new FloatSetting("Y Offset", 1.4f, 1.1f, 2.0f, this);
    public BooleanSetting offground = new BooleanSetting("Off ground", false, this);

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            disable();
        
        if (mc.player.posY < 6 && !mc.player.isElytraFlying() && mc.player.onGround) {
            EntityUtil.startSneaking();
            EntityUtil.packetJump(offground.getValue());
            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, ((mc.player.posY + offset.getValue()) * -1), mc.player.posZ, offground.getValue()));
            EntityUtil.stopSneaking(false);
            disable();
        }
    }
}