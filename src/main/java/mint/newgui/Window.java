package mint.newgui;

import mint.Mint;
import mint.modules.Module;
import mint.modules.core.NewGuiModule;
import mint.settingsrewrite.SettingRewrite;
import mint.settingsrewrite.impl.ColorSetting;
import mint.settingsrewrite.impl.EnumSetting;
import mint.utils.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

import java.util.ArrayList;

public class Window {

    String name;
    int x;
    int y;
    int width;
    int height;
    Module.Category category;

    boolean isDragging;
    int dragX;
    int dragY;
    boolean isOpened;

    ArrayList<ModuleWindow> modules = new ArrayList<>();

    public Window(String name, int x, int y, int width, int height, Module.Category category) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;
        isOpened = true;
    }

    public void dragScreen(int mouseX, int mouseY) {
        if (!isDragging)
            return;
        x = dragX + mouseX;
        y = dragY + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        dragScreen(mouseX, mouseY);
        RenderUtil.drawRect(x - 1, y, x + width + 1, y + height, NewGuiModule.getInstance().color.getColor().getRGB());
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(name, x + (width / 2f) - (Mint.textManager.getStringWidth(name) / 2f), y + (height / 2f) - (Mint.textManager.getFontHeight() / 2f), -1);
        if (isOpened) {
            modules.clear();
            int y = this.y;
            assert Mint.moduleManager != null;
            for (Module module : Mint.moduleManager.getModulesByCategory(category)) {
                int openedHeight = 0;
                if (module.isOpened) {
                    assert Mint.settingsRewrite != null;
                    for (SettingRewrite settingsRewrite : module.getSettings()) {
                        if (settingsRewrite.isVisible())
                            openedHeight += 10;
                        if (settingsRewrite instanceof EnumSetting && settingsRewrite.isVisible())
                            openedHeight += 4;

                        if (settingsRewrite instanceof ColorSetting && settingsRewrite.isVisible()) {
                            if (((ColorSetting) settingsRewrite).isOpen()) {
                                openedHeight += 112;
                                if (((ColorSetting) settingsRewrite).isSelected())
                                    openedHeight += 10;
                            }
                        }
                    }
                }
                modules.add(new ModuleWindow(module.getName(), x, y += height, width, height, NewGuiModule.getInstance().backgroundColor.getColor(), NewGuiModule.getInstance().color.getColor(), module));
                y += openedHeight;
            }
            RenderUtil.drawOutlineRect(x, this.y + height, x + width, y + height, NewGuiModule.getInstance().color.getColor(), 1.5f);
        }
        RenderUtil.drawOutlineRect(x, this.y, x + width, this.y + height, NewGuiModule.getInstance().color.getColor(), 1.5f);
        if (isOpened)
            modules.forEach(modules -> modules.drawScreen(mouseX, mouseY, partialTicks));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isInside(mouseX, mouseY)) {
            dragX = x - mouseX;
            dragY = y - mouseY;
            isDragging = true;
        }
        if (mouseButton == 1 && isInside(mouseX, mouseY)) {
            isOpened = !isOpened;
            Mint.INSTANCE.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        if (isOpened)
            modules.forEach(modules -> modules.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (isOpened)
            modules.forEach(modules -> modules.onKeyTyped(typedChar, keyCode));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0)
            isDragging = false;
    }

    public void initGui() {
        if (isOpened)
            modules.forEach(ModuleWindow::initGui);
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }
}
