package com.moye.crawler.spider.meishi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.moye.crawler.spider.utils.HttpClientUtil;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/23 9:36
 * @modified By
 */
public class PageParse {

    private static HttpClientUtil httpClientUtil = new HttpClientUtil();

    public static List<String> getPageList(){
        List<String> idList = Lists.newArrayList();
        int offset = 0;
        int pagesize = 32;
        int limit = 1;
        String url = "";
        String response = "";
        try{
            for(int page = 1 ; page < 23 ; page ++){
                offset = (page - 1) * pagesize;
                limit = pagesize;
                url = "http://apimobile.meituan.com/group/v4/poi/pcsearch/253?uuid=3c52519a6abb4359be63.1527037848.1.0.0" +
                        "&userid=-1&limit="+limit+"&offset="+offset+"&cateId=-1&q=%E7%83%A7%E7%83%A4";
                response =  httpClientUtil.get( url );
                idList.addAll( parseId( response ) );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       return idList;
    }
    private static List<String> parseId(String jsonStr){
        List<String> idList = Lists.newArrayList();
        JSONObject jsonObject = JSONObject.parseObject( jsonStr );
        JSONObject data = jsonObject.getJSONObject( "data" );
        JSONArray array = data.getJSONArray( "searchResult" );
        array.forEach( obj ->{
            JSONObject meishi = (JSONObject)obj;
            idList.add( meishi.get( "id" ).toString() );
        } );
        return idList;
    }

    public static void main(String[] args) {
       List<String> ids =  PageParse.getPageList();
        System.out.println(ids.size());
    }
}
