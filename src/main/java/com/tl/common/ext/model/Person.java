package com.tl.common.ext.model;

import com.tl.common.ext.annotation.ExcelRootTitle;
import com.tl.common.ext.annotation.ExcelSetting;

import java.math.BigDecimal;
import java.util.Date;

@ExcelRootTitle(rootTitles = {"name","age"})
public class Person {

    public Person(String xing, String daming, String xiaoming, String age) {
        this.xing = xing;
        this.daming = daming;
        this.xiaoming = xiaoming;
        this.age = age;
    }

    @ExcelSetting(colTitleName = "姓名",isFatherTitle = true,childTitles = {"xing","ming"})
    private String name;


    @ExcelSetting(colTitleName = "名",isFatherTitle = true,childTitles = {"daming","xiaoming"})
    private String ming;


    @ExcelSetting(colTitleName = "姓" ,isFirst = true,nextColName = "daming")
    private String xing;

    @ExcelSetting(colTitleName = "大名",nextColName = "xiaoming")
    private String daming;
    @ExcelSetting(colTitleName = "小名",nextColName = "age")
    private String xiaoming;

    @ExcelSetting(colTitleName = "年龄")
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMing() {
        return ming;
    }

    public void setMing(String ming) {
        this.ming = ming;
    }

    public String getXing() {
        return xing;
    }

    public void setXing(String xing) {
        this.xing = xing;
    }

    public String getDaming() {
        return daming;
    }

    public void setDaming(String daming) {
        this.daming = daming;
    }

    public String getXiaoming() {
        return xiaoming;
    }

    public void setXiaoming(String xiaoming) {
        this.xiaoming = xiaoming;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
