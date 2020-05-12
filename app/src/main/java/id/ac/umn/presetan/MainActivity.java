package id.ac.umn.presetan;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import id.ac.umn.presetan.Adapter.ViewPagerAdapter;
import id.ac.umn.presetan.Interface.EditImageFragmentListener;
import id.ac.umn.presetan.Interface.FiltersListFragmentListener;
import id.ac.umn.presetan.Utils.BitmapUtils;


public class MainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {
    public static final String pictureName = "flash.png";
    //public String user = "Guest";
    FirebaseUser currUser;

    private static final int PERMISSION_PICK_IMAGE = 1000;

    ImageView img_preview;
    TabLayout tabLayout;
    ViewPager viewPager;
    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    int brightnessFinal = 0;
    float saturationFinal = 1.0f;
    float constraintFinal = 1.0f;

    //Load native image lib
    static {
        System.loadLibrary("NativeImageProcessor");
    }

//    private static final int PERMISSION_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Presetan Filter");

        //View
        img_preview = (ImageView)findViewById(R.id.image_preview);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator);


        originalBitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);

        if (originalBitmap != null) {
            loadImage();
            //openImageFromGallery();
        } else {
            loadImage();
            openImageFromGallery();
        }
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadImage() {
        if (originalBitmap == null) {
            originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 300, 300);
        }
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        img_preview.setImageBitmap(originalBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        String username = "Guest";

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        currUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        if(currUser != null) {
            username = currUser.getEmail();
        }
        adapter.addFragment(filtersListFragment, "FILTERS");

        if(Global.login == 1) {
            editImageFragment = new EditImageFragment();
            editImageFragment.setListener(this);
            adapter.addFragment(editImageFragment, "EDIT");
        }

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onConstraintChanged(float constraint) {
        constraintFinal = constraint;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constraint));
        img_preview.setImageBitmap(myFilter.processFilter(finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(constraintFinal));

        finalBitmap = myFilter.processFilter(bitmap);
        originalBitmap = null;
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        img_preview.setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

    }

    private void resetControl() {
        if(editImageFragment != null){
            editImageFragment.resetControls();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        constraintFinal = 1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_open){
            openImageFromGallery();
            return true;
        }
        if(id == R.id.action_save){
            saveImageFromGallery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            final String path;
                            try {
                                path = BitmapUtils.insertImage(getContentResolver(),
                                        finalBitmap,
                                        System.currentTimeMillis()+"_profile.jpg",
                                        null);
                                if (!TextUtils.isEmpty(path)){
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                            "Image saved to gallery!",
                                            Snackbar.LENGTH_LONG).setAction("OPEN", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            openImage(path);
                                        }
                                    });
                                    snackbar.show();
                                }
                                else {
                                    Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                            "Unable to save image", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PERMISSION_PICK_IMAGE) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

            //clear bitmap memory
            originalBitmap.recycle();
            finalBitmap.recycle();
            filteredBitmap.recycle();

            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            img_preview.setImageBitmap(originalBitmap);
            bitmap.recycle();

            //render selected
            filtersListFragment.displayThumbnail(originalBitmap);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
