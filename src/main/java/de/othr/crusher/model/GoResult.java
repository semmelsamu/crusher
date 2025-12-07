package de.othr.crusher.model;

/**
 * Enum representing the result of a boulder climbing attempt.
 * <p>
 * Tracks whether a climber finished the boulder, came close, or did not finish.
 * </p>
 */
public enum GoResult {
    DID_NOT_FINISH("Did not finish"),
    CLOSE_TRY("Close try"),
    FINISHED("Finished");

    private final String displayName;

    GoResult(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the human-readable display name for the result.
     *
     * @return display name of the result
     */
    public String getDisplayName() {
        return displayName;
    }
}
