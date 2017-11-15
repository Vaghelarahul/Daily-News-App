

package com.example.android.newsstand;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.newsstand.data.NewsContract.NewsEntry;
import com.example.android.newsstand.sync.ScheduleNewsFirebaseJob;
import com.example.android.newsstand.sync.TaskForScheduledJob;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, NewsAdapter.NewsItemClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int LOADER_ID = 112;

    private NewsAdapter mNewsAdapter;
    private static RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScheduleNewsFirebaseJob.scheduleJob(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mNewsAdapter = new NewsAdapter(this, this);
        mRecyclerView.setAdapter(mNewsAdapter);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                NewsEntry._ID,
                NewsEntry.COLUMN_NEWS_TITLE,
                NewsEntry.COLUMN_NEWS_DESCRIPTION,
                NewsEntry.COLUMN_NEWS_URL,
                NewsEntry.COLUMN_NEWS_IMAGE
        };

        CursorLoader cursorLoader = new CursorLoader(
                this,
                NewsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mNewsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewsAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(String webLink) {

        Uri webUri = Uri.parse(webLink);
        Intent intent = new Intent(Intent.ACTION_VIEW, webUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void refreshContent() {

        TaskForScheduledJob.backgroundTask task = new TaskForScheduledJob.backgroundTask();
        task.execute(this);
        Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (R.id.refresh == item.getItemId()) {
            refreshContent();
        }
        return super.onOptionsItemSelected(item);
    }
}
