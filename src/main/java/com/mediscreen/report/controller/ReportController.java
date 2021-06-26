package com.mediscreen.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mediscreen.report.model.Report;
import com.mediscreen.report.service.ReportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class ReportController {

    Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    ReportService reportService;
    
    @PostMapping(path = "/assess/id")
    public ResponseEntity<Report> reportAnalysis(@RequestParam int patId) throws JsonMappingException, JsonProcessingException {
      logger.info("generating report."); 
      Report report = reportService.generateReport(patId);
      return ResponseEntity.ok().body(report);
    }
    
}
