package com.maj.mall.search.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.maj.mall.annotations.LoginRequired;
import com.maj.mall.bean.PmsBaseAttrInfo;
import com.maj.mall.bean.PmsBaseAttrValue;
import com.maj.mall.bean.PmsSearchSkuInfo;
import com.maj.mall.bean.PmsSkuAttrValue;
import com.maj.mall.bean.PmsSkuInfo;
import com.maj.mall.dto.PmsSearchCrumb;
import com.maj.mall.dto.PmsSearchParam;
import com.maj.mall.service.PmsBaseAttrService;
import com.maj.mall.service.SearchService;
import com.maj.mall.service.SkuService;

@Controller
public class SearchController {
	
    static Logger LOG = LoggerFactory.getLogger(SearchController.class);

	@Reference(retries = 0, timeout = 50000)
	private SkuService skuService;
	
	@Reference(retries = 0, timeout = 50000)
	private SearchService searchService;
	
    @Reference(retries = 0, timeout = 50000)
    PmsBaseAttrService pmsBaseAttrService;

	@RequestMapping("test")
	@ResponseBody
	public String test() {
		return "success";
	}
	
    /**
     * 主页查询
     * 
     * @param pmsSearchParam
     * @param modelMap
     * @return String
     * @date: 2019年12月3日 上午11:36:36
     */
	@RequestMapping("list.html")
	public String list(PmsSearchParam pmsSearchParam, ModelMap modelMap) {
        // 1, 调用搜索服务，返回搜索结果
        List<PmsSearchSkuInfo> searchSkuInfos = search(pmsSearchParam);
        modelMap.addAttribute("skuLsInfoList", searchSkuInfos);

        // 2, 基础属性值ID的集合
        // 根据基础属性值查询基础属性信息
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = getBaseAttrInfo(searchSkuInfos);
        modelMap.put("attrList", pmsBaseAttrInfos);
		
        // 3, 当前请求的参数信息
        String urlParam = getUrlParamBySearchParam(pmsSearchParam);
        modelMap.put("urlParam", urlParam);
        String keyword = pmsSearchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            modelMap.put("keyword", keyword);
        }

        // 4, 平台属性集合进行进一步处理，去掉当前valueId所在属性组；记录面包屑（选中的属性值）
        // 选中的基础属性值（面包屑选项），valueIds不为空，说明有面包屑选项
        String[] valueIds = pmsSearchParam.getValueId();
        if (valueIds != null && valueIds.length > 0 && pmsBaseAttrInfos != null) {
            Set<String> valueIdSet = new HashSet<>(Arrays.asList(valueIds));
            // 面包屑集合
            List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
            /*
             * 遍历基础属性信息，基础属性值的信息，当属性值匹配面包屑时，移除；
             * 因为面包屑都是从基础属性的值中选出来的，且每种基础属性只能选择一次，所以遍历完一遍基础属性，一定能找到所有面包屑
             */
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
            while(iterator.hasNext()) {
                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                // 根据属性值查出来的这些数据，所以属性值列表不可能为空
                List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                // 遍历
                for (PmsBaseAttrValue attrValue : attrValueList) {
                    if (valueIdSet.contains(attrValue.getId())) {
                        // 生成面包屑对象
                        PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                        pmsSearchCrumb.setUrlParam(getUrlParamCrumb(pmsSearchParam, attrValue.getId()));
                        pmsSearchCrumb.setValueId(attrValue.getId());
                        pmsSearchCrumb.setValueName(attrValue.getValueName());
                        pmsSearchCrumbs.add(pmsSearchCrumb);

                        // 移除基础属性信息
                        iterator.remove();
                    }
                }
            }

            modelMap.put("attrValueSelectedList", pmsSearchCrumbs);
        }

