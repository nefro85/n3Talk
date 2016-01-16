package pl.n3fr0.n3talk.mirror;

public class MirrorException extends RuntimeException {

    public MirrorException(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        String message = null;
        switch (code) {
            case 1:
                message = "Object is lockt!";
                break;
            default:
                message = "UNKNOWN!";
                break;
        }
        return message;
    }

    private int code;

    public static final int OBJECT_LOCKT = 1;

}
