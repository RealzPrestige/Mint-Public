package mint.events;

public class ChorusEvent extends EventProcessor {

    private final double eventPosX;
    private final double eventPosY;
    private final double eventPosZ;

    public ChorusEvent(double x, double y, double z) {
        this.eventPosX = x;
        this.eventPosY = y;
        this.eventPosZ = z;
    }

    public double getEventPosX(){
        return this.eventPosX;
    }

    public double getEventY(){
        return this.eventPosY;
    }

    public double getEventZ(){
        return this.eventPosZ;
    }
}