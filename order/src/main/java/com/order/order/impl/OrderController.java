package com.order.order.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(value = "/order")
public class OrderController {

	public static final String APPID = "order-service";

	@Autowired
	private OrderService orderService;

	@RequestMapping(value = "/buySth")
	@ResponseBody
	public String buySomethingProxy(@RequestParam int userId,@RequestParam int money, @RequestParam String trxId) throws IOException {

//		try {
//			throw new IOException();
//		} catch (IOException e) {
//			return "error";
//		}

		return orderService.buySomething(userId, money, trxId);
	}
	
}
