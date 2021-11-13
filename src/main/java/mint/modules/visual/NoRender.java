package mint.modules.visual;

import mint.events.PacketEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.utils.NullUtil;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "No Render", category = Module.Category.Visual, description = "Renders No")
public class NoRender extends Module {
    static NoRender INSTANCE = new NoRender();

    public BooleanSetting fire = new BooleanSetting("Fire Overlay", false, this);
    public BooleanSetting hurtCam = new BooleanSetting("Hurt Camera Effect", false, this);
    public BooleanSetting insideBlocks = new BooleanSetting("Inside Blocks Overlay", false, this);
    public BooleanSetting explosions = new BooleanSetting("Explosions Effect", false, this);
    public BooleanSetting armorRemover = new BooleanSetting("Armor Remover", false, this);

    public NoRender() {
        setInstance();
    }

    public void onLogin() {
        if (!isEnabled())
            return;

        disable();
        enable();
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;

        if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.FIRE))
            event.setCanceled(fire.getValue());

        if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.BLOCK))
            event.setCanceled(insideBlocks.getValue());
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;

        if (event.getPacket() instanceof SPacketExplosion)
            event.setCanceled(explosions.getValue());
    }

    public static NoRender getInstance() {
        if (INSTANCE == null)
            INSTANCE = new NoRender();
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }
}
