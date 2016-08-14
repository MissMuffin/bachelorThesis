package de.muffinworks.knittingapp.util;

import android.os.Environment;

public final class Constants {

    public static final String EMPTY_SYMBOL = "Z";

    public static String KNITTING_FONT_PATH = "fonts/OwnKnittingFont.ttf";

    public static String METADATA_FILENAME = "metadata.json";

    public static final String EXPORT_DIR = "Knitting Patterns";

    //gets path to external storage that is user accessible and won't be deleted after app install
    public static final String EXPORT_FOLDER_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/" + EXPORT_DIR;

    public static int FILE_PICKER_REQUEST_CODE = 2342;

    public static final String EXTRA_PATTERN_ID = "de.muffinworks.EXTRA_PATTERN_ID";
    public static final String EXTRA_PATTERN_DELETED = "de.muffinworks.EXTRA_PATTERN_DELETED";

    public static final int PERMISSION_REQUEST_WRITE_SD = 1337;

    public static final int REQUEST_CODE_EDITOR = 1;

    public static final int DEFAULT_ROWS = 10;
    public static final int DEFAULT_COLUMNS = 10;

    public static final int MAX_ROWS_AND_COLUMNS_LIMIT = 35;

    public static final String[] DEFAULT_PATTERN = {
            "10" + Constants.EMPTY_SYMBOL, "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL, "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL, "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL, "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL, "10" + Constants.EMPTY_SYMBOL
    };

    public static final String [] SYMBOL_DESCRIPTIONS = {
            "Rechte Masche", "Linke Masche", "Rechtsverschränkte Masche", "Linksverschränkte Masche",
            "Masche rechts abheben", "Masche links abheben", "2 rechts zusammen stricken",
            "2 links zusammen stricken", "Randmasche", "1 rechte Masche aufnehmen",
            "1 linke Masche aufnehmen", "Abbinden", "Rechts neigende Zunahme", "Links neigende Zunahme",
            "Umschlag", "Leerer Platzhalter"
    };

    public static final String [] SYMBOLS = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "Z"
    };
}
