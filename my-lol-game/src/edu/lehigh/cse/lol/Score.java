package edu.lehigh.cse.lol;

// TODO: clean up comments

// TODO: need to refactor and simplify... first two methods could go in PostScene

import com.badlogic.gdx.utils.Timer;

public class Score
{
    /*
     * MANAGE WINNING AND LOSING LEVELS
     */

    /**
     * When a level ends in failure, this is how we shut it down, print a
     * message, and then let the user resume it
     * 
     * @param loseText
     *            Text to print when the level is lost
     */
    static void loseLevel(String loseText)
    {
        // Prevent multiple calls from behaving oddly
        if (Level._gameOver)
            return;
        Level._gameOver = true;

        // Run the level-complete trigger
        LOL._game.levelCompleteTrigger(false);

        if (Level._currLevel._loseSound != null)
            Level._currLevel._loseSound.play();

        // drop everything from the hud
        Level._currLevel._controls.clear();

        // TODO: For now, we'll just (ab)use the setPopUp feature... need to
        // make it more orthogonal eventually...
        //
        // NB: we can call setpopupimage too, which would make this all
        // "just work" for ALE, though still not orthogonal
        PostScene.setPopUp(loseText, 255, 255, 255, 32);
        if (Level._backgroundYouLost != null) {
            PostScene.setPopUpImage(Media.getImage(Level._backgroundYouLost)[0], 0, 0,
                    LOL._game._config.getScreenWidth(), LOL._game._config.getScreenHeight());
        }
        PostScene._win = false;
        // NB: timers really need to be stored somewhere, so we can stop/start
        // them without resorting to this coarse mechanism
        Timer.instance().clear();
    }

    /**
     * When a level is won, this is how we end the scene and allow a transition
     * to the next level
     */
    static void winLevel()
    {
        // Prevent multiple calls from behaving oddly
        if (Level._gameOver)
            return;
        Level._gameOver = true;

        // Run the level-complete trigger
        LOL._game.levelCompleteTrigger(true);

        if (Level._winSound != null)
            Level._winSound.play();

        if (LOL._game._unlockLevel == LOL._game._currLevel) {
            LOL._game._unlockLevel++;
            LOL._game.saveUnlocked();
        }

        // drop everything from the hud
        Level._currLevel._controls.clear();

        // TODO: For now, we'll just (ab)use the setPopUp feature... need to
        // make it more orthogonal eventually...
        //
        // NB: we can call setpopupimage too, which would make this all
        // "just work" for ALE, though still not orthogonal
        PostScene.setPopUp(Level._textYouWon, 255, 255, 255, 32);
        if (Level._backgroundYouWon != null) {
            PostScene.setPopUpImage(Media.getImage(Level._backgroundYouWon)[0], 0, 0,
                    LOL._game._config.getScreenWidth(), LOL._game._config.getScreenHeight());
        }
        // NB: timers really need to be stored somewhere, so we can stop/start
        // them without resorting to this coarse mechanism
        Timer.instance().clear();
        PostScene._win = true;
    }

    /*
     * BASIC SUPPORT
     */

    /**
     * Track the number of _heroes that have been created
     */
    static int _heroesCreated;

    /**
     * Track the number of _heroes that have been removed/defeated
     */
    static int _heroesDefeated;

    /**
     * Count of the goodies (with score 1) that have been collected in this
     * level
     * 
     * TODO: switch to a vector of goodie types
     */
    static int [] _goodiesCollected = new int[4];

    /**
     * Number of _heroes who have arrived at any destination yet
     */
    static int _destinationArrivals;

    /**
     * Count the number of enemies that have been created
     */
    static int _enemiesCreated;

    /**
     * Count the enemies that have been defeated
     */
    static int _enemiesDefeated;

    /**
     * Reset score info when a new level is created
     */
    static void reset()
    {
        // reset the hero statistics
        _heroesCreated = 0;
        _heroesDefeated = 0;
        // reset goodie statistics
        for (int i = 0; i < 4; ++i)
            _goodiesCollected[i] = 0;

        // reset destination statistics
        _destinationArrivals = 0;

        // reset enemy statistics
        _enemiesCreated = 0;
        _enemiesDefeated = 0;
    }

    /*
     * METHODS CALLED WHEN AN ENTITY IS REMOVED
     */

    /**
     * Use this to inform the level that a hero has been defeated
     * 
     * @param e
     *            The enemy who defeated the hero
     */
    static void defeatHero(Enemy e)
    {
        _heroesDefeated++;
        if (_heroesDefeated == Score._heroesCreated)
            loseLevel(e._onDefeatHeroText != "" ? e._onDefeatHeroText : Level._textYouLost);
    }

