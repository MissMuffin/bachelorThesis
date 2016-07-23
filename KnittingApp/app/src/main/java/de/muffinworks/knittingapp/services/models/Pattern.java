package de.muffinworks.knittingapp.services.models;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Bianca on 22.07.2016.
 */
public class Pattern extends Metadata {


    private String[] patternRows;
    private int rows;
    private int columns;
    private int currentRow;


    public Pattern() {
        super();
    }

    public String[] getPatternRows() {
        return patternRows;
    }

    public void setPatternRows(String[] patternRows) {
        this.patternRows = patternRows;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
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
