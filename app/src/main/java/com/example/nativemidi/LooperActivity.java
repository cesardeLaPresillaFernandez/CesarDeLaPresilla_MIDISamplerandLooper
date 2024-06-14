package com.example.nativemidi;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.Manifest;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class LooperActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnTouchListener {
    public TextView tvbpm;
    Button canal1, canal2, canal3, canal4;
    SeekBar sliderBpm;

    Switch metronomo;


    private static final int MICROPHONE_PERMISSION_CODE = 200;

    MediaRecorder recorderCanal1 = null, recorderCanal2 = null, recorderCanal3 = null, recorderCanal4 = null;
    MediaPlayer playerCanal1 = null, playerCanal2 = null, playerCanal3 = null, playerCanal4 = null;

    int progreso;

    File pista1=null, pista2=null,pista3=null,pista4=null;
    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);



    Timer timer;
    TimerTask tarea;

    boolean run;

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looper);

        //Setup UI
        tvbpm = (TextView) findViewById(R.id.bpm);
        metronomo = (Switch) findViewById(R.id.switchMetronomo);
        canal1 = (Button) findViewById(R.id.btLooper1);
        canal2 = (Button) findViewById(R.id.btLooper2);
        canal3 = (Button) findViewById(R.id.btLooper3);
        canal4 = (Button) findViewById(R.id.btLooper4);



        sliderBpm = (SeekBar) findViewById(R.id.seekBarBpm);
        sliderBpm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tvbpm.setText("" + progress * 3);
                setProgreso(progress*3);
                Log.i("UserLoginTask", "onprogresschanged"+getProgreso());
                run=false;
                metronomo.setChecked(false);

            }
        });
        metronomo.setOnCheckedChangeListener(this);

        canal1.setOnTouchListener(this);
        canal2.setOnTouchListener(this);
        canal3.setOnTouchListener(this);
        canal4.setOnTouchListener(this);

        canal1.setBackgroundColor(Color.parseColor("#FF0000"));
        canal2.setEnabled(false);
        canal3.setEnabled(false);
        canal4.setEnabled(false);

        pista1 = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"pista1.mp3");
        pista2 = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"pista2.mp3");
        pista3 = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"pista3.mp3");
        pista4 = new File(getApplicationContext().getFilesDir().getAbsolutePath()+"pista4.mp3");

        if(comprobarMicrofono()){
            getMicrophonePermission();
        }

        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);



    }

    private boolean comprobarMicrofono(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }else{
            return false;
        }
    }
    //Pide permisos al usuario sobre el uso del micrófono
    private void getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
        }
    }
    //Inicia el Timer del metronomo
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.isChecked()){
            run=true;
            timer= new Timer();

            tarea= new TimerTask() {
                @Override
                public void run() {
                    if(run) {
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                    }else{
                        timer.cancel();
                        timer.purge();
                    }
                }
            };

            timer.schedule(tarea, 0, (long) calcularMilis());

        }else {
            run =false;

        }
    }
    //calcula los milisegundos equivalentes al bpm seleccionado
    public double calcularMilis(){
        int prog=getProgreso();
        Log.i("UserLoginTask", "progreso"+prog);
        double tempo=1000*(60.0/prog);
        Log.i("UserLoginTask", "tempo"+tempo);
        return tempo;
    }
    //onTouchListener
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (view.getId()) {
            case R.id.btLooper1:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    try {
                        recorderCanal1= new MediaRecorder();
                        recorderCanal1.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorderCanal1.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorderCanal1.setOutputFile(pista1);
                        recorderCanal1.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorderCanal1.prepare();
                        recorderCanal1.start();
                        Log.i("UserLoginTask", "playerCanal1 Empieza a grabar");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    recorderCanal1.stop();
                    recorderCanal1.release();
                    Log.i("UserLoginTask", "playerCanal1 Termina de grabar");

                    try {
                        playerCanal1= new MediaPlayer();
                        playerCanal1.setDataSource(String.valueOf(pista1));
                        playerCanal1.prepare();
                        playerCanal1.start();
                        Log.i("UserLoginTask", "playerCanal1 Empieza a reproducir");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    canal1.setBackgroundColor(Color.parseColor("#808080"));
                    canal1.setEnabled(false);
                    canal2.setEnabled(true);
                    canal2.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                break;
            case R.id.btLooper2:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    try {
                        recorderCanal2= new MediaRecorder();
                        recorderCanal2.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorderCanal2.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorderCanal2.setOutputFile(pista2);
                        recorderCanal2.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorderCanal2.prepare();
                        recorderCanal2.start();
                        Log.i("UserLoginTask", "playerCanal2 Empieza a grabar");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    recorderCanal2.stop();
                    recorderCanal2.release();
                    Log.i("UserLoginTask", "playerCanal2 Termina de grabar");

                    try {
                        playerCanal2= new MediaPlayer();
                        playerCanal2.setDataSource(String.valueOf(pista2));
                        playerCanal2.prepare();
                        playerCanal2.start();
                        Log.i("UserLoginTask", "playerCanal2 Empieza a reproducir");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    canal2.setBackgroundColor(Color.parseColor("#808080"));
                    canal2.setEnabled(false);
                    canal3.setEnabled(true);
                    canal3.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                break;

            case R.id.btLooper3:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    try {
                        recorderCanal3= new MediaRecorder();
                        recorderCanal3.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorderCanal3.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorderCanal3.setOutputFile(pista3);
                        recorderCanal3.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorderCanal3.prepare();
                        recorderCanal3.start();
                        Log.i("UserLoginTask", "recorderCanal3 Empieza a grabar");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    recorderCanal3.stop();
                    recorderCanal3.release();
                    Log.i("UserLoginTask", "playerCanal3 Termina de grabar");

                    try {
                        playerCanal3= new MediaPlayer();
                        playerCanal3.setDataSource(String.valueOf(pista3));
                        playerCanal3.prepare();
                        playerCanal3.start();
                        Log.i("UserLoginTask", "playerCanal3 Empieza a reproducir");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    canal3.setBackgroundColor(Color.parseColor("#808080"));
                    canal3.setEnabled(false);
                    canal4.setEnabled(true);
                    canal4.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                break;
            case R.id.btLooper4:

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    try {
                        recorderCanal4= new MediaRecorder();
                        recorderCanal4.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorderCanal4.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorderCanal4.setOutputFile(pista4);
                        recorderCanal4.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorderCanal4.prepare();
                        recorderCanal4.start();
                        Log.i("UserLoginTask", "recorderCanal4 Empieza a grabar");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    recorderCanal4.stop();
                    recorderCanal4.release();
                    Log.i("UserLoginTask", "playerCanal4 Termina de grabar");

                    try {
                        playerCanal4= new MediaPlayer();
                        playerCanal4.setDataSource(String.valueOf(pista4));
                        playerCanal4.prepare();
                        playerCanal4.start();
                        Log.i("UserLoginTask", "playerCanal4 Empieza a reproducir");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    canal4.setBackgroundColor(Color.parseColor("#808080"));
                    canal4.setEnabled(false);

                }
                break;
        }
        return false;
    }

}