package com.maplecloudy.spider.template;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import com.maplecloudy.spider.parse.AbstractTemplate;
import com.maplecloudy.spider.parse.ParseData;
import com.maplecloudy.spider.schema.Content;
import com.maplecloudy.spider.schema.Outlink;

/**
 * ${Remarks}
 *
 * Author ${Author}
 * Date  ${Date}
 */
public class ${ClassName} extends AbstractTemplate {
    
  public ${ClassName}() throws MalformedURLException {
    ${init}
  }
  
  @Override
  public ParseData parse(Outlink url, Content content, RunMode runMode) {
  	
  	ParseData parseData = new ParseData();
  	${parse}
  	return parseData;
  }
  
${method}
}
