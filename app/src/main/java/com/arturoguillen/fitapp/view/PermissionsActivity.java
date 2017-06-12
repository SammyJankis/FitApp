package com.arturoguillen.fitapp.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.arturoguillen.fitapp.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agl on 12/06/2017.
 */

public abstract class PermissionsActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private List<String> permissions;

    public abstract String getTag();

    public abstract List<String> getPermissionsToGrant();

    public abstract Runnable getCallbackOnPermissionsGranted();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissions = new ArrayList<>();
        permissions.addAll(getPermissionsToGrant());

        if (!areAllPermissionsGranted()) {
            requestPermissions();
        }
    }

    private boolean areAllPermissionsGranted() {
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                return false;
            }
        }
        return true;
    }

    private List<String> getPermissionsNotGranted() {
        List<String> notGrantedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                notGrantedPermissions.add(permission);
            }
        }
        return notGrantedPermissions;
    }

    private String[] getStringArrayFromList(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    private boolean isPermissionGranted(String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(this, permission);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        final List<String> permissionsNotGranted = getPermissionsNotGranted();
        boolean shouldProvideRationale = checkIfPermissionsShoulProvideRationale(permissionsNotGranted);

        if (shouldProvideRationale) {

            showRationaleDialog(new Runnable() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(PermissionsActivity.this, getStringArrayFromList(permissionsNotGranted),
                            REQUEST_PERMISSIONS_REQUEST_CODE);
                }
            });
        } else {
            ActivityCompat.requestPermissions(PermissionsActivity.this, getStringArrayFromList(permissionsNotGranted),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private boolean checkIfPermissionsShoulProvideRationale(List<String> permissionsNotGranted) {
        boolean shouldProvideRationale = false;
        for (String permission : permissionsNotGranted) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                shouldProvideRationale = true;
                break;
            }
        }
        return shouldProvideRationale;
    }

    private void showRationaleDialog(final Runnable ok) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("FirApp");
        adb.setMessage("It is necessary that you accept these permissions to have fun with our app !!");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ok.run();
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        adb.show();
    }

    private void showGoToSettingsDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("FirApp");
        adb.setMessage("You can change your permissions in Settings, but until then, we can't continue.");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        adb.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(getTag(), "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCallbackOnPermissionsGranted().run();
            } else {
                showGoToSettingsDialog();
            }
        }
    }
}
