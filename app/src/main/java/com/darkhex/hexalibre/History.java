package com.darkhex.hexalibre;

public class History {
    private String ISBN;
    private String Issue;
    private String Return;
    private String Status;

    public History(String ISBN, String issue, String aReturn, String status) {
        this.ISBN = ISBN;
        Issue = issue;
        Return = aReturn;
        Status = status;
    }

    public History() {
    }

    public String getISBN() {
        return ISBN;
    }

    public String getIssue() {
        return Issue;
    }

    public String getReturn() {
        return Return;
    }

    public String getStatus() {
        return Status;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setIssue(String issue) {
        Issue = issue;
    }

    public void setReturn(String aReturn) {
        Return = aReturn;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
