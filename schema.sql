-- ============================================================
-- HELPDESK COMPLAINT / TICKETING SYSTEM - DATABASE SCHEMA
-- Database Engine: MySQL 5.7+ / 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS helpdesk_db;
USE helpdesk_db;

-- ------------------------------------------------------------
-- Table: users
-- Stores both Employees and Admin users
-- Roles: 'EMPLOYEE', 'ADMIN'
-- ------------------------------------------------------------
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS keywords;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'EMPLOYEE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Table: tickets
-- Stores raised complaints/tickets with assigned priority and status
-- Statuses: 'OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'
-- Priorities: 'HIGH', 'MEDIUM', 'LOW'
-- ------------------------------------------------------------
CREATE TABLE tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'General',
    priority VARCHAR(20) NOT NULL DEFAULT 'LOW',
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    admin_remark TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- Table: keywords
-- Stores keyword definitions, integer weights, and default priority levels
-- Used by PriorityService to auto-assign ticket priority based on complaint text
-- ------------------------------------------------------------
CREATE TABLE keywords (
    keyword_id INT AUTO_INCREMENT PRIMARY KEY,
    keyword VARCHAR(100) NOT NULL UNIQUE,
    weight INT NOT NULL DEFAULT 1,
    priority_level VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SEED DATA INSERTION
-- ============================================================

-- Seed Users (Passwords stored as plain text for demo)
INSERT INTO users (name, email, password, role) VALUES 
('System Administrator', 'admin@helpdesk.com', 'admin123', 'ADMIN'),
('John Doe', 'john@company.com', 'user123', 'EMPLOYEE'),
('Jane Smith', 'jane@company.com', 'user123', 'EMPLOYEE');

-- Seed Keywords (Categorized by Weight & Priority)
-- HIGH Weight Keywords (Weight = 5)
INSERT INTO keywords (keyword, weight, priority_level) VALUES
('urgent', 5, 'HIGH'),
('server down', 5, 'HIGH'),
('security breach', 5, 'HIGH'),
('critical', 5, 'HIGH'),
('crash', 5, 'HIGH'),
('database failure', 5, 'HIGH'),
('system down', 5, 'HIGH'),
('outage', 5, 'HIGH');

-- MEDIUM Weight Keywords (Weight = 3)
INSERT INTO keywords (keyword, weight, priority_level) VALUES
('slow', 3, 'MEDIUM'),
('error', 3, 'MEDIUM'),
('not working', 3, 'MEDIUM'),
('bug', 3, 'MEDIUM'),
('freeze', 3, 'MEDIUM'),
('printer issue', 3, 'MEDIUM'),
('network lag', 3, 'MEDIUM'),
('display error', 3, 'MEDIUM');

-- LOW Weight Keywords (Weight = 1)
INSERT INTO keywords (keyword, weight, priority_level) VALUES
('request', 1, 'LOW'),
('installation', 1, 'LOW'),
('query', 1, 'LOW'),
('help', 1, 'LOW'),
('access', 1, 'LOW'),
('password reset', 1, 'LOW'),
('update', 1, 'LOW'),
('software request', 1, 'LOW');

-- Sample Initial Tickets
INSERT INTO tickets (user_id, title, description, category, priority, status, admin_remark) VALUES
(2, 'Database server down in region east', 'The main database failure occurred at 9 AM. Urgent investigation required as server down.', 'IT Infrastructure', 'HIGH', 'OPEN', NULL),
(2, 'Email client running slow', 'My Outlook client is quite slow and showing a minor display error on launch.', 'Software', 'MEDIUM', 'IN_PROGRESS', 'IT team is looking into the log files.'),
(3, 'Request for new keyboard', 'Submitting a query and request for replacement keyboard installation.', 'Hardware', 'LOW', 'RESOLVED', 'New keyboard dispatched to employee desk.');
