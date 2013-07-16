package eu.pinnoo.garbagecalendar.ui.preferences;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import eu.pinnoo.garbagecalendar.R;
import eu.pinnoo.garbagecalendar.data.Address;
import eu.pinnoo.garbagecalendar.data.LocalConstants;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wouter Pinnoo <pinnoo.wouter@gmail.com>
 */
public class AddressAdapter extends ArrayAdapter<Address> {

    private List<Address> objects;
    private List<Address> original;
    private Context context;

    public AddressAdapter(Context context, int textViewResourceId, List<Address> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
        original = new ArrayList<Address>();
        original.addAll(objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.address_table_row, null);
        }
        v.setBackgroundColor(position % 2 == 0 ? LocalConstants.COLOR_TABLE_EVEN_ROW : Color.WHITE);
        TextView addressText = (TextView) v.findViewById(R.id.toptext);
        try {
            addressText.setText(objects.get(position).getStreetname() + ", " + objects.get(position).getCity());
        } catch (NullPointerException e) {
            addressText.setText(context.getString(R.string.none));
        }


        TextView nrText = (TextView) v.findViewById(R.id.nrtext);
        try {
            nrText.setText(objects.get(position).getFormattedNr(context));
        } catch (NullPointerException e) {
            nrText.setText(context.getString(R.string.none));
        }

        return v;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                if (charSequence == null || charSequence.length() == 0) {
                    results.values = original;
                    results.count = original.size();
                } else {
                    ArrayList<Address> filtered = new ArrayList<Address>();
                    ArrayList<Address> filteredLowPriority = new ArrayList<Address>();
                    for (Address item : original) {
                        int match = item.matches(charSequence.toString());
                        if (match == Address.FULL_MATCH) {
                            filtered.add(item);
                        } else if (match == Address.PARTIAL_MATCH) {
                            filteredLowPriority.add(item);
                        }
                    }
                    for (Address item : filteredLowPriority) {
                        filtered.add(item);
                    }
                    results.values = filtered;
                    results.count = filtered.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                objects.clear();
                objects.addAll((ArrayList<Address>) filterResults.values);

                notifyDataSetChanged();
            }
        };
    }
}
