package com.tl.common.ext.utils;

import com.github.pagehelper.Page;
import com.tl.common.ext.model.PageInfo;
import javassist.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;

/*
 * @Description: 控制器常用工具
 * @Author Neo Lin
 * @Date  2018/4/11 14:30
 */
public class BaseController {

    /*
     * @Description:  处理成分页对象
     * @Author Neo Lin
     * @param  [list]
     * @return  com.tl.bjts.dzfp.model.PageInfo
     * @Date  2018/4/11
     */
    public PageInfo dealPageInfo(List list) {
        Page page = (Page) list;
        PageInfo<List> pageInfo = new PageInfo<>();
        if(list == null || list.isEmpty()){
            pageInfo.setPage(1);
            pageInfo.setRecords(0);
            pageInfo.setTotal(0);
            pageInfo.setCount(0L);
            pageInfo.setRows(new ArrayList());
        }else{
            com.github.pagehelper.PageInfo pi = page.toPageInfo();
            pageInfo.setPage(pi.getPageNum());
            pageInfo.setRecords(pi.getSize());
            pageInfo.setCount(pi.getTotal());
            pageInfo.setTotal(pi.getPages());
            pageInfo.setRows(pi.getList());
        }

        return pageInfo;
    }

    /*
     * @Description:  处理成分页对象
     * @Author Neo Lin
     * @param  [list]
     * @return  com.tl.bjts.dzfp.model.PageInfo
     * @Date  2018/4/11
     */
    public static <T extends PageInfo> T dealPageInfoByType(List list, T t) {
        Page page = (Page) list;
        if(list == null || list.isEmpty()){
            t.setPage(1);
            t.setRecords(0);
            t.setTotal(0);
            t.setCount(0L);
            t.setRows(new ArrayList());
        }else{
            com.github.pagehelper.PageInfo pi = page.toPageInfo();
            t.setPage(pi.getPageNum());
            t.setRecords(pi.getSize());
            t.setCount(pi.getTotal());
            t.setTotal(pi.getPages());
            t.setRows(pi.getList());
        }
        return t;
    }




    /*
     * @Description: 将输入流序列化成对象，并校验参数
     * @Author Neo Lin
     * @param  [in, t]
     * @return  void
     * @Date  2018/4/11
     */
    public <T> T getAndCheckParam(InputStream in, Class<T> clazz) throws IOException {
        String reqStr = readStreamString(in);
        T t = GsonUtils.getDefaultGson().fromJson(reqStr, clazz);
        AnnoUtil.checkParam(t);
        return t;
    }

    public <T> T getAndCheckParam(InputStream in, Type typeOfT) throws IOException {
        String reqStr = readStreamString(in);
        T t = GsonUtils.getDefaultGson().fromJson(reqStr, typeOfT);
        AnnoUtil.checkParam(t);
        return t;
    }

    public <T> T getAndCheckParam(String json, Type typeOfT) {
        T t = GsonUtils.getDefaultGson().fromJson(json, typeOfT);
        AnnoUtil.checkParam(t);
        return t;
    }

    public <T> T getParam(String json, Type typeOfT) {
        return GsonUtils.getDefaultGson().fromJson(json, typeOfT);
    }

    public <T> T getParam(InputStream in, Type typeOfT) throws IOException {
        String reqStr = readStreamString(in);
        return GsonUtils.getDefaultGson().fromJson(reqStr, typeOfT);
    }

    public <T> T getParam(InputStream in, Class<T> clazz) throws IOException {
        String reqStr = readStreamString(in);
        return GsonUtils.getDefaultGson().fromJson(reqStr, clazz);
    }

    private String readStreamString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        int l;
        while((l = inputStream.read(buf)) > 0) {
            baos.write(buf, 0, l);
        }

        byte[] bytes = baos.toByteArray();
        String respStr = new String(bytes, "utf-8");
        return respStr;
    }



}
