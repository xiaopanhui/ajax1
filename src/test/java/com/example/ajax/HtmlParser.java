package com.example.ajax;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.text.AbstractDocument;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Jsoup解析html标签时类似于JQuery的一些符号
 *
 * @author chixh
 *
 */
public class HtmlParser {
    protected List<List<String>> data = new LinkedList<List<String>>();

    /**
     * 获取value值
     *
     * @param e
     * @return
     */
    public static String getValue(Element e) {
        return e.attr("value");
    }

    /**
     * 获取
     * <tr>
     * 和
     * </tr>
     * 之间的文本
     *
     * @param e
     * @return
     */
    public static String getText(Element e) {
        return e.text();
    }

    /**
     * 识别属性id的标签,一般一个html页面id唯一
     *
     * @param body
     * @param id
     * @return
     */
    public static Element getID(String body, String id) {
        Document doc = Jsoup.parse(body);
        // 所有#id的标签
        Elements elements = doc.select("#" + id);
        // 返回第一个
        return elements.first();
    }

    /**
     * 识别属性class的标签
     *
     * @param body
     * @return
     */
    public static Elements getClassTag(String body, String classTag) {
        Document doc = Jsoup.parse(body);
        // 所有#id的标签
        return doc.select("." + classTag);
    }

    /**
     * 获取tr标签元素组
     *
     * @param e
     * @return
     */
    public static Elements getTR(Element e) {
        return e.getElementsByTag("tr");
    }

    /**
     * 获取td标签元素组
     *
     * @param e
     * @return
     */
    public static Elements getTD(Element e) {
        return e.getElementsByTag("td");
    }
    /**
     * 获取表元组
     * @param table
     * @return
     */
    public static List<List<String>> getTables(Element table){
        List<List<String>> data = new ArrayList<>();

        for (Element etr : table.select("tr")) {
            List<String> list = new ArrayList<>();
            for (Element etd : etr.select("td")) {
                String temp = etd.text();
                //增加一行中的一列
                list.add(temp);
            }
            //增加一行
            data.add(list);
        }
        return data;
    }
    /**
     * 读html文件
     * @param fileName
     * @return
     */
    public static String readHtml(String fileName){
        FileInputStream fis = null;
        StringBuffer sb = new StringBuffer();
        try {
            fis = new FileInputStream(fileName);
            byte[] bytes = new byte[1024];
            while (-1 != fis.read(bytes)) {
                sb.append(new String(bytes));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sb.toString();
    }
    public  static String paseHtml(String str ) {
//        String  str="http://www.baidu.com";
        File file = null;

        try {
            URL url = new URL( str);
            InputStream in = url.openStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader bufr = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String detail="";
            while ((detail = bufr.readLine()) != null) {
                str= sb.append(detail).toString();
            }
             file = new File("./temporary.html");
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(str.getBytes("UTF-8"));

            bufr.close();
            isr.close();
            in.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(file);

    }

    public static void main(String[] args) throws IOException {
        String url = "http://mil.news.sina.com.cn/roll/index.d.html?cid=57918&tdsourcetag=s_pctim_aiomsg&qq-pf-to=pcqq.group";
        String filename = paseHtml(url);
        Document doc = Jsoup.parse(readHtml( filename));
        Elements links = doc.select("a[href]"); //带有href属性的a元素
        Elements elements = doc.select(".fixList").select(".linkNews").select("li");
        //扩展名为.png的图片
        Elements pngs = doc.select("img[src$=.png]");
        Element masthead = doc.select("div.masthead").first();
        String data = "";
        StringBuffer sb1 = new StringBuffer();
        for (Element e : elements) {
            data=sb1.append(e.text()).toString();

        }

        Map<String, String> map = new HashMap<>();
        map.put("data",data);
        JSONObject jsonObject = JSONObject.fromObject(map);
//        //3、将json对象转化为json字符串
        String result = jsonObject.toString();
//        System.out.println( result );


        Document doc2 = Jsoup.parse(readHtml("./rtnerror.html"));
//        Elements tables = doc2.select("table");
//        System.out.println(tables.size());
        Element table = doc2.select("table").get(25);
        StringBuffer sb = new StringBuffer();

        Map<String,String> mapx=new HashMap<>();
        String detail="";
        List<List<String>> list = getTables(table);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("respCode","200");
        jsonObject1.put("respDesc","调用成功!");
        JSONArray jsonArray = new JSONArray();
        for (List<String> list2 : list) {
            if(list2.size()>1&&!list2.get(0).equals(" ")){//判断是否有数据
                JSONObject jsonObject2 = new JSONObject();
                String[] listname = {"序号","单位编号","单位名称","个人编号","姓名","身份证号码(证件号码)","IC卡编号","险种类型","参保状态","人员状态","缴费工资","缴费基数","出生日期","参加工作日期","参保时间","个人身份","用工形式","所占编制","行政级别","技术职称","是否农民工标志","是否劳模标志","基本养老保险视同缴费年限","联系地址","邮政编码","企业内编号","联系电话"};
                for (int i=0;i<listname.length;i++){
                    jsonObject2.put(listname[i],list2.get(i));
                }
                jsonArray.add(jsonObject2);

//                System.out.println(jsonObject2);
            }

        }
        jsonObject1.put("data",jsonArray);
        System.out.println(jsonObject1);

        //整个转为data
//        map1.put("data", detail);
//        JSONObject jsonObject1 = JSONObject.fromObject( map1);
       //3、将json对象转化为json字符串
//       String result1 = jsonObject1.toString();

//        System.out.println( result1);
    }


}
