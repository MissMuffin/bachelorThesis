package de.muffinworks.knittingapp.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import static org.junit.Assert.*;

/**
 * Created by Bianca on 21.07.2016.
 */
public class KnittingParserTest {

    @Test
    public void convertToGrid_2x2() {
        String in = "2h\n2y";
        String[][] expected = {
                {"h", "y"},
                {"h", "y"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_2x3() {
        String in = "2h\n3y";
        String[][] expected = {
                {"h", "y"},
                {"h", "y"},
                {".", "y"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_2() {
        String in = "2h";
        String[][] expected = {
                {"h"},
                {"h"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_02() {
        String in = "02h";
        String[][] expected = {
                {"h"},
                {"h"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_2_1() {
        String in = "2hh";
        String[][] expected = {
                {"h"},
                {"h"},
                {"h"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_2x2_empty() {
        String in = "2.\n2.";
        String[][] expected = {
                {".", "."},
                {".", "."}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_emptyString() {
        String in = "";
        String[][] expected = null;
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(expected == result);
    }

    @Test
    public void convertToGrid_null() {
        String in = null;
        String[][] expected = null;
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(expected == result);
    }

    @Test
    public void convertToGrid_2_1_1x4() {
        String in = "2hgt\n4d";
        String[][] expected = {
                {"h", "d"},
                {"h", "d"},
                {"g", "d"},
                {"t", "d"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_3x2() {
        String in = "2h\n2d\n2g";
        String[][] expected = {
                {"h", "d", "g"},
                {"h", "d", "g"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_3x2_withForbiddenChars() {
        String in = "2h\n2d\n_+-,!@#$%^&*();/|<>\"':?= 2g";
        String[][] expected = {
                {"h", "d", "g"},
                {"h", "d", "g"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_2x3_test() {
        String in = "hdg\nhdg\n";
        String[][] expected = {
                {"h", "h"},
                {"d", "d"},
                {"g", "g"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToString_3x2() {
        String[][] in = {
                {"h", "d", "g"},
                {"h", "d", "g"}
        };
        String expected = "2h\n2d\n2g";
        String result = KnittingParser.parseGridToRowFormat(in);
        assertTrue(expected.equals(result));
    }

    @Test
    public void convertToString_3x2WithEmptySpacesInTheEnd() {
        String[][] in = {
                {"h", ".", "."},
                {"h", "d", "."}
        };
        String expected = "2h\n.d\n2.";
        String result = KnittingParser.parseGridToRowFormat(in);
        assertTrue(expected.equals(result));
    }

    @Test
    public void convertToString_3x2WithEmptySpaces() {
        String[][] in = {
                {".", ".", "."},
                {".", ".", "."}
        };
        String expected = "2.\n2.\n2.";
        String result = KnittingParser.parseGridToRowFormat(in);
        assertTrue(expected.equals(result));
    }

    @Test
    public void convertToString_1x1() {
        String[][] in = {
                {"."}
        };
        String expected = ".";
        String result = KnittingParser.parseGridToRowFormat(in);
        assertTrue(expected.equals(result));
    }

    @Test
    public void rowsWithSameLengthsToPojo() {
        String in = "2n\n2g\n2h";
        ArrayList<String> expected = new ArrayList<>();
        expected.add("2n");
        expected.add("2g");
        expected.add("2h");
        String[] result = KnittingParser.parseRowFormatToPojo(in);
        assertTrue(result.length == expected.size());
        for (int i = 0; i < result.length; i++) {
            if (!result[i].equals(expected.get(i))) fail();
        }
    }

    @Test
    public void rowsWithDifferentLengthsToPojo() {
        String in = "2n\n4g\n2h";
        ArrayList<String> expected = new ArrayList<>();
        expected.add("2n2.");
        expected.add("4g");
        expected.add("2h2.");
        String[] result = KnittingParser.parseRowFormatToPojo(in);
        assertTrue(result.length == expected.size());
        for (int i = 0; i < result.length; i++) {
            if (!result[i].equals(expected.get(i))) fail();
        }
    }

    @Test
    public void gridToPojo() {
        String[][] in = {
                {"h", "f", "."},
                {"h", "g", "."},
                {"h", "g", "j"}
        };
        ArrayList<String> expected = new ArrayList<>();
        expected.add("3h");
        expected.add("f2g");
        expected.add("2.j");
        String[] result = KnittingParser.parseGridFormatToPojo(in);
        assertTrue(result.length == expected.size());

        for (int i = 0; i < result.length; i++) {
            if (!result[i].equals(expected.get(i))) fail();
        }
    }

    @Test
    public void pojoToGrid() {
        String[][] expected = {
                {"h", "f", "."},
                {"h", "g", "."},
                {"h", "g", "j"}
        };
        String[] in = {
                "3h",
                "f2g",
                "2.j"
        };
        String[][] result = KnittingParser.parsePojoToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void pojoToRow() {
        String[] in = {
                "3h",
                "f2g",
                "2.j"
        };
        String expected = "3h\nf2g\n2.j";
        String result = KnittingParser.parsePojoToRowFormat(in);
        assertTrue(result.equals(expected));
    }

    @Test
    public void rowToPojoWithTrainlingNumber() {
        String in = "3r2";
        String[] expected = {
                "3r"
        };
        String[] result = KnittingParser.parseRowFormatToPojo(in);
        assertTrue(result.equals(expected));
    }

    @Test
    public void rowToGridWithTrainlingNumber() {
        String in = "3r2";
        String[][] expected = {
                {"r"},
                {"r"},
                {"r"}
        };
        String[][] result = KnittingParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }
}
