package com.tl.common.ext.utils;

import com.tl.common.ext.annotation.ExcelRootTitle;
import com.tl.common.ext.annotation.ExcelSetting;
import com.tl.common.ext.exception.TlBusinessException;
import com.tl.common.ext.model.Person;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

public class PoiUtils {


    public static void createExcelSingleSheet(OutputStream os, String sheetName, List mapContent, Class clazz, List<String> excludeNames, boolean useOrder) throws Exception {
        List<SheetModel> sheetModels = new LinkedList<SheetModel>();
        SheetModel s = new SheetModel();
        s.setSheetName(sheetName);
        Map<Integer, Dpyz> columnsOrder;
        if (useOrder) {
            columnsOrder = getColumnsOrder(clazz, excludeNames);
        } else {
            columnsOrder = getColumnsOrderDirect(clazz, excludeNames);
        }
        s.setTitle(getKeyMap(columnsOrder));
        s.setContent(mapContent);
        s.setWidths(getwidths(columnsOrder));
        s.setClazz(clazz);
        sheetModels.add(s);
        createExcel(os, sheetModels);
    }

    public static void createExcelSingleSheetWithMultiTitle(OutputStream os, String sheetName, List mapContent, Class clazz) throws Exception {
        List<SheetModel> sheetModels = new LinkedList<SheetModel>();
        SheetModel s = new SheetModel();
        s.setSheetName(sheetName);
        Map<Integer, Dpyz> columnsOrder;
        columnsOrder = getColumnsOrder(clazz, null);
        TitleTreeNode multiTitle = getMultiTitle(clazz);
        setMultiTitleIndex(multiTitle);
        s.setMultiTitle(multiTitle);
        s.setTitle(getKeyMap(columnsOrder));
        s.setContent(mapContent);
        s.setWidths(getwidths(columnsOrder));
        s.setClazz(clazz);
        sheetModels.add(s);
        createExcel(os, sheetModels);
    }


    public static void createExcelSingleSheet(OutputStream os, String sheetName, List mapContent, Class clazz, List<String> excludeNames) throws Exception {
        createExcelSingleSheet(os, sheetName, mapContent, clazz, excludeNames, true);
    }

    public static void createExcelSingleSheet(OutputStream os, String sheetName, List mapContent, Class clazz) throws Exception {
        createExcelSingleSheet(os, sheetName, mapContent, clazz, null, true);
    }

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";

    public static class SheetModel {

        private String sheetName;

        private LinkedHashMap<String, String> title;   //表头对应关系

        private List content;

        private Integer[] widths;

        private Class clazz;

        private TitleTreeNode multiTitle;

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public LinkedHashMap<String, String> getTitle() {
            return title;
        }

        public void setTitle(LinkedHashMap<String, String> title) {
            this.title = title;
        }

        public List getContent() {
            return content;
        }

        public void setContent(List content) {
            this.content = content;
        }

        public Integer[] getWidths() {
            return widths;
        }

        public void setWidths(Integer[] widths) {
            this.widths = widths;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public TitleTreeNode getMultiTitle() {
            return multiTitle;
        }

        public void setMultiTitle(TitleTreeNode multiTitle) {
            this.multiTitle = multiTitle;
        }
    }

