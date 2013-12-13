package edu.lehigh.cse.ale;

// TODO: there is a lot of redundant code related to font management and text creation 

// TODO: clean up comments

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import edu.lehigh.cse.ale.Level.HudEntity;
import edu.lehigh.cse.ale.Level.HudPress;

public class Controls
{
    /*
     * BASIC FUNCTIONALITY
     */

    /**
     * Store the duration between when the program started and when the _current level started, so that we can reuse the
     * timer from one level to the next
     */
    private static float _countDownRemaining;
    
    /**
     * Store the amount of stopwatch that has transpired
     */
    private static float _stopWatchProgress;

    private static float _winCountRemaining;
    
    /**
     * Controls is a pure static class, and should never be constructed explicitly
     */
    private Controls()
    {
    }

    /*
     * TEXT-ONLY CONTROLS
     */

    /**
     * Change the amount of time left in a countdown
     *
     * @param delta
     *            The amount of time to add before the timer expires
     */
    public static void updateTimerExpiration(float delta)
    {
        _countDownRemaining += delta;
    }

    static void updateTimerForPause(float delta)
    {
        _countDownRemaining += delta;
        // TODO: how do we deal with pausing? This next line isn't needed, but a
        // pause screen could change that...
        //
        // _stopWatchProgress -= delta;
    }
    
    /**
     * Add a countdown timer to the screen. When time is up, the level ends in defeat
     *
     * @param timeout
     *            Starting value of the timer
     * @param text
     *            The text to display when the timer expires
     * @param x
     *            The x coordinate where the timer should be drawn
     * @param y
     *            The y coordinate where the timer should be drawn
     */
      public static void addCountdown(float timeout, String text, int x, int y)
    {
        addCountdown(timeout, text, x, y, 255, 255, 255, 32);
    }

    /**
     * Add a countdown timer to the screen, with extra features for describing the appearance of the font. When time is
     * up, the level ends in defeat.
     *
     * @param timeout
     *            Starting value of the timer
     * @param text
     *            The text to display when the timer expires
     * @param x
     *            The x coordinate where the timer should be drawn
     * @param y
     *            The y coordinate where the timer should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
      public static void addCountdown(final float timeout, final String text, final int x, final int y, final int red, final int green, final int blue,
              int size)
      {
          _countDownRemaining = timeout; 
          final BitmapFont bf = Media.getFont("arial.ttf", size);
          HudEntity he = new HudEntity(){
              @Override
              void render(SpriteBatch sb)
              {
                  // handle color
                  float r = red;
                  float g = green;
                  float b = blue;
                  r = r/256;
                  g = g/256;
                  b = b/256;
                  bf.setColor(r, g, b, 1);

                  _countDownRemaining -= Gdx.graphics.getDeltaTime();

                  if (_countDownRemaining > 0) {
                      // get elapsed time for this level
                      String newtext = "" + (int)_countDownRemaining;
                      bf.draw(sb, newtext, x, y);
                  }
                  else {
                      Score.loseLevel(text);
                  }
            }            
        };
        Level._currLevel._hudEntries.add(he);
      }

      /**
       * Print the frames per second
       * 
       * @param x
       * @param y
       * @param red
       * @param green
       * @param blue
       * @param size
       */
      public static void addFPS(final int x, final int y, final int red, final int green, final int blue, int size)
      {
          final BitmapFont bf = Media.getFont("arial.ttf", size);
          HudEntity he = new HudEntity(){
              @Override
              void render(SpriteBatch sb)
              {
                  // handle color
                  float r = red;
                  float g = green;
                  float b = blue;
                  bf.setColor(r/256, g/256, b/256, 1);
                  bf.draw(sb, "fps: " + Gdx.graphics.getFramesPerSecond(), x, y);
            }            
        };
        Level._currLevel._hudEntries.add(he);
      }

      
    /**
     * Add a countdown timer to the screen. When time is up, the level ends in victory
     *
     * @param timeout
     *            Starting value of the timer
     * @param x
     *            The x coordinate where the timer should be drawn
     * @param y
     *            The y coordinate where the timer should be drawn
     */
    public static void addWinCountdown(float timeout, int x, int y)
    {
        addWinCountdown(timeout, x, y, 255, 255, 255, 32);
    }

