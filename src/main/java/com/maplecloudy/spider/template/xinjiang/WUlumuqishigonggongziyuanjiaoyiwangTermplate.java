package com.maplecloudy.spider.template.xinjiang;

import com.maplecloudy.avro.util.AvroUtils;
import com.maplecloudy.spider.parse.AbstractTemplate;
import com.maplecloudy.spider.parse.ParseData;
import com.maplecloudy.spider.protocol.HttpParameters;
import com.maplecloudy.spider.protocol.httpmethod.HttpUtils;
import com.maplecloudy.spider.schema.Content;
import com.maplecloudy.spider.schema.CrawlDatum;
import com.maplecloudy.spider.schema.Outlink;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 乌鲁木齐市公共资源交易网 解析模板
 * <p>
 * Author yanzhen
 * Date  2020-11-07
 */
public class WUlumuqishigonggongziyuanjiaoyiwangTermplate extends AbstractTemplate {

  public WUlumuqishigonggongziyuanjiaoyiwangTermplate() throws MalformedURLException {
    // 模板名字，请以网址全程为准
    name = "乌鲁木齐市公共资源交易网";
    // 爬虫种子页面
    addSeedLink("http://zwfw.wlmq.gov.cn/ggzy/", "首页");
    // 爬虫更新的需要的链接
    addUpdateLink("http://zwfw.wlmq.gov.cn/EWB-FRONT/rest/frontAppCustomAction/getPageInfoListNew?code=001002&page=1", "列表第一页");
    addUpdateLink("http://zwfw.wlmq.gov.cn/EWB-FRONT/rest/frontAppCustomAction/getPageInfoListNew?code=001002&page=2", "列表第二页");
    // 网站所有有的链接类型，以及对应的正则
    addDict("首页", "http://zwfw.wlmq.gov.cn/ggzy/", "GET", "utf-8");
    addDict("列表第一页", "http://zwfw.wlmq.gov.cn/EWB-FRONT/rest/frontAppCustomAction/getPageInfoListNew\\?code=\\w+&page=1", "POST", "utf-8");
    addDict("列表页", "http://zwfw.wlmq.gov.cn/EWB-FRONT/rest/frontAppCustomAction/getPageInfoListNew\\?code=\\w+&page=\\d+", "GET", "utf-8");
    addDict("详情页", "http://zwfw.wlmq.gov.cn/ggzy/jyxx/\\d+/\\d+/\\d+/[0-9a-zA-Z_-]+.html", "GET", "utf-8");

  }

  public ParseData parse(Outlink url, Content content, RunMode runMode) throws IOException, JSONException {

    ParseData parseData = new ParseData();
    if (matches(url, "首页")) {
      genSHOUyeLinks(parseData.outLinks, url, content, runMode);
      genSHOUyeDatas(parseData.dataMap, url, content, runMode);
    } else if (matches(url, "列表第一页")) {
      genLIEbiaodiyiyeLinks(parseData.outLinks, url, content, runMode);
      genLIEbiaodiyiyeDatas(parseData.dataMap, url, content, runMode);
    } else if (matches(url, "列表页")) {
      genLIEbiaoyeLinks(parseData.outLinks, url, content, runMode);
      genLIEbiaoyeDatas(parseData.dataMap, url, content, runMode);
    } else if (matches(url, "详情页")) {
      genXIANGqingyeLinks(parseData.outLinks, url, content, runMode);
      genXIANGqingyeDatas(parseData.dataMap, url, content, runMode);
    }
    return parseData;
  }

  public static String getCode(String url) {
    String typeName = "";
    if (url.contains("&code=")) {
      typeName = url.split("code=")[1].split("&")[0];
    }
    return typeName;
  }

