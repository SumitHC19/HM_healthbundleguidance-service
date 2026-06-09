package com.alight.asg.model.header.v1_0;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class TokenDecoderUtil {

    // https://jira.alight.com/browse/APIFND-2242
    private static String encodedChar1;
    private static String encodedChar2;
    public static String encodingChar = StandardCharsets.UTF_8.name();

    static {
        // No checked exception when using Charset overload
        encodedChar1 = URLEncoder.encode("{", StandardCharsets.UTF_8);
        encodedChar2 = URLEncoder.encode("[", StandardCharsets.UTF_8);
    }

    /**
     *
     * @param aJsonString
     * @return String
     */

    public static String decodeIfNeeded(String aJsonString) throws UnsupportedEncodingException {
        if (aJsonString != null && (aJsonString.startsWith(encodedChar1) || aJsonString.startsWith(encodedChar2))) {
            aJsonString = URLDecoder.decode(aJsonString, encodingChar);
        }

        return aJsonString;
    }
}
