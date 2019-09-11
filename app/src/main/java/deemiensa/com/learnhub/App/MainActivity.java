package deemiensa.com.learnhub.App;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import deemiensa.com.learnhub.Activities.Login1;
import deemiensa.com.learnhub.Activities.LoginActivity;
import deemiensa.com.learnhub.BaseFragments.Channels;
import deemiensa.com.learnhub.BaseFragments.Docs;
import deemiensa.com.learnhub.BaseFragments.Profile;
import deemiensa.com.learnhub.BaseFragments.Upload;
import deemiensa.com.learnhub.BaseFragments.Video;
import deemiensa.com.learnhub.R;
import deemiensa.com.learnhub.Tasks.MyDownloadService;
import deemiensa.com.learnhub.Tasks.MyUploadService;
import deemiensa.com.learnhub.Utils.SharedPref;
import deemiensa.com.learnhub.Utils.Util;
import deemiensa.com.learnhub.businessobjects.YouTube.POJOs.YouTubeChannel;
import deemiensa.com.learnhub.businessobjects.YouTube.POJOs.YouTubePlaylist;
import deemiensa.com.learnhub.businessobjects.YouTube.VideoBlocker;
import deemiensa.com.learnhub.businessobjects.db.SearchHistoryDb;
import deemiensa.com.learnhub.businessobjects.db.SearchHistoryTable;
import deemiensa.com.learnhub.businessobjects.interfaces.SearchHistoryClickListener;
import deemiensa.com.learnhub.gui.activities.PreferencesActivity;
import deemiensa.com.learnhub.gui.businessobjects.BlockedVideosDialog;
import deemiensa.com.learnhub.gui.businessobjects.MainActivityListener;
import deemiensa.com.learnhub.gui.businessobjects.YouTubePlayer;
import deemiensa.com.learnhub.gui.businessobjects.adapters.SearchHistoryCursorAdapter;
import deemiensa.com.learnhub.gui.fragments.ChannelBrowserFragment;
import deemiensa.com.learnhub.gui.fragments.PlaylistVideosFragment;
import deemiensa.com.learnhub.gui.fragments.SearchVideoGridFragment;

import static deemiensa.com.learnhub.Utils.SharedPref.clearSession;

