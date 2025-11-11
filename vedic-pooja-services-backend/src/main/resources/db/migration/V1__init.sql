-- Flyway Migration: Initial schema

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(255) UNIQUE,
  phone VARCHAR(20) UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('USER','PUROHIT','ADMIN') NOT NULL DEFAULT 'USER',
  locale VARCHAR(10) DEFAULT 'en',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE purohits (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  experience_years INT DEFAULT 0,
  specialization VARCHAR(255),
  bio TEXT,
  languages JSON,
  location_city VARCHAR(120),
  location_state VARCHAR(120),
  latitude DECIMAL(10,7),
  longitude DECIMAL(10,7),
  service_radius_km INT DEFAULT 50,
  status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_purohits_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE purohit_documents (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  purohit_id BIGINT NOT NULL,
  doc_type VARCHAR(50),
  s3_key VARCHAR(512) NOT NULL,
  verified ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
  notes VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (purohit_id) REFERENCES purohits(id)
);

CREATE TABLE services (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(150) NOT NULL,
  description TEXT,
  duration_minutes INT NOT NULL,
  base_price_cents INT NOT NULL,
  currency VARCHAR(3) NOT NULL DEFAULT 'INR',
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE purohit_services (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  purohit_id BIGINT NOT NULL,
  service_id BIGINT NOT NULL,
  price_cents INT NOT NULL,
  notes VARCHAR(255),
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_purohit_service (purohit_id, service_id),
  FOREIGN KEY (purohit_id) REFERENCES purohits(id),
  FOREIGN KEY (service_id) REFERENCES services(id)
);

CREATE TABLE availability (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  purohit_id BIGINT NOT NULL,
  date DATE NOT NULL,
  time_slot VARCHAR(20) NOT NULL,
  is_available BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_availability (purohit_id, date, time_slot),
  FOREIGN KEY (purohit_id) REFERENCES purohits(id)
);

CREATE TABLE bookings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  purohit_id BIGINT NOT NULL,
  service_id BIGINT NOT NULL,
  booking_date DATE NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  address_line1 VARCHAR(255),
  address_line2 VARCHAR(255),
  city VARCHAR(120),
  state VARCHAR(120),
  latitude DECIMAL(10,7),
  longitude DECIMAL(10,7),
  status ENUM('HOLD','PENDING_PAYMENT','CONFIRMED','ACCEPTED','REJECTED','CANCELLED','COMPLETED','NO_SHOW') NOT NULL DEFAULT 'HOLD',
  payment_id BIGINT,
  notes TEXT,
  hold_expires_at DATETIME,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (purohit_id) REFERENCES purohits(id),
  FOREIGN KEY (service_id) REFERENCES services(id)
);

CREATE TABLE payments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  booking_id BIGINT NOT NULL,
  amount_cents INT NOT NULL,
  currency VARCHAR(3) NOT NULL DEFAULT 'INR',
  provider ENUM('RAZORPAY','STRIPE') NOT NULL,
  provider_order_id VARCHAR(100) UNIQUE,
  provider_payment_id VARCHAR(100),
  status ENUM('CREATED','AUTHORIZED','CAPTURED','FAILED','REFUNDED') NOT NULL DEFAULT 'CREATED',
  method VARCHAR(50),
  fee_cents INT DEFAULT 0,
  tax_cents INT DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE TABLE reviews (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  booking_id BIGINT NOT NULL UNIQUE,
  rating TINYINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comment TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE TABLE settlements (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  purohit_id BIGINT NOT NULL,
  period_start DATE NOT NULL,
  period_end DATE NOT NULL,
  gross_cents INT NOT NULL,
  platform_fee_cents INT NOT NULL,
  net_cents INT NOT NULL,
  status ENUM('PENDING','PAID') NOT NULL DEFAULT 'PENDING',
  paid_at DATETIME,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (purohit_id) REFERENCES purohits(id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_purohits_status_city_state ON purohits(status, location_city, location_state);
CREATE INDEX idx_availability_purohit_date ON availability(purohit_id, date);
CREATE INDEX idx_bookings_purohit_start_status ON bookings(purohit_id, start_time, status);
CREATE INDEX idx_bookings_user_start ON bookings(user_id, start_time);
CREATE INDEX idx_payments_provider_order ON payments(provider, provider_order_id);