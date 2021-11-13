package mint.mixins;

import mint.events.EntityCollisionEvent;
import mint.events.MoveEvent;
import mint.modules.movement.Strafe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.MoverType;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {EntityPlayerSP.class})
public class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(final Minecraft p_i47378_1_, final World p_i47378_2_, final NetHandlerPlayClient p_i47378_3_, final StatisticsManager p_i47378_4_, final RecipeBook p_i47378_5_) {
        super(p_i47378_2_, p_i47378_3_.getGameProfile());
    }

    @Redirect(method = {"onUpdateWalkingPlayer"}, at = @At(value = "FIELD", target = "net/minecraft/util/math/AxisAlignedBB.minY:D"))
    private double minYHook(AxisAlignedBB bb) {
        if (Strafe.getInstance().isEnabled() && Strafe.getInstance().changeY && Strafe.getInstance().strafeMode.getValueEnum().equals(Strafe.StrafeMode.Instant)) {
            Strafe.getInstance().changeY = false;
            return Strafe.getInstance().minY;
        }
        return bb.minY;
    }

    @Inject(method = {"pushOutOfBlocks"}, at = {@At("HEAD")}, cancellable = true)
    private void pushOutOfBlock(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        MinecraftForge.EVENT_BUS.post(new EntityCollisionEvent.Block());
        info.setReturnValue(!new EntityCollisionEvent.Block().isCanceled());
    }

    @Inject(method = {"move"}, at = {@At("HEAD")}, cancellable = true)
    public void move(final MoverType type, final double x, final double y, final double z, final CallbackInfo ci) {
        final MoveEvent event = new MoveEvent(type, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
            return;
        }
        if (event.x != x || event.y != y || event.z != z) {
            super.move(type, event.x, event.y, event.z);
            ci.cancel();
        }
    }
}