        return "list";
	}
	
    /**
     * 获取面包屑参数（= 当前请求参数 - 自身属性值参数）
     * 
     * @param pmsSearchParam
     * @param delValueId
     *            移除的面包屑
     * @return String
     * @date: 2019年12月4日 下午3:00:26
     */
    private String getUrlParamCrumb(PmsSearchParam pmsSearchParam, String delValueId) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] valueIds = pmsSearchParam.getValueId();

        StringBuffer urlParam = new StringBuffer();

        if (!StringUtils.isEmpty(keyword)) {
            urlParam.append("keyword=");
            urlParam.append(keyword);
        }

        if (!StringUtils.isEmpty(catalog3Id)) {
            if (urlParam.length() > 0) {
                urlParam.append("&");
            }
            urlParam.append("catalog3Id=");
            urlParam.append(catalog3Id);
        }

        if (valueIds != null) {
            for (String valueId : valueIds) {
                if (valueId.equals(delValueId)) {
                    continue;
                }
                // 对于不是当前面包屑基础选项值的信息，进行拼接
                if (urlParam.length() > 0) {
                    urlParam.append("&");
                }
                urlParam.append("valueId=" + valueId);
            }
        }

        return urlParam.toString();

    }

    /**
     * 根据条件查询
     * 
     * @param pmsSearchParam
     * @return List<PmsSearchSkuInfo>
     * @date: 2019年12月4日 下午2:38:38
     */
    private List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam) {
        try {
            return searchService.list(pmsSearchParam);
        } catch (Exception e) {
            LOG.error("查询elsearch错误！", e);
            return null;
        }
    }

    /**
     * 根据sku信息，获取基础属性信息
     * 
     * @param searchSkuInfos
     * @return List<PmsBaseAttrInfo>
     * @date: 2019年12月4日 下午2:41:05
     */
    private List<PmsBaseAttrInfo> getBaseAttrInfo(List<PmsSearchSkuInfo> searchSkuInfos) {
        if (searchSkuInfos == null || searchSkuInfos.isEmpty()) {
            return null;
        }

        // 从sku信息获取基础属性值信息
        Set<String> valueIdSet = new HashSet<>();
        for (PmsSearchSkuInfo searchSkuInfo : searchSkuInfos) {
            List<PmsSkuAttrValue> skuAttrValueList = searchSkuInfo.getSkuAttrValueList();
            for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                valueIdSet.add(pmsSkuAttrValue.getValueId());
            }
        }

        // 根据基础属性值查询基础属性信息
        return pmsBaseAttrService.getPmsBaseSaleAttrByValueIds(valueIdSet);
    }

    /**
     * 主页
     * 
     * @return String
     * @date: 2019年12月3日 上午11:36:47
     */
	@RequestMapping("index")
    @LoginRequired(loginSuccess = false)
	public String index() {
		return "index";
	}
	
    /**
     * 更新操作（更新数据库中的信息到elsearch）
     * 
     * @return String
     * @date: 2019年12月3日 上午11:35:10
     */
	@RequestMapping("update")
	@ResponseBody
	public String updateSearch() {
		// 搜索引擎对象
		List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
		
        // 数据库中查询
		List<PmsSkuInfo> pmsSkuInfoList = skuService.getAllSku();
		
		for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
			
			PmsSearchSkuInfo pmsSearchSkuInfo = getSearchSkuInfoBySkuInfo(pmsSkuInfo);

			pmsSearchSkuInfos.add(pmsSearchSkuInfo);
		}
		
		// 保存到搜索引擎
		searchService.save(pmsSearchSkuInfos);
		
		return "success";
	}
	
	/**
	 * 根据数据库PmsSkuInfo获取搜索引擎对象
	 * @param pmsSkuInfo
	 * @return
	 */
	private PmsSearchSkuInfo getSearchSkuInfoBySkuInfo(PmsSkuInfo pmsSkuInfo) {
		PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
		pmsSearchSkuInfo.setCatalog3Id(pmsSkuInfo.getCatalog3Id());
//		pmsSearchSkuInfo.setHotScore();
		pmsSearchSkuInfo.setId(Long.valueOf(pmsSkuInfo.getId()));
		pmsSearchSkuInfo.setPrice(pmsSkuInfo.getPrice());
		pmsSearchSkuInfo.setProductId(pmsSkuInfo.getProductId());
		pmsSearchSkuInfo.setSkuAttrValueList(pmsSkuInfo.getSkuAttrValueList());
		pmsSearchSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuDefaultImg());
		pmsSearchSkuInfo.setSkuDesc(pmsSkuInfo.getSkuDesc());
		pmsSearchSkuInfo.setSkuName(pmsSkuInfo.getSkuName());
		
		return pmsSearchSkuInfo;
	}

    /**
     * 根据查询参数拼接url get条件
     * 
     * @param pmsSearchParam
     * @return String
     * @date: 2019年12月4日 上午11:19:00
     */
    private String getUrlParamBySearchParam(PmsSearchParam pmsSearchParam) {
        String keyword = pmsSearchParam.getKeyword();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String[] valueIds = pmsSearchParam.getValueId();

        StringBuffer urlParam = new StringBuffer();

        if(!StringUtils.isEmpty(keyword)) {
            urlParam.append("keyword=");
            urlParam.append(keyword);
        }
        
        if(!StringUtils.isEmpty(catalog3Id)) {
            if(urlParam.length() > 0) {
                urlParam.append("&");
            }
            urlParam.append("catalog3Id=");
            urlParam.append(catalog3Id);
        }

        if (valueIds != null) {
            for (String valueId : valueIds) {
                if (urlParam.length() > 0) {
                    urlParam.append("&");
                }
                urlParam.append("valueId=" + valueId);
            }
        }

        return urlParam.toString();
    }
}





















