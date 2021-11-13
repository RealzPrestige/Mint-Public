package mint.mixins;

import mint.events.EntityCollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {EntityPlayer.class})
public abstract class MixinEntityPlayer {

    @Inject(method = {"applyEntityCollision"}, at = {@At("HEAD")}, cancellable = true)
    public void applyEntityCollision(final Entity entity, final CallbackInfo info) {
        MinecraftForge.EVENT_BUS.post(new EntityCollisionEvent.Entity());

        if (new EntityCollisionEvent.Entity().isCanceled())
            info.cancel();

    }
}