    /**
     * Add a countdown timer to the screen, with extra features for describing the appearance of the font. When time is
     * up, the level ends in victory
     *
     * @param timeout
     *            Starting value of the timer
     * @param x
     *            The x coordinate where the timer should be drawn
     * @param y
     *            The y coordinate where the timer should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
    public static void addWinCountdown(final float timeout, final int x, final int y, final int red, final int green, final int blue,
            int size)
    {
        _winCountRemaining = timeout; 
        final BitmapFont bf = Media.getFont("arial.ttf", size);
        HudEntity he = new HudEntity(){
            @Override
            void render(SpriteBatch sb)
            {
                // handle color
                float r = red;
                float g = green;
                float b = blue;
                r = r/256;
                g = g/256;
                b = b/256;
                bf.setColor(r, g, b, 1);

                _winCountRemaining -= Gdx.graphics.getDeltaTime();

                if (_winCountRemaining > 0) {
                    // get elapsed time for this level
                    String newtext = "" + (int)_winCountRemaining;
                    bf.draw(sb, newtext, x, y);
                }
                else {
                    Score.winLevel();
                }
          }            
      };
      Level._currLevel._hudEntries.add(he);
    }

    /**
     * Add a count of the current number of goodies of type 1
     *
     * @param max
     *            If this is > 0, then the message wil be of the form XX/max instead of just XX
     * @param text
     *            The text to display after the number of goodies
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     */
      public static void addGoodieCount1(int max, String text, int x, int y)
    {
        addGoodieCount1(max, text, x, y, 255, 255, 255, 32);
    }

  
    /**
     * Add a count of the current number of goodies of type 1, with extra features for describing the appearance of the font
     *
     * @param max
     *            If this is > 0, then the message wil be of the form XX/max instead of just XX
     * @param text
     *            The text to display after the number of goodies
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
    public static void addGoodieCount1(int max, final String text, final int x, final int y, final int red, final int green, final int blue, int size)
    {
        // The suffix to display after the goodie count:
        final String suffix = (max > 0) ? "/" + max + " " + text : " " + text;
        final BitmapFont bf = Media.getFont("arial.ttf", size);
        HudEntity he = new HudEntity(){
            @Override
            void render(SpriteBatch sb)
            {
                // handle color
                float r = red;
                float g = green;
                float b = blue;
                r = r/256;
                g = g/256;
                b = b/256;
                bf.setColor(r, g, b, 1);

                // get elapsed time for this level
                String newtext = "" + Score._goodiesCollected1 + suffix;
                bf.draw(sb, newtext, x, y);
            }            
        };
        Level._currLevel._hudEntries.add(he);
    }

    /**
     * Add a count of the current number of goodies of type 2, with extra features for describing the appearance of the font
     *
     * @param max
     *            If this is > 0, then the message wil be of the form XX/max instead of just XX
     * @param text
     *            The text to display after the number of goodies
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
 public static void addGoodieCount2(int max, final String text, final int x, final int y, final int red, final int green, final int blue, int size)
    {
     // The suffix to display after the goodie count:
     final String suffix = (max > 0) ? "/" + max + " " + text : " " + text;
     final BitmapFont bf = Media.getFont("arial.ttf", size);
     HudEntity he = new HudEntity(){
         @Override
         void render(SpriteBatch sb)
         {
             // handle color
             float r = red;
             float g = green;
             float b = blue;
             r = r/256;
             g = g/256;
             b = b/256;
             bf.setColor(r, g, b, 1);

             // get elapsed time for this level
             String newtext = "" + Score._goodiesCollected2 + suffix;
             bf.draw(sb, newtext, x, y);
         }            
     };
     Level._currLevel._hudEntries.add(he);
    }

    /**
     * Add a count of the current number of goodies of type 3, with extra features for describing the appearance of the font
     *
     * @param max
     *            If this is > 0, then the message wil be of the form XX/max instead of just XX
     * @param text
     *            The text to display after the number of goodies
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
 public static void addGoodieCount3(int max, final String text, final int x, final int y, final int red, final int green, final int blue, int size)
    {
     // The suffix to display after the goodie count:
     final String suffix = (max > 0) ? "/" + max + " " + text : " " + text;
     final BitmapFont bf = Media.getFont("arial.ttf", size);
     HudEntity he = new HudEntity(){
         @Override
         void render(SpriteBatch sb)
         {
             // handle color
             float r = red;
             float g = green;
             float b = blue;
             r = r/256;
             g = g/256;
             b = b/256;
             bf.setColor(r, g, b, 1);

             // get elapsed time for this level
             String newtext = "" + Score._goodiesCollected3 + suffix;
             bf.draw(sb, newtext, x, y);
         }            
     };
     Level._currLevel._hudEntries.add(he);

    }

    /**
     * Add a count of the current number of goodies of type 4, with extra features for describing the appearance of the font
     *
     * @param max
     *            If this is > 0, then the message wil be of the form XX/max instead of just XX
     * @param text
     *            The text to display after the number of goodies
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
 public static void addGoodieCount4(int max, final String text, final int x, final int y, final int red, final int green, final int blue, int size)
    {
     // The suffix to display after the goodie count:
     final String suffix = (max > 0) ? "/" + max + " " + text : " " + text;
     final BitmapFont bf = Media.getFont("arial.ttf", size);
     HudEntity he = new HudEntity(){
         @Override
         void render(SpriteBatch sb)
         {
             // handle color
             float r = red;
             float g = green;
             float b = blue;
             r = r/256;
             g = g/256;
             b = b/256;
             bf.setColor(r, g, b, 1);

             // get elapsed time for this level
             String newtext = "" + Score._goodiesCollected4 + suffix;
             bf.draw(sb, newtext, x, y);
         }            
     };
     Level._currLevel._hudEntries.add(he);
    }

    /**
     * Add a count of the _current number of enemies who have been defeated
     *
     * @param max
     *            If this is > 0, then the message wil be of the form XX/max instead of just XX
     * @param text
     *            The text to display after the number of goodies
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     */
    public static void addDefeatedCount(int max, String text, int x, int y)
    {
        addDefeatedCount(max, text, x, y, 255, 255, 255, 32);
    }

