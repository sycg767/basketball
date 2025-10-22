package com.basketball.utils;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * User-Agent解析工具类
 */
public class UserAgentParser {

    /**
     * 解析浏览器信息
     */
    public static String getBrowser(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty()) {
            return "Unknown";
        }
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
            Browser browser = userAgent.getBrowser();
            return browser.getName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * 解析操作系统信息
     */
    public static String getOperatingSystem(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty()) {
            return "Unknown";
        }
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
            OperatingSystem os = userAgent.getOperatingSystem();
            return os.getName();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
