/**
 * @Author CMODs
 * @AIDE AIDE+
*/
package com.mvp.cmods.mem.mvp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import android.app.ActivityManager;

public class MemoryModifyService extends Service {

    private static final String TAG = "Mem";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Mem created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "started");
        if (intent != null) {
            String packageName = intent.getStringExtra("PACKAGE_NAME");
            long offset = intent.getLongExtra("OFFSET", -1);
            String hexValue = intent.getStringExtra("HEX_VALUE");
            String libraryName = intent.getStringExtra("LIBRARY_NAME");
            if (packageName != null && offset >= 0 && hexValue != null && libraryName != null) {
                int pid = getProcessIdByPackageName(this, packageName);
                if (pid > 0) {
                    long libraryBaseAddress = getLibraryBaseAddress(pid, libraryName);
                    if (libraryBaseAddress != -1) {
                        modifyProcessMemory(pid, libraryBaseAddress + offset, hexValue);
                    } else {
                        Log.e(TAG, "Failed to find base address for library: " + libraryName);
                    }
                } else {
                    Log.e(TAG, "Failed to find PID for package: " + packageName);
                }
            }
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void modifyProcessMemory(int pid, long address, String hexValue) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            if (process != null) {
                String memPath = "/proc/" + pid + "/mem";
                RandomAccessFile memoryFile = new RandomAccessFile(new File(memPath), "rw");
                memoryFile.seek(address);

                byte[] bytes = hexStringToByteArray(hexValue);
                memoryFile.write(bytes);
                memoryFile.close();

                Log.d(TAG, "Modified process " + pid + " memory at address " + address + " with value " + hexValue);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error modifying process memory", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
				+ Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private int getProcessIdByPackageName(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            if (process.processName.equals(packageName)) {
                return process.pid;
            }
        }
        return -1;
    }

    private long getLibraryBaseAddress(int pid, String libraryName) {
        try {
            File mapsFile = new File("/proc/" + pid + "/maps");
            RandomAccessFile reader = new RandomAccessFile(mapsFile, "r");
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(libraryName)) {
                    String[] parts = line.split("-");
                    if (parts.length > 1) {
                        reader.close();
                        return Long.parseLong(parts[0], 16);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Error reading maps file", e);
        }
        return -1;
    }
}

