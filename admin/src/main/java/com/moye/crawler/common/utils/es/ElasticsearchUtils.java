package com.moye.crawler.common.utils.es;//package com.infinite.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.gson.*;
import com.moye.crawler.common.constant.CodeConstant;
import com.moye.crawler.core.spider.common.Webpage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.moreLikeThisQuery;


/**
 * 描述: Elasticsearch 工具类
 *
 * @author yanpenglei
 * @create 2017-11-04 11:20
 **/
@Component
public class ElasticsearchUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchUtils.class);

    @Autowired
    private TransportClient transportClient;

    private static TransportClient client;

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()))
            .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) -> new JsonPrimitive(src.getTime()))
            .setDateFormat(DateFormat.LONG).create();

    @PostConstruct
    public void init() {
        client = this.transportClient;
    }

    public static boolean checkType(String index, String type, String mapping) {
        if (client == null){ return false;}
        if (!client.admin().indices().typesExists(new TypesExistsRequest(new String[]{index}, type)).actionGet().isExists()) {
            LOGGER.info(type + " type不存在,正在准备创建type");
            File mappingFile;
            try {
                mappingFile = new File(ClassUtils.getDefaultClassLoader().getResource(mapping).getFile());
            } catch (Exception e) {
                LOGGER.info("查找ES mapping配置文件出错, " + e.getLocalizedMessage());
                return false;
            }
            LOGGER.debug(type + " MappingFile:" + mappingFile.getPath());
            PutMappingResponse mapPuttingResponse = null;

            PutMappingRequest putMappingRequest = null;
            try {
                putMappingRequest = Requests.putMappingRequest(index).type(type).source(FileUtils.readFileToString(mappingFile));
            } catch (IOException e) {
                LOGGER.error("创建 jvmSample mapping 失败," + e.getLocalizedMessage());
            }
            mapPuttingResponse = client.admin().indices().putMapping(putMappingRequest).actionGet();

            if (mapPuttingResponse.isAcknowledged()) LOGGER.info("创建" + type + "type成功");
            else {
                LOGGER.error("创建" + type + "type索引失败");
                return false;
            }
        } else LOGGER.debug(type + " type 存在");
        return true;
    }
    public static boolean checkIndex(String index, String mapping) {
        if (client == null) return false;
        if (!client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet().isExists()) {
            File indexMappingFile;
            try {
                indexMappingFile = new File(ClassUtils.getDefaultClassLoader().getResource(mapping).getFile());
            } catch (Exception e) {
                LOGGER.info("查找" + index + "index mapping配置文件出错, " + e.getLocalizedMessage());
                return false;
            }
            LOGGER.debug(index + "index MappingFile:" + indexMappingFile.getPath());
            LOGGER.info(index + " index 不存在,正在准备创建index");
            CreateIndexResponse createIndexResponse = null;
            try {
                createIndexResponse = client.admin().indices()
                        .prepareCreate(index)
                        .setSettings(FileUtils.readFileToString(indexMappingFile))
                        .execute().actionGet();
            } catch (IOException e) {
                LOGGER.error("创建 " + index + " index 失败");
                return false;
            }
            if (createIndexResponse.isAcknowledged()) LOGGER.info(index + " index 成功");
            else {
                LOGGER.info(index + " index失败");
                return false;
            }
        } else LOGGER.debug(index + " index 存在");
        return true;
    }

    /**
     * 创建索引
     *
     * @param index
     * @return
     */
    public static boolean createIndex(String index) {
        if (isIndexExist(index)) {
            LOGGER.info("Index is  exits!");
            return true;
        }
        LOGGER.info("Index is  exits!");

        CreateIndexResponse indexresponse = client.admin().indices().prepareCreate(index).execute().actionGet();
        LOGGER.info("执行建立成功？" + indexresponse.isAcknowledged());

        return indexresponse.isAcknowledged();
    }

    public static boolean prepareIndex(String indexName, String typeName, Webpage webpage){
        if (isIndexExist(indexName)) {
            LOGGER.info("Index is  exits!");
            addData((JSONObject)JSONObject.toJSON(webpage),indexName,typeName,Hashing.md5().hashString(webpage.getUrl(), Charset.forName("utf-8")).toString());
            return true;
        }
        LOGGER.info("Index is  exits!");
        try{
            IndexResponse indexResponse = client.prepareIndex(indexName, typeName)
                    .setId(Hashing.md5().hashString(webpage.getUrl(), Charset.forName("utf-8")).toString())
                    .setSource(JSONObject.toJSONString(webpage))
                    .get();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.info("索引建立失败" );
            return false;
        }
    }


    /**
     * 删除索引
     *
     * @param index
     * @return
     */
    public static boolean deleteIndex(String index) {
        if (!isIndexExist(index)) {
            LOGGER.info("Index is not exits!");
        }
        DeleteIndexResponse dResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
        if (dResponse.isAcknowledged()) {
            LOGGER.info("delete index " + index + "  successfully!");
        } else {
            LOGGER.info("Fail to delete index " + index);
        }
        return dResponse.isAcknowledged();
    }

    /**
     * 根据条件删除索引
     * @param index
     * @param type
     * @param param
     */
    public static boolean deleteByTerm(String index, String type, Map<String, String> param){
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        param.entrySet().forEach(item ->{
            boolQuery.must(QueryBuilders.matchPhraseQuery(item.getKey(), item.getValue()));
        });
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQuery).setExplain(true).execute().actionGet();
        System.out.println(response.getHits().totalHits());
        for(SearchHit hit : response.getHits()){
            String id = hit.getId();
            System.out.println(id);
            //bulkRequest.add(client.prepareDelete(index, type, id).request());
            deleteDataById(index, type, id);
        }
        return true;

    }
    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    public static boolean isIndexExist(String index) {
        IndicesExistsResponse inExistsResponse = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        if (inExistsResponse.isExists()) {
            LOGGER.info("Index [" + index + "] is exist!");
        } else {
            LOGGER.info("Index [" + index + "] is not exist!");
        }
        return inExistsResponse.isExists();
    }

    /**
     * 数据添加，正定ID
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public static String addData(JSONObject jsonObject, String index, String type, String id) {

        IndexResponse response = client.prepareIndex(index, type, id).setSource(jsonObject).get();

        LOGGER.info("addData response status:{},id:{}", response.status().getStatus(), response.getId());

        return response.getId();
    }

    /**
     * 数据添加
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @return
     */
    public static String addData(JSONObject jsonObject, String index, String type) {
        return addData(jsonObject, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }


    /**
     * 通过ID删除数据
     *
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     */
    public static void deleteDataById(String index, String type, String id) {

        DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();

        LOGGER.info("deleteDataById response status:{},id:{}", response.status().getStatus(), response.getId());
    }



    /**
     * 通过ID 更新数据
     *
     * @param jsonObject 要增加的数据
     * @param index      索引，类似数据库
     * @param type       类型，类似表
     * @param id         数据ID
     * @return
     */
    public static void updateDataById(JSONObject jsonObject, String index, String type, String id) {

        UpdateRequest updateRequest = new UpdateRequest();

        updateRequest.index(index).type(type).id(id).doc(jsonObject);

        client.update(updateRequest);

    }

    /**
     * 判断制定id是否存在
     * @param index
     * @param type
     * @param id
     * @return
     */
    public static boolean isExists(String index, String type, String id){
        GetResponse response = client.prepareGet(index, type, id ).get();
        return response.isExists();
    }

    /**
     * 通过ID获取数据
     *
     * @param index  索引，类似数据库
     * @param type   类型，类似表
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    public static Map<String, Object> searchDataById(String index, String type, String id, String fields) {

        GetRequestBuilder getRequestBuilder = client.prepareGet(index, type, id);

        if (StringUtils.isNotEmpty(fields)) {
            getRequestBuilder.setFetchSource(fields.split(","), null);
        }

        GetResponse getResponse = getRequestBuilder.execute().actionGet();

        return getResponse.getSource();
    }

    /**
     * 使用分词查询
     *
     * @param index     索引名称
     * @param type      类型名称,可传入多个type逗号分隔
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param size      文档大小限制
     * @param matchStr  过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, long startTime, long endTime, Integer size, String matchStr) {
        return searchListData(index, type, startTime, endTime, size, null, null, false, null, matchStr);
    }

    /**
     * 使用分词查询
     *
     * @param index    索引名称
     * @param type     类型名称,可传入多个type逗号分隔
     * @param size     文档大小限制
     * @param fields   需要显示的字段，逗号分隔（缺省为全部字段）
     * @param matchStr 过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String matchStr) {
        return searchListData(index, type, 0, 0, size, fields, null, false, null, matchStr);
    }

    /**
     * 使用分词查询
     *
     * @param index       索引名称
     * @param type        类型名称,可传入多个type逗号分隔
     * @param size        文档大小限制
     * @param fields      需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField   排序字段
     * @param matchPhrase true 使用，短语精准匹配
     * @param matchStr    过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String sortField, boolean matchPhrase, String matchStr) {
        return searchListData(index, type, 0, 0, size, fields, sortField, matchPhrase, null, matchStr);
    }


    /**
     * 使用分词查询
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {
        return searchListData(index, type, 0, 0, size, fields, sortField, matchPhrase, highlightField, matchStr);
    }


    /**
     * 使用分词查询
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static List<Map<String, Object>> searchListData(String index, String type, long startTime, long endTime, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (startTime > 0 && endTime > 0) {
            boolQuery.must(QueryBuilders.rangeQuery("timestamp")
                    .format("epoch_millis")
                    .from(startTime)
                    .to(endTime)
                    .includeLower(true)
                    .includeUpper(true));
        }

        //搜索的的字段
        if (StringUtils.isNotEmpty(matchStr)) {
            for (String s : matchStr.split(",")) {
                String[] ss = s.split("=");
                if (ss.length > 1) {
                    if (matchPhrase == Boolean.TRUE) {
                        boolQuery.must(QueryBuilders.matchPhraseQuery(s.split("=")[0], s.split("=")[1]));
                    } else {
                        boolQuery.must(QueryBuilders.matchQuery(s.split("=")[0], s.split("=")[1]));
                    }
                }
            }
        }

        // 高亮（xxx=111,aaa=222）
        if (StringUtils.isNotEmpty(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();

            //highlightBuilder.preTags("<span style='color:red' >");//设置前缀
            //highlightBuilder.postTags("</span>");//设置后缀

            // 设置高亮字段
            highlightBuilder.field(highlightField);
            searchRequestBuilder.highlighter(highlightBuilder);
        }


        searchRequestBuilder.setQuery(boolQuery);

        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }
        searchRequestBuilder.setFetchSource(true);

        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

        if (size != null && size > 0) {
            searchRequestBuilder.setSize(size);
        }

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        LOGGER.info("\n{}", searchRequestBuilder);

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        long totalHits = searchResponse.getHits().totalHits();
        long length = searchResponse.getHits().getHits().length;

        LOGGER.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            return setSearchResponse(searchResponse, highlightField);
        }

        return null;

    }

    /**
     * 使用分词查询,并分页
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param currentPage    当前页
     * @param pageSize       每页显示条数
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（xxx=111,aaa=222）
     * @return
     */
    public static EsPage searchDataPage(String index, String type, int currentPage, int pageSize, long startTime, long endTime, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);

        // 需要显示的字段，逗号分隔（缺省为全部字段）
        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }

        //排序字段
        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (startTime > 0 && endTime > 0) {
            boolQuery.must(QueryBuilders.rangeQuery("timestamp")
                    .format("epoch_millis")
                    .from(startTime)
                    .to(endTime)
                    .includeLower(true)
                    .includeUpper(true));
        }

        // 查询字段
        if (StringUtils.isNotEmpty(matchStr)) {
            for (String s : matchStr.split(",")) {
                String[] ss = s.split("=");
                if (matchPhrase == Boolean.TRUE) {
                    boolQuery.must(QueryBuilders.matchPhraseQuery(s.split("=")[0], s.split("=")[1]).slop( 3 ));
                } else {
                    boolQuery.must(QueryBuilders.matchQuery(s.split("=")[0], s.split("=")[1]));
                }
            }
        }

        // 高亮（xxx=111,aaa=222）
        if (StringUtils.isNoneBlank(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();

            // 设置高亮字段
            highlightBuilder.field(highlightField);
            highlightBuilder.requireFieldMatch(false);
            highlightBuilder.preTags("<span style=\"color:red\">");
            highlightBuilder.postTags("</span>");
            searchRequestBuilder.highlighter(highlightBuilder);
            searchRequestBuilder.highlighter(highlightBuilder);
        }
//        //设置高亮显示
//        if(StringUtils.isNoneBlank(highlightField)){
//            //设置高亮显示
//            HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
//            highlightBuilder.preTags("<span style=\"color:red\">");
//            highlightBuilder.postTags("</span>");
//            searchRequestBuilder.highlighter(highlightBuilder);
//        }

        searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        searchRequestBuilder.setQuery(boolQuery);

        // 分页应用
        searchRequestBuilder.setFrom(currentPage).setSize(pageSize);

        // 设置是否按查询匹配度排序
        searchRequestBuilder.setExplain(true);

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        LOGGER.info("\n{}", searchRequestBuilder);



        // 执行搜索,返回搜索响应信息
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        long totalHits = searchResponse.getHits().totalHits();
        long length = searchResponse.getHits().getHits().length;

        LOGGER.debug("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            List<Map<String, Object>>  sourceList = setSearchResponse(searchResponse, highlightField);
            return new EsPage(currentPage, pageSize, (int) totalHits, sourceList);
        }

        return null;

    }

    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse
     * @param highlightField
     */
    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = null;
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            stringBuffer = new StringBuffer();
            searchHit.getSource().put("id", searchHit.getId());
            if (StringUtils.isNotEmpty(highlightField)) {
                System.out.println("遍历 高亮结果集，覆盖 正常结果集" + searchHit.getSource());
                if(searchHit.getHighlightFields().size() > 0 && searchHit.getHighlightFields().get(highlightField) != null){
                    Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();
                    if (text != null) {
                        for (Text str : text) {
                            stringBuffer.append(str.string());
                        }
                        //遍历 高亮结果集，覆盖 正常结果集
                        searchHit.getSource().put(highlightField, stringBuffer.toString());
                    }
                }
            }
            sourceList.add(searchHit.getSource());
        }

        return sourceList;
    }


    /**
     * 根据网站的文章ID获取相似网站的文章
     *
     * @param id   文章ID
     * @param size 页面容量
     * @param page 页码
     * @return
     */
    public static List<Webpage> moreLikeThis(String id, int size, int page) {
        MoreLikeThisQueryBuilder.Item[] items = {new MoreLikeThisQueryBuilder.Item( CodeConstant.INDEX_NAME, CodeConstant.TYPE_NAME, id)};
//        MoreLikeThisRequestBuilder mlt = new MoreLikeThisRequestBuilder(client, "indexName", "indexType", "id");
        String[] fileds = {"title","content"};
        SearchResponse response = client.prepareSearch(CodeConstant.INDEX_NAME)
                .setTypes(CodeConstant.TYPE_NAME)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(moreLikeThisQuery(fileds, null, items)
                        .minTermFreq(2)
                        .maxQueryTerms(12))
                .setSize(size).setFrom(size * (page - 1))
                .execute()
                .actionGet();
        return warpHits2List(response.getHits());
    }

    private static List<Webpage> warpHits2List(SearchHits hits) {
        List<Webpage> webpageList = Lists.newLinkedList();
        hits.forEach(searchHitFields -> {
            webpageList.add(warpHits2Info(searchHitFields));
        });
        return webpageList;
    }

    private static Webpage warpHits2Info(SearchHit hit) {
        Webpage webpage = gson.fromJson(hit.getSourceAsString(), Webpage.class);
        webpage.setId(hit.getId());
        return webpage;
    }



}