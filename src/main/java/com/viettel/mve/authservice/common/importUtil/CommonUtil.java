package com.viettel.mve.authservice.common.importUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jxl.Workbook;
import jxl.WorkbookSettings;

public class CommonUtil {
    private static final String[] SIGNED_ARR = new String[] {"à", "á", "ạ", "ả", "ã", "â", "ầ", "ấ", "ậ", "ẩ", "ẫ",
            "ă", "ằ", "ắ", "ặ", "ẳ", "ẵ", "è", "é", "ẹ", "ẻ", "ẽ", "ê", "ề", "ế", "ệ", "ể", "ễ", "ì", "í", "ị", "ỉ",
            "ĩ", "ò", "ó", "ọ", "ỏ", "õ", "ô", "ồ", "ố", "ộ", "ổ", "ỗ", "ơ", "ờ", "ớ", "ợ", "ở", "ỡ", "ù", "ú", "ụ",
            "ủ", "ũ", "ư", "ừ", "ứ", "ự", "ử", "ữ", "ỳ", "ý", "ỵ", "ỷ", "ỹ", "đ", "À", "Á", "Ạ", "Ả", "Ã", "Â", "Ầ",
            "Ấ", "Ậ", "Ẩ", "Ẫ", "Ă", "Ằ", "Ắ", "Ặ", "Ẳ", "Ẵ", "È", "É", "Ẹ", "Ẻ", "Ẽ", "Ê", "Ề", "Ế", "Ệ", "Ể", "Ễ",
            "Ì", "Í", "Ị", "Ỉ", "Ĩ", "Ò", "Ó", "Ọ", "Ỏ", "Õ", "Ô", "Ồ", "Ố", "Ộ", "Ổ", "Ỗ", "Ơ", "Ờ", "Ớ", "Ợ", "Ở",
            "Ỡ", "Ù", "Ú", "Ụ", "Ủ", "Ũ", "Ư", "Ừ", "Ứ", "Ự", "Ử", "Ữ", "Ỳ", "Ý", "Ỵ", "Ỷ", "Ỹ", "Đ"};
    private static final String[] UNSIGNED_ARR = new String[] {"a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
            "a", "a", "a", "a", "a", "a", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "i", "i", "i", "i",
            "i", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "u", "u", "u",
            "u", "u", "u", "u", "u", "u", "u", "u", "y", "y", "y", "y", "y", "d", "A", "A", "A", "A", "A", "A", "A",
            "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "E", "E", "E", "E", "E", "E", "E", "E", "E", "E", "E",
            "I", "I", "I", "I", "I", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O", "O",
            "O", "U", "U", "U", "U", "U", "U", "U", "U", "U", "U", "U", "Y", "Y", "Y", "Y", "Y", "D"};
    public static Workbook saveImportExcelFile(MultipartFile uploadFile, String fileName, String uploadPath)
            throws Exception {
        fileName = CommonUtil.getSafeFileName(CommonUtil.removeSign(fileName));
        if (isAllowedType(fileName)) {
            saveFile(uploadFile, fileName, uploadPath);
            File desDir = new File(uploadPath);
            File serverFile = new File(desDir.getAbsolutePath() + File.separator + fileName);
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252"); // UTF-8
            ws.setCellValidationDisabled(true);
            Workbook workbook = Workbook.getWorkbook(serverFile, ws);
            return workbook;
        } else {
            throw new Exception("FILE TYPE NOT ALLOW");
        }
    }
    public static void saveFile(MultipartFile uploadFile, String fileName, String uploadPath) throws Exception {
        if (isAllowedType(uploadFile.getName())) {
            File desDir = new File(uploadPath);
            if (!desDir.exists()) {
                desDir.mkdir();
            }
            String url = desDir.getAbsolutePath() + File.separator + getSafeFileName(fileName);
            OutputStream outStream = new FileOutputStream(url);
            try {
                InputStream inStream = uploadFile.getInputStream();
                int bytesRead;
                byte[] buffer = new byte[1024 * 8];
                while ((bytesRead = inStream.read(buffer, 0, 1024 * 8)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                inStream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                outStream.close();
            }
        } else {
            throw new Exception("FILE TYPE NOT ALLOW");
        }
    }
    public static boolean isAllowedType(String fileName) {
        if (fileName != null && !"".equals(fileName.trim())) {
            String[] allowedType = {".jpg", ".jpeg", ".png", ".doc", ".docx", ".xls", ".xlsx", ".pdf", ".rar", ".zip",
                    ".gif", ".txt", ".log", ".xml", ".7zip"};
            String ext = extractFileExt(fileName);
            if (ext == null) {
                return true;
            }
            ext = ext.toLowerCase();
            for (String extendFile : allowedType) {
                if (extendFile.equals(ext)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static String extractFileExt(String fileName) {
        int dotPos = fileName.lastIndexOf(".");
        if (dotPos != -1) {
            return fileName.substring(dotPos);
        }
        return null;
    }
    public static String getSafeFileName(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != '/' && c != '\\' && c != 0) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    public static String removeSign(String originalName) {
        if (originalName == null) {
            return "";
        }
        String result = originalName;
        for (int i = 0; i < SIGNED_ARR.length; i++) {
            result = result.replaceAll(SIGNED_ARR[i], UNSIGNED_ARR[i]);
        }
        return result;
    }
    public static String formatNumber(Double d) {
        if (d == null) {
            return "";
        } else {
            DecimalFormat format = new DecimalFormat("######.#####");
            return format.format(d);
        }
    }
    public static String convertDateToString(Date date) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.format(date);
        }
    }
    public static Date convertStringToDate(String date) throws Exception {
        if (date == null || date.trim().isEmpty()) {
            return null;
        } else {
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            dateFormat.setLenient(false);
            return dateFormat.parse(date);
        }
    }
    public static Double NVL(Double value) {

        return NVL(value, new Double(0));
    }

    public static Integer NVL(Integer value) {
        return value == null ? new Integer(0) : value;
    }

    public static Integer NVL(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Double NVL(Double value, Double defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Long NVL(Long value, Long defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static String NVL(String value, String nullValue, String notNullValue) {
        return value == null ? nullValue : notNullValue;
    }

    public static String NVL(String value, String defaultValue) {
        return NVL(value, defaultValue, value);
    }

    public static String NVL(String value) {
        return NVL(value, "");
    }

    public static Long NVL(Long value) {
        return NVL(value, 0L);
    }

    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static boolean isValidPhoneNumber(String email) {
        String regex = "^[0-9]+$";
        return email.matches(regex);
    }
}
