package words.com.fileservicev2.domain.utils;

import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.MultimediaInfo;

public class AudioUtils {


    public static EncodingAttributes toEncodingAttributes(MultimediaInfo info, int vbrQuality) {
        AudioAttributes audioAttr = new AudioAttributes();
        audioAttr.setCodec("libmp3lame");
        audioAttr.setBitRate(null); // For VBR
        audioAttr.setQuality(vbrQuality);
        audioAttr.setChannels(info.getAudio().getChannels());
        audioAttr.setSamplingRate(info.getAudio().getSamplingRate());

        EncodingAttributes attr = new EncodingAttributes();
        attr.setOutputFormat("mp3");
        attr.setAudioAttributes(audioAttr);
        return attr;
    }

}
