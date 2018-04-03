package com.example.mobilecloud.mydatasharingapplication;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PHOTO = 2;
    private EditText editText;
    private ImageView imageView;
    private ImageView imageView2;
    private Button send_text;
    private Button choose_photos;
    private Button send_photo;
    private Button take_photo;
    private String mCurrentPhotoPath;
    private ArrayList<Uri> uri_global_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text_msg);
        imageView = findViewById(R.id.image_view);
        imageView2 = findViewById(R.id.image_view_2);
        send_text = findViewById(R.id.send_text_button);
        choose_photos = findViewById(R.id.choose_photos_button);
        send_photo = findViewById(R.id.send_photos_button);
        take_photo = findViewById(R.id.save_button);

        send_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String entered_text = editText.getText().toString();
                Intent contentIntent = new Intent();
                contentIntent.setAction(Intent.ACTION_SEND);
                contentIntent.putExtra(Intent.EXTRA_TEXT,entered_text);
                contentIntent.setType("text/plain");
                startActivity(Intent.createChooser(contentIntent,"Select APP"));
            }
        });

        choose_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PHOTO);
            }
        });

        send_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.setType("image/*");
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uri_global_list);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(shareIntent.createChooser(shareIntent,"Send Image Via"));
            }
        });

        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(camIntent.resolveActivity(getPackageManager()) != null)
                {
                    File file_name = get_file_name();
                    Uri photoUri = FileProvider.getUriForFile(MainActivity.this,"com.example.mobilecloud.mydatasharingapplication",file_name);
                    camIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                    startActivityForResult(camIntent,REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            imageView.setImageDrawable(Drawable.createFromPath(mCurrentPhotoPath));
        } else if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                ClipData clipData = data.getClipData();
                ArrayList<Bitmap> images = new ArrayList<>();


                Bitmap bitmap = null;
                Bitmap bitmap2 = null;

                if( clipData != null ) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        uri_global_list.add(item.getUri());
                    }
                }

                Log.v("NIKITAPP",  "uri : " + uri_global_list.size());
                if (uri_global_list != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (Uri) uri_global_list.get(0));
                        imageView.setImageBitmap(bitmap);
                        if (uri_global_list.size() > 1) {
                            bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), (Uri) uri_global_list.get(1));
                            imageView2.setImageBitmap(bitmap2);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public File get_file_name() {
        File image;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String image_file_name = "IMG_" + timeStamp + "_";
        File storage_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(image_file_name,".jpg",storage_path);
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
