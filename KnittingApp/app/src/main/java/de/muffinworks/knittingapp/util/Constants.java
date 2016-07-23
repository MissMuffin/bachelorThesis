package de.muffinworks.knittingapp.util;

import android.content.Context;

import java.io.File;

/**
 * Created by Bianca on 02.06.2016.
 */
public final class Constants {

    public static String KNITTING_FONT_PATH = "fonts/KSymbolsW.ttf";
    public static String KNITTING_FONT_PATH_TEST = "fonts/KnittingTest.ttf";

    public static String METADATA_FILENAME = "metadata.json";

    public static File getApplicationDirectory(Context context) {
        return context.getFilesDir();
    }

    public static final String [] KEY_DESCRIPTIONS = {
            "q desc",
            "w desc",
            "e desc",
            "r desc",
            "t desc",
            "y desc",
            "u desc",
            "i desc",
            "o desc",
            "p desc",
            "a desc",
            "s desc",
            "d desc",
            "f desc",
            "g desc",
            "h desc",
            "j desc",
            "k desc",
            "l desc",
            "z desc",
    };

    public static final String [] KEY_STRINGS = {
            "q",
            "w",
            "e",
            "r",
            "t",
            "y",
            "u",
            "i",
            "o",
            "p",
            "a",
            "s",
            "d",
            "f",
            "g",
            "h",
            "j",
            "k",
            "l",
            "z",
    };
}
