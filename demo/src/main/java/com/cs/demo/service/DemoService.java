package com.cs.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.cs.demo.domain.Event;
import com.cs.demo.domain.STATE;
import com.cs.demo.repository.EventRepository;

@Service
public class DemoService {

	@Value("${processTimeLimit}")
	private long processTimeLimit;
	
	@Value("${inputFile}")
	private String inputFile;
	
	@Autowired
	private EventRepository eventRepository;
	
	Logger logger = LoggerFactory.getLogger(DemoService.class);

	public List<Event> readJson() throws StreamReadException, DatabindException, IOException{
		InputStream inJson = Event.class.getResourceAsStream(inputFile);
		List<Event> eventList = new ObjectMapper().readValue(inJson, List.class);
		return eventList;
	}
	
	public Map<String, Event> processLogs() throws Exception {
		logger.info("ProcessLogs Started");
		List<Event> evenetList = readJson();
		List<Event> startedEventList = evenetList.stream()
				.filter(event -> STATE.STARTED.toString().equalsIgnoreCase(event.getState()))
				.collect(Collectors.toList());
		Map<String, Event> finishedEventMap = evenetList.stream()
				.filter(event -> STATE.FINISHED.toString().equalsIgnoreCase(event.getState()))
				.collect(Collectors.toMap(Event::getId, Function.identity()));
		
		startedEventList.stream().forEach(startEvent -> {
			Event endEvent = finishedEventMap.get(startEvent.getId());
			if (endEvent != null) {
				long processTime = endEvent.getTimestamp() - startEvent.getTimestamp();
				endEvent.setAlert(processTime >= processTimeLimit);
			}
		});
		logger.info("ProcessLogs End");
		return finishedEventMap;
	}
	
	public void saveExpensiveEvents(Map<String, Event> events){
		events.values().stream().forEach(event -> {
			com.cs.demo.entity.Event eve = new com.cs.demo.entity.Event();
			eve.setId(Long.parseLong(event.getId()));
			eve.setState(event.getState());
			eve.setType(event.getType().toString());
			eve.setTimeStamp(event.getDurationMs());
			eventRepository.save(eve);
		});
	}
}
