package com.example.randomlocks.gamesnote.helperClass;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmBackupRestore {

    private final static String TAG = RealmBackupRestore.class.getName();
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private File EXPORT_REALM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private String EXPORT_REALM_FILE_NAME = "gamesnote.realm";
    private String IMPORT_REALM_FILE_NAME = "default.realm"; // Eventually replace this if you're using a custom db name
    private Activity activity;
    private Realm realm;

    public RealmBackupRestore(Activity activity) {
        this.activity = activity;
    }

    private Realm getNewRealmInstance(Context context) {
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().schemaVersion(21).build();
        Realm.setDefaultConfiguration(configuration);
        return Realm.getDefaultInstance();
    }

    public void backup() {
        // First check if we have storage permissions
        realm = Realm.getDefaultInstance();

        File exportRealmFile;

        Log.d(TAG, "Realm DB Path = " + realm.getPath());

        EXPORT_REALM_PATH.mkdirs();

        // create a backup file
        exportRealmFile = new File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME);

        // if backup file already exists, delete it
        exportRealmFile.delete();

        // copy current realm to backup file
        realm.writeCopyTo(exportRealmFile);

        String msg = "File exported to Path: " + EXPORT_REALM_PATH + "/" + EXPORT_REALM_FILE_NAME;
        Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        Log.d(TAG, msg);

        realm.close();
    }

    public void restore() {
        //Restore
        String restoreFilePath = EXPORT_REALM_PATH + "/" + EXPORT_REALM_FILE_NAME;

        Log.d(TAG, "oldFilePath = " + restoreFilePath);

        copyBundledRealmFile(restoreFilePath, IMPORT_REALM_FILE_NAME);
        Log.d(TAG, "Data restore is done");
    }

    private String copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
            File file = new File(activity.getApplicationContext().getFilesDir(), outFileName);
            if (!file.exists()) {
                return null;
            }

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            Toaster.make(activity, "backup restored");
            return file.getAbsolutePath();
        } catch (IOException e) {
            Toaster.make(activity, "No backup found");
        }
        return null;
    }



    private String dbPath() {
        return realm.getPath();
    }


}