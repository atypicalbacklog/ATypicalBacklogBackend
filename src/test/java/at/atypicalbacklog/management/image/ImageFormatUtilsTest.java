package at.atypicalbacklog.management.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageFormatUtilsTest {

    private static final byte[] JPEG_HEADER = new byte[] {(byte) 0xFF, (byte) 0xD8, 0x00};
    private static final byte[] PNG_HEADER = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] GIF_HEADER = "GIF89a".getBytes();
    private static final byte[] WEBP_HEADER = new byte[] {
        'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P'
    };

    @Test
    void detectMimetypeFromBytes_recognizesCommonFormats() {
        assertEquals("image/jpeg", ImageFormatUtils.detectMimetypeFromBytes(JPEG_HEADER));
        assertEquals("image/png", ImageFormatUtils.detectMimetypeFromBytes(PNG_HEADER));
        assertEquals("image/gif", ImageFormatUtils.detectMimetypeFromBytes(GIF_HEADER));
        assertEquals("image/webp", ImageFormatUtils.detectMimetypeFromBytes(WEBP_HEADER));
    }

    @Test
    void detectMimetype_prefersMagicBytesOverHint() {
        assertEquals("image/webp", ImageFormatUtils.detectMimetype(WEBP_HEADER, "image/jpeg"));
    }
}
