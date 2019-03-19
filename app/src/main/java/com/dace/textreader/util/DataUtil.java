package com.dace.textreader.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Environment;

import com.dace.textreader.bean.VoiceErrorWordBean;

import java.util.List;

import okhttp3.MediaType;

/**
 * 数据格式转换
 * Created by 70391 on 2017/7/31.
 */

public class DataUtil {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static boolean isDraftNeedRefresh = true;  //草稿箱是否需要刷新

    public static boolean isMp3Ok = false;  //是否能转mp3

    /**
     * 文章类型转换(中文)
     *
     * @param typeCode 类型代码
     * @return 类型
     */
    public static String typeConversion(int typeCode) {
        String type = "";
        switch (typeCode) {
            case 0:
                type = "故事";
                break;
            case 1:
                type = "科学";
                break;
            case 2:
                type = "诗词曲";
                break;
            case 3:
                type = "美文";
                break;
            case 4:
                type = "文言文";
                break;
            case 5:
                type = "传记";
                break;
            case 6:
                type = "小说";
                break;
            case 7:
                type = "历史";
                break;
            case 8:
                type = "国学";
                break;
            case 9:
                type = "哲学";
                break;
            case 10:
                type = "课内";
                break;
        }
        return type;
    }

    private static final String[] grade = new String[]{"一年级上册", "一年级下册",
            "二年级上册", "二年级下册", "三年级上册", "三年级下册", "四年级上册", "四年级下册",
            "五年级上册", "五年级下册", "六年级上册", "六年级下册",
            "初一上册", "初一下册", "初二上册", "初二下册", "初三上册", "初三下册",
            "高一上册", "高一下册", "高二上册", "高二下册", "高三上册", "高三下册"};
    private static final int[] gradeCode = new int[]{111, 112, 121, 122, 131, 132,
            141, 142, 151, 152, 161, 162,
            211, 212, 221, 222, 231, 232,
            311, 312, 321, 322, 331, 332};

    /**
     * 根据年级获取gradeId
     *
     * @param string
     * @return
     */
    public static int grade2GradeId(String string) {
        int id = -1;
        for (int i = 0; i < grade.length; i++) {
            if (string.equals(grade[i])) {
                id = gradeCode[i];
                break;
            }
        }
        return id;
    }

    /**
     * 根据年级代码获取年级
     *
     * @param code
     * @return
     */
    public static String gradeCode2Grade(int code) {
        String string = "";
        for (int i = 0; i < gradeCode.length; i++) {
            if (code == gradeCode[i]) {
                string = grade[i];
                break;
            }
        }
        return string;
    }

    private static final String[] primaryGrade = new String[]{"一年级上", "一年级下",
            "二年级上", "二年级下", "三年级上", "三年级下", "四年级上", "四年级下",
            "五年级上", "五年级下", "六年级上", "六年级下"};
    private static final String[] juniorHighGrade = new String[]{"初一上", "初一下",
            "初二上", "初二下", "初三上", "初三下"};
    private static final String[] highGrade = new String[]{"高一上", "高一下",
            "高二上", "高二下", "高三上", "高三下"};
    private static final int[] primaryGradeCode = new int[]{111, 112, 121, 122, 131, 132,
            141, 142, 151, 152, 161, 162};
    private static final int[] juniorHighGradeCode = new int[]{211, 212, 221, 222, 231, 232};
    private static final int[] highGradeCode = new int[]{311, 312, 321, 322, 331, 332};

    /**
     * 根据年级获取gradeId
     *
     * @param grade 年级
     * @return gradeId
     */
    public static int grade2GradeId(String grade, int code) {
        int gradeId = -1;
        if (code == 0) {  //小学
            for (int i = 0; i < primaryGrade.length; i++) {
                if (grade.equals(primaryGrade[i])) {
                    gradeId = primaryGradeCode[i];
                    break;
                }
            }
        } else if (code == 1) {  //初中
            for (int i = 0; i < juniorHighGrade.length; i++) {
                if (grade.equals(juniorHighGrade[i])) {
                    gradeId = juniorHighGradeCode[i];
                    break;
                }
            }
        } else if (code == 2) {  //高中
            for (int i = 0; i < highGrade.length; i++) {
                if (grade.equals(highGrade[i])) {
                    gradeId = highGradeCode[i];
                    break;
                }
            }
        }
        return gradeId;
    }

    /**
     * 根据年级ID得到年级代码
     *
     * @param gradeId 年级ID
     * @return
     */
    public static int gradeId2Code(int gradeId) {
        int code = -1;
        for (int g_id_0 : primaryGradeCode) {
            if (gradeId == g_id_0) {
                code = 0;
                break;
            }
        }
        if (code == -1) {
            for (int g_id_1 : juniorHighGradeCode) {
                if (gradeId == g_id_1) {
                    code = 1;
                    break;
                }
            }
        }
        if (code == -1) {
            for (int g_id_2 : highGradeCode) {
                if (gradeId == g_id_2) {
                    code = 2;
                    break;
                }
            }
        }
        return code;
    }

