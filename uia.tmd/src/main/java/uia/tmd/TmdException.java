package uia.tmd;

public class TmdException extends Exception {

    private static final long serialVersionUID = 1406034261813021234L;

    public TmdException(String message) {
        super(message);
    }

    public TmdException(String message, Throwable th) {
        super(message, th);
    }

    public TmdException(Throwable th) {
        super(th);
    }

}
