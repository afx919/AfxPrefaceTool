package com.afx.afx_preface_tool;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppListViewAdapter extends ArrayAdapter<AppListItemBean> {
    MainActivity main_activity = null;
    public AppListViewAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<AppListItemBean> objects, MainActivity main_activity) {
        super(context, resource, textViewResourceId, objects);
        this.main_activity = main_activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);

        final AppListItemBean item = getItem(position);

        ImageView item_icon = (ImageView) view.findViewById(R.id.item_icon);
        assert (item_icon != null);
        if(item != null && item.icon != null) {
            item_icon.setImageDrawable(item.icon);
        }

        TextView item_app_name = (TextView) view.findViewById(R.id.item_app_name);
        assert (item_app_name != null);
        if(item != null && item.name != null) {
            item_app_name.setText(item.name);
        }

        TextView item_package_name = (TextView) view.findViewById(R.id.item_package_name);
        assert (item_package_name != null);
        if(item != null && item.package_name != null) {
            item_package_name.setText(item.package_name);
        }

        Button delete_item = (Button) view.findViewById(R.id.delete_item);
        assert (delete_item != null);
        delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(main_activity != null) {
                    main_activity.removeAutoStartApp(item.package_name);
                }
            }
        });

        return view;
    }
}
