package com.smart.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.razorpay.*;

@Controller
public class PaymentController {

	@PostMapping("/user/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
		System.out.println(data);
		int amount=Integer.parseInt(data.get("amount").toString());
		var client = new RazorpayClient("rzp_test_kfwamSmSdvBtMu", "ZjS6RLfqcG6jqKbqAxDyBZ2U");
		JSONObject ob=new JSONObject();
		ob.put("amount", amount*100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_8897123");
		
		//creating new order
		
		Order order = client.orders.create(ob);
		System.out.println(order);
//		System.out.println("Heyy order function execution");
		return order.toString();
	}
}