  //从[首页]提取链接
  public void genSHOUyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws IOException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String[] codes = new String[]{
        "001001",
        "001002"
    };
    for (int i = 0; i < codes.length; i++) {
      Outlink outlink = new Outlink("http://zwfw.wlmq.gov.cn/EWB-FRONT/rest/frontAppCustomAction/getPageInfoListNew?code=" + codes[i] + "&page=1", "");
      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("params", "{\"siteGuid\":\"7eb5f7f1-9041-43ad-8e13-8fcb82ea831a\",\"categoryNum\":\"" + codes[i] + "\",\"pageIndex\":1,\"pageSize\":15,\"jyStatus\":\"\",\"kw\":\"\"}")};
      String entity = new ObjectMapper().writeValueAsString(data);
      httpParameters.getMap().put("x-www-form-urlencoded", entity);
      httpParameters.setContentType(
          "application/x-www-form-urlencoded; charset=UTF-8");
      httpParameters.setMethod("http");
      outlink.addExtend(httpParameters.getMap());
      outlinks.add(outlink);
    }
  }

  //从[首页]提取数据
  public void genSHOUyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, JSONException, MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表第一页]提取链接
  public void genLIEbiaodiyiyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws IOException, JSONException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = new String(content.getContent(), "utf-8");
    JSONObject json = new JSONObject(html);
    JSONArray detail = json.getJSONObject("custom").getJSONArray("infodata");

    for (int i = 0; i < detail.length(); i++) {
      JSONObject jsonObject = detail.getJSONObject(i);
      String newUrl = "http://zwfw.wlmq.gov.cn" + jsonObject.getString("infourl");
      Outlink outlink = new Outlink(newUrl, "");
      outlinks.add(outlink);
    }
    int recordsNum = json.getJSONObject("custom").getInt("count");
    int pageNo = recordsNum % 15 == 0 ? recordsNum / 15
        : recordsNum / 15 + 1;
    for (int i = 2; i <= pageNo; i++) {
      Outlink outlink = new Outlink(url.getUrl().replace("&page=1", "&page=" + i), "");
      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("params", "{\"siteGuid\":\"7eb5f7f1-9041-43ad-8e13-8fcb82ea831a\",\"categoryNum\":\"" + getCode(url.getUrl()) + "\",\"pageIndex\":" + i + ",\"pageSize\":15,\"jyStatus\":\"\",\"kw\":\"\"}")};
      String entity = new ObjectMapper().writeValueAsString(data);
      httpParameters.getMap().put("x-www-form-urlencoded", entity);
      httpParameters.setContentType(
          "application/x-www-form-urlencoded; charset=UTF-8");
      httpParameters.setMethod("http");
      outlink.addExtend(httpParameters.getMap());
      outlinks.add(outlink);
    }
  }

  //从[列表第一页]提取数据
  public void genLIEbiaodiyiyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表页]提取链接
  public void genLIEbiaoyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, JSONException, MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = new String(content.getContent(), "utf-8");
    JSONObject json = new JSONObject(html);
    JSONArray detail = json.getJSONObject("custom").getJSONArray("infodata");
    for (int i = 0; i < detail.length(); i++) {
      JSONObject jsonObject = detail.getJSONObject(i);
      String newUrl = "http://zwfw.wlmq.gov.cn" + jsonObject.getString("infourl");
      Outlink outlink = new Outlink(newUrl, "");
      outlinks.add(outlink);
    }
  }

  //从[列表页]提取数据
  public void genLIEbiaoyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[详情页]提取链接
  public void genXIANGqingyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

  }

  //从[详情页]提取数据
  public void genXIANGqingyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

    String html = new String(content.getContent(), "utf-8");
    Document doc = Jsoup.parse(html);
    String text = doc.text();
    System.out.println(text);
    Elements contentElements = doc.select(".ewb-go");
    String contentHtml = contentElements.html();
//    System.out.println(contentHtml);

    //源地址
    dataMap.put("url", url.getUrl());
    //来源网站
    dataMap.put("web", (new URL(url.getUrl()).getHost()));
    //标题
    dataMap.put("title", doc.select(".ewb-go-h2").get(0).text());
    //副标题
    dataMap.put("bakeTitle", "");
    //内容
//    dataMap.put("content", contentHtml);
    //项目名称
