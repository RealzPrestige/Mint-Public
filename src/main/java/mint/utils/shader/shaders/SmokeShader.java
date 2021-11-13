package mint.utils.shader.shaders;

import mint.utils.RenderUtil;
import mint.utils.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class SmokeShader extends FramebufferShader
{
    public static SmokeShader SMOKE_SHADER;
    public float time;
    
    public SmokeShader() {
        super("smoke.frag");
    }
    
    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }
    
    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), this.time);
        this.time += Float.intBitsToFloat(Float.floatToIntBits(949.1068f) ^ 0x7F29DD70) * RenderUtil.deltaTime;
    }
    
    static {
        SmokeShader.SMOKE_SHADER = new SmokeShader();
    }
}
