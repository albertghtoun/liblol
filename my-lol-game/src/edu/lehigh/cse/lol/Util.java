package edu.lehigh.cse.lol;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Util
{
    static Renderable makePicture(final float x, final float y, final float width, final float height, String imgName)
    {
        // set up the image to display
        //
        // NB: this will fail gracefully (no crash) for invalid file names
        TextureRegion[] trs = Media.getImage(imgName);
        final TextureRegion tr = (trs != null) ? trs[0] : null;
        return new Renderable()
        {
            @Override
            public void render(SpriteBatch sb, float elapsed)
            {
                if (tr != null)
                    sb.draw(tr, x, y, 0, 0, width, height, 1, 1, 0);
            }
        };
    }

    static Renderable makeText(final int x, final int y, final String message, final int red, final int green,
            final int blue, String fontName, int size)
    {
        final BitmapFont bf = Media.getFont(fontName, size);
        return new Renderable()
        {
            @Override
            public void render(SpriteBatch sb, float elapsed)
            {
                bf.setColor(((float) red) / 256, ((float) green) / 256, ((float) blue) / 256, 1);
                bf.drawMultiLine(sb, message, x, y + bf.getMultiLineBounds(message).height);
            }
        };
    }

    static Renderable makeCenteredText(final String message, final int red, final int green, final int blue,
            String fontName, int size)
    {
        final BitmapFont bf = Media.getFont(fontName, size);
        final float x = LOL._game._config.getScreenWidth() / 2 - bf.getMultiLineBounds(message).width / 2;
        final float y = LOL._game._config.getScreenHeight() / 2 + bf.getMultiLineBounds(message).height / 2;
        return new Renderable()
        {
            @Override
            public void render(SpriteBatch sb, float elapsed)
            {
                bf.setColor(((float) red) / 256, ((float) green) / 256, ((float) blue) / 256, 1);
                bf.drawMultiLine(sb, message, x, y);
            }
        };
    }

    /**
     * A random number generator... students always seem to need this
     */
    private static Random _generator = new Random();

    /**
     * Generate a random number x such that 0 <= x < max
     * 
     * @param max
     *            The largest number returned will be one less than max
     * @return a random integer
     */
    public static int getRandom(int max)
    {
        return _generator.nextInt(max);
    }

    /**
     * Draw a box on the scene
     * 
     * Note: the box is actually four narrow rectangles
     * 
     * @param x0
     *            X coordinate of top left corner
     * @param y0
     *            Y coordinate of top left corner
     * @param x1
     *            X coordinate of bottom right corner
     * @param y1
     *            Y coordinate of bottom right corner
     * @param imgName
     *            name of the image file to use when drawing the rectangles
     * @param density
     *            Density of the obstacle. When in doubt, use 1
     * @param elasticity
     *            Elasticity of the obstacle. When in doubt, use 0
     * @param friction
     *            Friction of the obstacle. When in doubt, use 1
     */
    static public void drawBoundingBox(int x0, int y0, int x1, int y1, String imgName, float density, float elasticity,
            float friction)
    {
        // draw four rectangles and we're good
        Obstacle b = Obstacle.makeAsBox(x0 - 1, y0 - 1, Math.abs(x0 - x1) + 2, 1, imgName);
        b.setPhysics(density, elasticity, friction);

        Obstacle t = Obstacle.makeAsBox(x0 - 1, y1, Math.abs(x0 - x1) + 2, 1, imgName);
        t.setPhysics(density, elasticity, friction);

        Obstacle l = Obstacle.makeAsBox(x0 - 1, y0 - 1, 1, Math.abs(y0 - y1) + 2, imgName);
        l.setPhysics(density, elasticity, friction);

        Obstacle r = Obstacle.makeAsBox(x1, y0 - 1, 1, Math.abs(y0 - y1) + 2, imgName);
        r.setPhysics(density, elasticity, friction);
    }

    /**
     * Draw a picture on the current level
     * 
     * Note: the order in which this is called relative to other entities will
     * determine whether they go under or over
     * this picture.
     * 
     * @param x
     *            X coordinate of top left corner
     * @param y
     *            Y coordinate of top left corner
     * @param width
     *            Width of the picture
     * @param height
     *            Height of this picture
     * @param imgName
     *            Name of the picture to display
     * @param zIndex
     *            The z index of the image. There are 5 planes: -2, -2, 0, 1, and 2. By default, everything goes to
     *            plane 0
     */
    public static void drawPicture(final int x, final int y, final int width, final int height, final String imgName,
            int zIndex)
    {
        Level._currLevel.addSprite(Util.makePicture(x, y, width, height, imgName), zIndex);
    }
}