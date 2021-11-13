package mint.newgui.settingbutton.settingbuttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.newgui.settingbutton.Button;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.EnumSetting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;

public class EnumButton extends Button {
    SettingRewrite setting;
    EnumSetting modeSetting;

    public EnumButton(SettingRewrite setting, EnumSetting modeSetting) {
        super(setting);
        this.setting = setting;
        this.modeSetting = modeSetting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(modeSetting.getName() + " " + ChatFormatting.GRAY + modeSetting.getValueEnum().toString(), x + 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        int x = 0;
        for (Enum<?> enums : modeSetting.getConstants()) {
            RenderUtil.drawRect(this.x + x + (width / 2f) - (x / 2f) - ((Mint.textManager.getStringWidth("_") * modeSetting.getConstants().length) / 1.5f), y + height - 3, this.x + x + (width / 2f) - (x / 2f) - ((Mint.textManager.getStringWidth("_") * modeSetting.getConstants().length) / 1.5f) + 5, y + height - 2, modeSetting.getValueEnum().equals(enums) ? new Color(255, 255, 255).getRGB() : NewGuiModule.getInstance().color.getColor().getRGB());
            RenderUtil.drawOutlineRect(this.x + x + (width / 2f) - (x / 2f) - ((Mint.textManager.getStringWidth("_") * modeSetting.getConstants().length) / 1.5f), y + height - 3, this.x + x + (width / 2f) - (x / 2f) - ((Mint.textManager.getStringWidth("_") * modeSetting.getConstants().length) / 1.5f) + 5, y + height - 2,new Color(0,0,0), 0.1f);
            x += 15;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY)) {
            if (mouseButton == 0)
                modeSetting.increase();
            else if (mouseButton == 1)
                modeSetting.decrease();

            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
}
