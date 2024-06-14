/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.nativemidi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.content.Context;

import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;



public class ActivitySampler extends AppCompatActivity
        implements
        SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener, View.OnTouchListener{

    private static final String TAG = MainActivity.class.getName();;

    private AppMidiManager mAppMidiManager;

    // Connected devices
    private ArrayList<MidiDeviceInfo> mReceiveDevices = new ArrayList<MidiDeviceInfo>();
    private ArrayList<MidiDeviceInfo> mSendDevices = new ArrayList<MidiDeviceInfo>();

    // Send Widgets
    Spinner mOutputDevicesSpinner;

    SeekBar mControllerSB;
    SeekBar mPitchBendSB;

    EditText mProgNumberEdit;

    // Receive Widgets
    Spinner mInputDevicesSpinner;
    TextView mReceiveMessageTx;

    // Cargar las librerías nativas
    static {
        AppMidiManager.loadNativeAPI();
    }
    //Notas MIDI
    byte[] keys = {60, 64, 67};
    byte[] keys2 = {62, 66, 69};
    byte[] keys3 = {64, 68, 71};
    byte[] keys4 = {66, 70, 73};
    byte[] keys5 = {68, 72, 75};
    byte[] keys6 = {70, 74, 77};
    byte[] keys7 = {72, 76, 79};
    byte[] keys8 = {74, 78, 81};
    byte[] keys9 = {76, 80, 83};
    byte[] keys10 = {78, 82, 85};
    byte[] keys11 = {80, 84, 87};
    byte[] keys12 = {82, 86, 69};

    byte[] velocities = {60, 60, 60};   // Velocidad de nota
    byte channel = 0;    // mandar MIDI por el canal 0

    Button boton1, boton2,boton3,boton4,boton5,boton6,boton7,boton8,boton9,boton10,boton11,boton12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sampler);

        // Iniciar Interfaz nativa java
        initNative();

        // Setup UI
        mOutputDevicesSpinner = (Spinner)findViewById(R.id.outputDevicesSpinner);
        mOutputDevicesSpinner.setOnItemSelectedListener(this);

        boton1=((Button)findViewById(R.id.btSamlpler1));
        boton2=((Button)findViewById(R.id.btSamlpler2));
        boton3=((Button)findViewById(R.id.btSamlpler3));
        boton4=((Button)findViewById(R.id.btSamlpler4));
        boton5=((Button)findViewById(R.id.btSamlpler5));
        boton6=((Button)findViewById(R.id.btSamlpler6));
        boton7=((Button)findViewById(R.id.btSamlpler7));
        boton8=((Button)findViewById(R.id.btSamlpler8));
        boton9=((Button)findViewById(R.id.btSamlpler9));
        boton10=((Button)findViewById(R.id.btSamlpler10));
        boton11=((Button)findViewById(R.id.btSamlpler11));
        boton12=((Button)findViewById(R.id.btSamlpler12));

        boton1.setOnTouchListener(this);
        boton2.setOnTouchListener(this);
        boton3.setOnTouchListener(this);
        boton4.setOnTouchListener(this);
        boton5.setOnTouchListener(this);
        boton6.setOnTouchListener(this);
        boton7.setOnTouchListener(this);
        boton8.setOnTouchListener(this);
        boton9.setOnTouchListener(this);
        boton10.setOnTouchListener(this);
        boton11.setOnTouchListener(this);
        boton12.setOnTouchListener(this);

        boton1.setBackgroundColor(Color.parseColor("#808080"));
        boton2.setBackgroundColor(Color.parseColor("#808080"));
        boton3.setBackgroundColor(Color.parseColor("#808080"));
        boton4.setBackgroundColor(Color.parseColor("#808080"));
        boton5.setBackgroundColor(Color.parseColor("#808080"));
        boton6.setBackgroundColor(Color.parseColor("#808080"));
        boton7.setBackgroundColor(Color.parseColor("#808080"));
        boton8.setBackgroundColor(Color.parseColor("#808080"));
        boton9.setBackgroundColor(Color.parseColor("#808080"));
        boton10.setBackgroundColor(Color.parseColor("#808080"));
        boton11.setBackgroundColor(Color.parseColor("#808080"));
        boton12.setBackgroundColor(Color.parseColor("#808080"));

        mControllerSB = (SeekBar)findViewById(R.id.controllerSeekBar);
        mControllerSB.setMax(MidiSpec.MAX_CC_VALUE);
        mControllerSB.setOnSeekBarChangeListener(this);

        mPitchBendSB = (SeekBar)findViewById(R.id.pitchBendSeekBar);
        mPitchBendSB.setMax(MidiSpec.MAX_PITCHBEND_VALUE);
        mPitchBendSB.setProgress(MidiSpec.MID_PITCHBEND_VALUE);
        mPitchBendSB.setOnSeekBarChangeListener(this);

        mInputDevicesSpinner = (Spinner)findViewById(R.id.inputDevicesSpinner);
        mInputDevicesSpinner.setOnItemSelectedListener(this);

        mProgNumberEdit = (EditText)findViewById(R.id.progNumEdit);

        mReceiveMessageTx = (TextView)findViewById(R.id.receiveMessageTx);

        MidiManager midiManager = (MidiManager) getSystemService(Context.MIDI_SERVICE);
        midiManager.registerDeviceCallback(new MidiDeviceCallback(), new Handler());

        // Setup de la interfaz MIDI
        mAppMidiManager = new AppMidiManager(midiManager);

        // Scan inicial
        ScanMidiDevices();
    }
    //onTouchListener de los botones para las notas
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.btSamlpler1:
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // Pressed
                mAppMidiManager.sendNoteOn(channel,keys, velocities) ;
                boton1.setBackgroundColor(Color.parseColor("#FF0000"));

            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                //Released
                mAppMidiManager.sendNoteOff(channel,keys, velocities) ;
                boton1.setBackgroundColor(Color.parseColor("#808080"));
            }break;
            case R.id.btSamlpler2:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys2, velocities) ;
                    boton2.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys2, velocities) ;
                    boton2.setBackgroundColor(Color.parseColor("#808080"));
                }break;
            case R.id.btSamlpler3:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys3, velocities) ;
                    boton3.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys3, velocities) ;
                    boton3.setBackgroundColor(Color.parseColor("#808080"));
                }break;
            case R.id.btSamlpler4:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys4, velocities) ;
                    boton4.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys4, velocities) ;
                    boton4.setBackgroundColor(Color.parseColor("#808080"));
                }break;
            case R.id.btSamlpler5:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys5, velocities) ;
                    boton5.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys5, velocities) ;
                    boton5.setBackgroundColor(Color.parseColor("#808080"));
                }break;
            case R.id.btSamlpler6:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // Pressed
                mAppMidiManager.sendNoteOn(channel,keys6, velocities) ;
                    boton6.setBackgroundColor(Color.parseColor("#FF0000"));
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                //Released
                mAppMidiManager.sendNoteOff(channel,keys6, velocities) ;
                    boton6.setBackgroundColor(Color.parseColor("#808080"));
            }break;
            case R.id.btSamlpler7:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys7, velocities) ;
                    boton7.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys7, velocities) ;
                    boton7.setBackgroundColor(Color.parseColor("#808080"));
                }break;

            case R.id.btSamlpler8:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys, velocities) ;
                    boton8.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys8, velocities) ;
                    boton8.setBackgroundColor(Color.parseColor("#808080"));
                }break;
            case R.id.btSamlpler9:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys9, velocities) ;
                    boton9.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys9, velocities) ;
                    boton9.setBackgroundColor(Color.parseColor("#808080"));
                }break;
            case R.id.btSamlpler10:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys10, velocities) ;
                    boton10.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys10, velocities) ;
                    boton10.setBackgroundColor(Color.parseColor("#808080"));
                }break;
            case R.id.btSamlpler11:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys11, velocities) ;
                    boton11.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys11, velocities) ;
                    boton11.setBackgroundColor(Color.parseColor("#808080"));
                }break;

            case R.id.btSamlpler12:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // Pressed
                    mAppMidiManager.sendNoteOn(channel,keys12, velocities) ;
                    boton12.setBackgroundColor(Color.parseColor("#FF0000"));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Released
                    mAppMidiManager.sendNoteOff(channel,keys12, velocities) ;
                    boton12.setBackgroundColor(Color.parseColor("#808080"));
                }break;

        }

        return false;
    }


         

    // Scan de dispositivos
    private class MidiDeviceCallback extends MidiManager.DeviceCallback {
        @Override
        public void onDeviceAdded(MidiDeviceInfo device) {
            ScanMidiDevices();
        }

        @Override
        public void onDeviceRemoved(MidiDeviceInfo device) {
            ScanMidiDevices();
        }
    }

   //reune la lista de dispositivos conectados y modifica la listview
    private void ScanMidiDevices() {
        mAppMidiManager.ScanMidiDevices(mSendDevices, mReceiveDevices);
        onDeviceListChange();
    }

    //Formatea mensajes MIDI para su lectura

    private void showReceivedMessage(byte[] message) {
        switch ((message[0] & 0xF0) >> 4) {
            case MidiSpec.MIDICODE_NOTEON:
                mReceiveMessageTx.setText(
                        "NOTE_ON [ch:" + (message[0] & 0x0F) +
                                " key:" + message[1] +
                                " vel:" + message[2] + "]");
                break;

            case MidiSpec.MIDICODE_NOTEOFF:
                mReceiveMessageTx.setText(
                        "NOTE_OFF [ch:" + (message[0] & 0x0F) +
                                " key:" + message[1] +
                                " vel:" + message[2] + "]");
                break;

            // Potentially handle other messages here.
        }
    }
    // OnSeekBarChangeListener
    @Override
    public void onProgressChanged(SeekBar seekBar, int pos, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.controllerSeekBar:
                mAppMidiManager.sendController((byte)0, MidiSpec.MIDICC_MODWHEEL, (byte)pos);
                break;

            case R.id.pitchBendSeekBar:
                mAppMidiManager.sendPitchBend((byte)0, pos);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    // AdapterView.OnItemSelectedListener
    @Override
    public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
        switch (spinner.getId()) {
            case R.id.outputDevicesSpinner: {
                MidiDeviceListItem listItem = (MidiDeviceListItem) spinner.getItemAtPosition(position);
                mAppMidiManager.openReceiveDevice(listItem.getDeviceInfo());
            }
            break;

            case R.id.inputDevicesSpinner: {
                MidiDeviceListItem listItem = (MidiDeviceListItem)spinner.getItemAtPosition(position);
                mAppMidiManager.openSendDevice(listItem.getDeviceInfo());
            }
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    /**
     * A class to hold MidiDevices in the list controls.
     */
    private class MidiDeviceListItem {
        private MidiDeviceInfo mDeviceInfo;

        public MidiDeviceListItem(MidiDeviceInfo deviceInfo) {
            mDeviceInfo = deviceInfo;
        }

        public MidiDeviceInfo getDeviceInfo() { return mDeviceInfo; }

        @Override
        public String toString() {
            return mDeviceInfo.getProperties().getString(MidiDeviceInfo.PROPERTY_NAME);
        }
    }

    /**
     * Fills the specified list control with a set of MidiDevices
     * @param spinner   The list control.
     * @param devices   The set of MidiDevices.
     */
    private void fillDeviceList(Spinner spinner, ArrayList<MidiDeviceInfo> devices) {
        ArrayList<MidiDeviceListItem> listItems = new ArrayList<MidiDeviceListItem>();
        for(MidiDeviceInfo devInfo : devices) {
            listItems.add(new MidiDeviceListItem(devInfo));
        }

        // Crear adaptador para el spinner
        ArrayAdapter<MidiDeviceListItem> dataAdapter =
                new  ArrayAdapter<MidiDeviceListItem>(this,
                        android.R.layout.simple_spinner_item,
                        listItems);
        // Layout
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    /**
     * Fills the Input & Output UI device list with the current set of MidiDevices for each type.
     */
    private void onDeviceListChange() {
        fillDeviceList(mOutputDevicesSpinner, mReceiveDevices);
        fillDeviceList(mInputDevicesSpinner, mSendDevices);
    }

    //
    // Native Interface methods
    //
    private native void initNative();

    /**
     * Called from the native code when MIDI messages are received.
     * @param message
     */
    private void onNativeMessageReceive(final byte[] message) {
        // Messages are received on some other thread, so switch to the UI thread
        // before attempting to access the UI
        runOnUiThread(new Runnable() {
            public void run() {
                showReceivedMessage(message);
            }
        });
    }
}