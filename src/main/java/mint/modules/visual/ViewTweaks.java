package mint.modules.visual;

import mint.modules.Module;
import mint.modules.ModuleInfo;
import mint.settingsrewrite.impl.*;
import mint.utils.NullUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author kambing, zPrestige
 */

@ModuleInfo(name = "View Tweaks", category = Module.Category.Visual, description = "Tweak how your game looks.")
public class ViewTweaks extends Module {

    private static ViewTweaks INSTANCE = new ViewTweaks();

    public BooleanSetting fullBright = new BooleanSetting("Full Bright", false, this);

    public ParentSetting fovParent = new ParentSetting("FOV", false, this);
    public BooleanSetting fov = new BooleanSetting("FOV Changer", false, this, v -> fovParent.getValue());
    public BooleanSetting fovStay = new BooleanSetting("FOV Stay", false, this, v -> fovParent.getValue());
    public FloatSetting fovValue = new FloatSetting("Fov Value", 140.0f, 0.0f, 180.0f, this, v -> fovParent.getValue());

    public BooleanSetting antiFog = new BooleanSetting("Anti Fog", false, this);
    public BooleanSetting noWeather = new BooleanSetting("No Weather", false, this);

    public ParentSetting timeParent = new ParentSetting("Time", false, this);
    public BooleanSetting timeChange = new BooleanSetting("Time Changer", true, this, v -> timeParent.getValue());
    public IntegerSetting time = new IntegerSetting("Time", 0, 0, 23000, this, v -> timeParent.getValue());

    public ParentSetting skyParent = new ParentSetting("Sky", false, this);
    public BooleanSetting skyColorChange = new BooleanSetting("Sky Color Changer", false, this, v -> skyParent.getValue());
    public BooleanSetting rainbow = new BooleanSetting("Rainbow", false, this, v -> skyParent.getValue());
    public ColorSetting color = new ColorSetting("Sky Color", new Color(-1), this, v -> skyParent.getValue() && !rainbow.getValue());
    public KeySetting bind = new KeySetting("Third Person", Keyboard.KEY_NONE, this);


    public ViewTweaks() {
        setInstance();
    }

    public static ViewTweaks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ViewTweaks();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (fullBright.getValue())
            mc.gameSettings.gammaSetting = 6969.0f;
    }

    @Override
    public void onUpdate() {
        if (NullUtil.fullNullCheck())
            return;
        if (bind.getKey() > -1)
            if (Keyboard.isKeyDown(bind.getKey()))
                mc.gameSettings.thirdPersonView = 1;
            else mc.gameSettings.thirdPersonView = 0;
        if (fov.getValue() && !fovStay.getValue())
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fovValue.getValue());
        if (mc.gameSettings.gammaSetting != 6969.0f && fullBright.getValue())
            mc.gameSettings.gammaSetting = 6969.0f;
        if (noWeather.getValue())
            mc.world.setRainStrength(0);
    }

    @SubscribeEvent
    public void onWorld(EntityViewRenderEvent.RenderFogEvent event) {
        if (isEnabled())
            if (timeChange.getValue()) {
                mc.world.setTotalWorldTime((long) time.getValue());
                mc.world.setWorldTime((long) time.getValue());
            }
    }

    public void onLogin() {
        if (isEnabled()) {
            disable();
            enable();
        }
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if(isEnabled())
        if (antiFog.getValue()) {
            event.setDensity(0.0f);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onFogColor(final EntityViewRenderEvent.FogColors event) {
        if (isEnabled())
            if (skyColorChange.getValue()) {
                event.setRed(color.getColor().getRed() / 255.0f);
                event.setGreen(color.getColor().getGreen() / 255.0f);
                event.setBlue(color.getColor().getBlue() / 255.0f);
            }
    }

    @SubscribeEvent
    public void onFovChange(EntityViewRenderEvent.FOVModifier event) {
        if(isEnabled())
        if (fovStay.getValue())
            event.setFOV(fovValue.getValue());
    }
}
