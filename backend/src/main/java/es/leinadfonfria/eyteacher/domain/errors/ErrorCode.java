package es.leinadfonfria.eyteacher.domain.errors;

/**
 * Contains application-level error codes used to identify specific error conditions.
 * Each constant represents a unique numeric code associated with a particular error type.
 */
public class ErrorCode {
    /** An unexpected or unclassified error occurred. */
    public static final int UNKNOWN_ERROR = 1000;
    /** The requested user could not be found. */
    public static final int USER_NOT_FOUND = 1001;
    /** The provided user ID has an invalid format. */
    public static final int INVALID_USER_ID_FORMAT = 1002;
    /** The provided user data is invalid or incomplete. */
    public static final int INVALID_USER_DATA = 1003;
    /** The provided email address is already registered. */
    public static final int EMAIL_ALREADY_IN_USE = 1004;
    /** The provided password does not meet the required criteria. */
    public static final int INVALID_PASSWORD = 1005;
    /** The provided passwords do not match. */
    public static final int DIFFERENT_PASSWORD = 1006;
    /** The provided login credentials are incorrect. */
    public static final int INVALID_CREDENTIALS = 1007;
}