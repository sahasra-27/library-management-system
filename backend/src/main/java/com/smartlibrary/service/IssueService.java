package com.smartlibrary.service;

import com.smartlibrary.entity.*;
import com.smartlibrary.dto.IssueRequest;
import com.smartlibrary.dto.ReturnRequest;
import com.smartlibrary.exception.LibraryException;
import com.smartlibrary.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class IssueService {

    @Autowired
    private IssuedBookRepository issuedBookRepository;

    @Autowired
    private ReturnedBookRepository returnedBookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ActivityLogService auditLog;

    @Transactional
    public IssuedBook issueBook(IssueRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new LibraryException("User not found", HttpStatus.NOT_FOUND));

        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            throw new LibraryException("User account is suspended and cannot borrow books.", HttpStatus.FORBIDDEN);
        }

        // Check active limits
        List<IssuedBook> activeIssues = issuedBookRepository.findByUserIdAndStatus(user.getId(), IssuedBook.IssueStatus.ISSUED);
        int maxLimit = settingsService.getMaxBooksLimit();
        if (activeIssues.size() >= maxLimit) {
            throw new LibraryException("User has reached their maximum borrowing limit of " + maxLimit + " books.", HttpStatus.BAD_REQUEST);
        }

        // Check unpaid fines
        List<Fine> unpaidFines = fineRepository.findUnpaidByUserId(user.getId());
        if (!unpaidFines.isEmpty()) {
            throw new LibraryException("User has outstanding unpaid fines. Please clear fines before borrowing.", HttpStatus.BAD_REQUEST);
        }

        // Find Book Copy
        BookCopy copy = bookCopyRepository.findByBarcode(request.getBarcode())
                .orElseThrow(() -> new LibraryException("Book copy with barcode " + request.getBarcode() + " not found.", HttpStatus.NOT_FOUND));

        if (copy.getStatus() != BookCopy.CopyStatus.AVAILABLE) {
            throw new LibraryException("Book copy is not available (Current Status: " + copy.getStatus() + ")", HttpStatus.BAD_REQUEST);
        }

        Book book = copy.getBook();
        if (book.getStatus() == Book.BookStatus.DELETED) {
            throw new LibraryException("Book has been deleted from catalog.", HttpStatus.BAD_REQUEST);
        }

        // Update status of Copy & Book quantities
        copy.setStatus(BookCopy.CopyStatus.BORROWED);
        bookCopyRepository.save(copy);

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        int borrowDays = settingsService.getBorrowDurationDays();
        IssuedBook issue = IssuedBook.builder()
                .user(user)
                .bookCopy(copy)
                .issueDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(borrowDays))
                .status(IssuedBook.IssueStatus.ISSUED)
                .build();

        IssuedBook savedIssue = issuedBookRepository.save(issue);

        // Audit Log
        auditLog.logActivity(user, "BOOK_ISSUED", "Issued book copy: " + copy.getBarcode());

        // Notify User
        notificationService.sendNotification(user, "ISSUE_SUCCESS", 
                "Successfully borrowed '" + book.getTitle() + "'. Due date: " + issue.getDueDate().toLocalDate());

        return savedIssue;
    }

    @Transactional
    public ReturnedBook returnBook(ReturnRequest request, User processor) {
        BookCopy copy = bookCopyRepository.findByBarcode(request.getBarcode())
                .orElseThrow(() -> new LibraryException("Book copy not found", HttpStatus.NOT_FOUND));

        if (copy.getStatus() != BookCopy.CopyStatus.BORROWED) {
            throw new LibraryException("This copy is not currently borrowed", HttpStatus.BAD_REQUEST);
        }

        // Find active checkout
        List<IssuedBook> activeList = issuedBookRepository.findAllActiveIssues();
        IssuedBook issue = activeList.stream()
                .filter(ib -> ib.getBookCopy().getId().equals(copy.getId()))
                .findFirst()
                .orElseThrow(() -> new LibraryException("No active checkout session found for this copy", HttpStatus.NOT_FOUND));

        if (processor.getRole() == com.smartlibrary.entity.User.Role.USER 
                && !issue.getUser().getId().equals(processor.getId())) {
            throw new LibraryException("You are not authorized to return a book checked out to another user.", HttpStatus.UNAUTHORIZED);
        }

        LocalDateTime now = LocalDateTime.now();

        // Update issue status
        issue.setStatus(IssuedBook.IssueStatus.RETURNED);
        issuedBookRepository.save(issue);

        // Update copy status & book quantity
        copy.setStatus(BookCopy.CopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

        Book book = copy.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        bookRepository.save(book);

        // Create return log
        ReturnedBook returnedBook = ReturnedBook.builder()
                .issuedBook(issue)
                .returnDate(now)
                .bookCondition(request.getBookCondition() != null ? request.getBookCondition() : "GOOD")
                .processedBy(processor)
                .build();
        ReturnedBook savedReturn = returnedBookRepository.save(returnedBook);

        // Check if overdue & generate fine
        if (now.isAfter(issue.getDueDate())) {
            long daysLate = Duration.between(issue.getDueDate(), now).toDays();
            if (daysLate > 0) {
                BigDecimal rate = settingsService.getFineAmount();
                BigDecimal fineAmount = rate.multiply(new BigDecimal(daysLate));

                Fine fine = Fine.builder()
                        .issuedBook(issue)
                        .user(issue.getUser())
                        .amount(fineAmount)
                        .reason("Overdue return fine for book: " + book.getTitle())
                        .dueDate(now.plusDays(7))
                        .status(Fine.FineStatus.UNPAID)
                        .build();
                fineRepository.save(fine);

                notificationService.sendNotification(issue.getUser(), "FINE_REMINDER", 
                        "Overdue return for '" + book.getTitle() + "'. A fine of ₹" + fineAmount + " has been added.");
            }
        }

        auditLog.logActivity(issue.getUser(), "BOOK_RETURNED", "Returned book copy: " + copy.getBarcode());

        notificationService.sendNotification(issue.getUser(), "RETURN_SUCCESS", 
                "Successfully returned '" + book.getTitle() + "'. Thank you!");

        // Reservation Queue Check: Notify first person waiting in queue
        List<Reservation> reservations = reservationRepository.findByBookIdAndStatus(book.getId(), Reservation.ReservationStatus.PENDING);
        if (!reservations.isEmpty()) {
            Reservation nextReservation = reservations.get(0);
            nextReservation.setStatus(Reservation.ReservationStatus.READY_FOR_PICKUP);
            nextReservation.setExpiryDate(LocalDateTime.now().plusDays(3));
            reservationRepository.save(nextReservation);

            notificationService.sendNotification(nextReservation.getUser(), "RESERVATION_AVAILABLE",
                    "The book '" + book.getTitle() + "' you reserved is now available! Please visit the desk to pick it up by " + nextReservation.getExpiryDate().toLocalDate() + ".");
        }

        return savedReturn;
    }

    @Transactional
    public IssuedBook renewBook(Long issueId) {
        IssuedBook issue = issuedBookRepository.findById(issueId)
                .orElseThrow(() -> new LibraryException("Issue record not found", HttpStatus.NOT_FOUND));

        if (issue.getStatus() != IssuedBook.IssueStatus.ISSUED) {
            throw new LibraryException("This book cannot be renewed (Current status: " + issue.getStatus() + ")", HttpStatus.BAD_REQUEST);
        }

        // Check if book has pending reservations
        List<Reservation> reservations = reservationRepository.findByBookIdAndStatus(
                issue.getBookCopy().getBook().getId(), Reservation.ReservationStatus.PENDING);
        if (!reservations.isEmpty()) {
            throw new LibraryException("Book is reserved by another student and cannot be renewed.", HttpStatus.BAD_REQUEST);
        }

        // Reset due date
        int duration = settingsService.getBorrowDurationDays();
        issue.setDueDate(LocalDateTime.now().plusDays(duration));
        IssuedBook saved = issuedBookRepository.save(issue);

        auditLog.logActivity(issue.getUser(), "BOOK_RENEWED", "Renewed checkout ID: " + issueId);
        notificationService.sendNotification(issue.getUser(), "RENEW_SUCCESS", 
                "Successfully renewed '" + issue.getBookCopy().getBook().getTitle() + "'. New due date: " + saved.getDueDate().toLocalDate());

        return saved;
    }

    @Transactional
    public Reservation reserveBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new LibraryException("User not found", HttpStatus.NOT_FOUND));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new LibraryException("Book not found", HttpStatus.NOT_FOUND));

        if (book.getAvailableQuantity() > 0) {
            throw new LibraryException("Book is currently available for checkout. No reservation needed.", HttpStatus.BAD_REQUEST);
        }

        // Check if already borrowed
        if (issuedBookRepository.existsActiveIssueForUserAndBook(userId, bookId)) {
            throw new LibraryException("You cannot reserve this book because you currently have it checked out.", HttpStatus.BAD_REQUEST);
        }

        // Check if already reserved
        boolean existsActive = reservationRepository.existsByUserIdAndBookIdAndStatusIn(
                userId, bookId, List.of(Reservation.ReservationStatus.PENDING, Reservation.ReservationStatus.APPROVED, Reservation.ReservationStatus.READY_FOR_PICKUP)
        );
        if (existsActive) {
            throw new LibraryException("You have already reserved this book.", HttpStatus.BAD_REQUEST);
        }

        List<Reservation> activeReservations = reservationRepository.findByBookIdAndStatusInOrderByReservationDateAsc(
                bookId, List.of(Reservation.ReservationStatus.PENDING, Reservation.ReservationStatus.APPROVED, Reservation.ReservationStatus.READY_FOR_PICKUP)
        );

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .status(Reservation.ReservationStatus.PENDING)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        auditLog.logActivity(user, "BOOK_RESERVED", "Reserved book: " + book.getTitle());
        notificationService.sendNotification(user, "RESERVATION_CONFIRMED", 
                "You have successfully reserved '" + book.getTitle() + "'. Position in queue: " + (activeReservations.size() + 1));

        return saved;
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new LibraryException("Reservation record not found", HttpStatus.NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new LibraryException("Unauthorized access to cancel reservation", HttpStatus.UNAUTHORIZED);
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        auditLog.logActivity(reservation.getUser(), "RESERVATION_CANCELLED", "Cancelled reservation ID: " + reservationId);
    }

    @Transactional
    public void payFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new LibraryException("Fine record not found", HttpStatus.NOT_FOUND));

        if (fine.getStatus() == Fine.FineStatus.PAID) {
            throw new LibraryException("Fine is already paid", HttpStatus.BAD_REQUEST);
        }

        fine.setStatus(Fine.FineStatus.PAID);
        fine.setPaidDate(LocalDateTime.now());
        fineRepository.save(fine);

        String bookTitle = fine.getIssuedBook() != null ? fine.getIssuedBook().getBookCopy().getBook().getTitle() : "Manual Fine";
        auditLog.logActivity(fine.getUser(), "FINE_PAID", "Paid fine of ₹" + fine.getAmount());
        notificationService.sendNotification(fine.getUser(), "FINE_PAID", 
                "Successfully cleared fine of ₹" + fine.getAmount() + " for '" + bookTitle + "'.");
    }

    @Transactional
    public void unpayFine(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new LibraryException("Fine record not found", HttpStatus.NOT_FOUND));

        if (fine.getStatus() != Fine.FineStatus.PAID) {
            throw new LibraryException("Fine is not paid", HttpStatus.BAD_REQUEST);
        }

        fine.setStatus(Fine.FineStatus.UNPAID);
        fine.setPaidDate(null);
        fineRepository.save(fine);

        auditLog.logActivity(fine.getUser(), "FINE_UNPAID", "Marked fine of ₹" + fine.getAmount() + " as unpaid");
    }


    public Integer getQueuePosition(Reservation reservation) {
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING 
                && reservation.getStatus() != Reservation.ReservationStatus.APPROVED 
                && reservation.getStatus() != Reservation.ReservationStatus.READY_FOR_PICKUP) {
            return null;
        }
        List<Reservation> active = reservationRepository.findByBookIdAndStatusInOrderByReservationDateAsc(
                reservation.getBook().getId(),
                List.of(Reservation.ReservationStatus.PENDING, Reservation.ReservationStatus.APPROVED, Reservation.ReservationStatus.READY_FOR_PICKUP)
        );
        for (int i = 0; i < active.size(); i++) {
            if (active.get(i).getId().equals(reservation.getId())) {
                return i + 1;
            }
        }
        return null;
    }

    @Transactional
    public Reservation createReservationAdmin(Reservation reservation, String statusStr) {
        // Validation Checks
        Long userId = reservation.getUser().getId();
        Long bookId = reservation.getBook().getId();

        // Prevent duplicate active reservations
        boolean existsActive = reservationRepository.existsByUserIdAndBookIdAndStatusIn(
                userId, bookId, List.of(Reservation.ReservationStatus.PENDING, Reservation.ReservationStatus.APPROVED, Reservation.ReservationStatus.READY_FOR_PICKUP)
        );
        if (existsActive) {
            throw new LibraryException("Duplicate active reservation exists for this user and book.", HttpStatus.BAD_REQUEST);
        }

        // Prevent reserving currently borrowed book
        if (issuedBookRepository.existsActiveIssueForUserAndBook(userId, bookId)) {
            throw new LibraryException("User has currently checked out this book and cannot reserve it.", HttpStatus.BAD_REQUEST);
        }

        if (statusStr != null) {
            reservation.setStatus(Reservation.ReservationStatus.valueOf(statusStr.toUpperCase().replace(" ", "_")));
        } else {
            reservation.setStatus(Reservation.ReservationStatus.PENDING);
        }

        Reservation saved = reservationRepository.save(reservation);
        auditLog.logActivity(saved.getUser(), "RESERVATION_CREATED_ADMIN", "Admin reserved book: " + saved.getBook().getTitle());
        return saved;
    }

    @Transactional
    public Reservation updateReservation(Long id, Reservation updateDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Reservation not found", HttpStatus.NOT_FOUND));

        reservation.setStatus(updateDetails.getStatus());
        reservation.setExpiryDate(updateDetails.getExpiryDate());
        if (updateDetails.getReservationDate() != null) {
            reservation.setReservationDate(updateDetails.getReservationDate());
        }

        Reservation saved = reservationRepository.save(reservation);
        auditLog.logActivity(saved.getUser(), "RESERVATION_UPDATED", "Updated reservation ID: " + id);
        return saved;
    }

    @Transactional
    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Reservation not found", HttpStatus.NOT_FOUND));
        reservationRepository.delete(reservation);
        auditLog.logActivity(reservation.getUser(), "RESERVATION_DELETED", "Deleted reservation ID: " + id);
    }

    @Transactional
    public Fine createFineManual(Fine fine, Long userId, Long issueId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new LibraryException("User not found", HttpStatus.NOT_FOUND));
        fine.setUser(user);

        if (fine.getAmount() == null || fine.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new LibraryException("Fine amount cannot be negative.", HttpStatus.BAD_REQUEST);
        }

        if (issueId != null) {
            IssuedBook issue = issuedBookRepository.findById(issueId)
                    .orElseThrow(() -> new LibraryException("Issued book record not found", HttpStatus.NOT_FOUND));
            fine.setIssuedBook(issue);
        }

        if (fine.getStatus() == null) {
            fine.setStatus(Fine.FineStatus.UNPAID);
        }
        if (fine.getStatus() == Fine.FineStatus.PAID && fine.getPaidDate() == null) {
            fine.setPaidDate(LocalDateTime.now());
        }

        Fine saved = fineRepository.save(fine);
        auditLog.logActivity(user, "FINE_CREATED_MANUAL", "Created manual fine of ₹" + saved.getAmount());
        return saved;
    }

    @Transactional
    public Fine updateFine(Long id, Fine updateDetails) {
        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Fine not found", HttpStatus.NOT_FOUND));

        if (updateDetails.getAmount() == null || updateDetails.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new LibraryException("Fine amount cannot be negative.", HttpStatus.BAD_REQUEST);
        }

        fine.setAmount(updateDetails.getAmount());
        fine.setReason(updateDetails.getReason());
        fine.setDueDate(updateDetails.getDueDate());
        
        Fine.FineStatus prevStatus = fine.getStatus();
        fine.setStatus(updateDetails.getStatus());
        
        if (updateDetails.getStatus() == Fine.FineStatus.PAID && prevStatus != Fine.FineStatus.PAID) {
            fine.setPaidDate(LocalDateTime.now());
        } else if (updateDetails.getStatus() != Fine.FineStatus.PAID) {
            fine.setPaidDate(null);
        }

        Fine saved = fineRepository.save(fine);
        auditLog.logActivity(saved.getUser(), "FINE_UPDATED", "Updated fine ID: " + id);
        return saved;
    }

    @Transactional
    public void deleteFine(Long id) {
        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Fine not found", HttpStatus.NOT_FOUND));
        fineRepository.delete(fine);
        auditLog.logActivity(fine.getUser(), "FINE_DELETED", "Deleted fine ID: " + id);
    }

    public java.util.Map<String, BigDecimal> getFineStats() {
        return getFineStats(null);
    }

    public java.util.Map<String, BigDecimal> getFineStats(Long userId) {
        BigDecimal totalPaid;
        BigDecimal totalUnpaid;
        if (userId != null) {
            totalPaid = fineRepository.sumTotalPaidFinesByUserId(userId);
            totalUnpaid = fineRepository.sumTotalUnpaidFinesByUserId(userId);
        } else {
            totalPaid = fineRepository.sumTotalPaidFines();
            totalUnpaid = fineRepository.sumTotalUnpaidFines();
        }
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;
        if (totalUnpaid == null) totalUnpaid = BigDecimal.ZERO;

        java.util.Map<String, BigDecimal> stats = new java.util.HashMap<>();
        stats.put("totalCollected", totalPaid);
        stats.put("totalUnpaid", totalUnpaid);
        return stats;
    }

    public Page<IssuedBook> searchIssues(Long userId, String query, Pageable pageable) {
        return issuedBookRepository.searchIssues(userId, query, pageable);
    }

    public Page<ReturnedBook> searchReturns(Long userId, String query, Pageable pageable) {
        return returnedBookRepository.searchReturns(userId, query, pageable);
    }

    public Page<Reservation> searchReservations(Long userId, String statusStr, String query, Pageable pageable) {
        Reservation.ReservationStatus status = null;
        if (statusStr != null && !statusStr.isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
            status = Reservation.ReservationStatus.valueOf(statusStr.toUpperCase().replace(" ", "_"));
        }
        return reservationRepository.searchReservations(userId, status, query, pageable);
    }

    public Page<Fine> searchFines(Long userId, String statusStr, String query, Pageable pageable) {
        Fine.FineStatus status = null;
        if (statusStr != null && !statusStr.isEmpty() && !statusStr.equalsIgnoreCase("ALL")) {
            status = Fine.FineStatus.valueOf(statusStr.toUpperCase());
        }
        return fineRepository.searchFines(userId, status, query, pageable);
    }
}
