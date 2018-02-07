package de.thb.paf.scrabblefactory.factories;

import de.thb.paf.scrabblefactory.managers.GameEventManager;
import de.thb.paf.scrabblefactory.models.events.GameEventType;
import de.thb.paf.scrabblefactory.models.events.GroundContactEvent;
import de.thb.paf.scrabblefactory.models.events.IGameEvent;
import de.thb.paf.scrabblefactory.models.events.MoveEvent;

/**
 * Factory class dedicated to create and assemble new game events.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */
public class EventFactory {

    /**
     * Get the game event instance defined by it's game event type.
     * @param eventType The event type which defines the game event instance
     * @return The assembled game event instance
     */
    public IGameEvent getGameEvent(GameEventType eventType) {
        IGameEvent event = GameEventManager.getInstance().getGameEvent(eventType);
        if(event != null) {
            return event;
        }

        switch(eventType) {
            case MOVE:
                event = new MoveEvent();
                break;
            case GROUND_CONTACT:
                event = new GroundContactEvent();
                break;
            default:
                event = null;
                break;
        }

        // add event to the global game event manager
        if(event != null) {
            GameEventManager.getInstance().addGameEvent(event);
        }

        return event;
    }
}
