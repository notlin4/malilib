package fi.dy.masa.malilib.listener;

import org.jetbrains.annotations.ApiStatus;

/**
 * Post-ReWrite code
 */
@ApiStatus.Experimental
public interface EventListener
{
    /**
     * Called when the event happens/triggers
     */
    void onEvent();

    /**
     * Creates a new chained EventListener, where this listener
     * runs first and the provided other listener runs after.
     * @param other the listener to chain after this listener
     * @return the new chained listener
     */
    default EventListener chain(EventListener other)
    {
        return () -> {
            this.onEvent();
            other.onEvent();
        };
    }
}