    /**
     * Use this to inform the level that a goodie has been collected by the hero
     * 
     * @param g
     *            The goodie that was collected
     */
    static void onGoodieCollected(Goodie g)
    {
        // Update any/all goodie counts
        for (int i = 0; i < 4; ++i)
            _goodiesCollected[i] += g._score[i];

        // possibly win the level, but only if we win on goodie count and all
        // four counts are high enoug
        if (Level._victoryType != Level.VictoryType.GOODIECOUNT)
            return;
        if ((Level._victoryGoodie1Count <= _goodiesCollected[0]) && (Level._victoryGoodie2Count <= _goodiesCollected[1])
                && (Level._victoryGoodie3Count <= _goodiesCollected[2])
                && (Level._victoryGoodie4Count <= _goodiesCollected[3]))
        {
            winLevel();
        }

    }

    /**
     * Use this to inform the level that a hero has reached the destination
     * 
     * @param d
     *            The destination that the hero reached
     */
    static void onDestinationArrive()
    {
        // check if the level is complete
        _destinationArrivals++;
        if ((Level._victoryType == Level.VictoryType.DESTINATION) && (_destinationArrivals >= Level._victoryHeroCount))
            winLevel();
    }

    /**
     * Internal method for handling whenever an enemy is defeated
     */
    static void onDefeatEnemy()
    {

        // update the count of defeated enemies
        _enemiesDefeated++;

        // if we win by defeating enemies, see if we've defeated enough of them:
        boolean win = false;
        if (Level._victoryType == Level.VictoryType.ENEMYCOUNT) {
            // -1 means "defeat all enemies"
            if (Level._victoryEnemyCount == -1)
                win = _enemiesDefeated == _enemiesCreated;
            else
                win = _enemiesDefeated >= Level._victoryEnemyCount;
        }
        if (win)
            winLevel();
    }

    /*
     * MANUAL SCORE MANIPULATION
     */

    /**
     * Manually set the number of goodies of type 1 that have been collected.
     * 
     * @param value
     *            The number to increment the number of goodies collected by.
     */
    public static void setGoodiesCollected1(int value)
    {
        _goodiesCollected[0] = value;
    }

    /**
     * Manually set the number of goodies of type 2 that have been collected.
     * 
     * @param value
     *            The number to increment the number of goodies collected by.
     */
    public static void setGoodiesCollected2(int value)
    {
        _goodiesCollected[1] = value;
    }

    /**
     * Manually set the number of goodies of type 3 that have been collected.
     * 
     * @param value
     *            The number to increment the number of goodies collected by.
     */
    public static void setGoodiesCollected3(int value)
    {
        _goodiesCollected[2] = value;
    }

    /**
     * Manually set the number of goodies of type 4 that have been collected.
     * 
     * @param value
     *            The number to increment the number of goodies collected by.
     */
    public static void setGoodiesCollected4(int value)
    {
        _goodiesCollected[3] = value;
    }

    /**
     * Manually increment the number of goodies of type 1 that have been
     * collected.
     */
    public static void incrementGoodiesCollected1()
    {
        _goodiesCollected[0]++;
    }

    /**
     * Manually increment the number of goodies of type 2 that have been
     * collected.
     */
    public static void incrementGoodiesCollected2()
    {
        _goodiesCollected[1]++;
    }

    /**
     * Manually increment the number of goodies of type 3 that have been
     * collected.
     */
    public static void incrementGoodiesCollected3()
    {
        _goodiesCollected[2]++;
    }

    /**
     * Manually increment the number of goodies of type 4 that have been
     * collected.
     */
    public static void incrementGoodiesCollected4()
    {
        _goodiesCollected[3]++;
    }

    /**
     * Getter for number of goodies of type 1 that have been collected.
     * 
     * @return The number of goodies collected.
     */
    public static int getGoodiesCollected1()
    {
        return _goodiesCollected[0];
    }

    /**
     * Getter for number of goodies of type 2 that have been collected.
     * 
     * @return The number of goodies collected.
     */
    public static int getGoodiesCollected2()
    {
        return _goodiesCollected[1];
    }

    /**
     * Getter for number of goodies of type 3 that have been collected.
     * 
     * @return The number of goodies collected.
     */
    public static int getGoodiesCollected3()
    {
        return _goodiesCollected[2];
    }

    /**
     * Getter for number of goodies of type 4 that have been collected.
     * 
     * @return The number of goodies collected.
     */
    public static int getGoodiesCollected4()
    {
        return _goodiesCollected[3];
    }
}