package com.example.sqlite;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.service.autofill.FieldClassification;
import android.view.Gravity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_read;
    private Button btn_write;
    private Button btn_update;
    private Button btn_remove;
    private EditText et_name;
    private EditText et_email;
    private EditText et_phone;
    private TextView tv_content;
    protected Myhelper myHelper;
    public static Pattern p =
            Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_read=findViewById(R.id.btn_read);
        btn_remove=findViewById(R.id.btn_remove);
        btn_update=findViewById(R.id.btn_update);
        btn_write=findViewById(R.id.btn_write);
        et_name=findViewById(R.id.et_name);
        et_email=findViewById(R.id.et_email);
        et_phone=findViewById(R.id.et_phoneNumber);
        tv_content=findViewById(R.id.tv_content);
        myHelper=new Myhelper(this);
        myHelper.getWritableDatabase();
        btn_read.setOnClickListener(this);
        btn_write.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        String name,email,phone;
        SQLiteDatabase db;
        ContentValues contentValues;
        name=et_name.getText().toString();
        email=et_email.getText().toString();
        phone=et_phone.getText().toString();

        switch (view.getId()){
            case R.id.btn_read:
                db=myHelper.getWritableDatabase();
                Cursor cursor=db.query("info",null,null,null,null,null,null);
                if(cursor.getCount()==0) {
                    tv_content.setText("");
                    Toast.makeText(this,"none",Toast.LENGTH_SHORT).show();
                    tv_content.append("----------------------"+"\n");
                    tv_content.append("no records"+"\n");
                    tv_content.append("----------------------");
                }
                else{
                    cursor.moveToFirst();
                    tv_content.setText("");
                    tv_content.setGravity(Gravity.LEFT);
                    tv_content.append("NAME: "+cursor.getString(1)+"\n");
                    tv_content.append("EMAIL: "+cursor.getString(2)+"\n");
                    tv_content.append("PHONE: "+cursor.getString(3)+"\n");
                }
                while(cursor.moveToNext()){
                    tv_content.append("NAME: "+cursor.getString(1)+"\n");
                    tv_content.append("EMAIL: "+cursor.getString(2)+"\n");
                    tv_content.append("PHONE: "+cursor.getString(3)+"\n");
                }
                cursor.close();
                db.close();
                break;

            case R.id.btn_write:
                name=name.trim();
                phone=phone.trim();
                if(isEmail(email)&&check(name)&&check(phone)){
                    db=myHelper.getWritableDatabase();
                    Cursor cursor1=db.rawQuery("select * from info where phone=?",new String[]{phone});
                    if (cursor1.getCount()==0){
                        contentValues=new ContentValues();
                        contentValues.put("name",name);
                        contentValues.put("email",email);
                        contentValues.put("phone",phone);
                        db.insert("info",null,contentValues);
                        cursor1.close();
                        db.close();
                        Toast.makeText(this,"write successfully!",Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(this,"the phone number already exists!",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this,"your input is invalidate!", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_update:
                db=myHelper.getWritableDatabase();
                contentValues=new ContentValues();
                if(isEmail(email)||check(name)){
                    contentValues.put("name",name);
                    contentValues.put("email",email);
                    db.update("info",contentValues,"phone=?",new String[]{phone});
                    db.close();
                }
                else
                    Toast.makeText(MainActivity.this,"please check your input!",Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_remove:
                db=myHelper.getWritableDatabase();
                db.delete("info",null,null);
                Toast.makeText(MainActivity.this,"you have delete all data!", Toast.LENGTH_LONG).show();
                db.close();
                break;
        }
    }

    public static boolean isEmail(String email){
        if(null==email||"".equals(email))
            return false;
        Matcher m=p.matcher(email);
        return m.matches();
    }
    public static boolean check(String string){
        if(null==string||"".equals(string))
            return false;
        else
            return true;
    }
}
