package com.maplecloudy.spider.template.liaoning;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maplecloudy.spider.parse.AbstractTemplate;
import com.maplecloudy.spider.parse.ParseData;
import com.maplecloudy.spider.protocol.httpmethod.HttpUtils;
import com.maplecloudy.spider.schema.Content;
import com.maplecloudy.spider.schema.CrawlDatum;
import com.maplecloudy.spider.schema.Outlink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 辽宁省公共资源交易平台 解析模板
 * <p>
 * Author maplecloudy
 * Date  2020-11-16
 */

/**
 * 该网站请求方式为get
 */
public class LIAOningshenggonggongziyuanjiaoyipingtaiTermplate extends AbstractTemplate {

  public LIAOningshenggonggongziyuanjiaoyipingtaiTermplate() throws MalformedURLException {
    // 模板名字，请以网址全程为准
    name = "辽宁省公共资源交易平台";
    // 爬虫种子页面
    addSeedLink("https://www.lnsggzy.com/", "首页");
    // 爬虫更新的需要的链接
    addUpdateLink("https://www.lnsggzy.com/003/secondpageDeal.html", "列表第一页");
    addUpdateLink("https://www.lnsggzy.com/003/2.html", "列表第二页");
    // 网站所有有的链接类型，以及对应的正则
    addDict("首页", "https://www.lnsggzy.com/", "GET", "utf-8");
    addDict("列表第一页", "https://www.lnsggzy.com/003/secondpageDeal.html", "GET", "utf-8");
    addDict("列表页", "https://www.lnsggzy.com/003/\\d+.html", "GET", "utf-8");
    addDict("详情页", "https://www.lnsggzy.com/\\d+/\\d+/\\d+/\\d+/[0-9a-zA-Z_-]+.html", "GET", "gbk");

  }

  public ParseData parse(Outlink url, Content content, RunMode runMode) throws MalformedURLException, UnsupportedEncodingException {

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

    Outlink outlink = new Outlink("https://www.lnsggzy.com/003/secondpageDeal.html", "");
    outlinks.add(outlink);
  }

