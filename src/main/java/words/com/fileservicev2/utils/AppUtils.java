package words.com.fileservicev2.utils;

import java.time.ZoneId;

public class AppUtils {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("window");
    }

    public static String getFilePrefixByOs() {
        return isWindows() ? "" : "/";
    }

    public static ZoneId APP_ZONE_ID = ZoneId.of("UTC"); // Default zone ID for the application
}
