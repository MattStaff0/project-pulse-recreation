package team.projectpulse.system.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String objectName, Object id) {
        super("Could not find " + objectName + " with id " + id);
    }
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
