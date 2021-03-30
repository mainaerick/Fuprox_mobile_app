package com.fuprox.noqueue.utils;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import android.util.Log;

import com.fuprox.noqueue.model.JSONParser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;



public class Dbhelper extends SQLiteOpenHelper {

    public  static String TAG="";


    static ProgressDialog pDialog;
    // products JSONArray
    static JSONArray products = null;
    static ArrayList<HashMap<String, String>> productsList;

    // Creating JSON Parser object
    final JSONParser jParser = new JSONParser();

//    DATABASE DETAILS

private static final int DB_VERSION = 1;
    private static final String DB_NAME = "queue.db";

//    USER DETAILS
    private static final String user_table = "user";
    private static final String user_id = "_id";
    private static final String db_user_name = "name";
    private static final String db_user_email = "email";
    private static final String db_user_pass = "pass";
    private static final String db_user_id = "user_id";
    private static final String db_user_no = "user_no";

    private static final String SETTING_TABLE = "settings";
    private static final String SETTING_ID = "_id";
    private static final String SETTING_NOTIFICATIONDELAY = "notify_before";
    private static final String SETTING_NOTIFICATIONSTATE= "notify_state";
    private static final String SETTING_IPADDRESS= "ip_address";



//    BOOKING DETAILS

    public String
            booking_id="_id",
            company_name="company_name",
            branch_name="branch_name",
            booking_time="time_booked",
            booking_table="booking",
            db_booking_id="booking_id",
            db_branch_id="branch_id",
            db_service_name="service_name",
            booking_active="booking_status";

//    favourites details
    public String fav_id="_id",
    fav_branchname="branch_name",
    fav_branchid="branch_id",
    fav_branchlongitute="branch_long",
    fav_branchlatitude="branch_lat",
    fav_table="favourite_table";





    public Dbhelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_FAVOURITE="CREATE TABLE "+ fav_table +"("
                + fav_id+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + fav_branchname + " TEXT,"
                + fav_branchid + " TEXT,"
                + fav_branchlongitute + " TEXT,"
                + fav_branchlatitude + " TEXT"
                +")";


        String CREATE_TABLE_USERS = "CREATE TABLE " + user_table + "("
                + user_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + db_user_name + " TEXT,"
                + db_user_email + " TEXT,"
                + db_user_pass + " TEXT,"
                + db_user_id + " TEXT,"
                + db_user_no +" TEXT"
                +")";

        String CREATE_TABLE_BOOKING = "CREATE TABLE " + booking_table + "("
                + booking_id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + company_name + " TEXT,"
                + branch_name + " TEXT,"
                + booking_time + " TEXT,"
                + db_booking_id + " TEXT,"
                + db_branch_id + " TEXT,"
                + db_service_name + " TEXT,"
                + booking_active + " TEXT"
                + ")";

        String CREATE_TABLE_SETTINGS = "CREATE TABLE " + SETTING_TABLE + "("
                + SETTING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SETTING_NOTIFICATIONDELAY + " INTEGER,"
                + SETTING_NOTIFICATIONSTATE + " TEXT,"
                + SETTING_IPADDRESS + " TEXT"
                + ")";


        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_BOOKING);
        db.execSQL(CREATE_TABLE_SETTINGS);
        db.execSQL(CREATE_TABLE_FAVOURITE);

