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

package com.me.mylolgame;

import edu.lehigh.cse.lol.SplashConfiguration;

/**
 * The game starts with a splash screen, which has buttons for "PLAY", "HELP",
 * and "QUIT". This file provides configuration information for the splash
 * screen.
 */
public class SplashConfig implements SplashConfiguration {
    /**
     * The name of the image to display as the splash screen. Be sure to
     * register this image in your main java file.
     */
    @Override
    public String getBackgroundImage() {
        return "splash.png";
    }

    /**
     * The name of the music to play while the splash screen is showing. Be sure
     * to register this file in your main java file.
     */
    @Override
    public String getMusic() {
        return "tune.ogg";
    }

    /**
     * The X coordinate of the bottom left corner of the "PLAY" button. If
     * you're not sure, click on the screen, and your 'console' in Eclipse
     * should tell you the coordinate where you clicked.
     */
    @Override
    public int getPlayX() {
        return 192;
    }

    /**
     * The Y coordinate of the bottom left corner of the "PLAY" button.
     */
    @Override
    public int getPlayY() {
        return 91;
    }

    /**
     * The width of the "PLAY" button
     */
    @Override
    public int getPlayWidth() {
        return 93;
    }

    /**
     * The height of the "PLAY" button
     */
    @Override
    public int getPlayHeight() {
        return 52;
    }

    /**
     * The X coordinate of the bottom left corner of the "HELP" button.
     */
    @Override
    public int getHelpX() {
        return 48;
    }

    /**
     * The Y coordinate of the bottom left corner of the "HELP" button.
     */
    @Override
    public int getHelpY() {
        return 93;
    }

    /**
     * The width of the "HELP" button
     */
    @Override
    public int getHelpWidth() {
        return 80;
    }

    /**
     * The height of the "HELP" button
     */
    @Override
    public int getHelpHeight() {
        return 40;
    }

    /**
     * The X coordinate of the bottom left corner of the "QUIT" button.
     */
    @Override
    public int getQuitX() {
        return 363;
    }

    /**
     * The Y coordinate of the bottom left corner of the "QUIT" button.
     */
    @Override
    public int getQuitY() {
        return 93;
    }

    /**
     * The width of the "QUIT" button
     */
    @Override
    public int getQuitWidth() {
        return 69;
    }

    /**
     * The height of the "QUIT" button
     */
    @Override
    public int getQuitHeight() {
        return 39;
    }
}
