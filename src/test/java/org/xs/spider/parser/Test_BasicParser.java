package org.xs.spider.parser;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.xs.spider.rating.Rating;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 15-5-29.
 */
public class Test_BasicParser {


    @Test
    public void test1() {
        //http://www.cbrhq.gov.cn/Item/13014.aspx

        String url = "http://www.cbrhq.gov.cn/Item/13014.aspx";
        try {
            Document document = Jsoup.connect(url).get();
            BasicParser parser = new BasicParser();
            String content = parser.getContent(document);
            FileUtils.write(new File("d:/textContent.html"), content, "gbk");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() {
        Document doc = Jsoup.parse("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "                          <tbody><tr>\n" +
                "                            <td><p>&nbsp;&nbsp;&nbsp; 春季是动物疫病多发季节，万合永镇政府结合动物防疫工作实际，采取有力措施，切实抓好口蹄疫、禽流感等重大动物疫病防治，控制重大动物疫病发生，确保畜牧业生产健康发展，打好春季动物防疫攻坚战。<br>\n" +
                "&nbsp;&nbsp;&nbsp; 一是精心安排组织，明确工作责任。镇政府及时组织召开春季动物防疫专题会议，全面安排部署全镇春季动物防疫工作，强化责任追究制度，做好督促检查工作，保证动物防疫工作的顺利完成。<br>\n" +
                "&nbsp;&nbsp;&nbsp; 二是完善防控措施，确保免疫密度。组织全镇防疫工作力量分组包片实行拉网式集中免疫，规范防疫行为，严格操作程序，坚持做到一畜一针，杜绝空针和飞针现象。同时，认真做好免疫档案登记和管理，适时查缺补漏，做到不漏一户、一畜、一禽，对集中免疫时不宜免疫的畜禽、新生及新补栏畜禽定期及时进行补免，确保禽畜免疫率100%。<br>\n" +
                "&nbsp;&nbsp;&nbsp; 三是加强宣传监测，提高预警能力。大力宣传和科普动物防疫知识和技术，增强群众对防疫工作重要性的认识和应急处理能力。同时，完善疫情报告网络体系，建立健全巡查制度和定点、定人、包片负责的疫情监测制度，随时掌握疫情动态。<br>\n" +
                "&nbsp;&nbsp;&nbsp; 目前，我镇口蹄疫、羊痘、禽流感、猪瘟、猪蓝耳病等疫（菌）苗已全面免疫接种，共防疫生猪5800口，羊10.8万只，牛2600头，禽类1.7万羽，全镇春季动物防疫工作已经全面完成。</p></td>\n" +
                "                          </tr>\n" +
                "                        </tbody></table>");

       // Rating.doTextRate(doc.body());
        //doScoreToElement(doc.body());
        System.out.println(doc.body().toString());
    }


    private int doScoreToElement(Element element) {
        boolean hasText = element.hasText();

        Elements children = element.children();
        if (children.size() == 0) {//不含有子节点
            return Rating.doRate(element);
        } else {//含有子节点
            int accum = 0;
            for (Element child : children) {
                accum += doScoreToElement(child);
            }
            element.attr("score", String.valueOf(accum));
            return accum;

        }
    }
}
