package com.tl.common.ext.model;

public class RegexPattern {


    public static final String NUMBER_PATTERN = "^[+-]?\\d*\\.\\d*$";
    public static final String MONEY_PATTERN = "^(([1-9][0-9]*)|(([0]\\.\\d{1,2}|[1-9][0-9]*\\.\\d{1,2})))$";
    public static final String EMAIL_PATTERN = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    public static final String PHONE_PATTERN = "^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$";
    public static final String CHINESE_PATTERN = "^[\\u4e00-\\u9fa5]{0,}$";
    public static final String LETTERS_NUMBERS_PATTERN = "^[A-Za-z0-9]+$";
    public static final String TAXPAYER_CODE_PATTERN = "^([0-9a-zA-Z]{15}|[0-9a-zA-Z]{18})$";

    public static final String REGEX_DATE_STR_YYYYMMDD_LINE ="^([1-2]\\d{3})[\\-](0[1-9]|10|11|12)[\\-]([1-2][0-9]|0[1-9]|30|31)$";
    public static final String REGEX_DATE_YYYYMMDD_STR="^([1-2]\\d{3})(0[1-9]|10|11|12)([1-2][0-9]|0[1-9]|30|31)$";
    public static final String REGEX_DATE_YYYYMM_STR="^([1-2]\\d{3})(0[1-9]|10|11|12)$";
    public static final String REGEX_SBYWBDM="^[A]\\d{7}$";
    public static final String REGEX_SBPC="^([0-9]|[A-Z])[0-9]$";
    public static final String REGEX_HGBGDH="^\\d{18}$";

}
