package pl.n3fr0.n3talk.mirror;

public enum MirrorCommands {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    INIT("init");

    private MirrorCommands(String command) {
        this.command = command;
    }

    public String command() {
        return command;
    }

    private String command;


}
