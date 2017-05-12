package com.lw.mvc.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class HandlerException {

	@ExceptionHandler({ArithmeticException.class})
	public ModelAndView handleMathException(Exception ex){
		ModelAndView mv = new ModelAndView("error");
		mv.addObject("ex", ex);
		return mv ;
	}
}
