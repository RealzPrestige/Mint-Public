package mint.events;

import net.minecraft.item.ItemStack;

public class RenderItemEvent extends EventProcessor {
    ItemStack stack;

    public RenderItemEvent(ItemStack stack){
        this.stack = stack;
    }

    public static class MainHand extends RenderItemEvent {
        public MainHand(ItemStack stack) {
            super(stack);
        }
        public ItemStack getItemStack(){
            return stack;
        }
    }

    public static class Offhand extends RenderItemEvent {
        public Offhand(ItemStack stack) {
            super(stack);
        }
        public ItemStack getItemStack(){
            return stack;
        }
    }

}
