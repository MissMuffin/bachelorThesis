package de.muffinworks.knittingapp.util;

import android.content.Context;

import java.io.File;

/**
 * Created by Bianca on 02.06.2016.
 */
public final class Constants {

    public static final String EMPTY_SYMBOL = "Z";

    public static String KNITTING_FONT_PATH = "fonts/OwnKnittingFont.ttf";
    public static String KNITTING_FONT_PATH_XENAKI = "fonts/KSymbolsWEmptyDot.ttf";

    public static String METADATA_FILENAME = "metadata.json";

    public static final String EXTRA_PATTERN_ID = "de.muffinworks.EXTRA_PATTERN_ID";

    public static final int REQUEST_CODE_EDITOR = 1;

    public static final int DEFAULT_ROWS = 10;
    public static final int DEFAULT_COLUMNS = 10;

    public static final int MAX_ROWS_AND_COLUMNS_LIMIT = 35;

    public static final String [] KEY_DESCRIPTIONS = {
            "Rechte Masche",
            "Linke Masche",
            "Rechtsverschränkte Masche",
            "Linksverschränkte Masche",
            "Masche rechts abheben",
            "Masche links abheben",
            "2 rechts zusammen stricken",
            "2 links zusammen stricken",
            "Randmasche",
            "1 rechte Masche aufnehmen",
            "1 linke Masche aufnehmen",
            "Abbinden",
            "Rechts neigende Zunahme",
            "Links neigende Zunahme",
            "Umschlag",
            "Leerer Platzhalter"
    };

    public static final String [] KEY_STRINGS = {
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "Z"
    };
}
