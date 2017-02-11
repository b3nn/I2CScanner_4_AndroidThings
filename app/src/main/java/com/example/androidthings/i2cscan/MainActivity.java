/*
 * Copyright 2016, The Android Open Source Project
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
 */

package com.example.androidthings.i2cscan;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;
import java.util.List;


/**
 * Quick and dirty i2c scan for Android IOT devices.
 * This was only tested on a Raspberry Pi 3, which should have "I2C1" enabled by default.
 * Devices are considered "found" if there is no error trying to read or write data to an
 * address on the bus.
 */
public class MainActivity extends Activity {
    private static final String TAG = "i2cScan";
    private I2cDevice mDevice;

    // Range for our i2c Address scan. Not using 10 bit addressing.
    private int I2CStartAddress = 8;
    private int I2CEndAddress = 119;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected  void onStart(){
        super.onStart();
        Log.i(TAG, "Calling i2c Scan");
        scani2c();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mDevice != null) {
            try {
                mDevice.close();
                mDevice = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close I2C device", e);
            }
        }
    }

    protected void scani2c() {
        PeripheralManagerService manager = new PeripheralManagerService();
        List<String> deviceList = manager.getI2cBusList();

        if (deviceList.isEmpty()) {
            Log.i(TAG, "No I2C bus available on this device.");
            updateView(R.id.bus_found, "Sorry, no I2C bus found");

        } else {
            Log.i(TAG, "List of available devices: " + deviceList);

            for (String deviceName : deviceList) {
                Log.i(TAG, "Starting Scan of bus: " + deviceName);
                updateView(R.id.bus_found, deviceName);

                for( int address = I2CStartAddress; address < I2CEndAddress; address++) {

                    try {
                        mDevice = manager.openI2cDevice(deviceName, address);
                        i2cWriteShit(mDevice, address);
                        Log.i(TAG, "FOUND i2c: " + address);
                        updateView(R.id.address_found, Integer.toString(address) + includeHex(address));
                        mDevice.close();
                    } catch (IOException e) {
                        Log.w(TAG, "Unable to access I2C device: " + address);
                        updateView(R.id.address_not_found, Integer.toString(address));
                    }

                }
            }

        }

    }

    private void i2cWriteShit(I2cDevice device, int address) throws IOException {
        // This is arbitrary. We just need to see if we get a NAC error.
        byte value = device.readRegByte(address);
        value |= 0x40;
        device.writeRegByte(address, value);
    }

    private String includeHex(int value) {
        return " [0x" + Integer.toString(value, 16) + "] ";
    }
    private void updateView(int view_id, String message) {
        TextView view = (TextView) findViewById(view_id);
        String textString = view.getText().toString();
        textString += " " + message;
        view.setText(textString);
    }
}
