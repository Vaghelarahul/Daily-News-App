package com.example.android.newsstand;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.newsstand.data.NewsContract;
import com.example.android.newsstand.utilities.ImageUtils;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    private Cursor mCursor;
    private Context mContext;

    private NewsItemClickHandler mOnClickHandler;

    public interface NewsItemClickHandler {
        void onItemClick(String webLink);
    }

    public NewsAdapter(Context context, NewsItemClickHandler onClickHandler) {
        this.mContext = context;
        this.mOnClickHandler = onClickHandler;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        boolean attachToRootImmediately = false;

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, attachToRootImmediately);

        NewsViewHolder viewHolder = new NewsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String heading = mCursor.getString(mCursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_TITLE));
        String description = mCursor.getString(mCursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_DESCRIPTION));
        byte[] imageByte = mCursor.getBlob(mCursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_IMAGE));

        Drawable drawableImage = null;
        if (imageByte != null) {
            Bitmap bitmapImage = ImageUtils.getImageBitmap(imageByte);
            drawableImage = new BitmapDrawable(mContext.getResources(), bitmapImage);

            if (Build.VERSION.SDK_INT >= 16) {
                holder.imageListItem.setBackground(drawableImage);
            }
        }

        holder.titleListItem.setText(heading);
        holder.typeListItem.setText(description);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageListItem;
        TextView titleListItem;
        TextView typeListItem;

        public NewsViewHolder(View itemView) {
            super(itemView);

            imageListItem = (ImageView) itemView.findViewById(R.id.news_image);
            titleListItem = (TextView) itemView.findViewById(R.id.news_title);
            typeListItem = (TextView) itemView.findViewById(R.id.content_type);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            mCursor.moveToPosition(clickedPosition);
            String webLink = mCursor.getString(mCursor.getColumnIndex(NewsContract.NewsEntry.COLUMN_NEWS_URL));
            mOnClickHandler.onItemClick(webLink);
        }
    }
}
