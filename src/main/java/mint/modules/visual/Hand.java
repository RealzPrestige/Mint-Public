package mint.modules.visual;

import mint.events.RenderItemEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.FloatSetting;
import mint.settingsrewrite.impl.ParentSetting;
import mint.utils.NullUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "Hand", category = Module.Category.Visual, description = "Changes looks of ya sexy hand")
public class Hand extends Module {
    static Hand INSTANCE = new Hand();

    public ParentSetting mainhandParent = new ParentSetting("Mainhand", true, this);

    public ParentSetting mainhandTranslation = new ParentSetting("Mainhand Translate", true, this);
    public FloatSetting mainhandX = new FloatSetting("Mainhand X", 0.0f, -10.0f, 10.0f, this, v -> mainhandParent.getValue() && mainhandTranslation.getValue());
    public FloatSetting mainhandY = new FloatSetting("Mainhand Y", 0.0f, -10.0f, 10.0f, this, v -> mainhandParent.getValue() && mainhandTranslation.getValue());
    public FloatSetting mainhandZ = new FloatSetting("Mainhand Z", 0.0f, -10.0f, 10.0f, this, v -> mainhandParent.getValue() && mainhandTranslation.getValue());

    public ParentSetting mainhandScaling = new ParentSetting("Mainhand Scaling", false, this, v -> mainhandParent.getValue());
    public FloatSetting mainhandScaleX = new FloatSetting("Mainhand Scale X", 0.0f, -10.0f, 10.0f, this, v -> mainhandParent.getValue() && mainhandScaling.getValue());
    public FloatSetting mainhandScaleY = new FloatSetting("Mainhand Scale Y", 0.0f, -10.0f, 10.0f, this, v -> mainhandParent.getValue() && mainhandScaling.getValue());
    public FloatSetting mainhandScaleZ = new FloatSetting("Mainhand Scale Z", 0.0f, -10.0f, 10.0f, this, v -> mainhandParent.getValue() && mainhandScaling.getValue());

    public ParentSetting mainhandRotation =  new ParentSetting("Mainhand Rotation", false, this, v -> mainhandParent.getValue());
    public FloatSetting mainhandRotationX = new FloatSetting("Mainhand Rotation X", 0.0f, 0.0f, 10.0f, this, v -> mainhandParent.getValue() && mainhandRotation.getValue());
    public FloatSetting mainhandRotationY = new FloatSetting("Mainhand Rotation Y", 0.0f, 0.0f, 10.0f, this,v -> mainhandParent.getValue() && mainhandRotation.getValue());
    public FloatSetting mainhandRotationZ = new FloatSetting("Mainhand Rotation Z", 0.0f, 0.0f, 10.0f, this, v -> mainhandParent.getValue() && mainhandRotation.getValue());

    public ParentSetting offhandParent = new ParentSetting("Offhand", true, this);

    public ParentSetting offhandTranslation = new ParentSetting("Offhand Translation", false, this, v -> offhandParent.getValue());
    public FloatSetting offhandX = new FloatSetting("Offhand X", 0.0f, -10.0f, 10.0f, this,v -> offhandParent.getValue() && offhandTranslation.getValue());
    public FloatSetting offhandY = new FloatSetting("Offhand Y", 0.0f, -10.0f, 10.0f, this,v -> offhandParent.getValue() && offhandTranslation.getValue());
    public FloatSetting offhandZ = new FloatSetting("Offhand Z", 0.0f, -10.0f, 10.0f, this, v -> offhandParent.getValue() && offhandTranslation.getValue());

    public ParentSetting offhandScaling = new ParentSetting("Offhand Scaling", false, this, v -> offhandParent.getValue());
    public FloatSetting offhandScaleX = new FloatSetting("Offhand Scale X", 0.0f, -10.0f, 10.0f, this, v -> offhandParent.getValue() && offhandScaling.getValue());
    public FloatSetting offhandScaleY = new FloatSetting("Offhand Scale Y", 0.0f, -10.0f, 10.0f, this,v -> offhandParent.getValue() && offhandScaling.getValue());
    public FloatSetting offhandScaleZ = new FloatSetting("Offhand Scale Z", 0.0f, -10.0f, 10.0f, this, v -> offhandParent.getValue() && offhandScaling.getValue());

    public ParentSetting offhandRotation = new ParentSetting("Offhand Rotation", false, this, v -> offhandParent.getValue());
    public FloatSetting offhandRotationX = new FloatSetting("Offhand Rotation X", 0.0f, 0.0f, 10.0f,this,v -> offhandParent.getValue() && offhandRotation.getValue());
    public FloatSetting offhandRotationY = new FloatSetting("Offhand Rotation Y", 0.0f, 0.0f, 10.0f,this,v -> offhandParent.getValue() && offhandRotation.getValue());
    public FloatSetting offhandRotationZ = new FloatSetting("Offhand Rotation Z", 0.0f, 0.0f, 10.0f,this,v -> offhandParent.getValue() && offhandRotation.getValue());

    public FloatSetting alpha = new FloatSetting("Item Opacity", 255.0f, 0.0f, 255.0f,this);


    public Hand() {
        this.setInstance();
    }

    public static Hand getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Hand();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderMainhand(RenderItemEvent.MainHand event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;

        if (!event.getItemStack().getItem().equals(mc.player.getHeldItemMainhand().getItem()))
            return;

        GL11.glTranslated(mainhandX.getValue() / 40.0f, mainhandY.getValue() / 40.0f, mainhandZ.getValue() / 40.0f);
        GlStateManager.scale((mainhandScaleX.getValue() / 10.0f) + 1.0f, (mainhandScaleY.getValue() / 10.0f) + 1.0f, (mainhandScaleZ.getValue() / 10.0f) + 1.0f);
        GlStateManager.rotate(mainhandRotationX.getValue() * 36.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(mainhandRotationY.getValue() * 36.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mainhandRotationZ.getValue() * 36.0f, 0.0f, 0.0f, 1.0f);
    }

    @SubscribeEvent
    public void onRenderOffhand(RenderItemEvent.Offhand event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;
        if(!event.getItemStack().getItem().equals(mc.player.getHeldItemOffhand().getItem()))
            return;
        GL11.glTranslated(offhandX.getValue() / 40.0f, offhandY.getValue() / 40.0f, offhandZ.getValue() / 40.0f);
        GlStateManager.scale((offhandScaleX.getValue() / 10.0f) + 1.0f, (offhandScaleY.getValue() / 10.0f) + 1.0f, (offhandScaleZ.getValue() / 10.0f) + 1.0f);
        GlStateManager.rotate(offhandRotationX.getValue() * 36.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(offhandRotationY.getValue() * 36.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(offhandRotationZ.getValue() * 36.0f, 0.0f, 0.0f, 1.0f);
    }
}
