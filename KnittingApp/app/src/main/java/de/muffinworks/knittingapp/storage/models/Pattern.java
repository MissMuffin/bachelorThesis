package de.muffinworks.knittingapp.storage.models;

import java.util.Arrays;
import java.util.Objects;

import de.muffinworks.knittingapp.util.Constants;
import de.muffinworks.knittingapp.util.PatternParser;

/**
 * Created by Bianca on 22.07.2016.
 */
public class Pattern extends Metadata {


    private String[] patternRows = {
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL,
            "10" + Constants.EMPTY_SYMBOL
    };
    private int rows = Constants.DEFAULT_ROWS;
    private int columns = Constants.DEFAULT_COLUMNS;
    private int currentRow = 1;


    public Pattern() {
        super();
    }

    public String[] getPatternRows() {
        return patternRows;
    }

    public void setPatternRows(String[] patternRows) {
        this.patternRows = patternRows;
        this.rows = patternRows.length;
        this.columns = PatternParser.parseRowToGridFormat(patternRows[0]).length;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern = (Pattern) o;
        return rows == pattern.rows &&
                columns == pattern.columns &&
                currentRow == pattern.currentRow &&
                Arrays.equals(patternRows, pattern.patternRows) &&
                name.equals(pattern.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patternRows, rows, columns, currentRow, name);
    }
}
