package de.muffinworks.knittingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.Arrays;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.layouts.RowEditorLinearLayout;
import de.muffinworks.knittingapp.storage.PatternStorage;
import de.muffinworks.knittingapp.storage.models.Pattern;
import de.muffinworks.knittingapp.views.adapters.KeyboardTypingAdapter;

public class RowEditorFragment extends Fragment implements KeyboardTypingAdapter.RowEditorKeyListener {

    private static final String TAG = "RowEditorFragment";

    public RowEditorLinearLayout mRowEditorView;
    private Pattern mPattern;
    private PatternStorage mStorage;

    public static RowEditorFragment getInstance(String patternId) {
        RowEditorFragment fragment = new RowEditorFragment();
        if (patternId != null) {
            Bundle bundle = new Bundle();
            bundle.putString("id", patternId);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorage = PatternStorage.getInstance();
        mStorage.init(getActivity());
        if (getArguments() != null) {
            mPattern = mStorage.load(getArguments().getString("id"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor_row, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRowEditorView = (RowEditorLinearLayout) view.findViewById(R.id.row_editor_container);
        if (mPattern != null) {
            mRowEditorView.setPattern(mPattern.getPatternRows());
        }
        GridView mKeyboard = (GridView) view.findViewById(R.id.keyboard_gridview);
        mKeyboard.setAdapter(new KeyboardTypingAdapter(getActivity(), this));

        view.findViewById(R.id.op_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEnter();
            }
        });

        view.findViewById(R.id.op_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelete();
            }
        });
    }

    public void savePattern() {
        mPattern.setPatternRows(mRowEditorView.getPattern());
        mStorage.save(mPattern);
        Snackbar.make(getView(), getString(R.string.success_save_pattern), Snackbar.LENGTH_SHORT).show();
    }

    public boolean hasPatternChanged() {
        return !Arrays.deepEquals(mRowEditorView.getPattern(),mPattern.getPatternRows());
    }

    /**
     * emulates the backspace key on the system's soft keyboard
     */
    public void onDelete() {
        mRowEditorView.onDeletePressed();
    }

    /**
     * emulates the enter key on the system's soft keyboard
     */
    public void onEnter() {
        mRowEditorView.onEnterPressed();
    }

    public void onNumPadClick(String number) {
        int start = mRowEditorView.getEditText().getSelectionStart();
        mRowEditorView.getEditText().getText().insert(start, number);
    }

    public void notifyDataChanged() {
        mPattern = mStorage.load(mPattern.getId());
        mRowEditorView.setPattern(mPattern.getPatternRows());
    }

    @Override
    public void onKeyClicked(String key) {
        int start = mRowEditorView.getEditText().getSelectionStart();
        mRowEditorView.getEditText().getText().insert(start, key.toUpperCase());
    }
}
