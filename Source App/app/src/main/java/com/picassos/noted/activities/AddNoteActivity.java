package com.picassos.noted.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.picassos.noted.R;
import com.picassos.noted.constants.Constants;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Category;
import com.picassos.noted.entities.Note;
import com.picassos.noted.receivers.ReminderReceiver;
import com.picassos.noted.sheets.AttachImageBottomSheetModal;
import com.picassos.noted.sheets.CategoriesBottomSheetModal;
import com.picassos.noted.sheets.MoreActionsBottomSheetModal;
import com.picassos.noted.sheets.ReminderBottomSheetModal;
import com.picassos.noted.utils.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddNoteActivity extends AppCompatActivity implements MoreActionsBottomSheetModal.OnDeleteListener,
        CategoriesBottomSheetModal.OnChooseListener, RewardedVideoAdListener, ReminderBottomSheetModal.OnAddListener,
        ReminderBottomSheetModal.OnRemoveListener, AttachImageBottomSheetModal.OnChooseImageListener,
        AttachImageBottomSheetModal.OnChooseVideoListener, MoreActionsBottomSheetModal.OnLockListener,
        MoreActionsBottomSheetModal.OnSpeechInputListener {

    // Bundle
    private Bundle bundle;

    // REQUEST CODES
    private static final int REQUEST_DELETE_NOTE_CODE = 3;
    private static final int REQUEST_DISCARD_NOTE_CODE = 4;
    private static final int REQUEST_VIEW_NOTE_IMAGE = 5;
    private static final int REQUEST_VIEW_NOTE_VIDEO = 6;

    /**
     * note fields added to activity
     * note title for note title
     * note subtitle for note subtitle
     * note description for note text & content
     * note created at for note date time
     */
    private EditText noteTitle;
    private EditText noteSubtitle;
    private EditText noteDescription;
    private TextView noteCreatedAt;

    // Save Note
    private Button noteSave;

    /**
     * note attachments
     * 1. note image
     * 2. note video
     * 3. note web url
     */
    private RoundedImageView noteImage;
    private RoundedImageView noteVideo;
    private LinearLayout noteWebUrlContainer;
    private TextView noteWebUrl;

    /**
     * default selected data
     * 1. default selected note color
     * 2. default selected image path
     * 3. default selected video path
     * 4. default selected category
     * 5. default lock state
     * 6. default reminder set
     * 7. default selected image uri
     */
    private String selectedNoteColor;
    private String selectedImagePath;
    private String selectedVideoPath;
    private int selectedNoteCategory;
    private boolean isLocked = false;
    private String reminderSet;
    private Uri selectedImageUri;

    /**
     * attach url dialog, custom dialog.
     * @Dialog attach_link_dialog for the dialog class
     * @Button add_link for add link button
     * @EditText link for the link field
     */
    private Dialog attachLinkDialog;
    private Button addLink;
    private EditText link;

    /**
     * preset note takes a Note class.
     * we use this var to check whether
     * the note is available to be edited
     * or viewed by the user.
     */
    private Note presetNote;

    /**
     * alarm manager for reminders
     */
    private AlarmManager alarm;
    private long alarmStartTime;
    PendingIntent reminderIntent;

    private RewardedVideoAd rewardedVideoAd;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_add_note);

        // initialize bundle
        bundle = new Bundle();

        if (Constants.ENABLE_GOOGLE_ADMOB_ADS) {
            // Use an activity context to get the rewarded video instance.
            rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            rewardedVideoAd.setRewardedVideoAdListener(this);

            loadRewardedVideoAd();
        }

        // alarm manager
        alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmStartTime = 0;
        reminderSet = "";

        /* note title, text watcher is added
         * to note title to check if it's !Empty
         * then enables the save button. */
        noteTitle = findViewById(R.id.note_title);
        noteTitle.addTextChangedListener(noteTitleTextWatcher);

        // note created at
        noteCreatedAt = findViewById(R.id.note_created_at);
        noteCreatedAt.setText(
                new SimpleDateFormat("MM.dd.yyyy, HH:mm a", Locale.getDefault())
                        .format(new Date()));

        // note subtitle
        noteSubtitle = findViewById(R.id.note_subtitle);

        // note description
        noteDescription = findViewById(R.id.note_description);

        /* note category start */

        selectedNoteCategory = 0;

        /* note category end */

        /* note image start */

        selectedImagePath = "";
        selectedImageUri = Uri.EMPTY;
        noteImage = findViewById(R.id.note_image);
        noteImage.setOnClickListener(v -> {
            Intent intent = new Intent(AddNoteActivity.this, ViewAttachedImageActivity.class);
            intent.putExtra("image_path", selectedImagePath);
            startActivityForResult(intent, REQUEST_VIEW_NOTE_IMAGE);
        });

        ImageView attachImage = findViewById(R.id.attach_image);
        attachImage.setOnClickListener(v -> {
            AttachImageBottomSheetModal attachImageBottomSheetModal = new AttachImageBottomSheetModal();
            attachImageBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        attachImage.setOnLongClickListener(v -> {
            Toast.makeText(this,  getString(R.string.attach_image), Toast.LENGTH_SHORT).show();
            return true;
        });

        findViewById(R.id.note_image_share).setOnClickListener(v -> {
            if (selectedImageUri != null) {
                Intent share = ShareCompat.IntentBuilder.from(this)
                        .setStream(selectedImageUri)
                        .setType("text/html")
                        .getIntent()
                        .setAction(Intent.ACTION_SEND)
                        .setDataAndType(selectedImageUri, "image/*")
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, getString(R.string.share_image)));
            }
        });

        findViewById(R.id.note_image_remove).setOnClickListener(v -> {
            Dialog confirmDialog = new Dialog(this);

            confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            confirmDialog.setContentView(R.layout.popup_confirm);

            // enable dialog cancel
            confirmDialog.setCancelable(true);
            confirmDialog.setOnCancelListener(dialog -> confirmDialog.dismiss());

            // confirm header
            TextView confirmHeader = confirmDialog.findViewById(R.id.confirm_header);
            confirmHeader.setText(getString(R.string.remove_image));

            // confirm text
            TextView confirmText = confirmDialog.findViewById(R.id.confirm_text);
            confirmText.setText(getString(R.string.remove_image_description));

            // confirm allow
            TextView confirmAllow = confirmDialog.findViewById(R.id.confirm_allow);
            confirmAllow.setOnClickListener(v1 -> {
                // remove image
                noteImage.setImageBitmap(null);
                findViewById(R.id.note_image_container).setVisibility(View.GONE);
                selectedImagePath = "";
                // dismiss dialog
                confirmDialog.dismiss();
            });

            // confirm cancel
            TextView confirmCancel = confirmDialog.findViewById(R.id.confirm_deny);
            confirmCancel.setOnClickListener(v2 -> confirmDialog.dismiss());

            if (confirmDialog.getWindow() != null) {
                confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }

            confirmDialog.show();
        });
        /* note image end */

        /* note video start */

        selectedVideoPath = "";
        noteVideo = findViewById(R.id.note_video);
        noteVideo.setOnClickListener(v -> {
            Intent intent = new Intent(AddNoteActivity.this, ViewAttachedVideoActivity.class);
            intent.putExtra("video_path", selectedVideoPath);
            startActivityForResult(intent, REQUEST_VIEW_NOTE_VIDEO);
        });

        findViewById(R.id.note_video_remove).setOnClickListener(v -> {
            Dialog confirmDialog = new Dialog(this);

            confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            confirmDialog.setContentView(R.layout.popup_confirm);

            // enable dialog cancel
            confirmDialog.setCancelable(true);
            confirmDialog.setOnCancelListener(dialog -> confirmDialog.dismiss());

            // confirm header
            TextView confirmHeader = confirmDialog.findViewById(R.id.confirm_header);
            confirmHeader.setText(getString(R.string.remove_video));

            // confirm text
            TextView confirmText = confirmDialog.findViewById(R.id.confirm_text);
            confirmText.setText(getString(R.string.remove_video_description));

            // confirm allow
            TextView confirmAllow = confirmDialog.findViewById(R.id.confirm_allow);
            confirmAllow.setOnClickListener(v1 -> {
                // remove video
                noteVideo.setImageBitmap(null);
                findViewById(R.id.note_video_container).setVisibility(View.GONE);
                selectedVideoPath = "";
                // dismiss dialog
                confirmDialog.dismiss();
            });

            // confirm cancel
            TextView confirmCancel = confirmDialog.findViewById(R.id.confirm_deny);
            confirmCancel.setOnClickListener(v2 -> confirmDialog.dismiss());

            if (confirmDialog.getWindow() != null) {
                confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            }

            confirmDialog.show();
        });

        /* note video end */

        /* note web url start */
        noteWebUrlContainer = findViewById(R.id.note_web_url_container);
        noteWebUrl = findViewById(R.id.note_web_url);

        ImageView attachLink = findViewById(R.id.attach_link);
        attachLink.setOnClickListener(v -> requestAttachLink());
        attachLink.setOnLongClickListener( v -> {
            Toast.makeText(this, getString(R.string.attach_link), Toast.LENGTH_SHORT).show();
            return true;
        });

        // remove link
        findViewById(R.id.note_web_url_remove).setOnClickListener(v -> {
            noteWebUrl.setText(null);
            noteWebUrlContainer.setVisibility(View.GONE);
        });

        /* note web url end */

        // choose category
        findViewById(R.id.choose_category).setOnClickListener(v -> {
            CategoriesBottomSheetModal categoriesBottomSheetModal = new CategoriesBottomSheetModal();
            categoriesBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        findViewById(R.id.choose_category).setOnLongClickListener(v -> {
            Toast.makeText(this, getString(R.string.choose_category), Toast.LENGTH_SHORT).show();
            return true;
        });

        // save note button
        noteSave = findViewById(R.id.note_save);
        noteSave.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(noteTitle.getText())) {
                if (TextUtils.isEmpty(noteSubtitle.getText().toString()) || TextUtils.isEmpty(noteDescription.getText().toString())) {
                    Toast.makeText(this, getString(R.string.note_fields_required), Toast.LENGTH_SHORT).show();
                } else {
                    saveNote( noteTitle.getText().toString(), noteCreatedAt.getText().toString(), noteSubtitle.getText().toString(), selectedNoteColor, noteDescription.getText().toString(), selectedImagePath, selectedImageUri.toString(), selectedVideoPath, isLocked);
                }
            } else {
                Toast.makeText(this, getString(R.string.note_title_required), Toast.LENGTH_SHORT).show();
            }
        });

        // return back and finish activity
        ImageView goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(noteTitle.getText())) {
                if (TextUtils.isEmpty(noteSubtitle.getText().toString()) || TextUtils.isEmpty(noteDescription.getText().toString())) {
                    finish();
                } else {
                    saveNote( noteTitle.getText().toString(), noteCreatedAt.getText().toString(), noteSubtitle.getText().toString(), selectedNoteColor, noteDescription.getText().toString(), selectedImagePath, selectedImageUri.toString(), selectedVideoPath, isLocked);
                }
            } else {
                finish();
            }
        });

        /* check if the note is preset (available or exist) */
        if (getIntent().getBooleanExtra("modifier", false)) {
            presetNote = (Note) getIntent().getSerializableExtra("note");
            bundle.putSerializable("note_data", presetNote);
            isModifier();
        }

        // more actions sheet
        findViewById(R.id.more_actions).setOnClickListener(v -> {
            MoreActionsBottomSheetModal moreActionsBottomSheetModal = new MoreActionsBottomSheetModal();
            moreActionsBottomSheetModal.setArguments(bundle);
            moreActionsBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        /* add reminder start */

        findViewById(R.id.add_reminder).setOnClickListener(v -> {
            bundle.putString("REMINDER_SET", reminderSet);

            ReminderBottomSheetModal reminderBottomSheetModal = new ReminderBottomSheetModal();
            reminderBottomSheetModal.setArguments(bundle);
            reminderBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        /* add reminder end */

        // set default note color
        selectedNoteColor = getString(R.color.note_theme_one);

        /* note themes start */
        findViewById(R.id.note_theme_one).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_one);
            setNoteColor();
        });

        findViewById(R.id.note_theme_two).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_two);
            setNoteColor();
        });

        findViewById(R.id.note_theme_three).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_three);
            setNoteColor();
        });

        findViewById(R.id.note_theme_four).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_four);
            setNoteColor();
        });

        findViewById(R.id.note_theme_five).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_five);
            setNoteColor();
        });

        findViewById(R.id.note_theme_six).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_six);
            setNoteColor();
        });

        findViewById(R.id.note_theme_seven).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_seven);
            setNoteColor();
        });

        findViewById(R.id.note_theme_eight).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_eight);
            setNoteColor();
        });

        findViewById(R.id.note_theme_nine).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_nine);
            setNoteColor();
        });

        findViewById(R.id.note_theme_ten).setOnClickListener(v -> {
            selectedNoteColor = getString(R.color.note_theme_ten);
            setNoteColor();
        });
        /* note themes end */

        // set preset note color
        setNoteColorIfPreset();

    }

    /**
     * save not into room database
     * table = notes, request_insert.
     * @param title for note title
     * @param created_at for note date
     * @param sub_title for note subtitle
     * @param color for note color
     * @param description for note description
     * @param image for image attachment
     * @param video for video attachment
     */
    private void saveNote(String title, String created_at, String sub_title, String color, String description, String image, String image_uri, String video, boolean locked) {
        final Note note = new Note();

        note.setNote_title(title);
        note.setNote_created_at(created_at);
        note.setNote_subtitle(sub_title);
        note.setNote_color(color);
        note.setNote_description(description);
        note.setNote_image_path(image);
        note.setNote_image_uri(image_uri);
        note.setNote_video_path(video);
        note.setNote_category_id(selectedNoteCategory);
        note.setNote_reminder(reminderSet);
        note.setNote_locked(locked);

        if (noteWebUrlContainer.getVisibility() == View.VISIBLE) {
            note.setNote_web_link(noteWebUrl.getText().toString());
        }

        if (presetNote != null) {
            note.setNote_id(presetNote.getNote_id());
        }

        if (alarmStartTime != 0) {
            Objects.requireNonNull(alarm).set(AlarmManager.RTC_WAKEUP, alarmStartTime, reminderIntent);
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                APP_DATABASE.requestDatabase(getApplicationContext()).dao().request_insert_note(note);
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
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }

        new SaveNoteTask().execute();
    }

    /**
     * text watcher enables save
     * button when note title is not
     * empty, and other required fields
     */
    private final TextWatcher noteTitleTextWatcher = new TextWatcher() {
        @SuppressLint("SetTextI18n")
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            noteSave.setEnabled(!TextUtils.isEmpty(noteTitle.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * text watcher enables add link
     * button when link field is not empty
     */
    private final TextWatcher noteLinkTextWatcher = new TextWatcher() {
        @SuppressLint("SetTextI18n")
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addLink.setEnabled(!TextUtils.isEmpty(link.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * set note item background
     * color, add default selected
     * grey color for light & dark
     */
    private void setNoteColor() {
        GradientDrawable shape = (GradientDrawable) noteDescription.getBackground();
        shape.setColor(Color.parseColor(selectedNoteColor));
    }

    /**
     * set note item background
     * color if preset note color
     * resource is found
     */
    private void setNoteColorIfPreset() {
        if (presetNote != null && presetNote.getNote_color() != null && !presetNote.getNote_color().trim().isEmpty()) {
            switch (presetNote.getNote_color()) {
                case "#fffee7ab":
                    findViewById(R.id.note_theme_one).performClick();
                    break;
                case "#ffffdbc3":
                    findViewById(R.id.note_theme_two).performClick();
                    break;
                case "#ffffc5d1":
                    findViewById(R.id.note_theme_three).performClick();
                    break;
                case "#ffe7d0f9":
                    findViewById(R.id.note_theme_four).performClick();
                    break;
                case "#ffcdccfe":
                    findViewById(R.id.note_theme_five).performClick();
                    break;
                case "#ffb5e9d3":
                    findViewById(R.id.note_theme_six).performClick();
                    break;
                case "#ffb3e5fd":
                    findViewById(R.id.note_theme_seven).performClick();
                    break;
                case "#ffb5d8ff":
                    findViewById(R.id.note_theme_eight).performClick();
                    break;
                case "#ffe5e5e5":
                    findViewById(R.id.note_theme_nine).performClick();
                    break;
                case "#ffbcbcbc":
                    findViewById(R.id.note_theme_ten).performClick();
                    break;
            }
        }
    }

    /**
     * request open attach
     * link popup. after checking
     * the validation of the link,
     * when click on (add link), attach
     * link the note.
     */
    @SuppressLint("SetTextI18n")
    private void requestAttachLink() {
        attachLinkDialog = new Dialog(this);

        attachLinkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        attachLinkDialog.setContentView(R.layout.popup_attach_link);

        // enable dialog cancel
        attachLinkDialog.setCancelable(true);
        attachLinkDialog.setOnCancelListener(dialog -> attachLinkDialog.dismiss());

        // attachment link
        link = attachLinkDialog.findViewById(R.id.link);
        link.addTextChangedListener(noteLinkTextWatcher);
        link.requestFocus();

        // add link to note
        addLink = attachLinkDialog.findViewById(R.id.add_link);
        addLink.setOnClickListener(v -> {
            if (link.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, getString(R.string.link_url_empty), Toast.LENGTH_SHORT).show();
            } else if (!Patterns.WEB_URL.matcher(link.getText().toString()).matches()) {
                Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_SHORT).show();
            } else {
                noteWebUrl.setText(link.getText().toString());
                noteWebUrlContainer.setVisibility(View.VISIBLE);

                attachLinkDialog.dismiss();
            }
        });

        // cancel link
        TextView cancelLink = attachLinkDialog.findViewById(R.id.cancel_link);
        cancelLink.setOnClickListener(v -> attachLinkDialog.dismiss());

        if (attachLinkDialog.getWindow() != null) {
            attachLinkDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            attachLinkDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            attachLinkDialog.getWindow().getAttributes().windowAnimations = R.style.DetailAnimation;
            Window window = attachLinkDialog.getWindow();
            WindowManager.LayoutParams WLP = window.getAttributes();
            WLP.gravity = Gravity.BOTTOM;
            window.setAttributes(WLP);
        }

        attachLinkDialog.show();
    }

    /**
     * request open reminder popup
     * @param title for note title
     * @param subtitle for note subtitle
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private void requestOpenReminder(String title, String subtitle) {
        Dialog addReminderDialog = new Dialog(this);

        addReminderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        addReminderDialog.setContentView(R.layout.popup_reminder);

        // enable dialog cancel
        addReminderDialog.setCancelable(true);
        addReminderDialog.setOnCancelListener(dialog -> addReminderDialog.dismiss());

        // time picker
        TimePicker timePicker = addReminderDialog.findViewById(R.id.time_picker);

        // confirm allow
        TextView confirmAllow = addReminderDialog.findViewById(R.id.confirm_allow);
        confirmAllow.setOnClickListener(v1 -> {
            Intent intent = new Intent(AddNoteActivity.this, ReminderReceiver.class);
            intent.putExtra("notificationId", 1);
            intent.putExtra("title", title);
            intent.putExtra("subtitle", subtitle);

            // getBroadcast(context, requestCode, intent, flags);
            reminderIntent = PendingIntent.getBroadcast(AddNoteActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // get current date
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = simpleDateFormat.format(date);

            // get reminder data
            String am_pm = "";
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();

            // create time
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            startTime.set(Calendar.MINUTE, minute);
            startTime.set(Calendar.SECOND, 0);
            alarmStartTime = startTime.getTimeInMillis();

            if (startTime.get(Calendar.AM_PM) == Calendar.PM) {
                am_pm = "PM";
            } else if (startTime.get(Calendar.AM_PM) == Calendar.AM) {
                am_pm = "AM";
            }

            String formattedHour = (startTime.get(Calendar.HOUR) == 0) ? "12" : Integer.toString(startTime.get(Calendar.HOUR));

            reminderSet = formattedDate + " " + am_pm + " " + formattedHour + ":" + minute;

            findViewById(R.id.reminder_set).setVisibility(View.VISIBLE);
            TextView reminderText = findViewById(R.id.reminder_set_text);
            reminderText.setText(reminderSet);

            Toast.makeText(this, getString(R.string.reminder_set_successfully), Toast.LENGTH_SHORT).show();

            addReminderDialog.dismiss();
        });

        // confirm cancel
        TextView confirmCancel = addReminderDialog.findViewById(R.id.confirm_deny);
        confirmCancel.setOnClickListener(v2 -> addReminderDialog.dismiss());

        if (addReminderDialog.getWindow() != null) {
            addReminderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            addReminderDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            addReminderDialog.getWindow().getAttributes().windowAnimations = R.style.DetailAnimation;
            Window window = addReminderDialog.getWindow();
            WindowManager.LayoutParams WLP = window.getAttributes();
            WLP.gravity = Gravity.BOTTOM;
            window.setAttributes(WLP);
        }

        addReminderDialog.show();
    }

    /**
     * request for the file path
     * @param uri for uri
     * @return file path (images, videos allowed)
     */
    private String requestFilePath(Uri uri) {
        @SuppressLint("Recycle") Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);

        int nameIndex = Objects.requireNonNull(returnCursor).getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File file = new File(getFilesDir(), name);
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = Objects.requireNonNull(inputStream).available();

            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            Log.e("Size", "Size: " + size);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return file.getPath();
    }

    /**
     * check whether the note
     * is preset (available) or
     * not to apply given data
     */
    private void isModifier() {
        noteTitle.setText(presetNote.getNote_title());
        noteSubtitle.setText(presetNote.getNote_subtitle());
        noteDescription.setText(presetNote.getNote_description());
        noteCreatedAt.setText(presetNote.getNote_created_at());
        selectedNoteColor = presetNote.getNote_color();
        selectedNoteCategory = presetNote.getNote_category_id();
        reminderSet = presetNote.getNote_reminder();
        isLocked = presetNote.isNote_locked();

        // check if image attachment is set
        if (presetNote.getNote_image_path() != null && !presetNote.getNote_image_path().trim().isEmpty()
                && presetNote.getNote_image_uri() != null) {
            noteImage.setImageBitmap(BitmapFactory.decodeFile(presetNote.getNote_image_path()));
            findViewById(R.id.note_image_container).setVisibility(View.VISIBLE);
            selectedImagePath = presetNote.getNote_image_path();
            selectedImageUri = Uri.parse(presetNote.getNote_image_uri());
        }

        // check if video attachment is set
        if (presetNote.getNote_video_path() != null && !presetNote.getNote_video_path().trim().isEmpty()) {
            Bitmap video_thumbnail = ThumbnailUtils.createVideoThumbnail(presetNote.getNote_video_path(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            noteVideo.setImageBitmap(video_thumbnail);
            findViewById(R.id.note_video_container).setVisibility(View.VISIBLE);
            selectedVideoPath = presetNote.getNote_video_path();
        }

        // check if link attachment is set
        if (presetNote.getNote_web_link() != null && !presetNote.getNote_web_link().trim().isEmpty()) {
            noteWebUrl.setText(presetNote.getNote_web_link());
            noteWebUrlContainer.setVisibility(View.VISIBLE);
        }

        // check if reminder is set
        if (presetNote.getNote_reminder() != null) {
            if (!presetNote.getNote_reminder().trim().isEmpty()) {
                findViewById(R.id.reminder_set).setVisibility(View.VISIBLE);
                TextView reminderText = findViewById(R.id.reminder_set_text);
                reminderText.setText(presetNote.getNote_reminder());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIEW_NOTE_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                if (Objects.requireNonNull(data.getStringExtra("request")).equals("remove_image")) {
                    // remove image
                    noteImage.setImageBitmap(null);
                    findViewById(R.id.note_image_container).setVisibility(View.GONE);
                    selectedImagePath = "";
                }
            }
        } else if (requestCode == REQUEST_VIEW_NOTE_VIDEO && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getStringExtra("request").equals("remove_video")) {
                    // remove video
                    noteVideo.setImageBitmap(null);
                    findViewById(R.id.note_video_container).setVisibility(View.GONE);
                    selectedVideoPath = "";
                }
            }
        }
    }

    @Override
    public void onDeleteListener(int requestCode) {
        /* check if the returned request code
        * belongs to a deleted note then close
        * the activity and refresh the recycler view */
        if (requestCode == REQUEST_DELETE_NOTE_CODE) {
            Intent intent = new Intent();
            intent.putExtra("is_note_removed", true);
            setResult(RESULT_OK, intent);
            Toast.makeText(this, getString(R.string.note_moved_to_trash), Toast.LENGTH_SHORT).show();
            finish();
        } else if (requestCode == REQUEST_DISCARD_NOTE_CODE) {
            finish();
        }
    }

    @Override
    public void onChooseListener(int requestCode, Category category) {
        int REQUEST_CHOOSE_CATEGORY_CODE = 5;
        if (requestCode == REQUEST_CHOOSE_CATEGORY_CODE) {
            if (category != null) {
                selectedNoteCategory = category.getCategory_id();
                TextView note_category = findViewById(R.id.note_category);
                note_category.setText(category.getCategory_title());

                Toast.makeText(this, category.getCategory_title() + " " + getString(R.string.category_is_selected), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAddListener(int requestCode) {
        if (!TextUtils.isEmpty(noteTitle.getText().toString()) && !TextUtils.isEmpty(noteSubtitle.getText().toString())) {
            requestOpenReminder(noteTitle.getText().toString(), noteSubtitle.getText().toString());
        } else {
            Toast.makeText(this, getString(R.string.note_title_subtitle_empty), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveListener(int requestCode) {
        reminderSet = "";
        findViewById(R.id.reminder_set).setVisibility(View.GONE);
        TextView reminderText = findViewById(R.id.reminder_set_text);
        reminderText.setText("");

        Toast.makeText(this, getString(R.string.reminder_removed), Toast.LENGTH_SHORT).show();
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
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        loadRewardedVideoAd();
        Intent intent = new Intent();
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
        super.onBackPressed();

        if (!TextUtils.isEmpty(noteTitle.getText())) {
            if (TextUtils.isEmpty(noteSubtitle.getText().toString()) || TextUtils.isEmpty(noteDescription.getText().toString())) {
                finish();
            } else {
                saveNote( noteTitle.getText().toString(), noteCreatedAt.getText().toString(), noteSubtitle.getText().toString(), selectedNoteColor, noteDescription.getText().toString(), selectedImagePath, selectedImageUri.toString(), selectedVideoPath, isLocked);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onChooseImageListener(int requestCode, Bitmap bitmap, Uri uri) {
        if (requestCode == AttachImageBottomSheetModal.REQUEST_SELECT_IMAGE_FROM_GALLERY_CODE
                || requestCode == AttachImageBottomSheetModal.REQUEST_CAMERA_IMAGE_CODE) {
            // set image
            noteImage.setImageBitmap(bitmap);
            findViewById(R.id.note_image_container).setVisibility(View.VISIBLE);
            // request image path
            selectedImagePath = requestFilePath(uri);
            // request image uri
            selectedImageUri = uri;
        }
    }

    @Override
    public void onChooseVideoListener(int requestCode, Uri uri) {
        if (requestCode == AttachImageBottomSheetModal.REQUEST_SELECT_VIDEO_FROM_GALLERY_CODE) {
            // request video thumbnail
            Bitmap video_thumbnail = ThumbnailUtils.createVideoThumbnail(requestFilePath(uri), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            // set video image
            noteVideo.setImageBitmap(video_thumbnail);
            findViewById(R.id.note_video_container).setVisibility(View.VISIBLE);
            // request video path
            selectedVideoPath = requestFilePath(uri);
        }
    }

    @Override
    public void onLockListener(int requestCode) {
        if (requestCode == MoreActionsBottomSheetModal.REQUEST_LOCK_NOTE_CODE) {
            isLocked = true;
        } else if (requestCode == MoreActionsBottomSheetModal.REQUEST_UNLOCK_NOTE_CODE) {
            isLocked = false;
        }
    }

    @Override
    public void onSpeechInputListener(int requestCode, String text) {
        if (requestCode == MoreActionsBottomSheetModal.REQUEST_SPEECH_INPUT_CODE) {
            noteDescription.append(" " + text);
        }
    }
}