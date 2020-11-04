package com.maplecloudy.spider.template;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import com.maplecloudy.spider.parse.AbstractTemplate;
import com.maplecloudy.spider.parse.ParseData;
import com.maplecloudy.spider.schema.Content;
import com.maplecloudy.spider.schema.Outlink;

/**
 * 安徽省政府采购网 解析模板
 *
 * Author kim
 * Date  2020-10-31
 */
public class ANhuishengzhengfucaigouwangTermplate extends AbstractTemplate {
    
  public ANhuishengzhengfucaigouwangTermplate() throws MalformedURLException {
    // 模板名字，请以网址全程为准
    name = "安徽省政府采购网";
    // 爬虫种子页面
    addSeedLink("http://www.ccgp-anhui.gov.cn/", "首页");
    // 爬虫更新的需要的链接
    addUpdateLink("http://www.ccgp-anhui.gov.cn/cmsNewsController/getCgggNewsList.do?channelCode=cggg", "列表第一页");
    addUpdateLink("http://www.ccgp-anhui.gov.cn/cmsNewsController/getCgggNewsList.do?pageNum=2", "列表第二页");
    // 网站所有有的链接类型，以及对应的正则
    addDict("首页", "http://www.ccgp-anhui.gov.cn/", "GET", "utf-8");
    addDict("列表第一页", "http://www.ccgp-anhui.gov.cn/cmsNewsController/getCgggNewsList.do?channelCode=cggg", "GET", "utf-8");
    addDict("列表页", "http://www.ccgp-anhui.gov.cn/cmsNewsController/getCgggNewsList.do\\?pageNum=\\d", "GET", "utf-8");
    addDict("详情页", "http://www.ccgp-anhui.gov.cn/cmsNewsController/cmsNewsDetail.do\\?newsId=.", "GET", "utf-8");

  }
  
  public ParseData parse(Outlink url, Content content, RunMode runMode) {
  	
  	ParseData parseData = new ParseData();
  	if (matches(url, "首页")) {
      genSHOUyeLinks(parseData.outLinks, url, content,runMode);
      genSHOUyeDatas(parseData.dataMap, url, content,runMode);
    } else if (matches(url, "列表第一页")) {
      genLIEbiaodiyiyeLinks(parseData.outLinks, url, content,runMode);
      genLIEbiaodiyiyeDatas(parseData.dataMap, url, content,runMode);
    } else if (matches(url, "列表页")) {
      genLIEbiaoyeLinks(parseData.outLinks, url, content,runMode);
      genLIEbiaoyeDatas(parseData.dataMap, url, content,runMode);
    } else if (matches(url, "详情页")) {
      genXIANGqingyeLinks(parseData.outLinks, url, content,runMode);
      genXIANGqingyeDatas(parseData.dataMap, url, content,runMode);
    }
  	return parseData;
  }
  
  //从[首页]提取链接
  public void genSHOUyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) {
    if(!(runMode == RunMode.BOTH || runMode == RunMode.FETCH) )
      return;

  }

  //从[首页]提取数据
  public void genSHOUyeDatas(Map<String,String> dataMap, Outlink url, Content content, RunMode runMode) {
    if(!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表第一页]提取链接
  public void genLIEbiaodiyiyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) {
    if(!(runMode == RunMode.BOTH || runMode == RunMode.FETCH) )
      return;

  }

  //从[列表第一页]提取数据
  public void genLIEbiaodiyiyeDatas(Map<String,String> dataMap, Outlink url, Content content, RunMode runMode) {
    if(!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[列表页]提取链接
  public void genLIEbiaoyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) {
    if(!(runMode == RunMode.BOTH || runMode == RunMode.FETCH) )
      return;

  }

  //从[列表页]提取数据
  public void genLIEbiaoyeDatas(Map<String,String> dataMap, Outlink url, Content content, RunMode runMode) {
    if(!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }

  //从[详情页]提取链接
  public void genXIANGqingyeLinks(List<Outlink> outlinks, Outlink url, Content content, RunMode runMode) {
    if(!(runMode == RunMode.BOTH || runMode == RunMode.FETCH) )
      return;

  }

  //从[详情页]提取数据
  public void genXIANGqingyeDatas(Map<String,String> dataMap, Outlink url, Content content, RunMode runMode) {
    if(!(runMode == RunMode.BOTH || runMode == RunMode.PARSE))
      return;

  }


}
