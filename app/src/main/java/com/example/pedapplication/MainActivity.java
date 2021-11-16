package com.example.pedapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AbsenteesListener,PresentListener,RestListener,ODListener {
    Button confirmbtn, addstudentbtn;
    RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseFirestore firestore;
    ArrayList<Students> arrayList, absenteesList,odList,restList,presentList;
    MyAdapter myAdapter;
    HashMap<String,String> hm;
    HashMap<String, String> absenteesDetails,odDetails,restDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getting the date which captain selects to store the attendance details into the database
        SharedPreferences prefs1 = PreferenceManager.getDefaultSharedPreferences(this);
        String get_date2 = prefs1.getString("date2", "no_id");


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String data = prefs.getString("message", "no_id");

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        confirmbtn = findViewById(R.id.confrimbtnmain);
        addstudentbtn = findViewById(R.id.addstudentbtn);

        hm = new HashMap<>();

        arrayList = new ArrayList<>();
        odList = new ArrayList<>();
        absenteesDetails = new HashMap<>();
        restList = new ArrayList<>();
        presentList = new ArrayList<>();

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.Rview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        absenteesList = new ArrayList<>();

        //passing interface references
        myAdapter = new MyAdapter(MainActivity.this, arrayList,this,this,this,this);
        recyclerView.setAdapter(myAdapter);

        EventChangeListener();
        System.out.println(arrayList);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new
                ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        navigationView.setCheckedItem(R.id.nav_home);

        addstudentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(getApplicationContext(), AddStudent.class)));
            }
        });

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(Students present: presentList){
                    hm.put("status","Present");
                    hm.put("name", present.name);
                    hm.put("department", present.department);
                    hm.put("year", present.year);
                    hm.put("rollno", present.rollno);
                    hm.put("gender", present.gender);
                    DocumentReference documentReference = firestore.collection("captains").document(data).collection("Attendance").document(get_date2).collection("PresentList").document(hm.get("name") + " " + hm.get("rollno"));
                    documentReference.set(hm);
                }
                for(Students present: absenteesList){
                    hm.put("status","Absent");
                    hm.put("name", present.name);
                    hm.put("department", present.department);
                    hm.put("year", present.year);
                    hm.put("rollno", present.rollno);
                    hm.put("gender", present.gender);
                    DocumentReference documentReference = firestore.collection("captains").document(data).collection("Attendance").document(get_date2).collection("AbsenteesList").document(hm.get("name") + " " + hm.get("rollno"));
                    documentReference.set(hm);
                }
                for(Students present: odList){
                    hm.put("status","OD");
                    hm.put("name", present.name);
                    hm.put("department", present.department);
                    hm.put("year", present.year);
                    hm.put("rollno", present.rollno);
                    hm.put("gender", present.gender);
                    DocumentReference documentReference = firestore.collection("captains").document(data).collection("Attendance").document(get_date2).collection("ODList").document(hm.get("name") + " " + hm.get("rollno"));
                    documentReference.set(hm);
                }
                for(Students present: restList){
                    hm.put("status","Rest");
                    hm.put("name", present.name);
                    hm.put("department", present.department);
                    hm.put("year", present.year);
                    hm.put("rollno", present.rollno);
                    hm.put("gender", present.gender);
                    DocumentReference documentReference = firestore.collection("captains").document(data).collection("Attendance").document(get_date2).collection("RestList").document(hm.get("name") + " " + hm.get("rollno"));
                    documentReference.set(hm);
                }
               startActivity(new Intent(getApplicationContext(),DetailsStroredSuccessfully.class));
            }
        });

    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.addstudents:
                Intent intent = new Intent(MainActivity.this, AddStudent.class);
                startActivity(intent);
                break;

            case R.id.logout:

                Intent intent2 = new Intent(getApplicationContext(), CaptainLogin.class);
                startActivity(intent2);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void EventChangeListener() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String data = prefs.getString("message", "no_id");

        firestore.collection("captains").document(data).collection("Students").orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {

                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                arrayList.add(dc.getDocument().toObject(Students.class));

                            }

                            myAdapter.notifyDataSetChanged();


                        }
//                        if (arrayList.size() == 0) {
//                            if (progressDialog.isShowing()) {
//                                progressDialog.dismiss();
//                            }
//                        }


                    }
                });


    }

    @Override
    public void onAbsenteesQuantityChange(ArrayList<Students> arrayList) {
        absenteesDetails.clear();
        absenteesList.clear();
        absenteesList.addAll(arrayList);
        for (Students s : arrayList) {
            System.out.println(s.name + " Absent");
            absenteesDetails.put(s.name, s.rollno);


        }

    }

    @Override
    public void onODQuantityChange(ArrayList<Students> arrayList) {
        odList.clear();
        odList.addAll(arrayList);
        for (Students s : odList) {
            System.out.println(s.name + " OD");
        }

    }

    @Override
    public void onPresentQuantityChange(ArrayList<Students> arrayList) {
        presentList.clear();
        presentList.addAll(arrayList);
        for (Students s : presentList) {
            System.out.println(s.name + " Present");
        }

    }

    @Override
    public void onRestQuantityChange(ArrayList<Students> arrayList) {
        restList.clear();
        restList.addAll(arrayList);
            for (Students s : restList) {
                System.out.println(s.name + " Rest");
            }

    }
}