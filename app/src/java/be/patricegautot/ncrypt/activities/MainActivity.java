package be.patricegautot.ncrypt.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import be.patricegautot.ncrypt.R;
import be.patricegautot.ncrypt.customObjects.SavedEncryption;
import be.patricegautot.ncrypt.helpers.Crypting;
import be.patricegautot.ncrypt.helpers.Keys;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageButton encryptBtn;
    private ImageButton decryptBtn;
    private ImageButton savedBtn;
    private ImageButton textBtn;
    private ImageButton imageBtn;
    private ImageButton backBtn;
    private ImageButton settingsBtn;
    private ImageButton contactBtn;
    private ImageButton shareBtn;
    private LinearLayout menuLayout;
    private LinearLayout actionLayout;
    private LinearLayout typeLayout;
    private ImageView logoApp;
    private RelativeLayout rootView;

    private Animation fadeInIntro;
    private Animation introRL;
    private Animation introLR;
    private Animation outroRL;
    private Animation outroLR;

    private String action;
    private String type;

    boolean typeSelection;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        setupAd();
        new Thread(new Runnable() {
            @Override
            public void run() {
                setupClickListeners();

            }
        }).start();

        typeSelection = false;

        rootView = findViewById(R.id.root_layout);
        actionLayout = findViewById(R.id.main_action_layout);
        typeLayout = findViewById(R.id.main_type_layout);

        fadeInIntro = AnimationUtils.loadAnimation(this, R.anim.fadein_ud);
        introLR = AnimationUtils.loadAnimation(this, R.anim.intro_trans_lr);
        introRL = AnimationUtils.loadAnimation(this, R.anim.intro_trans_rl);
        outroLR = AnimationUtils.loadAnimation(this, R.anim.exit_trans_lr);
        outroRL = AnimationUtils.loadAnimation(this, R.anim.exit_trans_rl);

        outroLR.setAnimationListener(OUT_LR_Listener);
        outroRL.setAnimationListener(OUT_RL_Listener);


        rootView.startAnimation(fadeInIntro);
    }

    private void setupAd() {
        MobileAds.initialize(this, Keys.ADDMOB_APP_ID);
    }

    private void setupClickListeners() {
        encryptBtn = findViewById(R.id.main_action_encrypt);
        decryptBtn = findViewById(R.id.main_action_decrypt);
        savedBtn   = findViewById(R.id.main_action_saved);

        encryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = Keys.ACTION_ENCRYPT;
                fromActionToTypeAnim();
            }
        });
        decryptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = Keys.ACTION_DECRYPT;
                fromActionToTypeAnim();
            }
        });
        savedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action = Keys.ACTION_SAVED;
                fromActionToTypeAnim();
            }
        });

        settingsBtn = findViewById(R.id.main_menu_btn_settings);
        contactBtn  = findViewById(R.id.main_menu_btn_contact);
        shareBtn    = findViewById(R.id.main_menu_btn_share);

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingsActivity();
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareActivity();
            }
        });
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactActivity();
            }
        });

        textBtn  = findViewById(R.id.main_type_text);
        imageBtn = findViewById(R.id.main_type_image);
        backBtn  = findViewById(R.id.main_back_btn);

        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = Keys.TYPE_TEXT;
                openInputActivity();
            }
        });
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = Keys.TYPE_IMAGE;
                openInputActivity();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromTypeToActionAnim();
            }
        });
    }

    private void openContactActivity() {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    private void openInputActivity() {
        if(action.equals(Keys.ACTION_SAVED)){
            Intent intent = new Intent(this, SavedActivity.class);
            intent.putExtra(Keys.KEY_TYPE, type);
            startActivity(intent);
        }
        else if(action.equals(Keys.ACTION_DECRYPT) && type.equals(Keys.TYPE_IMAGE)){
            Intent intent = new Intent(this, SavedActivity.class);
            intent.putExtra(Keys.KEY_TYPE, type);
            intent.putExtra(Keys.KEY_PICKUP, 1);
            intent.putExtra(Keys.KEY_ACTION, Keys.ACTION_DECRYPT);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, GetInputActivity.class);
            intent.putExtra(Keys.KEY_ACTION, action);
            intent.putExtra(Keys.KEY_TYPE, type);
            startActivity(intent);
        }
    }

    private void fromTypeToActionAnim() {
        typeSelection = false;
        typeLayout.startAnimation(outroLR);
    }

    private void fromActionToTypeAnim() {
        typeSelection = true;
        actionLayout.startAnimation(outroRL);
    }


    @Override
    public void onBackPressed() {
        if(typeSelection == true) fromTypeToActionAnim();
        else super.onBackPressed();
    }

    public void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openShareActivity() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey, look at this app, it is great, make sure to check it out :\nhttps://play.google.com/store/apps/details?id=be.patricegautot.ncrypt";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "NCRYPT");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private Animation.AnimationListener OUT_LR_Listener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            typeLayout.setVisibility(View.INVISIBLE);
            actionLayout.setVisibility(View.VISIBLE);
            actionLayout.startAnimation(introLR);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    private Animation.AnimationListener OUT_RL_Listener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            actionLayout.setVisibility(View.INVISIBLE);
            typeLayout.setVisibility(View.VISIBLE);
            typeLayout.startAnimation(introRL);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
