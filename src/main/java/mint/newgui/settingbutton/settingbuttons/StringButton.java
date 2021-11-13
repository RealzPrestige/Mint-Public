package mint.newgui.settingbutton.settingbuttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.newgui.settingbutton.Button;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.StringSetting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class StringButton extends Button {
    SettingRewrite setting;
    StringSetting stringSetting;

    public StringButton(SettingRewrite setting, StringSetting stringSetting) {
        super(setting);
        this.setting = setting;
        this.stringSetting = stringSetting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(stringSetting.isOpen() ? stringSetting.getName() + " " + ChatFormatting.GRAY + stringSetting.getValue() + Mint.textManager.getIdleSign() : stringSetting.getName() + " " + ChatFormatting.GRAY + stringSetting.getValue(), x + 2, y, -1);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY) && mouseButton == 0) {
            stringSetting.setOpen(!stringSetting.isOpen());
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (!stringSetting.isOpen())
            return;

        if (keyCode == 14) {
            if (stringSetting.getValue() != null && stringSetting.getValue().length() > 0)
                stringSetting.setValue(stringSetting.getValue().substring(0, stringSetting.getValue().length() - 1));
        } else if (keyCode == 28)
            stringSetting.setOpen(false);
        else if (keyCode == 27)
            stringSetting.setOpen(false);
        else stringSetting.setValue(stringSetting.getValue() + "" + typedChar);
    }
}


