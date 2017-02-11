# I2CScanner for Android Things (IOT) 
Simple i2c bus scanner for Android Things IOT devices.

Tested on the Raspberry Pi 3 running the 0.2 Android Things SDK.

Compile and push using Android Studio. Found i2c devices will be displayed both on screen and in logcat. Default range will look for read/write responses from address 8 [0x08] to 119 [0x77] on each discovered bus. By default, the Raspberry Pi should have bus "I2C1" enabled using GPIO pins 3 (SDA) and 4 (SCL). 

https://developer.android.com/things/hardware/raspberrypi-io.html
![RPi Pinout](https://developer.android.com/things/images/pinout-raspberrypi.png "RPi 3 Pinout")

