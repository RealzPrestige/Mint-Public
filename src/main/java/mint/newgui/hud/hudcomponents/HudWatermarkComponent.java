package mint.newgui.hud.hudcomponents;

import mint.Mint;
import mint.modules.core.Hud;
import mint.newgui.hud.HudModule;
import mint.utils.ColorUtil;
import mint.utils.RenderUtil;

public class HudWatermarkComponent extends HudModule {
    int x;
    int y;
    int dragX;
    int dragY;
    boolean isDragging;

    public HudWatermarkComponent() {
        super("Watermark");
        x = 0;
        y = 0;
    }

    public void dragScreen(int mouseX, int mouseY) {
        if (!isDragging)
            return;
        x = dragX + mouseX;
        y = dragY + mouseY;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        dragScreen(mouseX, mouseY);
        assert Mint.textManager != null;
        if (getValue()) {
            if (isInsideDragField(mouseX, mouseY)) {
                RenderUtil.drawRect(x, y, x + Mint.textManager.getStringWidth("Mint 0.1.1"), y + Mint.textManager.getFontHeight(), ColorUtil.toRGBA(0, 0, 0, 100));
                assert Mint.textManager != null;
                RenderUtil.drawRect(x + Mint.textManager.getStringWidth("Mint 0.1.1") + 3, y - 7, x + Mint.textManager.getStringWidth("Mint 0.1.1") + 3 + Mint.textManager.getStringWidth("X: " + x + " Y: " + y), y - 7 + Mint.textManager.getFontHeight(), ColorUtil.toRGBA(0, 0, 0, 100));
                Mint.textManager.drawStringWithShadow("X: " + x + " Y: " + y, x + Mint.textManager.getStringWidth("Mint 0.1.1") + 3, y - 7, -1);
            }
            drawText();
        }
    }

    public void drawText() {
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow("Mint 0.1.1", x, y, Hud.getInstance().color.getColor().getRGB());
    }

    public boolean isInsideDragField(int mouseX, int mouseY) {
        assert Mint.textManager != null;
        return (mouseX > x && mouseX < x + Mint.textManager.getStringWidth("Mint 0.1.1")) && (mouseY > y && mouseY < y + Mint.textManager.getFontHeight());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isInsideDragField(mouseX, mouseY)) {
            dragX = x - mouseX;
            dragY = y - mouseY;
            isDragging = true;
        }
        if (mouseButton == 0 && isInside(mouseX, mouseY))
            value = !value;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0)
            isDragging = false;
    }
}
