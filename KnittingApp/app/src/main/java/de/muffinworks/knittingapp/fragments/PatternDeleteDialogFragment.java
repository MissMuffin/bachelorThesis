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
import de.muffinworks.knittingapp.services.PatternStorageService;

/**
 * Created by Bianca on 25.07.2016.
 */
public class PatternDeleteDialogFragment extends DialogFragment {

    private OnPatternDeleteInteractionListener mListener;
    private String mName = "";


    public PatternDeleteDialogFragment() {}

    public static PatternDeleteDialogFragment newInstance(String name) {
        PatternDeleteDialogFragment fragment = new PatternDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString("name");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new android.app.AlertDialog.Builder(getActivity())
                .setTitle("Strickmuster " + mName + " wirklich loeschen?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onConfirmDelete();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
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
                    + " must implement OnPatternDeleteInteractionListener");
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
