package mint.modules.movement;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.DoubleSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.FloatSetting;
import mint.utils.EntityUtil;
import mint.utils.NullUtil;
import mint.utils.PlayerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;

import java.util.Objects;

@ModuleInfo(name = "Water Speed", category = Module.Category.Movement, description = "Makes swim fast vroom vroom in le water")
public class WaterSpeed extends Module {

    public BooleanSetting packetSneak = new BooleanSetting("Packet Sneak", false, this);
    public DoubleSetting upFactor = new DoubleSetting("Up Factor", 1.0, 0.0, 20.0, this);
    public DoubleSetting downFactor = new DoubleSetting("Down Factor", 1.0, 0.0, 20.0, this);
    public DoubleSetting horizontalFactor = new DoubleSetting("Horizontal Factor", 1.0, 0.0, 20.0, this);
    public BooleanSetting consistent = new BooleanSetting("Consistent", false, this);
    public EnumSetting onGround = new EnumSetting("On Ground", OnGround.Cancel, this);
    public BooleanSetting useTimer = new BooleanSetting("Use Timer", false, this);
    public FloatSetting timerAmount = new FloatSetting("Timer Amount", 1.1f, 1.0f, 2.0f, this, v -> useTimer.getValue());

    public enum OnGround {Cancel, Offground}

    boolean isPacketSneaking;

    public void onToggle() {
        mc.timer.tickLength = 50.0f / 1.0f;
    }

    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;

        if (!(mc.player.isInWater() || mc.player.isInLava()))
            return;

        if (mc.world.getBlockState(PlayerUtil.getPlayerPos(mc.player)).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(PlayerUtil.getPlayerPos(mc.player).up()).getBlock().equals(Blocks.AIR))
            return;

        if (onGround.getValueEnum().equals(OnGround.Cancel) && mc.player.onGround)
            return;

        if (onGround.getValueEnum().equals(OnGround.Offground) && mc.player.onGround)
            mc.player.onGround = false;

        if (useTimer.getValue() && EntityUtil.isMoving())
            mc.timer.tickLength = 50.0F / timerAmount.getValue();
        else mc.timer.tickLength = 50.0f / 1.0f;

        if (packetSneak.getValue() && !mc.gameSettings.keyBindSneak.isKeyDown() && !isPacketSneaking) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isPacketSneaking = true;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.player.motionY = upFactor.getValue() / 40.0;
        else if (consistent.getValue())
            mc.player.motionY = 0.0;

        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.player.motionY = -downFactor.getValue() / 40.0;
        else if (consistent.getValue())
            mc.player.motionY = 0.0f;

        if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
            mc.player.motionX *= horizontalFactor.getValue() / 10;
            mc.player.motionZ *= horizontalFactor.getValue() / 10;
        }

        if (isPacketSneaking) {
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isPacketSneaking = false;
        }

    }
}