    /**
     * Add a count of the _current number of enemies who have been defeated, with extra features for describing the
     * appearance of the font
     *
     * @param max
     *            If this is > 0, then the message wil be of the form XX/max instead of just XX
     * @param text
     *            The text to display after the number of goodies
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
    public static void addDefeatedCount(int max, final String text, final int x, final int y, final int red, final int green, final int blue, int size)
    {
        // The suffix to display after the goodie count:
        final String suffix = (max > 0) ? "/" + max + " " + text : " " + text;
        final BitmapFont bf = Media.getFont("arial.ttf", size);
        HudEntity he = new HudEntity(){
            @Override
            void render(SpriteBatch sb)
            {
                // handle color
                float r = red;
                float g = green;
                float b = blue;
                r = r/256;
                g = g/256;
                b = b/256;
                bf.setColor(r, g, b, 1);

                // get elapsed time for this level
                String newtext = "" + Score._enemiesDefeated + suffix;
                bf.draw(sb, newtext, x, y);
            }            
        };
        Level._currLevel._hudEntries.add(he);
    }

    /**
     * Add a stopwatch for tracking how long a level takes
     *
     * @param x
     *            The x coordinate where the stopwatch should be drawn
     * @param y
     *            The y coordinate where the stopwatch should be drawn
     */
    static public void addStopwatch(int x, int y)
    {
        addStopwatch(x, y, 255, 255, 255, 32);
    }