public class MainActivity extends BaseActivity implements
        BaseFragment.FragmentNavigation,
        FragNavController.TransactionListener,
        FragNavController.RootFragmentListener,
        MainActivityListener {

    @BindView(R.id.content_frame)
    protected FrameLayout contentFrame;

    private Toolbar toolbar;

    // new variables being added
    @Nullable
    @BindView(R.id.fragment_container)
    protected FrameLayout           fragmentContainer;

    private Video mainFragment;
    private SearchVideoGridFragment searchVideoGridFragment;
    private ChannelBrowserFragment channelBrowserFragment;
    /** Fragment that shows Videos from a specific Playlist */
    private PlaylistVideosFragment playlistVideosFragment;
    private MainActivity.VideoBlockerPlugin videoBlockerPlugin;

    private boolean dontAddToBackStack = false;

    /** Set to true of the UpdatesCheckerTask has run; false otherwise. */
    private static boolean updatesCheckerTaskRan = false;

    public static final String ACTION_VIEW_CHANNEL = "MainActivity.ViewChannel";
    public static final String ACTION_VIEW_FEED = "MainActivity.ViewFeed";
    public static final String ACTION_VIEW_PLAYLIST = "MainActivity.ViewPlaylist";
    private static final String MAIN_FRAGMENT   = "MainActivity.MainFragment";
    private static final String SEARCH_FRAGMENT = "MainActivity.SearchFragment";
    private static final String CHANNEL_BROWSER_FRAGMENT = "MainActivity.ChannelBrowserFragment";
    private static final String PLAYLIST_VIDEOS_FRAGMENT = "MainActivity.PlaylistVideosFragment";
    private static final String VIDEO_BLOCKER_PLUGIN = "MainActivity.VideoBlockerPlugin";

    private int[] mTabIconsSelected = {
            R.drawable.ic_video_library,
            R.drawable.ic_library_books,
            R.drawable.ic_add_box,
            R.drawable.ic_mail,
            R.drawable.ic_person,};

    String[] TABS;

    public static TabLayout bottomTabLayout;
    private FragNavController mNavController;
    private FragmentHistory fragmentHistory;

    // firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAG = "Storage#MainActivity";

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;

    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // firebase authentication listener to check if a user is signed in or not
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //contentFrame = (FrameLayout) findViewById(R.id.content_frame);
        bottomTabLayout = findViewById(R.id.bottom_tab_layout);
        TABS = getResources().getStringArray(R.array.tab_name);

        initTab();

        fragmentHistory = new FragmentHistory();

        mNavController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_frame)
                .transactionListener(this)
                .rootFragmentListener(this, TABS.length)
                .build();

        switchTab(0);

        bottomTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                fragmentHistory.push(tab.getPosition());
                switchTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                mNavController.clearStack();

                switchTab(tab.getPosition());
            }
        });

        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }
        onNewIntent(getIntent());

        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:" + intent);
                hideProgressDialog();

                switch (intent.getAction()) {
                    case MyDownloadService.DOWNLOAD_COMPLETED:
                        // Get number of bytes downloaded
                        long numBytes = intent.getLongExtra(MyDownloadService.EXTRA_BYTES_DOWNLOADED, 0);

                        // Alert success
                        showMessageDialog(getString(R.string.success), String.format(Locale.getDefault(),
                                "%d bytes downloaded from %s",
                                numBytes,
                                intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case MyDownloadService.DOWNLOAD_ERROR:
                        // Alert failure
                        showMessageDialog("Error", String.format(Locale.getDefault(),
                                "Failed to download from %s",
                                intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case MyUploadService.UPLOAD_COMPLETED:
                    case MyUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if this Activity was launched by clicking on an upload notification
        if (intent.hasExtra(MyUploadService.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyDownloadService.getIntentFilter());
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initTab() {
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(getTabView(i));
            }
        }
    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item_bottom, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageDrawable(Util.setDrawableSelector(MainActivity.this, mTabIconsSelected[position], mTabIconsSelected[position]));
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void switchTab(int position) {
        mNavController.switchTab(position);
//        updateToolbarTitle(position);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Activity may be destroyed when the devices is rotated, so we need to make sure that the
        // channel play list is holding a reference to the activity being currently in use...
        if (channelBrowserFragment != null)
            channelBrowserFragment.getChannelPlaylistsFragment().setMainActivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       /* getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;*/

        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        // setup the video blocker notification icon which will be displayed in the tool bar
//        videoBlockerPlugin.setupIconForToolBar(menu);

        // setup the SearchView (actionbar)
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint(getString(R.string.search_videos));

        // set the query hints to be equal to the previously searched text
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(final String newText) {
                // if the user does not want to have the search string saved, then skip the below...
                if (SkyTubeApp.getPreferenceManager().getBoolean(getString(R.string.pref_key_disable_search_history), false)
                        ||  newText == null  ||  newText.length() <= 1) {
                    return false;
                }

                SearchHistoryCursorAdapter searchHistoryCursorAdapter = (SearchHistoryCursorAdapter) searchView.getSuggestionsAdapter();
                Cursor cursor = SearchHistoryDb.getSearchHistoryDb().getSearchCursor(newText);

                // if the adapter has not been created, then create it
                if (searchHistoryCursorAdapter == null) {
                    searchHistoryCursorAdapter = new SearchHistoryCursorAdapter(getBaseContext(),
                            R.layout.search_hint,
                            cursor,
                            new String[]{SearchHistoryTable.COL_SEARCH_TEXT},
                            new int[]{android.R.id.text1},
                            0);
                    searchHistoryCursorAdapter.setSearchHistoryClickListener(new SearchHistoryClickListener() {
                        @Override
                        public void onClick(String query) {
                            displaySearchResults(query, searchView);
                        }
                    });
                    searchView.setSuggestionsAdapter(searchHistoryCursorAdapter);
                } else {
                    // else just change the cursor...
                    searchHistoryCursorAdapter.changeCursor(cursor);
                }

                // update the current search string
                searchHistoryCursorAdapter.setSearchBarString(newText);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!SkyTubeApp.getPreferenceManager().getBoolean(SkyTubeApp.getStr(R.string.pref_key_disable_search_history), false)) {
                    // Save this search string into the Search History Database (for Suggestions)
                    SearchHistoryDb.getSearchHistoryDb().insertSearchText(query);
                }

                displaySearchResults(query, searchView);

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeVideo/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        switch (item.getItemId()) {
            /*case R.id.menu_blocker:
                videoBlockerPlugin.onMenuBlockerIconClicked();
                return true;*/
            case R.id.menu_preferences:
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_enter_video_url:
                displayEnterVideoUrlDialog();
                return true;
            case android.R.id.home:
                if(mainFragment == null || !mainFragment.isVisible()) {
                    onBackPressed();
                    return true;
                }
            case R.id.menu_logout:
                mAuth.signOut();
                //clearSession();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mNavController.isRootFragment()) {
            mNavController.popFragment();
        } else {

            if (fragmentHistory.isEmpty()) {
                super.onBackPressed();
            } else {

                if (fragmentHistory.getStackSize() > 1) {

                    int position = fragmentHistory.popPrevious();
                    switchTab(position);
                    updateTabSelection(position);

                } else {
                    switchTab(0);
                    updateTabSelection(0);
                    fragmentHistory.emptyStack();
                }
            }
        }

        if (mainFragment != null  &&  mainFragment.isVisible()) {
            // If the Subscriptions Drawer is open, close it instead of minimizing the app.
            /*if(mainFragment.isDrawerOpen()) {
                mainFragment.closeDrawer();
            } else {*/
                // On Android, when the user presses back button, the Activity is destroyed and will be
                // recreated when the user relaunches the app.
                // We do not want that behaviour, instead then the back button is pressed, the app will
                // be **minimized**.
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            //}
        } else {
            super.onBackPressed();
        }
    }


    private void updateTabSelection(int currentTab) {

        for (int i = 0; i < TABS.length; i++) {
            TabLayout.Tab selectedTab = bottomTabLayout.getTabAt(i);
            if (currentTab != i) {
                selectedTab.getCustomView().setSelected(false);
            } else {
                selectedTab.getCustomView().setSelected(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNavController != null) {
            mNavController.onSaveInstanceState(outState);
        }

        if(mainFragment != null)
            getSupportFragmentManager().putFragment(outState, MAIN_FRAGMENT, mainFragment);
        if(searchVideoGridFragment != null && searchVideoGridFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, SEARCH_FRAGMENT, searchVideoGridFragment);
        if(channelBrowserFragment != null && channelBrowserFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, CHANNEL_BROWSER_FRAGMENT, channelBrowserFragment);
        if(playlistVideosFragment != null && playlistVideosFragment.isVisible())
            getSupportFragmentManager().putFragment(outState, PLAYLIST_VIDEOS_FRAGMENT, playlistVideosFragment);

        // save the video blocker plugin
        outState.putSerializable(VIDEO_BLOCKER_PLUGIN, videoBlockerPlugin);
        outState.putParcelable(KEY_FILE_URI, mFileUri);
        outState.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    @Override
    public void pushFragment(Fragment fragment) {
        if (mNavController != null) {
            mNavController.pushFragment(fragment);
        }
    }

    @Override
    public void onTabTransaction(Fragment fragment, int index) {
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {

            updateToolbar();
        }
    }

    private void updateToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setDisplayShowHomeEnabled(!mNavController.isRootFragment());
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }


    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if (getSupportActionBar() != null && mNavController != null) {

            updateToolbar();
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {

            case FragNavController.TAB1:
                return new Video();
            case FragNavController.TAB2:
                return new Docs();
            case FragNavController.TAB3:
                return new Upload();
            case FragNavController.TAB4:
                return new Channels();
            case FragNavController.TAB5:
                return new Profile();
        }
        throw new IllegalStateException("Need to send an index that we know");
    }

//    private void updateToolbarTitle(int position){
//
//
//        getSupportActionBar().setTitle(TABS[position]);
//
//    }

    public void updateToolbarTitle(String title) {

        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onChannelClick(String channelId) {
        Bundle args = new Bundle();
        args.putString(ChannelBrowserFragment.CHANNEL_ID, channelId);
        switchToChannelBrowserFragment(args);
    }

    @Override
    public void onChannelClick(YouTubeChannel channel) {
        Bundle args = new Bundle();
        args.putSerializable(ChannelBrowserFragment.CHANNEL_OBJ, channel);
        switchToChannelBrowserFragment(args);
    }

    private void switchToChannelBrowserFragment(Bundle args) {
        channelBrowserFragment = new ChannelBrowserFragment();
        channelBrowserFragment.getChannelPlaylistsFragment().setMainActivityListener(this);
        channelBrowserFragment.setArguments(args);
        switchToFragment(channelBrowserFragment);
    }

    @Override
    public void onPlaylistClick(YouTubePlaylist playlist) {
        playlistVideosFragment = new PlaylistVideosFragment();
        Bundle args = new Bundle();
        args.putSerializable(PlaylistVideosFragment.PLAYLIST_OBJ, playlist);
        playlistVideosFragment.setArguments(args);
        switchToFragment(playlistVideosFragment);
    }

    /**
     * Hide the virtual keyboard and then switch to the Search HomeVideo Grid Fragment with the selected
     * query to search for videos.
     *
     * @param query Query text submitted by the user.
     */
    private void displaySearchResults(String query, @NotNull final View searchView) {
        // hide the keyboard
        searchView.clearFocus();

        // open SearchVideoGridFragment and display the results
        searchVideoGridFragment = new SearchVideoGridFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SearchVideoGridFragment.QUERY, query);
        searchVideoGridFragment.setArguments(bundle);
        switchToFragment(searchVideoGridFragment);
    }

    private void switchToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.content_frame, fragment);
        if(!dontAddToBackStack)
            transaction.addToBackStack(null);
        else
            dontAddToBackStack = false;
        transaction.commit();
    }


    /**
     * Display the Enter HomeVideo URL dialog.
     */
    private void displayEnterVideoUrlDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_enter_video_url)
                .setTitle(R.string.enter_video_url)
                .setPositiveButton(R.string.play, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // get the inputted URL string
                        final String videoUrl = ((EditText)((AlertDialog) dialog).findViewById(R.id.dialog_url_edittext)).getText().toString();

                        // play the video
                        YouTubePlayer.launch(videoUrl, MainActivity.this);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

        // paste whatever there is in the clipboard (hopefully it is a video url)
        ((EditText) alertDialog.findViewById(R.id.dialog_url_edittext)).setText(getClipboardItem());

        // clear URL edittext button
        alertDialog.findViewById(R.id.dialog_url_clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) alertDialog.findViewById(R.id.dialog_url_edittext)).setText("");
            }
        });
    }

    /**
     * Return the last item stored in the clipboard.
     *
     * @return	{@link String}
     */
    private String getClipboardItem() {
        String              clipboardText    = "";
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // if the clipboard contain data ...
        if (clipboardManager != null  &&  clipboardManager.hasPrimaryClip()) {
            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);

            // gets the clipboard as text.
            clipboardText = item.getText().toString();
        }

        return clipboardText;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * A module/"plugin"/icon that displays the total number of blocked videos.
     */
    private static class VideoBlockerPlugin implements VideoBlocker.VideoBlockerListener,
            BlockedVideosDialog.BlockedVideosDialogListener,
            Serializable {

        private ArrayList<VideoBlocker.BlockedVideo> blockedVideos = new ArrayList<>();
        private transient AppCompatActivity activity = null;


        VideoBlockerPlugin(BaseActivity activity) {
            // notify this class whenever a video is blocked...
            VideoBlocker.setVideoBlockerListener(this);
            this.activity = activity;
        }


        public void setActivity(AppCompatActivity activity) {
            this.activity = activity;
        }


        @Override
        public void onVideoBlocked(VideoBlocker.BlockedVideo blockedVideo) {
            blockedVideos.add(blockedVideo);
            activity.invalidateOptionsMenu();
        }


        /**
         * Setup the video blocker notification icon which will be displayed in the tool bar.
         */
       /* void setupIconForToolBar(final Menu menu) {
            if (getTotalBlockedVideos() > 0) {
                // display a red bubble containing the number of blocked videos
                ActionItemBadge.update(activity,
                        menu.findItem(R.id.menu_blocker),
                        ContextCompat.getDrawable(activity, R.drawable.ic_video_blocker),
                        ActionItemBadge.BadgeStyles.RED,
                        getTotalBlockedVideos());
            } else {
                // Else, set the bubble to transparent.  This is required so that when the user
                // clicks on the icon, the app will be able to detect such click and displays the
                // BlockedVideosDialog (otherwise, the ActionItemBadge would just ignore such clicks.
                ActionItemBadge.update(activity,
                        menu.findItem(R.id.menu_blocker),
                        ContextCompat.getDrawable(activity, R.drawable.ic_video_blocker),
                        new BadgeStyle(BadgeStyle.Style.DEFAULT, com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge, Color.TRANSPARENT, Color.TRANSPARENT, Color.WHITE),
                        "");
            }
        }*/


        void onMenuBlockerIconClicked() {
            new BlockedVideosDialog(activity, this, blockedVideos).show();
        }


        @Override
        public void onClearBlockedVideos() {
            blockedVideos.clear();
            activity.invalidateOptionsMenu();
        }


        /**
         * @return Total number of blocked videos.
         */
        private int getTotalBlockedVideos() {
            return blockedVideos.size();
        }

    }

    private void alertDisplayer(String title,String message, final boolean error){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(!error) {
                            Intent intent = new Intent(MainActivity.this, Profile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
        android.app.AlertDialog ok = builder.create();
        ok.show();
    }

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        mDownloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);

        //updateUI(mAuth.getCurrentUser());
    }
}
