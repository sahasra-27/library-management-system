package com.smartlibrary.mapper;

import com.smartlibrary.entity.*;
import com.smartlibrary.dto.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LibraryMapper {

    public static UserDto toUserDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus().name())
                .profilePhoto(user.getProfilePhoto())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .occupation(user.getOccupation())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static BookDto toBookDto(Book book) {
        if (book == null) return null;
        return BookDto.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .authorId(book.getAuthor() != null ? book.getAuthor().getId() : null)
                .authorName(book.getAuthor() != null ? book.getAuthor().getName() : null)
                .publisherId(book.getPublisher() != null ? book.getPublisher().getId() : null)
                .publisherName(book.getPublisher() != null ? book.getPublisher().getName() : null)
                .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                .language(book.getLanguage())
                .edition(book.getEdition())
                .publicationYear(book.getPublicationYear())
                .price(book.getPrice())
                .shelfNumber(book.getShelfNumber())
                .rackNumber(book.getRackNumber())
                .quantity(book.getQuantity())
                .availableQuantity(book.getAvailableQuantity())
                .description(book.getDescription())
                .coverImage(book.getCoverImage())
                .subtitle(book.getSubtitle())
                .numberOfPages(book.getNumberOfPages())
                .coverImageUrl(book.getCoverImageUrl())
                .pageCount(book.getPageCount())
                .status(book.getStatus().name())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }

    public static AuthorDto toAuthorDto(Author author) {
        if (author == null) return null;
        return new AuthorDto(author.getId(), author.getName(), author.getBiography());
    }

    public static PublisherDto toPublisherDto(Publisher publisher) {
        if (publisher == null) return null;
        return new PublisherDto(publisher.getId(), publisher.getName(), publisher.getAddress(), publisher.getPhone());
    }

    public static IssueResponse toIssueResponse(IssuedBook issue, Fine fine, ReturnedBook returnedBook) {
        if (issue == null) return null;
        Book book = issue.getBookCopy().getBook();
        return IssueResponse.builder()
                .id(issue.getId())
                .userId(issue.getUser().getId())
                .username(issue.getUser().getUsername())
                .userEmail(issue.getUser().getEmail())
                .bookId(book.getId())
                .bookTitle(book.getTitle())
                .barcode(issue.getBookCopy().getBarcode())
                .issueDate(issue.getIssueDate())
                .dueDate(issue.getDueDate())
                .returnDate(returnedBook != null ? returnedBook.getReturnDate() : null)
                .status(issue.getStatus().name())
                .fineAmount(fine != null ? fine.getAmount() : BigDecimal.ZERO)
                .bookCondition(returnedBook != null ? returnedBook.getBookCondition() : null)
                .authorName(book.getAuthor() != null ? book.getAuthor().getName() : null)
                .isbn(book.getIsbn())
                .build();
    }

    public static ReservationDto toReservationDto(Reservation reservation) {
        return toReservationDto(reservation, null);
    }

    public static ReservationDto toReservationDto(Reservation reservation, Integer queuePosition) {
        if (reservation == null) return null;
        return ReservationDto.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .username(reservation.getUser().getUsername())
                .bookId(reservation.getBook().getId())
                .bookTitle(reservation.getBook().getTitle())
                .isbn(reservation.getBook().getIsbn())
                .reservationDate(reservation.getReservationDate())
                .expiryDate(reservation.getExpiryDate())
                .queuePosition(queuePosition)
                .status(reservation.getStatus().name())
                .build();
    }

    public static FineDto toFineDto(Fine fine) {
        return toFineDto(fine, null);
    }

    public static FineDto toFineDto(Fine fine, ReturnedBook returnedBook) {
        if (fine == null) return null;
        
        Long issueId = fine.getIssuedBook() != null ? fine.getIssuedBook().getId() : null;
        String bookTitle = fine.getIssuedBook() != null ? fine.getIssuedBook().getBookCopy().getBook().getTitle() : "Manual Fine";
        String barcode = fine.getIssuedBook() != null ? fine.getIssuedBook().getBookCopy().getBarcode() : "N/A";
        LocalDateTime issueDate = fine.getIssuedBook() != null ? fine.getIssuedBook().getIssueDate() : null;
        
        LocalDateTime returnDate = returnedBook != null ? returnedBook.getReturnDate() : null;
        Long daysLate = null;
        
        if (fine.getIssuedBook() != null) {
            LocalDateTime end = returnDate != null ? returnDate : LocalDateTime.now();
            if (end.isAfter(fine.getIssuedBook().getDueDate())) {
                daysLate = java.time.Duration.between(fine.getIssuedBook().getDueDate(), end).toDays();
            }
        }

        return FineDto.builder()
                .id(fine.getId())
                .issueId(issueId)
                .userId(fine.getUser().getId())
                .username(fine.getUser().getUsername())
                .bookTitle(bookTitle)
                .barcode(barcode)
                .amount(fine.getAmount())
                .status(fine.getStatus().name())
                .paidDate(fine.getPaidDate())
                .issueDate(issueDate)
                .returnDate(returnDate)
                .dueDate(fine.getDueDate())
                .daysLate(daysLate)
                .reason(fine.getReason())
                .build();
    }

    public static ActivityLogDto toActivityLogDto(ActivityLog log) {
        if (log == null) return null;
        return ActivityLogDto.builder()
                .id(log.getId())
                .username(log.getUser() != null ? log.getUser().getUsername() : "System")
                .action(log.getAction())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
