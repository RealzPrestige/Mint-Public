package mint.mixins;

import mint.Mint;
import mint.modules.core.FontChanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={FontRenderer.class})
public abstract class MixinFontRenderer {
    private final Minecraft mc = Minecraft.getMinecraft();
    @Shadow
    protected abstract int renderString(String var1, float var2, float var3, int var4, boolean var5);

    @Inject(method = {"drawString(Ljava/lang/String;FFIZ)I"}, at = {@At("HEAD")}, cancellable = true)
    public void renderStringHook(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> info) {
        if (this.mc.world != null && FontChanger.getInstance().isEnabled() && FontChanger.getInstance().override.getValue() && Mint.textManager != null) {
            float result = Mint.textManager.drawStringFull(text, x, y, color, dropShadow);
            info.setReturnValue((int) result);
        }
    }
}

