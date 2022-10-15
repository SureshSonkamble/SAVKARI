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
import android.text.TextUtils;
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
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Customer_List extends AppCompatActivity {
    int ldno,cdno;
    ProgressBar pgb;
    TransparentProgressDialog pd;
    DecimalFormat df2;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;

    String str_name,str_ac_head_id, str_ip_addr,str_port_number,formattedDate,Query_date;
    String doc_dt,loan_doc_no,emi;
    ConnectionClass connection;
    Toolbar toolbar;
    LinearLayout lin_synk_data,lin_view_data;
    private DatabaseHelper databaseHelper;
    ConnectionDetector cd;
    Boolean isInternetPresent = false;
    EditText edt_port,edt_ip;
    Button btn_save,btn_cancel;
    AlertDialog dialog;
    SharedPreferences sp;
    String con_ipaddress ,portnumber,SubCodeStr;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        connection = new ConnectionClass();
        databaseHelper=new DatabaseHelper(this);
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("~सावकारी~");
        toolbar_title.setTextColor(0xFFFFFFFF);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        Date  d = Calendar.getInstance().getTime();

        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Query_date=out.format(d);

        //Connection con = connection.CONN();
       // con = connectionClass.CONN(con_ipaddress,portnumber);

       /* SharedPreferences ss = getSharedPreferences("COMP_DESC", MODE_PRIVATE);
        m_compcode = ss.getInt("COMP_CODE", 0);

        SharedPreferences sp = getSharedPreferences("IMEINumber", MODE_PRIVATE);
        IMEINumber = sp.getString("IMEINumber", "");
        SharedPreferences s = getSharedPreferences("TAB_DATA", MODE_PRIVATE);
        str_waiter = s.getString("tab_user_name", "");
        tab_user_code = s.getString("tab_user_code", "");
        m_TAB_CODE=Double.parseDouble(tab_user_code);
*/
        lin_view_data=(LinearLayout) findViewById(R.id.lin_view_data);
        lin_view_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Customer_Loan_Transaction.class);
                startActivity(i);
            }
        });
        lin_synk_data=(LinearLayout) findViewById(R.id.lin_synk_data);
        pgb=(ProgressBar)findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(Customer_List.this, R.drawable.transaction);
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new LinearLayoutManager(Customer_List.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Customer_List.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);
        //report_search("");
        //------------------------------------------------------------------------------------------
         searchView=(SearchView)findViewById(R.id.report_searchView);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 0) {
                    SubCodeStr = newText;
                    // SubCodeStr = SubCodeStr.replaceAll(" ", "%" + " ").toLowerCase();
                    //subcodestr = subcodestr.replaceAll("\\s+", "% ").toLowerCase();
                    Log.d("ssss", SubCodeStr);
                    //new FetchSearchResult().execute();
                    cust_list(SubCodeStr);
                } else if (TextUtils.isEmpty(newText)) {

                } else {
                    cust_list(SubCodeStr);
                }
                return false;
            }
        });
       lin_synk_data.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               {
                   //---------------------------
                   cd = new ConnectionDetector(getApplicationContext());
                   isInternetPresent = cd.isConnectingToInternet();
                   if (isInternetPresent)
                   {
                       ip_popup_form();
                       SharedPreferences sp1 = getSharedPreferences("IPADDR", MODE_PRIVATE);
                       con_ipaddress = sp1.getString("ipaddress", "");
                       portnumber = sp1.getString("portnumber", "");

                       if (attendance_recyclerAdapter != null) {
                           attendance_recyclerAdapter.notifyDataSetChanged();
                           System.out.println("Adapter " + attendance_recyclerAdapter.toString());
                       }
                   }
                   else
                   {
                       AlertDialog.Builder alertDialog = new AlertDialog.Builder(Customer_List.this);
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
               }
           }
       });
        cust_list("");
    }

    public void ip_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.ip_popup_form, null);

        edt_ip = (EditText) alertLayout.findViewById(R.id.edt_ip);
        edt_port = (EditText) alertLayout.findViewById(R.id.edt_port);

        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_ip_addr=edt_ip.getText().toString();
                str_port_number=edt_port.getText().toString();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("IPADDR", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("ipaddress", str_ip_addr);
                editor.putString("portnumber", str_port_number);
                editor.commit();
                dialog.dismiss();
                //--------------------------------------------------------
                //-------------------------------
                PreparedStatement ps;
                ResultSet rs;
                try {
                    Connection con = connection.CONN(con_ipaddress,portnumber);
                    if (con == null) {
                        Toast.makeText(getApplicationContext(), "Error in connection with SQL server", Toast.LENGTH_SHORT).show();
                    } else {
                        //-------Delete Custtran------------------
                        databaseHelper.delete_custtran();
                        //---------------Load GLMast-----------------------
                        ps = con.prepareStatement("select ac_head_id,gl_desc from glmast where ac_head_id in(select ac_head_id from custwiseloan where bal_amount>0)  order by gl_desc");
                        //ps = con.prepareStatement("select ac_head_id,gl_desc from glmast   order by gl_desc");
                        rs = ps.executeQuery();
                        databaseHelper.delete_lgmast();
                        while (rs.next()) {
                            String  desc = rs.getString("gl_desc");
                            float id = Float.parseFloat(rs.getString("ac_head_id"));
                            databaseHelper.insert_glmast(id, desc);
                        }
                        Log.d("ssss","GLMast");
                        //---------------Load Custloan-----------------------
                        ps = con.prepareStatement("select doc_dt, doc_no,ac_head_id, str(amount,10,2) as amount, str(bal_amount,10,2) as bal_amount, str(emi_amount,10,2) as emi_amount,loan_type from custwiseloan");
                        rs = ps.executeQuery();
                        databaseHelper.delete_cust_laon();
                        while (rs.next()) {
                            String doc_dt = rs.getString("doc_dt");
                            Log.d("ddddc",rs.getString("doc_dt"));
                            float doc_no = Float.parseFloat(rs.getString("doc_no"));
                            String id = rs.getString("ac_head_id").replaceAll("\\s", "");
                            String amt = rs.getString("amount").replaceAll("\\s", "");
                            String bal_amt = rs.getString("bal_amount").replaceAll("\\s", "");
                            String emi_amt = rs.getString("emi_amount").replaceAll("\\s", "");

                            String  loan_type = rs.getString("loan_type").replaceAll("\\s", "");
                            databaseHelper.insert_custloan(doc_dt,doc_no, id,amt,bal_amt,emi_amt,loan_type);
                            Log.d("ssss","Custloan");
                        }
                        //---------------Load Docno-----------------------
                        ps = con.prepareStatement("select loan_doc_no,custtran_docno from doc_no");
                        rs = ps.executeQuery();
                        databaseHelper.delete_docno();
                        while (rs.next()) {
                            float ldno = Float.parseFloat(rs.getString("loan_doc_no"));
                            float cdno = Float.parseFloat(rs.getString("custtran_docno"));
                            databaseHelper.insert_docno(ldno, cdno);
                            Log.d("ssss","Docno");
                        }
                    }  //z = "Success";
                  //  Toast.makeText(getApplicationContext(), "Data Loaded Successfully", Toast.LENGTH_LONG).show();
                    success();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Exception"+e, Toast.LENGTH_SHORT).show();
                }
                //---------------------------------------------------------
            }
        });

        btn_cancel = (Button) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(Customer_List.this);
        alert.setView(alertLayout);

        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }

    public void cust_list(String desc) {

        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();
            databaseHelper=new DatabaseHelper(this);
            menu_card_arryList.clear();
            Cursor c= databaseHelper.show_glmast(desc);
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                   // String id  =c.getString(0);
                    String ac_head_id  =c.getString(0);
                    String gldesc  =c.getString(1);
                    String emi  =c.getString(2);

                  //  map.put("id", id );
                    map.put("ac_head_id", ac_head_id );
                    map.put("gl_desc", gldesc );
                    map.put("emi", emi );
                    menu_card_arryList.add(map);
                    Log.d("aaaa",ac_head_id);

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
            Toast.makeText(Customer_List.this, "Error.." + e, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_list_, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {
            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.list_d1.setText(attendance_list.get(position).get("gl_desc"));
            holder.list_d2.setText(attendance_list.get(position).get("emi"));
           // holder.list_d3.setText(attendance_list.get(position).get("plac_desc"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            df2 = new DecimalFormat("#.##");
                            str_ac_head_id=attendance_list.get(position).get("ac_head_id");

                            collect_emi();

                            pd.dismiss();
                        }
                    }, 2000);
                }
            });

            holder.lin.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    df2 = new DecimalFormat("#.##");
                    str_ac_head_id=attendance_list.get(position).get("ac_head_id");
                    str_name=attendance_list.get(position).get("gl_desc");
                    // insert_data(Temp_frm_date,Temp_to_date);
                    Intent i=new Intent(Customer_List.this,Customer_Loan.class);
                    i.putExtra("ac_head_id", str_ac_head_id);
                    i.putExtra("forname", str_name);

                    startActivity(i);
                    pd.dismiss();
                    return false;
                }
            });
        }
        @Override
        public int getItemCount() {
            return attendance_list.size();
        }
        public class Pex_ViewHolder extends RecyclerView.ViewHolder {
            TextView list_d1, list_d2, list_d3, list_d4, list_item_type;
            LinearLayout lin;
            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                //this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                //this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);
            }
        }
    }
    void collect_emi( )
    {
        pd.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                str_ac_head_id=str_ac_head_id+".0";
                Cursor c= databaseHelper.show_custwise_loan(str_ac_head_id);
                // Cursor c= databaseHelper.show_custwise_loan("761.0");
                if (c.getCount() != 0) {
                    c.moveToFirst();
                    do {
                        Log.d("ssss","in");
                        doc_dt=c.getString(1);
                        loan_doc_no=c.getString(2);
                        emi=c.getString(6);

                    } while (c.moveToNext());
                }
                else{
                    Toast.makeText(getApplicationContext(),"No Data found for list ...",Toast.LENGTH_SHORT).show();
                }

                //==============update docno========================
                try {
                    Cursor cd = databaseHelper.show_doc_no();
                    if (cd.getCount() != 0) {
                        cd.moveToFirst();
                        do {
                            ldno = Integer.parseInt(cd.getString(1));
                            cdno = Integer.parseInt(cd.getString(2));
                        } while (cd.moveToNext());

                        Log.d("doc_no", " " + ldno + "\n" + cdno);
                        databaseHelper.update_Docno(ldno, cdno + 1);
                        //    Log.d("doc_no", "after " + ldno + "\n" + cdno);
                    }
                    Cursor cc = databaseHelper.show_doc_no();
                    if (cc.getCount() != 0) {
                        cc.moveToFirst();
                        do {
                            ldno = Integer.parseInt(cc.getString(1));
                            cdno = Integer.parseInt(cc.getString(2));
                        } while (cc.moveToNext());

                        Log.d("doc_no", " " + ldno + "\n" + cdno);
                        //  databaseHelper.update_Docno(ldno, cdno + 1);
                        //    Log.d("doc_no", "after " + ldno + "\n" + cdno);
                    }
                    //-----------------Save-------------------------------------
                    databaseHelper.insert_custtran(cdno,Query_date,str_ac_head_id, emi,Integer.parseInt(loan_doc_no),doc_dt);


                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"EMI"+e, Toast.LENGTH_SHORT).show();
                }
                //save_data();
            }
        }, 1000);

        Toast.makeText(Customer_List.this, "E.M.I Collected Successfully.", Toast.LENGTH_SHORT).show();
      //  databaseHelper.delete_cust(str_ac_head_id);
       databaseHelper.update_cust(str_ac_head_id);
        pd.dismiss();
        refresh();
    }
   void success()
   {
       AlertDialog.Builder alertDialog = new AlertDialog.Builder(Customer_List.this);
       alertDialog.setTitle("Success");
       alertDialog.setMessage("Data Loaded Successfully..!.");
       alertDialog.setIcon(R.drawable.check);

       alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               Intent intent = new Intent(getApplicationContext(),Customer_List.class);
               startActivity(intent);
               finish();
               cust_list("");
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

   void refresh(){
        Intent i=new Intent(getApplicationContext(),Customer_List.class);
        startActivity(i);
        finish();
   }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Customer_List.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.warn);
        builder.setMessage("Are You Sure,You Want To Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

    @Override
    protected void onResume() {
        cust_list("");
        super.onResume();
    }
}
