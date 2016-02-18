# XSExtractor
提取新闻、博客等长文本网页的正文工具

  在和许多开发爬虫，特别是抓取新闻资讯等网页的爬虫开发者交流的时候，发现很多人对于怎样去提取页面中的信息感到无从下手。
因此我结合自己在开发爬虫项目时候的用到的一些技术和技巧，将抽取页面信息的主要模块开源出来，供大家参考，
做得不足的地方欢迎拍砖。

先谈谈自己爬虫项目的背景：
 
  项目主要的功能是实时采集经济、财经、政策等咨询和新闻信息（共一万多个信息源），将这些抓取到新闻资讯抽取出来结构化，
然后存储到关系型数据库中，然后再对这些抓取到的新闻资讯进行基于内容的去重、提取关键词、分类等离线处理，最后通过推送引擎推送到各个下游系统以及全文检索系统中。

开发过程中遇到的问题：
>
-  1、信息源太多，每个网站的结构都不一样，怎样从不同结构的网页中抽取需要的新闻信息（新闻标题、新闻时间、新闻内容等）
-  2、需要保存新闻的部分格式，便于将新闻资讯转到富文本编辑器中进行二次编辑。
-  3、需要将新闻资讯中的图片下载下来保存到本地图片服务器。
-  4、如果新闻分页，需要将新闻采集完整。
-  5、由于需要将新闻直接推送（不经过人工编辑）至手机新闻App等系统，所以采集到的新闻内容中不能含有任何杂质，
例如：广告图片、推荐链接、推荐阅读、免责声明、关注微信、来源说明、版权说明、分页标识等。同时，由于财经类型新闻可能含有股票代码
和股票价格等信息，为了不让用户在看到我们的新闻的时候对股票价格有误解，因此新闻中的股票代码和股票价格也需要去除。

综合这些需求，我们做了三种方案：
>
- 1、能不能做一个通用的解析器，能够解析所有的网页。答案是不可能。网上所有的抽取算法找了个遍，最高的号称准确率95%，但是在我们这种需求下，70%的准确率都不到，而且几乎每篇都含有杂质。
- 2、每一个网站定义一种解析规则。问题来了，如此繁重的任务谁来做？以后添加信息源也只能由开发人员添加。
- 3、依据需求定义自己的通用抽取解析器，不能准确解析的再进行扩展。

很明显，我们采用了第三种方案。

XSExtractor是我从采集系统中分离出来的抽取器（做了一些改变），采用一种基于页面文本密度的算法进行抽取（具体算法的实现我以后会在我的博客中介绍，
博客地址：[liangqingyu.com](http://www.liangqingyu.com)）。Html页面采用Jsoup进行格式化和结构化。希望能够帮助一部分人。
具体使用方法：
>
    //Document document = Jsoup.connect(url).get();
    //OR
    //Document document = Jsoup.parse(htmlStr);
    BasicParser parser = new BasicParser();
    String content = parser.getContent(document);
    //String text=parser.getContentText(document); //获取纯文本

  或者：
>
    //Document document = Jsoup.connect(url).get();
    //OR
    //Document document = Jsoup.parse(htmlStr);
    Parser parser= ParserLocator.getInstance().getParser(url);
    String content = parser.getContent(document);
    //String text=parser.getContentText(document); //获取纯文本

推荐使用第二种

前面说过，任何一种算法都不能100%抽取准确，而实际商业生产环境中又不允许出现错误。于是，当某个网站的新闻采集不够准确时可以采用下面的处理方法。
例如当采集网易新闻抓取不准确时：
写一个叫WangyiParser的类，继承自BasicParser，重写BasicParser中定位正文的方法，同时在构造方法中将自己注册到ParserLocator中，如下：
>
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

>
    //使用时
    new WangyiParser();//此处为测试，故需要new出对象进行注册，实际环境中可以采用Spring扫面包将所有重写的解析类实例化（注册）
    String url = "http://news.163.com/15/0703/00/ATIEDCLF00014JB6.html";
    try {
        Document document = Jsoup.connect(url).get();
        Parser parser= ParserLocator.getInstance().getParser(url);
        String content = parser.getContent(document);
        System.out.println(content);
    } catch (IOException e) {
    e.printStackTrace();
    }


XSExtractor尚未写完，还有很多其他问题需要解决，也欢迎大家献计献策。
关于爬虫开发以及开源爬虫系统的研究，感兴趣的话可以加QQ群：235971260 
一起讨论爬虫开发过程中遇到的问题，也欢迎HR们加入。   



