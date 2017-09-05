/*
 * Copyright <2017> <ANDREW LAM>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.utilities;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Andrew Chi Heng Lam on 8/19/2017.
 * <p>
 * This utility class contain methods to calculate the proper span size per device dp and orientation
 */

public final class LayoutManagerUtils {
    public static int getSpanSize(Context context) {
        // Set the desired thumbnail width (in dp)
        final int THUMBNAIL_WIDTH_DP = 135;

        // Get the current screen's width
        Configuration config = context.getResources().getConfiguration();
        int currentWidth = config.screenWidthDp;

        // Log width
        // Log.i("LayoutManagerUtil: ", "Device's currentWidth is: " + currentWidth);

        // Divide the currentWidth by the thumbnail dp width, to get an int number of movie entry that would fit;
        return currentWidth / THUMBNAIL_WIDTH_DP;
    }
}
