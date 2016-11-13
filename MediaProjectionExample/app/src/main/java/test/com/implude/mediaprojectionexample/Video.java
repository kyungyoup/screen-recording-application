package test.com.implude.mediaprojectionexample;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * Created by user on 2016-11-06.
 */
public class Video extends AppCompatActivity{
    private boolean doubleBackToExitPressedOnce = false;
    private MediaPlayer mediaPlayer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video);
        try {
            VideoViewMethod();
        }catch (Exception e){
            jumpMain();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
    public void VideoViewMethod(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        final VideoView videoView = (VideoView)findViewById(R.id.video_view);
        videoView.setVideoPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/video.mp4");
        final android.widget.MediaController mediaController = new android.widget.MediaController(this);
        videoView.setMediaController(new MediaController(this) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
                    ((Activity) getContext()).finish();
                return super.dispatchKeyEvent(event);
            }
        });
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
        videoView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaController.show(0);
            }
        }, 100);
        videoView.requestFocus();
        videoView.setOnPreparedListener(PreparedListener);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                jumpMain();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            super.onBackPressed();
            finish();
        }
        else {
            doubleBackToExitPressedOnce= true;
            Toast.makeText(this,"종료를 원한다면 뒤로가기 버튼을 한번 더 눌러주세요",Toast.LENGTH_SHORT).show();
        }
    }

    private synchronized void jumpMain(){
        finish();
    }
    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            VideoView videoView = (VideoView)findViewById(R.id.video_view);
            try {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                }
                videoView.seekTo(0);
                mediaPlayer.setVolume(0, 0);

                mediaPlayer.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
