package de.thb.paf.scrabblefactory.managers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import de.thb.paf.scrabblefactory.gameplay.GameContactListener;

/**
 * Manager class responsible for holding and managing the global physical Box2D world's instance.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */

public class WorldPhysicsManager implements IGameManager {

    /**
     * The default gravity which will be applied to new Box2D world instances
     */
    private static final Vector2 DEFAULT_GRAVITY = new Vector2(0, 0);

    /**
     * The singleton instance of the WorldPhysicsManager
     */
    private static WorldPhysicsManager instance;

    /**
     * The Box2D physical world instance
     */
    private World physicalWorld;

    /**
     * The physical world's current contact listener
     */
    private GameContactListener contactListener;

    /**
     * static initializer: called when the class is loaded by the JVM
     */
    static {
        instance = new WorldPhysicsManager();
    }

    /**
     * Private singleton constructor.
     */
    private WorldPhysicsManager() {
        this.physicalWorld = new World(DEFAULT_GRAVITY, true);
    }

    /**
     * Get the global WorldPhysicsManager instance.
     * @return The global game object instance
     */
    public static WorldPhysicsManager getInstance() {
        return instance;
    }

    /**
     * Get the physical world instance.
     * @return The physical world instance
     */
    public World getPhysicalWorld() {
        return this.physicalWorld;
    }

    /**
     * Set the physical world's gravity.
     * @param gravity The physical world's gravity vector
     */
    public void setWorldGravity(Vector2 gravity) {
        this.physicalWorld.setGravity(gravity);
    }

    /**
     * Get the physical world's current contact listener.
     * @return The physical world's current game contact listener
     */
    public GameContactListener getContactListener() {
        return this.contactListener;
    }

    /**
     * Set the physical world's current contact listener
     * @param contactListener The physical world's current contact listener.
     */
    public void setContactListener(GameContactListener contactListener) {
        this.contactListener = contactListener;
    }

    @Override
    public void dispose() {
        this.physicalWorld = new World(DEFAULT_GRAVITY, true);
    }
}
