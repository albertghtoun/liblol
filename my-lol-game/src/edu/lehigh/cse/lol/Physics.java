/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org>
 */

package edu.lehigh.cse.lol;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

import edu.lehigh.cse.lol.Level.Action;

/**
 * Game designers can configure the physics of a level (i.e., the default
 * forces, if any), via this class. Internally, the class constructs a box2d
 * physics world and instantiates the callbacks needed to ensure that LOL works
 * correctly.
 */
public class Physics {
    /**
     * This ratio means that every 10 pixels on the screen will correspond to a
     * meter. Note that 'pixels' are defined in terms of what a programmer's
     * Config says, not the actual screen size, because the programmer's Config
     * gets scaled to screen dimensions.
     */
    static final float PIXEL_METER_RATIO = 10;

    /**
     * When a hero collides with a "sticky" obstacle, this is the code we run to
     * figure out what to do
     * 
     * @param sticky The sticky entity... it should always be an obstacle for
     *            now
     * @param other The other entity... it should always be a hero for now
     * @param contact A description of the contact event
     */
    static void handleSticky(final PhysicsSprite sticky, final PhysicsSprite other, Contact contact) {
        // don't create a joint if we've already got one
        if (other.mDJoint != null)
            return;
        // don't create a joint if we're supposed to wait
        if (System.nanoTime() < other.mStickyDelay)
            return;
        // handle sticky obstacles... only do something if we're hitting the
        // obstacle from the right direction
        if ((sticky.mIsSticky[0] && other.getYPosition() >= sticky.getYPosition() + sticky.mSize.y)
                || (sticky.mIsSticky[1] && other.getXPosition() + other.mSize.x <= sticky
                .getXPosition())
                || (sticky.mIsSticky[3] && other.getXPosition() >= sticky.getXPosition()
                + sticky.mSize.x)
                || (sticky.mIsSticky[2] && other.getYPosition() + other.mSize.y <= sticky
                .getYPosition())) {
            // create distance and weld joints... somehow, the combination is
            // needed to get this to work. Note that this function runs during
            // the box2d step, so we need to make the joint in a callback that
            // runs later
            final Vector2 v = contact.getWorldManifold().getPoints()[0];
            Level.sCurrent.mOneTimeEvents.add(new Action() {
                @Override
                public void go() {
                    other.mBody.setLinearVelocity(0, 0);
                    DistanceJointDef d = new DistanceJointDef();
                    d.initialize(sticky.mBody, other.mBody, v, v);
                    d.collideConnected = true;
                    other.mDJoint = (DistanceJoint)Level.sCurrent.mWorld.createJoint(d);
                    WeldJointDef w = new WeldJointDef();
                    w.initialize(sticky.mBody, other.mBody, v);
                    w.collideConnected = true;
                    other.mWJoint = (WeldJoint)Level.sCurrent.mWorld.createJoint(w);
                }
            });
        }
    }

