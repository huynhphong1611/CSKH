package com.bkav.cskh;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bkav.cskh.adapter.ListMessagerAdapter;
import com.bkav.cskh.data.StaticConfig;
import com.bkav.cskh.model.Consersation;
import com.bkav.cskh.model.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "Tag";
    public static final int REQUEST_IMAGE_GALLERY = 1;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_SUPPORTER_MESSAGE = 1;
    private RecyclerView mRecyclerChat;
    private ListMessagerAdapter mListMessagerAdapter;
    private EditText mEditText;
    private ImageButton mButtonSent;
    private ImageButton mButtonAddImg;
    private Consersation mConsersation;
    private LinearLayoutManager mLinearLayoutManager;
    private TelephonyManager mTelephonyManager;
    private FirebaseAuth mAuth;
    private String mEmail;
    private String mPassWord;
    private String mImei;
    private Bitmap mBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        mButtonSent.setOnClickListener(this);
        mButtonAddImg.setOnClickListener(this);
            /*
             * Firebase */

            FirebaseDatabase.getInstance().getReference().child(mImei).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        Message newMessage = new Message();

                        newMessage.mIdSender = (String) mapMessage.get("mIdSender");
                        newMessage.mText = (String) mapMessage.get("mText");

                        mConsersation.getListMessageData().add(newMessage);
                        mListMessagerAdapter.notifyDataSetChanged();
                        mLinearLayoutManager.scrollToPosition(mConsersation.getListMessageData().size() - 1);
                        mRecyclerChat.setAdapter(mListMessagerAdapter);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initialize() {
        mButtonSent = (ImageButton) findViewById(R.id.btnSend);
        mButtonAddImg = (ImageButton) findViewById((R.id.btnChooseImg));
        mEditText = (EditText) findViewById(R.id.editWriteMessage);
        mRecyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);

        mConsersation = new Consersation();
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerChat.setHasFixedSize(true);
        mRecyclerChat.setLayoutManager(mLinearLayoutManager);

        mListMessagerAdapter = new ListMessagerAdapter(this, mConsersation);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mImei = mTelephonyManager.getImei();
        mEmail =  mImei+ "@bkav.com";
        StaticConfig.UID = mImei;
        mAuth = FirebaseAuth.getInstance();
        mPassWord = "anonymous@bkav.com";
        signIn();

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSend: {
                String content = mEditText.getText().toString().trim();
                if (content.length() > 0 ) {
                    mEditText.setText("");
                    Message message = new Message();
                    message.mIdSender = StaticConfig.UID;
                    message.mText = content;
                    FirebaseDatabase.getInstance().getReference().child(message.mIdSender).push().setValue(message);
                }
                if(mImei == null) Toast.makeText(this, "Bạn chưa cấp quyền quản lí cuộc gọi", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btnChooseImg: {
                checkPermissionsStorage();
                chooseImg();
                break;
            }
        }

    }
    /*dang ki*/
    private void registrationAccount(){
        mAuth.createUserWithEmailAndPassword(mEmail, mPassWord)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                        }
                    }
                });
    }
    /* dang nhap*/
    private void signIn(){
        mAuth.signInWithEmailAndPassword(mEmail, mPassWord)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:dang ki", task.getException());
                            registrationAccount();
                        }
                    }
                });
    }
    /*
    * xin quyền*/
    private void checkPermissionsStorage(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.isAnyPermissionPermanentlyDenied()){
                                showSettingsDialog();
                        }else{
                            finish();
                        }

                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).onSameThread()
                .check();
    }
    private void chooseImg(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data !=null){
            Uri imageUri = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SpannableString ss = new SpannableString("abc\n");
            Drawable drawable = new BitmapDrawable(getResources(), mBitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            mEditText.setText(ss);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cho phép CSKH sử dụng các quyền cần thiết để hỗ trợ bạn tốt nhất");
        builder.setMessage("Để bật tính năng này, bấm vào CÀI ĐẶT bên dưới và kích hoạt trong menu Quyền");
        builder.setPositiveButton("CÀI ĐẶT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Không phải lúc này", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
