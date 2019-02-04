package be.patricegautot.ncrypt.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

import be.patricegautot.ncrypt.R;
import be.patricegautot.ncrypt.customObjects.SavedEncryption;
import be.patricegautot.ncrypt.customObjects.SavedEncryptionAdapter;
import be.patricegautot.ncrypt.database.SavedEncryptionDatabase;
import be.patricegautot.ncrypt.helpers.Keys;
import be.patricegautot.ncrypt.helpers.SharedPreferencesHelper;

public class SavedActivity extends AppCompatActivity {

    private FrameLayout progressBar;
    private TextView emptyListTV;
    private RecyclerView recyclerView;
    private SavedEncryptionAdapter adapter;
    private String type = "";
    private boolean pickupImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        //Log.e("SavedActivity", "creating content");

        Intent intent = getIntent();

        if(intent.hasExtra(Keys.KEY_TYPE)) type = intent.getStringExtra(Keys.KEY_TYPE);

        if(intent.hasExtra(Keys.KEY_ACTION) && intent.getStringExtra(Keys.KEY_ACTION).equals(Keys.ACTION_DECRYPT)) setTitle("Decrypt");

        pickupImage = (intent.hasExtra(Keys.KEY_PICKUP));

        adapter = new SavedEncryptionAdapter(new ArrayList<SavedEncryption>(), this, type, pickupImage);

        setupLiveData();

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        registerForContextMenu(recyclerView);

        emptyListTV = findViewById(R.id.empty_list_tv);
        progressBar = findViewById(R.id.progress_bar);

        //Log.e("SavedActivity", "Finished Content");
    }

    private void setupLiveData() {
        LiveData<List<SavedEncryption>> ld;
        if(type.equals(Keys.TYPE_IMAGE)){
            ld = SavedEncryptionDatabase.getInstance(this).savedEncryptionDao().querryTypeOnly(SavedEncryption.TYPE_IMAGE);
            //Log.e("SvdActvt", "Loaded Images only");
        }
        else if (type.equals(Keys.TYPE_TEXT)){
            ld = SavedEncryptionDatabase.getInstance(this).savedEncryptionDao().querryTypeOnly(SavedEncryption.TYPE_TEXT);
            //Log.e("SvdActvt", "Loaded texts only");
        }
        else{
            ld = SavedEncryptionDatabase.getInstance(this).savedEncryptionDao().querryAll();
            //Log.e("SvdActvt", "Loaded everything");
        }

        ld.observe(this, new Observer<List<SavedEncryption>>() {
            @Override
            public void onChanged(@Nullable List<SavedEncryption> savedEncryptions) {
                if(SharedPreferencesHelper.getSavedOrder(SavedActivity.this).equals(getString(R.string.settings_order_by_new_to_old))) {
                    Collections.reverse(savedEncryptions);
                    //Log.e("SvdActvt", "From newest to oldest: " + SharedPreferencesHelper.getSavedOrder(SavedActivity.this));
                }
                adapter.setData(savedEncryptions);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if(savedEncryptions.size() == 0) emptyListTV.setVisibility(View.VISIBLE);
                else                            {recyclerView.setVisibility(View.VISIBLE); emptyListTV.setVisibility(View.GONE);}
                recyclerView.setAdapter(adapter);
                //Log.e("SAvedActivity-SetupLD", "there are " + savedEncryptions.size() + " elements in adapter and " + recyclerView.getAdapter().getItemCount() + " elems in recyclerview");
            }
        });
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
