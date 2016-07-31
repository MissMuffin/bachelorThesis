package de.muffinworks.knittingapp.views.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import de.muffinworks.knittingapp.R;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 31.07.2016.
 */
public class GlossaryAdapter extends BaseAdapter {

    private LayoutInflater mInflator;
    private Context mContext;

    public GlossaryAdapter(Context context) {
        mContext = context;
        mInflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Constants.SYMBOLS.length;
    }

    @Override
    public Object getItem(int position) {
        return Constants.SYMBOLS[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GlossaryItemViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.view_item_glossary, null);
            viewHolder = new GlossaryItemViewHolder(convertView);
            viewHolder.mSymbol.setTypeface(Typeface.createFromAsset(mContext.getAssets(), Constants.KNITTING_FONT_PATH));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GlossaryItemViewHolder) convertView.getTag();
        }

        viewHolder.mSymbol.setText(Constants.SYMBOLS[position]);
        viewHolder.mDescription.setText(Constants.SYMBOL_DESCRIPTIONS[position]);

        return convertView;
    }

    static class GlossaryItemViewHolder {
        public TextView mSymbol;
        public TextView mDescription;

        public GlossaryItemViewHolder(View root) {
            mSymbol = (TextView) root.findViewById(R.id.symbol);
            mDescription = (TextView) root.findViewById(R.id.symbol_description);
        }
    }
}