    /**
     * Configure physics for the current level
     * 
     * @param defaultXGravity The default force moving entities to the left
     *            (negative) or right (positive)... Usually zero
     * @param defaultYGravity The default force pushing the hero down (negative)
     *            or up (positive)... Usually zero or -10
     */
    public static void configure(float defaultXGravity, float defaultYGravity) {
        // create a world with gravity
        Level.sCurrent.mWorld = new World(new Vector2(defaultXGravity, defaultYGravity), true);

        // set up the collision handlers
        Level.sCurrent.mWorld.setContactListener(new ContactListener() {
            /**
             * When two bodies start to collide, we can use this to forward to
             * our onCollide methods
             */
            @Override
            public void beginContact(final Contact contact) {
                // Get the bodies, make sure both are PhysicsSprites
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();
                if (!(a instanceof PhysicsSprite) || !(b instanceof PhysicsSprite))
                    return;

                // the order is Hero, Enemy, Goodie, Projectile, Obstacle, SVG,
                // Destination
                //
                // Of those, Hero, Enemy, and Projectile are the only ones with
                // a non-empty onCollide
                final PhysicsSprite c0;
                final PhysicsSprite c1;
                if (a instanceof Hero) {
                    c0 = (PhysicsSprite)a;
                    c1 = (PhysicsSprite)b;
                } else if (b instanceof Hero) {
                    c0 = (PhysicsSprite)b;
                    c1 = (PhysicsSprite)a;
                } else if (a instanceof Enemy) {
                    c0 = (PhysicsSprite)a;
                    c1 = (PhysicsSprite)b;
                } else if (b instanceof Enemy) {
                    c0 = (PhysicsSprite)b;
                    c1 = (PhysicsSprite)a;
                } else if (a instanceof Projectile) {
                    c0 = (PhysicsSprite)a;
                    c1 = (PhysicsSprite)b;
                } else if (b instanceof Projectile) {
                    c0 = (PhysicsSprite)b;
                    c1 = (PhysicsSprite)a;
                } else {
                    return;
                }

                // Schedule an event to run as soon as the physics world
                // finishes its step.
                //
                // NB: this is called from render, while world is updating...
                // you can't modify the world or its entities until the update
                // finishes, so we have to schedule collision-based updates to
                // run after the world update.
                Level.sCurrent.mOneTimeEvents.add(new Action() {
                    @Override
                    public void go() {
                        c0.onCollide(c1, contact);
                    }
                });
            }

            /**
             * We ignore endcontact
             */
            @Override
            public void endContact(Contact contact) {
            }

            /**
             * Presolve is a hook for disabling certain collisions. We use it
             * for collision immunity, sticky obstacles, and one-way walls
             */
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                // get the bodies, make sure both are PhysicsSprites
                Object a = contact.getFixtureA().getBody().getUserData();
                Object b = contact.getFixtureB().getBody().getUserData();
                if (!(a instanceof PhysicsSprite) || !(b instanceof PhysicsSprite))
                    return;
                PhysicsSprite gfoA = (PhysicsSprite)a;
                PhysicsSprite gfoB = (PhysicsSprite)b;

                // handle sticky obstacles... only do something if at least one
                // entity is a sticky entity
                if (gfoA.mIsSticky[0] || gfoA.mIsSticky[1] || gfoA.mIsSticky[2]
                        || gfoA.mIsSticky[3]) {
                    handleSticky(gfoA, gfoB, contact);
                    return;
                } else if (gfoB.mIsSticky[0] || gfoB.mIsSticky[1] || gfoB.mIsSticky[2]
                        || gfoB.mIsSticky[3]) {
                    handleSticky(gfoB, gfoA, contact);
                    return;
                }

                // if the PhysicsSprites have the same passthrough ID, and it's
                // not zero, then disable the contact
                if (gfoA.mPassThroughId != 0 && gfoA.mPassThroughId == gfoB.mPassThroughId) {
                    contact.setEnabled(false);
                    return;
                }

                // is either one-sided? If not, we're done
                PhysicsSprite onesided = null;
                PhysicsSprite other = null;
                if (gfoA.mIsOneSided > -1) {
                    onesided = gfoA;
                    other = gfoB;
                } else if (gfoB.mIsOneSided > -1) {
                    onesided = gfoB;
                    other = gfoA;
                } else {
                    return;
                }

                // if we're here, see if we should be disabling a one-sided
                // obstacle collision
                WorldManifold worldManiFold = contact.getWorldManifold();
                int numPoints = worldManiFold.getNumberOfContactPoints();
                for (int i = 0; i < numPoints; i++) {
                    Vector2 vector2 = other.mBody.getLinearVelocityFromWorldPoint(worldManiFold
                            .getPoints()[i]);
                    // disable based on the value of isOneSided and the vector
                    // between the entities
                    if (onesided.mIsOneSided == 0 && vector2.y < 0)
                        contact.setEnabled(false);
                    else if (onesided.mIsOneSided == 2 && vector2.y > 0)
                        contact.setEnabled(false);
                    else if (onesided.mIsOneSided == 1 && vector2.x > 0)
                        contact.setEnabled(false);
                    else if (onesided.mIsOneSided == 3 && vector2.x < 0)
                        contact.setEnabled(false);
                }
            }

            /**
             * We don't do anything fancy on postsolve
             */
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }
}
