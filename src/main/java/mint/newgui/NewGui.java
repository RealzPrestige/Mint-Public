package mint.newgui;

import mint.Mint;
import mint.modules.Module;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

public class NewGui extends GuiScreen {
    static NewGui INSTANCE = new NewGui();
    ArrayList<Window> windows = new ArrayList<>();

    public NewGui() {
        setInstance();
        load();
    }

    public static NewGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        windows.forEach(windows -> windows.drawScreen(mouseX, mouseY, partialTicks));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        windows.forEach(windows -> windows.mouseClicked(mouseX, mouseY, clickedButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releasedButton) {
        windows.forEach(windows -> windows.mouseReleased(mouseX, mouseY, releasedButton));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        windows.forEach(windows -> windows.onKeyTyped(typedChar, keyCode));
    }

    @Override
    public void initGui() {
        super.initGui();
        windows.forEach(Window::initGui);
    }

    public void load() {
        int x = -130;
        assert Mint.moduleManager != null;
        for (Module.Category categories : Mint.moduleManager.getCategories()) {
            windows.add(new Window(categories.getName(), x += 134, 10, 130, 10, categories));
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