    /**
     * Add a stopwatch for tracking how long a level takes, with extra features for describing the appearance of the
     * font
     *
     * @param x
     *            The x coordinate where the stopwatch should be drawn
     * @param y
     *            The y coordinate where the stopwatch should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
    static public void addStopwatch(final int x, final int y, final int red, final int green, final int blue, int size)
    {
        _stopWatchProgress = 0;
        final BitmapFont bf = Media.getFont("arial.ttf", size);
        HudEntity he = new HudEntity(){
            @Override
            void render(SpriteBatch sb)
            {
                // handle color
                float r = red;
                float g = green;
                float b = blue;
                r = r/256;
                g = g/256;
                b = b/256;
                bf.setColor(r, g, b, 1);
                
                _stopWatchProgress += Gdx.graphics.getDeltaTime();
                // get elapsed time for this level
                String newtext = "" + (int)_stopWatchProgress;
                bf.draw(sb, newtext, x, y);
            }            
        };
        Level._currLevel._hudEntries.add(he);
    }

    /**
     * Display a strength meter
     *
     * @param text
     *            The text to display after the remaining _strength value
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     */
    static public void addStrengthMeter(String text, int x, int y, Hero h)
    {
        // forward to the more powerful method...
        addStrengthMeter(text, x, y, 255, 255, 255, 32, h);
    }

    /**
     * Display a _strength meter, with extra features for describing the appearance of the font
     *
     * @param text
     *            The text to display after the remaining _strength value
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     * @param red
     *            A value between 0 and 255, indicating the red portion of the font color
     * @param green
     *            A value between 0 and 255, indicating the green portion of the font color
     * @param blue
     *            A value between 0 and 255, indicating the blue portion of the font color
     * @param size
     *            The font size, typically 32 but can be varied depending on the amount of text being drawn to the
     *            screen
     */
    static public void addStrengthMeter(final String text, final int x, final int y, final int red, final int green, final int blue, int size, final Hero h)
    {
        final BitmapFont bf = Media.getFont("arial.ttf", size);
        HudEntity he = new HudEntity(){
            @Override
            void render(SpriteBatch sb)
            {
                // handle color
                float r = red;
                float g = green;
                float b = blue;
                r = r/256;
                g = g/256;
                b = b/256;
                bf.setColor(r, g, b, 1);

                // get elapsed time for this level
                String newtext = "" + h._strength + " " + text;
                bf.draw(sb, newtext, x, y);
            }            
        };
        Level._currLevel._hudEntries.add(he);
    }

    /**
     * Display the number of remaining projectiles
     *
     * @param text
     *            The text to display after the number of goodies
     * @param x
     *            The x coordinate where the text should be drawn
     * @param y
     *            The y coordinate where the text should be drawn
     * @param red
     *            The red dimension of the font color
     * @param green
     *            The green dimension of the font color
     * @param blue
     *            The blue dimension of the font color
     * @param size
     *            The size of the font
     */
    public static void addProjectileCount(final String text, final int x, final int y, final int red, final int green, final int blue, int size)
    {
        final BitmapFont bf = Media.getFont("arial.ttf", size);
        HudEntity he = new HudEntity(){
            @Override
            void render(SpriteBatch sb)
            {
                // handle color
                float r = red;
                float g = green;
                float b = blue;
                r = r/256;
                g = g/256;
                b = b/256;
                bf.setColor(r, g, b, 1);

                // get elapsed time for this level
                String newtext = "" + Projectile._projectilesRemaining + " " + text;
                bf.draw(sb, newtext, x, y);
            }            
        };
        Level._currLevel._hudEntries.add(he);
    }

