package com.afx.afx_preface_tool;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    static Logger logger = null;

    static Logger getInstance() {
        if(logger == null) {
            logger = new Logger();
        }
        return logger;
    }


    FileOutputStream log_file_output_stream = null;

    private String log_file_path = "";

    public void initialize(Context context) {
        if(context != null && log_file_output_stream == null) {
            String path = context.getCacheDir().getAbsolutePath() + "/logs";

            {
                File file = new File(path);
                if(file.isFile()) {
                    file.delete();
                }
                if(!file.exists()) {
                    file.mkdir();
                }

                for(File temp : file.listFiles()) {
                    if(temp != null) {
                        if(System.currentTimeMillis() - temp.lastModified() > 604800000) {
                            file.delete();
                        }
                    }
                }
            }

            {
                SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
                log_file_path= path + "/" + context.getPackageName() + "_" + dateFormat.format(new Date());
            }
        }
    }

    public void flush() {
        if (log_file_output_stream != null) {
            try {
                log_file_output_stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void initializeLogFile() {
        if (log_file_output_stream != null) {
            return;
        }
        if (log_file_path != null && !log_file_path.trim().isEmpty()) {
            try {
                File file = new File(log_file_path);
                if (!file.exists()) {
                    file.createNewFile();
                }
                log_file_output_stream = new FileOutputStream(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    int write_line_count =0;
    private synchronized void writeLine(String s) {
        if(log_file_output_stream == null) {
            initializeLogFile();
        }
        if (log_file_output_stream != null) {
            try {
                log_file_output_stream.write(s.getBytes("utf-8"));
                log_file_output_stream.write('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        write_line_count++;
        if(write_line_count > 5) {
            flush();
        }
    }

    @NonNull
    private String generatorLogMessage(String level, String tag, String message) {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd :hh:mm:ss");
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(dateFormat.format(new Date()));
        builder.append("]\t");

        builder.append("[");
        builder.append(Thread.currentThread().getName());
        builder.append("]\t");

        builder.append("[");
        builder.append(level);
        builder.append("]\t");

        builder.append("[");
        builder.append(tag);
        builder.append("]\t");

        builder.append(message);

        return builder.toString();
    }

    public void d(String tag, String message) {
        Log.d(tag, message);
        writeLine(generatorLogMessage("Debug", tag, message));
    }

    public void d(String message) {
        Log.d("NONE", message);
        writeLine(generatorLogMessage("Debug", "NONE", message));
    }

    public void w(String tag, String message) {
        Log.w(tag, message);
        writeLine(generatorLogMessage("Warning", tag, message));
    }

    public void w( String message) {
        Log.w("NONE", message);
        writeLine(generatorLogMessage("Warning", "NONE", message));
    }

    public void e(String tag, String message) {
        Log.e(tag, message);
        writeLine(generatorLogMessage("Error", tag, message));
    }

    public void e(String message) {
        Log.e("NONE", message);
        writeLine(generatorLogMessage("Error", "NONE", message));
    }
}
