package de.muffinworks.knittingapp.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class PatternParserTest {

    @Test
    public void convertToGrid_2x2() {
        String in = "2h\n2y";
        String[][] expected = {
                {"h", "y"},
                {"h", "y"}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
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
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_2() {
        String in = "2h";
        String[][] expected = {
                {"h"},
                {"h"}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_02() {
        String in = "02h";
        String[][] expected = {
                {"h"},
                {"h"}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
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
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_2x2_empty() {
        String in = "2.\n2.";
        String[][] expected = {
                {".", "."},
                {".", "."}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_emptyString() {
        String in = "";
        String[][] expected = null;
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(expected == result);
    }

    @Test
    public void convertToGrid_null() {
        String in = null;
        String[][] expected = null;
        String[][] result = PatternParser.parseRowToGridFormat(in);
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
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_3x2() {
        String in = "2h\n2d\n2g";
        String[][] expected = {
                {"h", "d", "g"},
                {"h", "d", "g"}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToGrid_3x2_withForbiddenChars() {
        String in = "2h\n2d\n_+-,!@#$%^&*();/|<>\"':?= 2g";
        String[][] expected = {
                {"h", "d", "g"},
                {"h", "d", "g"}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
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
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void convertToString_3x2() {
        String[][] in = {
                {"h", "d", "g"},
                {"h", "d", "g"}
        };
        String expected = "2h\n2d\n2g";
        String result = PatternParser.parseGridToRowFormat(in);
        assertTrue(expected.equals(result));
    }

    @Test
    public void convertToString_3x2WithEmptySpacesInTheEnd() {
        String[][] in = {
                {"h", ".", "."},
                {"h", "d", "."}
        };
        String expected = "2h\n.d\n2.";
        String result = PatternParser.parseGridToRowFormat(in);
        assertTrue(expected.equals(result));
    }

    @Test
    public void convertToString_3x2WithEmptySpaces() {
        String[][] in = {
                {".", ".", "."},
                {".", ".", "."}
        };
        String expected = "2.\n2.\n2.";
        String result = PatternParser.parseGridToRowFormat(in);
        assertTrue(expected.equals(result));
    }

    @Test
    public void convertToString_1x1() {
        String[][] in = {
                {"."}
        };
        String expected = ".";
        String result = PatternParser.parseGridToRowFormat(in);
        assertTrue(expected.equals(result));
    }

    @Test
    public void rowsWithSameLengthsToPojo() {
        String in = "2n\n2g\n2h";
        ArrayList<String> expected = new ArrayList<>();
        expected.add("2n");
        expected.add("2g");
        expected.add("2h");
        String[] result = PatternParser.parseRowFormatToPojo(in);
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
        String[] result = PatternParser.parseRowFormatToPojo(in);
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
        String[] result = PatternParser.parseGridFormatToPojo(in);
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
        String[][] result = PatternParser.parsePojoToGridFormat(in);
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
        String result = PatternParser.parsePojoToRowFormat(in);
        assertTrue(result.equals(expected));
    }

    @Test
    public void rowToPojoWithTrailingNumber() {
        String in = "3r2";
        String[] expected = {
                "3r"
        };
        String[] result = PatternParser.parseRowFormatToPojo(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToGridWithTrailingNumber() {
        String in = "3r2";
        String[][] expected = {
                {"r"},
                {"r"},
                {"r"}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToGridEmptyFirstRow() {
        String in = "\n3h";
        String[] expected = {
                "3.",
                "3h"
        };
        String[] result = PatternParser.parseRowFormatToPojo(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToGridEmptyRowInMiddle() {
        String in = "4f\n\n3h";
        String[] expected = {
                "4f",
                "4.",
                "3h."
        };
        String[] result = PatternParser.parseRowFormatToPojo(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToGridEmptyRowAtEnd() {
        String in = "4f\n\n3h\n";
        String[][] expected = {
                {"f", ".", "h"},
                {"f", ".", "h"},
                {"f", ".", "h"},
                {"f", ".", "."}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToPojoEmptyRowAtEnd() {
        String in = "\n4f\n\n3h\n";
        String[] expected = {
                "4.",
                "4f",
                "4.",
                "3h."
        };
        String[] result = PatternParser.parseRowFormatToPojo(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToPojoEmptyRow() {
        String in = "\n";
        String[] expected = {
                "."
        };
        String[] result = PatternParser.parseRowFormatToPojo(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToGridEmptyRow() {
        String in = "\n";
        String[][] expected = {
                {"."}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToGridWidthEmptyRowInMiddle() {
        String in = "3h\n\n4h";
        String[][] expected = {
                {"h", ".", "h"},
                {"h", ".", "h"},
                {"h", ".", "h"},
                {".", ".", "h"}
        };
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(Arrays.deepEquals(result, expected));
    }

    @Test
    public void rowToPojoMoreThanMaxColumns() {
        String in = "55h3d";// + Constants.MAX_ROWS_AND_COLUMNS_LIMIT + "h";
        String[] expected = {
                "35h"
        };
        String[] result = PatternParser.parseRowFormatToPojo(in);
        assertTrue(Arrays.deepEquals(expected, result));
    }

    @Test
    public void rowToGridMoreThanMaxColumns() {
        String in = "55j5g";
        int expected = Constants.MAX_ROWS_AND_COLUMNS_LIMIT;
        String[][] result = PatternParser.parseRowToGridFormat(in);
        assertTrue(expected >= result.length);
    }
}
