package mint.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventProcessor extends Event {
    private int stage;

    public EventProcessor() {
    }

    public EventProcessor(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }
}

