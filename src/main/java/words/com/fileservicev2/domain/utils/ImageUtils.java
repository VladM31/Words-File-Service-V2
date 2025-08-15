package words.com.fileservicev2.domain.utils;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static final String WEBP_FORMAT = "webp";


    public static BufferedImage resizeImage(BufferedImage originalImage, int maxDimension, Double outputQuality) throws IOException {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (width <= maxDimension && height <= maxDimension) {
            if (outputQuality == null) {
                return originalImage;
            }

            return Thumbnails.of(originalImage)
                    .scale(1)
                    .outputQuality(outputQuality)
                    .asBufferedImage();
        }

        double scalingFactor = Math.min((double) maxDimension / width, (double) maxDimension / height);
        var quality = outputQuality == null ? 1.0 : outputQuality;

        return Thumbnails.of(originalImage)
                .scale(scalingFactor)
                .outputQuality(quality)
                .asBufferedImage();
    }

    private static BufferedImage convertImageFormat(BufferedImage originalImage, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, formatName, baos);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return ImageIO.read(bais);
    }


}
