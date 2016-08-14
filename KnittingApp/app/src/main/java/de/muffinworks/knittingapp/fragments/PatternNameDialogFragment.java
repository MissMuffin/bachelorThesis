package de.muffinworks.knittingapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;

import de.muffinworks.knittingapp.R;

public class PatternNameDialogFragment extends DialogFragment {

    private static final String BUNDLE_NAME = "name";

    private OnPatternNameInteractionListener mListener;
    private String mName = "";

    public PatternNameDialogFragment() {}

    public static PatternNameDialogFragment newInstance(String name) {
        PatternNameDialogFragment fragment = new PatternNameDialogFragment();
        Bundle args = new Bundle();
        args.putString(BUNDLE_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(BUNDLE_NAME);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LinearLayout parent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.view_pattern_name_input, null);
        final EditText input = (EditText) parent.findViewById(R.id.input);
        input.setText(mName);
        input.setSelection(input.length());
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSetName(input.getText().toString());
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                })
                .setTitle(getString(R.string.dialog_title_pattern_name))
                .setView(parent)
                .create();
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPatternNameInteractionListener) {
            mListener = (OnPatternNameInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.error_must_implement_interface,
                    "OnPatternNameInteractionListener"));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPatternNameInteractionListener {
        void onSetName(String name);
    }
}
