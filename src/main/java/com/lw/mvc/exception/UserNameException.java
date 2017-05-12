package com.lw.mvc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(reason="�û�������Ϊnull",code=HttpStatus.FORBIDDEN)
public class UserNameException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
