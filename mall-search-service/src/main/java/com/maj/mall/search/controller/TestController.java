package com.maj.mall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maj.mall.dto.PmsSearchParam;
import com.maj.mall.service.SearchService;

@Controller
public class TestController {
    @Autowired
    private SearchService searchService;

    @RequestMapping("test")
    @ResponseBody
    public String test() {

        PmsSearchParam pmsSearchParam = new PmsSearchParam();

        pmsSearchParam.setCatalog3Id("11");

        searchService.list(pmsSearchParam);

        return "success";
    }
}
