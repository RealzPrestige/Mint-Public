package mint.modules.player;

import mint.Mint;
import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.EnumSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Anti Aim", category = Module.Category.Player, description = "Spoofs your yaw and pitch.")
public class AntiAim extends Module {

    public EnumSetting mode = new EnumSetting("Mode", Mode.Custom, this);
    public IntegerSetting yaw = new IntegerSetting("Yaw", 90, -90, 90, this, v -> mode.getValueEnum().equals(Mode.Custom));
    public IntegerSetting pitch = new IntegerSetting("Pitch", 90, -90, 90, this, v -> mode.getValueEnum().equals(Mode.Custom));
    public IntegerSetting spinSpeed = new IntegerSetting("SpinSpeed", 10, 0, 50, this, v -> mode.getValueEnum().equals(Mode.Spin));
    int nextValue;

    @Override
    public void onUpdate() {
        nextValue += spinSpeed.getValue();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (!isEnabled()) {
            return;
        }
        if (e.getPacket() instanceof CPacketPlayer && !Mint.INSTANCE.mc.player.isHandActive()) {
            if (mode.getValueEnum().equals(Mode.Custom)) {
                ((CPacketPlayer) e.getPacket()).yaw = yaw.getValue();
                ((CPacketPlayer) e.getPacket()).pitch = pitch.getValue();
            } else if (mode.getValueEnum().equals(Mode.Spin)) {
                ((CPacketPlayer) e.getPacket()).yaw = nextValue;
                ((CPacketPlayer) e.getPacket()).pitch = nextValue;
            }
        }
    }

    public enum Mode {
        Custom,
        Spin
    }
}