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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.screenbreak.UnlockReceiver;
import com.example.screenbreak.databinding.FragmentHomeBinding;

//Graficas
import com.example.screenbreak.ui.alter.RecyclerViewAdapter;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PieChart mPieChart;

    private UnlockReceiver unlockReceiver;
    private TextView unlockCountTextView;
    private TextView textViewTotalPhoneTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //final TextView textView = binding.textHome;


        //Codigo del contador-----------------------
        unlockReceiver = new UnlockReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        requireActivity().registerReceiver(unlockReceiver, filter);
        unlockCountTextView = root.findViewById(R.id.unlock_count_text_view);
        //------------------------------------------

        // Configurar la gráfica circular
        mPieChart = root.findViewById(R.id.pie_chart);
        mPieChart.setUsePercentValues(false);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);
        mPieChart.setDragDecelerationFrictionCoef(0.95f);
        mPieChart.setDrawHoleEnabled(false);
        //mPieChart.setHoleColor(Color.WHITE);
        mPieChart.setTransparentCircleRadius(61f);
        mPieChart.setHoleRadius(58f);
        mPieChart.setMinimumWidth(1000);

        //Obtener apps y tiempos
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        List<PieEntry> appUsageTimeList = getTotalTimeYesterdayInMinutes(getInstalledApps());



        int count = 0;
        float totalPhoneTime = 0;
        for (PieEntry entry : appUsageTimeList) {
            float totalTime = entry.getValue();
            String appName = entry.getLabel();
            totalPhoneTime = totalPhoneTime + totalTime;
            //Log.d("MiApp", Float.toString(totalTime));
            //Log.d("MiApp", appName);

            int appMin = (int) totalTime;
            pieEntries.add(new PieEntry(appMin, appName));

            count++;
            if (count >= 5) {
                break;
            }
        }

        //Tiempo total de uso
        textViewTotalPhoneTime = root.findViewById(R.id.textViewTotalPhoneTime);
        if (totalPhoneTime <= 60) {
            textViewTotalPhoneTime.setText( totalPhoneTime + " min.");
        }else {
            // calcular horas y minutos
            int hours = (int) totalPhoneTime / 60;
            int minutes = (int) totalPhoneTime % 60;
            textViewTotalPhoneTime.setText( hours + " hrs " + minutes + " min.");
        }


        // Crear un conjunto de datos para la gráfica circular y configurar sus propiedades
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Top 5 Apps");


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
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(),pieEntries);
        recyclerView.setAdapter(adapter);
/*
        // Configurar el RecyclerView con el adaptador
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        int verticalSpaceHeight = getResources().getDimensionPixelSize(R.dimen.vertical_space);
        recyclerView.addItemDecoration(new RecyclerViewAdapter.VerticalSpaceItemDecoration(verticalSpaceHeight));
*/
        //Colores de la gráfica
        int numEntries = pieDataSet.getEntryCount();
        int[] randomColors = new int[numEntries];
        Random random = new Random();
        for (int i = 0; i < numEntries; i++) {
            randomColors[i] = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
        pieDataSet.setColors(randomColors);
        //pieDataSet.setColors(new int[] { ContextCompat.getColor(requireContext(), R.color.color1), ContextCompat.getColor(requireContext(), R.color.color2), ContextCompat.getColor(requireContext(), R.color.color3) });

        // Crear un objeto de tipo PieData y configurar su contenido y propiedades
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));

        // Asignar el objeto data a la vista de la gráfica circular y actualizar la vista
        mPieChart.setData(pieData);
        mPieChart.animateY(1500);
        mPieChart.invalidate();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Se desregistra el BroadcastReceiver
        requireActivity().unregisterReceiver(unlockReceiver);
    }
    @Override
    public void onResume() {
        super.onResume();
        // Se actualiza el valor del TextView cada vez que se muestra el fragmento
        unlockCountTextView.setText("Hoy has desbloqueado " + unlockReceiver.getUnlockCount() + " Veces tu telefono");
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

        // Marca un intervalo de tiempo de 24 horas a partir de las 12 am del dia de hoy
        UsageStatsManager usageStatsManager = (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();

        //Obtiene los tiempos con el intervalo
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        Map<String, Long> appUsageTimeMap = new HashMap<>();

        //Filtra los tiempos solo de las apps que tiene el usuario
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
        // Esta funcion obtiene la lista de apps instaladas por el usuario y filtra las de sistema.

        PackageManager packageManager = getContext().getPackageManager();
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> userInstalledApps = new ArrayList<>();

        // Filtra las apps del sistema
        for (ApplicationInfo appInfo : installedApps) {

            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userInstalledApps.add(appInfo);
            }
        }

        /*
        // Imprime la lista final.
        for (ApplicationInfo appInfo : userInstalledApps) {
            Log.d("InstalledApp", "Package name: " + appInfo.packageName);
        }*/

        return userInstalledApps;
    }


}
