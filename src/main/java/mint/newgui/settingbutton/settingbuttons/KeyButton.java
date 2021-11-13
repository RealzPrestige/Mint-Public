package mint.newgui.settingbutton.settingbuttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.newgui.settingbutton.Button;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.KeySetting;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Keyboard;

public class KeyButton extends Button {
    KeySetting keySetting;
    public KeyButton(SettingRewrite setting, KeySetting keySetting) {
        super(setting);
        this.keySetting = keySetting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x - 2, y, x + width + 2, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(keySetting.isTyping ? keySetting.getName() + " " + Mint.textManager.getIdleSign() : keySetting.getName() + " " + ChatFormatting.GRAY + (keySetting.getKey() == -1 ? "None" : Keyboard.getKeyName(keySetting.getKey())), x + 2, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isInside(mouseX, mouseY)) {
            keySetting.isTyping = !keySetting.isTyping;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode) {
        if (!keySetting.isTyping)
            return;
        if (keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_ESCAPE)
            keySetting.setBind(0);
        else keySetting.setBind(keyCode);

        Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        keySetting.isTyping = !keySetting.isTyping;
    }
}
