package be.patricegautot.ncrypt.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import be.patricegautot.ncrypt.R;
import be.patricegautot.ncrypt.customObjects.SavedEncryption;
import be.patricegautot.ncrypt.database.SavedEncryptionDatabase;
import be.patricegautot.ncrypt.helpers.Crypting;
import be.patricegautot.ncrypt.helpers.Helpers;
import be.patricegautot.ncrypt.helpers.Keys;

public class OutputActivity extends AppCompatActivity {

    private final static String TAG = OutputActivity.class.getSimpleName();

    private ImageButton savButton;
    private ImageButton cpyButton;
    private TextView outTv;
    private ImageView outImv;
    private String output;
    private FrameLayout progressBarContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        savButton = findViewById(R.id.btn_save);
        cpyButton = findViewById(R.id.btn_copy);
        outTv = findViewById(R.id.out_tv);
        outImv = findViewById(R.id.out_imv);
        progressBarContainer = findViewById(R.id.progress_bar_container);

        final Intent intent = getIntent();
        final String action = intent.getStringExtra(Keys.KEY_ACTION);
        String type = intent.getStringExtra(Keys.KEY_TYPE);


        if(action.equals(Keys.ACTION_ENCRYPT)) setTitle("Encrypt");
        else                   setTitle("Decrypt");


        if(type.equals(Keys.TYPE_TEXT)) {
            outImv.setVisibility(View.GONE);
            progressBarContainer.setVisibility(View.GONE);
            outTv.setVisibility(View.VISIBLE);

            if (action.equals(Keys.ACTION_DECRYPT)) savButton.setVisibility(View.GONE);
            final String out = intent.getStringExtra(Keys.KEY_DATA);
            output = out;

            outTv.setText(out);

            cpyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", out);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(OutputActivity.this, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show();
                }
            });

            savButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helpers.saveDialog(OutputActivity.this, output, "", "", -1);
                }
            });
        } else if(type.equals(Keys.TYPE_IMAGE)) {
            cpyButton.setVisibility(View.GONE);
            outTv.setVisibility(View.GONE);
            outImv.setVisibility(View.GONE);
            progressBarContainer.setVisibility(View.VISIBLE);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    String filepath = intent.getStringExtra(Keys.KEY_DATA);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filepath, options);
                    options.inSampleSize = Helpers.calculateInSampleSize(options, 1024, 1024);

                    options.inJustDecodeBounds = false;
                    final Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
                    int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
                    int[] newPixels;
                    bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

                    if(action.equals(Keys.ACTION_ENCRYPT))  newPixels = Crypting.nCryptBitmap(pixels, intent.getStringExtra(Keys.KEY_KEY));
                    else                                    newPixels = Crypting.dCryptBitmap(pixels, intent.getStringExtra(Keys.KEY_KEY));

                    final Bitmap newBitmap = Bitmap.createBitmap(newPixels, bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

                    OutputActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarContainer.setVisibility(View.GONE);
                            outImv.setVisibility(View.VISIBLE);
                            outImv.setImageBitmap(newBitmap);
                        }
                    });

                    savButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(action.equals(Keys.ACTION_ENCRYPT))      Helpers.savePictureDialog(OutputActivity.this, newBitmap, "", "", -1, action);
                            else if(action.equals(Keys.ACTION_DECRYPT)) Helpers.savePictureToGalleryConfirmationDialog(OutputActivity.this, newBitmap);
                        }
                    });
                }
            }).start();
        }
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
}
