package com.kentux.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class NewsAdapter extends ArrayAdapter<News> {
    private static final String LOG_TAG = NewsAdapter.class.getName();

    NewsAdapter(@NonNull Context context, @NonNull List<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        News currentNews = getItem(position);

        TextView categoryView = (TextView) convertView.findViewById(R.id.category);
        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        TextView dateView = (TextView) convertView.findViewById(R.id.date_text);
        TextView timeView = (TextView) convertView.findViewById(R.id.time);

        if (currentNews != null) {
            categoryView.setText(currentNews.getNewsCategory());

            titleView.setText(currentNews.getNewsTitle());

            dateView.setText(formatDate(currentNews.getNewsPublicationDate()));

            timeView.setText(formatTime(currentNews.getNewsPublicationDate()));
        }
        return convertView;
    }

    private String formatDate(String input) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        String date = "";
        try {
            Date newDate = format.parse(input);
            format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            date = format.format(newDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Could not parse current date");
        }
        return date;
    }

    private String formatTime(String input) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        String time = "";
        try {
            Date newTime = format.parse(input);
            format = new SimpleDateFormat("HH:mm", Locale.US);
            time = format.format(newTime);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Could not parse current date");
        }
        return time;
    }
}
