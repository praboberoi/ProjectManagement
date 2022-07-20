package nz.ac.canterbury.seng302.portfolio.utils;

/**
 * The possible fields that the users can be ordered by with their associated value.
 */
public enum UserField{
    USERID("userId"),
    FIRSTNAME("firstName"),
    USERNAME("username"),
    NICKNAME("nickname"),
    ROLES("roles");

    public final String value;

    /**
     * Assigns each enum a value on creation
     * @param value The value to be assigned
     */
    private UserField(String value) {
        this.value = value;
    }
}
