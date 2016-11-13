package test.com.implude.mediaprojectionexample;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class ListActivity extends AppCompatActivity{
    private Cursor moviecursor;
    private int movie_column_index;
    private int count;
    private MediaPlayer mMediaPlayer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        init_phone_movie_grid();
    }
    public static void createTumbnail(Bitmap bitmap, File FilePath, String filename){
        File file = new File(String.valueOf(FilePath));
        if(!file.exists()){
            file.mkdirs();
        }
        File fileCacheItem = new File(FilePath + filename);
        OutputStream out = null;
        try {
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap = Bitmap.createScaledBitmap(bitmap, 120, height/(width/60), true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                out.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void init_phone_movie_grid(){
        System.gc();
        String[] proj = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME,MediaStore.Video.Media.SIZE};
        moviecursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
        if (moviecursor != null) {
            count = moviecursor.getCount();
        }
        ListView movielist = (ListView) findViewById(R.id.PhoneMediaList);
        movielist.setAdapter(new VideoAdapter(getApplicationContext()));
        mMediaPlayer = new MediaPlayer();
        movielist.setOnItemClickListener(moviegridlistener);
    }
    private AdapterView.OnItemClickListener moviegridlistener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            System.gc();
            movie_column_index = moviecursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            moviecursor.moveToPosition(i);
            String filename = moviecursor.getString(movie_column_index);
            try{
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.reset();
                    mMediaPlayer.release();
                }
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(filename);
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                    Intent intent = new Intent(ListActivity.this, Video.class);
                    startActivity(intent);
                    mMediaPlayer.prepareAsync();
            }catch (Exception ignored){

            }
        }
    };
    private class VideoAdapter extends BaseAdapter {
        private Context mContext;
        public VideoAdapter(Context c){
            mContext = c;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        public int getCount(){
            return count;
        }
        public Object getItem(int position){
            return position;
        }
        public long getItemId(int position){
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            System.gc();
            View row = convertView;
            TextView tv;
            ImageView imageView;
            String image = "image";
            String id;
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/video.mp4",
                    MediaStore.Video.Thumbnails.MINI_KIND);
            createTumbnail(thumb, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), image);
            movie_column_index = moviecursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            moviecursor.moveToPosition(position);
            id = moviecursor.getString(movie_column_index);
            if(row==null){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.listviewitem, null);
                tv = (TextView)row.findViewById(R.id.textView);
                imageView = (ImageView)row.findViewById(R.id.image);
                imageView.setImageBitmap(thumb);
                tv.setText(id);
            }else {
                tv = (TextView) row.findViewById(R.id.textView);
                imageView = (ImageView) row.findViewById(R.id.image);
                imageView.setImageBitmap(thumb);
                tv.setText(id);
            }
            return row;
        }
    }

}
