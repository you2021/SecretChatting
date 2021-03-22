package com.juj27.secretchatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

public class ChattingActivity extends AppCompatActivity {

    ArrayList<MessageItem> messageItems = new ArrayList<>();
    ListView listView;
    //채팅 메세지 아답터
    ChatAdapter chatAdapter;

    EditText etMsg;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference chatRef;

    ProfileVOItem item = new ProfileVOItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        listView = findViewById(R.id.listview);
        etMsg = findViewById(R.id.et);
        chatAdapter = new ChatAdapter(this, messageItems);
        listView.setAdapter(chatAdapter);



        Intent intent = getIntent();
        String name = intent.getStringExtra("name");


        String chat;
//        String chat = G.nickName+name;
//
//       Toast.makeText(this, ""+G.nickName+"\n"+ name+"\n"+ chat, Toast.LENGTH_SHORT).show();


        String arr[]= {G.nickName, name};
        Arrays.sort(arr);
        chat= Arrays.toString(arr);

        chat= chat.replace("[","");
        chat= chat.replace(",","");
        chat= chat.replace("]","");


        Toast.makeText(this, ""+chat, Toast.LENGTH_SHORT).show();


        //Firebase  Database 에 저장되어 있는 메세지들 읽어오기
        firebaseDatabase = FirebaseDatabase.getInstance();
        //'chat'노드에 MessageItem 들을 저장 ['chat' 이라는 이름만 별도로 지정하면 여러 채팅방 개설도 가능함]
        chatRef = firebaseDatabase.getReference(chat);

        //'chat'노드의 값이 변경되는 것을 듣는 리스너
        //addValueEventListener() 는 노드 아래 자식 1개가 추가되어도
        //전체 데이터들을 모두 읽어들임. 그래서 이전 데이터들이 중복됨
        //addChildEventListener() : chat노드의 자식이 변경되었을 때 그 하나만 읽어들임.
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //새로 추가된 데이터 값( DataSnapshot 이 촬용한 값)
                MessageItem item = snapshot.getValue(MessageItem.class);

                //읽어들인 메세지를 리스트뷰가 보여주는 대량의 데이터에 추가
                messageItems.add(item);

                //리스트뷰 갱신 - 리스트뷰가 보여줄 뷰를 만들어내는 아답터에게 요청
                chatAdapter.notifyDataSetChanged();
                listView.setSelection(messageItems.size()-1);  //리스트뷰의 마지막 위치로 스트롤 이동
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
    }



    public void clickSend(View view) {

        //firebase DB에 저장할 데이터들 (닉네임, 메세지, 프로필이미지URL, 작성시간)
        String nickName = G.nickName;
        String message = etMsg.getText().toString();
        String profileUrl = G.profileUrl;

        //메세지 작성시간을 문자열..(시;분)
        Calendar calendar = Calendar.getInstance();  //현재시간 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

        //firebase DB에 MessageItem 객체를 통으로 저장하기..
        MessageItem item = new MessageItem(nickName, message, time, profileUrl);

        //'chat'노드에 MessageItem 통째로 값 추가(push)
        chatRef.push().setValue(item);

        //다음 메세지 입력이 수월하도록
        etMsg.setText("");

        //소프트 키패드 안보이도록..
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}