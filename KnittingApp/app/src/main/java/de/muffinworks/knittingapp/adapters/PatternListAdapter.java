package de.muffinworks.knittingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.muffinworks.knittingapp.PatternListActivity;
import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.services.PatternStorageService;
import de.muffinworks.knittingapp.services.models.Metadata;
import de.muffinworks.knittingapp.services.models.Pattern;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 23.07.2016.
 */
public class PatternListAdapter extends BaseAdapter {


    private Context mContext;
    private Metadata[] mPatterns;
    private PatternStorageService mService = PatternStorageService.getInstance();


    public PatternListAdapter(Context context) {
        mContext = context;
        try {
            mService.init(mContext);
        } catch (IOException e) {
            // TODO: 23.07.2016 handle exception
            e.printStackTrace();
        }
        mPatterns = mService.listMetadataEntries();
    }

    @Override
    public int getCount() {
        return mPatterns.length;
    }

    @Override
    public Object getItem(int position) {
        return mPatterns[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PatternItemViewHolder viewHolder;
        View v = convertView;

        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.list_pattern_item, null);
            viewHolder = new PatternItemViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (PatternItemViewHolder) v.getTag();
        }

        viewHolder.mPatternName.setText(mPatterns[position].getName());
        viewHolder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 23.07.2016 open editor with selected pattern
            }
        });
        viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO show dialog for delete confirmation
            }
        });

        return v;
    }

    class PatternItemViewHolder {
        public TextView mPatternName;
        public ImageButton mEditButton;
        public ImageButton mDeleteButton;

        public PatternItemViewHolder(View root) {
            mPatternName = (TextView) root.findViewById(R.id.pattern_name);
            mEditButton = (ImageButton) root.findViewById(R.id.button_edit);
            mDeleteButton = (ImageButton) root.findViewById(R.id.button_delete);
        }
    }
}
