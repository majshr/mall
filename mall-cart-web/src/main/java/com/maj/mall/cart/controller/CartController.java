package com.maj.mall.cart.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.maj.mall.annotations.LoginRequired;
import com.maj.mall.bean.OmsCartItem;
import com.maj.mall.bean.PmsSkuInfo;
import com.maj.mall.service.CartService;
import com.maj.mall.service.SkuService;
import com.maj.mall.util.CookieUtil;

@Controller
public class CartController {
	
	static final Logger LOG = LoggerFactory.getLogger(CartController.class);
	
    @Reference(timeout = 50000, retries = 0)
	private SkuService skuService;
	
    @Reference(timeout = 50000, retries = 0)
	private CartService cartService;
	
    private static final String CART_LIST_COOKIE_KEY = "cartListCookie";

    @RequestMapping("test")
    public String success() {
        cartService.cartList("123");
        return "success";
    }

    @LoginRequired(loginSuccess = false)
	@RequestMapping("checkCart")
	public String checkCart(String isChecked, String skuId, 
			HttpServletRequest request, HttpServletResponse response, HttpSession session, ModelMap modelMap) {
		String memberId = (String) request.getAttribute("memberId");
		String nickname = (String) request.getAttribute("nickname");
		
        memberId = "1000";

		// 调用服务，修改状态
		OmsCartItem omsCartItem = new OmsCartItem();
		omsCartItem.setMemberId(memberId);
		omsCartItem.setProductSkuId(skuId);
		omsCartItem.setIsChecked(isChecked);
		
		// 更新选中状态到数据库，更新缓存
		cartService.checkCart(omsCartItem);
		
		// 最新的数据从缓存中查出来，渲染给内嵌页
		List<OmsCartItem> omsCartItems = cartService.cartList(memberId);
		modelMap.put("cartList", omsCartItems);
        modelMap.put("totalAmount", getTotalAmount(omsCartItems));
		
		return "cartListInner";
	}
	
	@LoginRequired(loginSuccess = false)
	@RequestMapping("cartList")
	public String cartList(HttpServletRequest request, HttpServletResponse response,
			ModelMap modelMap) {
		List<OmsCartItem> omsCartItems;
		String memberId = (String) request.getAttribute("memberId");
		String nickname = (String) request.getAttribute("nickname");
        memberId = "1000";
		if(StringUtils.isNotBlank(memberId)) {
			// 已经登陆，查询数据库
			omsCartItems = cartService.cartList(memberId);
		}else {
			// 没有登录，cookie查询
            String omsCarItemsString = CookieUtil.getCookieValue(request, CART_LIST_COOKIE_KEY, true);
			
			if(StringUtils.isEmpty(omsCarItemsString)) {
				omsCartItems = new ArrayList<OmsCartItem>();
			} else {
				omsCartItems = JSON.parseArray(omsCarItemsString, OmsCartItem.class);	
			}
			// 设置总价格
			// 设计钱的方法，都使用BigDecimal
			omsCartItems.forEach(item->{
				item.setTotalPrice(item.getPrice().multiply(item.getQuantity()));
			});
		}
		
		modelMap.put("cartList", omsCartItems);
		modelMap.put("totalAmount", getTotalAmount(omsCartItems));
		return "cartList";
	}
	
	@LoginRequired(loginSuccess = false)
    @RequestMapping(value = "addToCart", method = RequestMethod.POST)
	public String addToCart(String skuId, Integer quantity, 
			HttpServletRequest request, HttpServletResponse response) {
		// 调用商品服务查询商品信息
		PmsSkuInfo skuInfo = skuService.get(skuId);
		
		// 将商品信息封装成购物车
		OmsCartItem omsCartItem = getOmsCartItem(skuInfo, quantity);
		
		//*********************添加购物车操作****************
		String memberId = (String) request.getAttribute("memberId");
		String nickname = (String) request.getAttribute("nickname");
        memberId = "1000";
		// 判断用户是否登录
		List<OmsCartItem> list;
		if(StringUtils.isEmpty(memberId)) {
            // 用户没有登录
            String omsCarItemsString = CookieUtil.getCookieValue(request, CART_LIST_COOKIE_KEY, true);
			
			if(StringUtils.isEmpty(omsCarItemsString)) {
				list = new ArrayList<OmsCartItem>();
			} else {
				list = JSON.parseArray(omsCarItemsString, OmsCartItem.class);	
			}
			
			addOrUpdateOmsCartItem(list, omsCartItem);
			
			// 放入cookie，3天有效
            CookieUtil.setCookie(request, response, CART_LIST_COOKIE_KEY, JSON.toJSONString(list), 
					60*60*72, true);
		} else {
			// 用户已经登陆，数据库中操作
			OmsCartItem omsCarItemAdded = cartService.ifCartExistByUser(memberId, skuId);
			// 商品未添加过购物车，添加
			if(omsCarItemAdded == null) {
				omsCartItem.setMemberId(memberId);
				cartService.addCart(omsCartItem);
			} else {
				// 商品已经添加过购物车，更新数量信息
				omsCarItemAdded.setQuantity(omsCarItemAdded.getQuantity().add(omsCartItem.getQuantity()));
				cartService.updateCart(omsCarItemAdded);
			}
			
			// 同步缓存
			cartService.flushCartCache(memberId);
		}
		
        // return "success";
        return "redirect:/success.html";
	}
	
	/**
	 * 计算总价格
	 * @param omsCartItems
	 * @return
	 */
	private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
		BigDecimal totalAmount = new BigDecimal("0");
		
        for (OmsCartItem omsCarItem : omsCartItems) {
            // 计算选中的
            if ("1".equals(omsCarItem.getIsChecked())) {
                totalAmount = totalAmount.add(omsCarItem.getTotalPrice());
            }
        }
		
		return totalAmount;
	}
	
	/**
	 * 新增或更新购物车商品信息
	 * @param list
	 * @param omsCartItem
	 */
	private void addOrUpdateOmsCartItem(List<OmsCartItem> list, OmsCartItem omsCartItem) {
		if(list == null) {
			list = new ArrayList<OmsCartItem>();
		}
		
        // 之前添加过相同商品，更新数量或添加商品
		for(OmsCartItem itemAdded : list) {
            if (itemAdded.getProductSkuId().equals(omsCartItem.getId())) {
				itemAdded.setQuantity(itemAdded.getQuantity().add(omsCartItem.getQuantity()));
                return;
			}
		}

        // 之前没添加过相同商品，直接添加
        list.add(omsCartItem);
	}
	
	/**
	 * 获取购物车对象
	 * @param skuInfo
	 * @param quantity
	 * @return
	 */
	private OmsCartItem getOmsCartItem(PmsSkuInfo skuInfo, Integer quantity) {
		OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        // 商品价格
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuInfo.getId());
        omsCartItem.setQuantity(new BigDecimal(quantity));
        
        return omsCartItem;
	}
}
