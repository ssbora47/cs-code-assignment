package com.cs.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cs.demo.domain.Event;
import com.cs.demo.domain.STATE;
import com.cs.demo.repository.EventRepository;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DemoServiceTest {
	@InjectMocks
    DemoService service;
      
    @Mock
    EventRepository dao;
     
    @Test
    public void testProcessLogs() throws Exception
    {
        Map<String, Event> eventMap = service.processLogs();
        assertEquals(3, eventMap.values().size());
    }
    
    @Test
    public void testSaveExpensiveEvents() throws Exception{
    	List<Event> eventList = service.readJson();
    	Map<String, Event> eventMap  = eventList.stream().filter(event -> STATE.FINISHED.toString().equalsIgnoreCase(event.getState()))
    	.collect(Collectors.toMap(Event::getId, Function.identity()));
    	service.saveExpensiveEvents(eventMap);
    	verify(dao, times(3)).findAll();
    }
}
