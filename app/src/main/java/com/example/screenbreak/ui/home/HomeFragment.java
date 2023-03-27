package com.example.screenbreak.ui.home;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import android.Manifest;
import android.app.usage.UsageStatsManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.provider.Settings;
import android.app.usage.UsageStats;
import android.util.Log;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PieChart mPieChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;

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


        //Medir uso
        PackageManager packageManager = requireContext().getPackageManager();
        String packageNameThis = "com.example.screenbreak";

        String packageName = "com.whatsapp";
        String packageName2 = "com.facebook.katana";
        String packageName3 = "com.zhiliaoapp.musically";


        boolean permissionGranted = packageManager.checkPermission(Manifest.permission.PACKAGE_USAGE_STATS, packageNameThis) == PackageManager.PERMISSION_GRANTED;

        if (!permissionGranted) {
            Log.println(Log.INFO, "MainActivity", "Estado del permiso: " + permissionGranted);
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        float totalTimeWhats = getTotalTimeYesterdayInMinutes(packageName);
        float totalTimeFacebook = getTotalTimeYesterdayInMinutes(packageName2);
        float totalTimeTikTok = getTotalTimeYesterdayInMinutes(packageName3);

        System.out.println("WhatsApp usage: "+totalTimeWhats);
        System.out.println("Facebook usage: "+totalTimeFacebook);
        System.out.println("TikTok usage: "+totalTimeTikTok);

        // Crear una lista de datos para la gráfica circular y configurar su contenido
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(totalTimeWhats, "WhatsApp"));
        pieEntries.add(new PieEntry(totalTimeFacebook, "Facebook"));
        pieEntries.add(new PieEntry(totalTimeTikTok, "TikTok"));

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
        binding = null;
    }



    public long getTotalTimeYesterdayInMinutes(String packageName) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, -1);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        for (UsageStats usageStats : usageStatsList) {
            if (usageStats.getPackageName().equals(packageName)) {
                return usageStats.getTotalTimeInForeground() / 1000 / 60;
            }
        }
        return -1;
    }

}
