package de.muffinworks.knittingapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.layouts.KeyboardLayout;
import de.muffinworks.knittingapp.services.PatternStorageService;
import de.muffinworks.knittingapp.services.models.Pattern;
import de.muffinworks.knittingapp.views.GridEditorView;
import de.muffinworks.knittingapp.views.adapters.KeyboardGridAdapter;

/**
 * Created by Bianca on 25.07.2016.
 */
public class GridEditorFragment extends Fragment
        implements  KeyboardGridAdapter.GridEditorKeyListener,
                    GridSizeDialogFragment.OnGridSizeInteractionListener {

    private PatternStorageService mService;
    private Pattern mPattern;
    private GridEditorView mGridEditorView;
    private GridView mKeyboard;
    private KeyboardLayout mDeleteButtonContainer;
    private KeyboardGridAdapter mKeyboardAdapter;
    private boolean mIsDeleteActive = false;


    public static GridEditorFragment getInstance(String patternId) {
        GridEditorFragment fragment = new GridEditorFragment();
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
        mService = PatternStorageService.getInstance();
        mService.init(getActivity());
        if (getArguments() != null) {
            mPattern = mService.load(getArguments().getString("id"));
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
        mGridEditorView = (GridEditorView) view.findViewById(R.id.grid);
        if (mPattern != null){
            mGridEditorView.setPattern(mPattern.getPatternRows());
        }
        mDeleteButtonContainer = (KeyboardLayout) view.findViewById(R.id.grid_delete_button_container);
        mKeyboard = (GridView) view.findViewById(R.id.keyboard_gridview);
        mKeyboardAdapter = new KeyboardGridAdapter(getActivity(), this);
        mKeyboard.setAdapter(mKeyboardAdapter);
    }

    public void notifyDataChanged() {
        if (mPattern != null) {
            mPattern = mService.load(mPattern.getId());
            mGridEditorView.setPattern(mPattern.getPatternRows());
        }
    }

    public void savePattern() {
        String[] newPatternRows = mGridEditorView.getPattern();
        mPattern.setPatternRows(newPatternRows);
        mService.save(mPattern);
        Snackbar.make(getView(), "Speichern erfolgreich", Snackbar.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      keyboard stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onKeyToggled(String key) {
        setDeleteActive(false);
        mGridEditorView.setSelectedKey(key);
    }

    public void onDeleteToggled() {
        setDeleteActive(!mIsDeleteActive);
    }

    private void setDeleteActive(boolean active) {
        mIsDeleteActive = active;
        mKeyboardAdapter.setDeleteActive(mIsDeleteActive);
        mGridEditorView.setDeleteActive(mIsDeleteActive);
        if (mIsDeleteActive) {
            mDeleteButtonContainer.setBackgroundColor(getResources().getColor(R.color.red_500, null));
        } else {
            mDeleteButtonContainer.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      set size dialog stuff
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void showSetSizeDialog() {
        GridSizeDialogFragment dialog = GridSizeDialogFragment.newInstance(
                mGridEditorView.getColumns(),
                mGridEditorView.getRows());
        dialog.setTargetFragment(GridEditorFragment.this, 300);
        dialog.show(getFragmentManager(), "grid_size_dialog");
    }

    @Override
    public void onSetChartSize(int columns, int rows) {
        mGridEditorView.setChartSize(columns, rows);
    }
}
