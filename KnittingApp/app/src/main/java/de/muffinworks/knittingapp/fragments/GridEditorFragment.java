package de.muffinworks.knittingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.Arrays;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.storage.PatternStorage;
import de.muffinworks.knittingapp.storage.models.Pattern;
import de.muffinworks.knittingapp.views.PatternGridView;
import de.muffinworks.knittingapp.views.adapters.KeyboardToggleAdapter;

/**
 * Created by Bianca on 25.07.2016.
 */
public class GridEditorFragment extends Fragment
        implements  KeyboardToggleAdapter.GridEditorKeyListener {

    private static final String BUNDLE_ID = "id";

    private PatternStorage mStorage;
    private Pattern mPattern;
    private PatternGridView mPatternGridView;
    private GridView mKeyboard;
    private LinearLayout mDeleteButtonContainer;
    private KeyboardToggleAdapter mKeyboardAdapter;
    private boolean mIsDeleteActive = false;


    public static GridEditorFragment getInstance(String patternId) {
        GridEditorFragment fragment = new GridEditorFragment();
        if (patternId != null) {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_ID, patternId);
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
            mPattern = mStorage.load(getArguments().getString(BUNDLE_ID));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editor_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPatternGridView = (PatternGridView) view.findViewById(R.id.grid);
        if (mPattern != null){
            mPatternGridView.setPattern(mPattern.getPatternRows());
        }
        mDeleteButtonContainer = (LinearLayout) view.findViewById(R.id.grid_delete_button_container);
        mKeyboard = (GridView) view.findViewById(R.id.keyboard_gridview);
        mKeyboardAdapter = new KeyboardToggleAdapter(getActivity(), this);
        mKeyboard.setAdapter(mKeyboardAdapter);
    }

    public void notifyDataChanged() {
        if (mPattern != null) {
            mPattern = mStorage.load(mPattern.getId());
            mPatternGridView.setPattern(mPattern.getPatternRows());
        }
    }

    public void savePattern() {
        String[] newPatternRows = mPatternGridView.getPattern();
        mPattern.setPatternRows(newPatternRows);
        mStorage.save(mPattern);
        Snackbar.make(
                getView(),
                R.string.pattern_saved,
                Snackbar.LENGTH_SHORT).show();
    }

    public boolean hasPatternChanged() {
        return !Arrays.deepEquals(mPatternGridView.getPattern(),mPattern.getPatternRows());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      keyboard stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onKeyToggled(String key) {
        setDeleteActive(false);
        mPatternGridView.setSelectedKey(key);
    }

    public void onDeleteToggled() {
        setDeleteActive(!mIsDeleteActive);
    }

    private void setDeleteActive(boolean active) {
        mIsDeleteActive = active;
        mKeyboardAdapter.setDeleteActive(mIsDeleteActive);
        mPatternGridView.setDeleteActive(mIsDeleteActive);
        if (mIsDeleteActive) {
            mDeleteButtonContainer.setBackgroundColor(getResources().getColor(R.color.red_500, null));
        } else {
            mDeleteButtonContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        }
    }

    public void setGridSize(int columns, int rows) {
        mPatternGridView.setGridSize(columns, rows);
    }
}
