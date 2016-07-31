package de.muffinworks.knittingapp.views.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import de.muffinworks.knittingapp.EditorActivity;
import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.ViewerActivity;
import de.muffinworks.knittingapp.storage.PatternStorage;
import de.muffinworks.knittingapp.storage.models.Metadata;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 23.07.2016.
 */
public class PatternListAdapter extends BaseAdapter {


    private Context mContext;
    private Metadata[] mPatterns;
    private PatternStorage mStorage = PatternStorage.getInstance();
    private LayoutInflater mInflater;


    public PatternListAdapter(Context context) {
        mContext = context;
        mStorage.init(mContext);
        mPatterns = mStorage.listMetadataEntries();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        mPatterns = mStorage.listMetadataEntries();
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PatternItemViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.view_item_list_pattern, null);
            viewHolder = new PatternItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PatternItemViewHolder) convertView.getTag();
        }

        viewHolder.mPatternName.setText(mPatterns[position].getName());
        viewHolder.mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patternId = ((Metadata)getItem(position)).getId();
                Intent intent = new Intent(mContext, EditorActivity.class);
                intent.putExtra(Constants.EXTRA_PATTERN_ID, patternId);
                mContext.startActivity(intent);
            }
        });
        viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Metadata m = (Metadata)getItem(position);
                confirmDeleteDialog(m.getId(), m.getName());
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patternId = ((Metadata)getItem(position)).getId();
                Intent intent = new Intent(mContext, ViewerActivity.class);
                intent.putExtra(Constants.EXTRA_PATTERN_ID, patternId);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    public void confirmDeleteDialog(final String id, String name) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.dialog_title_pattern_delete, name))
                .setPositiveButton(R.string.dialog_ok,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PatternStorage storage = PatternStorage.getInstance();
                        storage.delete(id);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.dialog_no,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.show();
    }

    static class PatternItemViewHolder {
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
