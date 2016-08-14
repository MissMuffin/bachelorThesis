package de.muffinworks.knittingapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import de.muffinworks.knittingapp.R;

public class PatternDeleteDialogFragment extends DialogFragment {

    private static final String BUNDLE_NAME = "name";

    private OnPatternDeleteInteractionListener mListener;
    private String mName = "";

    public PatternDeleteDialogFragment() {}

    public static PatternDeleteDialogFragment newInstance(String name) {
        PatternDeleteDialogFragment fragment = new PatternDeleteDialogFragment();
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
        return new android.app.AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_title_pattern_delete, mName))
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onConfirmDelete();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPatternDeleteInteractionListener) {
            mListener = (OnPatternDeleteInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + getString(R.string.error_must_implement_interface,
                    "OnPatternDeleteInteractionListener"));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPatternDeleteInteractionListener {
        void onConfirmDelete();
    }
}
