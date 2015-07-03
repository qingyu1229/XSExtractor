package org.xs.spider.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by 梁擎宇 on 15-7-3.
 */
public class WangyiParser extends BasicParser {


    /**
     * 假设现在抓取163的新闻时，不能够准确定位，
     * 于是重写一个叫WangyiParser的类，继承自BasicParser，
     * 重写BasicParser中定位正文的方法
     */

    public WangyiParser(){
        //将自己注册到ParserLocator中
        System.out.println("wangyi");
        ParserLocator.getInstance().register("news.163.com",this);
    }

    @Override
    public Element excavateContent(Document document) {
        return document.getElementById("endText");
    }
}
