package de.muffinworks.knittingapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.util.Constants;

public class GridSizeDialogFragment extends DialogFragment {

    private static final String BUNDLE_COLUMNS = "columns";
    private static final String BUNDLE_ROWS = "rows";

    private int mColumns = 0;
    private int mRows = 0;
    private OnGridSizeInteractionListener mListener;

    public GridSizeDialogFragment() {}

    public static GridSizeDialogFragment newInstance(int columns, int rows) {
        GridSizeDialogFragment fragment = new GridSizeDialogFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_COLUMNS, columns);
        args.putInt(BUNDLE_ROWS, rows);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumns = getArguments().getInt(BUNDLE_COLUMNS);
            mRows = getArguments().getInt(BUNDLE_ROWS);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //http://stackoverflow.com/a/15619098/4738174
        final AlertDialog dialog = (AlertDialog)getDialog();
        if (dialog != null) {
            (dialog.getButton(DialogInterface.BUTTON_POSITIVE)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newColumns = Integer.parseInt(mColumnsEdittext.getText().toString());
                    int newRows = Integer.parseInt(mRowsEdittext.getText().toString());
                    if (newColumns != mColumns || newRows != mRows) {
                        onChartSizeSetResult(
                                Integer.parseInt(mColumnsEdittext.getText().toString()),
                                Integer.parseInt(mRowsEdittext.getText().toString())
                        );
                    }
                    dismiss();
                }
            });
        }
    }

    private EditText mColumnsEdittext;
    private EditText mRowsEdittext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LinearLayout content = (LinearLayout) getActivity().getLayoutInflater().
                inflate(R.layout.dialog_set_grid_size, null);
        mColumnsEdittext = (EditText) content.findViewById(R.id.edittext_columns);
        mColumnsEdittext.setText(Integer.toString(mColumns));
        mColumnsEdittext.addTextChangedListener(new DimensionTextWatcher(mColumnsEdittext, mColumns));
        mColumnsEdittext.setSelection(mColumnsEdittext.length());

        mRowsEdittext = (EditText) content.findViewById(R.id.edittext_rows);
        mRowsEdittext.setText(Integer.toString(mRows));
        mRowsEdittext.addTextChangedListener(new DimensionTextWatcher(mRowsEdittext, mRows));
        mRowsEdittext.setSelection(mRowsEdittext.length());

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton(
                        R.string.dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //http://stackoverflow.com/a/15619098/4738174
                                //Do nothing here: button overriden in onStart()
                                //Empty onclick needed on older versions to instantiate button
                            }
                })
                .setNegativeButton(
                        R.string.dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                })
                .setTitle(getResources().getString(R.string.dialog_title_grid_size))
                .setView(content)
                .create();
        return dialog;
    }

    private void onChartSizeSetResult(int columns, int rows) {
        mListener.onSetChartSize(columns, rows);
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGridSizeInteractionListener) {
            mListener = (OnGridSizeInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.error_must_implement_interface,
                    "OnGridSizeInteractionListener"));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnGridSizeInteractionListener {
        void onSetChartSize(int columns, int rows);
    }

    class DimensionTextWatcher implements TextWatcher {
        private int oldValue;
        private EditText editText;

        public DimensionTextWatcher(EditText editText, int oldValue) {
            this.editText = editText;
            this.oldValue = oldValue;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                //no input
                ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                return;
            } else if (Integer.parseInt(s.toString()) == 0) {
                //input is 0
                editText.setError(getString(R.string.error_dimension_zero));
                editText.setSelection(editText.length());
                ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            } else if (Integer.parseInt(s.toString()) > Constants.MAX_ROWS_AND_COLUMNS_LIMIT) {
                //input > max dimens
                editText.setError(
                        getString(R.string.error_over_max_size, Constants.MAX_ROWS_AND_COLUMNS_LIMIT));
                editText.setSelection(editText.length());
                ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            } else {
                ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        }
    }
}
