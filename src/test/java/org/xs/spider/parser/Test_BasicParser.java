package org.xs.spider.parser;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 15-5-29.
 */
public class Test_BasicParser {


    @Test
    public void test1(){
        String url="http://news.ifeng.com/a/20150529/43860689_0.shtml";
        try {
            Document document= Jsoup.connect(url).get();
            BasicParser parser=new BasicParser();
            String content= parser.getContent(document);

            FileUtils.write(new File("d:/textContent.html"),content,"gbk");

            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
