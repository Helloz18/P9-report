package com.mediscreen.report.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediscreen.report.model.Report;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Service
public class ReportService {

    //récupérer les infos du patient depuis l'api patient
    //récupérer les notes depuis l'api history

    // l'ui envoie en post l'id du patient, exemple 1
    // appel vers api patient pour récupérer ses infos
    // --> récupère un json
    //{
//   "id": 1,
//   "firstName": "Test",
//   "lastName": "TestNone",
//   "birthdate": "1966-12-31",
//   "gender": "F",
//   "address": "1 Brookside St",
//   "phoneNumber": "100-222-3333"
// }
//avec le même id :
    //réponse get history pour un patient array de stories (json)
//     [
//   {
//     "historyId": "60cb9a652ae23137b57ea8ff",
//     "patId": 1,
//     "note": {
//       "noteTitle": "Titre de la note",
//       "noteContent": "Une note,\nsur un patient\nqui a un problème",
//       "noteDate": "2021-06-17",
//       "doctorName": "Maboul"
//     }
//   }
// ]
    
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
    
        public Report generateReport(int patId) throws JsonMappingException, JsonProcessingException {
           //get patientinfos form api patient
           // --> return Report avec age nom et prénom
           ResponseEntity<String> patient = getPatientInfos(patId);
           ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(patient.getBody());
            JsonNode firstName = root.path("firstName");
            JsonNode lastName = root.path("lastName");
            JsonNode birthdate = root.path("birthdate");
            JsonNode gender = root.path("gender");
            //faire requête vers api patient avec get patId
            Report report = new Report();
            report.setPatientFirstName(firstName.asText());
            report.setPatientLastName(lastName.asText());
            report.setPatientAge(calculateAge(birthdate.asText()););
            return report;
           //get patienthistory from api history
           // --> return [String] notes
           List<String> notes = getHistoryOfPatient(patId);
           int numberOfTerms = calculateNumberOfTerms(notes);
           String risk = givenNumberOfTermsGiveTheCorrespondingRiskName(numberOfTerms,report.getPatientAge(), gender.asText());
           //--> return String nom du risk
           // --> Report.set(nom du risk)
           report.setRisk(risk);
            return report;

        }
        
        public ResponseEntity<String> getPatientInfos(int patId) throws JsonMappingException, JsonProcessingException {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8081/patient/";
            ResponseEntity<String> response = restTemplate.getForEntity(url + patId, String.class);
            return response;
        }

        /**
         * calculate age from a birthdate like "1981-10-28"
         * @param birthdate
         * @return
         */
        public long calculateAge(String birthdate) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date1 = LocalDate.parse(birthdate, formatter);
            LocalDate date2 = LocalDate.now();
            TemporalUnit unit = ChronoUnit.YEARS;
            return Period.between(date1, date2).get(unit);
        }

        public List<String> getHistoryOfPatient(int patId) throws JsonMappingException, JsonProcessingException {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8082/history/";
            ResponseEntity<String> response = restTemplate.getForEntity(url + patId, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            List<String> notesContent = new ArrayList<>();
            for(int i=0; i< root.size(); i++) {
                String note = root.get(i).get("note").get("noteContent").asText();
                notesContent.add(note);
            }
         return notesContent; 
        }

        public int calculateNumberOfTerms(List<String> notes) {
            int numberOfTerms = 0;
            for(int i=0; i<terms.size(); i++) {
                for(String note : notes) {
                    if (note.contains(terms.get(i))) {
                        System.out.println(terms.get(i));
                        terms.remove(terms.get(i));
                        numberOfTerms++;
                        
                    };
                }
               
            }
            return numberOfTerms;
        }

        public String givenNumberOfTermsGiveTheCorrespondingRiskName(int numberOfTerms, long age, String gender) {
            //si - ou + de 30 ans
            //si homme ou femme
            //si nombre de termes
            return "";
        }
}
