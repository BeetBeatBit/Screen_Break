package com.example.screenbreak.ui.home;


import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.screenbreak.UnlockReceiver;
import com.example.screenbreak.databinding.FragmentHomeBinding;

//Graficas
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;


import com.example.screenbreak.R;
import android.graphics.Color;

//Stats
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.app.usage.UsageStats;
import android.util.Log;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PieChart mPieChart;

    private UnlockReceiver unlockReceiver;
    private TextView unlockCountTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        final TextView textView = binding.textHome;


        //Codigo del contador--------------------

        // Se crea una instancia del BroadcastReceiver
        unlockReceiver = new UnlockReceiver();

        // Se registra el BroadcastReceiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        requireActivity().registerReceiver(unlockReceiver, filter);

        // Se obtiene una referencia al elemento TextView del diseño
        unlockCountTextView = root.findViewById(R.id.unlock_count_text_view);
        //------------------------------------------


         getInstalledApps(); //La funcion retorna una lista con las apps del usuario

        List<String> appUsageTimeList = getTotalTimeYesterdayInMinutes(getInstalledApps()); //La funcion retorna una lista con las apps del usuario y su tiempo ordenadas descendente
        for (String appUsageTime : appUsageTimeList) {
            Log.d("MiApp", appUsageTime);
        }



        //NO TOCAR CODIGO AQUI ABAJO
        // Configurar la gráfica circular
        mPieChart = root.findViewById(R.id.pie_chart);
        mPieChart.setUsePercentValues(false);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);
        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.WHITE);
        mPieChart.setTransparentCircleRadius(61f);
        mPieChart.setHoleRadius(58f);

        // Crear una lista de datos para la gráfica circular y configurar su contenido
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        //pieEntries.add(new PieEntry(totalTimeWhats, "WhatsApp"));
        //pieEntries.add(new PieEntry(totalTimeFacebook, "Facebook"));
        //pieEntries.add(new PieEntry(totalTimeTikTok, "TikTok"));

        // Crear un conjunto de datos para la gráfica circular y configurar sus propiedades
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Apps mas usadas");
        pieDataSet.setColors(new int[] { ContextCompat.getColor(requireContext(), R.color.color1), ContextCompat.getColor(requireContext(), R.color.color2), ContextCompat.getColor(requireContext(), R.color.color3) });

        // Crear un objeto de tipo PieData y configurar su contenido y propiedades
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));

        // Asignar el objeto data a la vista de la gráfica circular y actualizar la vista
        mPieChart.setData(pieData);
        mPieChart.animateY(1000);
        mPieChart.invalidate();

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Se desregistra el BroadcastReceiver
        requireActivity().unregisterReceiver(unlockReceiver);
        //binding = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Se actualiza el valor del TextView cada vez que se muestra el fragmento
        unlockCountTextView.setText("Desbloqueos: " + unlockReceiver.getUnlockCount());
    }

    public List<String> getTotalTimeYesterdayInMinutes(List<ApplicationInfo> userInstalledApps) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
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

        List<String> sortedAppUsageTimeList = new ArrayList<>(appUsageTimeMap.entrySet())
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                //.map(entry -> entry.getKey() + " - " + entry.getValue() + " minutes")
                .map(entry -> entry.getKey() + " - " + entry.getValue())
                .collect(Collectors.toList());

        return sortedAppUsageTimeList;
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
