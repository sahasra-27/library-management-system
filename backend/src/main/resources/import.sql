-- Initialize Users (Password is plain-text 'password')
INSERT IGNORE INTO users (id, username, email, password, phone, role, occupation, status, created_at, updated_at) VALUES 
(1, 'admin', 'admin@smartlibrary.com', 'password', '+91-9999999999', 'ADMIN', 'Librarian', 'ACTIVE', NOW(), NOW()),
(2, 'student', 'student@smartlibrary.com', 'password', '+91-8888888888', 'USER', 'Student', 'ACTIVE', NOW(), NOW());

-- Initialize Categories
INSERT IGNORE INTO categories (id, name, description, created_at, updated_at) VALUES 
(1, 'Computer Science', 'Technical books on software development and systems', NOW(), NOW()),
(2, 'Fiction', 'Creative storytelling and novels', NOW(), NOW()),
(3, 'Mathematics', 'Pure and applied mathematics textbooks', NOW(), NOW()),
(4, 'Engineering', 'General engineering principles and tech design manuals', NOW(), NOW()),
(5, 'Science', 'General science, physics, chemistry, and biology journals', NOW(), NOW());

-- Initialize Authors
INSERT IGNORE INTO authors (id, name, biography, created_at, updated_at) VALUES 
(1, 'Martin Kleppmann', 'Researcher in distributed systems at University of Cambridge', NOW(), NOW()),
(2, 'J.K. Rowling', 'British author, best known for Harry Potter series', NOW(), NOW()),
(3, 'Joshua Bloch', 'Software engineer and author, best known for Effective Java', NOW(), NOW()),
(4, 'George Orwell', 'Dystopian novelist and essayist, famous for 1984', NOW(), NOW()),
(5, 'Robert C. Martin', 'Uncle Bob, agile advocate and Clean Code author', NOW(), NOW());

-- Initialize Publishers
INSERT IGNORE INTO publishers (id, name, address, phone, created_at, updated_at) VALUES 
(1, 'O Reilly Media', '1005 Gravenstein Hwy N, Sebastopol, CA', '+1-707-827-7000', NOW(), NOW()),
(2, 'Bloomsbury Publishing', '50 Bedford Square, London', '+44-20-7631-5600', NOW(), NOW()),
(3, 'Addison-Wesley', 'Boston, Massachusetts', '+1-617-848-6000', NOW(), NOW()),
(4, 'Secker & Warburg', 'London, United Kingdom', '+44-20-7840-8400', NOW(), NOW()),
(5, 'Prentice Hall', 'Upper Saddle River, New Jersey', '+1-201-236-7000', NOW(), NOW());

-- Initialize Settings
INSERT IGNORE INTO settings (id, settings_key, settings_value, description) VALUES 
(1, 'library_name', 'BookVerse', 'Name of the university library'),
(2, 'library_address', '123 Tech Campus, Silicon Valley', 'Physical address of library'),
(3, 'email', 'support@smartlibrary.com', 'Contact support email'),
(4, 'phone', '+1-555-0199', 'Contact support phone line'),
(5, 'fine_amount', '5', 'Fine amount in INR charged daily after due date'),
(6, 'borrow_duration', '14', 'Borrow duration limit in days'),
(7, 'max_books', '5', 'Max borrow books limit');

