package id.ac.umn.presetan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.zomato.photofilters.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static String pictureName;
    ImageView mImageView;
    Button mChooseButton;
    private Uri imageUri;
    private String pictureFilePath = null;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int REQUEST_PERMISSION = 1234;
    private static final String[] PERMISSIONS ={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
//    private static String appID = "namaAPK";
    private static final int PERMISSION_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.guest_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            Intent profile = new Intent(MainActivity.this, GuestProfile.class);
            startActivity(profile);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private boolean notPermission() {
        for (int i = 0; i < PERMISSION_COUNT; i++) {
            if(checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(notPermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSION);
        }
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION && grantResults.length > 0) {
            if(notPermission()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            }
        }
    }

    private void init() {
        if(!MainActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            findViewById(R.id.btnCamera).setVisibility(View.GONE);
        }
        mImageView = findViewById(R.id.galleryView);
        mChooseButton = findViewById(R.id.btnGallery);
        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickImageFromGallery();
                    }
                }
                else {
                    pickImageFromGallery();
                }
            }
        });

        Button foto = findViewById(R.id.btnCamera);
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(photoFile != null) {
                        //imageUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", photoFile);
                        imageUri = FileProvider.getUriForFile(MainActivity.this, "id.ac.umn.presetan.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "The camera app is not compatible", Toast.LENGTH_SHORT).show();
                }

                //link gw pake
                //https://stackoverflow.com/questions/11519691/passing-image-from-one-activity-another-activity
                //convert bitmap ke array
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                //passing byte arraynya ke intent berikutnya
                Intent picturePass = new Intent(MainActivity.this, EditActivity.class);
                picturePass.putExtra("image_path", byteArray);
                startActivity(picturePass);
            }
        });
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == IMAGE_PICK_CODE){
            assert data != null;
            mImageView.setImageURI(data.getData());
        }

//        if (requestCode == REQUEST_IMAGE_CAPTURE && requestCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            kotakFoto.setImageBitmap((imageBitmap));
//        }
//        if(requestCode == REQUEST_IMAGE_CAPTURE) {
//            if(imageUri == null) {
//                SharedPreferences getPrefs = getSharedPreferences(appID, 0);
//                String path = getPrefs.getString("path", "");
//                if(path.length() < 1) {
//                    recreate();
//                    return;
//                }
//                imageUri = Uri.parse("file://" + path);
//            }
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
//        } else if(data == null) {
//            recreate();
//            return;
//        }

        ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "Loading",
                "Please Wait", true);

        Intent editIntent = new Intent(MainActivity.this, EditActivity.class);
        editIntent.putExtra("image_path",pictureFilePath);
        startActivity(editIntent);

        dialog.cancel();
    }
}