    /**
     * 根据年级代码获取年级
     *
     * @param gradeCode 年级代码
     * @param code      小学初中高中
     * @return
     */
    public static String gradeCode2Grade(int gradeCode, int code) {
        String grade = "";
        if (code == 0) {
            for (int i = 0; i < primaryGradeCode.length; i++) {
                if (gradeCode == primaryGradeCode[i]) {
                    grade = primaryGrade[i];
                    break;
                }
            }
        } else if (code == 1) {
            for (int i = 0; i < juniorHighGradeCode.length; i++) {
                if (gradeCode == juniorHighGradeCode[i]) {
                    grade = juniorHighGrade[i];
                    break;
                }
            }
        } else if (code == 2) {
            for (int i = 0; i < highGradeCode.length; i++) {
                if (gradeCode == highGradeCode[i]) {
                    grade = highGrade[i];
                    break;
                }
            }
        }
        return grade;
    }

    private static final String[] ChineseGrade = new String[]{"一年级", "二年级", "三年级", "四年级",
            "五年级", "六年级", "初一", "初二", "初三", "高一", "高二", "高三"};
    private static final int[] CodeGrade = new int[]{11, 12, 13, 14, 15, 16, 21, 22, 23, 31, 32, 33};

    /**
     * 年级代码转汉语年级等级
     *
     * @param gradeCode
     * @return
     */
    public static String gradeCode2Chinese(int gradeCode) {
        String grade = "";
        for (int i = 0; i < CodeGrade.length; i++) {
            int code = CodeGrade[i];
            if (gradeCode / 10 == code) {
                grade = ChineseGrade[i];
                break;
            }
        }
        return grade;
    }

    /**
     * 等级转换
     *
     * @param gradeCode
     * @return
     */
    public static int gradeCode2Level(int gradeCode) {
        return gradeCode / 100;
    }

    /**
     * double类型转String
     */
    public static String double2String(double value) {
        if (Math.round(value) - value == 0) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    /**
     * double转整数String
     */
    public static String double2IntString(double value) {
        String result = double2String(value);
        if (result.contains(".")) {
            result = result.substring(0, result.indexOf("."));
        }
        return result;
    }

    /**
     * double转Float
     */
    public static float double2Float(double value) {
        float result;
        int i = (int) value;

        if (Math.round(value) - value == 0) {
            result = i;
        } else {
            result = i + 0.5f;
        }

        return result;
    }

    /**
     * 格式化String
     */
    public static String formatString(String value) {
        if (value.contains(".")) {
            if (value.split(".")[1].replace("0", "").isEmpty()) {
                return value.split(".")[0];
            }
        }
        return value;
    }

    /**
     * 获取下载文件的路径
     *
     * @param downloadUrl
     * @param version
     * @return
     */
    public static final String getDownloadFileName(String downloadUrl, String version) {
        String filename;
//        if (downloadUrl.contains("/") && downloadUrl.contains(".")) {
//            filename = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
//            filename = filename.substring(0, filename.lastIndexOf(".")) + version +
//                    filename.substring(filename.lastIndexOf("."));
//        } else {
//            filename = "pythe" + version + ".apk";
//        }
        filename = "pythe" + version + ".apk";
        //将文件下载到Environment.DIRECTORY_DOWNLOADS目录下，也就是SD卡的Download目录
        String directory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getPath();
        return directory + filename;
    }

    /**
     * 复制内容到截切板
     *
     * @param str 要复制的内容
     */
    public static void copyContent(Context context, String str) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("Label", str);
        if (cm != null) {
            cm.setPrimaryClip(cd);
            MyToastUtil.showToast(context, "复制成功");
        }
    }

    /**
     * 静默复制内容到截切板
     *
     * @param str 要复制的内容
     */
    public static void copyEmptyContentNoTips(Context context, String str) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cd = ClipData.newPlainText("Label", str);
        if (cm != null) {
            cm.setPrimaryClip(cd);
        }
    }

    /**
     * 获取剪切板的第一条内容
     *
     * @return
     */
    public static String getClipboardContent(Context context) {
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null) {
                ClipData.Item item = clipData.getItemAt(0);
                if (item != null && item.getText() != null) {
                    return item.getText().toString();
                }
            }
        }
        return null;
    }

    /**
     * 增加阅读数
     *
     * @param views
     * @return
     */
    public static String increaseViews(String views) {
        if (views == null) {
            return "0";
        } else {
            //使用正则表达式判断该字符串是否为数字，第一个\是转义符，\d+表示匹配1个或
            // 多个连续数字，"+"和"*"类似，"*"表示0个或多个
            if (views.matches("\\d+")) {
                int view = Integer.valueOf(views);
                view = view + 1;
                return String.valueOf(view);
            } else {
                return "0";
            }
        }
    }

    /**
     * 格式化错误字符集合
     *
     * @param content
     * @param list
     */
    public static void formatVoiceErrorWords(
            String content, List<VoiceErrorWordBean> list) {
        for (int i = 0; i < content.length(); i++) {
            String word = content.substring(i, i + 1);
            if (isSymbol(word)) {
                for (int j = 0; j < list.size(); j++) {
                    int index = list.get(j).getIndex();
                    if (index >= i) {
                        index = index + 1;
                        list.get(j).setIndex(index);
                    }
                }
            }

        }
    }

    /**
     * 判断是否是符号或者换行
     *
     * @return
     */
    private static boolean isSymbol(String string) {
        boolean b = false;

        String tmp = string;
        tmp = tmp.replaceAll("\\p{P}", "");
        if (string.length() != tmp.length()) {
            b = true;
        }

        if (string.contains("\n")) {
            b = true;
        }

        return b;
    }

}
