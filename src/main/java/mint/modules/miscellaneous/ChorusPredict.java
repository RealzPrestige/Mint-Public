package mint.modules.miscellaneous;

import mint.events.ChorusEvent;
import mint.events.RenderWorldEvent;
import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.ColorUtil;
import mint.utils.NullUtil;
import mint.utils.RenderUtil;
import mint.utils.Timer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@ModuleInfo(name = "Chorus Predict", category = Module.Category.Miscellaneous, description = "Renders where a player's chorus will go to.")
public class ChorusPredict extends Module {

    public IntegerSetting time = new IntegerSetting("Duration", 500, 50, 3000, this);
    public ParentSetting boxParent = new ParentSetting("Box", false, this);
    public BooleanSetting box = new BooleanSetting("Box Setting", false, this, v -> boxParent.getValue());
    public ColorSetting boxColor = new ColorSetting("Box Color", new Color(-1), this, v -> box.getValue() && boxParent.getValue());

    public ParentSetting outlineParent = new ParentSetting("Outline", false, this);
    public BooleanSetting outline = new BooleanSetting("OutlineSetting", true, this, v -> outlineParent.getValue());
    public ColorSetting outlineColor = new ColorSetting("Outline Color", new Color(-1), this, v -> outline.getValue() && outlineParent.getValue());
    public FloatSetting lineWidth = new FloatSetting("LineWidth", 1.0f, 0.1f, 5.0f, this, v -> outline.getValue() && outlineParent.getValue());

    public Timer timer = new Timer();
    public double x;
    public double y;
    public double z;
    long startTime;
    int alpha = boxColor.getColor().getAlpha();
    int alphaOutline = outlineColor.getColor().getAlpha();
    long urMom;
    double normal;

    public void onLogin() {
        if (isEnabled()) {
            disable();
            enable();
        }
    }

    @SubscribeEvent
    public void onEntityChorus(ChorusEvent event) {
        if (!isEnabled() || NullUtil.fullNullCheck())
            return;
        x = event.getEventPosX();
        y = event.getEventY();
        z = event.getEventZ();
        timer.reset();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void renderWorldLastEvent(RenderWorldEvent render3DEvent) {
        if (timer.passedMs(time.getValue())) return;
        if (System.currentTimeMillis() - this.startTime > this.time.getValue().longValue()) {
            urMom = System.currentTimeMillis() - this.startTime - this.time.getValue().longValue();
            normal = normalize(urMom, 0.0, this.time.getValue().doubleValue());
            normal = MathHelper.clamp(normal, 0.0, 1.0);
            normal = -normal + 1.0;
            alpha = (int) (normal * (double) alpha);
            alphaOutline = (int) (normal * (double) alpha);
        }
        AxisAlignedBB pos = RenderUtil.interpolateAxis(new AxisAlignedBB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3));
        if (outline.getValue())
            RenderUtil.drawBlockOutline(pos, new Color(outlineColor.getColor().getRed(), outlineColor.getColor().getGreen(), outlineColor.getColor().getBlue(), alphaOutline), lineWidth.getValue());
        if (box.getValue())
            RenderUtil.drawFilledBox(pos, ColorUtil.toRGBA(boxColor.getColor().getRed(), boxColor.getColor().getGreen(), boxColor.getColor().getBlue(), alpha));
    }

    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}