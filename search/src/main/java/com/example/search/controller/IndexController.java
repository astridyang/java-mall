package com.example.search.controller;

import com.example.search.service.MallSearchService;
import com.example.search.vo.SearchParam;
import com.example.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * @author sally
 * @date 2022-09-23 14:05
 */
@Controller
public class IndexController {

	@Resource
	MallSearchService mallSearchService;

	@GetMapping("/list.html")
	public String listPage(SearchParam searchParam, Model model) {
		SearchResult result = mallSearchService.search(searchParam);
		model.addAttribute("result", result);
		return "list";
	}
}