//    String projectName1 = "项目名称[\\s*|:|：]*([\u4E00-\u9FA5]+)";
//    Pattern compileProName1 = Pattern.compile(projectName1);
//    Matcher matcherProName1 = compileProName1.matcher(text);
//    if (matcherProName1.find()) {
//      dataMap.put("projectname", matcherProName1.group(1));
//    }
    String projectName = "项目名称[\\s*|:|：]+(\\d*[\u4E00-\u9FA5]+[\\d|“|\"|（]*[\u4E00-\u9FA5]+[”|\"|）]*[\u4E00-\u9FA5]*)";
    Pattern compileProName = Pattern.compile(projectName);
    Matcher matcherProName = compileProName.matcher(text);
    if (matcherProName.find()) {
      dataMap.put("projectname", matcherProName.group(1));
    }
    //项目编号
    String projectNum = "项目编号[\\s*|:|：]+([\u4E00-\u9FA5]*\\w*\\-?\\w*\\-?\\w*)";
    Pattern compileProjectNum = Pattern.compile(projectNum);
    Matcher matcherProjectNum = compileProjectNum.matcher(text);
    if (matcherProjectNum.find()) {
      dataMap.put("projectnum", matcherProjectNum.group(1));
    }
    String projectNum1 = "招标编号[\\s*|:|：]+([\u4E00-\u9FA5]*\\w*\\-?\\w*\\-?\\w*)";
    Pattern compileProjectNum1 = Pattern.compile(projectNum1);
    Matcher matcherProjectNum1 = compileProjectNum1.matcher(text);
    if (matcherProjectNum1.find()) {
      dataMap.put("projectnum", matcherProjectNum1.group(1));
    }
    //标的状态
    if (dataMap.get("title").contains("磋商")) {
      dataMap.put("projectStatus", "磋商");
    } else if (dataMap.get("title").contains("谈判")) {
      dataMap.put("projectStatus", "谈判");
    } else if (dataMap.get("title").contains("询价")) {
      dataMap.put("projectStatus", "询价");
    } else if (dataMap.get("title").contains("预审")) {
      dataMap.put("projectStatus", "预审");
    } else if (dataMap.get("title").contains("公示")) {
      dataMap.put("projectStatus", "公示");
    } else if (dataMap.get("title").contains("中标")) {
      dataMap.put("projectStatus", "中标");
    } else if (dataMap.get("title").contains("成交")) {
      dataMap.put("projectStatus", "成交");
    } else if (dataMap.get("title").contains("挂牌")) {
      dataMap.put("projectStatus", "挂牌");
    } else dataMap.put("projectStatus", "招标");
    //项目所在省份
    dataMap.put("province", "江苏省");
    //项目所在市
