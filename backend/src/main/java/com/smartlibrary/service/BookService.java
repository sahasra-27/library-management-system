package com.smartlibrary.service;

import com.smartlibrary.entity.*;
import com.smartlibrary.dto.BookDto;
import com.smartlibrary.exception.LibraryException;
import com.smartlibrary.repository.*;
import com.smartlibrary.dto.OpenLibraryWorkDetailDto;
import com.smartlibrary.service.OpenLibraryService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private ActivityLogService auditLog;

    @Autowired
    private OpenLibraryService openLibraryService;

    private final Path rootUploads = Paths.get("uploads");

    public BookService() {
        try {
            if (!Files.exists(rootUploads)) {
                Files.createDirectories(rootUploads);
            }
        } catch (IOException e) {
            // Directory creation failure
        }
    }

    public Page<Book> searchBooks(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return bookRepository.findAllActive(pageable);
        }
        return bookRepository.searchBooksActive(query.trim(), pageable);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Book not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Book createBook(BookDto dto, MultipartFile file) {
        if (bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new LibraryException("ISBN already exists: " + dto.getIsbn(), HttpStatus.BAD_REQUEST);
        }

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new LibraryException("Author not found", HttpStatus.NOT_FOUND));
        Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                .orElseThrow(() -> new LibraryException("Publisher not found", HttpStatus.NOT_FOUND));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new LibraryException("Category not found", HttpStatus.NOT_FOUND));

        Book book = Book.builder()
                .isbn(dto.getIsbn())
                .title(dto.getTitle())
                .author(author)
                .publisher(publisher)
                .category(category)
                .language(dto.getLanguage())
                .edition(dto.getEdition())
                .publicationYear(dto.getPublicationYear())
                .price(dto.getPrice())
                .shelfNumber(dto.getShelfNumber())
                .rackNumber(dto.getRackNumber())
                .quantity(dto.getQuantity())
                .availableQuantity(dto.getQuantity())
                .description(dto.getDescription())
                .subtitle(dto.getSubtitle())
                .numberOfPages(dto.getNumberOfPages())
                .coverImageUrl(dto.getCoverImageUrl())
                .pageCount(dto.getPageCount())
                .status(Book.BookStatus.ACTIVE)
                .build();

        if (file != null && !file.isEmpty()) {
            String imagePath = saveImage(file);
            book.setCoverImage(imagePath);
        } else if (dto.getCoverImage() != null && !dto.getCoverImage().isEmpty()) {
            book.setCoverImage(dto.getCoverImage());
            book.setCoverImageUrl(dto.getCoverImage());
        } else if (dto.getCoverImageUrl() != null && !dto.getCoverImageUrl().isEmpty()) {
            book.setCoverImage(dto.getCoverImageUrl());
            book.setCoverImageUrl(dto.getCoverImageUrl());
        }

        Book savedBook = bookRepository.save(book);

        // Generate individual book copy entries for checkouts
        for (int i = 1; i <= savedBook.getQuantity(); i++) {
            String barcode = "BAR-" + savedBook.getIsbn() + "-" + String.format("%03d", i);
            BookCopy copy = BookCopy.builder()
                    .book(savedBook)
                    .barcode(barcode)
                    .copyNumber(i)
                    .status(BookCopy.CopyStatus.AVAILABLE)
                    .build();
            bookCopyRepository.save(copy);
        }

        auditLog.logActivity(null, "BOOK_ADDED", "Added book title: " + savedBook.getTitle() + " (ISBN: " + savedBook.getIsbn() + ")");
        return savedBook;
    }

    @Transactional
    public Book updateBook(Long id, BookDto dto, MultipartFile file) {
        Book book = getBookById(id);

        if (!book.getIsbn().equals(dto.getIsbn()) && bookRepository.existsByIsbn(dto.getIsbn())) {
            throw new LibraryException("ISBN already exists: " + dto.getIsbn(), HttpStatus.BAD_REQUEST);
        }

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new LibraryException("Author not found", HttpStatus.NOT_FOUND));
        Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                .orElseThrow(() -> new LibraryException("Publisher not found", HttpStatus.NOT_FOUND));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new LibraryException("Category not found", HttpStatus.NOT_FOUND));

        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setCategory(category);
        book.setLanguage(dto.getLanguage());
        book.setEdition(dto.getEdition());
        book.setPublicationYear(dto.getPublicationYear());
        book.setPrice(dto.getPrice());
        book.setShelfNumber(dto.getShelfNumber());
        book.setRackNumber(dto.getRackNumber());
        book.setDescription(dto.getDescription());
        book.setSubtitle(dto.getSubtitle());
        book.setNumberOfPages(dto.getNumberOfPages());
        book.setCoverImageUrl(dto.getCoverImageUrl());
        book.setPageCount(dto.getPageCount());

        // Handle quantity update: adjust available copies
        int diff = dto.getQuantity() - book.getQuantity();
        if (diff > 0) {
            // Added new copies
            for (int i = book.getQuantity() + 1; i <= dto.getQuantity(); i++) {
                String barcode = "BAR-" + book.getIsbn() + "-" + String.format("%03d", i);
                BookCopy copy = BookCopy.builder()
                        .book(book)
                        .barcode(barcode)
                        .copyNumber(i)
                        .status(BookCopy.CopyStatus.AVAILABLE)
                        .build();
                bookCopyRepository.save(copy);
            }
            book.setAvailableQuantity(book.getAvailableQuantity() + diff);
            book.setQuantity(dto.getQuantity());
        } else if (diff < 0) {
            // Reduced copies: remove available ones starting from end
            int toRemove = Math.abs(diff);
            List<BookCopy> copies = bookCopyRepository.findByBookId(id);
            int removed = 0;
            for (int j = copies.size() - 1; j >= 0; j--) {
                BookCopy copy = copies.get(j);
                if (copy.getStatus() == BookCopy.CopyStatus.AVAILABLE && removed < toRemove) {
                    bookCopyRepository.delete(copy);
                    removed++;
                }
            }
            book.setAvailableQuantity(book.getAvailableQuantity() - removed);
            book.setQuantity(book.getQuantity() - removed);
        }

        if (file != null && !file.isEmpty()) {
            String imagePath = saveImage(file);
            book.setCoverImage(imagePath);
        } else if (dto.getCoverImage() != null) {
            book.setCoverImage(dto.getCoverImage());
            book.setCoverImageUrl(dto.getCoverImage());
        } else if (dto.getCoverImageUrl() != null) {
            book.setCoverImage(dto.getCoverImageUrl());
            book.setCoverImageUrl(dto.getCoverImageUrl());
        }

        Book savedBook = bookRepository.save(book);
        auditLog.logActivity(null, "BOOK_UPDATED", "Updated book: " + savedBook.getTitle());
        return savedBook;
    }

    @Transactional
    public void softDeleteBook(Long id) {
        Book book = getBookById(id);
        book.setStatus(Book.BookStatus.DELETED);
        bookRepository.save(book);
        auditLog.logActivity(null, "BOOK_DELETED", "Soft deleted book ID: " + id);
    }

    @Transactional
    public void restoreBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Book not found with ID: " + id, HttpStatus.NOT_FOUND));
        book.setStatus(Book.BookStatus.ACTIVE);
        bookRepository.save(book);
        auditLog.logActivity(null, "BOOK_RESTORED", "Restored book: " + book.getTitle());
    }

    // CSV Bulk Upload
    @Transactional
    public void bulkUploadBooksCsv(MultipartFile file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                String[] data = line.split(",");
                if (data.length < 8) continue;

                // Format: isbn, title, authorName, publisherName, categoryName, quantity, price, language
                String isbn = data[0].trim();
                String title = data[1].trim();
                String authorName = data[2].trim();
                String publisherName = data[3].trim();
                String categoryName = data[4].trim();
                int quantity = Integer.parseInt(data[5].trim());
                BigDecimal price = new BigDecimal(data[6].trim());
                String language = data[7].trim();

                if (bookRepository.existsByIsbn(isbn)) {
                    continue; // Skip duplicates
                }

                // Lookups or creations
                Author author = authorRepository.searchAuthors(authorName, Pageable.unpaged())
                        .getContent().stream().findFirst()
                        .orElseGet(() -> authorRepository.save(Author.builder().name(authorName).biography("CSV Import").build()));

                Publisher publisher = publisherRepository.searchPublishers(publisherName, Pageable.unpaged())
                        .getContent().stream().findFirst()
                        .orElseGet(() -> publisherRepository.save(Publisher.builder().name(publisherName).address("CSV Import").build()));

                Category category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(Category.builder().name(categoryName).description("CSV Import").build()));

                Book book = Book.builder()
                        .isbn(isbn)
                        .title(title)
                        .author(author)
                        .publisher(publisher)
                        .category(category)
                        .quantity(quantity)
                        .availableQuantity(quantity)
                        .price(price)
                        .language(language)
                        .status(Book.BookStatus.ACTIVE)
                        .build();

                Book savedBook = bookRepository.save(book);

                // Add copies
                for (int i = 1; i <= quantity; i++) {
                    String barcode = "BAR-" + savedBook.getIsbn() + "-" + String.format("%03d", i);
                    BookCopy copy = BookCopy.builder()
                            .book(savedBook)
                            .barcode(barcode)
                            .copyNumber(i)
                            .status(BookCopy.CopyStatus.AVAILABLE)
                            .build();
                    bookCopyRepository.save(copy);
                }
            }
            auditLog.logActivity(null, "BULK_UPLOAD_BOOKS", "Imported books from CSV file: " + file.getOriginalFilename());
        } catch (Exception e) {
            throw new LibraryException("Failed to import CSV: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void exportBooksToCsv(Writer writer) throws IOException {
        writer.write("ISBN,Title,Author,Publisher,Category,Quantity,Available,Price,Language\n");
        List<Book> books = bookRepository.findAll();
        for (Book b : books) {
            if (b.getStatus() == Book.BookStatus.DELETED) continue;
            writer.write(String.format("%s,\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,%s,%s\n",
                    b.getIsbn(),
                    b.getTitle().replace("\"", "\"\""),
                    b.getAuthor().getName().replace("\"", "\"\""),
                    b.getPublisher().getName().replace("\"", "\"\""),
                    b.getCategory().getName().replace("\"", "\"\""),
                    b.getQuantity(),
                    b.getAvailableQuantity(),
                    b.getPrice() != null ? b.getPrice().toString() : "0.00",
                    b.getLanguage() != null ? b.getLanguage() : "English"
            ));
        }
    }

    public byte[] exportBooksToExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Library Books");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ISBN", "Title", "Author", "Publisher", "Category", "Quantity", "Available Quantity", "Price", "Language"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            List<Book> books = bookRepository.findAll();
            for (Book b : books) {
                if (b.getStatus() == Book.BookStatus.DELETED) continue;
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(b.getIsbn());
                row.createCell(1).setCellValue(b.getTitle());
                row.createCell(2).setCellValue(b.getAuthor().getName());
                row.createCell(3).setCellValue(b.getPublisher().getName());
                row.createCell(4).setCellValue(b.getCategory().getName());
                row.createCell(5).setCellValue(b.getQuantity());
                row.createCell(6).setCellValue(b.getAvailableQuantity());
                row.createCell(7).setCellValue(b.getPrice() != null ? b.getPrice().doubleValue() : 0.0);
                row.createCell(8).setCellValue(b.getLanguage());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new LibraryException("Failed to export Excel report: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String saveImage(MultipartFile file) {
        try {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path target = this.rootUploads.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/api/public/uploads/" + filename;
        } catch (IOException e) {
            throw new LibraryException("Could not store book cover: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Book importOpenLibraryBook(String workId) {
        OpenLibraryWorkDetailDto info = openLibraryService.getWorkDetail(workId);
        
        String isbn = info.getIsbn();
        if (isbn == null || isbn.trim().isEmpty()) {
            isbn = "OL-" + workId.replace("OL", "").replaceAll("[^0-9]", "");
            if (isbn.length() > 20) {
                isbn = isbn.substring(0, 20);
            }
        }
        
        Optional<Book> existing = bookRepository.findByIsbn(isbn);
        if (existing.isPresent()) {
            return existing.get();
        }
        
        String authorName = "Unknown Author";
        if (info.getAuthors() != null && !info.getAuthors().isEmpty()) {
            authorName = info.getAuthors().get(0);
        }
        String finalAuthorName = authorName;
        Author author = authorRepository.findByName(authorName)
                .orElseGet(() -> authorRepository.save(Author.builder().name(finalAuthorName).biography("Imported from Open Library").build()));
        
        String categoryName = "General";
        if (info.getSubjects() != null && !info.getSubjects().isEmpty()) {
            categoryName = info.getSubjects().get(0);
            if (categoryName.length() > 50) {
                categoryName = categoryName.substring(0, 50);
            }
        }
        String finalCatName = categoryName;
        Category category = categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(Category.builder().name(finalCatName).description("Imported from Open Library").build()));
        
        String publisherName = "Unknown Publisher";
        Publisher publisher = publisherRepository.findByName(publisherName)
                .orElseGet(() -> publisherRepository.save(Publisher.builder().name(publisherName).address("Imported from Open Library").build()));
        
        Integer pubYear = null;
        if (info.getFirstPublishDate() != null && !info.getFirstPublishDate().isEmpty()) {
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\b\\d{4}\\b").matcher(info.getFirstPublishDate());
            if (matcher.find()) {
                pubYear = Integer.parseInt(matcher.group());
            }
        }
        
        Book book = Book.builder()
                .isbn(isbn)
                .title(info.getTitle())
                .author(author)
                .publisher(publisher)
                .category(category)
                .language("English")
                .edition("1st")
                .publicationYear(pubYear)
                .price(BigDecimal.valueOf(500.00))
                .shelfNumber("Shelf Temp")
                .rackNumber("Rack Temp")
                .quantity(5)
                .availableQuantity(5)
                .description(info.getDescription())
                .coverImage("") 
                .coverImageUrl(info.getCoverUrl() != null ? info.getCoverUrl() : "")
                .status(Book.BookStatus.ACTIVE)
                .build();
        
        Book savedBook = bookRepository.save(book);
        
        for (int i = 1; i <= 5; i++) {
            String barcode = "BAR-" + savedBook.getIsbn() + "-" + String.format("%03d", i);
            if (bookCopyRepository.findByBarcode(barcode).isEmpty()) {
                BookCopy copy = BookCopy.builder()
                        .book(savedBook)
                        .barcode(barcode)
                        .copyNumber(i)
                        .status(BookCopy.CopyStatus.AVAILABLE)
                        .build();
                bookCopyRepository.save(copy);
            }
        }
        
        auditLog.logActivity(null, "BOOK_IMPORTED", "Imported Open Library book: " + savedBook.getTitle());
        return savedBook;
    }
}
