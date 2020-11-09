package com.maplecloudy.spider.template;

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
 * 徐州政府采购 解析模板
 * <p>
 * Author yanzhen
 * Date  2020-11-06
 */
public class XUzhouzhengfucaigouTermplate extends AbstractTemplate {

  public XUzhouzhengfucaigouTermplate() throws MalformedURLException {
    // 模板名字，请以网址全程为准
    name = "徐州政府采购";
    // 爬虫种子页面
    addSeedLink("http://www.ccgp-xuzhou.gov.cn/Home/HomeIndex", "首页");
    // 爬虫更新的需要的链接
    addUpdateLink("http://www.ccgp-xuzhou.gov.cn/Home/PageListJson?sidx=createdate&category_id=9&page=1", "列表第一页");
    addUpdateLink("http://www.ccgp-xuzhou.gov.cn/Home/PageListJson?sidx=createdate&category_id=9&page=2", "列表第二页");
    // 网站所有有的链接类型，以及对应的正则
    addDict("首页", "http://www.ccgp-xuzhou.gov.cn/Home/HomeIndex", "GET", "utf-8");
    addDict("列表第一页", "http://www.ccgp-xuzhou.gov.cn/Home/PageListJson\\?sidx=createdate&category_id=\\d+&page=1", "POST", "utf-8");
    addDict("列表页", "http://www.ccgp-xuzhou.gov.cn/Home/PageListJson\\?sidx=createdate&category_id=\\d+&page=\\d++", "POST", "utf-8");
    addDict("详情页", "http://www.ccgp-xuzhou.gov.cn/Home/PageDetailsJson\\?articleid=[0-9a-zA-Z_-]+&type=\\d+", "GET", "utf-8");

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

  public static String getId(String url) {
    return url.split("category_id=")[1].split("&")[0];
  }

  //从[首页]提取链接
  public void genSHOUyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws IOException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String[] codes = new String[]{"4", "5", "331", "332", "333", "9", "10", "44", "55", "334", "335", "336", "919", "1010"};
    for (int i = 0; i < codes.length; i++) {
      Outlink outlink = new Outlink("http://www.ccgp-xuzhou.gov.cn/Home/PageListJson?sidx=createdate&category_id=" + codes[i] + "&page=1", "");
      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("sidx", "createdate"),
          new BasicNameValuePair("category_id", codes[i]),
          new BasicNameValuePair("page", "1"),
          new BasicNameValuePair("pagesize", "20"),
          new BasicNameValuePair("title", "")};
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
  public void genSHOUyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表第一页]提取链接
  public void genLIEbiaodiyiyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws IOException, JSONException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = new String(content.getContent(), "utf-8");
    JSONObject json = new JSONObject(html);
    JSONArray detail = json.getJSONArray("rows");
    for (int i = 0; i < detail.length(); i++) {
      JSONObject jsonObject = detail.getJSONObject(i);
      String id = "";
      String typeCode = getId(url.getUrl());
      if (jsonObject.has("articleid")) {
        id = jsonObject.getString("articleid");
      } else if (jsonObject.has("bulletinid")) {
        id = jsonObject.getString("bulletinid");
        typeCode = "0";
      } else if (jsonObject.has("contractsid")) {
        id = jsonObject.getString("contractsid");
      }
      String newUrl = "http://www.ccgp-xuzhou.gov.cn/Home/PageDetailsJson?articleid=" + id + "&type=" + typeCode;
      Outlink outlink = new Outlink(newUrl, "");
      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("articleid", id),
          new BasicNameValuePair("type", typeCode)};
      String entity = new ObjectMapper().writeValueAsString(data);
      httpParameters.getMap().put("x-www-form-urlencoded", entity);
      httpParameters.setContentType(
          "application/x-www-form-urlencoded; charset=UTF-8");
      httpParameters.setMethod("http");
      outlink.addExtend(httpParameters.getMap());
      outlinks.add(outlink);
    }

    int pageNo = json.getInt("total");
    for (int i = 2; i <= pageNo; i++) {
      Outlink outlink = new Outlink(url.getUrl().replace("page=1", "page=" + i), "");

      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("sidx", "createdate"),
          new BasicNameValuePair("category_id", getId(url.getUrl())),
          new BasicNameValuePair("page", String.valueOf(i)),
          new BasicNameValuePair("pagesize", "20"),
          new BasicNameValuePair("title", "")};
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
  public void genLIEbiaoyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws IOException, JSONException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = new String(content.getContent(), "utf-8");
    JSONObject json = new JSONObject(html);
    JSONArray detail = json.getJSONArray("rows");
    for (int i = 0; i < detail.length(); i++) {
      JSONObject jsonObject = detail.getJSONObject(i);
      String id = "";
      String typeCode = getId(url.getUrl());
      if (jsonObject.has("articleid")) {
        id = jsonObject.getString("articleid");
      } else if (jsonObject.has("bulletinid")) {
        id = jsonObject.getString("bulletinid");
        typeCode = "0";
      } else if (jsonObject.has("contractsid")) {
        id = jsonObject.getString("contractsid");
      }
      String newUrl = "http://www.ccgp-xuzhou.gov.cn/Home/PageDetailsJson?articleid=" + id + "&type=" + typeCode;
      Outlink outlink = new Outlink(newUrl, "");
      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("articleid", id),
          new BasicNameValuePair("type", typeCode)};
      String entity = new ObjectMapper().writeValueAsString(data);
      httpParameters.getMap().put("x-www-form-urlencoded", entity);
      httpParameters.setContentType(
          "application/x-www-form-urlencoded; charset=UTF-8");
      httpParameters.setMethod("http");
      outlink.addExtend(httpParameters.getMap());
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
  public void genXIANGqingyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, MalformedURLException, JSONException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

    String html = new String(content.getContent(), "utf-8");
    Document doc = Jsoup.parse(html);
    String text = doc.text();
    System.out.println(text);
    JSONObject json = new JSONObject(text);
    json.getJSONArray("rows").getJSONObject(0).getString("contractsid");

    //源地址
    dataMap.put("url", url.getUrl());
    //来源网站
    dataMap.put("web", (new URL(url.getUrl()).getHost()));
    //标题
    dataMap.put("title", json.getJSONArray("rows").getJSONObject(0).getString("contractsid"));
    //副标题
    dataMap.put("bakeTitle", "");
    //内容
    dataMap.put("content", html);
    //项目名称
