package com.sonkamble.savkari;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="loan.db";
    private static final String TABLE_CUSTTRAN="custtran";
    private static final String TABLE_DOCNO="docno";
    private static final String TABLE_GLMAST="glmast";
    private static final String TABLE_CUSTWISELOAN="custwiseloan";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
       // SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       // sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_CUSTTRAN+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,ADDR TEXT,EMAIL TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_CUSTTRAN+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT,DOC_NO INTEGER,DOC_DT TEXT,AC_HEAD_ID TEXT,AMOUNT TEXT,LOAN_DOC_NO INTEGER,LOAN_DOC_DT TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_DOCNO+ "(DOC_NO INTEGER PRIMARY KEY AUTOINCREMENT,LOAN_DOC_NO REAL ,CUSTTRAN_DOCNO REAL )");
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_GLMAST+ "(GID INTEGER PRIMARY KEY AUTOINCREMENT,AC_HEAD_ID REAL ,GLDESC TEXT,DEL_YN INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE " +TABLE_CUSTWISELOAN+ "(CID INTEGER PRIMARY KEY AUTOINCREMENT,DOC_DT TEXT, DOC_NO REAL,AC_HEAD_ID TEXT ,AMOUNT TEXT,BAL_AMOUNT TEXT,EMI_AMOUNT TEXT,LOAN_TYPE TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_CUSTTRAN);
            onCreate(sqLiteDatabase);
    }
    //===============Load  Data=======================
    public  boolean insert_custtran(int dno,String dt,String ac_head_id,String amt,int ldno,String ldt)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DOC_NO",dno);
        contentValues.put("DOC_DT",dt);
        contentValues.put("AC_HEAD_ID",ac_head_id);
        contentValues.put("AMOUNT",amt);
        contentValues.put("LOAN_DOC_NO",ldno);
        contentValues.put("LOAN_DOC_DT",ldt);
        Long result = sqLiteDatabase.insert(TABLE_CUSTTRAN,null,contentValues);
        if (result==-1)
            return  false;
        else
            return true;
    }
    public  boolean insert_custloan(String doc_dt ,Float dno,String ac_head_id,String amt,String bl_amt,String emi_amt,String ltype)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DOC_DT",doc_dt);
        contentValues.put("DOC_NO",dno);
        contentValues.put("AC_HEAD_ID",ac_head_id);
        contentValues.put("AMOUNT",amt);
        contentValues.put("BAL_AMOUNT",bl_amt);
        contentValues.put("EMI_AMOUNT",emi_amt);
        contentValues.put("LOAN_TYPE",ltype);
        Long result = sqLiteDatabase.insert(TABLE_CUSTWISELOAN,null,contentValues);
        if (result==-1)
            return  false;
        else
            return true;
    }
    public  boolean insert_glmast(Float ac_head_id,String gldesc)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("AC_HEAD_ID",ac_head_id);
        contentValues.put("GLDESC",gldesc);
        contentValues.put("DEL_YN",0);
        Long result = sqLiteDatabase.insert(TABLE_GLMAST,null,contentValues);
        if (result==-1)
            return  false;
        else
            return true;
    }
    public  boolean insert_docno(Float ldno,Float cdno)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("LOAN_DOC_NO",ldno);
        contentValues.put("CUSTTRAN_DOCNO",cdno);
        Long result = sqLiteDatabase.insert(TABLE_DOCNO,null,contentValues);
        if (result==-1)
            return  false;
        else
            return true;
    }
