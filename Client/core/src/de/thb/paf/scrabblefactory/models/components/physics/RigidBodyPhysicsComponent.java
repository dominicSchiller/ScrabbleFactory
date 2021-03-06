package de.thb.paf.scrabblefactory.models.components.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import de.thb.paf.scrabblefactory.models.IGameObject;
import de.thb.paf.scrabblefactory.models.entities.IEntity;

/**
 * Represents a rigid body assembling all static or dynamic body characteristic like
 * friction, density etc. in relation to real world physics.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */

public class RigidBodyPhysicsComponent extends AbstractPhysicsComponent {

    /**
     * List of body keys which can be used to load a body
     */
    private String[] bodyKeys;

    /**
     * The active body's name
     */
    private String activeBodyName;

    /**
     * The Box2D physics shape cache to create bodies from
     */
    private PhysicsShapeCache physicsShapeCache;

    /**
     * The LibGDX Box2D body instance
     */
    private Body body;

    /**
     * Movement velocity vector
     */
    private Vector2 velocity;

    /**
     * Viewing direction status (true: sprite looks to the left, false: sprite looks to the right)
     */
    private boolean isFlipped;

    /**
     * Constructor
     * @param id The component's unique identifier
     */
    public RigidBodyPhysicsComponent(Integer id) {
        super(id, PhysicsType.RIGID_BODY);
    }

    /**
     * Constructor
     * @param id The component's unique identifier
     * @param body The LibGDX Box2D body instance
     */
    public RigidBodyPhysicsComponent(Integer id, Body body) {
        super(id, PhysicsType.RIGID_BODY);
        this.body = body;
    }

    @Override
    public void update(float deltaTime) {

        // apply activation state
        super.update(deltaTime);
        float rotation = (float) Math.toDegrees(this.body.getAngle());

        IGameObject parent = this.getParent();
        parent.setRotation(rotation);
        parent.setPosition(this.body.getPosition());
    }

    @Override
    public void dispose() {
        super.dispose();
        this.physicsShapeCache.dispose();
        World world = this.body.getWorld();
        if(world.getBodyCount() > 0) {
            this.body.getWorld().destroyBody(this.body);
        }
    }

    /**
     * Get the active body's name.
     * @return The active body's name
     */
    public String getActiveBodyName() {
        return this.activeBodyName;
    }

    /**
     * Get the Box2D physics shape cache containing all body definitions
     * @return The Box2D physics shape cache
     */
    public PhysicsShapeCache getPhysicsShapeCache() {
        return this.physicsShapeCache;
    }

    /**
     * Get the Box2D body instance.
     * @return The Box2D body instance
     */
    public Body getBody() {
        return this.body;
    }

    /**
     * Get the rigid body's velocity.
     * @return The rigid body's velocity vector
     */
    public Vector2 getVelocity() {
        return this.velocity;
    }

    /**
     * Get the viewing direction status.
     * @return The current viewing direction status
     */
    public boolean isFlipped() {
        return isFlipped;
    }

    /**
     * Set the active body's name.
     * @param bodyName The active body's name
     */
    public void setActiveBodyName(String bodyName) {
        this.activeBodyName = bodyName;
    }

    /**
     * The Box2D physics shape cache containing all body definitions
     * @param physicsShapeShapeCache The Box2D physics shape cache
     */
    public void setPhysicsShapeShapeCache(PhysicsShapeCache physicsShapeShapeCache) {
        this.physicsShapeCache = physicsShapeShapeCache;
    }

    /**
     * Set the LibGDX Box2D body instance.
     * @param body The LibGDX Box2D body instance
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Set the Viewing direction status.
     * @param isFlipped The new viewing direction status
     */
    public void setFlipped(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }
}
