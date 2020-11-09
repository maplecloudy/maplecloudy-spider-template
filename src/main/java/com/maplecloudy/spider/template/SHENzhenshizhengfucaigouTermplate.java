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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * 深圳市政府采购 解析模板
 * <p>
 * Author yanzhen
 * Date  2020-11-06
 */
public class SHENzhenshizhengfucaigouTermplate extends AbstractTemplate {

  public SHENzhenshizhengfucaigouTermplate() throws MalformedURLException {
    // 模板名字，请以网址全程为准
    name = "深圳市政府采购";
    // 爬虫种子页面
    addSeedLink("http://www.szzfcg.cn/", "首页");
    // 爬虫更新的需要的链接
    addUpdateLink("", "列表第一页");
    addUpdateLink("", "列表第二页");
    // 网站所有有的链接类型，以及对应的正则
    addDict("首页", "http://www.szzfcg.cn/", "GET", "utf-8");
    addDict("列表第一页", "http://szzfcg.cn/portal/topicView.do\\?method=view&id=\\d+", "POST", "utf-8");
    addDict("列表第一页01", "http://www.szzfcg.cn/stock/stprFile.do", "POST", "utf-8");
    addDict("列表页", "http://szzfcg.cn/portal/topicView.do\\?method=view&id=\\d+&page=\\d+", "GET", "utf-8");
    addDict("列表页01", "http://www.szzfcg.cn/stock/stprFile.do\\?stprId=&page=\\d+", "GET", "utf-8");
    addDict("详情页", "http://szzfcg.cn/portal/documentView.do\\?method=view&id=\\d+", "GET", "utf-8");

  }

  public ParseData parse(Outlink url, Content content, RunMode runMode) throws IOException {

    ParseData parseData = new ParseData();
    if (matches(url, "首页")) {
      genSHOUyeLinks(parseData.outLinks, url, content, runMode);
      genSHOUyeDatas(parseData.dataMap, url, content, runMode);
    } else if (matches(url, "列表第一页")) {
      genLIEbiaodiyiyeLinks(parseData.outLinks, url, content, runMode);
      genLIEbiaodiyiyeDatas(parseData.dataMap, url, content, runMode);
    } else if (matches(url, "列表第一页01")) {
      genLIEbiaodiyiye01Links(parseData.outLinks, url, content, runMode);
      genLIEbiaodiyiye01Datas(parseData.dataMap, url, content, runMode);
    } else if (matches(url, "列表页")) {
      genLIEbiaoyeLinks(parseData.outLinks, url, content, runMode);
      genLIEbiaoyeDatas(parseData.dataMap, url, content, runMode);
    } else if (matches(url, "列表页01")) {
      genLIEbiaoye01Links(parseData.outLinks, url, content, runMode);
      genLIEbiaoye01Datas(parseData.dataMap, url, content, runMode);
    } else if (matches(url, "详情页")) {
      genXIANGqingyeLinks(parseData.outLinks, url, content, runMode);
      genXIANGqingyeDatas(parseData.dataMap, url, content, runMode);
    }
    return parseData;
  }

  public static String getId(String url) {
    return url.split("&id=")[1].split("&")[0];
  }

