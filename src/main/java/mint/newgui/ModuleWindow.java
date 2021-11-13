package mint.newgui;

import mint.Mint;
import mint.modules.Module;
import mint.modules.core.NewGuiModule;
import mint.newgui.settingbutton.Button;
import mint.newgui.settingbutton.settingbuttons.*;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.*;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.awt.*;
import java.util.ArrayList;

public class ModuleWindow {
    public String name;
    public int x;
    public int y;
    public int width;
    public int height;
    public Color disabledColor;
    public Color enabledColor;
    public Module module;
    ArrayList<Button> newButton = new ArrayList<>();

    public ModuleWindow(String name, int x, int y, int width, int height, Color disabledColor, Color enabledColor, Module module) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.disabledColor = disabledColor;
        this.enabledColor = enabledColor;
        this.module = module;
        getSettings();
    }

    /**
     * Note for zprestige by kambing
     * <p>
     * It throws an exception because ur doing two loops at the same time
     * which java doesnt like that (java is a crybaby) xD
     */

    public void getSettings() {
        ArrayList<Button> settingList = new ArrayList<>();
        for (SettingRewrite settingsRewrite : module.getSettings()) {
            if (settingsRewrite instanceof BooleanSetting && !settingsRewrite.getName().equals("Enabled"))
                settingList.add(new BooleanButton(settingsRewrite));
            if (settingsRewrite instanceof IntegerSetting)
                settingList.add(new IntegerButton(settingsRewrite, (IntegerSetting) settingsRewrite));
            if (settingsRewrite instanceof FloatSetting)
                settingList.add(new FloatButton(settingsRewrite, (FloatSetting) settingsRewrite));
            if (settingsRewrite instanceof DoubleSetting)
                settingList.add(new DoubleButton(settingsRewrite, (DoubleSetting) settingsRewrite));
            if (settingsRewrite instanceof EnumSetting)
                settingList.add(new EnumButton(settingsRewrite, (EnumSetting) settingsRewrite));
            if (settingsRewrite instanceof StringSetting)
                settingList.add(new StringButton(settingsRewrite, (StringSetting) settingsRewrite));
            if (settingsRewrite instanceof ColorSetting)
                settingList.add(new ColorButton(settingsRewrite, (ColorSetting) settingsRewrite));
            if (settingsRewrite instanceof ParentSetting)
                settingList.add(new ParentButton(settingsRewrite, (ParentSetting) settingsRewrite));
            if (settingsRewrite instanceof KeySetting)
                settingList.add(new KeyButton(settingsRewrite, (KeySetting) settingsRewrite));
        }
        newButton = settingList;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + width, y + height, NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        if (module.isEnabled())
            RenderUtil.drawRect(x + 1, y, x + width - 1, y + height, enabledColor.getRGB());
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x + 1, y, x + width - 1, y + height, ColorUtil.toRGBA(0, 0, 0, 100));
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(name, isInside(mouseX, mouseY) ? x + 2 : x + 1, y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        if (module.isOpened) {
            int y = this.y;
            for (Button button : newButton) {
                if (button.isVisible()) {
                    button.setX(x + 2);
                    button.setY(y += height);
                    button.setWidth(width - 4);
                    button.setHeight(button instanceof EnumButton ? height + 4 : height);
                    button.drawScreen(mouseX, mouseY, partialTicks);
                    if (button instanceof EnumButton)
                        y += 4;
                    if (button instanceof ColorButton && ((ColorButton) button).getColorSetting().isOpen()) {
                        y += 112;
                        if (((ColorButton) button).getColorSetting().isSelected())
                            y += 10;
                    }
                }
            }
            RenderUtil.drawOutlineRect(x + 2, this.y + height, x + width - 2, y + height - 1, NewGuiModule.getInstance().color.getColor(), 1f);
        }
    }


    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && isInside(mouseX, mouseY)) {
            module.isOpened = !module.isOpened;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        if (isInside(mouseX, mouseY) && mouseButton == 0)
            if (module.isEnabled())
                module.disable();
            else module.enable();

        newButton.forEach(newButton -> newButton.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void initGui() {
        if (module.isOpened)
            newButton.forEach(Button::initGui);
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        newButton.forEach(newButton -> newButton.onKeyTyped(typedChar, keyCode));
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    public int getHeight() {
        return height;
    }
}
