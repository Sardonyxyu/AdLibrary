package com.yingyongduoduo.ad.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StringUtil {

    public StringUtil() {
    }

    public static boolean isEmpty(String url) {
        return url == null || url.trim().length() == 0;
    }

    public static void writeTo(String str, File file) throws IOException {
        if (file != null) {
            File var2;
            if (!(var2 = file.getParentFile()).exists()) {
                var2.mkdirs();
            }

            FileWriter var10000 = new FileWriter(file);
            var10000.write(str);
            var10000.close();
        } else {
            throw new IOException("Target File Can not be null in StringUtil.writeTo");
        }
    }

    public static int parseInteger(String str, int optValue) {
        try {
            return Integer.parseInt(str);
        } finally {
            return optValue;
        }
    }

    public static float parseFloat(String str, float optValue) {
        try {
            return Float.parseFloat(str);
        } finally {
            return optValue;
        }
    }

}
