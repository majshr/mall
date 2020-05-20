package com.maj.mall.search.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.maj.mall.bean.PmsSearchSkuInfo;
import com.maj.mall.dto.PmsSearchParam;
import com.maj.mall.search.util.ElsearchUtil;
import com.maj.mall.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {

	private static final String MALL_INDEX_NAME = "mallmaj";

    @Autowired
    ElsearchUtil elsearchUtil;

	@Override
	public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {

        SearchSourceBuilder builder = getSearchSourceBuilder(pmsSearchParam);
        List<PmsSearchSkuInfo> searchSkuInfos = elsearchUtil.search(MALL_INDEX_NAME, builder);
        return searchSkuInfos;
    }

    /**
     * 根据查询参数获取elsearch查询信息
     * 
     * @param pmsSearchParam
     * @return SearchSourceBuilder
     * @date: 2019年12月3日 上午11:19:10
     */
    private SearchSourceBuilder getSearchSourceBuilder(PmsSearchParam pmsSearchParam) {
        // ********** dsl工具
        // 1, SearchSourceBuilder
        SearchSourceBuilder builder = new SearchSourceBuilder();

        // 第几页，一页多少条数据
        int from = pmsSearchParam.getFrom() == null ? 0 : pmsSearchParam.getFrom();
        int size = pmsSearchParam.getSize() == null ? 100 : pmsSearchParam.getSize();
        builder.from(from);
        builder.size(size);

        // 设置需要获取的列和不需要获取的列
        // builder.fetchSource(new String[] {}, new String[] {});

        // 设置排序规则（当前为id为字符串，所以字符串比较，排序结果不对，可以修改为long类型）
        builder.sort("id", SortOrder.DESC);

        // 设置超时时间为2s
        builder.timeout(new TimeValue(2000));

        // highlight；skuName字段高亮；默认高亮格式为斜体，自己设置为红色
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("</span>");
        builder.highlighter(highlightBuilder);

        // 2, BoolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        String[] valueIds = pmsSearchParam.getValueId();
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();

        // filter
        if (!StringUtils.isEmpty(catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        if (valueIds != null) {
            for (String valueId : valueIds) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId",
                        valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        // must
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        // 3, query
        builder.query(boolQueryBuilder);

        return builder;
    }

    @Override
    public void save(List<PmsSearchSkuInfo> pmsSearchSkuInfos) {
        elsearchUtil.insertBatch(MALL_INDEX_NAME, pmsSearchSkuInfos);
	}

}
