package com.example.cnp.myapplication;

        import java.io.ByteArrayInputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.IOException;
        import java.io.ObjectInputStream;
        import java.io.ObjectOutputStream;
        import java.io.StreamCorruptedException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.Set;

        import android.app.Service;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.res.AssetFileDescriptor;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.media.MediaRecorder;
        import android.net.Uri;
        import android.os.Environment;
        import android.os.IBinder;
        import android.telephony.PhoneStateListener;
        import android.telephony.TelephonyManager;
        import android.text.TextUtils;
        import android.util.Log;

/**
 * @author fanchangfa
 *    Android电话监听器
 */
public class Phone_listener extends Service {
    private MediaPlayer mp;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        init();
        //取得电话管理服务
        TelephonyManager tele = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        //对呼叫状态进行监听
        tele.listen(new phone_state_listener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void init() {
        if(mp == null){
            mp = new MediaPlayer();


            mp.setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {
                        // @Override
          /*覆盖文件播出完毕事件*/
                        public void onCompletion(MediaPlayer arg0) {
                            try {
              /*解除资源与MediaPlayer的赋值关系
               * 让资源可以为其它程序利用*/
                                mp.release();
                                mp = null;
              /*改变TextView为播放结束*/
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        }
                    });
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        /* 当MediaPlayer.OnErrorListener会运行的Listener */
            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
          /*覆盖错误处理事件*/
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    // TODO Auto-generated method stub
                    try {
              /*发生错误时也解除资源与MediaPlayer的赋值*/
                        mp.release();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        }
    }

    private int mCurrentVolume;
    private AudioManager mAudioManager;

    private SharedPreferences getSharedPreferences(){
        return getSharedPreferences("phone", Context.MODE_PRIVATE);
    }

    private final class phone_state_listener extends PhoneStateListener {

        /*电话状态有三种
         * 1. 来电
         * 2. 接通(通话中)
         * 3. 挂断
         * */

        /* (non-Javadoc)
         * @see android.telephony.PhoneStateListener#onCallStateChanged(int, java.lang.String)
         * 状态改变时执行
         */

        private String number;    //记录来电号码
        private MediaRecorder media;    //录音对象

        private File recorder_file;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //来电状态
                    this.number = incomingNumber;
                    ArrayList<UserItem> userItems = SaveObjectUtils.getObject(Phone_listener.this,"phone","useritem");
                    if(userItems!=null) {
                        for (UserItem userItem : userItems) {
                            Log.d("test", "onCallStateChanged: " + incomingNumber);
                            if (userItem.phoneNumber.equals(incomingNumber)) {
                                playMusic(Uri.parse(userItem.musicPath));
                                return;
                            }
                        }
                    }

                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //接通状态

                /*
                 * 注意顺序：先实例化存储文件的目录及格式，再对各项参数进行设置
                 * */
                    //实例化输出目录及文件名
//                    recorder_file = new File(Environment.getExternalStorageDirectory() ,
//                            number + System.currentTimeMillis() + ".3gp");
//
//                    media = new MediaRecorder();    //实例化MediaRecorder对象
//
//                    //设置录音来源：MIC
//                    media.setAudioSource(MediaRecorder.AudioSource.MIC);
//
//                    //设置录音格式为3gp格式
//                    media.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//
//                    //设置MediaRecorder的编码格式
//                    media.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//                    //设置输出目录
//                    media.setOutputFile(recorder_file.getAbsolutePath());
//
//                    try {
//                        media.prepare();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    media.start();

//            break;
//
                case TelephonyManager.CALL_STATE_IDLE:

                    stopMusic();
//                    //挂断电话
//                    if(media != null){    //停止录音
//                        media.stop();
//                        media.release();
//                        media = null;
//                    }

                    break;
            }
        }
        private void stopMusic(){
            try {
                resumeVolume();
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void playMusic(Uri musicUri) {

            try {
                setSystemVolumMax();
                init();
                if (mp.isPlaying())
                {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
                init();
                if(musicUri!= null){
                    mp.setDataSource(Phone_listener.this,musicUri);
                }else{
                    AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beautiful);
                    try {
                        mp.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                                file.getLength());
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mp.prepareAsync();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        private void setSystemVolumMax(){
            mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if(mAudioManager != null) {
                int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
            }
        }
        private void resumeVolume(){
            if(mAudioManager != null){
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,mCurrentVolume,0);
            }

        }

    }

}



