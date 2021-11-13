package mint.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class EntityCollisionEvent extends EventProcessor {

    @Cancelable
    public static class Block extends EntityCollisionEvent {
    }

    @Cancelable
    public static class Entity extends EntityCollisionEvent {
    }
}
