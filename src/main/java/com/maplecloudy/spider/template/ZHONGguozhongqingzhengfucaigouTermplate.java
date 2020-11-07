package com.maplecloudy.spider.template;

import com.maplecloudy.spider.parse.AbstractTemplate;
import com.maplecloudy.spider.parse.ParseData;
import com.maplecloudy.spider.protocol.httpmethod.HttpUtils;
import com.maplecloudy.spider.schema.Content;
import com.maplecloudy.spider.schema.CrawlDatum;
import com.maplecloudy.spider.schema.Outlink;
import com.maplecloudy.spider.util.DateUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中国重庆政府采购 解析模板
 * <p>
 * Author yanzhen
 * Date  2020-11-02
 */
public class ZHONGguozhongqingzhengfucaigouTermplate extends AbstractTemplate {

  public ZHONGguozhongqingzhengfucaigouTermplate() throws MalformedURLException {
    // 模板名字，请以网址全程为准
    name = "中国重庆政府采购";
    // 爬虫种子页面
    addSeedLink("https://www.ccgp-chongqing.gov.cn/", "首页");
    // 爬虫更新的需要的链接
    addUpdateLink("https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/new?endDate=2020-09-03&pi=1&ps=20&startDate=2019-09-03", "列表第一页");
    addUpdateLink("https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/new?endDate=2020-09-03&pi=2&ps=20&startDate=2019-09-03", "列表第二页");
    // 网站所有有的链接类型，以及对应的正则
    addDict("首页", "https://www.ccgp-chongqing.gov.cn/", "GET", "utf-8");
    addDict("列表第一页", "https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/new\\?endDate=\\d+-\\d+-\\d+&pi=1&ps=20&startDate=\\d+-\\d+-\\d+", "GET", "utf-8");
    addDict("列表页", "https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/new\\?endDate=\\d+-\\d+-\\d+&pi=\\d+&ps=20&startDate=\\d+-\\d+-\\d+", "GET", "utf-8");
    addDict("详情页", "https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/\\d+", "GET", "utf-8");

  }

  @Override
  public ParseData parse(Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, JSONException, MalformedURLException {

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

  //从[首页]提取链接
  public void genSHOUyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String endDate = DateUtils.getCurrentTime();
    String startDate = DateUtils.getTimeByMonth(-11);
    Outlink outlink = new Outlink("https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/new?endDate=" + endDate + "&pi=1&ps=20&startDate=" + startDate,"");
    outlinks.add(outlink);
  }

  //从[首页]提取数据
  public void genSHOUyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表第一页]提取链接
  public void genLIEbiaodiyiyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, JSONException, MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String endDate = DateUtils.getCurrentTime();
    String startDate = DateUtils.getTimeByMonth(-11);
    String html = new String(content.getContent(), "utf-8");
    JSONObject original = new JSONObject(html);
    int recordsNum = original.getInt("total");
    int pageNo = recordsNum % 10 == 0 ? recordsNum / 10 : recordsNum / 10 + 1;
    for (int i = 2; i <= pageNo; i++) {
      Outlink outlink = new Outlink("https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/new?endDate=" + endDate + "&pi=" + i + "&ps=20&startDate=" + startDate,"");
      outlinks.add(outlink);
    }

    JSONArray notices = original.getJSONArray("notices");
    for (int i = 0; i < notices.length(); i++) {
      String id = notices.getJSONObject(i).getString("id");
      Outlink outlink = new Outlink("https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/" + id,"");
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
    JSONObject original = new JSONObject(html);
    JSONArray notices = original.getJSONArray("notices");
    for (int i = 0; i < notices.length(); i++) {
      String id = notices.getJSONObject(i).getString("id");
      Outlink outlink = new Outlink("https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/" + id,"");
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
    //源地址
    dataMap.put("url", url.getUrl());
    //来源网站
    dataMap.put("web", (new URL(url.getUrl()).getHost()));
    //标题
    dataMap.put("title", json.getJSONObject("notice").getString("title"));
    //副标题
    dataMap.put("bakeTitle", "");
    //内容
    dataMap.put("content", text);
    //项目名称
//    String projectName1 = "项目名称[\\s*|:|：]*([\u4E00-\u9FA5]+)";
//    Pattern compileProName1 = Pattern.compile(projectName1);
//    Matcher matcherProName1 = compileProName1.matcher(text);
//    if (matcherProName1.find()) {
//      dataMap.put("projectname", matcherProName1.group(1));
//    }
      dataMap.put("projectname", json.getJSONObject("notice").getString("projectName"));
    //项目编号
      dataMap.put("projectnum", json.getJSONObject("notice").getString("id"));
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
    dataMap.put("province", "重庆市");
    //项目所在市
//        String city = "行政区域 ([\u4E00-\u9FA5]+).+";
//        Pattern compileCity = Pattern.compile(city);
//        Matcher matcherCity = compileCity.matcher(text);
//        if (matcherCity.find()) {
//          bm.setCounty(matcherCity.group(1));
//        }
    //项目所在市
    dataMap.put("city", "");
    //项目所在县
    dataMap.put("county", "");
    //无法区分地区时放置地区
    dataMap.put("district", "");
    //采购单位
      dataMap.put("purchaseUnit", json.getJSONObject("notice").getString("buyerName"));
    //代理机构
      dataMap.put("agency", json.getJSONObject("notice").getString("agentName"));
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
    dataMap.put("source", "湖南招标网");
    //发布时间
      dataMap.put("publishTime", json.getJSONObject("notice").getString("openBidTime"));
    //开标时间
      dataMap.put("bidOpeningTime", json.getJSONObject("notice").getString("openBidTime"));
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
      dataMap.put("budget", json.getJSONObject("notice").getString("projectBudget"));
    //附件地址
    dataMap.put("appendix", "");
    //面包屑索引路径
    dataMap.put("route", "");
    //备用key
    dataMap.put("id", url.url);
  }

  public static void main(String[] args) throws Exception {
    String url;
    url = "https://www.ccgp-chongqing.gov.cn/";
    url = "https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/new?endDate=2020-09-03&pi=1&ps=20&startDate=2019-09-03";
    url = "https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/new?endDate=2020-09-03&pi=2&ps=20&startDate=2019-09-03";
    url = "https://www.ccgp-chongqing.gov.cn/gwebsite/api/v1/notices/stable/877618718610599936";
    HttpUtils hp = HttpUtils.getInstance();
    CrawlDatum crawlDatum = new CrawlDatum();
    Content content = hp.getProtocolOutput(url, crawlDatum).getContent();
    Outlink outlink = new Outlink(url, "");
    ZHONGguozhongqingzhengfucaigouTermplate parse = new ZHONGguozhongqingzhengfucaigouTermplate();
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