    public static void createExcel(OutputStream os, List<SheetModel> sheetModels) throws Exception {

        //创建HSSFWorkbook对象(excel的文档对象)
        HSSFWorkbook wb = new HSSFWorkbook();

        int sheetNum = sheetModels.size();
        for (int st = 0; st < sheetNum; st++) {
            SheetModel sheetModel = sheetModels.get(st);
            //建立新的sheet对象（excel的表单）
            HSSFSheet sheet = wb.createSheet(sheetModel.getSheetName());
            sheet.setDefaultRowHeightInPoints(18);//设置缺省列高
            sheet.setDefaultColumnWidth(20);//设置缺省列宽

            List<?> listContent = sheetModel.getContent();
            Integer[] columns_widths = sheetModel.getWidths();
            LinkedHashMap<String, String> titleMap = sheetModel.getTitle();
            Iterator<Map.Entry<String, String>> titleIter = titleMap.entrySet().iterator();

            //创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //横向居中
            cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中

            HSSFFont fontStyle = wb.createFont();
            fontStyle.setFontName("宋体");
            fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(fontStyle);
            Integer childDepth = 0;
            Integer childLeafNum = 0;
            TitleTreeNode multiTitle = sheetModel.getMultiTitle();

            Integer titleRowSum = 0;
            if (multiTitle !=null && !CollectionUtils.isEmpty(multiTitle.getChildNodes())) {            //生成多级表头  //先赋值再合并单元格
                List<TitleTreeNode> childNodes = multiTitle.getChildNodes();
                childDepth = multiTitle.getChildDepth();  //标题的行数
                childLeafNum = multiTitle.getChildLeafNum();//标题的列数
                for (int i = 0; i < childDepth; i++) { //生成标题行
                    HSSFRow titleRow = sheet.createRow(i);
                    //生成标题列
                    for (int j = 0; j < childLeafNum; j++) {
                        HSSFCell titleCell = titleRow.createCell(j);
                        titleCell.setCellStyle(cellStyle);
                        if (i == 0) {
                            sheet.setColumnWidth(titleCell.getColumnIndex(), 256 * columns_widths[j]);
                        }
                    }
                }
                //设置标题并且合并单元格
                setTitleAndMerge(childNodes, sheet);
                titleRowSum = childDepth;
            } else { //单行表头的情况
                titleRowSum = 1;
                int titleIndex = 0;
                //在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
                HSSFRow titleRow = sheet.createRow(0);
                while (titleIter.hasNext()) {
                    Map.Entry<String, String> entry = titleIter.next();
                    //设置指定列的列宽，256 * 50这种写法是因为width参数单位是单个字符的256分之一
                    HSSFCell titleCell = titleRow.createCell(titleIndex);
                    titleCell.setCellStyle(cellStyle);
                    sheet.setColumnWidth(titleCell.getColumnIndex(), 256 * columns_widths[titleIndex]);
                    titleCell.setCellValue(entry.getValue());
                    titleIndex++;
                }
            }


            // 设置正文内容
            for (int i = 0; i < listContent.size(); i++) {
                Iterator<?> contentIter = titleMap.entrySet().iterator();
                int colIndex = 0;
                HSSFRow contentRow = sheet.createRow(i + titleRowSum);
                while (contentIter.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) contentIter
                            .next();
                    String key = entry.getKey();
                    Field field = getField(listContent.get(i), key);

                    Object obj = listContent.get(i);
                    Object content = TlBeanUtils.getter(obj, field.getName());
                    String value = "";
                    if (null != content) {
                        if (content instanceof Date) {
                            ExcelSetting excelSetting = field.getAnnotation(ExcelSetting.class);
                            String pattern = DATE_FORMAT_PATTERN;
                            if (excelSetting != null) {
                                pattern = excelSetting.datePattern();
                            }
                            value = TlDateUtils.format((Date) content, pattern);
                        } else {
                            value = content.toString();
                        }
                    }

                    HSSFCell contentCell = contentRow.createCell(colIndex);

                    contentCell.setCellValue(value);
                    colIndex++;
                }

            }

        }
        //输出Excel文件
        wb.write(os);
    }

    private static void setTitleAndMerge(List<TitleTreeNode> childNodes, HSSFSheet sheet) {
        //赋值并合并
        for (TitleTreeNode treeNode : childNodes) {
            Integer[] indexStartMap = treeNode.getIndexStartMap();
            Integer[] indexEndMap = treeNode.getIndexEndMap();
            Integer colNum = indexStartMap[0];//列号
            Integer rowNum = indexStartMap[1];//行号
            Integer endColNum = indexEndMap[0];//列号
            Integer endRowNum = indexEndMap[1];//行号
            HSSFRow row = sheet.getRow(rowNum);
            HSSFCell cell = row.getCell(colNum);
            cell.setCellValue(treeNode.getColName());
            if (colNum != endColNum || rowNum != endRowNum) { //合并
                CellRangeAddress region = new CellRangeAddress(rowNum, endRowNum, colNum, endColNum);
                sheet.addMergedRegion(region);
            }
            List<TitleTreeNode> treeNodeChildNodes = treeNode.getChildNodes();
            if (!CollectionUtils.isEmpty(treeNodeChildNodes)) {//递归
                setTitleAndMerge(treeNodeChildNodes, sheet);
            }
        }
    }

