package com.example.search.service;

import com.example.search.vo.SearchParam;
import com.example.search.vo.SearchResult;

/**
 * @author sally
 * @date 2022-09-23 14:56
 */
public interface MallSearchService {

	SearchResult search(SearchParam searchParam);
}
