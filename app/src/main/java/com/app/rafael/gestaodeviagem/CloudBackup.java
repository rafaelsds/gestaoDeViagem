package com.app.rafael.gestaodeviagem;

import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.app.rafael.gestaodeviagem.db.CriaBanco;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class CloudBackup extends AppCompatActivity
implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = "drive_Humanity";
    private static final int REQUEST_CODE_RESOLUTION = 3;
    public static DriveFile mfile;
    private static GoogleApiClient mGoogleApiClient;

    //private static final String DATABASE_PATH = "/data/data/" + "com.app.rafael.gestaodeviagem" + "/databases/" + CriaBanco.NOME_BANCO;

//    String DATABASE_PATH = "/data/" + "com.app.rafael.gestaodeviagem"
//            + "/databases/" + CriaBanco.NOME_BANCO;

   // private static final File DATA_DIRECTORY_DATABASE =
     //       new File("/data/" + "com.app.rafael.gestaodeviagem" + "/databases/" + CriaBanco.NOME_BANCO);

    private static final String MIME_TYPE = "application/x-sqlite-3";

    /*
     * Create a new file and save it to Drive.
     */
    public void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    //Error backup
                    System.out.println("Erro ao fazer backup");
                    return;
                }

                String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(MIME_TYPE);
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(CriaBanco.NOME_BANCO) // Google Drive File name
                        .setMimeType(mimeType)
                        .setStarred(true).build();
                // create a file on root folder
                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, result.getDriveContents()).setResultCallback(backupFileCallback);

            }
        });
    }

    public static void doGDriveBackup() {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(backupContentsCallback);

    }

    static final private ResultCallback<DriveApi.DriveContentsResult> backupContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(MIME_TYPE);
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(CriaBanco.NOME_BANCO) // Google Drive File name
                            .setMimeType(mimeType)
                            .setStarred(true).build();
                    // create a file on root folder
                    Drive.DriveApi.getRootFolder(mGoogleApiClient)
                            .createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                            .setResultCallback(backupFileCallback);
                }
            };

    static final private ResultCallback<DriveFolder.DriveFileResult> backupFileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    mfile = result.getDriveFile();
                    mfile.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, new DriveFile.DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long bytesExpected) {
                        }
                    }).setResultCallback(backupContentsOpenedCallback);
                }
            };

    static final private ResultCallback<DriveApi.DriveContentsResult> backupContentsOpenedCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }

                    DriveContents contents = result.getDriveContents();
                    BufferedOutputStream bos = new BufferedOutputStream(contents.getOutputStream());
                    byte[] buffer = new byte[1024];
                    int n;

                    ///TEMPPPP
                    File externalStorageDirectory = Environment.getExternalStorageDirectory();
                    File dataDirectory = Environment.getDataDirectory();

                    FileChannel source = null;
                    FileChannel destination = null;

                    String currentDBPath = "/data/" + "com.app.rafael.gestaodeviagem"  + "/databases/"+CriaBanco.NOME_BANCO;
                    String backupDBPath = CriaBanco.NOME_BANCO;
                    File currentDB = new File(dataDirectory, currentDBPath);
                    File backupDB = new File(externalStorageDirectory, backupDBPath);

                    try {
                        System.out.println("entrou");
                        FileInputStream is = new FileInputStream(Environment.getDataDirectory()+currentDBPath);
                        System.out.println(Environment.getDataDirectory());
                        source = new FileInputStream(currentDB).getChannel();
                        destination = new FileOutputStream(backupDB).getChannel();
                        destination.transferFrom(source, 0, source.size());

                        BufferedInputStream bis = new BufferedInputStream(is);

                        while ((n = bis.read(buffer)) > 0) {
                            bos.write(buffer, 0, n);
//                    DialogFragment_Sync.setProgressText("Backing up...");
                        }
                        bos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    contents.commit(mGoogleApiClient, null).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
//                    DialogFragment_Sync.setProgressText("Backup completed!");
//                    mToast(act.getResources().getString(R.string.backupComplete));
//                    DialogFragment_Sync.dismissDialog();
                        }
                    });
                }
            };



    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {

//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

            GoogleSignInOptions options =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addConnectionCallbacks(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                    .addApi(Drive.API)
                    .build();

        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            saveFileToDrive();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");

        saveFileToDrive();
//        doDriveBackup();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }
}