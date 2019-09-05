package com.tl.common.ext.utils;

import com.github.pagehelper.PageHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tl.common.ext.annotation.ConvertCode;
import com.tl.common.ext.exception.TlBusinessException;
import com.tl.common.ext.model.BaseListDTO;
import com.tl.common.ext.model.SimpleResult;
import com.tl.common.ext.service.DictService;
import com.tl.common.rpc.RpcAuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;


public class CommonUtils {

    private DictService dictService;

    private final String TYPE_ARRAY = "array";
    private final String TYPE_OBJECT = "object";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CommonUtils(DictService dictService) {
        this.dictService = dictService;
    }

    public CommonUtils() {
    }

    public <T extends BaseListDTO> void setPageParam(T t) {
        PageHelper.startPage(t.getPageNo(), t.getPageSize(), t.getOrderSql());
    }

    /*
     * @Description: 查询code对应的中文名称
     * @Author Neo Lin
     * @param  [code , key]
     * @return  java.lang.String
     * @Date  2018/4/24
     */
    public String getNameByCode(String code, String key) {
        if (StringUtils.isEmpty(code) || StringUtils.isEmpty(key)) {
            return "";
        }

        String s = dictService.getCachedDictByCode(code, key);
        if (!StringUtils.isEmpty(s)) {
            return s;
        }
        return code;
    }

    /*
     * @Description: 循环遍历转换code
     * @Author Neo Lin
     * @param  [list]
     * @return  void
     * @Date  2018/4/24
     */
    public void convertCode2Name(List list) {
        if (list.isEmpty()) {
            return;
        }
        for (Object obj : list) {
            convertCode2Name(obj);
        }
    }

    /*
     * @Description: 将code从字典表中查出来给mc赋值
     * @Author Neo Lin
     * @param  [obj]
     * @return  void
     * @Date  2018/4/24
     */
    public void convertCode2Name(Object obj) {
        if (obj == null)
            return;
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        ConvertCode convertCode;
        String dataFrom;
        String dtype;
        String code;
        String codeName;
        String fieldName;
        for (Field field : fields) {
            fieldName = field.getName();
            convertCode = field.getAnnotation(ConvertCode.class);
            if (convertCode == null) {
                continue;
            }
            dtype = convertCode.dtype();
            dataFrom = convertCode.dataFrom();
            if (StringUtils.isEmpty(dtype)) {
                continue;
            }
            Object getter = TlBeanUtils.getter(obj, dataFrom);
            if(getter instanceof String){
                code = (String)getter;
            }else if(getter instanceof Number){
                code = getter.toString();
            }else{
                return;
            }

            codeName = getNameByCode(code, dtype);
            TlBeanUtils.setter(obj, fieldName, codeName, String.class);
        }
    }

    public String happiness(String str, String url, RestTemplate restTemplate){
        url = RpcAuthHelper.attachUrl(url);
        String result = "";
        try {
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            HttpEntity<String> formEntity = new HttpEntity<String>(str, headers);
            result = restTemplate.postForObject(url, formEntity, String.class);
        } catch (Exception e) {
            logger.info("远程接口调发生错误，调用地址：{}，错误原因：{}", url, e.getMessage());
            logger.info("请求失败的报文：{}", str);
            throw new TlBusinessException(500, "远程接口调发生错误，错误原因：", e.getMessage());
        }
        return result;
    }

    /*
     * @Description:rpc 调用通用方法
     * @param  [param  请求的参数对象, url 请求地址, clazz 需要获取的对象]
     * @return  T
     */
    public <T> T rpc(Object param, String url, Class<T> clazz, RestTemplate restTemplate) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String str = gson.toJson(param);
        String result = happiness(str, url, restTemplate);
        SimpleResult<T> rtn = fromJsonObject(result, clazz);
        if (rtn.getCode() != 0) {
            logger.info("远程接口调用未返回正确的的结果，调用地址：{}，错误原因：{}", url, rtn.getMsg());
            logger.info("请求失败的报文：{}", str);
            throw new TlBusinessException(500, "远程接口调用未返回正确的的结果,错误原因：", rtn.getMsg());
        }
        return rtn.getData();
    }

    /*
     * @Description:rpc 调用通用方法
     * @param  [param  请求的参数对象, url 请求地址, clazz 需要获取的对象]
     * @return  T
     */
    public String rpcOrgin(String param, String url, RestTemplate restTemplate) {
        return happiness(param, url, restTemplate);
    }

    public <T> List<T> rpc4List(Object param, String url, Class<T> clazz, RestTemplate restTemplate) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String str = gson.toJson(param);
        String result = happiness(str, url, restTemplate);
        SimpleResult<List<T>>rtn = fromJsonArray(result, clazz);
        if (rtn.getCode() != 0) {
            logger.info("远程接口调用未返回正确的的结果，调用地址：{}，错误原因：{}", url, rtn.getMsg());
            logger.info("请求失败的报文：{}", str);
            throw new TlBusinessException(500, "远程接口调用未返回正确的的结果,错误原因：", rtn.getMsg());
        }
        return rtn.getData();
    }

    public <T> T rpcByType(Object param, String url, Type type, RestTemplate restTemplate) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String str = gson.toJson(param);
        String result = happiness(str, url, restTemplate);
        Type simpleType = new ParameterizedTypeImpl(SimpleResult.class, new Type[]{type});
        SimpleResult<T> rtn = GsonUtils.getDefaultGson().fromJson(result, simpleType);
        if (rtn.getCode() != 0) {
            logger.info("远程接口调用未返回正确的的结果，调用地址：{}，错误原因：{}", url, rtn.getMsg());
            logger.info("请求失败的报文：{}", str);
            throw new TlBusinessException(500, "远程接口调用未返回正确的的结果,错误原因：", rtn.getMsg());
        }
        return rtn.getData();
    }

    public <T> SimpleResult<T> fromJsonObject(String json, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(SimpleResult.class, new Class[]{clazz});
        return GsonUtils.getDefaultGson().fromJson(json, type);
    }

    public static <T> SimpleResult<List<T>> fromJsonArray(String json, Class<T> clazz) {
        // 生成List<T> 中的 List<T>
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        // 根据List<T>生成完整的Result<List<T>>
        Type type = new ParameterizedTypeImpl(SimpleResult.class, new Type[]{listType});
        return GsonUtils.getDefaultGson().fromJson(json, type);
    }





}
