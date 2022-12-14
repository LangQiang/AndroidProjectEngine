package com.lazylite.mod.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lzf on 5/28/21 11:23 AM
 */
public class StringCodec {
    private static final byte[] UTF8Signature = new byte[] { -17, -69, -65 }; // EF

    // BB
    // BF
    private static final byte[] UTF16BESignature = new byte[] { -2, -1, }; // FE

    // FF
    private static final byte[] UTF16LESignature = new byte[] { -1, -2, }; // FF

    // FE
    protected static Map<String, byte[]> encMap;

    static {
        encMap = new HashMap<String, byte[]>();
        encMap.put("UTF-8", UTF8Signature);
        encMap.put("UTF8", UTF8Signature);
        encMap.put("UTF-16BE", UTF16BESignature);
        encMap.put("UTF-16LE", UTF16LESignature);
    }

    private boolean addSignature = false;
    private String detectedEncoding;

    /**
     * Decide if this class is to add signature byte sequence indicating UTF-8
     * and UTF-16 Default is to not add signature
     */
    public boolean isAddSignature() {
        return addSignature;
    }

    public void setAddSignature(boolean addIt) {
        this.addSignature = addIt;
    }

    public byte[] encode(String s, String encoding)
            throws UnsupportedEncodingException {
        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }

        byte[] buf = s.getBytes(encoding);
        detectedEncoding = encoding; // looks nicer, but the point is to set

        // this when decoding
        if (!addSignature) {
            return buf;
        }

        byte[] prefix = (byte[])encMap.get(encoding);

        if (prefix == null) {
            return buf;
        }

        byte[] finalBuf = new byte[0];
        try {
            finalBuf = new byte[prefix.length + buf.length];
            System.arraycopy(prefix, 0, finalBuf, 0, prefix.length);
            System.arraycopy(buf, 0, finalBuf, prefix.length, buf.length);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return finalBuf;
    }

    public byte[] encode(String s) throws UnsupportedEncodingException {
        return encode(s, null);
    }

    public String decode(byte[] buf) throws UnsupportedEncodingException {
        return decode(buf, 0, buf.length, null);
    }

    public String decode(byte[] buf, String defaultEncoding)
            throws UnsupportedEncodingException {
        return decode(buf, 0, buf.length, defaultEncoding);
    }

    public String decode(byte[] buf, int offset, int length)
            throws UnsupportedEncodingException {
        return decode(buf, offset, length, null);
    }

    public String decode(byte[] buf, int offset, int length, String defaultEncoding)
            throws UnsupportedEncodingException {
        if (defaultEncoding == null) {
            defaultEncoding = System.getProperty("file.encoding");
        }

        detectedEncoding = null;

        // First check for signature sequence indicating encoding
        Iterator<Map.Entry<String, byte[]>> it = encMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, byte[]> e = (Map.Entry<String, byte[]>)it.next();
            byte[] signature = (byte[])e.getValue();

            if (startsWith(buf, offset, signature)) {
                detectedEncoding = (String)e.getKey();

                return new String(buf, signature.length + offset, length - signature.length,
                        detectedEncoding);
            }
        }

        // Try to guess if encoding is UTF-8
        if (isUTF8(buf, offset, length)) {
            detectedEncoding = "UTF-8";

            return new String(buf, offset, length, detectedEncoding);
        }

        return new String(buf, 0, length, defaultEncoding);
    }

    public static boolean isUTF8(byte[] buf) {
        return isUTF8(buf, 0, buf.length);
    }

    public static boolean isUTF8(byte[] buf, int offset, int length) {
        if (startsWith(buf, offset, UTF8Signature)) {
            return true;
        }

        boolean yesItIs = false;

        for (int i = offset; i < (offset + length); i++) {
            if ((buf[i] & 0xC0) == 0xC0) { // Begins with 0b11xxxxxx

                // Count number of 1 bits in first byte, equals total number of
                // bytes for UTF-8 character.
                int nBytes;

                for (nBytes = 2; nBytes < 8; nBytes++) {
                    int mask = 1 << (7 - nBytes);

                    if ((buf[i] & mask) == 0) {
                        break;
                    }
                }

                // Check that the following bytes begin with 0b10xxxxxx
                for (int j = 1; j < nBytes; j++) {
                    if (((i + j) >= length) || ((buf[i + j] & 0xC0) != 0x80)) {
                        return false;
                    }
                }

                yesItIs = true;
            }
        }

        return yesItIs;
    }

    public String getDetectedEncoding() {
        return detectedEncoding;
    }

    public static boolean isEncodable(String s, String encoding)
            throws UnsupportedEncodingException {
        StringCodec codec = new StringCodec();
        byte[] buf = codec.encode(s, encoding);
        String s2 = codec.decode(buf, encoding);

        return s2.equals(s);
    }

    private static boolean startsWith(byte[] buf, int offset, byte[] prefix) {
        for (int i = 0; i < prefix.length; i++) {
            if (((i + offset) == buf.length) || (buf[i + offset] != prefix[i])) {
                return false;
            }
        }

        return true;
    }

    public static String changeEncode(String string) {
        try {
            if (string != null && !StringCodec.isEncodable(string, "gbk")) {
                return new String(string.getBytes("iso-8859-1"), "gbk");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }


    public static final String SPEC_CHAR = "~$\\/:,;*?|???&";
    public static final String URL_PATTERN = "^(http(s{0,1})://)*[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";

    // ??????????????????????????????
    @SuppressLint("DefaultLocale")
    public static boolean isValidSongName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }

        Pattern patt = Pattern.compile(URL_PATTERN);
        Matcher matcher = patt.matcher(name);
        if(matcher.matches()) {
            return false;
        }

        int n = name.lastIndexOf('.');
        if (n > 0) {
            name = name.substring(0, n);
        }
        name = name.toLowerCase();
        int nLen = name.length();
        for (int i = 0; i < nLen; ++ i) {
            char c = name.charAt(i);
            if ((c < '0' || c > '9') && SPEC_CHAR.indexOf(c) == -1) {
                return true;
            }
        }

        return false;
    }


    // ?????????????????????
    public static boolean isGarbled(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }

        int nLen = str.length();
        for (int i = 0; i < nLen; ++ i)
        {
            char c = str.charAt(i);
            if (SPEC_CHAR.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }
}
