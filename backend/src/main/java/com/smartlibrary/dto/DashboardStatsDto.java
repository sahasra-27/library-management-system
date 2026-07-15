package com.smartlibrary.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    // Admin Stats
    private long totalBooks;
    private long availableBooks;
    private long borrowedBooks;
    private long returnedBooks;
    private long lostBooks;
    private long registeredUsers;
    private long pendingRequests;
    private long issuedToday;
    private long returnedToday;

    // Chart Data
    private Map<String, Long> booksByCategory;
    private Map<String, Long> monthlyIssues;
    private Map<String, Long> monthlyReturns;
    private List<BookDto> mostBorrowedBooks;

    // Student Stats (if queried for student)
    private long studentBorrowed;
    private long studentDue;
    private long studentOverdue;
    private long studentReturned;
    private long studentFavorites;
    private int profileCompletionPercentage;
    private long studentReserved;
    private double studentOutstandingFines;

    // Constructors
    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalBooks, long availableBooks, long borrowedBooks, long returnedBooks, long lostBooks, long registeredUsers, long pendingRequests, long issuedToday, long returnedToday, Map<String, Long> booksByCategory, Map<String, Long> monthlyIssues, Map<String, Long> monthlyReturns, List<BookDto> mostBorrowedBooks, long studentBorrowed, long studentDue, long studentOverdue, long studentReturned, long studentFavorites, int profileCompletionPercentage, long studentReserved, double studentOutstandingFines) {
        this.totalBooks = totalBooks;
        this.availableBooks = availableBooks;
        this.borrowedBooks = borrowedBooks;
        this.returnedBooks = returnedBooks;
        this.lostBooks = lostBooks;
        this.registeredUsers = registeredUsers;
        this.pendingRequests = pendingRequests;
        this.issuedToday = issuedToday;
        this.returnedToday = returnedToday;
        this.booksByCategory = booksByCategory;
        this.monthlyIssues = monthlyIssues;
        this.monthlyReturns = monthlyReturns;
        this.mostBorrowedBooks = mostBorrowedBooks;
        this.studentBorrowed = studentBorrowed;
        this.studentDue = studentDue;
        this.studentOverdue = studentOverdue;
        this.studentReturned = studentReturned;
        this.studentFavorites = studentFavorites;
        this.profileCompletionPercentage = profileCompletionPercentage;
        this.studentReserved = studentReserved;
        this.studentOutstandingFines = studentOutstandingFines;
    }

    // Getters and Setters
    public long getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(long totalBooks) {
        this.totalBooks = totalBooks;
    }

    public long getAvailableBooks() {
        return availableBooks;
    }

    public void setAvailableBooks(long availableBooks) {
        this.availableBooks = availableBooks;
    }

    public long getBorrowedBooks() {
        return borrowedBooks;
    }

    public void setBorrowedBooks(long borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public long getReturnedBooks() {
        return returnedBooks;
    }

    public void setReturnedBooks(long returnedBooks) {
        this.returnedBooks = returnedBooks;
    }

    public long getLostBooks() {
        return lostBooks;
    }

    public void setLostBooks(long lostBooks) {
        this.lostBooks = lostBooks;
    }

    public long getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(long registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public long getIssuedToday() {
        return issuedToday;
    }

    public void setIssuedToday(long issuedToday) {
        this.issuedToday = issuedToday;
    }

    public long getReturnedToday() {
        return returnedToday;
    }

    public void setReturnedToday(long returnedToday) {
        this.returnedToday = returnedToday;
    }

    public Map<String, Long> getBooksByCategory() {
        return booksByCategory;
    }

    public void setBooksByCategory(Map<String, Long> booksByCategory) {
        this.booksByCategory = booksByCategory;
    }

    public Map<String, Long> getMonthlyIssues() {
        return monthlyIssues;
    }

    public void setMonthlyIssues(Map<String, Long> monthlyIssues) {
        this.monthlyIssues = monthlyIssues;
    }

    public Map<String, Long> getMonthlyReturns() {
        return monthlyReturns;
    }

    public void setMonthlyReturns(Map<String, Long> monthlyReturns) {
        this.monthlyReturns = monthlyReturns;
    }

    public List<BookDto> getMostBorrowedBooks() {
        return mostBorrowedBooks;
    }

    public void setMostBorrowedBooks(List<BookDto> mostBorrowedBooks) {
        this.mostBorrowedBooks = mostBorrowedBooks;
    }

    public long getStudentBorrowed() {
        return studentBorrowed;
    }

    public void setStudentBorrowed(long studentBorrowed) {
        this.studentBorrowed = studentBorrowed;
    }

    public long getStudentDue() {
        return studentDue;
    }

    public void setStudentDue(long studentDue) {
        this.studentDue = studentDue;
    }

    public long getStudentOverdue() {
        return studentOverdue;
    }

    public void setStudentOverdue(long studentOverdue) {
        this.studentOverdue = studentOverdue;
    }

    public long getStudentReturned() {
        return studentReturned;
    }

    public void setStudentReturned(long studentReturned) {
        this.studentReturned = studentReturned;
    }

    public long getStudentFavorites() {
        return studentFavorites;
    }

    public void setStudentFavorites(long studentFavorites) {
        this.studentFavorites = studentFavorites;
    }

    public int getProfileCompletionPercentage() {
        return profileCompletionPercentage;
    }

    public void setProfileCompletionPercentage(int profileCompletionPercentage) {
        this.profileCompletionPercentage = profileCompletionPercentage;
    }

    public long getStudentReserved() {
        return studentReserved;
    }

    public void setStudentReserved(long studentReserved) {
        this.studentReserved = studentReserved;
    }

    public double getStudentOutstandingFines() {
        return studentOutstandingFines;
    }

    public void setStudentOutstandingFines(double studentOutstandingFines) {
        this.studentOutstandingFines = studentOutstandingFines;
    }

    // Builder
    public static DashboardStatsDtoBuilder builder() {
        return new DashboardStatsDtoBuilder();
    }

    public static class DashboardStatsDtoBuilder {
        private long totalBooks;
        private long availableBooks;
        private long borrowedBooks;
        private long returnedBooks;
        private long lostBooks;
        private long registeredUsers;
        private long pendingRequests;
        private long issuedToday;
        private long returnedToday;
        private Map<String, Long> booksByCategory;
        private Map<String, Long> monthlyIssues;
        private Map<String, Long> monthlyReturns;
        private List<BookDto> mostBorrowedBooks;
        private long studentBorrowed;
        private long studentDue;
        private long studentOverdue;
        private long studentReturned;
        private long studentFavorites;
        private int profileCompletionPercentage;
        private long studentReserved;
        private double studentOutstandingFines;

        DashboardStatsDtoBuilder() {
        }

        public DashboardStatsDtoBuilder totalBooks(long totalBooks) {
            this.totalBooks = totalBooks;
            return this;
        }

        public DashboardStatsDtoBuilder availableBooks(long availableBooks) {
            this.availableBooks = availableBooks;
            return this;
        }

        public DashboardStatsDtoBuilder borrowedBooks(long borrowedBooks) {
            this.borrowedBooks = borrowedBooks;
            return this;
        }

        public DashboardStatsDtoBuilder returnedBooks(long returnedBooks) {
            this.returnedBooks = returnedBooks;
            return this;
        }

        public DashboardStatsDtoBuilder lostBooks(long lostBooks) {
            this.lostBooks = lostBooks;
            return this;
        }

        public DashboardStatsDtoBuilder registeredUsers(long registeredUsers) {
            this.registeredUsers = registeredUsers;
            return this;
        }

        public DashboardStatsDtoBuilder pendingRequests(long pendingRequests) {
            this.pendingRequests = pendingRequests;
            return this;
        }

        public DashboardStatsDtoBuilder issuedToday(long issuedToday) {
            this.issuedToday = issuedToday;
            return this;
        }

        public DashboardStatsDtoBuilder returnedToday(long returnedToday) {
            this.returnedToday = returnedToday;
            return this;
        }

        public DashboardStatsDtoBuilder booksByCategory(Map<String, Long> booksByCategory) {
            this.booksByCategory = booksByCategory;
            return this;
        }

        public DashboardStatsDtoBuilder monthlyIssues(Map<String, Long> monthlyIssues) {
            this.monthlyIssues = monthlyIssues;
            return this;
        }

        public DashboardStatsDtoBuilder monthlyReturns(Map<String, Long> monthlyReturns) {
            this.monthlyReturns = monthlyReturns;
            return this;
        }

        public DashboardStatsDtoBuilder mostBorrowedBooks(List<BookDto> mostBorrowedBooks) {
            this.mostBorrowedBooks = mostBorrowedBooks;
            return this;
        }

        public DashboardStatsDtoBuilder studentBorrowed(long studentBorrowed) {
            this.studentBorrowed = studentBorrowed;
            return this;
        }

        public DashboardStatsDtoBuilder studentDue(long studentDue) {
            this.studentDue = studentDue;
            return this;
        }

        public DashboardStatsDtoBuilder studentOverdue(long studentOverdue) {
            this.studentOverdue = studentOverdue;
            return this;
        }

        public DashboardStatsDtoBuilder studentReturned(long studentReturned) {
            this.studentReturned = studentReturned;
            return this;
        }

        public DashboardStatsDtoBuilder studentFavorites(long studentFavorites) {
            this.studentFavorites = studentFavorites;
            return this;
        }

        public DashboardStatsDtoBuilder profileCompletionPercentage(int profileCompletionPercentage) {
            this.profileCompletionPercentage = profileCompletionPercentage;
            return this;
        }

        public DashboardStatsDtoBuilder studentReserved(long studentReserved) {
            this.studentReserved = studentReserved;
            return this;
        }

        public DashboardStatsDtoBuilder studentOutstandingFines(double studentOutstandingFines) {
            this.studentOutstandingFines = studentOutstandingFines;
            return this;
        }

        public DashboardStatsDto build() {
            return new DashboardStatsDto(totalBooks, availableBooks, borrowedBooks, returnedBooks, lostBooks, registeredUsers, pendingRequests, issuedToday, returnedToday, booksByCategory, monthlyIssues, monthlyReturns, mostBorrowedBooks, studentBorrowed, studentDue, studentOverdue, studentReturned, studentFavorites, profileCompletionPercentage, studentReserved, studentOutstandingFines);
        }
    }
}
