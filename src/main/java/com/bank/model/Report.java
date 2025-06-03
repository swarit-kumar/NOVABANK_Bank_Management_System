package com.bank.model;

import java.time.LocalDateTime;

public class Report {
    private int reportId;
    private String type;
    private LocalDateTime timestamp;
    private int staffId;
    private String staffName;

    // Constructors
    public Report() {}

    public Report(int reportId, String type, LocalDateTime timestamp, int staffId, String staffName) {
        this.reportId = reportId;
        this.type = type;
        this.timestamp = timestamp;
        this.staffId = staffId;
        this.staffName = staffName;
    }

    // Getters and Setters
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", staffId=" + staffId +
                ", staffName='" + staffName + '\'' +
                '}';
    }
}