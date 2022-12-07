package com.example.member.exception;

/**
 * @author sally
 * @date 2022-10-10 15:19
 */
public class UsernameExistException extends RuntimeException {
	public UsernameExistException() {
		super("username existed.");
	}
}
