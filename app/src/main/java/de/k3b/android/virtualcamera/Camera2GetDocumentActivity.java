/*
 * Copyright (c) 2021 by k3b.
 *
 * This file is part of VirtualCamera https://github.com/k3b/VirtualCamera/
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.android.virtualcamera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Translates from MediaStore.ACTION_IMAGE_CAPTURE to ACTION_GET_CONTENT.
 * Every client app with a button to take a photo will be presented a
 * picker/chooser where an existing photo can be chosen.
 */
public class Camera2GetDocumentActivity extends Activity {
    private static final String TAG = "k3b.virtualcamera";
    protected static final String IMAGE_JPEG_MIME = "image/jpeg";

    private static final int ACTION_REQUEST_ID_SOURCE_IMAGE = 21;

    private static final int PERMISSION_REQUEST_ID_FILE_WRITE = 23;
    private static final String PERMISSION_FILE_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int RESULT_NO_PERMISSIONS = -22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, PERMISSION_FILE_WRITE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(PERMISSION_FILE_WRITE, PERMISSION_REQUEST_ID_FILE_WRITE);
            return;
        }

        requestImageUri();
    }

    private void requestImageUri() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType(IMAGE_JPEG_MIME)
                .putExtra(Intent.EXTRA_TITLE, getString(R.string.label_select_picture))
                .putExtra(DocumentsContract.EXTRA_PROMPT, getString(R.string.label_select_picture))
                .addCategory(Intent.CATEGORY_OPENABLE)
                ;

        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), ACTION_REQUEST_ID_SOURCE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_REQUEST_ID_SOURCE_IMAGE) {
            if (resultCode == RESULT_OK) {
                final Uri inUri = (data == null) ? null : data.getData();
                copy(getDestinationUri(), inUri);
                setResult(RESULT_OK);
            }
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Uri getDestinationUri() {
        Intent intent = getIntent();
        if (intent != null && MediaStore.ACTION_IMAGE_CAPTURE.equals(intent.getAction())) {
            return intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        }
        return null;
    }

    private void copy(Uri outUri, Uri inUri) {
        if (outUri != null && inUri != null) {
            try (OutputStream os = this.getContentResolver().openOutputStream(outUri);
                 InputStream is = this.getContentResolver().openInputStream(inUri)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (IOException ex) {
                String message = "" + getText(R.string.permission_error) + ":" + outUri;
                Toast.makeText(this,message , Toast.LENGTH_LONG).show();
                Log.e(TAG, message, ex);
            }
        }
    }

    private void requestPermission(final String permission, final int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_ID_FILE_WRITE) {
            if (isGrantSuccess(grantResults)) {
                requestImageUri();
            } else {
                Toast.makeText(this, R.string.permission_error, Toast.LENGTH_LONG).show();
                setResult(RESULT_NO_PERMISSIONS, null);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isGrantSuccess(int[] grantResults) {

        return (grantResults != null)
                && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }


}
