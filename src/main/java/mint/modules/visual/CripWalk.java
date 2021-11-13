package mint.modules.visual;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.utils.NullUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mint.events.RenderWorldEvent;
@ModuleInfo(name = "Crip Walk", description = "Gang shit" , category = Module.Category.Visual)
public class CripWalk extends Module {

    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldEvent event) {
        if(NullUtil.fullNullCheck())
            return;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getName() == mc.player.getName())
                continue;
            player.limbSwing = 0;
            player.limbSwingAmount = 0;
            player.prevLimbSwingAmount = 0;
            player.rotationYawHead = 0;
            player.rotationPitch = 0;
            player.rotationYaw = 0;
        }
    }
}
