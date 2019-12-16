package com.example.monilek;

public class Commands {
    private byte[] raw;
public byte[] getRaw() {

    return raw;
    }
    public void setRaw(byte[] raw) {

        this.raw = raw;
    }

    public Commands SetChannelSettings(int channel, int gain, int input, boolean isOn, boolean isInSRB2) {
    this.raw = new byte[] {0x77,0x66,0x55, (byte) 0xAA,0x41,0x00,0x00
    };
    return this;
    }
    public Commands DeviceAndCS(int device){
        raw[7] = (byte)device;
        return this;
    }
    public static void  SetChecksum(byte[] buffer){
        int fb = buffer.length-4;
        int cs = 0;
        for (int i = 0; i<fb; i++) cs+= buffer[i];

        buffer[fb+3] = (byte)(cs>>0);
        buffer[fb+2] = (byte)(cs>>8);
        buffer[fb+1] = (byte)(cs>>16);
        buffer[fb+0] = (byte)(cs>>24);

    }
}
