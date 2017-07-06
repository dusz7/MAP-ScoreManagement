package com.hitiot.dusz7.naiverss.cache;

import android.content.Context;
import android.util.Log;

import com.hitiot.dusz7.naiverss.application.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by dusz7 on 2017/7/4.
 */

public class CacheUtils {

    private static Context context = MyApplication.getContext();

    private final static int CACHE_TIME = 100000;

    public static boolean saveObject(Serializable serializable, String fileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = context.openFileOutput(fileName, context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(serializable);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Serializable readObject(String file) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static boolean isCacheDataFailure(String cachefile) {
        boolean failure = false;
        File data = context.getFileStreamPath(cachefile);
        if (data.exists()
                && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME) {
            failure = true;
        }
        else if (!data.exists())
            failure = true;
        return failure;
    }

    public static void clearFile(String cachefile) {
        File data = context.getFileStreamPath(cachefile);
        if(data.exists()) {
            data.delete();
        }
    }
}
