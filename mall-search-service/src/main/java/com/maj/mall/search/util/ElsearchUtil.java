package com.maj.mall.search.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.maj.mall.bean.PmsSearchSkuInfo;

@Component
public class ElsearchUtil {

    static Logger LOG = LoggerFactory.getLogger(ElsearchUtil.class);

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 新建索引
     * 
     * @param idxName
     * @param mappingJson（不设置值的话，填空串或空值）
     * @date: 2019年12月2日 下午5:13:08
     */
    public boolean createIndex(String idxName, String mappingJson) {

        try {
            if (!indexExist(idxName)) {
                LOG.error(" idxName={} 已经存在！", idxName);
                return false;
            }
            CreateIndexRequest request = new CreateIndexRequest(idxName);
            buildSetting(request);
            if (!StringUtils.isEmpty(mappingJson)) {
                request.mapping(mappingJson, XContentType.JSON);
            }

            CreateIndexResponse res = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

            return res.isAcknowledged();
        } catch (Exception e) {
            LOG.error("创建索引错误！", e);
        }

        return false;
    }

    /**
     * 断某个index是否存在
     * 
     * @param idxName
     *            索引
     * @return
     * @throws Exception
     *             boolean
     * @date: 2019年12月2日 下午4:55:41
     */
    public boolean indexExist(String idxName) throws Exception {
        GetIndexRequest request = new GetIndexRequest(idxName);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
 
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 检查文档是否存在
     * 
     * @param index
     * @param documentId
     * @return boolean
     * @date: 2019年12月2日 下午5:50:04
     */
    public boolean documentExist(String index, String documentId) {
        GetRequest getRequest = new GetRequest(index, documentId);
        try {
            return restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOG.error("查询文档是否存在错误!", e);
            return false;
        }
    }

    /**
     * 设置分片
     * 
     * @See
     * @date 2019/10/17 19:27
     * @param request
     * @return void
     */
    public void buildSetting(CreateIndexRequest request) {

        request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
    }

    /**
     * 插入
     * 
     * @param idxName
     *            index
     * @param entity
     *            对象
     * @return void
     */
    public boolean insertOrUpdateOne(String idxName, PmsSearchSkuInfo skuInfo) {

        IndexRequest request = new IndexRequest(idxName);
        // 保存数据
        request.id(String.valueOf(skuInfo.getId()));
        // 以字符串形式提供源文档
        request.source(JSON.toJSONString(skuInfo), XContentType.JSON);
        // 超时时间
        request.timeout(TimeValue.timeValueSeconds(3));
        
        try {
            // 同步执行
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
            return true;
        } catch (Exception e) {
            LOG.error("添加文档信息错误！", e);
        }
        
        // 异步方式执行，添加一个监听器
        // restHighLevelClient.indexAsync(indexRequest, options, listener);

        return false;
    }

    /**
     * 批量插入数据
     * 
     * @param idxName
     *            index
     * @param list
     *            带插入列表
     * @return void
     */
    public void insertBatch(String idxName, List<PmsSearchSkuInfo> list) {

        BulkRequest request = new BulkRequest();
        // BulkRequest支持多种类型的请求
        list.forEach(searchSkuInfo -> request.add(new IndexRequest(idxName).id(String.valueOf(searchSkuInfo.getId()))
                .source(JSON.toJSONString(searchSkuInfo), XContentType.JSON)));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOG.error("批量添加文档错误！", e);
        } catch (Exception e) {
            LOG.error("批量添加文档错误！", e);
        }
    }

    /**
     * 批量删除
     * 
     * @param idxName
     *            index
     * @param idList
     *            待删除列表
     * @return void
     */
    public void deleteBatch(String idxName, Collection<PmsSearchSkuInfo> idList) {

        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(idxName, String.valueOf(item.getId()))));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文档
     * 
     * @param idxName
     *            index
     * @param builder
     *            查询参数
     * @param c
     *            结果类对象
     * @return java.util.List<T>
     * @throws @since
     */
    public List<PmsSearchSkuInfo> search(String idxName, SearchSourceBuilder builder) {
 
        SearchRequest request = new SearchRequest(idxName);
        request.source(builder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<PmsSearchSkuInfo> res = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                PmsSearchSkuInfo pmsSearchSkuInfo = JSON.parseObject(hit.getSourceAsString(), PmsSearchSkuInfo.class);

                // 高亮字段需要自己设置值
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (highlightFields != null && !highlightFields.isEmpty()) {
                    HighlightField highlightField = highlightFields.get("skuName");
                    Text[] fragments = highlightField.fragments();
                    StringBuffer skuName = new StringBuffer();
                    for (Text text : fragments) {
                        skuName.append(text.toString());
                    }
                    pmsSearchSkuInfo.setSkuName(skuName.toString());
                }

                res.add(pmsSearchSkuInfo);
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除index
     * 
     * @author WCNGS@QQ.COM
     * @See
     * @date 2019/10/17 17:13
     * @param idxName
     * @return void
     * @throws @since
     */
    public void deleteIndex(String idxName) {
        try {
            restHighLevelClient.indices().delete(new DeleteIndexRequest(idxName), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除单个文档
     * 
     * @param index
     * @param id
     * @return boolean
     * @date: 2019年12月3日 上午10:10:08
     */
    public boolean deleteDocument(String index, String id) {
        DeleteRequest request = new DeleteRequest(index, id);
        try {
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            return true;
        } catch(ElasticsearchException e) {
            LOG.error("删除文档错误！", e);
        } catch (IOException e) {
            LOG.error("删除文档错误！", e);
        }
        
        return false;
        
    }

    /**
     * @author WCNGS@QQ.COM
     * @See
     * @date 2019/10/17 17:13
     * @param idxName
     * @param builder
     * @return void
     * @throws
     * @since
     */
/*    public void deleteByQuery(String idxName, QueryBuilder builder) {
 
        DeleteByQueryRequest request = new DeleteByQueryRequest(idxName);
        request.setQuery(builder);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

}