//        String city = "行政区域 ([\u4E00-\u9FA5]+).+";
//        Pattern compileCity = Pattern.compile(city);
//        Matcher matcherCity = compileCity.matcher(text);
//        if (matcherCity.find()) {
//          bm.setCounty(matcherCity.group(1));
//        }
    //项目所在市
    dataMap.put("city", "连云港");
    //项目所在县
    dataMap.put("county", "");
    //无法区分地区时放置地区
    dataMap.put("district", "");
    //采购单位
    String purchaseUnit = "采购单位[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseUnit = Pattern.compile(purchaseUnit);
    Matcher matcherPurchaseUnit = compilePurchaseUnit.matcher(text);
    if (matcherPurchaseUnit.find()) {
      dataMap.put("purchaseUnit", matcherPurchaseUnit.group(1));
    }
    String purchaseUnit1 = "采购人信息 名 称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseUnit1 = Pattern.compile(purchaseUnit1);
    Matcher matcherPurchaseUnit1 = compilePurchaseUnit1.matcher(text);
    if (matcherPurchaseUnit1.find()) {
      dataMap.put("purchaseUnit", matcherPurchaseUnit1.group(1));
    }
    String purchaseUnit2 = "采购单位名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseUnit2 = Pattern.compile(purchaseUnit2);
    Matcher matcherPurchaseUnit2 = compilePurchaseUnit2.matcher(text);
    if (matcherPurchaseUnit2.find()) {
      dataMap.put("purchaseUnit", matcherPurchaseUnit2.group(1));
    }
    String purchaseUnit3 = "采购人[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseUnit3 = Pattern.compile(purchaseUnit3);
    Matcher matcherPurchaseUnit3 = compilePurchaseUnit3.matcher(text);
    if (matcherPurchaseUnit3.find()) {
      dataMap.put("purchaseUnit", matcherPurchaseUnit3.group(1));
    }
    String purchaseUnit4 = "采购人名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseUnit4 = Pattern.compile(purchaseUnit4);
    Matcher matcherPurchaseUnit4 = compilePurchaseUnit4.matcher(text);
    if (matcherPurchaseUnit4.find()) {
      dataMap.put("purchaseUnit", matcherPurchaseUnit4.group(1));
    }
    String purchaseUnit5 = "采购机构名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseUnit5 = Pattern.compile(purchaseUnit5);
    Matcher matcherPurchaseUnit5 = compilePurchaseUnit5.matcher(text);
    if (matcherPurchaseUnit5.find()) {
      dataMap.put("purchaseUnit", matcherPurchaseUnit5.group(1));
    }
    String purchaseUnit6 = "招标人[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseUnit6 = Pattern.compile(purchaseUnit6);
    Matcher matcherPurchaseUnit6 = compilePurchaseUnit6.matcher(text);
    if (matcherPurchaseUnit6.find()) {
      dataMap.put("purchaseUnit", matcherPurchaseUnit6.group(1));
    }
    String purchaseUnit7 = "招标人名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseUnit7 = Pattern.compile(purchaseUnit7);
    Matcher matcherPurchaseUnit7 = compilePurchaseUnit7.matcher(text);
    if (matcherPurchaseUnit7.find()) {
      dataMap.put("purchaseUnit", matcherPurchaseUnit7.group(1));
    }
    //代理机构
    String agency = "代理机构名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileAgency = Pattern.compile(agency);
    Matcher matcherAgency = compileAgency.matcher(text);
    if (matcherAgency.find()) {
      dataMap.put("agency", matcherAgency.group(1));
    }
    String agency1 = "采购代理机构信息 名 称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileAgency1 = Pattern.compile(agency1);
    Matcher matcherAgency1 = compileAgency1.matcher(text);
    if (matcherAgency1.find()) {
      dataMap.put("agency", matcherAgency1.group(1));
    }
    String agency2 = "采购代理机构全称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileAgency2 = Pattern.compile(agency2);
    Matcher matcherAgency2 = compileAgency2.matcher(text);
    if (matcherAgency2.find()) {
      dataMap.put("agency", matcherAgency2.group(1));
    }
    String agency3 = "代理机构[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileAgency3 = Pattern.compile(agency3);
    Matcher matcherAgency3 = compileAgency3.matcher(text);
    if (matcherAgency3.find()) {
      dataMap.put("agency", matcherAgency3.group(1));
    }
    //采购方式
    String purchaseMethod = "采购方式[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseMethod = Pattern.compile(purchaseMethod);
    Matcher matcherPurchaseMethod = compilePurchaseMethod.matcher(html);
    if (matcherPurchaseMethod.find()) {
      dataMap.put("purchaseMethod", matcherPurchaseMethod.group(1));
    }
    String purchaseMethod1 = "按采购方式[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compilePurchaseMethod1 = Pattern.compile(purchaseMethod1);
    Matcher matcherPurchaseMethod1 = compilePurchaseMethod1.matcher(html);
    if (matcherPurchaseMethod1.find()) {
      dataMap.put("purchaseMethod", matcherPurchaseMethod1.group(1));
    }
    //项目联系人
    String contactPerson1 = "项目联系人[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileContactPerson1 = Pattern.compile(contactPerson1);
    Matcher matcherContactPerso1 = compileContactPerson1.matcher(text);
    if (matcherContactPerso1.find()) {
      dataMap.put("contactPerson", matcherContactPerso1.group(1));
    }
    String contactPerson3 = "项目负责人[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileContactPerson3 = Pattern.compile(contactPerson3);
    Matcher matcherContactPerso3 = compileContactPerson3.matcher(text);
    if (matcherContactPerso3.find()) {
      dataMap.put("contactPerson", matcherContactPerso3.group(1));
    }
    String contactPerson2 = "联系方式[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileContactPerson2 = Pattern.compile(contactPerson2);
    Matcher matcherContactPerso2 = compileContactPerson2.matcher(text);
    if (matcherContactPerso2.find()) {
      dataMap.put("contactPerson", matcherContactPerso2.group(1));
    }
    String contactPerson = "联系人[：|:|\\s*]+([\u4E00-\u9FA5]+).+";
    Pattern compileContactPerson = Pattern.compile(contactPerson);
    Matcher matcherContactPerso = compileContactPerson.matcher(text);
    if (matcherContactPerso.find()) {
      dataMap.put("contactPerson", matcherContactPerso.group(1));
    }
    //项目联系人电话
    String contactPhone = "采购单位联系方式[\\s*|:|：]+([ |0-9|-]+).+";
    Pattern compileContactPhone = Pattern.compile(contactPhone);
    Matcher matcherContactPhone = compileContactPhone.matcher(text);
    if (matcherContactPhone.find()) {
      dataMap.put("contactphone", matcherContactPhone.group(1));
    }
    String contactPhone1 = "采购单位联系电话[\\s*|:|：]+([ |0-9|-]+).+";
    Pattern compileContactPhone1 = Pattern.compile(contactPhone1);
    Matcher matcherContactPhone1 = compileContactPhone1.matcher(text);
    if (matcherContactPhone1.find()) {
      dataMap.put("contactphone", matcherContactPhone1.group(1));
    }
    String contactPhone2 = "采购人联系方式[\\s*|:|：]+([ |0-9|-]+).+";
    Pattern compileContactPhone2 = Pattern.compile(contactPhone2);
    Matcher matcherContactPhone2 = compileContactPhone2.matcher(text);
    if (matcherContactPhone2.find()) {
      dataMap.put("contactphone", matcherContactPhone2.group(1));
    }
    String contactPhone3 = "采购人联系电话[\\s*|:|：]+([ |0-9|-]+).+";
    Pattern compileContactPhone3 = Pattern.compile(contactPhone3);
    Matcher matcherContactPhone3 = compileContactPhone3.matcher(text);
    if (matcherContactPhone3.find()) {
      dataMap.put("contactphone", matcherContactPhone3.group(1));
    }
    //项目联系人手机
    String contactMobilePhone1 = "电　话[\\s*|:|：]+([0-9]+-*[0-9]+)";
    Pattern compileContactMobilePhone1 = Pattern.compile(contactMobilePhone1);
    Matcher matcherContactMobilePhone1 = compileContactMobilePhone1.matcher(text);
    if (matcherContactMobilePhone1.find()) {
      dataMap.put("contactMobilePhone", matcherContactMobilePhone1.group(1));
    }
    String contactMobilePhone = "项目联系电话[\\s*|:|：]+([ |0-9|-]+).+";
    Pattern compileContactMobilePhone = Pattern.compile(contactMobilePhone);
    Matcher matcherContactMobilePhone = compileContactMobilePhone.matcher(text);
    if (matcherContactMobilePhone.find()) {
      dataMap.put("contactMobilePhone", matcherContactMobilePhone.group(1));
    }
    //招标类型
    if (dataMap.get("title").contains("磋商")) {
      dataMap.put("type", "采购公告");
    } else if (dataMap.get("title").contains("谈判")) {
      dataMap.put("type", "采购公告");
    } else if (dataMap.get("title").contains("询价")) {
      dataMap.put("type", "采购公告");
    } else if (dataMap.get("title").contains("预审")) {
      dataMap.put("type", "采购公告");
    } else if (dataMap.get("title").contains("公示")) {
      dataMap.put("type", "结果公告");
    } else if (dataMap.get("title").contains("中标")) {
      dataMap.put("type", "结果公告");
    } else if (dataMap.get("title").contains("成交")) {
      dataMap.put("type", "结果公告");
    } else if (dataMap.get("title").contains("挂牌")) {
      dataMap.put("type", "结果公告");
    } else dataMap.put("type", "招标公告");
    //信息来源
    dataMap.put("source", "连云港市政府采购交易平台");
    //发布时间
    String publishTime = "响应文件开启时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compilePublishTime = Pattern.compile(publishTime);
    Matcher matcherPublishTime = compilePublishTime.matcher(text);
    if (matcherPublishTime.find()) {
      dataMap.put("publishTime", matcherPublishTime.group(1));
    }
    String publishTime2 = "公告时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compilePublishTime2 = Pattern.compile(publishTime2);
    Matcher matcherPublishTime2 = compilePublishTime2.matcher(text);
    if (matcherPublishTime2.find()) {
      dataMap.put("publishTime", matcherPublishTime2.group(1));
    }
    String publishTime3 = "发布时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compilePublishTime3 = Pattern.compile(publishTime3);
    Matcher matcherPublishTime3 = compilePublishTime3.matcher(text);
    if (matcherPublishTime3.find()) {
      dataMap.put("publishTime", matcherPublishTime3.group(1));
    }
    String publishTime4 = "中标时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compilePublishTime4 = Pattern.compile(publishTime4);
    Matcher matcherPublishTime4 = compilePublishTime4.matcher(text);
    if (matcherPublishTime4.find()) {
      dataMap.put("publishTime", matcherPublishTime4.group(1));
    }
    String publishTime5 = "中标日期[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compilePublishTime5 = Pattern.compile(publishTime5);
    Matcher matcherPublishTime5 = compilePublishTime5.matcher(text);
    if (matcherPublishTime5.find()) {
      dataMap.put("publishTime", matcherPublishTime5.group(1));
    }
    String publishTime6 = "开启时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compilePublishTime6 = Pattern.compile(publishTime6);
    Matcher matcherPublishTime6 = compilePublishTime6.matcher(text);
    if (matcherPublishTime6.find()) {
      dataMap.put("publishTime", matcherPublishTime6.group(1));
    }
    //开标时间
    String bidOpeningTime = "开标时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compileBidOpeningTime = Pattern.compile(bidOpeningTime);
    Matcher matcherBidOpeningTime = compileBidOpeningTime.matcher(text);
    if (matcherBidOpeningTime.find()) {
      dataMap.put("bidOpeningTime", matcherBidOpeningTime.group(1));
    }
    String bidOpeningTime1 = "开标日期[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compileBidOpeningTime1 = Pattern.compile(bidOpeningTime1);
    Matcher matcherBidOpeningTime1 = compileBidOpeningTime1.matcher(text);
    if (matcherBidOpeningTime1.find()) {
      dataMap.put("bidOpeningTime", matcherBidOpeningTime1.group(1));
    }
    String bidOpeningTime2 = "招标公告日期[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+";
    Pattern compileBidOpeningTime2 = Pattern.compile(bidOpeningTime2);
    Matcher matcherBidOpeningTime2 = compileBidOpeningTime2.matcher(text);
    if (matcherBidOpeningTime2.find()) {
      dataMap.put("bidOpeningTime", matcherBidOpeningTime2.group(1));
    }
    //项目概况
    String summary = "项目概况[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileSummary = Pattern.compile(summary);
    Matcher matcherSummary = compileSummary.matcher(text);
    if (matcherSummary.find()) {
      dataMap.put("summary", matcherSummary.group(1));
    }
    //中标单位
    String bidUnit = "中标供应商[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileBidUnit = Pattern.compile(bidUnit);
    Matcher matcherBidUnit = compileBidUnit.matcher(text);
    if (matcherBidUnit.find()) {
      dataMap.put("bidUnit", matcherBidUnit.group(1));
    }
    String bidUnit1 = "中标单位[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileBidUnit1 = Pattern.compile(bidUnit1);
    Matcher matcherBidUnit1 = compileBidUnit1.matcher(text);
    if (matcherBidUnit1.find()) {
      dataMap.put("bidUnit", matcherBidUnit1.group(1));
    }
    String bidUnit2 = "中标人[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileBidUnit2 = Pattern.compile(bidUnit2);
    Matcher matcherBidUnit2 = compileBidUnit2.matcher(text);
    if (matcherBidUnit2.find()) {
      dataMap.put("bidUnit", matcherBidUnit2.group(1));
    }
    String bidUnit3 = "中标供应商名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileBidUnit3 = Pattern.compile(bidUnit3);
    Matcher matcherBidUnit3 = compileBidUnit3.matcher(text);
    if (matcherBidUnit3.find()) {
      dataMap.put("bidUnit", matcherBidUnit3.group(1));
    }
    String bidUnit4 = "中标人名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+";
    Pattern compileBidUnit4 = Pattern.compile(bidUnit4);
    Matcher matcherBidUnit4 = compileBidUnit4.matcher(text);
    if (matcherBidUnit4.find()) {
      dataMap.put("bidUnit", matcherBidUnit4.group(1));
    }
    //项目预算
