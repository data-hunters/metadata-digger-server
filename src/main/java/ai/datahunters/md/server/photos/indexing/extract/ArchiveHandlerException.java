package ai.datahunters.md.server.photos.indexing.extract;

public class ArchiveHandlerException extends Exception {
    private static final long serialVersionUID = -325582666145040783L;

    public ArchiveHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchiveHandlerException(String message) {
        super(message);
    }
}
