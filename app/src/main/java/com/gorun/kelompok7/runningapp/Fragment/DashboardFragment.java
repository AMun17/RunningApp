package com.gorun.kelompok7.runningapp.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gorun.kelompok7.runningapp.Adapter.AktivitasAdapter;
import com.gorun.kelompok7.runningapp.MainActivity;
import com.gorun.kelompok7.runningapp.Model.Aktivitas;
import com.gorun.kelompok7.runningapp.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    TextView textView;
    DatabaseReference databaseReference;
    List<Aktivitas> aktivitasList;
    ListView listViewAktivitas;
    private static final String JUDUL = "Aktivitas";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference("Aktivitas").child(MainActivity.ID_USER);
        aktivitasList = new ArrayList<>();
    }

    @Override
    public void onResume() {

        super.onResume();
        ((MainActivity) getActivity()).setActionBar(JUDUL);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.fragment_dashboard, null);


        listViewAktivitas = rootView.findViewById(R.id.listViewAktivitas);

        listViewAktivitas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Aktivitas aktivitas = aktivitasList.get(position);
                showDeleteDialog(aktivitas.getId());

                return true;
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Aktivitas aktivitas = postSnapshot.getValue(Aktivitas.class);
                    aktivitasList.add(aktivitas);
                }

                if(getActivity() != null) {
                    AktivitasAdapter aktivitasAdapter = new AktivitasAdapter(getActivity(), aktivitasList);
                    listViewAktivitas.setAdapter(aktivitasAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void showDeleteDialog(final String aktivitasId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Hapus Aktivitas")
                .setMessage("Anda yakin ingin menghapus aktivitas ini?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(deleteAktivitas(aktivitasId)){
                            changeFragment(new DashboardFragment());
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void changeFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        Toast.makeText(getActivity(), "Aktivitas berhasil dihapus", Toast.LENGTH_SHORT).show();
    }

    private boolean deleteAktivitas(String aktivitasId) {
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Aktivitas").child(MainActivity.ID_USER);
        dr.child(aktivitasId).removeValue();
        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
    }
}
