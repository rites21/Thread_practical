package example.LLD_Uber_Cab_Hailing_Service;

/**
 * Domain failure instead of raw RuntimeException.
 * Interviewers can tell "no driver" from "bad state" without parsing strings.
 */
public class CabHailingException extends RuntimeException {

    public CabHailingException(String message) {
        super(message);
    }
}
