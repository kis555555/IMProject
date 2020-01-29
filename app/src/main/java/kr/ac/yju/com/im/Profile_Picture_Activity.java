package kr.ac.yju.com.im;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Profile_Picture_Activity extends AppCompatActivity {

    SessionManager sessionManager;
    public String MemberType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__picture_);

        View view = getLayoutInflater().from(this).inflate(R.layout.activity_profile__picture_,null);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();

        ImageButton propicback = (ImageButton)findViewById(R.id.prof_backbtn);
        ImageButton setpropic = (ImageButton)findViewById(R.id.profile_pic_change_btn);
        ImageButton defpic = (ImageButton)findViewById(R.id.default_pic_btn);

        ImageView profile_picture = (ImageView)findViewById(R.id.profile_pic_circle);

        TextView prof_name = (TextView)findViewById(R.id.prof_name);
        TextView prof_membertype = (TextView)findViewById(R.id.prof_membertype);
        TextView prof_nickname = (TextView)findViewById(R.id.prof_nickname);

        if(user.get(sessionManager.CLASSFI) == "0"){
            MemberType = "일반회원";
        }
        else{
            MemberType = "사업자";
        }
        try{
            String name = user.get(sessionManager.NICKNAME);
            HttpURLConnection.setFollowRedirects(false);

            HttpURLConnection con = (HttpURLConnection)new URL("http://101.101.162.32:8080/profilepic/"+name+".png").openConnection();

            con.setRequestMethod("HEAD");

            if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
                profile_picture.setBackground(new ShapeDrawable(new OvalShape()));
                profile_picture.setClipToOutline(true);
                String pic = "http://101.101.162.32:8080/profilepic/"+name+".png";
                Glide.with(this).load(pic).into(profile_picture);
            }else{
                profile_picture.setBackground(new ShapeDrawable(new OvalShape()));
                profile_picture.setClipToOutline(true);
                String pic = "http://101.101.162.32:8080/profilepic/def.png";
                Glide.with(this).load(pic).into(profile_picture);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        prof_name.setText(user.get(sessionManager.NAME));
        prof_membertype.setText(MemberType);
        prof_nickname.setText(user.get(sessionManager.NICKNAME));

        propicback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotomypro = new Intent(getApplicationContext(), Myprofile_Activity.class);
                startActivity(gotomypro);
            }
        });

        setpropic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        defpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = user.get(sessionManager.NICKNAME);
                String pic = "http://101.101.162.32:8080/profilepic/def.png";
                Glide.with(view).load(pic).into(profile_picture);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

