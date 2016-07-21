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

    static private Pattern pattern = Pattern.compile("[0-9]+([a-z]|[A-Z])|[a-z]|[A-Z]");

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

    /**
     * ONE SYMBOL == ONE ALPHABETIC CHARACTER
     */

    public static String parseToRowFormat(String[][] input) {
        String result = "";

        for (int c = 0; c < input.length; c++) {

            String symbol = "";
            int count = 1;

            for (int r = 0; r < input[c].length; r++) {

                String s = input[c][r];
                //if last symbol of row is reached and there are empty spaces (== .) then ignore
                if (s.equals(symbol)) {

                    count++;
                } else {

                    //append count and symbol
                    //save new symbol
                    //reset count
                    result = count == 1 ? result + symbol : result + count + symbol;
                    symbol = s;
                    count = 1;
                }

                if (r == input[c].length - 1) {
                    if (!s.equals(".")) {
                        result = count == 1 ? result + symbol : result + count + symbol;
                    }
                }
            }

            //end of row
            result += "\n";
        }

        return result;
    }

    public static String[][] parseToGridFormat(String input) {
        //fully expaned rows
        ArrayList<String> expandedRows = new ArrayList<>();

        // TODO: 21.07.2016 remove all characters that are not numeric or used for the symbols font
        //https://stackoverflow.com/questions/1761051/difference-between-n-and-r
        input = input.replaceAll("[_+-.,!@#$%^&*();/|<>\"':?= ]+|\\\\(?!n|r)", "");

        //split at \n in = rows
        String[] compressedRows = input.split("\n");

        //get longest row string = columns and fill test with symbol groups from each row
        int columns = 0;

        for (int i = 0; i < compressedRows.length; i++) {
            ArrayList<String> groupedSymbols = new ArrayList<>(); //34g 4g g 111s
            Matcher m = pattern.matcher(compressedRows[i]);
            while(m.find()) {
                String group = m.group(0); //group 0 is alwazs entire match
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
                // TODO: 21.07.2016 fill null places with symbol for empty cell
            }
        }
        return result;
    }
}