//============SELECT========================================
    public Cursor show_custtran() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CUSTTRAN;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_glmast() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_GLMAST;
      //  String sql = "SELECT * FROM " +TABLE_GLMAST + " WHERE GLDESC" + " LIKE '%" + cat + "%'";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_glmast(String desc) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
       // String query = "SELECT * FROM " + TABLE_GLMAST;
        String query = "select "+TABLE_GLMAST+".AC_HEAD_ID ,GLDESC,EMI_AMOUNT from "+TABLE_GLMAST+","+TABLE_CUSTWISELOAN+" WHERE "+TABLE_GLMAST+".AC_HEAD_ID="+TABLE_CUSTWISELOAN+".AC_HEAD_ID AND   GLDESC" + " LIKE '%" + desc + "%' and DEL_YN=0";
        //  String query = "SELECT * FROM " +TABLE_GLMAST + " WHERE GLDESC" + " LIKE '%" + desc + "%'";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor custtran_data() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT "+TABLE_CUSTTRAN+".ID,"+TABLE_CUSTTRAN+".DOC_NO,DOC_DT,"+TABLE_CUSTTRAN+".AC_HEAD_ID,GLDESC,AMOUNT,LOAN_DOC_NO FROM " + TABLE_CUSTTRAN+","+ TABLE_GLMAST +" WHERE "+TABLE_GLMAST+".AC_HEAD_ID="+TABLE_CUSTTRAN+".AC_HEAD_ID and DEL_YN=1";
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_doc_no() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM  " + TABLE_DOCNO;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_loan_doc_no() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM  " + TABLE_DOCNO;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_custwise_loan() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM  " + TABLE_CUSTWISELOAN;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null, null);
        return cursor;
    }
    public Cursor show_custwise_loan(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
       // String sql = "SELECT * FROM " +TABLE_CUSTWISELOAN + " WHERE AC_HEAD_ID" + " LIKE '%" + id + "%'";
        String sql = "SELECT * FROM " +TABLE_CUSTWISELOAN + " WHERE AC_HEAD_ID =" +id + "";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        return cursor;

    }


    public Cursor custwise_loan() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        // String sql = "SELECT * FROM " +TABLE_CUSTWISELOAN + " WHERE AC_HEAD_ID" + " LIKE '%" + id + "%'";
        String sql = "select "+TABLE_GLMAST+".AC_HEAD_ID ,GLDESC,EMI_AMOUNT from "+TABLE_GLMAST+","+TABLE_CUSTWISELOAN+" WHERE "+TABLE_GLMAST+".AC_HEAD_ID="+TABLE_CUSTWISELOAN+".AC_HEAD_ID";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        return cursor;

    }
    //--------------------------------------------------------------------------------------
    //==========Delete data where===========================
    public void delete_cust(String id)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
       sqLiteDatabase.delete(TABLE_GLMAST, " AC_HEAD_ID = " +id+ " ",null);
    }
    //==========Delete data all===========================
    public void delete_custtran()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_CUSTTRAN, null,null);
    }
    public void delete_cust_tran(int id)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_CUSTTRAN, " ID = " +id+ " ",null);
    }
    public void delete_cust_laon()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_CUSTWISELOAN, null,null);
    }
    public void delete_lgmast()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_GLMAST, null,null);
    }
    public void delete_docno()
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_DOCNO, null,null);
    }
//=======================UPDATE==============================================
    public void update_Docno(int ldno,int cdno)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("LOAN_DOC_NO",ldno);
        contentValues.put("CUSTTRAN_DOCNO",cdno);
        sqLiteDatabase.update(TABLE_DOCNO,contentValues, null,null);
    }

    public void update_cust(String id)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("DEL_YN",1);
        sqLiteDatabase.update(TABLE_GLMAST,contentValues, " AC_HEAD_ID = " +id+ "",null);
    }

    public void update_del_cust(String id)
    {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("DEL_YN",0);
        sqLiteDatabase.update(TABLE_GLMAST,contentValues, " AC_HEAD_ID = " +id+ "",null);
    }
//---------------------------------------------------------------------------------------------
    public Cursor show_cat_exp_Data(String cat) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //  String query =("SELECT * FROM " +TABLE_NAME+ );
    //    String query ="SELECT * FROM " +TABLE_EXP+ " WHERE CAT = " +cat+ " ";
        String sql = "SELECT * FROM " +TABLE_CUSTTRAN + " WHERE CAT" + " LIKE '%" + cat + "%'";
        //Cursor cursor = sqLiteDatabase.rawQuery(query,null);
      //  String query ="SELECT * FROM " +TABLE_DLVR+ " WHERE ID = " +cid+ " ";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

        return cursor;

    }
    public Cursor show_date_exp_Data(String date) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //  String query =("SELECT * FROM " +TABLE_NAME+ );
      //  String query ="SELECT * FROM " +TABLE_EXP+ " WHERE DATE = " +date+ " ";
        String sql = "SELECT * FROM " +TABLE_CUSTTRAN + " WHERE DATE" + " LIKE '%" + date + "%'";
        //Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        //  String query ="SELECT * FROM " +TABLE_DLVR+ " WHERE ID = " +cid+ " ";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

        return cursor;

    }
    public Cursor show_date_exp_Data_Wise(String date,String date2) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //  String query =("SELECT * FROM " +TABLE_NAME+ );
        //  String query ="SELECT * FROM " +TABLE_EXP+ " WHERE DATE = " +date+ " ";
        //String sql = "SELECT * FROM " +TABLE_EXP + " WHERE DATE" + " BETWEEN "+ date + "' AND '" + date2 "+
       // String sql = "select * from "+ TABLE_EXP + " where DATE BETWEEN " + date + "+ AND +" + date2 + "+ ORDER BY DATE ASC";
        String sql = "select * from "+ TABLE_CUSTTRAN + " where DATE BETWEEN " + "LIKE '%" + date + "%' + "+ "AND" +" LIKE '%" + date2 + "%'";

        //Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        //  String query ="SELECT * FROM " +TABLE_DLVR+ " WHERE ID = " +cid+ " ";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);

        return cursor;

    }

    }
