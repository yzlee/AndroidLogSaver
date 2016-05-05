package log;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 将API访问记录，错误日志等写入SD卡的LOG文件夹
 * Created by 李勇智 on 4/14/16.
 */
public class LogSaver {

    private static SimpleDateFormat accurateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.SIMPLIFIED_CHINESE);
    private static SimpleDateFormat currentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
    private static final String SD_PATH = Environment.getExternalStorageDirectory() + "/";
    private static final String EXCEPTIONS_DIR_PATH = SD_PATH + "/LOG/EXCEPTIONS/";  //错误日志，按天计算
    private static final String API_INVOKE_DIR_PATH = SD_PATH + "/LOG/API_INVOKE/";  //API调用记录，按天计算
    private static final String DEBUGS_DIR_PATH = SD_PATH + "/LOG/DEBUGS/";

    /**
     * 将exception写入以当日命名的文件中，若超过10日，则删除最早的直到仅存10个文件
     * @param e
     */
    public static synchronized void printExceptionLogToFile(Throwable e) {
        printExceptionLogToFileInBackground(e);
    }

    public static synchronized void printExceptionLogToFileInBackground(final Throwable e) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String curDay = currentDay.format(new Date());
                String curTime = accurateTime.format(new Date());
                try {
                    File dir = new File(EXCEPTIONS_DIR_PATH);
                    onlyRetainNecessaryFile(10, dir, ".exlog");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(EXCEPTIONS_DIR_PATH + curDay + ".exlog");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(EXCEPTIONS_DIR_PATH + curDay + ".exlog", true);
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    //输出错误信息
                    printWriter.append("\r\n-----------------------------------------------------\r\n");
                    printWriter.append(curTime + "\r\n");
                    e.printStackTrace(printWriter);
                    Throwable cause = e.getCause();
                    while (cause != null) {
                        cause.printStackTrace(printWriter);
                        cause = cause.getCause();
                    }
                    printWriter.flush();
                    printWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();

    }

        /**
         * 记录每次访问api时间和类以及信息
         * @param api 接口名
         * @param clazz 类名
         * @param content 上传内容
         * @param info 自定义信息
         */
    public static synchronized void printApiInvokeLogToFile(String api, String clazz, String content, String info) {
        Log.i("liyongzhi", "[LogSaver](TimingAlert) " + clazz + " come into LogSaver");
        printApiInvokeLogToFileInBackground(api, clazz, content, info);
    }

    public static synchronized void printApiInvokeLogToFileInBackground(String api, final String clazz, final String content, final String info) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String curDay = currentDay.format(new Date());
                String curTime = accurateTime.format(new Date());
                File dir = new File(API_INVOKE_DIR_PATH);
                onlyRetainNecessaryFile(10, dir, ".apilog");
                try {
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(API_INVOKE_DIR_PATH + curDay + ".apilog");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fileWriter = new FileWriter(API_INVOKE_DIR_PATH + curDay + ".apilog", true);
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    printWriter.append(curTime + " : [" + clazz + "] " + info +  " " + content + " " + "\r\n");
                    printWriter.flush();
                    printWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 记录一些杂碎的调试信息
     * @param
     */
    public static synchronized void printOtherLogToFile(String clazz, String info, String... categorise) {
        String curDay = currentDay.format(new Date());
        String curTime = accurateTime.format(new Date());
        File dir = new File(DEBUGS_DIR_PATH);
        onlyRetainNecessaryFile(10, dir, ".debuglog");
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(DEBUGS_DIR_PATH + curDay + ".debuglog");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(DEBUGS_DIR_PATH + curDay + ".debuglog", true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            String cg = "";
            for (String category : categorise) {
                cg = cg + "(" +category + ")";
            }
            printWriter.append(curTime + " : [" + clazz + "] " + cg + " " + info + " " + "\r\n");
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 仅仅保留文件夹中以指定字符结尾的指定数目的文件
     * @param retainNum 保留的数量
     * @param dir       文件夹
     * @param endsWith  以字符结尾
     */
    private static synchronized void onlyRetainNecessaryFile(int retainNum, File dir, final String endsWith) {
        try {
            FilenameFilter filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(endsWith);
                }
            };
            int number = dir.listFiles(filenameFilter).length;
            if (number > retainNum) {
                File[] files = dir.listFiles();
                File firstFile = files[0];
                for (int i = 1; i < number; i++) {
                    if (files[i].lastModified() < firstFile.lastModified()) {
                        firstFile = files[i];
                    }
                }
                if (firstFile.exists()) {
                    if (firstFile.isFile()) {
                        firstFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
