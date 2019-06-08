package com.gorun.kelompok7.runningapp.Fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gorun.kelompok7.runningapp.LocationTrack;
import com.gorun.kelompok7.runningapp.MainActivity;
import com.gorun.kelompok7.runningapp.Model.Aktivitas;
import com.gorun.kelompok7.runningapp.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

public class HomeFragment extends Fragment {
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    Button buttonStart, buttonStop;
    public Chronometer timer;
    TextView tvWaktuTanggal, tvWaktu, tvJarak, tvKecepatan, tvKaloriBakar;
    private boolean currentlyTiming = false;
    double latAwal, latAkhir, longAwal, longAkhir;
    String dateAndTime;
    private long waktu;
    double distance, speed, percepatan, kaloriTerbakar;
    DecimalFormat df;
    DatabaseReference databaseReference;
    static ProgressDialog locate;
    List<Aktivitas> aktivitasList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0){
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }

            databaseReference = FirebaseDatabase.getInstance().getReference("Aktivitas").child(MainActivity.ID_USER);
            aktivitasList = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.fragment_home, null);
        buttonStart = rootView.findViewById(R.id.buttonStart);
        buttonStop = rootView.findViewById(R.id.buttonStop);
        timer = rootView.findViewById(R.id.timer);
        df = new DecimalFormat("0.00");

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTrack = new LocationTrack(getActivity());
                if (locationTrack.canGetLocation()){
                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();
                    buttonStart.setVisibility(View.GONE);
                    buttonStop.setVisibility(View.VISIBLE);
                    currentlyTiming = true;
                    Log.d("Timing Start", String.valueOf(currentlyTiming));
                    latAwal = latitude;
                    longAkhir = longitude;

                    dateAndTime = DateFormat.getDateTimeInstance().format(new Date());
                    Log.d("Waktu Mulai", dateAndTime);
                    //Toast.makeText(getActivity(), "Awal :" + String.valueOf(longitude) + " , " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();
                } else{
                    locationTrack.showSettingAlert();
                }

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationTrack = new LocationTrack(getActivity());
                if (locationTrack.canGetLocation()) {
                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    waktu = (SystemClock.elapsedRealtime() - timer.getBase())/1000;

                    timer.stop();
                    timer.setBase(SystemClock.elapsedRealtime());

                    Log.d("Waktu timer", String.valueOf(waktu));
                    buttonStart.setVisibility(View.VISIBLE);
                    buttonStop.setVisibility(View.GONE);
                    currentlyTiming = false;
                    Log.d("Timing Stop", String.valueOf(currentlyTiming));

                    longAkhir = longitude;
                    latAkhir = latitude;

                    Location locationStart = new Location("Start");
                    locationStart.setLongitude(longAwal);
                    locationStart.setLatitude(latAwal);

                    Location locationEnd = new Location("End");
                    locationEnd.setLongitude(longAkhir);
                    locationEnd.setLatitude(latAkhir);

                    distance = locationStart.distanceTo(locationEnd);

                    // Trick
                    if (distance < 0.1) {
                        distance = waktu * 35.0 / 100;
                    }

                    //Toast.makeText(getActivity(), "Awal :" + String.valueOf(longitude) + " , " + String.valueOf(latitude), Toast.LENGTH_SHORT).show();
                    try{
                        speed = distance/waktu;
                        percepatan = speed/waktu;
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.d("Jarak", String.valueOf(distance));
                    Log.d("Percepatan", String.valueOf(percepatan));

                    countCalori();
                    countDistance();
//                    countSpeed();
//                    Aktivitas.setKaloriTerbakar(countCalori());
                    showDialogSave();
                } else {
                    locationTrack.showSettingAlert();
                }
            }
        });

        return rootView;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wakted) {
        ArrayList result = new ArrayList();
        for (String permission : wakted) {
            if (!hasPermission(permission)){
                result.add(permission);
            }
        }
        return  result;
    }

    private boolean hasPermission(String permission) {
        if(canMakeSmores()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                return (checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ALL_PERMISSIONS_RESULT :
                for (String perms : permissionsToRequest){
                    if (!hasPermission(perms)){
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size()>0){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                        requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                    }
                                }
                            });
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public boolean getCurrentlyTimming() {
        return this.currentlyTiming;
    }


    private void showDialogSave() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        final View dialogView = getLayoutInflater().inflate(R.layout.popup_details, null);

        tvWaktuTanggal = dialogView.findViewById(R.id.waktuTanggal);
        tvWaktu = dialogView.findViewById(R.id.waktu);
        tvJarak = dialogView.findViewById(R.id.jarak);
        tvKecepatan = dialogView.findViewById(R.id.kecepatan);
        tvKaloriBakar = dialogView.findViewById(R.id.burnedKalori);

        tvWaktuTanggal.setText(dateAndTime);
        tvWaktu.setText("Waktu : " + df.format(waktu/60.0) + " menit");
        tvJarak.setText("Jarak : " + df.format(distance) + " meter");
        tvKecepatan.setText("Kecepatan : " + speed + " meter/detik");
        tvKaloriBakar.setText("Kalori terbakar : " + df.format(kaloriTerbakar) + " kal");
        dialog.setView(dialogView)
                .setTitle("Detail aktivitas Anda saat ini")
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(), "Anda memilih simpan", Toast.LENGTH_SHORT).show();
                        locate = new ProgressDialog(getActivity());
                        locate.setIndeterminate(true);
                        locate.setCancelable(false);
                        locate.setMessage("Sedang memuat...");
                        locate.show();
                        saveToDatabase();
                        dialog.dismiss();
                        Fragment fragment = new DashboardFragment();
                        changeFragment(fragment);
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(), "Anda memilih tidak", Toast.LENGTH_SHORT).show();
                        //dialog.dismiss();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                })
                .setNeutralButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(), "Anda memilih batal", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void countCalori() {
        double berat = MainActivity.BERAT;
        double tinggi = MainActivity.TINGGI;

        kaloriTerbakar = 60 * (((0.035 * berat) + ((percepatan * percepatan)/tinggi)) * 0.029 * berat) / 1000;
        Aktivitas  aktivitas = new Aktivitas();
        aktivitas.setKaloriTerbakar(kaloriTerbakar);
        //Toast.makeText(getActivity(), "Kalori terbakar : " + kaloriTerbakar, Toast.LENGTH_SHORT).show();
    }

    private void countDistance() {
    }

    private void saveToDatabase() {
        String name = MainActivity.NAMA_USER;
        String id = databaseReference.push().getKey();
        Aktivitas aktivitas = new Aktivitas(id, name, dateAndTime, waktu, distance, speed, kaloriTerbakar);

        databaseReference.child(id).setValue(aktivitas).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                locate.dismiss();
            }
        });
    }

    private void changeFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
