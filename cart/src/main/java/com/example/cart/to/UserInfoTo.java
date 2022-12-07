package com.example.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @author sally
 * @date 2022-10-12 10:31
 */
@ToString
@Data
public class UserInfoTo {
    private Long userId;
	private String userKey;
	private boolean tempUser = true;
}
