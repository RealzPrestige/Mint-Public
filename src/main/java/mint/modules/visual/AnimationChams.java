package mint.modules.visual;

import mint.events.RenderLivingEntityEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.BooleanSetting;
import mint.settingsrewrite.impl.IntegerSetting;
import mint.settingsrewrite.impl.StringSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "AnimationChams", category = Module.Category.Visual, description = "put image and it moves wow so amazing")
public class AnimationChams extends Module {
    public IntegerSetting alpha = new IntegerSetting("Alpha", 50, 0, 255, this);
    public IntegerSetting speed = new IntegerSetting("Speed", 1, 0, 20, this);
    public IntegerSetting scale = new IntegerSetting("Scale", 1, 0, 20, this);
    public IntegerSetting factor = new IntegerSetting("Factor", 1, 1, 5, this);
    public BooleanSetting custom = new BooleanSetting("Custom", false, this);
    public StringSetting filePath = new StringSetting("Path", "", this, v -> custom.getValue());
    final ResourceLocation RES_ITEM_GLINT;

    public AnimationChams() {
    }

    { //TODO: make custom png xDDDDDDDDDDDDDDDDDD
        RES_ITEM_GLINT = new ResourceLocation( "textures/misc/enchanted_item_glint.png");
    }

    @SubscribeEvent
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        Render<Entity> renderLiving = mc.renderManager.getEntityRenderObject(event.getEntityLivingBase());
        float newScale = 0.33333334F * scale.getValue();
        float f = event.getEntityLivingBase().ticksExisted + mc.getRenderPartialTicks();
        mc.entityRenderer.setupFogColor(true);
        GlStateManager.enableTexture2D();
        renderLiving.bindTexture(RES_ITEM_GLINT);
        GlStateManager.depthFunc(GL_EQUAL);
        GlStateManager.color(0.5f, 0.5f, 0.5f, alpha.getValue() / 255F);

        for (int i = 0; i < factor.getValue(); i++) {
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);

            //       GlStateManager.color(0.38f, 0.19f, 0.608f, 1.0f);
            GlStateManager.matrixMode(GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.scale(newScale, newScale, newScale);
            GlStateManager.rotate(30.0f - i * 60.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(0.0f, f * (0.001f + i * 0.003f) * speed.getValue(), 0.0f);
            GlStateManager.matrixMode(GL_MODELVIEW);
            event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

    }
}
