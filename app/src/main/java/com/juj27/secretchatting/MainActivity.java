package com.juj27.secretchatting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    EditText et ,etAge;
    CircleImageView civ;

    Uri imgUri;

    boolean isFirst = true;  //처음 애을 실행하여 프로필 데이터가 없는가?
    boolean isChanged = false;  //기존 프로필이미지를 변경한 적잉 있는가?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ac = getSupportActionBar();
        ac.setDisplayShowTitleEnabled(false);

        et = findViewById(R.id.et);
        civ = findViewById(R.id.circle);
        etAge = findViewById(R.id.et_age);


        //SharedPreferences에 미리 저장되어 있는 닉네임, 프로필 이미지가 있다면 읽어와라
         loadData();

         if(G.nickName != null){
             et.setText(G.nickName);
             Picasso.get().load(G.profileUrl).into(civ);
             etAge.setText(G.age);

             Intent intent = new Intent(this, ProfileActivity.class);
              startActivity(intent);

              finish();

             //처음이 아니네
             isFirst = false;
         }
    }

    void loadData(){
        SharedPreferences pref = getSharedPreferences("account", MODE_PRIVATE);
        G.nickName = pref.getString("nickName", null);
        G.profileUrl = pref.getString("profileUrl", null);
        G.age = pref.getString("age", null);
    }

    public void clickCircle(View view) {
        //앱에서 사진 선택
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10 && resultCode == RESULT_OK) {
            imgUri = data.getData();
            Picasso.get().load(imgUri).into(civ);   //외부저장소 동적 퍼미션이 자동 적용

            //사진이 변경되었으므로
            isChanged = true;
        }
    }

    public void clickBtn(View view) {

        //처음 이거나 사진이 변경 되었다면?
        if(isFirst || isChanged){
            saveData();
        }else {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        }


    }

    void saveData(){
        //닉네임 가져오기
        G.nickName = et.getText().toString();
        G.age = etAge.getText().toString();

        if (imgUri == null) {
            AlertDialog alertDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("사진을 업로드 해주세요.");
            builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            return;
        }

        //FireStorage 파일명 중복x
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName = simpleDateFormat.format(new Date())+".pgn";

        //이미지 업로드
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference imgRef = firebaseStorage.getReference("profileImage/"+fileName);

        UploadTask uploadTask = imgRef.putFile(imgUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //업로드 된 파일의 다운로드 주소 --> 인터넷경로 URL 얻어오기
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //다운로드 주소 url 문자열로 얻기
                        G.profileUrl = uri.toString();
                        Toast.makeText(MainActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();

//                        //Firestore DB 에 저장
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference rootRef = firebaseDatabase.getReference();

                        String proName = G.nickName;
                        String age = G.age;
                        String proUrl = G.profileUrl;

                        ProfileVOItem person = new ProfileVOItem(proName,age,proUrl);

                        DatabaseReference personRef = rootRef.child("person");
                        personRef.push().setValue(person);

                        //처음 실행할때만 닉네임과 사진을 입력하기
                        SharedPreferences pref = getSharedPreferences("account", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("nickName", G.nickName);
                        editor.putString("profileUrl", G.profileUrl);
                        editor.putString("age", G.age);

                        editor.commit();

                        //완료후 채팅화면 전환
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });
            }
        });
    }
}