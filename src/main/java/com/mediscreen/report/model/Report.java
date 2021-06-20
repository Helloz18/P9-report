package com.mediscreen.report.model;

public class Report {
    
    private String patientFirstName;
    private String patientLastName;
    private long patientAge;
    private String risk;

    
    public Report() {
    }

    public Report(String patientFirstName, String patientLastName, long patientAge, String risk) {
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientAge = patientAge;
        this.risk = risk;
    }
    
    public String getPatientFirstName() {
        return patientFirstName;
    }
    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }
    public String getPatientLastName() {
        return patientLastName;
    }
    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }
    public long getPatientAge() {
        return patientAge;
    }
    public void setPatientAge(long patientAge) {
        this.patientAge = patientAge;
    }
    public String getRisk() {
        return risk;
    }
    public void setRisk(String risk) {
        this.risk = risk;
    }

    @Override
    public String toString() {
        return "Report [patientAge=" + patientAge + ", patientFirstName=" + patientFirstName + ", patientLastName="
                + patientLastName + ", risk=" + risk + "]";
    }

    
}
