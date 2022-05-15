package com.picassos.noted.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.picassos.noted.R;
import com.picassos.noted.constants.Constants;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Todo;
import com.picassos.noted.sheets.TodoMoveToBottomSheetModal;
import com.picassos.noted.utils.Helper;

public class ViewTodoActivity extends AppCompatActivity implements RewardedVideoAdListener, TodoMoveToBottomSheetModal.OnMoveListener {

    // Request Codes
    private final static int REQUEST_DELETE_TODO_CODE = 1;
    private final static int REQUEST_MARK_TODO_CODE = 2;
    private final static int REQUEST_SAVE_TODO_CODE = 3;

    // States
    private final static int STATE_UNCOMPLETED = 0;
    private final static int STATE_COMPLETED = 1;

    // Bundle
    private Bundle bundle;

    // rewarded video ad
    private RewardedVideoAd rewardedVideoAd;

    // preset to-do
    private Todo presetTodo;

    private EditText todoTitle;
    private EditText todoDetails;
    private TextView todoCreatedAt;
    private SwitchCompat todoPriority;

    // to-do list
    private int todoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_view_todo);

        // initialize bundle
        bundle = new Bundle();

        if (Constants.ENABLE_GOOGLE_ADMOB_ADS) {
            // Use an activity context to get the rewarded video instance.
            rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            rewardedVideoAd.setRewardedVideoAdListener(this);

            loadRewardedVideoAd();
        }

        /* check if the note is preset (available or exist) */
        if (getIntent().getBooleanExtra("modifier", false)) {
            presetTodo = (Todo) getIntent().getSerializableExtra("todo");
            bundle.putSerializable("todo_data", presetTodo);
        }

        // return back and finish activity
        ImageView goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> requestSaveTodo(presetTodo));

        // delete to-do
        ImageView deleteTodo = findViewById(R.id.todo_delete);
        deleteTodo.setOnClickListener(v -> requestDeleteTodo(presetTodo));

        // more options
        ImageView moreOptions = findViewById(R.id.more_options);
        moreOptions.setOnClickListener(v -> {
            bundle.putSerializable("todo_data", presetTodo);
            TodoMoveToBottomSheetModal todoMoveToBottomSheetModal = new TodoMoveToBottomSheetModal();
            todoMoveToBottomSheetModal.setArguments(bundle);
            todoMoveToBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        // to-do list
        todoList = presetTodo.getTodo_list();

        // to-do title
        todoTitle = findViewById(R.id.todo_title);
        todoTitle.setText(presetTodo.getTodo_title());

        // to-do details
        todoDetails = findViewById(R.id.todo_details);
        todoDetails.setText(presetTodo.getTodo_details());

        // to-do created at
        todoCreatedAt = findViewById(R.id.todo_created_at);
        todoCreatedAt.setText(presetTodo.getTodo_created_at());

        // to-do priority
        todoPriority = findViewById(R.id.todo_priority);
        todoPriority.setChecked(presetTodo.isTodo_priority());

        // mark to-do
        TextView markTodo = findViewById(R.id.mark_todo);
        if (presetTodo.isTodo_state()) {
            markTodo.setText(getString(R.string.mark_uncompleted));
        } else {
            markTodo.setText(getString(R.string.mark_completed));
        }
        markTodo.setOnClickListener(v -> {
            if (presetTodo.isTodo_state()) {
                requestMarkTodo(presetTodo, STATE_UNCOMPLETED);
            } else {
                requestMarkTodo(presetTodo, STATE_COMPLETED);
            }
        });
    }

    /**
     * request delete to-do
     * @param todo for to-do
     */
    private void requestDeleteTodo(Todo todo) {
        if (todo != null) {
            @SuppressLint("StaticFieldLeak")
            class DeleteTodoTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    APP_DATABASE.requestDatabase(getApplicationContext()).dao().request_delete_todo(todo);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Intent intent = new Intent();
                    intent.putExtra("requestCode", REQUEST_DELETE_TODO_CODE);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            new DeleteTodoTask().execute();
        }
    }

    /**
     * request mark to-do as completed
     * @param todo for to-do
     */
    private void requestMarkTodo(Todo todo, int state) {
        if (todo != null) {
            @SuppressLint("StaticFieldLeak")
            class MarkTodoTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    APP_DATABASE.requestDatabase(getApplicationContext()).dao().request_mark_todo(todo.getTodo_id(), state);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Intent intent = new Intent();
                    intent.putExtra("requestCode", REQUEST_MARK_TODO_CODE);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            new MarkTodoTask().execute();
        }
    }

    /**
     * request save preset to-do
     * @param todo for to-do
     */
    private void requestSaveTodo(Todo todo) {
        if (todo != null) {
            if (!TextUtils.isEmpty(todoTitle.getText().toString())) {
                todo.setTodo_title(todoTitle.getText().toString());
                todo.setTodo_details(todoDetails.getText().toString());
                todo.setTodo_created_at(todoCreatedAt.getText().toString());
                todo.setTodo_priority(todoPriority.isChecked());
                todo.setTodo_list(todoList);

                @SuppressLint("StaticFieldLeak")
                class SaveTodoTask extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        APP_DATABASE.requestDatabase(getApplicationContext()).dao().request_insert_todo(todo);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (Constants.ENABLE_GOOGLE_ADMOB_ADS) {
                            if (rewardedVideoAd.isLoaded()) {
                                rewardedVideoAd.show();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra("requestCode", REQUEST_SAVE_TODO_CODE);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("requestCode", REQUEST_SAVE_TODO_CODE);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }

                new SaveTodoTask().execute();
            } else {
                Toast.makeText(this, getString(R.string.todo_title_required), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * load Google AdMob rewarded ad
     */
    private void loadRewardedVideoAd() {
        rewardedVideoAd.loadAd(Constants.GOOGLE_ADMOB_REWARDED_AD_UNIT_ID,
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d("AdMob", "Ad Loaded");
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d("AdMob", "Ad Opened");
    }

    @Override
    public void onRewardedVideoStarted() {
        Log.d("AdMob", "Ad Started");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        Intent intent = new Intent();
        intent.putExtra("requestCode", REQUEST_SAVE_TODO_CODE);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        loadRewardedVideoAd();
        Intent intent = new Intent();
        intent.putExtra("requestCode", REQUEST_SAVE_TODO_CODE);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d("AdMob", "User Left The Ad");
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d("Google AdMob", "Failed to load rewarded add. Please check your parameters!");
    }

    @Override
    public void onRewardedVideoCompleted() {
        Log.d("AdMob", "Ad Completed Successfully!");
    }

    @Override
    public void onBackPressed() {
        requestSaveTodo(presetTodo);
    }

    @Override
    public void onMoveListener(int requestCode, int identifier) {
        if (requestCode == TodoMoveToBottomSheetModal.REQUEST_MOVE_TASK_CODE) {
            todoList = identifier;
        }
    }
}