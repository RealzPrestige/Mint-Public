package mint.newgui.settingbutton.settingbuttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.newgui.settingbutton.Button;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.FloatSetting;
import mint.utils.ColorUtil;
import mint.utils.MathUtil;
import mint.utils.RenderUtil;
import org.lwjgl.input.Mouse;

public class FloatButton extends Button {

    int minimax;
    FloatSetting floatSetting;
    Number min;
    Number max;

    public FloatButton(SettingRewrite setting, FloatSetting floatSetting) {
        super(setting);
        this.floatSetting = floatSetting;
        min = floatSetting.getMinimum();
        max = floatSetting.getMaximum();
        minimax = max.intValue() - min.intValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragSlider(mouseX, mouseY);
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        RenderUtil.drawRect(x, y, ((Number) floatSetting.getValue()).floatValue() <= floatSetting.getMinimum() ? x : x + ((float) width + 2f) * ((((Number) floatSetting.getValue()).floatValue() - floatSetting.getMinimum()) / (floatSetting.getMaximum() - floatSetting.getMinimum())) - 2, y + (float) height, NewGuiModule.getInstance().color.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(floatSetting.getName() + " " + ChatFormatting.GRAY + MathUtil.round(floatSetting.getValue(), 1), x + 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
    }

    void dragSlider(int mouseX, int mouseY) {
        if (isInsideProper(mouseX, mouseY) && Mouse.isButtonDown(0))
            setSliderValue(mouseX);
    }

    public boolean isInsideProper(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width - 3) && (mouseY > y && mouseY < y + height);
    }

    void setSliderValue(int mouseX) {
        float percent = ((float) mouseX - x - 1) / ((float) width - 5);
        if (floatSetting.getValue() != null) {
            float result = floatSetting.getMinimum() + (float) minimax * percent;
            floatSetting.setValue((float) Math.round(10.0f * result) / 10.0f);
        }
    }

}
