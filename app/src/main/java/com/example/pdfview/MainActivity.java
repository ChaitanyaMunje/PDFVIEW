package com.example.pdfview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public File file;
    private File localFile;
    private StorageReference mStorageRef;
    private String name;
    public String path;
    public PDFView pdfView;
    public String pri;
    public ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.pdfView = (PDFView) findViewById(R.id.pdf_viewer);
        this.progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        this.name = "CSE4thsemDMGT.pdf";
        this.path = "CSE4thsemDMGT.pdf";
        this.pri = "CSE4thsemDMGT";
        this.file = getFileStreamPath(this.name);
        if (this.file.exists()) {
            this.pdfView.setVisibility(View.VISIBLE);
            this.pdfView.fromFile(this.file).enableSwipe(true).swipeHorizontal(false).enableAntialiasing(true).onLoad(new OnLoadCompleteListener() {
                public void loadComplete(int i) {
                }
            }).onError(new OnErrorListener() {
                public void onError(Throwable th) {
                    MainActivity.this.pdfView.setVisibility(View.INVISIBLE);
                    MainActivity.this.file.delete();
                    MainActivity finalR = MainActivity.this;
                    finalR.download(finalR.path, MainActivity.this.pri);
                }
            }).load();
            return;
        }
        download(this.path, this.pri);
    }

    /* access modifiers changed from: private */
    public void download(String str, String str2) {
        this.pdfView.setVisibility(View.VISIBLE);
        this.progressDialog.setMessage("Downloading...");
        this.progressDialog.show();
        this.mStorageRef = FirebaseStorage.getInstance().getReference(str);
        try {
            this.localFile = File.createTempFile(str2, ".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mStorageRef.getFile(this.file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                System.out.println(MainActivity.this.file);
                MainActivity.this.progressDialog.cancel();
                MainActivity.this.pdfView.setVisibility(View.VISIBLE);
                MainActivity.this.pdfView.fromFile(MainActivity.this.file).enableSwipe(true).swipeHorizontal(false).enableAntialiasing(true).load();//.pageFitPolicy(FitPolicy.WIDTH).load();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Check Internet", Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double bytesTransferred = (double) taskSnapshot.getBytesTransferred();
                Double.isNaN(bytesTransferred);
                double d = bytesTransferred * 100.0d;
                double totalByteCount = (double) taskSnapshot.getTotalByteCount();
                Double.isNaN(totalByteCount);
                double d2 = d / totalByteCount;
                ProgressDialog access$500 = MainActivity.this.progressDialog;
                StringBuilder sb = new StringBuilder();
                sb.append("Downloading ");
                sb.append((int) d2);
                sb.append("%...");
                access$500.setMessage(sb.toString());
            }
        });


    }
}
