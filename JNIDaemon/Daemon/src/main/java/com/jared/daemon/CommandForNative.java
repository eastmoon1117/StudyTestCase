package com.jared.daemon;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jared on 2018/1/29.
 */

public class CommandForNative {

    private static final String TAG = CommandForNative.class.getSimpleName();
    private static final boolean CMD_DEBUG = false;

    /**
     * 设置权限
     *
     * @param absPath 绝对路径
     * @param mode    权限wrx wrx wrx
     */
    public static void chmodFile(String absPath, int mode) {
        try {
            if (CMD_DEBUG)
                Log.d(TAG, "chmod " + mode + " " + absPath);
            Runtime.getRuntime().exec("chmod " + mode + " " + absPath).waitFor();
        } catch (Exception e) {
            Log.e(TAG, "chmodFile failed: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param context
     * @param destDir
     * @param filename
     */
    private static void rmFile(Context context, String destDir, String filename) {
        try {
            if (CMD_DEBUG)
                Log.d(TAG, "rm " + destDir + "/" + filename);
            Runtime.getRuntime().exec("rm " + destDir + "/" + filename).waitFor();
        } catch (Exception e) {
            Log.e(TAG, "rm file failed: " + e.getMessage());
        }
    }

    /**
     * 拷贝assert文件到指定的目录下
     *
     * @param context
     * @param assertFilePath assert目录下的子目录
     * @param assertFilename assert中的子目录的文件名字
     * @param destDir        目标文件路径
     * @return 返回目标文件的绝对路径
     */
    public static String copyAssetsFile(Context context, String assertFilePath, String assertFilename, String destDir) {
        if (CMD_DEBUG)
            Log.d(TAG, assertFilePath + "/" + assertFilename + ", to " + destDir);

        try {
            File file = new File(context.getDir(destDir, Context.MODE_PRIVATE), assertFilename);
            if (file.exists()) {
                Log.d(TAG, "binary has existed");
                return null;
            }

            AssetManager manager = context.getAssets();
            final InputStream is = manager.open(assertFilePath + File.separator + assertFilename);

            final String absPath = file.getAbsolutePath();
            final FileOutputStream out = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            is.close();

            return absPath;
        } catch (Exception e) {
            Log.e(TAG, "copyAssetsFile failed: " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static String getCPUDir() {
        String binaryDir = "armeabi";

        String abi = Build.CPU_ABI;
        if (abi.startsWith("armeabi-v7a")) {
            binaryDir = "armeabi-v7a";
        } else if (abi.startsWith("arm64-v8a")) {
            binaryDir = "arm64-v8a";
        } else if (abi.startsWith("mips64")) {
            binaryDir = "mips64";
        } else if (abi.startsWith("mips")) {
            binaryDir = "mips";
        } else if (abi.startsWith("x86")) {
            binaryDir = "x86";
        }

        return binaryDir;
    }

    public static boolean createFile(Context context, String destDir, String filename) {
        File file;
        try {
            file = new File(context.getDir(destDir, Context.MODE_PRIVATE), filename);
            if (file.exists()) {
                Log.d(TAG, "file has existed");
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "create failed: " + e.getMessage());
            return false;
        }

        String filePath  = file.getAbsolutePath();
        if (CMD_DEBUG)
            Log.e(TAG, "file path: " + filePath);

        /**
         * 设置权限
         */
        if (filePath == null) return false;

        chmodFile(filePath, 755);
        return true;
    }

    /**
     * @param context
     * @param destDir  文件需要拷贝到的目标目录
     * @param filename 文件名
     * @return
     */
    public static boolean installBinary(Context context, String destDir, String filename) {

        /**
         * 找到CPU的目录
         */
        String cpuDir = getCPUDir();

        /**
         * 拷贝assets下的文件到目标目录下
         */
        String filePath = copyAssetsFile(context, cpuDir, filename, destDir);

        /**
         * 设置权限
         */
        if (filePath == null) return false;

        chmodFile(filePath, 755);

        return true;
    }

    public static int isProcessExist(String binary) {
        int pid = -1;

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        try {
            String psCmd = "ps | grep " + binary;
            if (CMD_DEBUG)
                Log.d(TAG, psCmd);

            process = Runtime.getRuntime().exec(psCmd);

            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));

            process.waitFor();

            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
                successMsg.append('\n');
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
            if (CMD_DEBUG)
                Log.d(TAG, successMsg.toString());
            String info = successMsg.toString().substring(successMsg.toString().indexOf('\n'));
            if (CMD_DEBUG)
                Log.d(TAG, info);
            String[] buff = info.split(" +");
            if (CMD_DEBUG) {
                for (int i = 0; i < buff.length; i++) {
                    Log.d(TAG, i + ":" + buff[i]);
                }
            }
            if (buff.length >= 9) {
                pid = Integer.valueOf(buff[1]);
            }
            if (successMsg.toString().contains(binary)) {
                Log.d(TAG, "binary is already running:" + pid);
                return pid;
            }
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, binary + "error: " + e.getMessage());
        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (process != null) {
                process.destroy();
            }
        }
        return pid;
    }

    public static void killProcessByName(String binary) {
        int pid = isProcessExist(binary);

        try {
            String cmd = "kill " + pid;
            Runtime.getRuntime().exec(cmd).waitFor();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "kill" + binary + "error: " + e.getMessage());
        }
    }

    /**
     * @param context
     * @param dir     可执行文件的目录
     * @param binary  可执行文件的名字
     * @param args    对应执行的参数
     */
    public static void execBinary(Context context, String dir, String binary, String args) {
        String cmd = context.getDir(dir, Context.MODE_PRIVATE)
                .getAbsolutePath() + File.separator + binary;

        int pid = isProcessExist(binary);
        if (pid != -1) {
            return;
        }

        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append(cmd);
        cmdBuilder.append(" ");
        cmdBuilder.append(args);

        try {
            if (CMD_DEBUG)
                Log.d(TAG, cmdBuilder.toString());
            Runtime.getRuntime().exec(cmdBuilder.toString()).waitFor();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "execBinary" + binary + "error: " + e.getMessage());
        }
    }

    /**
     * 重新执行进程, kill之前的进程
     *
     * @param context
     * @param dir     可执行文件的目录
     * @param binary  可执行文件的名字
     * @param args    对应执行的参数
     */
    public static void restartBinary(Context context, String dir, String binary, String args) {
        String cmd = context.getDir(dir, Context.MODE_PRIVATE)
                .getAbsolutePath() + File.separator + binary;

        killProcessByName(binary);

        StringBuilder cmdBuilder = new StringBuilder();
        cmdBuilder.append(cmd);
        cmdBuilder.append(" ");
        cmdBuilder.append(args);

        try {
            if (CMD_DEBUG)
                Log.d(TAG, cmdBuilder.toString());
            Runtime.getRuntime().exec(cmdBuilder.toString()).waitFor();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "execBinary" + binary + "error: " + e.getMessage());
        }
    }
}
