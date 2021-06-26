package com.mediscreen.report.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.report.model.Report;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * this class get infos and notes of a patient and generate his report
 * gets id of patient from UI
 * gets infos of patient from Patient Api
 * {
    "id": 1,
    "firstName": "Test",
    "lastName": "TestNone",
    "birthdate": "1966-12-31",
    "gender": "F",
    "address": "1 Brookside St",
    "phoneNumber": "100-222-3333"
    }
 * gets notes of patient from History Api
 * [
        {
        "historyId": "60cb9a652ae23137b57ea8ff",
        "patId": 1,
        "note": {
            "noteTitle": "Titre de la note",
            "noteContent": "Une note,\nsur un patient\nqui a un problème",
            "noteDate": "2021-06-17",
            "doctorName": "Maboul"
        }
        }
    ]
 * Then from this, age is calculated, then risk is determine.   
 */
@Service
public class ReportService {

    Logger logger = LoggerFactory.getLogger(ReportService.class);

    /**
     * List of String containing every terms that can be used to determine risk of Diabete.
     */
    private List<String> terms = new ArrayList<>(Arrays.asList(
        "Hémoglobine A1C",
        "Microalbumine",
        "Taille",
        "Poids",
        "Fumeur",
        "Anormal",
        "Cholestérol",
        "Vertige",
        "Rechute",
        "Réaction",
        "Anticorps"
        ));
    
        /**
         * create a Report for a patient
         * 
         * @param patId send from the UI
         * @return Report
         * @throws JsonMappingException
         * @throws JsonProcessingException
         */
        public Report generateReport(int patId) throws JsonProcessingException {
            logger.info("generation of report.");
            // get patientinfos form api patient
            ResponseEntity<String> patient = getPatientInfos(patId);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(patient.getBody());
            JsonNode firstName = root.path("firstName");
            JsonNode lastName = root.path("lastName");
            JsonNode birthdate = root.path("birthdate");
            JsonNode gender = root.path("gender");
            // construction of first fields of Report
            Report report = new Report();
            report.setPatientFirstName(firstName.asText());
            report.setPatientLastName(lastName.asText());
            report.setPatientAge(calculateAge(birthdate.asText()));

            // get patienthistory from api history
            // if there is no notes for this patient, a message is set in place of the risk
            List<String> notes = getHistoryOfPatient(patId);
            String risk = "";
            if (notes == null) {
                risk = "No notes for this patient, analysis can't be perform.";
            } else {
                // get the number of terms in notes, a term is counted only once even if it
                // appears multiple times
                int numberOfTerms = calculateNumberOfTerms(notes);
                // evaluate the risk according to age, gender and number of terms of a patient
                risk = givenNumberOfTermsGiveTheCorrespondingRiskName(numberOfTerms, report.getPatientAge(),
                        gender.asText());
            }
            report.setRisk(risk);
            return report;
        }

        /**
         * get a responseEntity String with information of a patient.
         * 
         */
        public ResponseEntity<String> getPatientInfos(int patId) {
            logger.info("get the patient informations from container patient");
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://patient:8081/patient/";
            ResponseEntity<String> response = restTemplate.getForEntity(url + patId, String.class);
            logger.info("patient's info: "+ response.getBody());
            return response;
        }

        /**
         * calculate age from a birthdate like "1981-10-28"
         * 
         * @param birthdate
         * @return long age
         */
        public long calculateAge(String birthdate) {
            logger.info("calculate age of patient born on "+birthdate+".");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date1 = LocalDate.parse(birthdate, formatter);
            LocalDate date2 = LocalDate.now();
            TemporalUnit unit = ChronoUnit.YEARS;
            return Period.between(date1, date2).get(unit);
        }

        /**
         * * get notes taken by a praticien on a patient
         * 
         * @param patId
         * @return a List of String
         * @throws JsonMappingException
         * @throws JsonProcessingException
         */
        public List<String> getHistoryOfPatient(int patId) throws JsonProcessingException {
            logger.info("Call to container history to get notes taken for this patient.");
            List<String> notesContent = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://history:8082/history/";
            ResponseEntity<String> response = restTemplate.getForEntity(url + patId, String.class);
            ObjectMapper mapper = new ObjectMapper();
            if (response.getBody() == null) {
                logger.info("No notes found for this patient.");
                notesContent = null;
            } else {
                JsonNode root = mapper.readTree(response.getBody());
                for (int i = 0; i < root.size(); i++) {
                    String note = root.get(i).get("note").get("noteContent").asText();
                    notesContent.add(note);
                }
            }
            return notesContent;
        }

        /**
         * get the number of terms presents in notes of a patient. A term is counted
         * once even if it appears multiple times.
         * 
         * @param notes
         * @return
         */
        public int calculateNumberOfTerms(List<String> notes) {
            logger.info("Count the number of terms presents in the patient's notes");
            Set<String> termsFound = new HashSet<>();
            for (int i = 0; i < terms.size(); i++) {
                for (String note : notes) {
                    if (note.contains(terms.get(i))) {
                        termsFound.add(terms.get(i));
                    }
                }
            }
            int numberOfTerms = termsFound.size();
            return numberOfTerms;
        }

        /**
         * A risk name is defined by multiple criteras :
         * 
         * @param numberOfTerms
         * @param age
         * @param gender
         * @return a String with the risk of the patient
         */
        public String givenNumberOfTermsGiveTheCorrespondingRiskName(int numberOfTerms, long age, String gender) {
            logger.info("Defining the diabete risk of the patient. Number of terms: "+numberOfTerms+" age: "+age+" gender: "+gender);
            String risk = "";
            if (age >= 30) {
                risk = ageEqualsOrSuperiorTo30(numberOfTerms);
            } else if (age >= 0 && age < 30) {
                if (gender.equals(String.valueOf('F'))) {
                    risk = riskForGenderFwhoIsUnder30(numberOfTerms);
                } else if (gender.equals(String.valueOf('M'))) {
                    risk = riskForGenderMwhoIsUnder30(numberOfTerms);
                }
            }
            return risk;
        }

        /**
         * Risks for a person (gender F or gender M) which age is equals or superior to
         * 30 years.
         */
        public String ageEqualsOrSuperiorTo30(int numberOfTerms) {
            String risk = "";
            if (numberOfTerms >= 0 && numberOfTerms < 2) {
                risk = "None";
            } else if (numberOfTerms >= 2 && numberOfTerms < 6) {
                risk = "Borderline";
            } else if (numberOfTerms >= 6 && numberOfTerms < 8) {
                risk = "In danger";
            } else {
                risk = "Early onset";
            }
            return risk;
        }

        /**
         * risk for a person of gender F who is under 30
         * 
         * @param numberOfTerms
         * @return String risk of the patient
         */
        public String riskForGenderFwhoIsUnder30(int numberOfTerms) {
            String risk = "";
            if (numberOfTerms >=0 && numberOfTerms < 4) {
                risk = "None";
            } else if (numberOfTerms >= 4 && numberOfTerms < 7) {
                risk = "In danger";
            } else {
                risk = "Early onset";
            }
            return risk;
        }

        /**
         * risk for a person of gender M who is under 30
         * 
         * @param numberOfTerms
         * @return String risk of the patient
         */
        public String riskForGenderMwhoIsUnder30(int numberOfTerms) {
            String risk = "";
            if (numberOfTerms >= 0 && numberOfTerms < 3) {
                risk = "None";
            } else if (numberOfTerms >= 3 && numberOfTerms < 5) {
                risk = "In danger";
            } else {
                risk = "Early onset";
            }
            return risk;
        }
    }
