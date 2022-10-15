package com.sonkamble.savkari;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
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
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Customer_Loan extends AppCompatActivity {
    int mYear, mMonth, mDay;
    ImageView img_frm_date,img_to_date;
    TextView edt_date;
    DatePickerDialog datePickerDialog;
    SearchView searchView;
    Button btn_report;
    String SubCodeStr,db;
    ProgressBar pgb;
    PreparedStatement ps1;
    Connection con = null;
    String formattedDate,Query_date,Temp_to_date,str_month="",str_day="";
    TransparentProgressDialog pd;
    DecimalFormat df2;
    //================Recyclerview 1======================
    ArrayList<HashMap<String, String>> menu_card_arryList;
    private RecyclerView.LayoutManager layoutManager_pe;
    atnds_recyclerAdapter attendance_recyclerAdapter;
    private RecyclerView recycler_medal_offline_rpt_list;
    int ldno,cdno;
    double m_clbal,cbal;
    String ac_head_id,forname;
    double m_TAB_CODE;
    String emi,type,doc_dt,loan_doc_no,str_ac_head_id;
    ConnectionClass connection;
    Toolbar toolbar;
    AlertDialog dialog;
    ImageView btn_cancel;
    Button btn_save;
    EditText edt_emi;
    TextView txt_emi,txt_type;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_loan);
        connection = new ConnectionClass();
        databaseHelper=new DatabaseHelper(this);
         Bundle b=getIntent().getExtras();
         try {
             forname=b.getString("forname");
             ac_head_id=b.getString("ac_head_id");
         }catch (Exception e){
                      }
        //------------------------Toolbar-------------------------------------------
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);//title
        TextView toolbar_nm = (TextView) toolbar.findViewById(R.id.toolbar_nm);//title
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar_title.setText("Customer Wise Loan");
        toolbar_title.setTextColor(0xFFFFFFFF);
        toolbar_nm.setText(forname);
        toolbar_title.setTextColor(0xFFFFFFFF);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        pgb=(ProgressBar)findViewById(R.id.pgb);
        pd = new TransparentProgressDialog(Customer_Loan.this, R.drawable.transaction);
        //---------------------Recyclerview 1-----------------------------------------
        menu_card_arryList = new ArrayList<HashMap<String, String>>();
        recycler_medal_offline_rpt_list = (RecyclerView) findViewById(R.id.recycler_cust_list);
        layoutManager_pe = new LinearLayoutManager(Customer_Loan.this, RecyclerView.VERTICAL, false);
        recycler_medal_offline_rpt_list.setLayoutManager(layoutManager_pe);
        attendance_recyclerAdapter = new atnds_recyclerAdapter(Customer_Loan.this, menu_card_arryList);
        recycler_medal_offline_rpt_list.setAdapter(attendance_recyclerAdapter);

        final Calendar cd = Calendar.getInstance();
        mYear = cd.get(Calendar.YEAR);
        mMonth = cd.get(Calendar.MONTH);
        mDay = cd.get(Calendar.DAY_OF_MONTH);
        edt_date=(TextView) findViewById(R.id.edt_date);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        formattedDate = df.format(c);
        edt_date.setText(formattedDate);
        Date  d = Calendar.getInstance().getTime();

        SimpleDateFormat out = new SimpleDateFormat("MM/dd/yyyy");
        Query_date=out.format(d);

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Customer_Loan.this,R.style.AppCompatAlertDialogStyle,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                str_month="";
                                str_day="";
                                int m_month=monthOfYear+1;
                                str_month= "00"+m_month;
                                str_day= "00"+dayOfMonth;
                                str_month = str_month.substring(str_month.length()-2);
                                str_day = str_day.substring(str_day.length()-2);
                                edt_date.setText(""+str_day + "/" + str_month + "/" + year);
                                Query_date=""+(monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                                //edt_frm_date.setText(""+dayOfMonth + "/" +  (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        report_search();
    }
    public void report_search() {
        try {
            pgb.setVisibility(View.VISIBLE);
            // sp_data  = new ArrayList<Map<String, String>>();

            menu_card_arryList.clear();
           // Cursor c= databaseHelper.show_custwise_loan();
            ac_head_id=ac_head_id+".0";
            Cursor c= databaseHelper.show_custwise_loan(ac_head_id);
           // Cursor c= databaseHelper.show_custwise_loan("761.0");
            if (c.getCount() != 0) {
                c.moveToFirst();
                do {
                    Log.d("ssss","in");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("doc_dt", c.getString(1));
                    Log.d("dddd",c.getString(1));
                    map.put("doc_no", c.getString(2));
                    map.put("ac_head_id", c.getString(3));
                    map.put("amount", c.getString(4));
                    map.put("bal_amount", c.getString(5));
                    map.put("emi_amount", c.getString(6));
                    map.put("loan_type", c.getString(7));
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
            Toast.makeText(Customer_Loan.this, "Error.." + e, Toast.LENGTH_SHORT).show();
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_loan, parent, false);
            Pex_ViewHolder viewHolder = new Pex_ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final Pex_ViewHolder holder, final int position) {

            // holder.list_d1.setText(attendance_list.get(position).get("1"));
            holder.list_d1.setText(attendance_list.get(position).get("amount"));
            holder.list_d2.setText(attendance_list.get(position).get("bal_amount"));
            holder.list_d3.setText(attendance_list.get(position).get("emi_amount"));
            holder.list_d4.setText(attendance_list.get(position).get("loan_type"));
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pd.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            df2 = new DecimalFormat("#.##");
                            str_ac_head_id=attendance_list.get(position).get("ac_head_id");
                            loan_doc_no=attendance_list.get(position).get("doc_no");
                            emi=attendance_list.get(position).get("emi_amount");
                            type=attendance_list.get(position).get("loan_type");
                            doc_dt=attendance_list.get(position).get("doc_dt");
                            emi_popup_form();
                          //  Toast.makeText(getApplicationContext(), ""+str_ac_head_id+"\n"+loan_doc_no, Toast.LENGTH_SHORT).show();
                           // insert_data(Temp_frm_date,Temp_to_date);
                           /* Intent i=new Intent(Customer_List.this,Customer_ledger_Report.class);
                            i.putExtra("from_date", edt_frm_date.getText().toString());
                            i.putExtra("to_date", edt_to_date.getText().toString());
                            i.putExtra("m_clbal", df2.format(m_clbal));
                            i.putExtra("forname", attendance_list.get(position).get("gl_desc"));

                            startActivity(i);*/
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
            TextView list_d1, list_d2, list_d3, list_d4, list_item_type;
            LinearLayout lin;

            public Pex_ViewHolder(View itemView) {
                super(itemView);
                this.lin = (LinearLayout) itemView.findViewById(R.id.lin);
                this.list_d1 = (TextView) itemView.findViewById(R.id.list_d1);
                this.list_d2 = (TextView) itemView.findViewById(R.id.list_d2);
                this.list_d3 = (TextView) itemView.findViewById(R.id.list_d3);
                this.list_d4 = (TextView) itemView.findViewById(R.id.list_d4);

            }
        }
    }
    //============popup=========================
    public void  emi_popup_form() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.add_emi, null);

        txt_emi = (TextView) alertLayout.findViewById(R.id.txt_emi);
        txt_emi.setText(emi);
        txt_type = (TextView) alertLayout.findViewById(R.id.txt_type);
        txt_type.setText(type);
        edt_emi = (EditText) alertLayout.findViewById(R.id.txt_emi_amt);
        edt_emi.setText(emi.substring( 0, emi.indexOf(".")));
        edt_emi.setSelectAllOnFocus(true);

        btn_save = (Button) alertLayout.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  exp_note = edit_note.getText().toString();
                //   exp_amt = edit_exp_amt.getText().toString();
                pd.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //==============update docno========================
                        try {
                            Cursor c = databaseHelper.show_doc_no();
                            if (c.getCount() != 0) {
                                c.moveToFirst();
                                do {
                                    ldno = Integer.parseInt(c.getString(1));
                                    cdno = Integer.parseInt(c.getString(2));
                                } while (c.moveToNext());

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
                            databaseHelper.insert_custtran(cdno,Query_date,str_ac_head_id, edt_emi.getText().toString(),Integer.parseInt(loan_doc_no),doc_dt);
                           /* ps1 = con.prepareStatement("UPDATE DOC_NO SET LOAN_DOC_NO=LOAN_DOC_NO+1 ,CUSTTRAN_DOCNO=CUSTTRAN_DOCNO+1 where '" + Query_date + "' BETWEEN from_year and to_year");
                            ps1.executeUpdate();
                            ps1 = con.prepareStatement("SELECT LOAN_DOC_NO,CUSTTRAN_DOCNO FROM DOC_NO where '" + Query_date + "' BETWEEN from_year and to_year");
                            ResultSet s = ps1.executeQuery();
                            while (s.next()) {
                              //  doc_no=Double.parseDouble(s.getString("COUNTER_SALE_DOCNO"));
                                //  ps1 = con.prepareStatement("INSERT INTO COUNTERSALEITEM (DOC_NO,DOC_DT,ITEM_CODE,QTY,RATE,ITEM_VALUE,DOC_SRNO,LOCT_CODE,STOCK_METHOD,COMP_CODE,MRP,CASHMEMO_PRICE,DISCOUNT_PER,DISCOUNT_AMOUNT,NET_AMOUNT,PAID_AMOUNT,ADD_AMOUNT,AC_HEAD_ID) VALUES(" + s.getString("COUNTER_SALE_DOCNO") + ",'" + Query_date + "','" + map.get("item_code") + "'," + map.get("qty") + "," + map.get("mrp") + "," + map.get("value") + " , " + m_counter_sale_doc_srno + ",1,1,1," + map.get("mrp") + "," + map.get("mrp") + ","+str_disc+","+str_dis_amt+","+str_net_sale+","+str_paid_amt+","+str_add_amt+","+m_customer_code+")");
                                ps1 = con.prepareStatement("INSERT INTO custtran (doc_no,doc_dt,ac_head_id,amount,loac_doc_no,loan_doc_dt) VALUES("+ s.getString("CUSTTRAN_DOCNO")  +",'"+Query_date+"',"+str_ac_head_id+","+emi+","+ doc_no + ",'" + Query_date + "')");
                                ps1.executeUpdate();
                            }*/
                            databaseHelper.update_cust(str_ac_head_id);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),""+e, Toast.LENGTH_SHORT).show();
                        }
                        //save_data();
                    }
                }, 1000);
                dialog.dismiss();
                Toast.makeText(Customer_Loan.this, "E.M.I Collected Successfully.", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

        btn_cancel = (ImageView) alertLayout.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        AlertDialog.Builder alert = new AlertDialog.Builder(Customer_Loan.this);
        alert.setView(alertLayout);
        dialog = alert.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }
}