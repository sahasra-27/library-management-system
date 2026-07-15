package com.smartlibrary.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DatabasePatcher implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running Database Patcher to modify check constraints...");
        try {
            java.util.List<java.util.Map<String, Object>> columns = jdbcTemplate.queryForList("SHOW COLUMNS FROM users LIKE 'role'");
            System.out.println("COLUMN DETAILS FOR role: " + columns);
        } catch (Exception e) {
            System.out.println("Could not show columns: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE users MODIFY COLUMN role VARCHAR(20) NOT NULL");
            System.out.println("Altered users.role column to VARCHAR(20).");
        } catch (Exception e) {
            System.out.println("Could not alter users.role column to VARCHAR(20): " + e.getMessage());
        }
        try {
            // Drop check constraints if they exist in MySQL
            jdbcTemplate.execute("ALTER TABLE reservations DROP CHECK chk_reserve_status");
            System.out.println("Dropped chk_reserve_status check constraint.");
        } catch (Exception e) {
            System.out.println("Could not drop chk_reserve_status constraint (it may already be dropped): " + e.getMessage());
        }
        
        try {
            jdbcTemplate.execute("ALTER TABLE fines DROP CHECK chk_fine_status");
            System.out.println("Dropped chk_fine_status check constraint.");
        } catch (Exception e) {
            System.out.println("Could not drop chk_fine_status constraint (it may already be dropped): " + e.getMessage());
        }

        try {
            // Modify issue_id column to be nullable and drop its NOT NULL constraint
            jdbcTemplate.execute("ALTER TABLE fines MODIFY issue_id BIGINT NULL");
            System.out.println("Modified issue_id in fines table to be nullable.");
        } catch (Exception e) {
            System.out.println("Could not modify issue_id in fines table: " + e.getMessage());
        }

        try {
            // Alter columns to VARCHAR(50) to support new enum values in MySQL
            jdbcTemplate.execute("ALTER TABLE reservations MODIFY COLUMN status VARCHAR(50) NOT NULL DEFAULT 'PENDING'");
            System.out.println("Modified reservations.status to VARCHAR(50).");
        } catch (Exception e) {
            System.out.println("Could not modify reservations.status: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE fines MODIFY COLUMN status VARCHAR(50) NOT NULL DEFAULT 'UNPAID'");
            System.out.println("Modified fines.status to VARCHAR(50).");
        } catch (Exception e) {
            System.out.println("Could not modify fines.status: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE books ADD COLUMN subtitle VARCHAR(255) NULL");
            System.out.println("Added subtitle column to books table.");
        } catch (Exception e) {
            System.out.println("Could not add subtitle column (it may already exist): " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE books ADD COLUMN number_of_pages INT NULL");
            System.out.println("Added number_of_pages column to books table.");
        } catch (Exception e) {
            System.out.println("Could not add number_of_pages column (it may already exist): " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE books ADD COLUMN cover_image_url VARCHAR(255) NULL");
            System.out.println("Added cover_image_url column to books table.");
        } catch (Exception e) {
            System.out.println("Could not add cover_image_url column (it may already exist): " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE books ADD COLUMN page_count INT NULL");
            System.out.println("Added page_count column to books table.");
        } catch (Exception e) {
            System.out.println("Could not add page_count column (it may already exist): " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE users DROP CHECK chk_user_role");
            System.out.println("Dropped chk_user_role check constraint.");
        } catch (Exception e) {
            System.out.println("Could not drop chk_user_role via DROP CHECK: " + e.getMessage());
            try {
                jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT chk_user_role");
                System.out.println("Dropped chk_user_role constraint via DROP CONSTRAINT.");
            } catch (Exception ex) {
                System.out.println("Could not drop chk_user_role via DROP CONSTRAINT: " + ex.getMessage());
            }
        }

        try {
            jdbcTemplate.execute("UPDATE users SET role = 'USER' WHERE role = 'STUDENT'");
            System.out.println("Updated existing STUDENT roles to USER in users table.");
        } catch (Exception e) {
            System.out.println("Could not update existing STUDENT roles to USER: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("UPDATE users SET occupation = 'Student' WHERE (role = 'USER' OR role = 'STUDENT') AND occupation IS NULL");
            System.out.println("Set default occupation Student for existing users in users table.");
        } catch (Exception e) {
            System.out.println("Could not update default occupation: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE users ADD CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'USER'))");
            System.out.println("Added new chk_user_role constraint.");
        } catch (Exception e) {
            System.out.println("Could not add chk_user_role constraint: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN role SET DEFAULT 'USER'");
            System.out.println("Set users.role default to USER.");
        } catch (Exception e) {
            System.out.println("Could not alter role column default: " + e.getMessage());
        }

        System.out.println("Database Patcher execution finished.");
    }
}
