package com.dinesh.hungervalleydelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompletedOrders extends AppCompatActivity {

    private DatabaseReference mOrdersDatabase;
    ProgressBar progressBar;
    TextView txt_no_order;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private myadapter adapter;
    Toolbar toolbar;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_orders);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Completed Orders");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txt_no_order = (TextView) findViewById(R.id.txt_no_orders);
        recyclerView = (RecyclerView) findViewById(R.id.upload_list);

        user_id = getIntent().getStringExtra("user_id");

        progressBar.setVisibility(View.VISIBLE);

        mOrdersDatabase = FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(user_id).child("CompletedOrders");

        mOrdersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {

                    txt_no_order.setVisibility(View.GONE);

                    linearLayoutManager = new LinearLayoutManager(CompletedOrders.this);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    FirebaseRecyclerOptions<ListSetGet> options =
                            new FirebaseRecyclerOptions.Builder<ListSetGet>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(user_id).child("CompletedOrders"), ListSetGet.class)
                                    .build();


                    adapter = new myadapter(options);
                    adapter.startListening();
                    recyclerView.setAdapter(adapter);


                    progressBar.setVisibility(View.GONE);

                } else {

                    txt_no_order.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public class myadapter extends FirebaseRecyclerAdapter<ListSetGet, CompletedOrders.myadapter.myviewholder> {
        public myadapter(@NonNull FirebaseRecyclerOptions<ListSetGet> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull CompletedOrders.myadapter.myviewholder holder, final int position, @NonNull ListSetGet model) {

            final String Id = getRef(position).getKey();
            holder.name.setText(Id);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(CompletedOrders.this, SingleCompletedActivity.class);
                    intent.putExtra("user_id", Id);
                    intent.putExtra("my_user_id", user_id);
                    intent.putExtra("order_id", getRef(position).getKey());
                    startActivity(intent);

                }
            });
        }

        @NonNull
        @Override
        public myadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single1, parent, false);
            return new myviewholder(view);
        }

        class myviewholder extends RecyclerView.ViewHolder {

            TextView name, status;

            public myviewholder(@NonNull View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                //status = (TextView) itemView.findViewById(R.id.status);

            }
        }
    }

}