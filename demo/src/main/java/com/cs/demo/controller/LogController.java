package com.cs.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs.demo.domain.Event;
import com.cs.demo.service.DemoService;

@RestController
@RequestMapping("/api")
public class LogController {

	@Autowired 
	private DemoService demoService;
	
	@PostMapping("/processStart")
	public String processStart() throws Exception{
		Map<String, Event> events = demoService.processLogs();
		demoService.saveExpensiveEvents(events);
		return "Process Completed..";
	}
}
