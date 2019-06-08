package com.gorun.kelompok7.runningapp.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gorun.kelompok7.runningapp.Model.Aktivitas;
import com.gorun.kelompok7.runningapp.R;

import java.text.DecimalFormat;
import java.util.List;

public class AktivitasAdapter extends ArrayAdapter<Aktivitas> {
    private Activity context;
    List<Aktivitas> aktivitasList;
    DecimalFormat df;

    public AktivitasAdapter (Activity context, List<Aktivitas> aktivitasList) {
        super(context, R.layout.list_aktivitas, aktivitasList);
        this.context = context;
        this.aktivitasList = aktivitasList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        df = new DecimalFormat("0.00");
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_aktivitas, null, true);
        TextView tvWaktuTanggal = listViewItem.findViewById(R.id.waktuTanggal);
        TextView tvWaktu = listViewItem.findViewById(R.id.waktu);
        TextView tvJarak = listViewItem.findViewById(R.id.jarak);
        TextView tvKaloriTerbakar = listViewItem.findViewById(R.id.burnedKalori);

        Aktivitas aktivitas = aktivitasList.get(position);
        tvWaktuTanggal.setText(aktivitas.getTanggalWaktu());
        tvWaktu.setText("Waktu : " + df.format(aktivitas.getWaktu() / 60.0) + " menit");
        tvJarak.setText("Jarak : " + df.format(aktivitas.getJarak()) + " meter");
        tvKaloriTerbakar.setText("Kalori terbakar : " + df.format(aktivitas.getKaloriTerbakar()) + " kal");

        return listViewItem;
    }
}
