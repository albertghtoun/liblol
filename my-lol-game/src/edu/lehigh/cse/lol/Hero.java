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

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;

public class Hero extends PhysicsSprite {

    /**
     * Strength of the hero This determines how many collisions with enemies the
     * hero can sustain before it is defeated. The default is 1, and the default
     * enemy damage amount is 2, so that the default behavior is for the hero to
     * be defeated on any collision with an enemy, with the enemy *not*
     * disappearing
     */
    private int mStrength = 1;

    /**
     * Time until the hero's invincibility runs out
     */
    private float mInvincibleRemaining;

    /**
     * Animation support: cells involved in animation for invincibility
     */
    private Animation mInvincibleAnimation;

    /**
     * Is the hero currently in crawl mode?
     */
    private boolean mCrawling;

    /**
     * Animation support: cells involved in animation for crawling
     */
    private Animation mCrawlAnimation;

    /**
     * Animation support: cells involved in animation for throwing
     */
    private Animation mThrowAnimation;

    /**
     * Animation support: seconds that constitute a throw action
     */
    private float mThrowAnimateTotalLength;

    /**
     * Animation support: how long until we stop showing the throw animation
     */
    private float mThrowAnimationTimeRemaining;

    /**
     * Track if the hero is in the air, so that it can't jump when it isn't
     * touching anything. This does not quite work as desired, but is good
     * enough for LOL
     */
    private boolean mInAir;

    /**
     * When the hero jumps, this specifies the amount of velocity to add to
     * simulate a jump
     */
    private Vector2 mJumpImpulses;

    /**
     * Indicate that the hero can jump while in the air
     */
    private boolean mAllowMultiJump;

    /**
     * Does the hero jump when we touch it?
     */
    private boolean mIsTouchJump;

    /**
     * Animation support: cells involved in animation for jumping
     */
    private Animation mJumpAnimation;

    /**
     * Sound to play when a jump occurs
     */
    private Sound mJumpSound;

    /**
     * Does the hero only start moving when we touch it?
     */
    private Vector2 mTouchAndGo;

    /**
     * For tracking the current amount of rotation of the hero
     */
    private float mCurrentRotation;

    /**
     * Construct a Hero by creating a PhysicsSprite and incrementing the number
     * of heroes created. This code should never be called directly by the game
     * designer.
     * 
     * @param width The width of the hero
     * @param height The height of the hero
     * @param imgName The name of the file that has the default image for this
     *            hero
     */
    private Hero(float width, float height, String imgName) {
        super(imgName, width, height);
        Level.sCurrent.mScore.mHeroesCreated++;
    }

    /**
     * We can't just use the basic PhysicsSprite renderer, because we might need
     * to adjust a one-off animation (invincibility or throw) first
     * 
     * @param sb The SpriteBatch to use for drawing this hero
     * @param delta The time since the last render
     */
    @Override
    public void render(SpriteBatch sb, float delta) {
        // determine when to turn off throw animations
        if (mThrowAnimationTimeRemaining > 0) {
            mThrowAnimationTimeRemaining -= delta;
            if (mThrowAnimationTimeRemaining <= 0) {
                mThrowAnimationTimeRemaining = 0;
                mAnimator.setCurrentAnimation(mDefaultAnimation);
            }
        }

        // determine when to turn off invincibility. When we turn it off, if we
        // had an invincibility animation, turn it off too
        if (mInvincibleRemaining > 0) {
            mInvincibleRemaining -= delta;
            if (mInvincibleRemaining <= 0) {
                mInvincibleRemaining = 0;
                if (mInvincibleAnimation != null)
                    mAnimator.setCurrentAnimation(mDefaultAnimation);
            }
        }

        super.render(sb, delta);
    }

