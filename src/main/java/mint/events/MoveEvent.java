package mint.events;

import net.minecraft.entity.MoverType;

public class MoveEvent extends EventProcessor
{
    public MoverType type;
    public double x;
    public double y;
    public double z;
    public boolean moved;

    public MoveEvent(final MoverType type, final double x, final double y, final double z) {
        this.moved = false;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean isCancelable() {
        return true;
    }
}
