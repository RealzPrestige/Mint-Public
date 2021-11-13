package mint.mixins;

import mint.events.RenderItemEvent;
import mint.modules.visual.Hand;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

/**
 * @author zPrestige_
 * 13/10/2021
 * @author FB for itemOpacityChanger (inspired by Phobos.EU / Abyss)
 */

@Mixin(value = {RenderItem.class})
public abstract class MixinRenderItem {

    @Shadow
    private void renderModel(IBakedModel model, int color, ItemStack stack) {
    }

    @Inject(method = {"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"}, at = {@At("INVOKE")})
    public void renderItem(final ItemStack stack, final EntityLivingBase entityLivingBaseIn, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            if (transform.equals(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND))
                MinecraftForge.EVENT_BUS.post(new RenderItemEvent.Offhand(stack));
            else
                MinecraftForge.EVENT_BUS.post(new RenderItemEvent.MainHand(stack));
        }
    }

    @Redirect(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V"))
    private void renderModelColor(RenderItem renderItem, IBakedModel model, ItemStack stack) {
        if (Hand.getInstance().isEnabled())
            renderModel(model, new Color(1f, 1f, 1f, Hand.getInstance().alpha.getValue() / 255.0f).getRGB(), stack);
        else renderModel(model, new Color(1f, 1f, 1f, 1f).getRGB(), stack);
    }
}
