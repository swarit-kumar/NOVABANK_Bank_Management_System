package com.bank.service;

import com.bank.dao.ReportDAO;
import com.bank.model.Report;

import java.util.List;

public class ReportService {
    private ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    // Create a new report
    public boolean createReport(String type, int staffId) {
        return reportDAO.createReport(type, staffId);
    }

    // Get all reports
    public List<Report> getAllReports() {
        return reportDAO.getAllReports();
    }

    // Get reports by staff member
    public List<Report> getReportsByStaff(int staffId) {
        return reportDAO.getReportsByStaff(staffId);
    }
}