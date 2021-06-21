package com.mediscreen.report;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.report.controller.ReportController;
import com.mediscreen.report.model.Report;
import com.mediscreen.report.service.ReportService;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ReportController.class)
public class TestReportController {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    void givenPatId_whenReportCreated_thenReportIsCreatedAndRiskIsSet() throws Exception {
        int patId = 1;
        Report report = new Report();
        report.setPatientFirstName("Test");
        report.setPatientLastName("TestName");
        report.setPatientAge(35);
        String risk = "In danger";
        report.setRisk(risk);
        when(reportService.generateReport(patId)).thenReturn(report);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(reportService.generateReport(patId));
        mockMvc.perform(post("/assess/id")
        .param("patId", "1")
        .content(json)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful()); 
   
    }

}
