package nz.ac.canterbury.seng302.portfolio.utils;

/**
 * An exception class used for error handling when incorrect details have been passed.
 */
public class IncorrectDetailsException extends Exception {
    public IncorrectDetailsException(String errorMessage) {
        super(errorMessage);
    }
}
