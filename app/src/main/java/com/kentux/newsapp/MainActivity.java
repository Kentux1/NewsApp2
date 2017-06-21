package com.kentux.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private TextView mEmptyStateTextView;

    private static NewsAdapter mAdapter;

    private static LoaderManager loaderManager;

    ProgressBar loadingIndicator;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        String THE_GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?";
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String searchQuery = sharedPrefs.getString(
                getString(R.string.search_query_key),
                getString(R.string.search_query_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.order_by_key),
                getString(R.string.order_by_deafult));
        String myApiKey = getString(R.string.my_api_key);
        Uri baseUri = Uri.parse(THE_GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", myApiKey);
        Log.v("MainActivity", "Uri: " + uriBuilder);

        return new NewsLoader(this, uriBuilder.toString());
    }



    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_news);

        mAdapter.clear();

        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView newsListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshListView();
            }
        });
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        newsListView.setAdapter(mAdapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentNews = mAdapter.getItem(position);
                if (currentNews != null) {
                    Uri newsUri = Uri.parse(currentNews.getUrl());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                    if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(websiteIntent);
                    }
                }
            }
        });



        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(1, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.menu_refresh) {
            refreshListView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshListView() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Show loading indicator
            loadingIndicator.setVisibility(View.VISIBLE);
            mEmptyStateTextView.setText("");

            // Check if mAdapter is not null (which will happen if on launch there was no
            // connection)
            if (mAdapter != null) {
                // Clear the adapter
                mAdapter.clear();
            }
            if (loaderManager != null) {
                // Restart Loader
                loaderManager.restartLoader(1, null, this);
                // Inform SwipeRefreshLayout that loading is complete so it can hide its progress bar
                swipeRefreshLayout.setRefreshing(false);
            } else {
                loaderManager = getLoaderManager();
                loaderManager.initLoader(1, null, this);
                ListView newsListView = (ListView) findViewById(R.id.list);
                mAdapter = new NewsAdapter(this, new ArrayList<News>());
                newsListView.setAdapter(mAdapter);
                // Inform SwipeRefreshLayout that loading is complete so it can hide its progress bar
                swipeRefreshLayout.setRefreshing(false);
            }

        } else {
            // Hide progressBar
            loadingIndicator.setVisibility(View.GONE);

            // Check if mAdapter is not null (which will happen if on launch there was no
            // connection)
            if (mAdapter != null) {
                // Clear the adapter
                mAdapter.clear();
            }
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}
