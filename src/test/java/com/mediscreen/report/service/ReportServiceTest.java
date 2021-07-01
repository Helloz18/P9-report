package com.mediscreen.report.service;

import static org.junit.Assert.assertEquals;

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

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@WebMvcTest(ReportService.class)
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    public void givenBirthDateThenCalculateAge() {
        String birthdate = "2000-01-01";
        String today = "2021-06-01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date1 = LocalDate.parse(birthdate, formatter);
        // use a specific date to avoid failing in the future
        // LocalDate date2 = LocalDate.now();
        LocalDate date2 = LocalDate.parse(today, formatter);
        TemporalUnit unit = ChronoUnit.YEARS;
        assertEquals(21, Period.between(date1, date2).get(unit));
    }

    @Test
    public void calculateAge() {
        String birthdate = LocalDate.now().toString();
        assertEquals(0, reportService.calculateAge(birthdate));
    }

    @Test
    public void givenTermsWhenTermIsPresentTermShouldBeFound() {
        List<String> terms = new ArrayList<>(Arrays.asList(
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
            "Anticorps"));

        List<String> notes = new ArrayList<>(
                Arrays.asList(
                    "une note avec Hémoglobine A1C", 
                    "Microalbumine", 
                    "taille", 
                    "une note avec POIDS",
                    "FuMeUr", 
                    "anormal", 
                    "CHOLEstérol", 
                    "VERTIGE", 
                    "rechute", 
                    "Réaction,  anticorps, taille"));
        //use of Set to count a term once, even if it appears multiple times
        Set<String> termsFound = new HashSet<>();
        for (int i = 0; i < terms.size(); i++) {
            for (String note : notes) {
                if (note.toLowerCase().contains(terms.get(i).toLowerCase())) {
                    termsFound.add(terms.get(i));
                }
            }
        }
        int numberOfTerms = termsFound.size();
        assertEquals(11, numberOfTerms);
    }

    @Test
    public void givenNotesThenCalculateTermsShouldReturnRightNumber() {
        List<String> notes = new ArrayList<>(
                Arrays.asList(
                    "une note avec Hémoglobine A1C", 
                    "Microalbumine", 
                    "taille", 
                    "une note avec POIDS",
                    "FuMeUr", 
                    "anormal", 
                    "CHOLEstérol", 
                    "VERTIGE", 
                    "rechute", 
                    "Réaction,  anticorps, taille"));
        int numberOfTerms = reportService.calculateNumberOfTerms(notes);
        assertEquals(11, numberOfTerms);
    }

    @Test
    public void givenNumberOfTermsAndGivenGenderAndAgeThenRightRiskIsSet() {
        int termsZero = 0;
        int termsOne = 1;
        int termsTwo = 2;
        int termsThree = 3;
        int termsFour = 4;
        int termsFive = 5;
        int termsSix = 6;
        int termsSeven = 7;
        int termsHeight = 8;
        int termsNine = 9;
        int termsTen = 10;
        int termsEleven = 11;
        int termsTwelve = 12;

        long ageInferiorTo30 = 20;
        long ageSuperiorTo30 = 31;

        String genderF = String.valueOf('F');
        String genderM = String.valueOf('M');

        assertEquals("None", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsZero, ageInferiorTo30, genderF));
        assertEquals("None", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsOne, ageInferiorTo30, genderF));
        assertEquals("None", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsTwo, ageInferiorTo30, genderF));
        assertEquals("None", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsThree, ageInferiorTo30, genderF));
        assertEquals("In danger", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsFour, ageInferiorTo30, genderF));
        assertEquals("In danger", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsFive, ageInferiorTo30, genderF));
        assertEquals("In danger", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsSix, ageInferiorTo30, genderF));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsSeven, ageInferiorTo30, genderF));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsHeight, ageInferiorTo30, genderF));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsNine, ageInferiorTo30, genderF));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsTen, ageInferiorTo30, genderF));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsEleven, ageInferiorTo30, genderF));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsTwelve, ageInferiorTo30, genderF));

        assertEquals("None", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsZero, ageInferiorTo30, genderM));
        assertEquals("None", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsOne, ageInferiorTo30, genderM));
        assertEquals("None", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsTwo, ageInferiorTo30, genderM));
        assertEquals("In danger", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsThree, ageInferiorTo30, genderM));
        assertEquals("In danger", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsFour, ageInferiorTo30, genderM));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsFive, ageInferiorTo30, genderM));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsSix, ageInferiorTo30, genderM));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsSeven, ageInferiorTo30, genderM));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsHeight, ageInferiorTo30, genderM));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsNine, ageInferiorTo30, genderM));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsTen, ageInferiorTo30, genderM));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsEleven, ageInferiorTo30, genderM));
        assertEquals("Early onset", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsTwelve, ageInferiorTo30, genderM));

        assertEquals("Borderline", reportService.givenNumberOfTermsGiveTheCorrespondingRiskName(termsThree, ageSuperiorTo30, genderM));
    }
}
