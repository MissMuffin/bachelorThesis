package de.muffinworks.knittingapp.util;

import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bianca on 21.07.2016.
 */
public class KnittingParser {

    static private Pattern pattern = Pattern.compile("([0-9]*([a-zA-Z.]))");

    public static String parseGridToRowFormat(String[][] input) {
        if (input == null || Arrays.deepEquals(input, new String[0][0])) return null;
        String result = "";
        for (int r = 0; r < input[0].length; r++) { //all rows have the same length

            String previousSymbol = input[0][r];
            String currentSymbol = "";
            int count = 0;

            for (int c = 0; c < input.length; c++) {
                currentSymbol = input[c][r];
                if (currentSymbol.equals(previousSymbol)) {
                    count++;
                } else {
                    //append count and previousSymbol
                    //save new previousSymbol
                    //reset count
                    result += count == 1 ? previousSymbol : count + previousSymbol;
                    previousSymbol = currentSymbol;
                    count = 1;
                }
            }
            result += count == 1 ? previousSymbol : count + previousSymbol;
            if (r != input[0].length - 1) {
                result += "\n";
            }
        }
        //trim \n from end of string of there
        String test = result.substring(result.length() - 1);
        if ("\n".equals(test)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String[][] parseRowToGridFormat(String input) {

        if (input == null || input.isEmpty()) return null;

        //fully expaned rows 3h -> hhh
        ArrayList<String> expandedRows = new ArrayList<>();

        // TODO: 21.07.2016 remove all characters that are not numeric or used for the symbols font
        //https://stackoverflow.com/questions/1761051/difference-between-n-and-r
        input = input.replaceAll("[_+-,!@#$%^&*();/|<>\"':?= ]+|\\\\(?!n|r)", "");

        //split at \n in = rows
        String[] compressedRows = input.split("(?<=\n)");

        //get longest row string = columns and fill test with symbol groups from each row
        int columns = 0;

        for (int r = 0; r < compressedRows.length; r++) {
            ArrayList<String> groupedSymbols = new ArrayList<>(); //34g4gg111s -> 34g 4g g 111s

            String row = compressedRows[r].replaceAll("\\d+$", "");
            row = row.replace("\n", "");

            //check for row that contained only \n and is now ""
            if (row.equals("" )) {
                row = ".";
            }

            Matcher m = pattern.matcher(row);
            while(m.find()) {
                String group = m.group(0); //group 0 is always entire match
                groupedSymbols.add(group);
            }

            //find out how many symbols are in the row
            int symbolCount = 0;
            String expandedRow = "";

            for (String group : groupedSymbols) {

                String symbolFactor = group.replaceAll("\\D+", ""); //remove all letters 33
                String symbol = group.replaceAll("[^\\D]", ""); //remove all numbers k


                if (!symbolFactor.isEmpty()) {

                    //was grouped symbols: 33k
                    int factor = Integer.parseInt(symbolFactor);
                    symbolCount += factor;
                    for (int j = 0; j < factor; j++) {
                        expandedRow += symbol;
                    }

                } else {

                    //only one symbol, not grouped: k
                    symbolCount++;
                    expandedRow += symbol;
                }
            }
            if (symbolCount > columns) columns = symbolCount;
            expandedRows.add(expandedRow);
        }

        //create String[][]
        String[][] result = new String[columns][compressedRows.length];

        //itereate over String[][] and fill in from row strings
        for (int r = 0; r < compressedRows.length; r++) {
            for (int c = 0; c < expandedRows.get(r).length(); c++) {
                result[c][r] = Character.toString(expandedRows.get(r).charAt(c));
            }
        }
        //fill null places with . as placeholder for empty cell
        for (int c = 0; c < result.length; c++) {
            for (int r = 0; r < result[c].length; r++) {
                String symbol = result[c][r];
                if (symbol == null) {
                    result[c][r] = ".";
                }

            }
            String[] strings = result[c];
        }
        return result;
    }

    public static String parsePojoToRowFormat(String[] patternRows) {

        if (patternRows == null || patternRows.length == 0) return "10.\n10.\n10.\n10.\n10.\n10.\n10.\n10.\n10.\n10.";

        String result = "";
        for (String row : patternRows) {
            result += row + "\n";
        }
        result = result.substring(0, result.length() - 1); //remove last \n
        return result;
    }

    public static String[] parseRowFormatToPojo(String rowInput) {
        String[] rows = rowInput.split("(?<=\n)");
        int[] symbolsPerRows = new int[rows.length];
        int columns = 1;

        for (int r = 0; r < rows.length; r++) {
            ArrayList<String> groupedSymbols = new ArrayList<>();
            rows[r] = rows[r].replaceAll("\\d+$", "");
            rows[r] = rows[r].replaceAll("\n", "");
            Matcher m = pattern.matcher(rows[r]);
            while(m.find()) {
                String group = m.group(0);
                groupedSymbols.add(group);
            }
            int columnCount = 0;
            for (String group : groupedSymbols) {
                String symbolFactor = group.replaceAll("\\D+", "");
                if (!symbolFactor.isEmpty()) {
                    int factor = Integer.parseInt(symbolFactor);
                    columnCount += factor;
                    symbolsPerRows[r] += factor;
                } else {
                    columnCount++;
                    symbolsPerRows[r]++;
                }
            }

            if (columnCount > columns) columns = columnCount;
        }

        for (int r = 0; r < rows.length ; r++) {
            int diff = columns - symbolsPerRows[r];
            if (diff > 1) {
                rows[r] += diff + ".";
            } else if (diff == 1){
                rows[r] += ".";
            }
        }
        return rows;
    }

    public static String[] parseGridFormatToPojo(String[][] gridInput) {
        String rowFormat = parseGridToRowFormat(gridInput);
        return parseRowFormatToPojo(rowFormat);
    }

    public static String[][] parsePojoToGridFormat(String[] patternRows) {
        if (patternRows == null || patternRows.length == 0) {
            String[][] emptyDefaultPattern = new String[Constants.DEFAULT_COLUMNS_SIZE][Constants.DEFAULT_ROWS_SIZE];
            for (int c = 0; c < Constants.DEFAULT_COLUMNS_SIZE; c++) {
                for (int r = 0; r < Constants.DEFAULT_ROWS_SIZE; r++) {
                    emptyDefaultPattern[c][r] = ".";
                }
            }
            return emptyDefaultPattern;
        }
        String rowFormat = parsePojoToRowFormat(patternRows);
        return parseRowToGridFormat(rowFormat);
    }
}