//    String budget = "预算金额[\\s*|:|：]+([￥|¥][0-9|\\.*]+\\s*[\u4E00-\u9FA5]*)";
    String budget = "预算金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)";
    Pattern compileBudget = Pattern.compile(budget);
    Matcher matcherBudge = compileBudget.matcher(text);
    if (matcherBudge.find()) {
      dataMap.put("budget", matcherBudge.group(1));
    }
    String budget1 = "总中标金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)";
    Pattern compileBudget1 = Pattern.compile(budget1);
    Matcher matcherBudge1 = compileBudget1.matcher(text);
    if (matcherBudge1.find()) {
      dataMap.put("budget", matcherBudge1.group(1));
    }
    String budget2 = "中标金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)";
    Pattern compileBudget2 = Pattern.compile(budget2);
    Matcher matcherBudge2 = compileBudget2.matcher(text);
    if (matcherBudge2.find()) {
      dataMap.put("budget", matcherBudge2.group(1));
    }
    String budget3 = "预算金额（最高限价）[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)";
    Pattern compileBudget3 = Pattern.compile(budget3);
    Matcher matcherBudge3 = compileBudget3.matcher(text);
    if (matcherBudge3.find()) {
      dataMap.put("budget", matcherBudge3.group(1));
    }
    String budget7 = "项目预算[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)";
    Pattern compileBudget7 = Pattern.compile(budget7);
    Matcher matcherBudge7 = compileBudget7.matcher(text);
    if (matcherBudge7.find()) {
      dataMap.put("budget", matcherBudge7.group(1));
    }
    String budget4 = "单价[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)";
    Pattern compileBudget4 = Pattern.compile(budget4);
    Matcher matcherBudge4 = compileBudget4.matcher(text);
    if (matcherBudge4.find()) {
      dataMap.put("budget", matcherBudge4.group(1));
    }
    String budget5 = "金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)";
    Pattern compileBudget5 = Pattern.compile(budget5);
    Matcher matcherBudge5 = compileBudget5.matcher(text);
    if (matcherBudge5.find()) {
      dataMap.put("budget", matcherBudge5.group(1));
    }
    String budget6 = "价格[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)";
    Pattern compileBudget6 = Pattern.compile(budget6);
    Matcher matcherBudge6 = compileBudget6.matcher(text);
    if (matcherBudge6.find()) {
      dataMap.put("budget", matcherBudge6.group(1));
    }
    //附件地址
    dataMap.put("appendix", "");
    //面包屑索引路径
    dataMap.put("route", doc.select(".ewb-loca").get(0).text().split("：")[1]);
    //备用key
    dataMap.put("id", url.url);
  }

  public static void main(String[] args) throws Exception {
    String url;
    url = "http://zwfw.wlmq.gov.cn/ggzy/";
    url = "http://zwfw.wlmq.gov.cn/EWB-FRONT/rest/frontAppCustomAction/getPageInfoListNew?code=001002&page=1";
//    url = "http://zwfw.wlmq.gov.cn/EWB-FRONT/rest/frontAppCustomAction/getPageInfoListNew?code=001002&page=2";
//        url = "http://zwfw.wlmq.gov.cn/ggzy/jyxx/001002/001002003/20200708/A850B1A90DA443CDE05310C410AC5772202007-000101.html";
    HttpUtils hp = HttpUtils.getInstance();
    CrawlDatum crawlDatum = new CrawlDatum();
    HttpParameters httpParameters = new HttpParameters();
    BasicNameValuePair[] data = {
        new BasicNameValuePair("params", "{\"siteGuid\":\"7eb5f7f1-9041-43ad-8e13-8fcb82ea831a\",\"categoryNum\":" + "\"001002\"" + ",\"pageIndex\":2,\"pageSize\":15,\"jyStatus\":\"\",\"kw\":\"\"}")};
    String s = new ObjectMapper().writeValueAsString(data);
    httpParameters.setType("post");
    httpParameters.setMethod("http");
    httpParameters.getMap().put("x-www-form-urlencoded", s);
    crawlDatum.setExtendData(httpParameters.getMap());
    Content content = hp.getProtocolOutput(url, crawlDatum).getContent();
    Outlink outlink = new Outlink(url, "");
    WUlumuqishigonggongziyuanjiaoyiwangTermplate parse = new WUlumuqishigonggongziyuanjiaoyiwangTermplate();
    ParseData parseData = parse.parse(outlink, content, RunMode.BOTH);
    Map<String, String> map = parseData.dataMap;
    List<Outlink> outLinks = parseData.outLinks;
    for (Outlink outLink : outLinks) {
      System.out.println(outLink.url);
    }
    Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      System.out.println(entry.getKey() + "  ： " + entry.getValue());
//      System.out.println(entry.getValue());
    }
//    JSONObject jsonMap = JSONObject.fromObject(map);
//    System.out.print("bidmodel=" + jsonMap);


  }

}