    /*
     * GRAPHICAL BUTTON CONTROLS
     */

    /**
     * Add a button that moves an entity downward
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param rate
     *            Rate at which the entity moves
     * @param entity
     *            The entity to move downward
     */
    public static void addDownButton(int x, int y, int width, int height, String imgName, final float rate,
            final PhysicsSprite entity)
    {
        if (entity._physBody.getType() == BodyType.StaticBody)
            entity._physBody.setType(BodyType.KinematicBody);
        
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Vector2 v = entity._physBody.getLinearVelocity();
                v.y = -rate;
                entity.updateVelocity(v);
            }

            @Override
            void onUpPress()
            {    
                Vector2 v = entity._physBody.getLinearVelocity();
                v.y = 0;
                entity.updateVelocity(v);
            }
        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button that moves an entity upward
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param rate
     *            Rate at which the entity moves
     * @param entity
     *            The entity to move
     */
    public static void addUpButton(int x, int y, int width, int height, String imgName, final float rate,
            final PhysicsSprite entity)
    {
        if (entity._physBody.getType() == BodyType.StaticBody)
            entity._physBody.setType(BodyType.KinematicBody);
        
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Vector2 v = entity._physBody.getLinearVelocity();
                v.y = rate;
                entity.updateVelocity(v);
            }

            @Override
            void onUpPress()
            {    
                Vector2 v = entity._physBody.getLinearVelocity();
                v.y = 0;
                entity.updateVelocity(v);
            }
        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button that moves the given entity left
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param rate
     *            Rate at which the entity moves
     * @param entity
     *            The entity that should move left when the button is pressed
     */
    public static void addLeftButton(int x, int y, int width, int height, String imgName, final float rate,
            final PhysicsSprite entity)
    {
        // TODO: on Level 60, we want this to be a DynamicBody... can we do that
        // universally, or is there a problem? I made it dynamic for now, ensure
        // we don't actually want kinematic

        // TODO: all these buttons should work while holding...
        if (entity._physBody.getType() == BodyType.StaticBody)
            entity._physBody.setType(BodyType.DynamicBody);
        
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Vector2 v = entity._physBody.getLinearVelocity();
                v.x = -rate;
                entity.updateVelocity(v);
            }

