package mint.mixins;

import mint.modules.visual.NoRender;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {LayerBipedArmor.class})
public abstract class MixinRenderArmor {

    @Shadow
    protected abstract void setModelVisible(ModelBiped var1);

    /**
     * @author zPrestige_ (idk for some reason @Overwrite needs an @Author)
     * @Reason bro idk why it litterally gave a warning: "@Overwrite is missing an @reason tag"
     *
     **/

    @Overwrite
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn) {
        setModelVisible(p_188359_1_);
        if (NoRender.getInstance().isEnabled())
            switch (slotIn) {
                case HEAD:
                    p_188359_1_.bipedHead.showModel = !NoRender.getInstance().armorRemover.getValue();
                    p_188359_1_.bipedHeadwear.showModel = !NoRender.getInstance().armorRemover.getValue();
                    break;
                case CHEST:
                    p_188359_1_.bipedBody.showModel = !NoRender.getInstance().armorRemover.getValue();
                    p_188359_1_.bipedRightArm.showModel = !NoRender.getInstance().armorRemover.getValue();
                    p_188359_1_.bipedLeftArm.showModel = !NoRender.getInstance().armorRemover.getValue();
                    break;
                case LEGS:
                    p_188359_1_.bipedBody.showModel = !NoRender.getInstance().armorRemover.getValue();
                    p_188359_1_.bipedRightLeg.showModel = !NoRender.getInstance().armorRemover.getValue();
                    p_188359_1_.bipedLeftLeg.showModel = !NoRender.getInstance().armorRemover.getValue();
                    break;
                case FEET:
                    p_188359_1_.bipedRightLeg.showModel = !NoRender.getInstance().armorRemover.getValue();
                    p_188359_1_.bipedLeftLeg.showModel = !NoRender.getInstance().armorRemover.getValue();
            }
    }
}

