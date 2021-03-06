package com.arturoguillen.fitapp.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import com.arturoguillen.fitapp.R;
import com.arturoguillen.fitapp.utils.LogUtils;

/**
 * Created by agl on 12/06/2017.
 */

public abstract class PermissionsActivity extends InjectedActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private final String permissionLocation = Manifest.permission.ACCESS_FINE_LOCATION;

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length == 0) {
                LogUtils.DEBUG(this.getLocalClassName(), "User interaction was cancelled");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                for (String permission : permissions) {
                    if (permission.equals(permissionLocation)) {
                        onLocationPermissionGranted();
                    }
                }
            } else {
                showDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        startActivity(createGoToSettingsIntent());
                        finish();
                    }

                }, R.string.change_permissions_in_settings, R.string.go_to_settings);
            }
        }
    }

    public void onLocationPermissionGranted() {
    }

    public void requestLocationPermission() {
        if (isPermissionGranted(permissionLocation)) {
            onLocationPermissionGranted();
        } else {
            requestPermission(permissionLocation);
        }
    }

    private boolean checkIfPermissionShouldProvideRationale(String permissionsNotGranted) {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsNotGranted);
    }

    private boolean isPermissionGranted(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission) {
        boolean shouldProvideRationale = checkIfPermissionShouldProvideRationale(permission);

        final String[] permissionArray = new String[]{permission};

        if (shouldProvideRationale) {
            showDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, final int i) {
                    ActivityCompat.requestPermissions(PermissionsActivity.this, permissionArray,
                            REQUEST_PERMISSIONS_REQUEST_CODE);
                }

            }, R.string.necessary_accept_permissions);
        } else {
            ActivityCompat.requestPermissions(this, permissionArray,
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}