  //从[首页]提取数据
  public void genSHOUyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表第一页]提取链接
  public void genLIEbiaodiyiyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = getHtmlString(content, "utf-8");
    Document doc = Jsoup.parse(html);
    Elements datas = doc.select(".ewb-con-bd").select("a[href]")
        .select("a[target]").select("a[title]");
    for (Element data : datas) {
      Outlink outlink = new Outlink("https://www.lnsggzy.com" + data.attr("href"), "");
      outlinks.add(outlink);
    }
    //简化处理，直接抛446
    for (int i = 2; i <= 446; i++) {
      Outlink outlink = new Outlink(url.getUrl().replace("secondpageDeal.html", i + ".html"), "");
      outlinks.add(outlink);
    }
  }


  //从[列表第一页]提取数据
  public void genLIEbiaodiyiyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表页]提取链接
  public void genLIEbiaoyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws UnsupportedEncodingException, MalformedURLException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    String html = getHtmlString(content, "utf-8");
    Document doc = Jsoup.parse(html);
    Elements datas = doc.select(".ewb-con-bd").select("a[href]")
        .select("a[target]").select("a[title]");
    for (Element data : datas) {
      Outlink outlink = new Outlink("https://www.lnsggzy.com" + data.attr("href"), "");
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

    String html = getHtmlString(content, "utf-8");
    Document doc = getHtmlDom(html);
    String text = doc.text();
    System.out.println(text);
//      if (!text.contains("公安备案：2105009900415001")) {
//        System.out.println("详情页可能发生跳转错误！！！！！");
//        return;
//      }
    Elements contentElements = doc.select(".ewb-infodetail");
    String contentHtml = contentElements.html();
//    System.out.println(contentHtml);

    //源地址
    dataMap.put("url", url.getUrl());
    //来源网站
    dataMap.put("web", (new URL(url.getUrl()).getHost()));
    //标题
    try {
      dataMap.put("title", doc.select(".ewb-infodetail-title").get(0).text());
    } catch (Exception e) {
      dataMap.put("title", "");
    }
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
    Optional<String> projectName = regexValue(text, "项目名称[\\s*|:|：]+(\\d*[\u4E00-\u9FA5]+[\\d|“|\"|（]*[\u4E00-\u9FA5]+[”|\"|）]*[\u4E00-\u9FA5]*)", 1);
    projectName.ifPresent(value -> dataMap.put("projectname", value));

    //项目编号
    Optional<String> projectNum = regexValue(html, "项目编号[\\s*|:|：]+([\u4E00-\u9FA5]*\\w*\\-?\\w*\\-?\\w*)", 1);
    projectNum.ifPresent(value -> dataMap.put("projectname", value));

    Optional<String> projectNum1 = regexValue(text, "招标编号[\\s*|:|：]+([\u4E00-\u9FA5]*\\w*\\-?\\w*\\-?\\w*)", 1);
    projectNum1.ifPresent(value -> dataMap.put("projectname", value));

    Optional<String> projectNum2 = regexValue(text, "采购编号[\\s*|:|：]+([\u4E00-\u9FA5]*\\w*\\-?\\w*\\-?\\w*)", 1);
    projectNum2.ifPresent(value -> dataMap.put("projectname", value));


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
    dataMap.put("province", "辽宁省");
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
    Optional<String> purchaseUnit = regexValue(text, "采购单位[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit.ifPresent(value -> dataMap.put("purchaseUnit", value));

    Optional<String> purchaseUnit1 = regexValue(text, "采购人信息 名 称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit1.ifPresent(value -> dataMap.put("purchaseUnit", value));

    Optional<String> purchaseUnit2 = regexValue(text, "采购单位名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit2.ifPresent(value -> dataMap.put("purchaseUnit", value));

    Optional<String> purchaseUnit3 = regexValue(text, "采购单位[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit3.ifPresent(value -> dataMap.put("purchaseUnit", value));

    Optional<String> purchaseUnit4 = regexValue(text, "采购人[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit4.ifPresent(value -> dataMap.put("purchaseUnit", value));

    Optional<String> purchaseUnit5 = regexValue(text, "采购人名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit5.ifPresent(value -> dataMap.put("purchaseUnit", value));

    Optional<String> purchaseUnit6 = regexValue(text, "采购机构名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit6.ifPresent(value -> dataMap.put("purchaseUnit", value));

    Optional<String> purchaseUnit7 = regexValue(text, "招标人[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit7.ifPresent(value -> dataMap.put("purchaseUnit", value));

    Optional<String> purchaseUnit8 = regexValue(text, "招标人名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseUnit8.ifPresent(value -> dataMap.put("purchaseUnit", value));

    //代理机构
    Optional<String> agency = regexValue(text, "代理机构名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    agency.ifPresent(value -> dataMap.put("agency", value));

    Optional<String> agency1 = regexValue(text, "采购代理机构信息 名 称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    agency1.ifPresent(value -> dataMap.put("agency", value));

    Optional<String> agency2 = regexValue(text, "采购代理机构全称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    agency2.ifPresent(value -> dataMap.put("agency", value));

    Optional<String> agency3 = regexValue(text, "代理机构[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    agency3.ifPresent(value -> dataMap.put("agency", value));

    //采购方式
    Optional<String> purchaseMethod = regexValue(text, "代理机构[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseMethod.ifPresent(value -> dataMap.put("purchaseMethod", value));

    Optional<String> purchaseMethod1 = regexValue(text, "按采购方式[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    purchaseMethod1.ifPresent(value -> dataMap.put("purchaseMethod", value));
    //项目联系人
    Optional<String> contactPerson = regexValue(text, "项目联系人[\\s*|:|：]+([\u4E00-\u9FA5]+).", 1);
    contactPerson.ifPresent(value -> dataMap.put("contactPerson", value));

    Optional<String> contactPerson1 = regexValue(text, "项目负责人[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    contactPerson1.ifPresent(value -> dataMap.put("contactPerson", value));

    Optional<String> contactPerson2 = regexValue(text, "项目联系人[\\s*|:|：]+([\u4E00-\u9FA5]+).", 1);
    contactPerson2.ifPresent(value -> dataMap.put("contactPerson", value));

    Optional<String> contactPerson3 = regexValue(text, "联系人[：|:|\\s*]+([\u4E00-\u9FA5]+).+", 1);
    contactPerson3.ifPresent(value -> dataMap.put("contactPerson", value));

    //项目联系人电话
    Optional<String> contactPhone = regexValue(text, "采购单位联系方式[\\s*|:|：]+([ |0-9|-]+).+", 1);
    contactPhone.ifPresent(value -> dataMap.put("contactPhone", value));

    Optional<String> contactPhone1 = regexValue(text, "采购单位联系电话式[\\s*|:|：]+([ |0-9|-]+).+", 1);
    contactPhone1.ifPresent(value -> dataMap.put("contactPhone", value));

    Optional<String> contactPhone2 = regexValue(text, "采购人联系方[\\s*|:|：]+([ |0-9|-]+).+", 1);
    contactPhone2.ifPresent(value -> dataMap.put("contactPhone", value));

    Optional<String> contactPhone3 = regexValue(text, "采购人联系电话式[\\s*|:|：]+([ |0-9|-]+).+", 1);
    contactPhone3.ifPresent(value -> dataMap.put("contactPhone", value));

    //项目联系人手机
    Optional<String> contactMobilePhone = regexValue(text, "电　话[\\s*|:|：]+([0-9]+-*[0-9]+)", 1);
    contactMobilePhone.ifPresent(value -> dataMap.put("contactMobilePhone", value));

    Optional<String> contactMobilePhone2 = regexValue(text, "项目联系电话[\\s*|:|：]+([ |0-9|-]+).+", 1);
    contactMobilePhone2.ifPresent(value -> dataMap.put("contactMobilePhone", value));
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
    dataMap.put("source", "辽宁省公共资源交易平台");
    //发布时间
    Optional<String> publishTime = regexValue(text, "响应文件开启时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    publishTime.ifPresent(value -> dataMap.put("publishTime", value));

    Optional<String> publishTime2 = regexValue(text, "公告时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    publishTime2.ifPresent(value -> dataMap.put("publishTime", value));

    Optional<String> publishTime3 = regexValue(text, "发布时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    publishTime3.ifPresent(value -> dataMap.put("publishTime", value));

    Optional<String> publishTime4 = regexValue(text, "中标时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    publishTime4.ifPresent(value -> dataMap.put("publishTime", value));

    Optional<String> publishTime5 = regexValue(text, "中标日期[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    publishTime5.ifPresent(value -> dataMap.put("publishTime", value));

    Optional<String> publishTime6 = regexValue(text, "开启时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    publishTime6.ifPresent(value -> dataMap.put("publishTime", value));

    //开标时间
    Optional<String> bidOpeningTime = regexValue(text, "开标时间[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    bidOpeningTime.ifPresent(value -> dataMap.put("bidOpeningTime", value));

    Optional<String> bidOpeningTime1 = regexValue(text, "开标日期[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    bidOpeningTime1.ifPresent(value -> dataMap.put("bidOpeningTime", value));

    Optional<String> bidOpeningTime2 = regexValue(text, "招标公告日期[\\s*|:|：]+((\\d{1,4}-\\d{1,2}-\\d{1,2})+).+", 1);
    bidOpeningTime2.ifPresent(value -> dataMap.put("bidOpeningTime", value));

    //项目概况
    Optional<String> summary = regexValue(text, "项目概况[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    summary.ifPresent(value -> dataMap.put("summary", value));

    //中标单位
    Optional<String> bidUnit = regexValue(text, "中标供应商[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    bidUnit.ifPresent(value -> dataMap.put("bidUnit", value));

    Optional<String> bidUnit1 = regexValue(text, "中标单位[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    bidUnit1.ifPresent(value -> dataMap.put("bidUnit", value));

    Optional<String> bidUnit2 = regexValue(text, "中标人[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    bidUnit2.ifPresent(value -> dataMap.put("bidUnit", value));

    Optional<String> bidUnit3 = regexValue(text, "中标供应商名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    bidUnit3.ifPresent(value -> dataMap.put("bidUnit", value));

    Optional<String> bidUnit4 = regexValue(text, "中标人名称[\\s*|:|：]+([\u4E00-\u9FA5]+).+", 1);
    bidUnit4.ifPresent(value -> dataMap.put("bidUnit", value));

    //项目预算
    Optional<String> budget = regexValue(text, "预算金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget1 = regexValue(text, "总中标金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget1.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget2 = regexValue(text, "中标金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget2.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget3 = regexValue(text, "预算金额[[（最高限价）]?\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)", 1);
    budget3.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget4 = regexValue(text, "项目预算[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget4.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget5 = regexValue(text, "单价[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget5.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget6 = regexValue(text, "金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget6.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget7 = regexValue(text, "价格[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget7.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget8 = regexValue(text, "收费金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget8.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget9 = regexValue(text, "预中标金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget9.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget10 = regexValue(text, "预中标价格[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget10.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget11 = regexValue(text, "项目总投资额为[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget11.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget12 = regexValue(text, "合同金额[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget12.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget13 = regexValue(text, "采购预算[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget13.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget14 = regexValue(text, "项目概算[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)", 1);
    budget14.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget15 = regexValue(text, "中标金额（人民币元）[\\s*|:|：]+(（小写）)*([0-9]+[\\.|,|，]*[0-9]*[\\.|,]*[0-9]*\\s*[\u4E00-\u9FA5]*)", 1);
    budget15.ifPresent(value -> dataMap.put("budget", value));

    Optional<String> budget16 = regexValue(text, "项目预算金额：人民币[\\s*|:|：]+([0-9]+[\\.|,]*[0-9]*[\\.|,]*[0-9]*\\s*[\\u4E00-\\u9FA5]*)",1);
    budget16  .ifPresent(value -> dataMap.put("budget", value));
    //附件地址
    dataMap.put("appendix", "");
    //面包屑索引路径
    try {
      dataMap.put("route", doc.select(".ewb-loc").get(0).text().split("：")[1]);
    } catch (Exception e) {
      dataMap.put("route", "");
    }
    //备用key
    dataMap.put("id", url.url);
  }

  public static void main(String[] args) throws Exception {
    String url;
    url = "https://www.lnsggzy.com/";
    url = "https://www.lnsggzy.com/003/secondpageDeal.html";
    url = "https://www.lnsggzy.com/003/2.html";
    url = "https://www.lnsggzy.com/003/003002/003002004/20200909/b3ced63c-b524-4886-85c6-a392ff1f70b1.html";
    url = "https://www.lnsggzy.com/003/003004/003004001/20200909/8865.html";
    HttpUtils hp = HttpUtils.getInstance();
    CrawlDatum crawlDatum = new CrawlDatum();
    Content content = hp.getProtocolOutput(url, crawlDatum).getContent();
    Outlink outlink = new Outlink(url, "");
    LIAOningshenggonggongziyuanjiaoyipingtaiTermplate parse = new LIAOningshenggonggongziyuanjiaoyipingtaiTermplate();
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
////    JSONObject jsonMap = JSONObject.fromObject(map);
////    System.out.print("bidmodel=" + jsonMap);
  }


}
