package mint.utils.shader.shaders;


import mint.utils.RenderUtil;
import mint.utils.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class FlowShader extends FramebufferShader
{
    public static FlowShader FLOW_SHADER;
    public float time;
    
    public FlowShader() {
        super("flow.frag");
    }
    
    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }
    
    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), Float.intBitsToFloat(Float.floatToIntBits(12.494699f) ^ 0x7EC7EA49));
        this.time += Float.intBitsToFloat(Float.floatToIntBits(24055.986f) ^ 0x7DFF745F) * RenderUtil.deltaTime;
    }
    
    static {
        FlowShader.FLOW_SHADER = new FlowShader();
    }
}
