package mint.modules.miscellaneous;

import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.settingsrewrite.impl.ParentSetting;
import mint.utils.NullUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Packet Manipulator", category = Module.Category.Miscellaneous, description = "Manipulates packets.")
public class PacketManipulator extends Module {

    /*
    a test thingy dont mind it for now
     */

    public ParentSetting cancel = new ParentSetting("Cancel", false, this);
    public BooleanSetting s = new BooleanSetting("Server", false, this, v -> cancel.getValue());

    public ParentSetting c = new ParentSetting("Client", false, this, v -> cancel.getValue());
    public BooleanSetting cplayer = new BooleanSetting("Player", true, this);

    public ParentSetting send = new ParentSetting("Send", false, this);
    public IntegerSetting packet1 = new IntegerSetting("Packet1", 1, 1, 10, this, v -> send.getValue());

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        if (e.getPacket() instanceof CPacketPlayer && cplayer.getValue()) {
            e.setCanceled(true);
        }
    }
}