    /**
     * Make the hero jump, unless it is in the air and not multijump
     */
    void jump() {
        if (mInAir)
            return;
        Vector2 v = mBody.getLinearVelocity();
        v.add(mJumpImpulses);
        updateVelocity(v.x, v.y);
        if (!mAllowMultiJump)
            mInAir = true;
        if (mJumpAnimation != null)
            mAnimator.setCurrentAnimation(mJumpAnimation);
        if (mJumpSound != null)
            mJumpSound.play();
        // break any sticky joints, so the hero can actually move
        mStickyDelay = System.nanoTime() + 10000000;
    }

    /**
     * Stop the jump animation for a hero, and make it eligible to jump again
     */
    private void stopJump() {
        if (mInAir || mAllowMultiJump) {
            mInAir = false;
            mAnimator.setCurrentAnimation(mDefaultAnimation);
        }
    }

    /**
     * When the hero is touched, we might need to take action. If so, we use
     * this to determine what to do.
     */
    @Override
    void handleTouchDown(float x, float y) {
        // if the hero is touch-to-jump, then try to jump
        if (mIsTouchJump) {
            jump();
            return;
        }
        // if the hero is touch and go, make the hero start moving
        if (mTouchAndGo != null) {
            mHover = null;
            // if it was hovering, its body type won't be Dynamic
            if (mBody.getType() != BodyType.DynamicBody)
                mBody.setType(BodyType.DynamicBody);
            setAbsoluteVelocity(mTouchAndGo.x, mTouchAndGo.y, false);
            // turn off isTouchAndGo, so we can't double-touch
            mTouchAndGo = null;
            return;
        }
        super.handleTouchDown(x, y);
    }

    /**
     * Internal method to make the hero's throw animation play while it is
     * throwing a projectile
     */
    void doThrowAnimation() {
        if (mThrowAnimation != null) {
            mAnimator.setCurrentAnimation(mThrowAnimation);
            mThrowAnimationTimeRemaining = mThrowAnimateTotalLength;
        }
    }

    /**
     * Put the hero in crawl mode. Note that we make the hero rotate when it is
     * crawling
     */
    void crawlOn() {
        mCrawling = true;
        mBody.setTransform(mBody.getPosition(), -3.14159f / 2);
        if (mCrawlAnimation != null)
            mAnimator.setCurrentAnimation(mCrawlAnimation);
    }

    /**
     * Take the hero out of crawl mode
     */
    void crawlOff() {
        mCrawling = false;
        mBody.setTransform(mBody.getPosition(), 0);
        mAnimator.setCurrentAnimation(mDefaultAnimation);
    }

    /**
     * Change the rotation of the hero
     * 
     * @param delta How much to add to the current rotation
     */
    void increaseRotation(float delta) {
        if (mInAir) {
            mCurrentRotation += delta;
            mBody.setAngularVelocity(0);
            mBody.setTransform(mBody.getPosition(), mCurrentRotation);
        }
    }

    /**
     * The Hero is the dominant participant in all collisions. Whenever the hero
     * collides with something, we need to figure out what to do
     * 
     * @param other Other object involved in this collision
     * @param contact A description of the contact that caused this collision
     */
    @Override
    void onCollide(PhysicsSprite other, Contact contact) {
        // NB: we currently ignore SpriteId.PROJECTILE
        if (other instanceof Enemy)
            onCollideWithEnemy((Enemy)other);
        else if (other instanceof Destination)
            onCollideWithDestination((Destination)other);
        else if (other instanceof Obstacle)
            onCollideWithObstacle((Obstacle)other, contact);
        else if (other instanceof Svg.SVGSprite)
            onCollideWithSVG(other);
        else if (other instanceof Goodie)
            onCollideWithGoodie((Goodie)other);
    }

    /**
     * Dispatch method for handling Hero collisions with Destinations
     * 
     * @param d The destination with which this hero collided
     */
    private void onCollideWithDestination(Destination d) {
        // only do something if the hero has enough goodies of each type and
        // there's room in the destination
        boolean match = true;
        for (int i = 0; i < 4; ++i)
            match &= Level.sCurrent.mScore.mGoodiesCollected[i] >= d.mActivation[i];
            if (match && (d.mHolding < d.mCapacity) && mVisible) {
                // hide the hero quietly, since the destination might make a sound
                remove(true);
                d.mHolding++;
                if (d.mArrivalSound != null)
                    d.mArrivalSound.play();
                Level.sCurrent.mScore.onDestinationArrive();
            }
    }

