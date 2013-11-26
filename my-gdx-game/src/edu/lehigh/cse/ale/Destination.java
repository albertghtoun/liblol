package edu.lehigh.cse.ale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Destination extends PhysicsSprite {

	/*
	 * BASIC FUNCTIONALITY
	 */

	/**
	 * number of _heroes who can fit at /this/ destination
	 */
	int _capacity;

	/**
	 * number of _heroes already in /this/ destination
	 */
	int _holding;

	/**
	 * number of type 1 goodies that must be collected before this destination
	 * accepts any heroes
	 */
	int _activationScore1;

	/**
	 * number of type 2 goodies that must be collected before this destination
	 * accepts any heroes
	 */
	int _activationScore2;

	/**
	 * number of type 3 goodies that must be collected before this destination
	 * accepts any heroes
	 */
	int _activationScore3;

	/**
	 * number of type 4 goodies that must be collected before this destination
	 * accepts any heroes
	 */
	int _activationScore4;

	/**
	 * Create a destination
	 * 
	 * This should never be called directly.
	 * 
	 * @param x
	 *            X coordinate of top left corner of this destination
	 * @param y
	 *            X coordinate of top left corner of this destination
	 * @param width
	 *            Width of this destination
	 * @param height
	 *            Height of this destination
	 * @param ttr
	 *            Image to display
	 * @param isStatic
	 *            Can this destination move, or is it at a fixed location
	 * @param isCircle
	 *            true if this should use a circle underneath for its collision
	 *            detection, and false if a box should be used
	 */
	private Destination(float width, float height, TextureRegion tr) {
		super(tr, SpriteId.DESTINATION, width, height);
		_capacity = 1;
		_holding = 0;
		_activationScore1 = 0;
		_activationScore2 = 0;
		_activationScore3 = 0;
		_activationScore4 = 0;
	}

	/**
	 * Add a simple destination that uses a circle as its _fixture
	 * 
	 * @param x
	 *            X coordinate of top left corner of this destination
	 * @param y
	 *            X coordinate of top left corner of this destination
	 * @param width
	 *            Width of this destination
	 * @param height
	 *            Height of this destination
	 * @param imgName
	 *            Name of the image to display
	 * @return the Destination, so that it can be manipulated further
	 */
	public static Destination makeAsCircle(float x, float y, float width,
			float height, String imgName) {
		TextureRegion tr = new TextureRegion(new Texture(
				Gdx.files.internal("data/" + imgName)));
		float radius = (width > height) ? width : height;
		Destination d = new Destination(radius * 2, radius * 2, tr);

		// NB: this is a circle... really!
		CircleShape c = new CircleShape();
		c.setRadius(radius);
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = BodyType.StaticBody;
		boxBodyDef.position.x = x;
		boxBodyDef.position.y = y;
		d._physBody = Level._current._world.createBody(boxBodyDef);
		d._physBody.createFixture(c, 1);
		d._physBody.getFixtureList().get(0).setSensor(true);
		// link the body to the sprite
		d._physBody.setUserData(d);
		c.dispose();
		Level._current._sprites.add(d);
		return d;
	}

	/**
	 * Add a simple destination that uses a box as its _fixture
	 * 
	 * @param x
	 *            X coordinate of top left corner of this destination
	 * @param y
	 *            X coordinate of top left corner of this destination
	 * @param width
	 *            Width of this destination
	 * @param height
	 *            Height of this destination
	 * @param imgName
	 *            Name of the image to display
	 * @return the Destination, so that it can be manipulated further
	 */
	public static Destination makeAsBox(float x, float y, float width,
			float height, String imgName) {
		// get the image, create a destination, and give it default _physics
		TextureRegion ttr = Media.getImage(imgName);
		Destination dest = new Destination(width, height, ttr);
		dest.setBoxPhysics(0, 0, 0, BodyType.StaticBody, false, x, y);
		dest.setCollisionEffect(false);

		// add the destination to the scene
		Level._current._sprites.add(dest);

		// return the destination, so it can be modified
		return dest;
	}

	/**
	 * Change the number of type-1 goodies that must be collected before the
	 * destination accepts any heroes (the default is 0)
	 * 
	 * @param score
	 *            The number of goodies that must be collected.
	 * 
	 * @deprecated Use setActiviationScore[1-4]() instead
	 */
	@Deprecated
	public void setActivationScore(int score) {
		_activationScore1 = score;
	}

	/**
	 * Change the number of type-1 goodies that must be collected before the
	 * destination accepts any heroes (the default is 0)
	 * 
	 * @param score
	 *            The number of goodies that must be collected.
	 */
	public void setActivationScore1(int score) {
		_activationScore1 = score;
	}

	/**
	 * Change the number of type-2 goodies that must be collected before the
	 * destination accepts any heroes (the default is 0)
	 * 
	 * @param score
	 *            The number of goodies that must be collected.
	 */
	public void setActivationScore2(int score) {
		_activationScore2 = score;
	}

	/**
	 * Change the number of type-3 goodies that must be collected before the
	 * destination accepts any heroes (the default is 0)
	 * 
	 * @param score
	 *            The number of goodies that must be collected.
	 */
	public void setActivationScore3(int score) {
		_activationScore3 = score;
	}

	/**
	 * Change the number of type-4 goodies that must be collected before the
	 * destination accepts any heroes (the default is 0)
	 * 
	 * @param score
	 *            The number of goodies that must be collected.
	 */
	public void setActivationScore4(int score) {
		_activationScore4 = score;
	}

	/**
	 * Change the number of _heroes that can be accepted by this destination
	 * (the default is 1)
	 * 
	 * @param _heroes
	 *            The number of _heroes that can be accepted
	 */
	public void setHeroCount(int heroes) {
		_capacity = heroes;
	}

	/*
	 * COLLISION SUPPORT
	 */

	/**
	 * Destinations are the last collision detection entity, so their collision
	 * detection code does nothing.
	 * 
	 * @param other
	 *            Other object involved in this collision
	 */
	void onCollide(PhysicsSprite other) {
	}

	/*
	 * AUDIO SUPPORT
	 */

	/**
	 * Sound to play when a hero arrives at this destination
	 */
	Sound _arrivalSound;

	/**
	 * Specify the sound to play when a hero arrives at this destination
	 * 
	 * @param sound
	 *            The sound file name that should play
	 */
	public void setArrivalSound(String soundName) {
		_arrivalSound = Media.getSound(soundName);
	}

}
