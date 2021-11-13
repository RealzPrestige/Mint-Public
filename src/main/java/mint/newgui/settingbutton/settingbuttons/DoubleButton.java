package mint.newgui.settingbutton.settingbuttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.newgui.settingbutton.Button;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.DoubleSetting;
import mint.utils.ColorUtil;
import mint.utils.MathUtil;
import mint.utils.RenderUtil;
import org.lwjgl.input.Mouse;

public class DoubleButton extends Button {

    int minimax;
    DoubleSetting doubleSetting;
    Number min;
    Number max;

    public DoubleButton(SettingRewrite setting, DoubleSetting doubleSetting) {
        super(setting);
        this.doubleSetting = doubleSetting;
        min = doubleSetting.getMinimum();
        max = doubleSetting.getMaximum();
        minimax = max.intValue() - min.intValue();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragSlider(mouseX, mouseY);
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        RenderUtil.drawRect(x, y, ((Number) doubleSetting.getValue()).doubleValue() <= doubleSetting.getMinimum() ? x : (float) (x + ((float) width + 2f) * ((((Number) doubleSetting.getValue()).doubleValue() - doubleSetting.getMinimum()) / (doubleSetting.getMaximum() - doubleSetting.getMinimum())) - 2), y + (float) height, NewGuiModule.getInstance().color.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(doubleSetting.getName() + " " + ChatFormatting.GRAY + MathUtil.round(doubleSetting.getValue(), 2), x + 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
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
        if (doubleSetting.getValue() != null) {
            double result = doubleSetting.getMinimum() + (double) ((float) minimax * percent);
            doubleSetting.setValue(MathUtil.round(10.0 * result, 2) / 10.0);
        }
    }

}
