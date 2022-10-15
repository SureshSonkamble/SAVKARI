package com.sonkamble.savkari;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Suresh on 7/11/2020.
 */
public class ConnectionClass {


   // String ip = "192.168.29.151:2222";
  //  String ip = "192.168.29.216:1433";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String db = "SAVKARI";
    String un = "SA";
    String password = "PIMAGIC";
    /* String ip = "P3NWPLSK12SQL-v05.shr.prod.phx3.secureserver.net";
     String classs = "net.sourceforge.jtds.jdbc.Driver";
     String db = "ScarletIndia_yppunde";
     String un = "yppundeuser";
     String password = "klbS119@";
 */
    @SuppressLint("NewApi")

    public Connection CONN(String ip, String port) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()

                .permitAll().build();

        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;

        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databaseName=" + db + ";user=" + un + ";password=" + password + ";";
           /* ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"

                    + "databaseName=" + db + ";user=" + un + ";password="

                    + password + ";";*/

            conn = DriverManager.getConnection(ConnURL);
            Log.d("conn",""+conn);
        } catch (SQLException se) {

            Log.e("ERRO", se.getMessage());

        } catch (ClassNotFoundException e) {

            Log.e("ERRO", e.getMessage());

        } catch (Exception e) {

            Log.e("ERRO", e.getMessage());

        }

        return conn;

    }

}