    //获取表头
    public static LinkedHashMap<String, String> getKeyMap(Map<Integer, Dpyz> columnsOrder) {
        Iterator<Map.Entry<Integer, Dpyz>> iterator = columnsOrder.entrySet().iterator();
        LinkedHashMap<String, String> rtn = new LinkedHashMap<String, String>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Dpyz> next = iterator.next();
            String colTitleName = next.getValue().excelSetting.colTitleName();
            String name = next.getValue().getName();
            rtn.put(name, colTitleName);
        }
        return rtn;
    }

    public static class TitleTreeNode {

        public TitleTreeNode() {
            this.childNodes = new ArrayList<>();
            this.childLeafNum = 0;
            this.childDepth = 0;
        }

        private String name;  //
        private String colName;
        private Integer childLeafNum; //子树叶子节点数量
        private Integer childDepth; //子树的深度
        private Integer[] indexStartMap; //当前节点的位置起始位置
        private Integer[] indexEndMap; //当前节点的位置结束位置

        private List<TitleTreeNode> childNodes;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getChildLeafNum() {
            return childLeafNum;
        }

        public void setChildLeafNum(Integer childLeafNum) {
            this.childLeafNum = childLeafNum;
        }

        public Integer getChildDepth() {
            return childDepth;
        }

        public void setChildDepth(Integer childDepth) {
            this.childDepth = childDepth;
        }

        public List<TitleTreeNode> getChildNodes() {
            return childNodes;
        }

        public void setChildNodes(List<TitleTreeNode> childNodes) {
            this.childNodes = childNodes;
        }

        public String getColName() {
            return colName;
        }

        public void setColName(String colName) {
            this.colName = colName;
        }

        public Integer[] getIndexStartMap() {
            return indexStartMap;
        }

        public void setIndexStartMap(Integer[] indexStartMap) {
            this.indexStartMap = indexStartMap;
        }

        public Integer[] getIndexEndMap() {
            return indexEndMap;
        }

        public void setIndexEndMap(Integer[] indexEndMap) {
            this.indexEndMap = indexEndMap;
        }

    }

    //获取多级表头
    public static TitleTreeNode getMultiTitle(Class<?> clazz) {
        ExcelRootTitle excelRootTitle = clazz.getAnnotation(ExcelRootTitle.class);
        String[] rootTitles = excelRootTitle.rootTitles();
        if (rootTitles == null) {
            return null;
        }
        Map<String, LinkedHashMap> titleTree = new HashMap<>();
        TitleTreeNode node = new TitleTreeNode();
        return getNode("root", rootTitles, clazz);
    }

    public static void setMultiTitleIndex(TitleTreeNode node){
        Integer childLeafNum = node.getChildLeafNum();
        Integer childDepth = node.getChildDepth();
        List<TitleTreeNode> childNodes = node.getChildNodes();
        setMultiTileIndex(childNodes, 0, 0,childLeafNum, childDepth);

    }
    public static void setMultiTileIndex(List<TitleTreeNode> nodes, Integer startIndex, Integer depth,Integer parengChildLeaf,Integer parengChildDepth){
        for(TitleTreeNode node : nodes){
            Integer childLeafNum = node.getChildLeafNum();
            Integer childDepth = node.getChildDepth();
            node.setIndexStartMap(new Integer[]{startIndex,depth});

            List<TitleTreeNode> childNodes = node.getChildNodes();
            if(!CollectionUtils.isEmpty(childNodes)){
                setMultiTileIndex(childNodes, startIndex, depth+1,childLeafNum, childDepth);
            }
            if(childLeafNum == 0) {
                node.setIndexEndMap(new Integer[]{startIndex, depth + parengChildDepth - childDepth - 1}); //加上子树叶子节点-1  当前深度 加上上层子树深度减去本层子树深度 -1
                startIndex += 1;
            }else {
                node.setIndexEndMap(new Integer[]{startIndex + childLeafNum - 1, depth + parengChildDepth - childDepth - 1}); //加上子树叶子节点-1  当前深度 加上上层子树深度减去本层子树深度 -1
                startIndex += childLeafNum;
            }

        }
    }

    public static TitleTreeNode getNode(String name, String[] childTitles, Class clazz) {
        TitleTreeNode node = new TitleTreeNode();
        node.setName(name);
        System.out.println("遍历当前节点"+name);
        int childLeafNum = 0; //叶子节点
        int childDepth = 0; //深度

        List<TitleTreeNode> childs = node.getChildNodes();
        List<String> titleList = Arrays.asList(childTitles);
        for (String rootTitle : titleList) {
            TitleTreeNode childNode = new TitleTreeNode();
            childs.add(childNode);
            childNode.setName(rootTitle);

            //获取标题上的注解判断是否为根标题
            Field field = null;
            try {
                field = clazz.getDeclaredField(rootTitle);
            } catch (NoSuchFieldException e) {
                continue;
            }
            ExcelSetting excelSetting = field.getAnnotation(ExcelSetting.class);
            childNode.setColName(excelSetting.colTitleName());
            boolean fatherTitle = excelSetting.isFatherTitle();
            if (fatherTitle) {//有子标题(非叶子节点)
                TitleTreeNode bbbb = getNode(rootTitle, excelSetting.childTitles(), clazz);
                Integer leaf = bbbb.getChildLeafNum();
                Integer depth = bbbb.getChildDepth();
                childNode.setChildLeafNum(leaf);
                childNode.setChildDepth(depth);
                childLeafNum += leaf;
                if (depth > childDepth) { //取最大的深度
                    childDepth = depth;
                }
                childNode.setChildNodes(bbbb.getChildNodes());
            } else {//叶子节点
                childLeafNum++; //叶子节点+1
            }
        }
        childDepth +=1;
        node.setChildDepth(childDepth);
        node.setChildLeafNum(childLeafNum);
        return node;
    }


    //获取表格宽度
    public static Integer[] getwidths(Map<Integer, Dpyz> columnsOrder) {
        Iterator<Map.Entry<Integer, Dpyz>> iterator = columnsOrder.entrySet().iterator();
        List<Integer> widths = new LinkedList<Integer>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Dpyz> next = iterator.next();
            int width = next.getValue().excelSetting.width();
            widths.add(width);
        }
        return widths.toArray(new Integer[]{});
    }
