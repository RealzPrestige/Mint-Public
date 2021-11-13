package mint.mixins;

import mint.modules.visual.NoRender;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {EntityRenderer.class})
public abstract class MixinEntityRenderer {
    @Inject(method = {"hurtCameraEffect"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (!(NoRender.getInstance().isEnabled() || NoRender.getInstance().hurtCam.getValue()))
            return;

        info.cancel();
    }

    @Invoker("setupCameraTransform")
    abstract void setupCameraTransformInvoker(float partialTicks, int pass);
}
