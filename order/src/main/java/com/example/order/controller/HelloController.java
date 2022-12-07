package com.example.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author sally
 * @date 2022-10-14 10:05
 */
// @Controller
public class HelloController {

	@GetMapping("/{page}.html")
	public String page(@PathVariable("page") String page){
		return page;
	}
}