//        first_settings();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        switch (oldVersion) {
            case 1:
                db.execSQL("DROP TABLE IF EXISTS " + user_table);
            case 2:
                db.execSQL("DROP TABLE IF EXISTS " + booking_table);
            case 3:
                db.execSQL("DROP TABLE IF EXISTS " + SETTING_TABLE);
            case 4:
                db.execSQL("DROP TABLE IF EXISTS "+fav_table);
                break;
        }
        onCreate(db);
    }


    public boolean check_if_fav(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+fav_table +" where "+fav_branchid+" = "+id,null);

        return cursor.getCount() != 0;

    }
    public void insert_fav(fav_details fav_details){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(fav_branchid,fav_details.getBranchid());
        contentValues.put(fav_branchname,fav_details.getBranchname());
        contentValues.put(fav_branchlatitude,fav_details.getLatitude());
        contentValues.put(fav_branchlongitute,fav_details.getLongitude());

        db.insert(fav_table,null, contentValues);
        db.close();
    }
    public ArrayList<fav_details> get_fav(){
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<fav_details> fav_detailsArrayList = new ArrayList<>();
        fav_details fav_details;
        Cursor cursor = db.rawQuery("SELECT * FROM "+fav_table,null);

        if (cursor.getCount()==0){

        }

        while (cursor.moveToNext()){

            fav_details = new fav_details();
            fav_details.setId(cursor.getString(cursor.getColumnIndex(fav_id)));
            fav_details.setBranchid(cursor.getString(cursor.getColumnIndex(fav_branchid)));
            fav_details.setBranchname(cursor.getString(cursor.getColumnIndex(fav_branchname)));
            fav_details.setLatitude(cursor.getString(cursor.getColumnIndex(fav_branchlatitude)));
            fav_details.setLongitude(cursor.getString(cursor.getColumnIndex(fav_branchlongitute)));
            fav_detailsArrayList.add(fav_details);
        }
        return  fav_detailsArrayList;
    }
    public void delete_fav(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(fav_table, fav_id + "=" + id,null);
        db.close();
    }

    public String get_favid(String b_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+fav_table +" where "+fav_branchid+" = "+b_id,null);
        String favid = null;
        if (cursor.getCount()!=0){
            if (cursor.moveToFirst()){
                favid=cursor.getString(cursor.getColumnIndex(fav_id));
            }
        }
        Log.d(TAG, "get_favid: "+favid);
        return favid;
    }
    public void first_settings(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        Cursor cursor = db.rawQuery("SELECT * FROM "+SETTING_TABLE,null);
//        if (cursor.getCount()==0){
            contentValues.put(SETTING_NOTIFICATIONDELAY, 0);
            contentValues.put(SETTING_NOTIFICATIONSTATE, "1");
        contentValues.put(SETTING_IPADDRESS, "122");

        db.insert(SETTING_TABLE,null, contentValues);
//        }

        db.close();
    }
    public int getsettingsid() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+SETTING_TABLE,null);
        int id=0;
        if (cursor.moveToFirst()){
            id=cursor.getInt(cursor.getColumnIndex(SETTING_ID));
        }
        return id;
    }
    public int  getsettings(){
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<booking_details> booking_list = new ArrayList<>();
        booking_details booking_details;
        Cursor cursor = db.rawQuery("SELECT * FROM "+SETTING_TABLE,null);
        return cursor.getCount();
    }

    public String getnotistate(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+SETTING_TABLE,null);
        String state=" ";
        if (cursor.moveToFirst()){
            state=cursor.getString(cursor.getColumnIndex(SETTING_NOTIFICATIONSTATE));
        }
        return state;
    }
    public String get_ipaddress(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+SETTING_TABLE,null);
        String state=" ";
        if (cursor.moveToFirst()){
            state=cursor.getString(cursor.getColumnIndex(SETTING_IPADDRESS));
        }
        return state;
    }

    public int getnotidelay(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+SETTING_TABLE,null);
        int delay = 0;
        if (cursor.moveToFirst()){
            delay=cursor.getInt(cursor.getColumnIndex(SETTING_NOTIFICATIONDELAY));
        }
        return delay;
    }

    public void set_ipaddress(String ipaddress){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTING_IPADDRESS, ipaddress);
        db.update(SETTING_TABLE,contentValues, null,null);
        db.close();
    }

    public void setnotistate(String state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTING_NOTIFICATIONSTATE, state);
        db.update(SETTING_TABLE,contentValues, null,null);
        db.close();
    }
    public void setnotidelay(int delay){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTING_NOTIFICATIONDELAY, delay);
        db.update(SETTING_TABLE,contentValues, SETTING_ID+"="+getsettingsid(),null);
        db.close();
    }
    public void get_booking_online(booking_details booking_details){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(company_name, booking_details.getCo_name());
        contentValues.put(branch_name, booking_details.getB_name());
        contentValues.put(booking_time, booking_details.getTime());
        db.insert(booking_table,null, contentValues);
        db.close();
    }
    public int getactivebookings(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+booking_table+" where "+booking_active+"='1'",null);
        return cursor.getCount();
    }
    public void insert_booking(booking_details booking_details){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(company_name, booking_details.getCo_name());
        contentValues.put(branch_name, booking_details.getB_name());
        contentValues.put(booking_time, booking_details.getTime());
        contentValues.put(db_booking_id,booking_details.getBooking_id());
        contentValues.put(db_branch_id,booking_details.getBranch_id());
        contentValues.put(db_service_name,booking_details.getService_name());
        contentValues.put(booking_active,booking_details.getServiced());
        db.insert(booking_table,null, contentValues);
//        set an alarm
//        new SimpleAlarmManager(activity)
//                .setup(1,booking_milis)
//                .register(Integer.parseInt(obj.getString("booking_id")))
//                .start();
        db.close();
    }
    public void updateorder(String booking_id,String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(booking_active, status);
        db.update(booking_table,contentValues, db_booking_id+"="+booking_id,null);
        db.close();
    }
    public void update_pending_order(String token,String booking_id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(db_booking_id, booking_id);
        contentValues.put(booking_active, "1");

        Log.d(TAG, "update_pending_order:newtoken "+ token+ " token in db----"+token+"  booking id--"+booking_id);
        db.update(booking_table,contentValues, db_booking_id+"='"+token+"'",null);
        db.close();
    }

    public void delete_booking(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(booking_table, booking_id + "=" +id,null);
        db.close();
    }
    public void delete_all_booking(){
        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(booking_table, null,null);
        Cursor cursor = db.rawQuery("SELECT * FROM "+booking_table+" ORDER BY " + booking_time +";",null);

        if (cursor.moveToFirst()){
            do{
                db.delete(booking_table, booking_id + "=" + cursor.getInt(cursor.getColumnIndex(booking_id)),null);
            }while (cursor.moveToNext());
        }

        db.close();
    }

    public ArrayList<booking_details> get_booking(){

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<booking_details> booking_list = new ArrayList<>();
        booking_details booking_details;
        Cursor cursor = db.rawQuery("SELECT * FROM "+booking_table+" ORDER BY " + booking_time +" DESC;",null);

        if (cursor.getCount()==0){

        }
        while (cursor.moveToNext()){
            String co_name=cursor.getString(cursor.getColumnIndex(company_name));
            String b_name=cursor.getString(cursor.getColumnIndex(branch_name));
            String time=cursor.getString(cursor.getColumnIndex(booking_time));

            booking_details = new booking_details();
            booking_details.setId(cursor.getInt(cursor.getColumnIndex(booking_id)));
            booking_details.setCo_name(cursor.getString(cursor.getColumnIndex(company_name)));
            booking_details.setB_name(cursor.getString(cursor.getColumnIndex(branch_name)));
            booking_details.setTime(cursor.getString(cursor.getColumnIndex(booking_time)));
            booking_details.setBooking_id(cursor.getString(cursor.getColumnIndex(db_booking_id)));
            booking_details.setBranch_id(cursor.getString(cursor.getColumnIndex(db_branch_id)));
            booking_details.setServiced(cursor.getString(cursor.getColumnIndex(booking_active)));
            booking_details.setService_name(cursor.getString(cursor.getColumnIndex(db_service_name)));


            booking_list.add(booking_details);
        }
        return  booking_list;

    }
    public ArrayList<booking_details> get_booking_by_id(String _bookingid){

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<booking_details> booking_list = new ArrayList<>();
        booking_details booking_details;
        Cursor cursor = db.rawQuery("SELECT * FROM "+booking_table+" WHERE "+db_booking_id+"='"+_bookingid + "';",null);

        if (cursor!=null){

            while (cursor.moveToNext()){
                String co_name=cursor.getString(cursor.getColumnIndex(company_name));
                String b_name=cursor.getString(cursor.getColumnIndex(branch_name));
                String time=cursor.getString(cursor.getColumnIndex(booking_time));

                booking_details = new booking_details();
                booking_details.setId(cursor.getInt(cursor.getColumnIndex(booking_id)));
                booking_details.setCo_name(cursor.getString(cursor.getColumnIndex(company_name)));
                booking_details.setB_name(cursor.getString(cursor.getColumnIndex(branch_name)));
                booking_details.setTime(cursor.getString(cursor.getColumnIndex(booking_time)));
                booking_details.setBooking_id(cursor.getString(cursor.getColumnIndex(db_booking_id)));
                booking_details.setBranch_id(cursor.getString(cursor.getColumnIndex(db_branch_id)));
                booking_details.setServiced(cursor.getString(cursor.getColumnIndex(booking_active)));
                booking_details.setService_name(cursor.getString(cursor.getColumnIndex(db_service_name)));


                booking_list.add(booking_details);

            }
        }
        return  booking_list;

    }


    public void updateuser_no(String number){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(db_user_no, number);
        db.update(user_table,contentValues,null,null);
    }

    public void insert_user(String name, String email, String pass,String user_id_){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(db_user_name, name);
        contentValues.put(db_user_email, email);
        contentValues.put(db_user_pass, pass);
        contentValues.put(db_user_id,user_id_);
        contentValues.put(db_user_no,"0");

        if (check_user()>=1){
//            delete all users

            Cursor cursor = db.rawQuery("SELECT * FROM " + user_table + ";",null);
            if (cursor.moveToFirst()){
                do{
                    db.delete(user_table, user_id + "=" + cursor.getInt(cursor.getColumnIndex(user_id)),null);
                }while (cursor.moveToNext());
            }

        }
        else{
            db.insert(user_table,null, contentValues);
        }

        db.close();
    }

    public int check_user(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+user_table + ";",null);
        return cursor.getCount();
    }

    public void delete_user(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + user_table + ";",null);
        if (cursor.moveToFirst()){

            db.delete(user_table, user_id + "=" + cursor.getInt(cursor.getColumnIndex(user_id)),null);
        }
        cursor.close();
    }

    public String get_user_id(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+user_table + ";",null);
        String u_id = null;

        if (cursor.moveToFirst()){
            u_id=cursor.getString(cursor.getColumnIndex(db_user_id));
        }
        return u_id;
    }


    public String get_user_email(){
        String email = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+user_table + ";",null);
        if (cursor.moveToFirst()){
            email=cursor.getString(cursor.getColumnIndex(db_user_email));
        }

        return email;
    }

    public String getuser_no(){
        String number = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+user_table + ";",null);
        if (cursor.moveToFirst()){
            number=cursor.getString(cursor.getColumnIndex(db_user_no));
        }

        return number;
    }



}
