package com.bkav.cskh;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class WelcomeActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initialize();
    }
    private void initialize(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_PHONE_STATE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        launchHomeScreen();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            showSettingsDialog();
                        }
                        else finish();
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void launchHomeScreen() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    /*private boolean checkUserName(String name){
        if(name.length() == 0) return false;
        //65 -90 97-122
        for(int i = 0 ;i < name.length();i++){
            if(name.charAt(i) < 32 && name.charAt(i) > 32 && name.charAt(i) < 65 ) return false;
            if(name.charAt(i) > 90 && name.charAt(i) < 97 ) return false;
            if(name.charAt(i) > 122 && name.charAt(i) < 126 ) return false;
        }
        return true;
    }*/
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cho phép CSKH sử dụng quyền quản lý điện thoại");
        builder.setMessage("Để bật tính năng này, bấm vào CÀI ĐẶT bên dưới và kích hoạt Điện thoại trong menu Quyền");
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
