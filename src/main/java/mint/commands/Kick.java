package mint.commands;

import mint.Mint;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;

public class Kick extends Command {

    public Kick() {
        super("kick");
    }

    @Override
    public void execute(String[] commands) {
        Mint.INSTANCE.mc.getConnection().handleDisconnect(new SPacketDisconnect(new TextComponentString("admin abiss")));
    }
}