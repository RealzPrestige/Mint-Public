package mint.utils;

import mint.Mint;

public class NullUtil {

    public static boolean fullNullCheck() {
        return Mint.INSTANCE.mc.player == null || Mint.INSTANCE.mc.world == null;
    }
}