//    String projectName1 = "项目名称[\\s*|:|：]*([\u4E00-\u9FA5]+)";
//    Pattern compileProName1 = Pattern.compile(projectName1);
//    Matcher matcherProName1 = compileProName1.matcher(text);
//    if (matcherProName1.find()) {
//      dataMap.put("projectname", matcherProName1.group(1));
//    }
    dataMap.put("projectname", json.getJSONArray("rows").getJSONObject(0).getString("projectname"));
    //项目编号
    dataMap.put("projectnum", json.getJSONArray("rows").getJSONObject(0).getString("projectid"));
    //标的状态
    dataMap.put("projectStatus", json.getJSONArray("rows").getJSONObject(0).getString("purchaseway"));
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
    dataMap.put("city", "徐州市");
    //项目所在县
    dataMap.put("county", "");
    //无法区分地区时放置地区
    dataMap.put("district", "");
    //采购单位
    dataMap.put("purchaseUnit", json.getJSONArray("rows").getJSONObject(0).getString("purchasingunit"));
    //代理机构
    dataMap.put("agency", json.getJSONArray("rows").getJSONObject(0).getString("agentname"));
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

    dataMap.put("contactPerson", json.getJSONArray("rows").getJSONObject(0).getString("purchaseitemcode"));

    //项目联系人电话

    dataMap.put("contactphone", json.getJSONArray("rows").getJSONObject(0).getString("supplycontact"));

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
    dataMap.put("source", "徐州政府采购");
    //发布时间

    dataMap.put("publishTime", json.getJSONArray("rows").getJSONObject(0).getString("entrytime"));
    //开标时间
    dataMap.put("bidOpeningTime", "");
    //项目概况
    dataMap.put("summary", "");
    //中标单位
    dataMap.put("bidUnit", json.getJSONArray("rows").getJSONObject(0).getString("bidwinner"));
    //项目预算
//    String budget = "预算金额[\\s*|:|：]+([￥|¥][0-9|\\.*]+\\s*[\u4E00-\u9FA5]*)";
    dataMap.put("budget", json.getJSONArray("rows").getJSONObject(0).getString("procurementbudget"));
    //附件地址
    dataMap.put("appendix", "");
    //面包屑索引路径
    dataMap.put("route", "");
    //备用key
    dataMap.put("id", url.url);
  }

  public static void main(String[] args) throws Exception {
    String url;
    url = "http://www.ccgp-xuzhou.gov.cn/Home/HomeIndex";
    url = "http://www.ccgp-xuzhou.gov.cn/Home/PageListJson?sidx=createdate&category_id=9&page=1";
    url = "http://www.ccgp-xuzhou.gov.cn/Home/PageListJson?sidx=createdate&category_id=9&page=2";
//    url = "http://www.ccgp-xuzhou.gov.cn/Home/PageDetailsJson?articleid=93341eda-528e-42d0-97d9-3f1caabd881e&type=9";
    HttpUtils hp = HttpUtils.getInstance();
    CrawlDatum crawlDatum = new CrawlDatum();
    HttpParameters httpParameters = new HttpParameters();
    httpParameters.setType("post");
    BasicNameValuePair[] data = {
        new BasicNameValuePair("sidx", "createdate"),
        new BasicNameValuePair("category_id", "9"),
        new BasicNameValuePair("page", "2"),
        new BasicNameValuePair("pagesize", "20"),
        new BasicNameValuePair("title", "")};
    String entity = new ObjectMapper().writeValueAsString(data);
    httpParameters.getMap().put("x-www-form-urlencoded", entity);
    httpParameters.setContentType(
        "application/x-www-form-urlencoded; charset=UTF-8");
    httpParameters.setMethod("http");

//        HttpParameters httpParameters = new HttpParameters();
//        httpParameters.setType("post");
//        BasicNameValuePair[] data = {
//                new BasicNameValuePair("articleid", "7ae48039-df4a-48e5-a420-a317bce6a3e2"),
//                new BasicNameValuePair("type", "4")};
//        String entity = new ObjectMapper().writeValueAsString(data);
//        httpParameters.getMap().put("x-www-form-urlencoded", entity);
//        httpParameters.setContentType(
//                "application/x-www-form-urlencoded; charset=UTF-8");
//        httpParameters.setMethod("http");

    crawlDatum.setExtendData(httpParameters.getMap());
    Content content = hp.getProtocolOutput(url, crawlDatum).getContent();
    Outlink outlink = new Outlink(url, "");
    XUzhouzhengfucaigouTermplate parse = new XUzhouzhengfucaigouTermplate();
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