//
//    public static void main(String[] args) throws Exception {
//
//        List<Person> ps = new LinkedList<>();
//        ps.add(new Person("皮卡","屁屁","豆豆","100"));
//        ps.add(new Person("小志","王治郅","纸质","22"));
//        ps.add(new Person("小高","嘎啊","史蒂夫","18"));
//        ps.add(new Person("阿喵","喵喵","阿喵","66"));
//        ps.add(new Person("阿喵","喵喵","阿喵","66"));
//
//
//        File file = new File("E:\\javaio\\multi.xls");
//        FileOutputStream out = new FileOutputStream(file);
//        createExcelSingleSheetWithMultiTitle(out,"sheet",ps,Person.class);
//        System.out.println(1);
//
////        File file = new File("E:\\javaio\\hahah.xls");
////        FileInputStream fis = new FileInputStream(file);
////        List<Person> people = readExcel(fis, "hahah.xls", Person.class, false);
////        System.out.println(1);
//
//    }

    public static Map<Integer, Dpyz> getColumnsOrderDirect(Class clazz, List<String> excludeNames) {
        Map<Integer, Dpyz> indexMap = new LinkedHashMap<Integer, Dpyz>();
        Field[] fields = TlBeanUtils.getAllFields(clazz);
        int index = 1;
        for (Field field : fields) {
            String fieldName = field.getName();
            if (excludeNames != null && excludeNames.contains(fieldName)) {
                continue;
            }
            ExcelSetting excelSetting = field.getAnnotation(ExcelSetting.class);
            if (excelSetting == null) {
                continue;
            }
            indexMap.put(index++, new Dpyz(fieldName, excelSetting.colTitleName(), field.getType(), excelSetting));
        }
        return indexMap;
    }


    public static Map<Integer, Dpyz> getColumnsOrder(Class clazz, List<String> excludeNames) {
        Map<Integer, Dpyz> indexMap = new LinkedHashMap<Integer, Dpyz>();
        Field[] fields = TlBeanUtils.getAllFields(clazz);
        String firstColumn = "";
        Map<String, Dpyz> itemLinkedMap = new HashMap<String, Dpyz>();
        for (Field field : fields) {

            String fieldName = field.getName();
            if (excludeNames != null && excludeNames.contains(fieldName)) {
                continue;
            }
            ExcelSetting excelSetting = field.getAnnotation(ExcelSetting.class);
            if (excelSetting == null || excelSetting.isFatherTitle()) { //是腹肌标签不参与
                continue;
            }
            boolean first = excelSetting.isFirst();
            String colTitleName = excelSetting.colTitleName();
            String next = excelSetting.nextColName();
            if (first) {
                firstColumn = fieldName;
            }
            itemLinkedMap.put(fieldName, new Dpyz(next, colTitleName, field.getType(), excelSetting));
        }
        String next = firstColumn;
        boolean gogogo = true;
        int ii = 0;
        while (gogogo) {
            Dpyz dpyz = itemLinkedMap.get(next);
            if (dpyz == null) {
                gogogo = false;
                continue;
            }
            indexMap.put(ii++, new Dpyz(next, dpyz.getTitleName(), dpyz.getClazz(), dpyz.getExcelSetting()));
            next = dpyz.getName();
        }
        return indexMap;
    }

    public static class Dpyz {
        public Dpyz(String name, String titleName, Class clazz, ExcelSetting excelSetting) {
            this.name = name;
            this.titleName = titleName;
            this.clazz = clazz;
            this.excelSetting = excelSetting;
        }

        private String name;

        private String titleName;

        private Class clazz;

        private ExcelSetting excelSetting;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public ExcelSetting getExcelSetting() {
            return excelSetting;
        }

        public void setExcelSetting(ExcelSetting excelSetting) {
            this.excelSetting = excelSetting;
        }

        public String getTitleName() {
            return titleName;
        }

        public void setTitleName(String titleName) {
            this.titleName = titleName;
        }
    }


    public static <T> List<T> readExcel(InputStream is, String fileName, Class<T> clazz, boolean randomCol) throws Exception {
        Workbook hssfWorkbook = null;
        if (fileName.endsWith("xlsx")) {
            hssfWorkbook = new XSSFWorkbook(is);//Excel 2007
        } else if (fileName.endsWith("xls")) {
            hssfWorkbook = new HSSFWorkbook(is);//Excel 2003
        }

        Map<Integer, Dpyz> indexMap = new HashMap<Integer, Dpyz>();
        Map<String, Dpyz> titleMap = new HashMap<String, Dpyz>();

        Field[] fields = TlBeanUtils.getAllFields(clazz);
        if (randomCol) {
            for (Field field : fields) {
                String fieldName = field.getName();

                ExcelSetting excelSetting = field.getAnnotation(ExcelSetting.class);
                if (excelSetting == null) {
                    continue;
                }
                String colName = excelSetting.colTitleName();
                titleMap.put(colName, new Dpyz(fieldName, colName, field.getType(), excelSetting));
            }
        } else {
            indexMap = getColumnsOrder(clazz, null);
        }
        T t = null;
        List<T> list = new ArrayList<>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            if (randomCol) {
                Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                Row titleRow = hssfSheet.getRow(0); //获取第一行标题数据
                indexMap = new HashMap<Integer, Dpyz>();
                for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                    Cell titleCell = titleRow.getCell(i);
                    if (titleCell == null) {
                        continue;
                    }
                    String titleValue = titleCell.getStringCellValue();
                    Dpyz dpyz = titleMap.get(titleValue);
                    if (dpyz != null) {
                        indexMap.put(i, dpyz);
                    }
                }
            }
            Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            String sheetName = hssfSheet.getSheetName();
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                Row hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    Iterator<Map.Entry<Integer, Dpyz>> iterator = indexMap.entrySet().iterator();
                    Cell cell;
                    t = clazz.newInstance();
                    while (iterator.hasNext()) {
                        Integer index = 0;
                        try {
                            Map.Entry<Integer, Dpyz> next = iterator.next();
                            index = next.getKey();
                            Dpyz value = next.getValue();
                            String fieldName = value.getName();
                            Class aClass = value.getClazz();
                            cell = hssfRow.getCell(index);
                            if (cell == null) {
                                continue;
                            }
                            int cellType = cell.getCellType();
                            String cellStr = "";
                            if (Cell.CELL_TYPE_NUMERIC == cellType) {
                                DecimalFormat df = new DecimalFormat("#");
                                cellStr = df.format(cell.getNumericCellValue());
                            }
                            if (Cell.CELL_TYPE_STRING == cellType) {
                                cellStr = cell.getStringCellValue();
                            }
                            Object cellValue = null;
                            if (aClass == String.class) {
                                cellValue = cellStr;
                            } else if (aClass == Date.class) {
                                String dateValue = cellStr;
                                cellValue = TlDateUtils.parseString2Date(dateValue, value.getExcelSetting().datePattern());
                            } else if (aClass == BigDecimal.class) {
                                String de = cellStr;
                                if (de == null || "".equals(de)) {
                                    cellValue = BigDecimal.ZERO;
                                } else {
                                    cellValue = new BigDecimal(cellStr);
                                }
                            } else if (aClass == Integer.class) {
                                cellValue = Integer.valueOf(cellStr);
                            } else if (aClass == Long.class) {
                                cellValue = Long.valueOf(cellStr);
                            } else if (aClass == Short.class) {
                                cellValue = Short.valueOf(cellStr);
                            } else if (aClass == Double.class) {
                                cellValue = Double.valueOf(cellStr);
                            } else if (aClass == Float.class) {
                                cellValue = Float.valueOf(cellStr);
                            }
                            TlBeanUtils.setter(t, fieldName, cellValue, aClass);
                        } catch (Exception e) {
                            throw new TlBusinessException(501, "EXCEL内容有误", String.format("【%s】页,第%s行,第%s列,数据有误，请检查核对", sheetName, rowNum + 1, excelColIndexToStr(index + 1), e.getMessage()));
                        }
                    }
                    try {
                        AnnoUtil.checkParam(t);
                    } catch (TlBusinessException e) {
                        throw new TlBusinessException(501, "EXCEL内容有误", String.format("【%s】页,第%s行,%s,请检查核对", sheetName, rowNum + 1, e.getSubMsg()));
                    }
                    list.add(t);
                }
            }
        }
        return list;
    }


    /**
     * 支持当前对象及直接父类属性查询
     *
     * @param propName
     * @return
     */
    private static Field getField(Object obj, String propName) throws NoSuchFieldException {
        Field field;
        try {
            field = obj.getClass().getDeclaredField(propName);
        } catch (NoSuchFieldException e) {
            field = obj.getClass().getSuperclass().getDeclaredField(propName);
        }
        return field;
    }


    /**
     * Excel column index begin 1
     *
     * @param colStr
     * @param length
     * @return
     */
    public static int excelColStrToNum(String colStr, int length) {
        int num = 0;
        int result = 0;
        for (int i = 0; i < length; i++) {
            char ch = colStr.charAt(length - i - 1);
            num = (int) (ch - 'A' + 1);
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }

    /**
     * Excel column index begin 1
     *
     * @param columnIndex
     * @return
     */
    public static String excelColIndexToStr(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }


}
