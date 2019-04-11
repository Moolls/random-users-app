package ru.moolls.randomusers.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.moolls.randomusers.R;
import ru.moolls.randomusers.tools.ImageDownloader;
import ru.moolls.randomusers.service.RandomUserService;
import ru.moolls.randomusers.service.entity.User;

public class UserListActivity extends AppCompatActivity {

    private Button mLoadButton;
    private ListView mUserListView;
    private ArrayAdapter<User> userListAdapter;

    private List<User> currentUsers;


    private void setLoadingState() {
        mLoadButton.setEnabled(false);
        mLoadButton.setText(R.string.loading_button_loading);
    }

    private void setInitialStateButton() {
        mLoadButton.setEnabled(true);
        mLoadButton.setText(R.string.loading_button);
    }

    private void initViews() {
        setContentView(R.layout.activity_user_list);
        mLoadButton = findViewById(R.id.loadButton);
        mUserListView = findViewById(R.id.userListView);
    }

    private void startUserActivity(User user) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(UserActivity.EXTRA_USER, user);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        currentUsers = new ArrayList<>();
        userListAdapter = new UserAdapter(this, R.layout.user_list_item, currentUsers);
        mUserListView.setAdapter(userListAdapter);

        mLoadButton.setOnClickListener(v -> {
            new RandomUserService().getAllUsers()
                    .subscribe(new UserObserver());
        });

        mUserListView.setOnItemClickListener((parent, view, position, id) -> {
            User user = (User) parent.getItemAtPosition(position);
            startUserActivity(user);
        });
    }


    class UserAdapter extends ArrayAdapter<User> {
        public UserAdapter(Context context, int layoutId, List<User> objects) {
            super(context, layoutId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.user_list_item, parent, false);
            TextView userNameView = (TextView) row.findViewById(R.id.itemUsername);
            ImageView userAvatar = (ImageView) row.findViewById(R.id.itemThumbnail);
            User currentUser = getItem(position);

            userNameView.setText(currentUser.toString());
            Observable<Bitmap> bitmapObservable = ImageDownloader.loadImage(currentUser.getPhotoUrl());
            if (currentUser.getPhotoBitmap() == null) {
                bitmapObservable.subscribe(bitmap -> UserListActivity.this.runOnUiThread(() -> {
                            currentUser.setPhotoBitmap(bitmap);
                            userAvatar.setImageBitmap(bitmap);
                        }
                ));
            } else {
                userAvatar.setImageBitmap(currentUser.getPhotoBitmap());
            }
            return row;
        }
    }

    class UserObserver implements Observer<User> {
        @Override
        public void onSubscribe(Disposable d) {
            currentUsers.clear();
            UserListActivity.this.runOnUiThread(UserListActivity.this::setLoadingState);
            UserListActivity.this.runOnUiThread(userListAdapter::notifyDataSetChanged);
        }

        @Override
        public void onNext(User user) {
            currentUsers.add(user);
            UserListActivity.this.runOnUiThread(userListAdapter::notifyDataSetChanged);
        }

        @Override
        public void onError(Throwable e) {
            UserListActivity.this.runOnUiThread(UserListActivity.this::setLoadingState);
        }

        @Override
        public void onComplete() {
            UserListActivity.this.runOnUiThread(UserListActivity.this::setInitialStateButton);
        }
    }
}