    /**
     * Dispatch method for handling Hero collisions with Enemies
     * 
     * @param e The enemy with which this hero collided
     */
    private void onCollideWithEnemy(Enemy e) {
        // if the enemy always defeats the hero, no matter what, then defeat the
        // hero
        if (e.mAlwaysDoesDamage) {
            remove(false);
            Level.sCurrent.mScore.defeatHero(e);
            return;
        }
        // handle hero invincibility
        if (mInvincibleRemaining > 0) {
            // if the enemy is immune to invincibility, do nothing
            if (e.mImmuneToInvincibility)
                return;
            e.defeat(true);
        }
        // defeat by crawling?
        else if (mCrawling && e.mDefeatByCrawl) {
            e.defeat(true);
        }
        // defeat by jumping only if the hero's bottom is above the enemy's mid-section
        else if (mInAir && e.mDefeatByJump && getYPosition() > e.getYPosition() + e.mSize.y/2) {
            e.defeat(true);
        }
        // when we can't defeat it by losing strength, remove the hero
        else if (e.mDamage >= mStrength) {
            remove(false);
            Level.sCurrent.mScore.defeatHero(e);
        }
        // when we can defeat it by losing strength
        else {
            addStrength(-e.mDamage);
            e.defeat(true);
        }
    }

    /**
     * Update the hero's strength, and then run any strength change callback
     * that has been registered
     * 
     * @param amount The amount to add (use a negative value to subtract)
     */
    private void addStrength(int amount) {
        mStrength += amount;
        Lol.sGame.onStrengthChangeTrigger(Lol.sGame.mCurrLevelNum, this);
    }

    /**
     * Dispatch method for handling Hero collisions with Obstacles
     * 
     * @param o The obstacle with which this hero collided
     */
    private void onCollideWithObstacle(Obstacle o, Contact contact) {
        // do we need to play a sound?
        o.playCollideSound();

        // reset rotation of hero if this obstacle is not a sensor
        if ((mCurrentRotation != 0) && !o.mBody.getFixtureList().get(0).isSensor())
            increaseRotation(-mCurrentRotation);

        // if there is code attached to the obstacle for modifying the hero's
        // behavior, run it
        if (o.mHeroCollision != null)
            o.mHeroCollision.go(this, contact);

        // If this is a wall, then mark us not in the air so we can do more
        // jumps. Note that sensors should not enable
        // jumps for the hero.
        if ((mInAir || mAllowMultiJump) && !o.mBody.getFixtureList().get(0).isSensor()
                && !o.mNoJumpReenable)
            stopJump();
    }

    /**
     * Dispatch method for handling Hero collisions with SVG lines
     * 
     * @param s The svg line with which this hero collided
     */
    private void onCollideWithSVG(PhysicsSprite s) {
        // all we do is record that the hero is not in the air anymore, and is
        // not in a jump animation anymore
        stopJump();
    }

    /**
     * Dispatch method for handling Hero collisions with Goodies
     * 
     * @param g The goodie with which this hero collided
     */
    private void onCollideWithGoodie(Goodie g) {
        // hide the goodie
        g.remove(false);

        // count this goodie
        Level.sCurrent.mScore.onGoodieCollected(g);

        // update strength if the goodie is a strength booster
        addStrength(g.mStrengthBoost);

        // deal with invincibility
        if (g.mInvincibilityDuration > 0) {
            // update the time to end invincibility
            mInvincibleRemaining += g.mInvincibilityDuration;
            // invincible animation
            if (mInvincibleAnimation != null)
                mAnimator.setCurrentAnimation(mInvincibleAnimation);
        }
    }

    /*
     * PUBLIC INTERFACE
     */

