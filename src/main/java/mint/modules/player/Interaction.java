package mint.modules.player;

import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.utils.NullUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoat;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Interaction", category = Module.Category.Player, description = "Tweaks player's interactions.")
public class Interaction extends Module {

    //todo add shit like echest canceller(when trying to open an echest it just doesnt open it)
    public BooleanSetting placementParent = new BooleanSetting("Placement", true, this);
    public BooleanSetting boatPlace = new BooleanSetting("Boat", true, this, v -> placementParent.getValue());
    public BooleanSetting blockPlace = new BooleanSetting("Illegal Blocks", false, this, v -> placementParent.getValue());
    boolean cancelled;

    @Override
    public void onToggle() {
        cancelled = false;
    }

    @Override
    public void onUpdate() {
        if (cancelled) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(mc.objectMouseOver.getBlockPos(), EnumFacing.SOUTH, EnumHand.MAIN_HAND, 1, 1, 1));
            cancelled = false;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        //tryuseitemonblock packet + held item check(boat) + boatPlace setting check
        if (e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItemMainhand().getItem() instanceof ItemBoat && boatPlace.getValue()) {
            e.setCanceled(true);
        }

        //tryuseitem packet + held item check(block) + blockPlace setting check
        if (e.getPacket() instanceof CPacketPlayerTryUseItem && mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && blockPlace.getValue()) {
            e.setCanceled(true);
            cancelled = true;
        }
    }
}