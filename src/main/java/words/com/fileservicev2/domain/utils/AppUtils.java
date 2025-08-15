package words.com.fileservicev2.domain.utils;

public class AppUtils {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("window");
    }

    public static String getFilePrefixByOs() {
        return isWindows() ? "" : "/";
    }
}