    /**
     * Make a Hero with an underlying rectangular shape
     * 
     * @param x X coordinate of the hero
     * @param y Y coordinate of the hero
     * @param width width of the hero
     * @param height height of the hero
     * @param imgName File name of the default image to display
     * @return The hero that was created
     */
    public static Hero makeAsBox(float x, float y, float width, float height, String imgName) {
        Hero h = new Hero(width, height, imgName);
        h.setBoxPhysics(0, 0, 0, BodyType.DynamicBody, false, x, y);
        Level.sCurrent.addSprite(h, 0);
        return h;
    }

    /**
     * Make a Hero with an underlying circular shape
     * 
     * @param x X coordinate of the hero
     * @param y Y coordinate of the hero
     * @param width width of the hero
     * @param height height of the hero
     * @param imgName File name of the default image to display
     * @return The hero that was created
     */
    public static Hero makeAsCircle(float x, float y, float width, float height, String imgName) {
        float radius = Math.max(width, height);
        Hero h = new Hero(width, height, imgName);
        h.setCirclePhysics(0, 0, 0, BodyType.DynamicBody, false, x, y, radius / 2);
        Level.sCurrent.addSprite(h, 0);
        return h;
    }

    /**
     * Give the hero more strength than the default, so it can survive more
     * collisions with enemies. Note that calling this will not run any strength
     * change callbacks... they only run in conjunction with collisions with
     * goodies or enemies.
     * 
     * @param amount The new strength of the hero
     */
    public void setStrength(int amount) {
        mStrength = amount;
    }

    /**
     * Return the hero's strength, in case it is useful to trigger code
     * 
     * @return The strength of the hero
     */
    public int getStrength() {
        return mStrength;
    }

    /**
     * Indicate that upon a touch, this hero should begin moving with a specific
     * velocity
     * 
     * @param x Velocity in X dimension
     * @param y Velocity in Y dimension
     */
    public void setTouchAndGo(float x, float y) {
        mTouchAndGo = new Vector2(x, y);
    }

    /**
     * Specify the X and Y velocity to give to the hero whenever it is
     * instructed to jump
     * 
     * @param x Velocity in X direction
     * @param y Velocity in Y direction
     */
    public void setJumpImpulses(float x, float y) {
        mJumpImpulses = new Vector2(x, y);
    }

    /**
     * Indicate that this hero can jump while it is in the air
     */
    public void setMultiJumpOn() {
        mAllowMultiJump = true;
    }

    /**
     * Indicate that touching this hero should make it jump
     */

    public void setTouchToJump() {
        mIsTouchJump = true;
    }

    /**
     * Register an animation sequence, so that this hero can have a custom
     * animation while jumping
     * 
     * @param a The animation to display
     */
    public void setJumpAnimation(Animation a) {
        mJumpAnimation = a;
    }

    /**
     * Set the sound to play when a jump occurs
     * 
     * @param soundName The name of the sound file to use
     */
    public void setJumpSound(String soundName) {
        mJumpSound = Media.getSound(soundName);
    }

    /**
     * Register an animation sequence, so that this hero can have a custom
     * animation while throwing
     * 
     * @param a The animation to display
     */
    public void setThrowAnimation(Animation a) {
        mThrowAnimation = a;
        // compute the length of the throw sequence, so that we can get our
        // timer right for restoring the default animation
        mThrowAnimateTotalLength = 0;
        for (long l : a.mDurations)
            mThrowAnimateTotalLength += l;
        mThrowAnimateTotalLength /= 1000; // convert to seconds
    }

    /**
     * Register an animation sequence, so that this hero can have a custom
     * animation while crawling
     * 
     * @param a The animation to display
     */
    public void setCrawlAnimation(Animation a) {
        mCrawlAnimation = a;
    }

    /**
     * Register an animation sequence, so that this hero can have a custom
     * animation while invincible
     * 
     * @param a The animation to display
     */
    public void setInvincibleAnimation(Animation a) {
        mInvincibleAnimation = a;
    }
}
