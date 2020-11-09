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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 云南政府采购 解析模板
 * <p>
 * Author yanzhen
 * Date  2020-11-03
 */
public class YUNnanzhengfucaigouTermplateTmp extends AbstractTemplate {

  public YUNnanzhengfucaigouTermplateTmp() throws MalformedURLException {
    // 模板名字，请以网址全程为准
    name = "云南政府采购";
    // 爬虫种子页面
    addSeedLink("http://www.yngp.com/", "首页");
    // 爬虫更新的需要的链接
    addUpdateLink("http://www.yngp.com/bulletin.do?method=moreListQuery&page=1", "列表第一页");
    addUpdateLink("http://www.yngp.com/bulletin.do?method=moreListQuery&page=2", "列表第二页");
    // 网站所有有的链接类型，以及对应的正则
    addDict("首页", "http://www.yngp.com/", "GET", "gbk");
    addDict("列表第一页", "http://www.yngp.com/bulletin.do\\?method=moreListQuery&page=1", "POST", "gbk");
    addDict("列表页", "http://www.yngp.com/bulletin.do\\?method=moreListQuery&page=\\d+", "POST", "gbk");
    addDict("详情页", "http://www.yngp.com/newbulletin_zz.do\\?method=preinsertgomodify&operator_state=1&flag=view&bulletin_id=.+", "GET", "gbk");

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

  //从[首页]提取链接
  public void genSHOUyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) throws IOException {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.FETCH))
      return;

    Outlink outlink = new Outlink("http://www.yngp.com/bulletin.do?method=moreListQuery&page=1","");
    HttpParameters httpParameters = new HttpParameters();
    httpParameters.setType("post");
    BasicNameValuePair[] data = {
        new BasicNameValuePair("current", "1"),
        new BasicNameValuePair("rowCount", "10"),
        new BasicNameValuePair("searchPhrase", ""),
        new BasicNameValuePair("query_sign", "1")};
    String entity = new ObjectMapper().writeValueAsString(data);
    httpParameters.getMap().put("x-www-form-urlencoded", entity);
    httpParameters.setContentType(
        "application/x-www-form-urlencoded; charset=gbk");
    httpParameters.setMethod("http");
    outlink.addExtend(httpParameters.getMap());
    outlinks.add(outlink);
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

    String html = new String(content.getContent(), "gbk");
    System.out.println(html);
    JSONObject json = new JSONObject(html);
    JSONArray detail = json.getJSONArray("rows");
//        System.out.println(detail.length());
    for (int i = 0; i < detail.length(); i++) {
      String bulletinId = detail.getJSONObject(i).getString("bulletin_id");
      Outlink outlink = new Outlink(bulletinId,"");
      outlinks.add(outlink);
    }
    int totlePageCount = json.getInt("totlePageCount");
    System.out.println(totlePageCount);
    for (int i = 2; i <= totlePageCount; i++) {
      Outlink outlink = new Outlink("http://www.yngp.com/bulletin.do?method=moreListQuery&page=" + i,"");
      HttpParameters httpParameters = new HttpParameters();
      httpParameters.setType("post");
      BasicNameValuePair[] data = {
          new BasicNameValuePair("current", String.valueOf(i)),
          new BasicNameValuePair("rowCount", "10"),
          new BasicNameValuePair("searchPhrase", ""),
          new BasicNameValuePair("query_sign", "1")};
      String entity = new ObjectMapper().writeValueAsString(data);
      httpParameters.getMap().put("x-www-form-urlencoded", entity);
      httpParameters.setContentType(
          "application/x-www-form-urlencoded; charset=gbk");
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

    String html = new String(content.getContent(), "gbk");
    JSONObject json = new JSONObject(html);
    JSONArray detail = json.getJSONArray("rows");
    for (int i = 0; i < detail.length(); i++) {
      String bulletinId = detail.getJSONObject(i).getString("bulletin_id");
      Outlink outlink = new Outlink("http://www.yngp.com/newbulletin_zz.do?method=preinsertgomodify&operator_state=1&flag=view&bulletin_id=" + bulletinId,"");
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
  public void genXIANGqingyeDatas(Map<String, String> dataMap, Outlink url, Content content, RunMode runMode) {
    if (!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;


  }

  public static void main(String[] args) throws Exception {
    String url;
    url = "http://www.yngp.com/";
    url = "http://www.yngp.com/bulletin.do?method=moreListQuery&page=1";
//    url = "http://www.yngp.com/newbulletin_zz.do?method=preinsertgomodify&operator_state=1&flag=view&bulletin_id=3f2ffd57.17457de230d.-76d9";
//    url = "http://www.yngp.com/newbulletin_zz.do?method=preinsertgomodify&operator_state=1&flag=view&bulletin_id=3f2ffd57.17457de230d.-76d9";
//    url = "http://www.yngp.com/newbulletin_zz.do?method=preinsertgomodify&operator_state=1&flag=view&bulletin_id=3f2ffd57.17457de230d.-76d9";
//    url = "http://www.yngp.com/newbulletin_zz.do?method=preinsertgomodify&operator_state=1&flag=view&bulletin_id=5903a0df.16b461ab2dc.-7f6c";
    HttpUtils hp = HttpUtils.getInstance();
    CrawlDatum crawlDatum = new CrawlDatum();
    HttpParameters httpParameters = new HttpParameters();
    httpParameters.setType("post");
    BasicNameValuePair[] data = {
        new BasicNameValuePair("current", "2"),
        new BasicNameValuePair("rowCount", "10"),
        new BasicNameValuePair("searchPhrase", ""),
        new BasicNameValuePair("query_sign", "1")};
    String entity = new ObjectMapper().writeValueAsString(data);
    httpParameters.getMap().put("x-www-form-urlencoded", entity);
    httpParameters.setContentType("application/x-www-form-urlencoded; charset=gbk");
    httpParameters.setMethod("http");
    crawlDatum.setExtendData(httpParameters.getMap());
    Content content = hp.getProtocolOutput(url, crawlDatum).getContent();
    String s = new String(content.getContent(), "gbk");
    


    Outlink outlink = new Outlink(url, "");
    YUNnanzhengfucaigouTermplateTmp parse = new YUNnanzhengfucaigouTermplateTmp();
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