            @Override
            void onUpPress()
            {    
                Vector2 v = entity._physBody.getLinearVelocity();
                v.x = 0;
                entity.updateVelocity(v);
            }
        };
        // TODO: this is the *right* way to do images
        TextureRegion[] trs = Media.getImage(imgName);
        if (trs != null)
            pe.tr = trs[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button that moves the given entity to the right
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param rate
     *            Rate at which the entity moves
     * @param entity
     *            The entity that should move right when the button is pressed
     */
    public static void addRightButton(int x, int y, int width, int height, String imgName, final float rate,
            final PhysicsSprite entity)
    {
        // see left button for note on body type
        if (entity._physBody.getType() == BodyType.StaticBody)
            entity._physBody.setType(BodyType.DynamicBody);
        
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Vector2 v = entity._physBody.getLinearVelocity();
                v.x = rate;
                entity.updateVelocity(v);
            }

            @Override
            void onUpPress()
            {    
                Vector2 v = entity._physBody.getLinearVelocity();
                v.x = 0;
                entity.updateVelocity(v);
            }
        };
        // TODO: this is the *right* way to do images
        TextureRegion[] trs = Media.getImage(imgName);
        if (trs != null)
            pe.tr = trs[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button that moves the given entity at one speed when it is depressed, and at another otherwise
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param rateDownX
     *            Rate (X) at which the entity moves when the button is pressed
     * @param rateDownY
     *            Rate (Y) at which the entity moves when the button is pressed
     * @param rateUpX
     *            Rate (X) at which the entity moves when the button is not pressed
     * @param rateUpY
     *            Rate (Y) at which the entity moves when the button is not pressed
     * @param entity
     *            The entity that should move left when the button is pressed
     */
    public static void addTurboButton(int x, int y, int width, int height, String imgName, final int rateDownX,
            final int rateDownY, final int rateUpX, final int rateUpY, final PhysicsSprite entity)
    {
        // see left button for note on body type
        if (entity._physBody.getType() == BodyType.StaticBody)
            entity._physBody.setType(BodyType.DynamicBody);
        
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Vector2 v = entity._physBody.getLinearVelocity();
                v.x = rateDownX;
                v.y = rateDownY;
                entity.updateVelocity(v);
            }

            @Override
            void onUpPress()
            {   
                // TODO: do this on 'not pressing button anymore' too
                Vector2 v = entity._physBody.getLinearVelocity();
                v.x = rateUpX;
                v.y = rateUpY;
                entity.updateVelocity(v);
            }
        };
        // TODO: this is the *right* way to do images
        TextureRegion[] trs = Media.getImage(imgName);
        if (trs != null)
            pe.tr = trs[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button that moves the given entity at one speed, but doesn't stop the entity when the button is released
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param rateX
     *            Rate (X) at which the entity moves when the button is pressed
     * @param rateY
     *            Rate (Y) at which the entity moves when the button is pressed
     * @param entity
     *            The entity that should move left when the button is pressed
     */
    public static void addDampenedMotionButton(int x, int y, int width, int height, String imgName, final float rateX,
            final float rateY, final float dampening, final PhysicsSprite entity)
    {
        // see left button for note on body type
        if (entity._physBody.getType() == BodyType.StaticBody)
            entity._physBody.setType(BodyType.DynamicBody);
        
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Vector2 v = entity._physBody.getLinearVelocity();
                v.x = rateX;
                v.y = rateY;
                entity._physBody.setLinearDamping(0);
                entity.updateVelocity(v);
            }

            @Override
            void onUpPress()
            {   
                entity._physBody.setLinearDamping(dampening);
            }
        };
        // TODO: this is the *right* way to do images
        TextureRegion[] trs = Media.getImage(imgName);
        if (trs != null)
            pe.tr = trs[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button that puts the hero into crawl mode when depressed, and regular mode when released
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     */
    public static void addCrawlButton(int x, int y, int width, int height, String imgName, final Hero h)
    {
        Level.HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Gdx.app.log("crawl", "drawl down");

                h.crawlOn();
            }

            @Override
            void onUpPress()
            {
                Gdx.app.log("crawl", "crawl up");

                h.crawlOff();
            }

        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);
    }

    /**
     * Add a button to make the hero jump
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     */
    public static void addJumpButton(int x, int y, int width, int height, String imgName, final Hero h)
    {
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Gdx.app.log("jump", "jump down");
                h.jump();
            }

            @Override
            void onUpPress()
            {
                Gdx.app.log("jump", "jump up");
            }

        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button to make the hero throw a projectile
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     */
    public static void addThrowButton(int x, int y, int width, int height, String imgName, final Hero h)
    {
        // TODO: this should keep throwing if we hold...
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Projectile.throwFixed(h._physBody.getPosition().x, h._physBody.getPosition().y, h);
            }

            @Override
            void onUpPress()
            {
            }

        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button to make the hero throw a projectile, but holding doesn't make it throw more often
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     */
    public static void addSingleThrowButton(int x, int y, int width, int height, String imgName, final Hero h)
    {
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Projectile.throwFixed(h._physBody.getPosition().x, h._physBody.getPosition().y, h);
            }

            @Override
            void onUpPress()
            {
            }

        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * The default behavior for throwing is to throw in a straight line. If we instead desire that the bullets have some
     * sort of aiming to them, we need to use this method, which throws toward where the screen was pressed
     *
     * Note: you probably want to use an invisible button that covers the screen...
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     */
    public static void addVectorThrowButton(int x, int y, int width, int height, String imgName, final Hero h)
    {
        // TODO: this should keep throwing if we hold...
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Projectile.throwAt(h._physBody.getPosition().x, h._physBody.getPosition().y, vv.x, vv.y, h);
            }

            @Override
            void onUpPress()
            {
            }

        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * The default behavior for throwing projectiles is to throw in a straight line. If we instead desire that the
     * bullets have some sort of aiming to them, we need to use this method, which throws toward where the screen was
     * pressed. Note that with this command, the button that is drawn on the screen cannot be held down to throw
     * multipel projectiles in rapid succession.
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     */
    public static void addVectorSingleThrowButton(int x, int y, int width, int height, String imgName, final Hero h)
    {
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                Projectile.throwAt(h._physBody.getPosition().x, h._physBody.getPosition().y, vv.x, vv.y, h);
            }

            @Override
            void onUpPress()
            {
            }

        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Display a zoom in button
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param maxZoom
     *            Maximum zoom. 8 is usually a good default
     */
    public static void addZoomOutButton(float x, float y, float width, float height, String imgName, final float maxZoom)
    {
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 v)
            {
                float curzoom = Level._currLevel._gameCam.zoom;
                if (curzoom < maxZoom)
                    Level._currLevel._gameCam.zoom *= 2;
            }

            @Override
            void onUpPress()
            {
            }
        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Display a zoom out button
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param minZoom
     *            Minimum zoom. 0.25f is usually a good default
     */
    public static void addZoomInButton(int x, int y, int width, int height, String imgName, final float minZoom)
    {
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 v)
            {
                float curzoom = Level._currLevel._gameCam.zoom;
                if (curzoom > minZoom)
                    Level._currLevel._gameCam.zoom /= 2;
            }

            @Override
            void onUpPress()
            {    
            }
        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button that rotates the hero
     *
     * @param x
     *            X coordinate of top left corner of the button
     * @param y
     *            Y coordinate of top left corner of the button
     * @param width
     *            Width of the button
     * @param height
     *            Height of the button
     * @param imgName
     *            Name of the image to use for this button
     * @param rate
     *            Amount of rotation to apply to the hero on each press
     */
    public static void addRotateButton(int x, int y, int width, int height, String imgName, final float rate, final Hero h)
    {
        // TODO: this should keep rotating if we hold...
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                h.increaseRotation(rate);
            }

            @Override
            void onUpPress()
            {
            }

        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add an image to the heads-up display
     *
     * @param x
     *            X coordinate of top left corner of the image
     * @param y
     *            Y coordinate of top left corner of the image
     * @param width
     *            Width of the image
     * @param height
     *            Height of the image
     * @param imgName
     *            Name of the image to use
     */
    public static void addImage(int x, int y, int width, int height, String imgName)
    {
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                // TODO: this should return true/false, so we know if we should propagate...
            }
            @Override
            void onUpPress()
            {
                // TODO: this should return true/false, so we know if we should propagate...
            }
        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);        
    }

    /**
     * Add a button to the heads-up display that runs custom code via onControlPress
     *
     * @param x
     *            X coordinate of top left corner of the image
     * @param y
     *            Y coordinate of top left corner of the image
     * @param width
     *            Width of the image
     * @param height
     *            Height of the image
     * @param imgName
     *            Name of the image to use
     * @param id
     *            An id to use for the trigger event
     */
    public static void addTriggerControl(int x, int y, int width, int height, String imgName, final int id)
    {
        HudPress pe = new HudPress() {
            @Override
            void onDownPress(Vector3 vv)
            {
                ALE._game.onControlPressTrigger(id, ALE._game._currLevel);
            }
            @Override
            void onUpPress()
            {
            }
        };
        if (!imgName.equals(""))
            pe.tr = Media.getImage(imgName)[0];
        pe._done = false;
        pe._range = new Rectangle(x, y, width, height);        
        Level._currLevel._controls.add(pe);               
    }
}
