/*********************************************************************
 * *
 * Copyright (c) 2002-2007 by Survey Software Services, Inc.         *
 * All rights reserved.                                              *
 * *
 * This computer program is protected by copyright law and           *
 * international treaties. Unauthorized reproduction or distribution *
 * of this program, or any portion of it, may result in severe civil *
 * and criminal penalties, and will be prosecuted to the maximum     *
 * extent possible under the law.                                    *
 * *
 *********************************************************************/

package com.qa.utils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil {

    private static String info;

    public static String getInfoAboutPath() {
        return info;
    }

    /**
     * This method take URL and return last URL's part
     *
     * @param pathFileName String - full path
     * @return last URL's part
     *         example :
     *         pathFileName = "http://v69.surveysoftwareservices.com/Member/SurveyLogic/DesignSurveyGoTo.jsp?QID=3822240"
     *         return - "DesignSurveyGoTo.jsp?QID=3822240"
     */
    public static String getURL(String pathFileName) {
        if (AppUtil.isStringEmpty(pathFileName)) return "";
        return pathFileName.replaceFirst("^.+[/\\\\]([^/\\\\]+)$", "$1");
    }

    /**
     * This method take URL and return last URL's part without any parametrs
     *
     * @param pathFileName String - full path
     * @return last URL's part without parametrs
     *         example :
     *         pathFileName = "http://v69.surveysoftwareservices.com/Member/SurveyLogic/DesignSurveyGoTo.jsp?QID=3822240"
     *         return - "DesignSurveyGoTo.jsp"
     */

    public static String getURLWithoutRequestParameters(String pathFileName) {
        if (AppUtil.isStringEmpty(pathFileName)) return "";
        String s = pathFileName.replaceFirst("^.+[/\\\\]([^/\\\\]+)$", "$1");
        return s.replaceFirst("^([^?#]+)([\\?#].*)?$", "$1");
    }

    /**
     * This method take URL and return last URL's part
     *
     * @param URL           String - full path
     * @param parameterName String - expected parametr
     * @return parametr's value
     *         example :
     *         URL = "http://v69.surveysoftwareservices.com/Member/SurveyLogic/DesignSurveyGoTo.jsp?QID=3822240"
     *         parameterName - "QID"
     *         return - "3822240"
     */

    public static String getParameterFromURL(String URL, String parameterName) {
        String result = "";
        Matcher match = Pattern.compile("((?<=" + parameterName + "=)[a-z0-9A-Z]+[^&])").matcher(URL);
        if (match.find()) {
            result = URL.substring(match.start(), match.end());
        }
        return result;
    }

    public static long getIdFromUrl(String url) {
        Pattern pattern = Pattern.compile("\\d*$");
        Matcher matcher = pattern.matcher(url);
        long id = 0;
        if (matcher.find()) {
            id = Long.parseLong(matcher.group(0));
        }
        return id;
    }

    /**
     * This method check is requested string empty
     *
     * @param src CharSequence - string will be checked
     * @return true if src empty, else return false
     */
    public static boolean isStringEmpty(CharSequence src) {
        try {
            return src == null || src.toString().trim().length() == 0;
        } catch (Exception ex) {/**/
            ex.printStackTrace();
        }
        return true;
    }

    public static String getFileNameFromFileLocation(final String FILE_LOCATION) {
        String fileName = null;
        try {
            if (FILE_LOCATION == null || FILE_LOCATION.trim().length() == 0) return null;
            if (!FILE_LOCATION.contains("/")) {
                throw new IllegalArgumentException("File location is not valid. Check syntax of " + FILE_LOCATION);
            }
            if (!FILE_LOCATION.contains(".")) {
                throw new IllegalArgumentException("File extension is not valid. Check syntax of " + FILE_LOCATION);
            }
            int beginIndex = FILE_LOCATION.lastIndexOf("/") + 1;
            int lastIndex = FILE_LOCATION.lastIndexOf(".");
            if (beginIndex >= lastIndex)
                throw new IllegalArgumentException("File directory is not valid. Check syntax of " + FILE_LOCATION);
            fileName = FILE_LOCATION.substring(beginIndex, lastIndex);
        } catch (Exception ex) {
            ex.getMessage();
        }
        return fileName;
    }

    public static String loadFileAsString(String fileName, String encoding) {
        URL resource = AppUtil.class.getResource(fileName);
        try {
            InputStreamReader f = encoding == null ? new FileReader(resource == null ? fileName : resource.getPath()) : new InputStreamReader(
                    new FileInputStream(resource == null ? fileName : resource.getPath()), encoding);
            StringBuffer sb = new StringBuffer();
            try {
                char[] buf = new char[32768];
                int len;
                while ((len = f.read(buf, 0, buf.length)) >= 0) {
                    sb.append(buf, 0, len);
                }
                return sb.toString();
            } finally {
                try {
                    f.close();
                } catch (Exception e) {
                }
                ;
            }
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    public static void getListByFile(List<String> list, String fileName, String encoding, String delimiter) {
        if (list != null) list.clear();
        String str = loadFileAsString(fileName, encoding);
        if (str.length() <= 0) return;
        String[] arrayStr = str.split(delimiter);
        Collections.addAll(list, arrayStr);
    }

    /**
     * This method read file from resorceName and return Propertys
     *
     * @param aResourceName String - property's file (for example config.properties)
     * @return Propertys - collection with all propertys from file
     */
    public static Properties loadPropertiesFromClassPath(String aResourceName) {
        //InputStream is = GeneralConfig.class.getResourceAsStream(resorceName);
        InputStream is = null;
        try {
            URL resource = AppUtil.class.getClassLoader().getResource(aResourceName);
            info = resource.getPath();
            is = new FileInputStream(info);
        } catch (FileNotFoundException e) {
            // Let 'is' be null
            System.out.println("Not found config file (" + aResourceName + ")");
            return null;
        }
        if (is == null) {
            System.out.println("Not found config file (" + aResourceName + ")");
            return new Properties();
        }

        Properties ret = new Properties();
        try {
            ret.load(is);
        } catch (IOException ex) {
            System.out.println("Fail loading config file " + aResourceName);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static Properties loadPropertiesFromClassPath(String aResourceName, Class aClass) {
        InputStream is = null;
        try {
            URL resource = aClass.getResource(aResourceName);
            info = resource.getPath();
            if (info.contains("!")) {
                File file = new File(info);
                if (!file.exists()) throw new FileNotFoundException(info);
                else System.out.println("GOOD");
            } else is = new FileInputStream(resource == null ? aResourceName : info);
        } catch (FileNotFoundException e) {
            // Let 'is' be null
            e.printStackTrace();
        }
        if (is == null) {
            System.out.println("Not found config file (" + aResourceName + ")");
            return new Properties();
        }
        Properties ret = new Properties();
        try {
            ret.load(is);
        } catch (IOException ex) {
            System.out.println("Fail loading config file " + aResourceName);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * This method return StackTrace as String (only last rows)
     *
     * @param trace StackTraceElement[] - array which contain StackTrace
     * @return String - with last row from Stack Trace
     */

    public static String stackTraceToString(StackTraceElement[] trace) {
        StringBuffer sb = new StringBuffer(512);
        int hiddenCount = 0;
        for (int i = 0; i < trace.length; i++) {
            String prefix = "org.apache.catalina.";// 30 rows are usually hidden with this
            String breakClass = "javax.servlet.http.HttpServlet";
            String className = trace[i].getClassName();
            if (className.equals(breakClass)) break;

            boolean hide = className.startsWith(prefix);
            if (i == trace.length - 1) {
                hide = false; // last row is shown
            } else {
                if (hiddenCount == 0 && !trace[i + 1].getClassName().startsWith(prefix)) {
                    hide = false; // no sense to hide the only repeating row
                }
            }
            if (!hide) {
                if (hiddenCount > 0) {
                    sb.append("\t").append(hiddenCount).append(" rows (").append(prefix).append("..) hidden");
                }
                hiddenCount = 0;
                sb.append("\tat ").append(trace[i]);
            } else {
                hiddenCount++;
            }
        }
        return sb.toString();
    }

    public static String getStackTrace(Exception ex) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMessage()).append("\n");
        for (StackTraceElement element : ex.getStackTrace()) {
            builder.append(element).append("\n");
        }
        return builder.toString();
    }

}
