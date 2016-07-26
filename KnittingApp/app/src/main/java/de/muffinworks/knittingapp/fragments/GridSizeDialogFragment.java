package de.muffinworks.knittingapp.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.util.Constants;

public class GridSizeDialogFragment extends DialogFragment {

    private int mColumns = 0;
    private int mRows = 0;


    public GridSizeDialogFragment() {}

    public static GridSizeDialogFragment newInstance(int columns, int rows) {
        GridSizeDialogFragment fragment = new GridSizeDialogFragment();
        Bundle args = new Bundle();
        args.putInt("columns", columns);
        args.putInt("rows", rows);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumns = getArguments().getInt("columns");
            mRows = getArguments().getInt("rows");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout content = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dialog_set_grid_size, null);
        final EditText columns = (EditText) content.findViewById(R.id.edittext_columns);
        columns.setText(Integer.toString(mColumns));
        columns.addTextChangedListener(new DimensionTextWatcher(columns));

        final EditText rows = (EditText) content.findViewById(R.id.edittext_rows);
        rows.setText(Integer.toString(mRows));
        rows.addTextChangedListener(new DimensionTextWatcher(rows));

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        int newColumns = Integer.parseInt(columns.getText().toString());
                        int newRows = Integer.parseInt(rows.getText().toString());

                        if (newColumns != mColumns || newRows != mRows) {
                            onChartSizeSetResult(newColumns, newRows);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setTitle("Gittergröße ändern")
                .setView(content)
                .create();

        return dialog;
    }

    private void onChartSizeSetResult(int columns, int rows) {
        OnGridSizeInteractionListener listener = (OnGridSizeInteractionListener)getTargetFragment();
        listener.onSetChartSize(columns, rows);
        dismiss();
    }

    public interface OnGridSizeInteractionListener {
        void onSetChartSize(int columns, int rows);
    }

    class DimensionTextWatcher implements TextWatcher {

        private EditText mEditText;

        public DimensionTextWatcher(EditText editText) {
            mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                mEditText.setText("1");
                return;
            }
            int input = Integer.parseInt(s.toString());
            if (input > Constants.MAX_ROWS_AND_COLUMNS_LIMIT) {
                mEditText.setError("Nur 200 unterstuetzt");
                mEditText.setText(Constants.MAX_ROWS_AND_COLUMNS_LIMIT + "");
            } else if (input == 0) {
                mEditText.setText("1");
            }
        }
    }
}
