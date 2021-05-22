package com.dinesh.hungervalleydelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleOrderActivity extends AppCompatActivity {

    private RecyclerView res_name;

    private LinearLayoutManager linearLayoutManager, linearLayoutManager1;

    private DatabaseReference mCartListDatabase, mUserDatabase, myDatabase;
    String userId, my_user_id, statusTxt, payment_type_txt, delBoyId;
    TextView number, address, altNumber, total;
    ProgressBar progressbar;
    Button complete, save;
    int statusItem;
    int profitAmount, intTotal;
    EditText res_pay, status, customerPaymentType;
    DatabaseReference mDeliveryBoysDatabase;
    List<String> listItems, listItems1;
    String dateToStr;

    private myadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order);

        userId = getIntent().getStringExtra("user_id");
        my_user_id = getIntent().getStringExtra("my_user_id");

        number = (TextView) findViewById(R.id.phoneNumber);
        altNumber = (TextView) findViewById(R.id.altPhoneNumber);
        address = (TextView) findViewById(R.id.address);
        progressbar = findViewById(R.id.progressbar);
        total = findViewById(R.id.total);
        status = findViewById(R.id.status);
        complete = findViewById(R.id.complete);
        res_pay = findViewById(R.id.res_pay);
        save = findViewById(R.id.save);
        customerPaymentType = findViewById(R.id.payment_type);

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

                altNumber.setText("Alternate Number : " + dataSnapshot.child("Address").child("Mobile").getValue().toString());
                address.setText(dataSnapshot.child("Address").child("landmark").getValue().toString() + "\n" + dataSnapshot.child("Address").child("locality").getValue().toString() + "\n" + dataSnapshot.child("Address").child("location").getValue().toString());

                altNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + dataSnapshot.child("Address").child("Mobile").getValue().toString()));
                        startActivity(callIntent);
                    }
                });

                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressbar.setVisibility(View.GONE);
            }
        });

        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + userId));
                startActivity(callIntent);

            }
        });

        linearLayoutManager = new LinearLayoutManager(SingleOrderActivity.this);
        res_name.setLayoutManager(linearLayoutManager);


        FirebaseRecyclerOptions<OrderSetGet> options =
                new FirebaseRecyclerOptions.Builder<OrderSetGet>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Orders List").child("User View").child(userId).child("Orders"), OrderSetGet.class)
                        .build();

        adapter = new myadapter(options);
        adapter.startListening();
        res_name.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(my_user_id).child("Orders").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("amountStatus")) {

                    status.setText(dataSnapshot.child("amountStatus").getValue().toString());

                } else {

                    status.setText("");
                    //status.setBackgroundColor(Color.parseColor("#008000"));
                }

                if (dataSnapshot.hasChild("resPayAmount")) {

                    res_pay.setText(dataSnapshot.child("resPayAmount").getValue().toString());

                } else {

                    res_pay.setText("");
                    //status.setBackgroundColor(Color.parseColor("#008000"));
                }
                if (dataSnapshot.hasChild("customerPaymentType")) {

                    customerPaymentType.setText(dataSnapshot.child("customerPaymentType").getValue().toString());

                } else {

                    customerPaymentType.setText("");
                    //status.setBackgroundColor(Color.parseColor("#008000"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SingleOrderActivity.this);
                builder.setTitle("Select");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_baseline_edit_24);
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        statusTxt = items[item].toString();

                    }
                });

                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                               /* FirebaseDatabase.getInstance().getReference().child("Orders List").child("User View").child(userId).child("Status").setValue(statusTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            status.setText(statusTxt);
                                            status.setBackgroundColor(Color.parseColor("#008000"));

                                            Toast.makeText(SingleOrderActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });*/

                                status.setText(statusTxt);

                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        customerPaymentType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SingleOrderActivity.this);
                builder.setTitle("Select");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_baseline_edit_24);
                builder.setSingleChoiceItems(items1, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        payment_type_txt = items1[item].toString();

                    }
                });

                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                customerPaymentType.setText(payment_type_txt);

                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Orders List").child("User View").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                total.setText(String.valueOf(dataSnapshot.child("Total Price").getValue()));

                intTotal = Integer.parseInt(dataSnapshot.child("Total Price").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(SingleOrderActivity.this)
                        .setMessage("are you sure ?")
                        .setCancelable(false)
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

                                if (res_pay.getText().toString().isEmpty() || Integer.parseInt(res_pay.getText().toString()) > intTotal) {

                                    progressbar.setVisibility(View.GONE);
                                    res_pay.setError("error");
                                    res_pay.requestFocus();
                                    return;

                                } else if (status.getText().toString().isEmpty()) {

                                    progressbar.setVisibility(View.GONE);
                                    status.setError("error");
                                    status.requestFocus();
                                    return;

                                } else {

                                    Map userMap = new HashMap();
                                    userMap.put("resPayAmount", res_pay.getText().toString());
                                    userMap.put("amountStatus", status.getText().toString());
                                    userMap.put("customerPaymentType", customerPaymentType.getText().toString());
                                    userMap.put("Status", "onWay");
                                    userMap.put("profitAmount", intTotal - Integer.parseInt(res_pay.getText().toString()));

                                    FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(my_user_id).child("Orders").child(userId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                Toast.makeText(SingleOrderActivity.this, "SAVED.", Toast.LENGTH_LONG).show();

                                            } else {

                                                Toast.makeText(SingleOrderActivity.this, "Try Again!.", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });

                                }

                            }
                        }).create().show();


            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(SingleOrderActivity.this)
                        .setMessage("COMPLETE Order? are you sure?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {

                                progressbar.setVisibility(View.VISIBLE);

                                if (customerPaymentType.getText().toString().isEmpty()) {

                                    progressbar.setVisibility(View.GONE);
                                    customerPaymentType.setError("error");
                                    customerPaymentType.requestFocus();
                                    return;

                                } else {


                                    Date today = new Date();
                                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                                    dateToStr = format.format(today);

                                    moveGameRoom(FirebaseDatabase.getInstance().getReference().child("Orders List").child("User View").child(userId), FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(my_user_id).child("CompletedOrders").child(dateToStr + "(" + userId + ")"));
                                    progressbar.setVisibility(View.GONE);

                                }


                            }
                        }).create().show();


            }
        });
    }

    private void moveGameRoom(DatabaseReference fromPath, DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError == null) {

                            Map userMap = new HashMap();
                            userMap.put("resPayAmount", res_pay.getText().toString());
                            userMap.put("amountStatus", status.getText().toString());
                            userMap.put("customerPaymentType", customerPaymentType.getText().toString());
                            userMap.put("Status", "Complete");
                            userMap.put("profitAmount", intTotal - Integer.parseInt(res_pay.getText().toString()));

                            FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(my_user_id).child("CompletedOrders").child(dateToStr + "(" + userId + ")").updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task task) {

                                    if (task.isSuccessful()) {

                                        FirebaseDatabase.getInstance().getReference().child("DeliveryBoys").child(my_user_id).child("Orders").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {


                                                finish();

                                                Toast.makeText(SingleOrderActivity.this, "Done", Toast.LENGTH_LONG).show();


                                            }
                                        });

                                    } else {

                                        Toast.makeText(SingleOrderActivity.this, "Try Again.", Toast.LENGTH_LONG).show();

                                    }

                                }
                            });


                        } else {
                            Toast.makeText(SingleOrderActivity.this, "Try Again.", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
