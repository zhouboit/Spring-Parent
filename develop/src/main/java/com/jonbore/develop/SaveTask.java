package com.jonbore.develop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jonbore.develop.util.HttpClient;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bo.zhou
 * @date 2021/3/4 下午12:39
 */
public class SaveTask {
    private static String token = "BB35B0C0BC194CD289A7FE6B59EC05C9";
    private static String baseUrl = "http://192.168.80.45:8088/devOps";
    private static String projectId = "540FF4AB41DE49AABA642BA4B1AF6628";
    private static String sprintCode = "1-YF2012004-Sprint-4";
    private static String version = "V1.2.1";
    private static String plan = "第四轮迭代";
    private static String sheetName = "2021年第四轮迭代";
    private static final Map<String, String> importUser = Maps.newHashMap();
    private static final Map<String, String> importEpic = Maps.newHashMap();

    public static void main(String[] args) {
        List<Map<String, Object>> taskFromExcel = getTaskFromExcel("D:\\DingTalk\\doc\\BP\\@业务中台-任务分解20200512-至今-本地.xlsx");
        HttpClient instance = HttpClient.instance(baseUrl);
        String method = "/projectsprinttask/creating";
        JSONObject header = new JSONObject();
        header.put("Content-Type", "application/json");
        header.put("IOVTOKEN", token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        jsonObject.put("planId", getPlanId(plan));
        jsonObject.put("version", getVersionId(version));
        jsonObject.put("resolveVersion", getVersionId(version));
        jsonObject.put("projectSprintId", getSprintId(sprintCode));
        jsonObject.put("sprintId", getSprintId(sprintCode));
        jsonObject.put("operatorshow", "0");
        jsonObject.put("code", sprintCode);
        jsonObject.put("type", "0");
        jsonObject.put("level", "1");
        jsonObject.put("status", "1");
        for (Map<String, Object> stringObjectMap : taskFromExcel) {
            if (stringObjectMap == null
                    || stringObjectMap.get("column1") == null
                    || stringObjectMap.get("column1").toString().isEmpty()
                    || stringObjectMap.get("column2") == null
                    || stringObjectMap.get("column2").toString().isEmpty()
                    || stringObjectMap.get("column3") == null
                    || stringObjectMap.get("column3").toString().isEmpty()
                    || stringObjectMap.get("column4") == null
                    || stringObjectMap.get("column4").toString().isEmpty()
                    || stringObjectMap.get("column6") == null
                    || stringObjectMap.get("column6").toString().isEmpty()
                    || stringObjectMap.get("column7") == null
                    || stringObjectMap.get("column7").toString().isEmpty()
            ) {
                continue;
            }
            double h = Double.parseDouble(stringObjectMap.get("column6").toString()) * 8;
            if (stringObjectMap.get("column8") != null && !stringObjectMap.get("column8").toString().isEmpty()) {
                h = h / 2;
            }
            jsonObject.put("projectEpicId", getEpicId(stringObjectMap.get("column1").toString()));
            jsonObject.put("taskUser", getUserId(stringObjectMap.get("column7").toString()));
            jsonObject.put("remarks", "验收标准:<br>" + stringObjectMap.get("column4").toString());
            jsonObject.put("name", stringObjectMap.get("column2") + "-" + stringObjectMap.get("column3"));
            jsonObject.put("pannedWorkingHours", h);
            jsonObject.put("resolveBeginTime", new Date());
            jsonObject.put("expectedCompletionTime", new Date(new Date().getTime() + 1000 * 60 * 60 * 24 * 15));
            System.out.println(JSON.toJSONString(jsonObject));
            System.out.println(instance.sendPost(method, header, jsonObject));
            if (stringObjectMap.get("column8") != null && !stringObjectMap.get("column8").toString().isEmpty()) {
                jsonObject.put("taskUser", getUserId(stringObjectMap.get("column8").toString()));
                System.out.println(instance.sendPost(method, header, jsonObject));
            }
        }

    }

    private static List<Map<String, Object>> getTaskFromExcel(String filePath) {
        List<Map<String, Object>> result = null;
        try (InputStream inputStream = new FileInputStream(filePath)) {
            return read(filePath.substring(filePath.lastIndexOf(".") + 1), 0, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getSprintId(String name) {
        String userId;
        HttpClient instance = HttpClient.instance(baseUrl);
        String method = "/projectsprint/byCondition";
        JSONObject header = new JSONObject();
        header.put("Content-Type", "application/json");
        header.put("IOVTOKEN", token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectId", projectId);
        jsonObject.put("name", name);
        JSONObject sprintObj = JSON.parseObject(instance.sendPost(method, header, jsonObject));
        if (sprintObj != null
                && sprintObj.containsKey("status")
                && sprintObj.get("status") != null
                && sprintObj.get("status").toString().equals("success")
                && sprintObj.containsKey("content")
                && sprintObj.get("content") != null) {
            JSONArray data = (JSONArray) sprintObj.get("content");
            if (!data.isEmpty()) {
                JSONObject userData = (JSONObject) data.get(0);
                if (userData != null && userData.containsKey("id") && userData.get("id") != null) {
                    userId = userData.get("id").toString().isEmpty() ? null : userData.get("id").toString();
                    if (userId != null) {
                        return userId;
                    }
                }
            }
        }
        return null;
    }

    private static String getVersionId(String name) {
        String userId;
        HttpClient instance = HttpClient.instance(baseUrl);
        String method = "/projectappversion/byCondition";
        JSONObject header = new JSONObject();
        header.put("Content-Type", "application/json");
        header.put("IOVTOKEN", token);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", version);
        jsonObject.put("projectId", projectId);
        JSONObject vesionObj = JSON.parseObject(instance.sendPost(method, header, jsonObject));
        if (vesionObj != null
                && vesionObj.containsKey("status")
                && vesionObj.get("status") != null
                && vesionObj.get("status").toString().equals("success")
                && vesionObj.containsKey("content")
                && vesionObj.get("content") != null) {
            JSONArray data = (JSONArray) vesionObj.get("content");
            if (!data.isEmpty()) {
                JSONObject userData = (JSONObject) data.get(0);
                if (userData != null && userData.containsKey("id") && userData.get("id") != null) {
                    userId = userData.get("id").toString().isEmpty() ? null : userData.get("id").toString();
                    if (userId != null) {
                        return userId;
                    }
                }
            }
        }
        return null;
    }

    private static String getPlanId(String name) {
        String userId;
        HttpClient instance = HttpClient.instance(baseUrl);
        String method = "/projectPlan/byCondition";
        JSONObject header = new JSONObject();
        header.put("Content-Type", "application/json");
        header.put("IOVTOKEN", token);
        JSONObject jsonObject = JSON.parseObject("{\n" +
                "    \"name\":\"" + name + "\"\n" +
                "}");
        JSONObject planObj = JSON.parseObject(instance.sendPost(method, header, jsonObject));
        if (planObj != null
                && planObj.containsKey("status")
                && planObj.get("status") != null
                && planObj.get("status").toString().equals("success")
                && planObj.containsKey("content")
                && planObj.get("content") != null) {
            JSONArray data = (JSONArray) planObj.get("content");
            if (!data.isEmpty()) {
                JSONObject userData = (JSONObject) data.get(0);
                if (userData != null && userData.containsKey("id") && userData.get("id") != null) {
                    userId = userData.get("id").toString().isEmpty() ? null : userData.get("id").toString();
                    if (userId != null) {
                        return userId;
                    }
                }
            }
        }
        return null;
    }

    private static String getUserId(String name) {
        String userId = null;
        //http://192.168.80.45:8088/devOps/sysuser/queryUserByDepartmentId
        if (importUser.containsKey(name)) {
            userId = importUser.get(name);
        }
        HttpClient instance = HttpClient.instance(baseUrl);
        String method = "/sysuser/queryUserByDepartmentId";
        JSONObject header = new JSONObject();
        header.put("Content-Type", "application/json");
        header.put("IOVTOKEN", token);
        JSONObject jsonObject = JSON.parseObject("{\n" +
                "    \"condition\": {\n" +
                "        \"search\": \"" + name + "\"\n" +
                "    },\n" +
                "    \"pageSize\": 1,\n" +
                "    \"pageNum\": 1\n" +
                "}");
        JSONObject user = JSON.parseObject(instance.sendPost(method, header, jsonObject));
        if (user != null
                && user.containsKey("status")
                && user.get("status") != null
                && user.get("status").toString().equals("success")
                && user.containsKey("content")
                && user.get("content") != null) {
            JSONObject content = (JSONObject) user.get("content");
            if (content.containsKey("rows") && content.get("rows") != null) {
                JSONArray data = (JSONArray) content.get("rows");
                if (!data.isEmpty()) {
                    JSONObject userData = (JSONObject) data.get(0);
                    if (userData != null && userData.containsKey("id") && userData.get("id") != null) {
                        userId = userData.get("id").toString().isEmpty() ? null : userData.get("id").toString();
                        if (userId != null) {
                            importUser.put(name, userId);
                        }
                    }
                }
            }
        }
        return userId;
    }

    private static String getEpicId(String name) {
        String userId = null;
        if (importEpic.containsKey(name)) {
            userId = importEpic.get(name);
        }
        HttpClient instance = HttpClient.instance(baseUrl);
        String method = "/projectepic/byCondition";
        JSONObject header = new JSONObject();
        header.put("Content-Type", "application/json");
        header.put("IOVTOKEN", token);
        JSONObject jsonObject = JSON.parseObject("{\n" +
                "    \"projectId\": \"" + projectId + "\",\n" +
                "    \"name\":\"" + name + "\"\n" +
                "}");
        JSONObject epic = JSON.parseObject(instance.sendPost(method, header, jsonObject));
        if (epic != null
                && epic.containsKey("status")
                && epic.get("status") != null
                && epic.get("status").toString().equals("success")
                && epic.containsKey("content")
                && epic.get("content") != null) {
            JSONArray data = (JSONArray) epic.get("content");
            if (!data.isEmpty()) {
                JSONObject userData = (JSONObject) data.get(0);
                if (userData != null && userData.containsKey("id") && userData.get("id") != null) {
                    userId = userData.get("id").toString().isEmpty() ? null : userData.get("id").toString();
                    if (userId != null) {
                        importEpic.put(name, userId);
                    }
                }
            }
        }
        return userId;
    }

    //{"column1":"需求","column0":"序号","column5":"时间","column4":"责任人","column3":"验收标准","column2":"任务描述","column6":"工时"}
    private static List<Map<String, Object>> read(String fileExt, int sheetIndex, InputStream is) throws Exception {
        try (Workbook xssfWorkbook = "xlsx".equalsIgnoreCase(fileExt) ? new XSSFWorkbook(is) : new HSSFWorkbook(is)) {
            List<Map<String, Object>> ret = Lists.newArrayList();
            Sheet sheet = xssfWorkbook.getSheet(sheetName);
            if (sheet != null) {
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    readRow(ret, sheet.getRow(i));
                }
            }

            return ret;
        } catch (Exception e) {
            throw e;
        }
    }

    private static void readRow(List<Map<String, Object>> ret, Row row) {
        if (row != null) {
            int columnCount = row.getLastCellNum();
            Map<String, Object> record = Maps.newHashMap();
            for (int j = 0; j < columnCount; j++) {
                record.put("column" + j, readCell(row.getCell(j)));
            }
            ret.add(record);
        }
    }

    private static String readCell(Cell cell) {
        String value = "";
        if (cell == null) {
            return value;
        }
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date d = cell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    value = sdf.format(d);
                } else {
                    DecimalFormat df = new DecimalFormat("0.00");
                    value = df.format(cell.getNumericCellValue());
                }
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue() + "";
                break;
            case FORMULA:
                value = cell.getCellFormula() + "";
                break;
            case BLANK:
                value = "";
                break;
            case ERROR:
                value = "error";
                break;
            default:
                value = "unknown type";
                break;
        }
        return value;
    }


}
