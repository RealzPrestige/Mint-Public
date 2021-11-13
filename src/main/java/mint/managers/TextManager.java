package mint.managers;

import mint.Mint;
import mint.modules.core.FontChanger;
import mint.modules.miscellaneous.SignExploit;
import mint.newgui.font.CustomFont;
import mint.utils.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

import static mint.managers.ModuleManager.doneLoad;

public class TextManager {
    private final Timer idleTimer = new Timer();
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    private CustomFont customFont = new CustomFont(new Font("Verdana", Font.PLAIN, 17), true, false);
    private boolean idling;

    public TextManager() {
        updateResolution();
    }

    public void init() {
        if (doneLoad) {
            SignExploit.nullCheck();
            doneLoad = false;
        }
        assert Mint.moduleManager != null;
        FontChanger cFont = FontChanger.getInstance();
        try {
            setFontRenderer(new Font("Dialog", getStyle(), cFont.fontSize.getValue()), true, true);
        } catch (Exception ignored) {
        }
    }

    public int getStyle() {
        if (FontChanger.getInstance().style.getValueEnum().equals(FontChanger.Style.NORMAL))
            return 0;
        else if (FontChanger.getInstance().style.getValueEnum().equals(FontChanger.Style.ITALIC))
            return 2;
        else if (FontChanger.getInstance().style.getValueEnum().equals(FontChanger.Style.BOLD))
            return 1;
        else if (FontChanger.getInstance().style.getValueEnum().equals(FontChanger.Style.ITALICBOLD))
            return 3;

        return 0;
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        drawString(text, x, y, color, true);
    }

    public void drawString(String text, float x, float y, int color, boolean shadow) {
        assert Mint.moduleManager != null;
        if (FontChanger.getInstance().isEnabled()) {
            if (shadow) {
                customFont.drawStringWithShadow(text, x, y, color);
            } else {
                customFont.drawString(text, x, y, color);
            }
            return;
        }
        Mint.INSTANCE.mc.fontRenderer.drawString(text, x, y, color, shadow);
    }

    public float drawStringFull(String text, float x, float y, int color, boolean shadow) {
        assert Mint.moduleManager != null;
        if (FontChanger.getInstance().isEnabled()) {
            if (shadow) {
                customFont.drawStringWithShadow(text, x, y, color);
            } else {
                customFont.drawString(text, x, y, color);
            }
            return x;
        }
        Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, shadow);
        return x;
    }

    public int getStringWidth(String text) {
        assert Mint.moduleManager != null;
        if (FontChanger.getInstance().isEnabled()) {
            return customFont.getStringWidth(text);
        }
        return Mint.INSTANCE.mc.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        assert Mint.moduleManager != null;
        if (FontChanger.getInstance().isEnabled()) {
            return customFont.getStringHeight();
        }
        return Mint.INSTANCE.mc.fontRenderer.FONT_HEIGHT;
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public void updateResolution() {
        scaledWidth = Mint.INSTANCE.mc.displayWidth;
        scaledHeight = Mint.INSTANCE.mc.displayHeight;
        scaleFactor = 1;
        boolean flag = Mint.INSTANCE.mc.isUnicode();
        int i = Mint.INSTANCE.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (scaleFactor < i && scaledWidth / (scaleFactor + 1) >= 320 && scaledHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        if (flag && scaleFactor % 2 != 0 && scaleFactor != 1) {
            --scaleFactor;
        }
        double scaledWidthD = scaledWidth / scaleFactor;
        double scaledHeightD = scaledHeight / scaleFactor;
        scaledWidth = MathHelper.ceil(scaledWidthD);
        scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public String getIdleSign() {
        if (idleTimer.passedMs(500L)) {
            idling = !idling;
            idleTimer.reset();
        }
        if (idling) {
            return "_";
        }
        return "";
    }
}

