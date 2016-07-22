package de.muffinworks.knittingapp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Bianca on 21.07.2016.
 */
public class KnittingParser {

    static private Pattern pattern = Pattern.compile("([0-9]*([a-zA-Z.]))"); //shortned regex, test if works!!!
//    static private Pattern pattern = Pattern.compile("[0-9]+([a-z]|[A-Z])|[a-z]|[A-Z]");

    static String rowTestInput = "4k\n4f;:";
    static String rowTestInputDoubleSymbol = "3tt";
    static String rowTestInputSingleSymbol = "4gh";
    static String rowTestInputfromGrid = "qwe\nas\nz\n";

    static String[] row1 = {"q", "w" , "e"};
    static String[] row2 = {"a", "s" , "."};
    static String[] row3 = {"z", "." , "."};

    static String[][] gridTestInput = {row1, row2, row3};


    public static void main(String[] args) {
//		String test1 = parseToRowFormat(gridTestInput);
        String[][] test2 = parseToGridFormat(rowTestInput);
    }

    public static String parseToRowFormat(String[][] input) {
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

    public static String[][] parseToGridFormat(String input) {

        if (input == null || input.isEmpty()) return null;

        //fully expaned rows 3h -> hhh
        ArrayList<String> expandedRows = new ArrayList<>();

        // TODO: 21.07.2016 remove all characters that are not numeric or used for the symbols font
        //https://stackoverflow.com/questions/1761051/difference-between-n-and-r
        input = input.replaceAll("[_+-,!@#$%^&*();/|<>\"':?= ]+|\\\\(?!n|r)", "");

        //split at \n in = rows
        String[] compressedRows = input.split("\n");

        //get longest row string = columns and fill test with symbol groups from each row
        int columns = 0;

        for (int i = 0; i < compressedRows.length; i++) {
            ArrayList<String> groupedSymbols = new ArrayList<>(); //34g4gg111s -> 34g 4g g 111s
            Matcher m = pattern.matcher(compressedRows[i]);
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
}
