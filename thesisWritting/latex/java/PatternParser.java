package de.muffinworks.knittingapp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternParser {

    private static final String EMPTY = "";
    private static final String LINEFEED = "\n";
    private static final String DEFAULT_EMPTY_PATTERN_STRING =
            "10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL
            +"10"+Constants.EMPTY_SYMBOL;
    private static final String REGEX_ALL_NUMBER_CHARACTER_PAIRS = "([0-9]*)([a-oA-OzZ])";
    private static final String REGEX_ALL_FORBIDDEN_CHARS = "[_+-,!@#$%^&*();/|<>\"':?= .]+|\\\\(?!n|r)";
    private static final String REGEX_LOOKBEHIND_LINEFEED = "(?<=\n)";
    private static final String REGEX_ALL_NON_DIGITS_END = "\\D+";
    private static final String REGEX_ALL_DIGITS_END = "\\d+$";
    private static final String REGEX_ALL_DIGITS = "[^\\D]";

    static private Pattern pattern = Pattern.compile(REGEX_ALL_NUMBER_CHARACTER_PAIRS);

    public static String parseGridToRowFormat(String[][] input) {
        if (input == null || Arrays.deepEquals(input, new String[0][0])) return null;
        String result = "";
        for (int r = 0; r < input[0].length; r++) { //input[0] -> all rows have the same length

            String previousSymbol = input[0][r]; //init with first symbol in pattern
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
        //trim \n from end of string here
        String test = result.substring(result.length() - 1);
        if ("\n".equals(test)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String[][] parseRowToGridFormat(String input) {

        if (input == null || input.isEmpty()) return null;

        //expaned row of 3h -> hhh
        ArrayList<String> expandedRows = new ArrayList<>();

        //remove all characters that are not numeric or used for the symbols font
        //https://stackoverflow.com/questions/1761051/difference-between-n-and-r
        input = input.replaceAll(REGEX_ALL_FORBIDDEN_CHARS, EMPTY);

        //split at linefeed \n in to get rows
        String[] compressedRows = input.split(REGEX_LOOKBEHIND_LINEFEED);

        int columns = 0;

        for (int r = 0; r < compressedRows.length; r++) {
            ArrayList<String> groupedSymbols = new ArrayList<>(); //34g4gg111s -> 34g 4g g 111s

            String row = compressedRows[r].replaceAll("\\d+$", EMPTY);
            row = row.replace("\n", "");

            //check if row contained only \n and is now ""
            if (row.equals("" )) {
                row = Constants.EMPTY_SYMBOL;
            }

            Matcher m = pattern.matcher(row);
            while(m.find()) {
                String group = m.group(0); //group 0 is always entire match
                groupedSymbols.add(group);
            }

            //find out how many symbols are in the row to get number of columns
            int symbolCount = 0;
            String expandedRow = "";

            for (String group : groupedSymbols) {
                //remove all letters
                String symbolFactor = group.replaceAll(REGEX_ALL_NON_DIGITS_END, EMPTY);
                //remove all numbers
                String symbol = group.replaceAll(REGEX_ALL_DIGITS, EMPTY);

                if (!symbolFactor.isEmpty()) {
                    //was grouped symbol e.g. 33k
                    int factor = Integer.parseInt(symbolFactor);

                    if ((symbolCount + factor) > Constants.MAX_ROWS_AND_COLUMNS_LIMIT) {
                        factor = Constants.MAX_ROWS_AND_COLUMNS_LIMIT - symbolCount;
                    }

                    symbolCount += factor;
                    for (int j = 0; j < factor; j++) {
                        expandedRow += symbol;
                    }
                } else {
                    //only one symbol, not grouped e.g. k
                    symbolCount++;
                    expandedRow += symbol;
                }
            }
            if (symbolCount > columns) columns = symbolCount;
            expandedRows.add(expandedRow);
        }
        String[][] result = new String[columns][compressedRows.length];

        //itereate over String[][] and fill in from row strings
        for (int r = 0; r < compressedRows.length; r++) {
            for (int c = 0; c < expandedRows.get(r).length(); c++) {
                result[c][r] = Character.toString(expandedRows.get(r).charAt(c));
            }
        }
        //fill null places with placeholder for empty cell
        for (int c = 0; c < result.length; c++) {
            for (int r = 0; r < result[c].length; r++) {
                String symbol = result[c][r];
                if (symbol == null) {
                    result[c][r] = Constants.EMPTY_SYMBOL;
                }
            }
            String[] strings = result[c];
        }
        return result;
    }

    public static String parsePojoToRowFormat(String[] patternRows) {
        if (patternRows == null || patternRows.length == 0) return DEFAULT_EMPTY_PATTERN_STRING;
        String result = "";
        for (String row : patternRows) {
            result += row + "\n";
        }
        result = result.substring(0, result.length() - 1); //remove last \n
        return result;
    }

    public static String[] parseRowFormatToPojo(String rowInput) {
        //split after linefeed \n
        String[] rows = rowInput.split(REGEX_LOOKBEHIND_LINEFEED);
        int[] symbolsPerRows = new int[rows.length];
        int columns = 1;

        for (int r = 0; r < rows.length; r++) {
            ArrayList<MatchResult> groupedSymbols = new ArrayList<>();
            rows[r] = rows[r].replaceAll(REGEX_ALL_DIGITS_END, EMPTY);
            rows[r] = rows[r].replaceAll(LINEFEED, EMPTY);
            Matcher m = pattern.matcher(rows[r]);
            while(m.find()) {
                groupedSymbols.add(m.toMatchResult());
            }

            StringBuilder sb = new StringBuilder();

            int columnCount = 0;
            for (MatchResult group : groupedSymbols) {
                String symbolFactor = group.group(1);
                String symbol = group.group(2);
                if (columnCount >= Constants.MAX_ROWS_AND_COLUMNS_LIMIT)
                    break;

                if (!symbolFactor.isEmpty()) {
                    int factor = Integer.parseInt(symbolFactor);

                    if (factor + columnCount > Constants.MAX_ROWS_AND_COLUMNS_LIMIT) {
                        factor = Constants.MAX_ROWS_AND_COLUMNS_LIMIT - columnCount;
                    }

                    columnCount += factor;
                    symbolsPerRows[r] += factor;
                    sb.append(factor).append(symbol);
                } else {
                    columnCount++;
                    symbolsPerRows[r]++;
                    sb.append(symbol);
                }
            }
            rows[r] = sb.toString();
            if (columnCount > columns) columns = columnCount;
        }

        // Append trailing emtpy symbols to ensure the right amount of columns
        for (int r = 0; r < rows.length ; r++) {
            int diff = columns - symbolsPerRows[r];
            if (diff > 1) {
                rows[r] += diff + Constants.EMPTY_SYMBOL;
            } else if (diff == 1){
                rows[r] += Constants.EMPTY_SYMBOL;
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
            String[][] emptyDefaultPattern = new String[Constants.DEFAULT_COLUMNS][Constants.DEFAULT_ROWS];
            for (int c = 0; c < Constants.DEFAULT_COLUMNS; c++) {
                for (int r = 0; r < Constants.DEFAULT_ROWS; r++) {
                    emptyDefaultPattern[c][r] = Constants.EMPTY_SYMBOL;
                }
            }
            return emptyDefaultPattern;
        }
        String rowFormat = parsePojoToRowFormat(patternRows);
        return parseRowToGridFormat(rowFormat);
    }
}
