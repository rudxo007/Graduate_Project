package com.example.rudxo_000.final_picopter_project;

import java.nio.ByteBuffer;

public class AndToPiCommand {
    public final static int COMMAND_SIZE = 16;

    private final float yaw;
    private final float pitch;
    private final float roll;
    private final int power;


    public AndToPiCommand(float yaw, float pitch, float roll, int power){
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.power = power;
    }

    public static AndToPiCommand parse(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);

        float yaw = buf.getFloat(0);
        float pitch = buf.getFloat(4);
        float roll = buf.getFloat(8);
        int power = buf.getInt(12);

        return new AndToPiCommand(yaw, pitch, roll, power);
    }

    public byte[] toByteArray(){
        byte[] data = new byte[COMMAND_SIZE];
        ByteBuffer buf = ByteBuffer.wrap(data);

        buf.putFloat(0,yaw);
        buf.putFloat(4,pitch);
        buf.putFloat(8,roll);
        buf.putInt(12, power);

        return data;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    @Override
    public String toString() {
        return "AndToPiCommand [power="+power+", yaw=" + yaw + ", pitch=" + pitch + ", roll=" + roll + "]";
    }
}
