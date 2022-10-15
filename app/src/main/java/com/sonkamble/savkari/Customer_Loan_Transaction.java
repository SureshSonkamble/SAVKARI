package com.sonkamble.savkari;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Customer_Loan_Transaction extends AppCompatActivity {
    ProgressBar pgb;
    TransparentProgressDialog pd;
    TextView txt_synk,txt_ttl;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    Toolbar toolbar;
    ConnectionClass connection;
    private DatabaseHelper databaseHelper;
    PreparedStatement ps1;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    String con_ipaddress ,portnumber;
    double ttl=0.00;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_loan_tran);
        databaseHelper=new DatabaseHelper(this);
        connection = new ConnectionClass();
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_nm = (TextView) toolbar.findViewById(R.id.toolbar_nm);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Loan Transaction");
        toolbar_title.setTextColor(0xFFFFFFFF);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        txt_synk=(TextView)findViewById(R.id.txt_synk);
        txt_ttl=(TextView)findViewById(R.id.txt_ttl);
        pgb=(ProgressBar)findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(Customer_Loan_Transaction.this, R.drawable.transaction);
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new LinearLayoutManager(Customer_Loan_Transaction.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Customer_Loan_Transaction.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        txt_synk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menu_card_arryList.isEmpty())
                {
                     txt_synk.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No Data found.", Toast.LENGTH_SHORT).show();
                }else
                {
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                try {
                    //---------------------------
                    cd = new ConnectionDetector(getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {

                        SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
                        con_ipaddress = sp1.getString("ipaddress", "");
                        portnumber = sp1.getString("portnumber", "");

                        Connection con = connection.CONN(con_ipaddress,portnumber);
                        if (con == null) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                        } else {
                            databaseHelper = new DatabaseHelper(Customer_Loan_Transaction.this);
                            menu_card_arryList.clear();
                            int cnt=0;
                            Cursor c = databaseHelper.show_custtran();
                            if (c.getCount() != 0) {
                                c.moveToFirst();
                                do {

                                    String doc_no = c.getString(1);
                                    String doc_dt = c.getString(2);
                                    String ac_head_id = c.getString(3);
                                    String amount = c.getString(4);
                                    String loac_doc_no = c.getString(5);
                                    String loan_doc_dt = c.getString(6);

                                    // ps1 = con.prepareStatement("INSERT INTO custtran (doc_no,doc_dt,ac_head_id,amount,loan_doc_no,loan_doc_dt) VALUES(" + doc_no + ",'" + doc_dt + "'," + ac_head_id + "," + amount + "," + loac_doc_no + ",'" + loan_doc_dt + "')");
                                    ps1 = con.prepareStatement("INSERT INTO custtran (doc_no,doc_dt,ac_head_id,amount,loan_doc_no,loan_doc_dt,comp_code,apk_yn) VALUES(" + doc_no + ",'" + doc_dt + "'," + ac_head_id + "," + amount + "," + loac_doc_no + ",'" + loan_doc_dt + "',1,1)");
                                    ps1.executeUpdate();

                                    ps1 = con.prepareStatement("UPDATE DOC_NO SET LOAN_DOC_NO=" + loac_doc_no + " ,CUSTTRAN_DOCNO=" + doc_no + "");
                                    ps1.executeUpdate();

                                    cnt++;
                                } while (c.moveToNext());
                                if(cnt>0)
                                {
                                    pd.dismiss();
                                    success();
                                    databaseHelper.delete_custtran();
                                }
                                else
                                {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "Data Not Saved", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), "No Data found for list ...", Toast.LENGTH_SHORT).show();
                            }
                       //     Toast.makeText(getApplicationContext(), "Success..!", Toast.LENGTH_LONG).show();
                        }  //z = "Success";


                    }
                    else
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Customer_Loan_Transaction.this);
                            alertDialog.setTitle("WiFi Connection Error");
                            alertDialog.setMessage("Would You Like To Try Again.");
                            alertDialog.setIcon(R.drawable.fail);

                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(),Customer_List.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            //alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.show();
                            //alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setCancelable(false);
                        }

                } catch (Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Exception" + e, Toast.LENGTH_SHORT).show();
                }
                    }
                }, 2000);
                 //   pd.dismiss();
                  //  Toast.makeText(Customer_Loan_Transaction.this, "WiFi Connection Error", Toast.LENGTH_SHORT).show();
            }
            }
        });
        report_search();
    }
    public void report_search() {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            menu_card_arryList.clear();
           // Cursor c= databaseHelper.show_custtran();
            Cursor c= databaseHelper.custtran_data();
            ttl=0.00;
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    Log.d("ssss","in");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("id", c.getString(0));
                    map.put("doc_no", c.getString(1));
                    map.put("doc_dt", c.getString(2));
                    map.put("ac_head_id", c.getString(3));
                    map.put("name", c.getString(4));
                    map.put("amount", c.getString(5));
                    map.put("ldno", c.getString(6));
                    ttl=ttl+Double.parseDouble(c.getString(5));
                    txt_ttl.setText(""+ttl);
                    menu_card_arryList.add(map);

                } while (c.moveToNext());
            }
            else{
                Toast.makeText(getApplicationContext(),"No Data found for list ...",Toast.LENGTH_SHORT).show();
            }
            pgb.setVisibility(View.GONE);
            Log.d("Attendance_End_Data", "" + menu_card_arryList.toString());

            if (attendance_recyclerAdapter != null) {
                attendance_recyclerAdapter.notifyDataSetChanged();
                System.out.println("Adapter " + attendance_recyclerAdapter.toString());
            }

        } catch (Exception e) {
            Toast.makeText(Customer_Loan_Transaction.this, "Error.." + e, Toast.LENGTH_SHORT).show();
        }
    }

    public class atnds_recyclerAdapter extends RecyclerView.Adapter<atnds_recyclerAdapter.Pex_ViewHolder> {
        Context context;
        ArrayList<HashMap<String, String>> attendance_list;

        public atnds_recyclerAdapter(Context context, ArrayList<HashMap<String, String>> antds_list) {
            this.attendance_list = antds_list;
            this.context = context;
        }

        @Override
        public Pex_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_loan_tran, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.list_d1.setText(attendance_list.get(position).get("doc_no"));
            holder.list_d2.setText(attendance_list.get(position).get("doc_dt"));
            holder.list_d3.setText(attendance_list.get(position).get("name"));
            holder.list_d4.setText(attendance_list.get(position).get("amount"));
            holder.list_d5.setText(attendance_list.get(position).get("ldno"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            int id=Integer.parseInt(attendance_list.get(position).get("id"));
                            databaseHelper.delete_cust_tran(id);
                            delete(attendance_list.get(position).get("ac_head_id"));
                            pd.dismiss();
                        }
                    }, 2000);

                }
            });
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }

        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4,list_d5, list_item_type;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
                this.list_d5 = (TextView) itemView.findViewById(R.id.list_d5);

            }
        }
    }
    void success()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Customer_Loan_Transaction.this);
        alertDialog.setTitle("Success");
        alertDialog.setMessage("Data Saved Successfully..!.");
        alertDialog.setIcon(R.drawable.check);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(),Customer_List.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        //alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
    }

    void delete(String cid)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Customer_Loan_Transaction.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.warn);
        builder.setMessage("Are You Sures ? Do You Want To Delete The Record.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               // Toast.makeText(getApplicationContext(), "" + cid , Toast.LENGTH_SHORT).show();
                try {

                  // databaseHelper.update_cust();
                    databaseHelper.update_del_cust(cid);
                   Intent i=new Intent(getApplicationContext(),Customer_Loan_Transaction.class);
                   startActivity(i);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();


            }
        });
        builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


}

