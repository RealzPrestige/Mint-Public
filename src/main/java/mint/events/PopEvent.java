package mint.events;

public class PopEvent extends EventProcessor {

    private final int entity;

    public PopEvent(int entityId) {
        entity = entityId;
    }

    public int getEntityId() {
        return entity;
    }

}
