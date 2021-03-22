package com.juj27.secretchatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    ListView View;
    ArrayList<ProfileVOItem> items = new ArrayList<>();
    ProfileAdapter adapter;

    ProfileVOItem item = new ProfileVOItem();

    TextView tvName, tvAge;
    CircleImageView img;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ac = getSupportActionBar();
        ac.setDisplayShowTitleEnabled(false);

        tvName = findViewById(R.id.tv_me);
        tvAge = findViewById(R.id.tv_me_age);
        img = findViewById(R.id.circle_img);

        tvName.setText(G.nickName);
        tvAge.setText(G.age+"세");
        Glide.with(this).load(G.profileUrl).into(img);


        View = findViewById(R.id.list_profile);
        adapter = new ProfileAdapter(this, items);
        View.setAdapter(adapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference personRef = firebaseDatabase.getReference("person");

        personRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                item = snapshot.getValue(ProfileVOItem.class);

                if (G.profileUrl != item.proUrl ){
                    items.add(item);

                    adapter.notifyDataSetChanged();
                    View.setSelection(items.size());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                Intent intent = new Intent(ProfileActivity.this, ChattingActivity.class);
                intent.putExtra("name", item.proName );
                Log.i("aa",item.proName);
                startActivity(intent);
            }
        });
    }

    public void clickProfile(android.view.View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5 && resultCode == RESULT_OK) {
            uri = data.getData();
            Picasso.get().load(uri).into(img);   //외부저장소 동적 퍼미션이 자동 적용

            UpdateData();

        }
    }

    void UpdateData(){
        //FireStorage 파일명 중복x
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName = simpleDateFormat.format(new Date())+".pgn";

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference imgRef = firebaseStorage.getReference("profileImage/"+fileName);

        UploadTask uploadTask = imgRef.putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                G.profileUrl = uri.toString();
                Toast.makeText(ProfileActivity.this, "저장 완료", Toast.LENGTH_SHORT).show();

                //Firestore DB 에 저장
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference rootRef = firebaseDatabase.getReference();

                String proName = G.nickName;
                String age = G.age;
                String proUrl = G.profileUrl;

                ProfileVOItem person = new ProfileVOItem(proName,age,proUrl);

                DatabaseReference personRef = rootRef.child("person");
                personRef.push().setValue(person);
            }
        });
    }


}