package com.example.rudxo_000.final_picopter_project;

import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity {
    RelativeLayout layout_joystick, layout_joystick2;
    TextView textView1, textView2, textView3, textView4, stateTextView;

    JoyStickClass js, js2;

    private String Ip_address = "192.168.42.1";
    private int port = 1000;
    private Socket socket = null;
    private OutputStream writeSocket;
    private InputStream readSocket;
    private Handler mHandler = new Handler();

    private ConnectivityManager cManager;
    private NetworkInfo wifi;
    private ServerSocket serverSocket;

    private boolean stopping = false;
    private int temp = 0;
    private static final int plusValue = 10;
    private int power = 0;
    private float yaw = 0;
    private float roll = 0;
    private float pitch = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        stateTextView = (TextView) findViewById(R.id.ConnectState);

        layout_joystick = (RelativeLayout) findViewById(R.id.layout_joystick);
        layout_joystick2 = (RelativeLayout) findViewById(R.id.layout_joystick2);

        js = new JoyStickClass(getApplicationContext(), layout_joystick, R.drawable.image_button);
        js.setStickSize(150, 150);
        js.setLayoutSize(500, 500);
        js.setLayoutAlpha(300);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(50);

        js2 = new JoyStickClass(getApplicationContext(), layout_joystick2, R.drawable.image_button);
        js2.setStickSize(150, 150);
        js2.setLayoutSize(500, 500);

        js2.setLayoutAlpha(150);
        js2.setStickAlpha(100);
        js2.setOffset(90);
        js2.setMinimumDistance(50);

        layout_joystick.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                try {
                    js.drawStick(arg1);

                    if (arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                        int direction = js.get4Direction();

                        if (direction == JoyStickClass.STICK_UP) {
                            yaw = 0;
                            if (temp > js.getY()) {
                                power += plusValue;
                                if (power >= 1024) {
                                    power = 1024;
                                }
                                textView1.setText("power : " + power);
                            }
                            temp = js.getY();
                        } else if (direction == JoyStickClass.STICK_RIGHT) {
                            yaw = js.getX();
                            textView2.setText("yaw : " + yaw);
                        } else if (direction == JoyStickClass.STICK_DOWN) {
                            yaw = 0;
                            if (temp < js.getY()) {
                                power -= plusValue;
                                if (power <= 0) {
                                    power = 0;
                                }
                                textView1.setText("power : " + power);
                            }
                            temp = js.getY();
                        } else if (direction == JoyStickClass.STICK_LEFT) {
                            yaw = js.getX();
                            textView2.setText("yaw : " + yaw);
                        }
                    } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                        yaw = 0;
                        textView1.setText("power : " + power);
                        textView2.setText("yaw : " + yaw);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView1.setText("power : " + power);
                textView2.setText("yaw : " + yaw);
                return true;
            }
        });

        layout_joystick2.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                try {
                    js2.drawStick(arg1);

                    if (arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                        pitch = (js2.getY() * (-1));
                        roll = js2.getX();
                    } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                        pitch = 0;
                        roll = 0;
                        textView3.setText("pitch : " + pitch);
                        textView4.setText("roll : " + roll);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView3.setText("pitch : " + pitch);
                textView4.setText("roll : " + roll);
                return true;
            }
        });
    }

    public void OnClick(View v) throws Exception {
        switch (v.getId()) {
            case R.id.btnConnet:
                stopping = false;
                (new Connect()).start();
                break;
            case R.id.btnDisconnect:
                (new Disconnect()).start();
                break;
        }
    }

    class Connect extends Thread {
        public void run() {

            try {
                socket = new Socket(Ip_address, port);

                if (socket != null) {
                    writeSocket = socket.getOutputStream();
                    readSocket = socket.getInputStream();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setStateTextView("Connect");
                        setToast("connect");
                    }
                });
                //(new recvSocket()).start();
                (new sendMessage()).start();
            } catch (Exception e) {
                final String recvInput = "connect fail";

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setStateTextView("Disconnect");
                        setToast(recvInput);
                    }
                });
            }
        }
    }

    class Disconnect extends Thread {
        public void run() {
            Log.i("info","start_disconnection");
            try {
                if (socket != null) {
                    Log.i("info","check_disconnection");
                    stopping = true;
                    writeSocket.close();
                    readSocket.close();
                    socket.close();
                    socket = null;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setStateTextView("Disconnect");
                            setToast("disconnect");
                        }
                    });
                }
            } catch (Exception e) {
                final String recvInput = "Fail";
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setStateTextView("Connect");
                        setToast(recvInput);
                    }
                });
            }
        }
    }

    class recvSocket extends Thread {
        public void run() {
            try {/*
                while (!stopping) {
                    //socket.setSoTimeout(5000);
                    int result = readSocket.read();
                    if (result == -1) {
                        stopping = true;
                        (new Disconnect()).start();
                        break;
                    }
                }*/
            } catch (Exception e) {
                final String recvInput = "The problem, ended the connection";
                stopping = true;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setStateTextView("Disconnect");
                        setToast(recvInput);
                    }
                });
            }
        }
    }

    class sendMessage extends Thread {
        int directionLeft = js.get4Direction();
        int directionRight = js2.get4Direction();

        WriteFile writefile = new WriteFile();

        public void run() {
            try {
                //OutputStream os = socket.getOutputStream();
                //InputStream is = socket.getInputStream();

                while (!stopping) {
                    AndToPiCommand command = new AndToPiCommand(yaw, pitch, roll, power);
                    writeSocket.write(command.toByteArray());
                    Log.i("info", command.toString());
                    socket.setSoTimeout(5000);
                    readSocket.read();
                    //Thread.sleep(500);
                    //writefile.writeDataintxtFile(FILENAME, yaw, pitch, roll);
                }
            } catch (UnknownHostException e) {
                (new Disconnect()).start();
                e.printStackTrace();
            } catch (IOException e) {
                (new Disconnect()).start();
                e.printStackTrace();
            } catch (Exception e) {
                (new Disconnect()).start();
                e.printStackTrace();
            }
            Log.i("info", "Thread dead");
        }
    }

    void setToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void setStateTextView(String text) {
        stateTextView.setText("State: " + text);
    }

    @Override
    public void onBackPressed() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onStop();
    }
}