package mint.modules.visual;

import mint.events.CrystalTextureEvent;
import mint.events.RenderCrystalEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.NullUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Crystal Chams", category = Module.Category.Visual, description = "Changes crystal looks :))")
public class CrystalChams extends Module {
    public BooleanSetting crystalBlend = new BooleanSetting("Crystal Blend", false, this);
    public BooleanSetting glintBlend = new BooleanSetting("Glint Blend", false, this);
    public IntegerSetting rotations = new IntegerSetting("Rotations", 30, 0, 200, this);
    public DoubleSetting scale = new DoubleSetting("Scale", 1.0, 0.0, 2.0, this);
    public FloatSetting lineWidth = new FloatSetting("Line Width", 1.0f, 0.1f, 3.0f, this);
    public ColorSetting color = new ColorSetting("Color", new Color(0xBC672AC6, true), this);
    static final ResourceLocation RES_ITEM_GLINT;


    static {
        RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    }

    @SubscribeEvent
    public void renderCrystalTexture(CrystalTextureEvent event) {
        if (isEnabled())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderCrystalPre(RenderCrystalEvent.RenderCrystalPreEvent event) {
        if (isEnabled())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderCrystalPost(RenderCrystalEvent.RenderCrystalPostEvent event) {
        if (NullUtil.fullNullCheck() || !isEnabled())
            return;
        glPushMatrix();
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        float rotation = event.getEntityEnderCrystal().innerRotation + event.getPartialTicks();
        float rotationMoved = MathHelper.sin(rotation * 0.2F) / 2 + 0.5F;
        rotationMoved += Math.pow(rotationMoved, 2);
        glTranslated(event.getX(), event.getY(), event.getZ());
        glScaled(scale.getValue(), scale.getValue(), scale.getValue());
        if (crystalBlend.getValue())
            glEnable(GL_BLEND);
        glDepthMask(false);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
        GL11.glTexCoord3d(1.0, 1.0, 1.0);
        GL11.glEnable(3553);
        GL11.glBlendFunc(768, 771);
        if (glintBlend.getValue()) {
            GL11.glBlendFunc(770, 32772);
        } else {
            GL11.glBlendFunc(770, 771);
        }
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth((float) ((double) lineWidth.getValue()));
        GL11.glColor4f(color.getColor().getRed() / 255f, color.getColor().getGreen() / 255f, color.getColor().getBlue() / 255f, color.getColor().getAlpha() / 255f);
        glPolygonMode(GL_FRONT_AND_BACK, GL_POLYGON_MODE);
        event.getModelNoBase().render(event.getEntityEnderCrystal(), 0, rotation * (rotations.getValue() / 10), rotationMoved * 0.2F, 0, 0, 0.0625F);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        event.getModelNoBase().render(event.getEntityEnderCrystal(), 0, rotation * (rotations.getValue() / 10), rotationMoved * 0.2F, 0, 0, 0.0625F);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_TEXTURE_2D);
        glDepthMask(true);
        if (crystalBlend.getValue())
            glDisable(GL_BLEND);
        glPopAttrib();
        glPopMatrix();
    }
}