-- Initialize Books
INSERT IGNORE INTO books (id, isbn, title, author_id, publisher_id, category_id, language, edition, publication_year, price, shelf_number, rack_number, quantity, available_quantity, description, status, created_at, updated_at) VALUES
(1, '9781491903070', 'Designing Data-Intensive Applications', 1, 1, 1, 'English', '1st', 2017, 3200.00, 'Shelf A', 'Rack 12', 3, 2, 'Detailed analysis of storage, scalability, and consensus in distributed data systems.', 'ACTIVE', NOW(), NOW()),
(2, '9780747532699', 'Harry Potter and the Philosophers Stone', 2, 2, 2, 'English', 'Special', 1997, 450.00, 'Shelf B', 'Rack 04', 5, 5, 'The novel that introduced the wizarding world to readers worldwide.', 'ACTIVE', NOW(), NOW()),
(3, '9780134685991', 'Effective Java', 3, 3, 1, 'English', '3rd', 2018, 1500.00, 'Shelf A', 'Rack 10', 4, 4, 'Best practice guidelines for writing clear, correct, and robust Java code.', 'ACTIVE', NOW(), NOW()),
(4, '9780132350884', 'Clean Code', 5, 5, 1, 'English', '1st', 2008, 1800.00, 'Shelf A', 'Rack 08', 4, 4, 'A handbook of agile software craftsmanship including refactoring tools.', 'ACTIVE', NOW(), NOW()),
(5, '9780451524935', '1984', 4, 4, 2, 'English', 'Reprint', 1949, 350.00, 'Shelf C', 'Rack 02', 3, 3, 'A classic dystopian social science fiction novel that details total surveillance.', 'ACTIVE', NOW(), NOW()),
(6, '9780545010221', 'Harry Potter and the Deathly Hallows', 2, 2, 2, 'English', '1st', 2007, 550.00, 'Shelf B', 'Rack 05', 5, 5, 'The seventh and final novel in the Harry Potter franchise series.', 'ACTIVE', NOW(), NOW());

-- Initialize Book Copies
INSERT IGNORE INTO book_copies (id, book_id, barcode, copy_number, status) VALUES 
(1, 1, 'BAR-9781491903070-001', 1, 'BORROWED'),
(2, 1, 'BAR-9781491903070-002', 2, 'AVAILABLE'),
(3, 1, 'BAR-9781491903070-003', 3, 'AVAILABLE'),
(4, 2, 'BAR-9780747532699-001', 1, 'AVAILABLE'),
(5, 2, 'BAR-9780747532699-002', 2, 'AVAILABLE'),
(6, 2, 'BAR-9780747532699-003', 3, 'AVAILABLE'),
(7, 2, 'BAR-9780747532699-004', 4, 'AVAILABLE'),
(8, 2, 'BAR-9780747532699-005', 5, 'AVAILABLE'),
(9, 3, 'BAR-9780134685991-001', 1, 'AVAILABLE'),
(10, 3, 'BAR-9780134685991-002', 2, 'AVAILABLE'),
(11, 3, 'BAR-9780134685991-003', 3, 'AVAILABLE'),
(12, 3, 'BAR-9780134685991-004', 4, 'AVAILABLE'),
(13, 4, 'BAR-9780132350884-001', 1, 'AVAILABLE'),
(14, 4, 'BAR-9780132350884-002', 2, 'AVAILABLE'),
(15, 4, 'BAR-9780132350884-003', 3, 'AVAILABLE'),
(16, 4, 'BAR-9780132350884-004', 4, 'AVAILABLE'),
(17, 5, 'BAR-9780451524935-001', 1, 'AVAILABLE'),
(18, 5, 'BAR-9780451524935-002', 2, 'AVAILABLE'),
(19, 5, 'BAR-9780451524935-003', 3, 'AVAILABLE'),
(20, 6, 'BAR-9780545010221-001', 1, 'AVAILABLE'),
(21, 6, 'BAR-9780545010221-002', 2, 'AVAILABLE'),
(22, 6, 'BAR-9780545010221-003', 3, 'AVAILABLE'),
(23, 6, 'BAR-9780545010221-004', 4, 'AVAILABLE'),
(24, 6, 'BAR-9780545010221-005', 5, 'AVAILABLE');

-- Initialize Issues (Borrow)
INSERT IGNORE INTO issued_books (id, user_id, book_copy_id, issue_date, due_date, status, created_at) VALUES
(1, 2, 1, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 'ISSUED', NOW());
