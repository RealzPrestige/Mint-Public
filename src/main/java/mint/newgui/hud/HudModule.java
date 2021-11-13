package mint.newgui.hud;

import mint.Mint;
import mint.modules.core.NewGuiModule;
import mint.utils.RenderUtil;

public class HudModule {
    int x;
    int y;
    int w;
    int h;
    String name;
    public boolean value;

    public HudModule(String name) {
        this.name = name;
        this.value = false;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(x, y, x + w, y + h,value ? NewGuiModule.getInstance().color.getColor().getRGB() : NewGuiModule.getInstance().backgroundColor.getColor().getRGB());
        assert Mint.textManager != null;
        Mint.textManager.drawStringWithShadow(name, x, y, -1);
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
    }

    public void drawText(){
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0 && isInside(mouseX, mouseY))
            value = !value;
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton){
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.w = width;
    }

    public void setHeight(int height) {
        this.h = height;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + w) && (mouseY > y && mouseY < y + h);
    }
    public String getName(){
        return name;
    }

    public boolean getValue(){
        return value;
    }

    public String getPos(){
        return  x + ", " + y;
    }
}
