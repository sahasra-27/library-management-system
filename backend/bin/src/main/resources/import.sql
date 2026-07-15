-- Initialize Users (Password is plain-text 'password')
INSERT IGNORE INTO users (id, username, email, password, phone, role, status, created_at, updated_at) VALUES 
(1, 'admin', 'admin@smartlibrary.com', 'password', '+91-9999999999', 'ADMIN', 'ACTIVE', NOW(), NOW()),
(2, 'student', 'student@smartlibrary.com', 'password', '+91-8888888888', 'STUDENT', 'ACTIVE', NOW(), NOW());

-- Initialize Categories
INSERT IGNORE INTO categories (id, name, description, created_at, updated_at) VALUES 
(1, 'Computer Science', 'Technical books on software development and systems', NOW(), NOW()),
(2, 'Fiction', 'Creative storytelling and novels', NOW(), NOW()),
(3, 'Mathematics', 'Pure and applied mathematics textbooks', NOW(), NOW());

-- Initialize Authors
INSERT IGNORE INTO authors (id, name, biography, created_at, updated_at) VALUES 
(1, 'Martin Kleppmann', 'Researcher in distributed systems at University of Cambridge', NOW(), NOW()),
(2, 'J.K. Rowling', 'British author, best known for Harry Potter series', NOW(), NOW());

-- Initialize Publishers
INSERT IGNORE INTO publishers (id, name, address, phone, created_at, updated_at) VALUES 
(1, 'O Reilly Media', '1005 Gravenstein Hwy N, Sebastopol, CA', '+1-707-827-7000', NOW(), NOW()),
(2, 'Bloomsbury', '50 Bedford Square, London', '+44-20-7631-5600', NOW(), NOW());

-- Initialize Settings
INSERT IGNORE INTO settings (id, settings_key, settings_value, description) VALUES 
(1, 'library_name', 'Smart Library', 'Name of the university library'),
(2, 'library_address', '123 Tech Campus, Silicon Valley', 'Physical address of library'),
(3, 'email', 'support@smartlibrary.com', 'Contact support email'),
(4, 'phone', '+1-555-0199', 'Contact support phone line'),
(5, 'fine_amount', '5', 'Fine amount in INR charged daily after due date'),
(6, 'borrow_duration', '14', 'Borrow duration limit in days'),
(7, 'max_books', '5', 'Max borrow books limit');
