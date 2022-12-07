package com.example.member.exception;

/**
 * @author sally
 * @date 2022-10-10 15:20
 */
public class PhoneExistException extends RuntimeException {
	public PhoneExistException() {
		super("phone existed.");
	}
}
