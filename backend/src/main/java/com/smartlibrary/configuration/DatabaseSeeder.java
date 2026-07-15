package com.smartlibrary.configuration;

import com.smartlibrary.entity.*;
import com.smartlibrary.repository.*;
import com.smartlibrary.service.OpenLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private OpenLibraryService openLibraryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Categories
        Category compSci = getOrCreateCategory("Computer Science", "Technical books on software development, networks, and algorithms");
        Category fiction = getOrCreateCategory("Fiction", "Creative storytelling, drama, and novels");
        Category math = getOrCreateCategory("Mathematics", "Pure and applied mathematics textbooks");
        Category engineering = getOrCreateCategory("Engineering", "General engineering principles and tech design manuals");
        Category science = getOrCreateCategory("Science", "General science, physics, chemistry, and biology journals");

        // 2. Seed Authors
        Author kleppmann = getOrCreateAuthor("Martin Kleppmann", "Distributed systems researcher and lecturer at University of Cambridge");
        Author rowling = getOrCreateAuthor("J.K. Rowling", "Renowned British fantasy novel author, best known for Harry Potter");
        Author bloch = getOrCreateAuthor("Joshua Bloch", "Software engineer and author, best known for Effective Java and design guidelines");
        Author orwell = getOrCreateAuthor("George Orwell", "Dystopian novelist and essayist, famous for 1984 and Animal Farm");
        Author martin = getOrCreateAuthor("Robert C. Martin", "Uncle Bob, veteran software engineer, speaker, and Clean Code author");

        // 3. Seed Publishers
        Publisher oreilly = getOrCreatePublisher("O Reilly Media", "1005 Gravenstein Hwy N, Sebastopol, CA", "+1-707-827-7000");
        Publisher bloomsbury = getOrCreatePublisher("Bloomsbury Publishing", "50 Bedford Square, London", "+44-20-7631-5600");
        Publisher addison = getOrCreatePublisher("Addison-Wesley", "Boston, Massachusetts", "+1-617-848-6000");
        Publisher secker = getOrCreatePublisher("Secker & Warburg", "London, United Kingdom", "+44-20-7840-8400");
        Publisher prentice = getOrCreatePublisher("Prentice Hall", "Upper Saddle River, New Jersey", "+1-201-236-7000");

        // 4. Seed Books
        seedBook("9781491903070", "Designing Data-Intensive Applications", kleppmann, oreilly, compSci, "1st", 2017, 3200.00, "Shelf A", "Rack 12", 3, "Detailed analysis of storage, scalability, and consensus in distributed data systems.");
        seedBook("9780747532699", "Harry Potter and the Philosophers Stone", rowling, bloomsbury, fiction, "Special", 1997, 450.00, "Shelf B", "Rack 04", 5, "The novel that introduced the wizarding world to readers worldwide.");
        seedBook("9780134685991", "Effective Java", bloch, addison, compSci, "3rd", 2018, 1500.00, "Shelf A", "Rack 10", 4, "Best practice guidelines for writing clear, correct, and robust Java code.");
        seedBook("9780132350884", "Clean Code", martin, prentice, compSci, "1st", 2008, 1800.00, "Shelf A", "Rack 08", 4, "A handbook of agile software craftsmanship including refactoring tools.");
        seedBook("9780451524935", "1984", orwell, secker, fiction, "Reprint", 1949, 350.00, "Shelf C", "Rack 02", 3, "A classic dystopian social science fiction novel that details total surveillance.");
        seedBook("9780545010221", "Harry Potter and the Deathly Hallows", rowling, bloomsbury, fiction, "1st", 2007, 550.00, "Shelf B", "Rack 05", 5, "The seventh and final novel in the Harry Potter franchise series.");

        // Fetch and seed extra books from Open Library API without cover images
        String[] extraIsbns = {
            "9780131103627", // The C Programming Language
            "9780201633610", // Design Patterns
            "9780134494166", // Clean Architecture
            "9780321125217"  // Domain-Driven Design
        };

        for (String isbn : extraIsbns) {
            try {
                if (bookRepository.existsByIsbn(isbn)) {
                    continue;
                }
                
                System.out.println("Seeding book from Open Library API for ISBN: " + isbn);
                java.util.Map<String, Object> info = openLibraryService.fetchBookByIsbn(isbn);
                
                Long authorId = null;
                if (info.get("authorId") instanceof Number) {
                    authorId = ((Number) info.get("authorId")).longValue();
                }
                
                Long publisherId = null;
                if (info.get("publisherId") instanceof Number) {
                    publisherId = ((Number) info.get("publisherId")).longValue();
                }
                
                Long categoryId = null;
                if (info.get("categoryId") instanceof Number) {
                    categoryId = ((Number) info.get("categoryId")).longValue();
                }
                
                Author author = authorId != null ? authorRepository.findById(authorId).orElse(null) : null;
                Publisher publisher = publisherId != null ? publisherRepository.findById(publisherId).orElse(null) : null;
                Category category = categoryId != null ? categoryRepository.findById(categoryId).orElse(null) : null;
                
                if (author == null || publisher == null || category == null) {
                    continue;
                }
                
                Integer publicationYear = null;
                if (info.get("publicationYear") instanceof Number) {
                    publicationYear = ((Number) info.get("publicationYear")).intValue();
                }
                
                Integer numberOfPages = null;
                if (info.get("numberOfPages") instanceof Number) {
                    numberOfPages = ((Number) info.get("numberOfPages")).intValue();
                }
                
                Integer pageCount = null;
                if (info.get("pageCount") instanceof Number) {
                    pageCount = ((Number) info.get("pageCount")).intValue();
                }
                
                Book book = Book.builder()
                        .isbn(isbn)
                        .title((String) info.get("title"))
                        .subtitle((String) info.get("subtitle"))
                        .author(author)
                        .publisher(publisher)
                        .category(category)
                        .language((String) info.get("language"))
                        .edition("1st")
                        .publicationYear(publicationYear)
                        .price(BigDecimal.valueOf(500.00))
                        .shelfNumber("Shelf Temp")
                        .rackNumber("Rack Temp")
                        .quantity(5)
                        .availableQuantity(5)
                        .description((String) info.get("description"))
                        .coverImage("") // Do not add cover image
                        .coverImageUrl("") // Do not add cover image URL
                        .numberOfPages(numberOfPages)
                        .pageCount(pageCount)
                        .status(Book.BookStatus.ACTIVE)
                        .build();
                
                Book savedBook = bookRepository.save(book);
                
                // Seed book copies
                for (int i = 1; i <= 5; i++) {
                    String barcode = "BAR-" + isbn + "-" + String.format("%03d", i);
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
                System.out.println("Successfully seeded book from Open Library: " + savedBook.getTitle());
            } catch (Exception e) {
                System.err.println("Failed to seed book with ISBN " + isbn + ": " + e.getMessage());
            }
        }
    }

    private Category getOrCreateCategory(String name, String description) {
        Optional<Category> opt = categoryRepository.findAll().stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }
        Category cat = Category.builder().name(name).description(description).build();
        return categoryRepository.save(cat);
    }

    private Author getOrCreateAuthor(String name, String bio) {
        Optional<Author> opt = authorRepository.findAll().stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }
        Author aut = Author.builder().name(name).biography(bio).build();
        return authorRepository.save(aut);
    }

    private Publisher getOrCreatePublisher(String name, String address, String phone) {
        Optional<Publisher> opt = publisherRepository.findAll().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
        if (opt.isPresent()) {
            return opt.get();
        }
        Publisher pub = Publisher.builder().name(name).address(address).phone(phone).build();
        return publisherRepository.save(pub);
    }

    private void seedBook(String isbn, String title, Author author, Publisher publisher, Category category,
                          String edition, Integer year, double price, String shelf, String rack, int qty, String desc) {
        Optional<Book> opt = bookRepository.findByIsbn(isbn);
        if (opt.isPresent()) {
            return;
        }

        Book book = Book.builder()
                .isbn(isbn)
                .title(title)
                .author(author)
                .publisher(publisher)
                .category(category)
                .language("English")
                .edition(edition)
                .publicationYear(year)
                .price(BigDecimal.valueOf(price))
                .shelfNumber(shelf)
                .rackNumber(rack)
                .quantity(qty)
                .availableQuantity(qty)
                .description(desc)
                .status(Book.BookStatus.ACTIVE)
                .build();

        Book savedBook = bookRepository.save(book);

        // Seed Book Copies for this book
        for (int i = 1; i <= qty; i++) {
            String barcode = "BAR-" + isbn + "-" + String.format("%03d", i);
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
    }
}
