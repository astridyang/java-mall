package com.example.common.constant;

/**
 * @author sally
 * @date 2022-08-22 5:10 PM
 */
public class ProductConstant {
	public enum AttrEnum {
		ATTR_TYPE_BASE(1, "基本属性"), ATTR_TYPE_SALE(0, "销售属性");
		private int code;
		private String msg;

		AttrEnum(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public int getCode() {
			return code;
		}
	}

	public enum StatusEnum {
		NEW(0, "Created"), UP(1, "up"), DOWN(2, "down");
		private int code;
		private String msg;

		StatusEnum(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

		public int getCode() {
			return code;
		}
	}
}
