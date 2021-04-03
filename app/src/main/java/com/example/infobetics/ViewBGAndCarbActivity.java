package com.example.infobetics;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ViewBGAndCarbActivity extends AppCompatActivity {
    private FirebaseFirestore fdb = FirebaseFirestore.getInstance();
    private CollectionReference bgCarbRef = fdb.collection("BGAndCarbohydrate");
    private BGAndCarbAdapter bgAndCarbAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_b_g_and_carb);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();
        final FirebaseUser fUser = firebaseAuth.getCurrentUser();

        setRecyclerView();
    }

    private void setRecyclerView() {
        //Order recycler view by hightest to lowest insulin amount
        //And show data only for the current logged in user
        Query query = bgCarbRef.orderBy("currentDateAndTime", Query.Direction.DESCENDING).whereEqualTo("userID", userID);

        //Get query into adapter
        FirestoreRecyclerOptions<BGAndCarbData> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<BGAndCarbData>().setQuery(query, BGAndCarbData.class)
                .build();

        //Assign adapter variable
        bgAndCarbAdapter = new BGAndCarbAdapter(firestoreRecyclerOptions);

        //Reference to recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bgAndCarbAdapter);

        bgAndCarbAdapter.setOnItemClickListener(new BGAndCarbAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                BGAndCarbData bgAndCarbData = documentSnapshot.toObject(BGAndCarbData.class);
                String id = documentSnapshot.getId();
                Intent editIntent = new Intent(ViewBGAndCarbActivity.this, EditBGAndCarbActivity.class);
                editIntent.putExtra("documentID", id);
                startActivity(editIntent);
            }
        });
    }

    //When to listen to database changes
    @Override
    protected void onStart() {
        super.onStart();
        bgAndCarbAdapter.startListening();
    }

    //When not to listen to database changes
    @Override
    protected void onStop() {
        super.onStop();
        bgAndCarbAdapter.startListening();
    }
}
