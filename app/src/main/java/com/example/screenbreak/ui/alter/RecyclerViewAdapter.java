package com.example.screenbreak.ui.alter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.screenbreak.R;
import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<PieEntry> pieEntries;
    private Context mContext;

    public RecyclerViewAdapter(Context context,List<PieEntry> pieEntries) {

        this.pieEntries = pieEntries;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PieEntry entry = pieEntries.get(position);

        holder.textViewAppName.setText(entry.getLabel());
        //Log.d("PRUEBA DE NOMBREE", entry.getLabel());

        String packageName = getPackageName(entry.getLabel());
        //Log.d("PRUEBA DEFINITIVA: ", packageName);

        holder.appIcon.setBackground(getAppIconByPackageName(packageName));


        int totalTime = (int) entry.getValue();
        if (totalTime <= 60) {
            holder.textViewAppTime.setText(totalTime + " min.");
        } else {
            int hours = totalTime / 60;
            int minutes = totalTime % 60;
            holder.textViewAppTime.setText(hours + " hrs " + minutes + " min.");
        }
    }

    @Override
    public int getItemCount() {
        return pieEntries.size();
    }

    public Drawable getAppIconByPackageName(String packageName) {
        //PackageManager pm = getPackageManager();
        PackageManager pm = mContext.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return pm.getApplicationIcon(appInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPackageName(String appName) {
        PackageManager packageManager = mContext.getPackageManager();

        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : appList) {
            String packageName = appInfo.packageName;
            String name = appInfo.loadLabel(packageManager).toString();
            if (name.equals(appName)) {
                return packageName;
            }
        }
        return null;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewAppName;
        public TextView textViewAppTime;

        public ImageView appIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewAppName = itemView.findViewById(R.id.textViewAppName);
            textViewAppTime = itemView.findViewById(R.id.textViewAppTime);
            appIcon = itemView.findViewById(R.id.icon_id);
        }
    }
    public static class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }
}