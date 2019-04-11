package ru.moolls.randomusers.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.reactivex.Observable;
import ru.moolls.randomusers.R;
import ru.moolls.randomusers.tools.ImageDownloader;
import ru.moolls.randomusers.service.entity.User;
import ru.moolls.randomusers.tools.SharingManager;

public class UserActivity extends AppCompatActivity {

    public static final String EXTRA_USER = "package ru.moolls.randomusers.activity.UserActivity.USER";
    private User currentUser;

    private ImageView mUserPhoto;
    private TextView mUserName;
    private TextView mPhoneData;
    private TextView mEmailData;
    private TextView mNationalData;
    private TextView mCityData;
    private TextView mAddressData;

    private Button mCallButton;
    private Button mEmailButton;

    private void initViews() {
        mUserPhoto = findViewById(R.id.userPhoto);
        mUserName = findViewById(R.id.userName);
        mPhoneData = findViewById(R.id.phoneData);
        mEmailData = findViewById(R.id.emailData);
        mNationalData = findViewById(R.id.nationalData);
        mCityData = findViewById(R.id.cityData);
        mAddressData = findViewById(R.id.addressData);
        mCallButton = findViewById(R.id.callButton);
        mEmailButton = findViewById(R.id.emailButton);
    }

    private void fillLabels() {
        mUserName.setText(getString(R.string.full_name_pattern,
                currentUser.getFirstName(), currentUser.getLastName()));
        mPhoneData.setText(currentUser.getPhone());
        mEmailData.setText(currentUser.getEmail());
        mNationalData.setText(currentUser.getNational());
        mCityData.setText(currentUser.getCity());
        mAddressData.setText(currentUser.getAddress());
    }


    private void initPhoto() {
        Observable<Bitmap> bitmapObservable = ImageDownloader.loadImage(currentUser.getPhotoUrl());
        bitmapObservable.subscribe(bitmap -> this.runOnUiThread(() -> {
            mUserPhoto.setImageBitmap(bitmap);
            currentUser.setPhotoBitmap(bitmap);
        }));
    }

    private void initCurrentUser() {
        currentUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
    }

    private void initButtonListeners() {
        mCallButton.setOnClickListener(v -> {
            String dial = "tel:" + currentUser.getPhone();
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
        });

        mEmailButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{currentUser.getEmail()});
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.share_contact_info_menu_action:
                SharingManager.shareTextInfo(this, currentUser.fullInfo());
                break;

            case R.id.share_photo_menu_action:
                SharingManager.shareImage(this, currentUser.getPhotoBitmap());
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initViews();
        initButtonListeners();
        initCurrentUser();
        fillLabels();
        initPhoto();
    }
}
