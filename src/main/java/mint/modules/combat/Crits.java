package mint.modules.combat;

import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Crits", category = Module.Category.Combat, description = "Automatically makes your hits turn into critical hits.")
public class Crits extends Module {

    public BooleanSetting targetParent = new BooleanSetting("Target", true, this);
    public BooleanSetting crystal = new BooleanSetting("End Crystals", false, this, v -> targetParent.getValue());
    public BooleanSetting pauseInLiquids = new BooleanSetting("Pause In Liquids", true, this);
    public BooleanSetting confirmPos = new BooleanSetting("Confirm Position", true, this);

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (e.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity) e.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
            if (!(e.getPacket() instanceof EntityEnderCrystal && crystal.getValue()) || ((mc.player.isInLava() || mc.player.isInWater()) && pauseInLiquids.getValue()) && !mc.player.onGround) {
                return;
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.10000000149011612, mc.player.posZ, false));
            if (confirmPos.getValue()) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            }
        }
    }
}