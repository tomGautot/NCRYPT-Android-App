package be.patricegautot.ncrypt.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.ByteArrayOutputStream;
import java.io.File;

import be.patricegautot.ncrypt.R;
import be.patricegautot.ncrypt.customObjects.SavedEncryption;
import be.patricegautot.ncrypt.helpers.Crypting;
import be.patricegautot.ncrypt.helpers.Helpers;
import be.patricegautot.ncrypt.helpers.ImageSaver;
import be.patricegautot.ncrypt.helpers.Keys;

public class GetInputActivity extends AppCompatActivity {

    private static final int SELECTED_PIC = 1;
    private static final String TAG = GetInputActivity.class.getSimpleName();

    private ActionBar actionBar;
    private ImageButton goButton;
    private EditText inputET;
    private EditText keyET;
    private RelativeLayout imageImportBtn;
    private ImageView image;
    private TextView hintText;
    private ProgressBar progressBar;
    private String action = "";
    private String type = "";
    private Bitmap bitmap;
    private String imageFilepath;
    private boolean imageSet;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        imageSet = false;

        goButton       = findViewById(R.id.go_button);
        keyET          = findViewById(R.id.key_et);

        //bitmap = Bitmap.createBitmap(new int[]{1}, 1, 1, Bitmap.Config.ARGB_8888);

        Intent inIntent = getIntent();

        if(inIntent.hasExtra(Keys.KEY_TYPE)){
            type = inIntent.getStringExtra(Keys.KEY_TYPE);

            //Log.e(TAG, "type : " + type);

            if(type.equals(Keys.TYPE_IMAGE)){

                if(inIntent.hasExtra(Keys.KEY_BASE_SE)){
                    SavedEncryption se = inIntent.getParcelableExtra(Keys.KEY_BASE_SE);
                    loadImageOverlayForBaseSe(se);
                    imageFilepath = se.getText();
                    imageSet = true;
                }
                else loadImageOverlay();
            }
            else{
                loadTextOverlay();
                if(inIntent.hasExtra(Keys.KEY_BASE_SE)){
                    SavedEncryption se = inIntent.getParcelableExtra(Keys.KEY_BASE_SE);
                    inputET.setText(se.getText());
                    keyET.setHint(se.getHint());
                }

            }
        }

        mInterstitialAd = new InterstitialAd(this);

        action = inIntent.getStringExtra(Keys.KEY_ACTION);
        if(action.equals(Keys.ACTION_ENCRYPT)){
            setTitle("Encrypt");
            goButton.setImageResource(R.drawable.encrypt_btn);
            mInterstitialAd.setAdUnitId(Keys.ADDMOB_ENCRTPTED_AD_ID);
        }
        else{
            setTitle("Decrypt");
            goButton.setImageResource(R.drawable.decrypt_btn);
            mInterstitialAd.setAdUnitId(Keys.ADDMOB_DECRTPTED_AD_ID);
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdFailedToLoad(int i) {
                //Log.e(TAG, "ad failed to load");
            }
        });

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        actionBar = getSupportActionBar();

        if(type.equals(Keys.TYPE_TEXT)) goButton.setOnClickListener(textType);
        else goButton.setOnClickListener(imageType);



    }

    private void loadImageOverlayForBaseSe(SavedEncryption se) {
        image          = findViewById(R.id.picture_imv);
        hintText       = findViewById(R.id.hint_text);
        progressBar    = findViewById(R.id.image_load_progressbar);

        hintText.setVisibility(View.GONE);
        image.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        image.setImageBitmap(Helpers.loadSEBitmap(this, se));
        progressBar.setVisibility(View.GONE);
    }

    private void loadTextOverlay() {
        inputET        = findViewById(R.id.input_et);
        inputET.setVisibility(View.VISIBLE);
    }

    private void loadImageOverlay() {
        //Log.e(TAG, "Loading Image Overlay");

        image          = findViewById(R.id.picture_imv);
        hintText       = findViewById(R.id.hint_text);
        progressBar    = findViewById(R.id.image_load_progressbar);

        image.setOnClickListener(imageImportListener);
        image.setVisibility(View.VISIBLE);
        hintText.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {


            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View.OnClickListener textType = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String input, key, output ="";

            input = inputET.getText().toString();
            key = keyET.getText().toString();

            String illegals = Crypting.illegalCharacters(input);

            if(illegals.length() > 0){
                inputET.setError(getString(R.string.input_char_error) + illegals);
                return;
            }

            if(key.length() < 4){
                keyET.setError(getString(R.string.key_length_error));
                return;
            }

            if(action.equals(Keys.ACTION_ENCRYPT)){
                output = Crypting.nCrypt(input, key);
            } else if (action.equals(Keys.ACTION_DECRYPT)){
                output = Crypting.dCrypt(input, key);
            } else {
                //Log.e(TAG, "Intent extra problem");
                return;
            }

            //Log.e(TAG, "input.lenght = " + input.length() + "   output.length = " + output.length());



            Intent intent = new Intent(GetInputActivity.this, OutputActivity.class);
            intent.putExtra(Keys.KEY_DATA, output);
            intent.putExtra(Keys.KEY_ACTION, action);
            intent.putExtra(Keys.KEY_TYPE, Keys.TYPE_TEXT);
            startActivity(intent);
            showAd();

        }
    };

    private View.OnClickListener imageType =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(!imageSet) return;

            String key = keyET.getText().toString();

            if(key.length() < 4){
                keyET.setError(getString(R.string.key_length_error));
                return;
            }

            Intent intent = new Intent(GetInputActivity.this, OutputActivity.class);
            intent.putExtra(Keys.KEY_ACTION, action);
            intent.putExtra(Keys.KEY_TYPE, Keys.TYPE_IMAGE);
            intent.putExtra(Keys.KEY_DATA, imageFilepath);
            intent.putExtra(Keys.KEY_KEY, keyET.getText().toString());
             startActivity(intent);
             showAd();

        }
    };

    private View.OnClickListener imageImportListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (ActivityCompat.checkSelfPermission(GetInputActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GetInputActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECTED_PIC);
            } else {
                hintText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECTED_PIC);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECTED_PIC:
                if (resultCode == RESULT_OK) {

                    imageSet = true;

                    Uri uri = data.getData();
                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filepath = cursor.getString(columnIndex);
                    cursor.close();

                    imageFilepath = filepath;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filepath, options);
                    options.inSampleSize = Helpers.calculateInSampleSize(options, 1024, 1024);
                    //Log.e(TAG,"importing bitmap using sample size " + Helpers.calculateInSampleSize(options, 1024, 1024));

                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(filepath, options);
                    //Log.e(TAG, "path : " + filepath + " bitmap is Null : " + ((bitmap.equals(null)) ? "1" : "0") );
                    image.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                    hintText.setVisibility(View.GONE);
                } else {
                    if(!imageSet) hintText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case SELECTED_PIC:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, SELECTED_PIC);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }
                break;
        }
    }

    private boolean showAd(){
        //Log.e(TAG, "Try to show ad");
        if (mInterstitialAd.isLoaded()) {

            int r = Helpers.randomNumberInRange(1, 3);
            //Log.e(TAG, "ad should be showing: " + r);
            if(r == 1) mInterstitialAd.show();
        } else {
            //Log.e(TAG, "The interstitial wasn't loaded yet.");
        }
        return true;
    }

}
