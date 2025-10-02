package models;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

import appmodules.applicant.Applicant;
import gui.ColorPalette;

public class Enquiry implements Serializable {
    private static final long serialVersionUID = 1L;
    private Applicant applicant;
    private Project project;
    private String subject, content, address, category, answer = "1";
    private String color;
    private User replier;
    private Boolean answered;
    private LocalDate replyDate;

    // private static final Map<String, String> CATEGORY_MAP = Map.of(
    //     "1", "Eligibility",
    //     "2", "Maintenance",
    //     "3", "Estate Renewal",
    //     "4", "Payment",
    //     "5", "GST Refund",
    //     "6", "Resale Flats",
    //     "7", "Shops, Offices & Spaces"
    // );

    public Enquiry(Applicant applicant, Project project, String subject, String content) {
        this.applicant = applicant;
        this.subject = subject;
        this.content = content;
        this.answer = "";
        this.project = project;
        this.answered = false;
        this.color = ColorPalette.RED;
    }

    // public void setCategory(String categoryValue) {
    //     this.category = CATEGORY_MAP.entrySet()
    //         .stream()
    //         .filter(entry -> entry.getKey().equals(categoryValue))
    //         .peek(entry -> this.category = entry.getKey())
    //         .map(Map.Entry::getValue)
    //         .findFirst()
    //         .orElse("Unknown");
    // }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setReplier(User replier) {
        this.replier = replier;
    }
    public String getSubject() {
        return subject;
    }

    public String getColor(){
        return color;
    }
    
    public String getAnswered(){
        if(answered){
            return "Yes";
        } else{
            return "No";
        }
    }

    public String getContent() {
        return content;
    }

    public String getAnswer() {
        return answer;
    }

    public User getReplier(){
        return replier;
    }

    // public String getCategory() {
    //     return category;
    // }

    public Project getProject() {
        return project;
    }


    public void setAnswer(String answer, User replier) {
        this.answer = answer;
        this.color = ColorPalette.GREEN;
        this.replier = replier;
        this.replyDate = LocalDate.now();
        this.answered = true;
    }

    public void clearAnswer(){
        this.answer = "";
        this.color = ColorPalette.RED;
        this.answered = false;
    }
}
