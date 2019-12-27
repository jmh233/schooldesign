package com.calculate.number_demo.Service;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class Check {
    private static final String POST_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token=" + AuthService.getAuth();

    /**
     * 识别本地图片的文字
     */
    public static String checkFile(String path) throws URISyntaxException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new NullPointerException("图片不存在");
        }
        String image = BaseImg.getImageStrFromPath(path);
        String param = "id_card_side=" + "front" + "&image=" + image;
        return post(param);
    }

    /**
     * 图片url
     * 识别结果，为json格式
     */
    public static String checkUrl(String url) throws IOException, URISyntaxException {
        String param = "url=" + url;
        return post(param);
    }

    /**
     * 通过传递参数：url和image进行文字识别
     */
    private static String post(String param) throws URISyntaxException, IOException {
        //开始搭建post请求
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost();
        URI url = new URI(POST_URL);
        post.setURI(url);
        //设置请求头，请求头必须为application/x-www-form-urlencoded，因为是传递一个很长的字符串，不能分段发送
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        StringEntity entity = new StringEntity(param);
        post.setEntity(entity);
        HttpResponse response = httpClient.execute(post);
        System.out.println(response.toString());
        if (response.getStatusLine().getStatusCode() == 200) {
            String str;
            String key="";
            try {
                //读取服务器返回过来的json字符串数据
                str = EntityUtils.toString(response.getEntity());
                System.out.println(str);

                JSONObject responseBody=new JSONObject(str);
                Map<String, Object> map= responseBody.toMap();
                Map<String,Object> words_result= (Map<String, Object>) map.get("words_result");
//
//                String ss = "公民身份证号码";
//                String idnum = new String(ss.getBytes("UTF-8"),"utf-8");
                String idnum = null;
                for(Map.Entry<String, Object> entry : words_result.entrySet()){
                    String mapKey = entry.getKey();
                    if (mapKey.equals("公民身份证号码")){
                        idnum= (String) entry.getValue();
                        break;
                    }
//                    String mapValue = (String) entry.getValue();

                }
                System.out.println(idnum);

//
//                JSONArray arr= ((JSONObject) responseBody).getJSONArray("words");
//                for (int i=0;i<arr.length();i++){
//                    String st=arr.getJSONObject(i).getString("公民身份号码");
//                    key+=st;
//                }
                System.out.println(key);
                return key;
//                System.out.println(jsonObject.getString("words_result"));

//                return str;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String path = "C:\\Users\\teague.jiang\\IdeaProjects\\number_demo\\src\\main\\java\\com\\calculate\\number_demo\\Service\\demo2.jpg";
        try {
            long now = System.currentTimeMillis();
            checkFile(path);
            System.out.println("耗时：" + (System.currentTimeMillis() - now) / 1000 + "s");
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
