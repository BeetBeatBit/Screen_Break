package com.example.screenbreak.ui.alter;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.screenbreak.R;
import com.example.screenbreak.databinding.FragmentAlterBinding;
import com.example.screenbreak.ui.home.RecyclerViewAdapter;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class alterFragment extends Fragment {

    private FragmentAlterBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //return super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentAlterBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        //Obtener apps y tiempos
        List<PieEntry> appUsageTimeList = getTotalTimeYesterdayInMinutes(getInstalledApps());

        // Crear un RecyclerView y un RecyclerViewAdapter
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // Define el espacio vertical entre elementos
        int verticalSpaceHeight = getResources().getDimensionPixelSize(R.dimen.recycler_view_vertical_space);
        recyclerView.addItemDecoration(new RecyclerViewAdapter.VerticalSpaceItemDecoration(verticalSpaceHeight));

        // Crea un nuevo LinearLayoutManager y lo asigna al RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Crea un nuevo adaptador y lo asigna al RecyclerView
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(appUsageTimeList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    public List<PieEntry> getTotalTimeYesterdayInMinutes(List<ApplicationInfo> userInstalledApps) {
        /*
        //ayer
        UsageStatsManager usageStatsManager = (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        */

        //hoy
        UsageStatsManager usageStatsManager = (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        Map<String, Long> appUsageTimeMap = new HashMap<>();
        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();
            if (isUserInstalledApp(packageName, userInstalledApps)) {
                long timeInForeground = usageStats.getTotalTimeInForeground() / 1000 / 60;
                if (timeInForeground > 0) {
                    appUsageTimeMap.put(getAppName(packageName), timeInForeground);
                }
            }
        }

        List<PieEntry> pieEntries = new ArrayList<>();

        for (Map.Entry<String, Long> entry : appUsageTimeMap.entrySet()) {
            int totalTime = entry.getValue().intValue();
            String appName = entry.getKey();
            pieEntries.add(new PieEntry(totalTime, appName));
        }

        // Ordenar la lista de PieEntry en orden descendente según totalTime
        Collections.sort(pieEntries, new Comparator<PieEntry>() {
            @Override
            public int compare(PieEntry e1, PieEntry e2) {
                int value1 = (int) (Float.floatToIntBits(e1.getValue()) >> 23);
                int value2 = (int) (Float.floatToIntBits(e2.getValue()) >> 23);
                return Integer.compare(value2, value1);
            }
        });

        return pieEntries;
    }

    private boolean isUserInstalledApp(String packageName, List<ApplicationInfo> userInstalledApps) {
        for (ApplicationInfo appInfo : userInstalledApps) {
            if (packageName.equals(appInfo.packageName)) {
                return true;
            }
        }
        return false;
    }

    private String getAppName(String packageName) {
        PackageManager packageManager = requireContext().getPackageManager();
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, 0)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }


    public List<ApplicationInfo> getInstalledApps() {
        PackageManager packageManager = getContext().getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> userInstalledApps = new ArrayList<>();

        for (ApplicationInfo appInfo : installedApps) {
            // Filter out system apps
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userInstalledApps.add(appInfo);
            }
        }
        /*
        for (ApplicationInfo appInfo : userInstalledApps) {
            Log.d("InstalledApp", "Package name: " + appInfo.packageName);
        }*/

        return userInstalledApps;
    }

}