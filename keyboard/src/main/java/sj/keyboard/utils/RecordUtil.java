package sj.keyboard.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/7/10.
 */

public class RecordUtil {
    private String outPutFileName;

    private MediaRecorder recorder;
    private MediaPlayer mediaPlayer;
    private Context mContext;

    private int[] decibel;

    public RecordUtil(Context context) {
        mContext = context;
        initDecibel();
    }

    private void initDecibel() {
        decibel = new int[42];
        int start = 0;
        int end = 26;
        for(int i = 0;i < 6;++i) {
            for (int j = start; j < end; ++j) {
                decibel[j] = i;
            }
            start = end;
            end += 3;
        }
    }

    public void start() {
        outPutFileName = mContext.getFilesDir().getAbsolutePath() + File.separator
                + "RecordRemDir";
        File dir = new File(outPutFileName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        outPutFileName = outPutFileName + File.separator + createtFileName() + ".amr";
//        outPutFileName = "/sdcard/" + UtilTool.createtFileName() + ".amr";
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioSamplingRate(8000);
            recorder.setAudioEncodingBitRate(8000);

            recorder.setOutputFile(outPutFileName);

            recorder.prepare();

            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized String createtFileName() {
        java.util.Date dt = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName = fmt.format(dt);
        return fileName;
    }

    public int getRecordDecibel() {
        if(null == recorder) {
            return 0;
        }
        int x = recorder.getMaxAmplitude();
        if (x != 0) {
            int f = (int) (10 * Math.log(x) / Math.log(10));
            if(f < 41) {
                return decibel[f];
            }
            return 6;
        }
        return 0;
    }

    public void finish() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        finish();
        File file = new File(outPutFileName);
        file.delete();
    }

    public int getVoiceDuration() {
        try {
            if (outPutFileName != null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(mContext, Uri.parse(outPutFileName));
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer.getDuration() / 1000;
    }

    public String getFileName() {
        return outPutFileName;
    }
}
