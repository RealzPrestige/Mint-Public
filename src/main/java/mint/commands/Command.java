package mint.commands;

import mint.Mint;

public abstract class Command {
    protected String name;
    protected String[] commands;

    public Command(String name) {
        this.name = name;
        this.commands = new String[]{""};
    }

    public Command(String name, String[] commands) {
        this.name = name;
        this.commands = commands;
    }

    public static String getCommandPrefix() {
        return Mint.commandManager.getPrefix();
    }

    public abstract void execute(String[] var1);

    public String getName() {
        return this.name;
    }

    public String[] getCommands() {
        return this.commands;
    }

}

