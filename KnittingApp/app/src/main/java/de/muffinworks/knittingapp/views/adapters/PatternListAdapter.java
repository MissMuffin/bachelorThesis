package de.muffinworks.knittingapp.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.RowEditorActivity;
import de.muffinworks.knittingapp.ViewerActivity;
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
        mService.init(mContext);
        Pattern test = new Pattern();
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
    public void notifyDataSetChanged() {
        mPatterns = mService.listMetadataEntries();
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PatternItemViewHolder viewHolder;
        View v = convertView;

        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.view_list_pattern_item, null);
            viewHolder = new PatternItemViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (PatternItemViewHolder) v.getTag();
        }

        viewHolder.mPatternName.setText(mPatterns[position].getName());
        viewHolder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patternId = ((Metadata)getItem(position)).getId();
                Intent intent = new Intent(mContext, RowEditorActivity.class);
                intent.putExtra(Constants.EXTRA_PATTERN_ID, patternId);
                mContext.startActivity(intent);
            }
        });
        viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO show dialog for delete confirmation
                mService.delete((Metadata)getItem(position));
                notifyDataSetChanged();
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patternId = ((Metadata)getItem(position)).getId();
                Intent intent = new Intent(mContext, ViewerActivity.class);
                intent.putExtra(Constants.EXTRA_PATTERN_ID, patternId);
                mContext.startActivity(intent);
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
