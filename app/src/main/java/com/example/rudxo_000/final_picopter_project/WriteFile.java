package com.example.rudxo_000.final_picopter_project;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class WriteFile {
    public void writeDataintxtFile(String fileName, float yaw, float pitch, float roll) throws IOException
    {
        FileOutputStream output = new FileOutputStream(fileName,true);

        String rollData = "roll="+roll+"\r\n";
        String yawData = "yaw="+yaw+", ";
        String pitchData = "pitch="+pitch+", ";
        output.write(yawData.getBytes());
        output.write(pitchData.getBytes());
        output.write(rollData.getBytes());
        output.close();
    }

    public void writeDataintxtFile(String fileName, byte[] buf) throws IOException
    {
        FileOutputStream output = new FileOutputStream(fileName,true);

        ByteBuffer b = ByteBuffer.wrap(buf);

        float yaw = b.getFloat(0);
        float pitch = b.getFloat(4);
        float roll = b.getFloat(8);

        String rollData = "roll="+roll+"\r\n";
        String yawData = "yaw="+yaw+", ";
        String pitchData = "pitch="+pitch+", ";
        output.write(yawData.getBytes());
        output.write(pitchData.getBytes());
        output.write(rollData.getBytes());
        output.close();
    }
}
