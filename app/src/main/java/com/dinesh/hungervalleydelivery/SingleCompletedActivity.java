package com.dinesh.hungervalleydelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SingleCompletedActivity extends AppCompatActivity {

    private RecyclerView res_name;

    private LinearLayoutManager linearLayoutManager, linearLayoutManager1;

    private DatabaseReference mCartListDatabase, mUserDatabase, myDatabase;
    String userId, my_user_id, order_id, statusTxt, payment_type_txt, delBoyId;
    TextView number, address, altNumber, total;
    ProgressBar progressbar;
    int statusItem;
    int profitAmount, intTotal;
    DatabaseReference mDeliveryBoysDatabase;
    List<String> listItems, listItems1;

    private myadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_completed);

        userId = getIntent().getStringExtra("user_id");
        my_user_id = getIntent().getStringExtra("my_user_id");
        order_id = getIntent().getStringExtra("order_id");

        number = (TextView) findViewById(R.id.phoneNumber);
        altNumber = (TextView) findViewById(R.id.altPhoneNumber);
        address = (TextView) findViewById(R.id.address);
        progressbar = findViewById(R.id.progressbar);
        total = findViewById(R.id.total);


        listItems = new ArrayList<String>();
        listItems.add("Done");
        listItems.add("Pending");

        listItems1 = new ArrayList<String>();
        listItems1.add("Cash");
        listItems1.add("Online");


        number.setText("Phone Number : " + userId);

        res_name = (RecyclerView) findViewById(R.id.res_name);

        progressbar.setVisibility(View.VISIBLE);

        final CharSequence[] items = {"Done", "Pending"};
        final CharSequence[] items1 = {"Cash", "Online"};

        mCartListDatabase = FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(my_user_id).child("Orders");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                progressbar.setVisibility(View.VISIBLE);

                //altNumber.setText("Alternate Number : " + dataSnapshot.child("Address").child("Mobile").getValue().toString());
                //address.setText(dataSnapshot.child("Address").child("landmark").getValue().toString() + "\n" + dataSnapshot.child("Address").child("locality").getValue().toString() + "\n" + dataSnapshot.child("Address").child("location").getValue().toString());


                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressbar.setVisibility(View.GONE);
            }
        });

        linearLayoutManager = new LinearLayoutManager(SingleCompletedActivity.this);
        res_name.setLayoutManager(linearLayoutManager);


        FirebaseRecyclerOptions<OrderSetGet> options =
                new FirebaseRecyclerOptions.Builder<OrderSetGet>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(my_user_id).child("CompletedOrders").child(order_id).child("Orders"), OrderSetGet.class)
                        .build();

        adapter = new myadapter(options);
        adapter.startListening();
        res_name.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(my_user_id).child("CompletedOrders").child(order_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                total.setText(String.valueOf(dataSnapshot.child("Total Price").getValue()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public class myadapter extends FirebaseRecyclerAdapter<OrderSetGet, myadapter.myviewholder> {
        public myadapter(@NonNull FirebaseRecyclerOptions<OrderSetGet> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull myadapter.myviewholder holder, int position, @NonNull OrderSetGet model) {

            progressbar.setVisibility(View.VISIBLE);

            holder.restro.setText(model.getRes());
            holder.price.setText(model.getPrice());
            holder.product_name.setText(model.getpName());
            holder.quantity.setText(model.getQuantity());

            progressbar.setVisibility(View.GONE);
        }

        @NonNull
        @Override
        public myadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_list, parent, false);
            return new myadapter.myviewholder(view);
        }

        class myviewholder extends RecyclerView.ViewHolder {

            TextView restro, price, product_name, quantity;

            public myviewholder(@NonNull View itemView) {
                super(itemView);
                price = (TextView) itemView.findViewById(R.id.price);
                restro = (TextView) itemView.findViewById(R.id.restro);
                product_name = (TextView) itemView.findViewById(R.id.product_name);
                quantity = (TextView) itemView.findViewById(R.id.qnty);


            }
        }
    }
}