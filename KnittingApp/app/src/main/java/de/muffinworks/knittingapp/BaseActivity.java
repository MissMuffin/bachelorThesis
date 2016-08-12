package de.muffinworks.knittingapp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.muffinworks.knittingapp.storage.PatternStorage;
import de.muffinworks.knittingapp.util.Constants;

/**
 * Created by Bianca on 01.08.2016.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected String TAG = this.getClass().getSimpleName();

    protected PatternStorage mStorage;
    private ActionBar mActionBar;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStorage = PatternStorage.getInstance();
        mStorage.init(this);

        mActionBar = getSupportActionBar();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    protected void enableBackInActionBar(boolean enabled) {
        mActionBar.setDisplayHomeAsUpEnabled(enabled);
        mActionBar.setDisplayShowHomeEnabled(enabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

    protected boolean isExternalStoragePermissionGranted() {
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * https://developer.android.com/training/permissions/requesting.html
     */
    protected void requestExternalStoragePermission() {
        if (!isExternalStoragePermissionGranted()) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                showAlertDialog(getString(R.string.info_storage_permission),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        Constants.PERMISSION_REQUEST_WRITE_SD);
                            }
                        });
                return;
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.PERMISSION_REQUEST_WRITE_SD);
        }
    }

    protected void showAlertDialog(String message, Dialog.OnClickListener positiveCallback) {
        mDialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_ok, positiveCallback)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();
        mDialog.show();
    }

    protected void showAlertDialog(String message) {
        showAlertDialog(message, null);
    }
}