  //从[首页]提取链接
  public void genSHOUyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String[] codes = new String[]{"61122347", "2719966", "1660", "2014"};
    for (int i = 0; i < codes.length; i++) {
      Outlink outlink = new Outlink("http://szzfcg.cn/portal/topicView.do?method=view&id=" + codes[i], "");
      outlinks.add(outlink);
    }
  }

  //从[首页]提取数据
  public void genSHOUyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表第一页]提取链接
  public void genLIEbiaodiyiyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws IOException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = new String(content.getContent(), "utf-8");
    Document doc = Jsoup.parse(html);
    Elements elements = doc.select("tbody[class=tableBody]").select("tr");
    System.out.println();
    for (Element element : elements) {
      String href = element.select("a").attr("href");
      String newUrl = "http://szzfcg.cn/portal/documentView.do?method=view&id=" + href.split("id=")[1];
      Outlink outlink = new Outlink(newUrl, "");
      outlinks.add(outlink);
    }
    String text = doc.select("td[class=statusBar]").text();
    Integer recordsNum = Integer.valueOf(text.split("找到")[1].split("条")[0].replace(",", "").trim());
    int pageNo = recordsNum % 20 == 0 ? recordsNum / 20
        : recordsNum / 20 + 1;
    for (int i = 2; i <= pageNo; i++) {
      Outlink outlink = new Outlink(url.getUrl() + "&page=" + i, "");
      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("ec_i", "topicChrList_20070702"),
          new BasicNameValuePair("topicChrList_20070702_crd", "20"),
          new BasicNameValuePair("topicChrList_20070702_p", String.valueOf(i)),
          new BasicNameValuePair("method", "view"),
          new BasicNameValuePair("id", getId(url.getUrl())),
          new BasicNameValuePair("topicChrList_20070702_rd", "20"),};
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

  //从[列表第一页01]提取链接
  public void genLIEbiaodiyiye01Links(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws IOException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = new String(content.getContent(), "utf-8");
    Document doc = Jsoup.parse(html);
    Elements elements = doc.select("tbody[class=tableBody]").select("tr");
    System.out.println();
    for (Element element : elements) {
      String href = element.select("a").attr("href");
      String newUrl = "http://www.szzfcg.cn/stock/stprFile.do?method=download&id=" + href.split("id=")[1];
      Outlink outlink = new Outlink(newUrl, "");
      outlinks.add(outlink);
    }
    String text = doc.select("td[class=statusBar]").text();
    Integer recordsNum = Integer.valueOf(text.split("找到")[1].split("条")[0].replace(",", "").trim());
    int pageNo = recordsNum % 15 == 0 ? recordsNum / 15
        : recordsNum / 15 + 1;
    for (int i = 2; i <= pageNo; i++) {
      Outlink outlink = new Outlink("http://www.szzfcg.cn/stock/stprFile.do?stprId=" + "&page=" + i, "");
      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("ec_i", "ec"),
          new BasicNameValuePair("ec_crd", "15"),
          new BasicNameValuePair("ec_p", String.valueOf(i)),
          new BasicNameValuePair("siteId", "1"),
          new BasicNameValuePair("ec_rd", "15"),};
      String entity = new ObjectMapper().writeValueAsString(data);
      httpParameters.getMap().put("x-www-form-urlencoded", entity);
      httpParameters.setContentType(
          "application/x-www-form-urlencoded; charset=UTF-8");
      httpParameters.setMethod("http");
      outlink.addExtend(httpParameters.getMap());
      outlinks.add(outlink);
    }
  }

  //从[列表第一页01]提取数据
  public void genLIEbiaodiyiye01Datas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表页]提取链接
  public void genLIEbiaoyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = new String(content.getContent(), "utf-8");
    Document doc = Jsoup.parse(html);
    Elements elements = doc.select("tbody[class=tableBody]").select("tr");
    System.out.println();
    for (Element element : elements) {
      String href = element.select("a").attr("href");
      String newUrl = "http://szzfcg.cn/portal/documentView.do?method=view&id=" + href.split("id=")[1];
      Outlink outlink = new Outlink(newUrl, "");
      outlinks.add(outlink);
    }
  }

  //从[列表页]提取数据
  public void genLIEbiaoyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表页01]提取链接
  public void genLIEbiaoye01Links(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = new String(content.getContent(), "utf-8");
    Document doc = Jsoup.parse(html);
    Elements elements = doc.select("tbody[class=tableBody]").select("tr");
    System.out.println();
    for (Element element : elements) {
      String href = element.select("a").attr("href");
      String newUrl = "http://www.szzfcg.cn/stock/stprFile.do?method=download&id=" + href.split("id=")[1];
      Outlink outlink = new Outlink(newUrl, "");
      outlinks.add(outlink);
    }
  }

  //从[列表页01]提取数据
  public void genLIEbiaoye01Datas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[详情页]提取链接
  public void genXIANGqingyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

  }

  //从[详情页]提取数据
  public void genXIANGqingyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) throws MalformedURLException, UnsupportedEncodingException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

    String html = new String(content.getContent(), "utf-8");
    Document doc = Jsoup.parse(html);
    String text = doc.text();
//    System.out.println(text);
    Elements contentElements = doc.select(".m-bd");
    String contentHtml = contentElements.html();
//    System.out.println(contentHtml);

    //源地址
    dataMap.put("url", url.getUrl());
    //来源网站
    dataMap.put("web", (new URL(url.getUrl()).getHost()));
    //标题
    dataMap.put("title", doc.select("tbody>tr").get(1).text());
    //副标题
    dataMap.put("bakeTitle", "");
    //内容
//    dataMap.put("content", html);
    //项目名称
//    String projectName1 = "项目名称[\\s*|:|：]*([\u4E00-\u9FA5]+)";
//    Pattern compileProName1 = Pattern.compile(projectName1);
//    Matcher matcherProName1 = compileProName1.matcher(text);
//    if (matcherProName1.find()) {
//      dataMap.put("projectname", matcherProName1.group(1));
//    }
//    System.out.println(doc.select("tbody>tr").get(4).select("td").get(3).select("p").text());
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
    dataMap.put("province", "广东省");
    //项目所在市
//        String city = "行政区域 ([\u4E00-\u9FA5]+).+";
//        Pattern compileCity = Pattern.compile(city);
//        Matcher matcherCity = compileCity.matcher(text);
//        if (matcherCity.find()) {
//          bm.setCounty(matcherCity.group(1));
//        }
    //项目所在市
    dataMap.put("city", "深联市");
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
    dataMap.put("source", "深联招标采购网");
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
    dataMap.put("summary", doc.select("tbody>tr").get(4).select("td").get(2).select("p").text());
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
    dataMap.put("budget", doc.select("tbody>tr").get(4).select("td").get(3).select("p").text() + "万元");
    //附件地址
    dataMap.put("appendix", "");
    //面包屑索引路径
    dataMap.put("route", "");
    //备用key
    dataMap.put("id", url.url);
  }

  public static void main(String[] args) throws Exception {
    String url;
    url = "http://www.szzfcg.cn/";
    url = "http://szzfcg.cn/portal/topicView.do?method=view&id=61122347";
//    url = "http://szzfcg.cn/portal/topicView.do?method=view&id=61122347&page=14";
//    url = "http://www.szzfcg.cn/stock/stprFile.do";
//    url = "http://www.szzfcg.cn/stock/stprFile.do?stprId=&page=637";
//    url = "http://szzfcg.cn/portal/documentView.do?method=view&id=654126881";
    HttpUtils hp = HttpUtils.getInstance();
    CrawlDatum crawlDatum = new CrawlDatum();
    Content content = hp.getProtocolOutput(url, crawlDatum).getContent();
    Outlink outlink = new Outlink(url, "");
    SHENzhenshizhengfucaigouTermplate parse = new SHENzhenshizhengfucaigouTermplate();
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
