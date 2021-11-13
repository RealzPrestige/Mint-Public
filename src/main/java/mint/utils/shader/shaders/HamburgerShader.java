package mint.utils.shader.shaders;


import mint.utils.RenderUtil;
import mint.utils.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class HamburgerShader extends FramebufferShader {
    public static HamburgerShader HAMBURGER_SHADER;
    public float time;

    public HamburgerShader() {
        super("hamburger.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), time);
        time += Float.intBitsToFloat(Float.floatToIntBits(1015.0615f) ^ 0x7F395856) * RenderUtil.deltaTime;
    }

    static {
        HamburgerShader.HAMBURGER_SHADER = new HamburgerShader();
    }
}
