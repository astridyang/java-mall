package com.example.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author sally
 * @date 2022-10-10 11:31
 */
@Data
public class RegisterVo {
	// TODO 封装错误信息map时key重复
	// @NotEmpty(message = "username must not be empty")
	@Length(min = 2, max = 18, message = "username's length must between 2-18")
	private String username;

	// @NotEmpty(message = "password can't be empty")
	@Length(min = 6, max = 18, message = "length of password must between 6-18")
	private String password;

	// @NotEmpty(message = "phone can't be empty")
	@Pattern(regexp = "^1([3-9])[0-9]{9}$", message = "phone pattern is incorrect")
	private String phone;

	@NotEmpty(message = "code can't be empty")
	private String code;
}
