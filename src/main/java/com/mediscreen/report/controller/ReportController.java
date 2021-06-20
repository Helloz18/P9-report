package com.mediscreen.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mediscreen.report.model.Report;
import com.mediscreen.report.service.ReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class ReportController {

    @Autowired
    ReportService reportService;
    
    @PostMapping(path = "/assess/id")
    public ResponseEntity<Report> reportAnalysis(@RequestParam int patId) throws JsonMappingException, JsonProcessingException {
        Report report = reportService.generateReport(patId);
       
        return ResponseEntity.ok().body(report);
      }
    
}
