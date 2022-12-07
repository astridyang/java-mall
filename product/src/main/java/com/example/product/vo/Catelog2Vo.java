package com.example.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author sally
 * @date 2022-09-21 9:57
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {
	private String id;
	private String name;
	private String catalog1Id;
	private List<Catelog3Vo> catalog3List;

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Catelog3Vo {
		private String id;
		private String name;
		private String catelog2Id;
	}
}
