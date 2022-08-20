DROP DATABASE IF EXISTS Room8s;
CREATE DATABASE Room8s;

USE Room8s;

CREATE TABLE apartments(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_name NVARCHAR(100) NOT NULL,
number_of_people INT DEFAULT 1
);


CREATE TABLE user_level(
ID INT AUTO_INCREMENT PRIMARY KEY,
level_name NVARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE users
(
ID INT AUTO_INCREMENT PRIMARY KEY,
user_name NVARCHAR(100) NOT NULL UNIQUE,
email NVARCHAR(100) NOT NULL UNIQUE,
user_level INT DEFAULT 1, -- default to basic user
user_password NVARCHAR(256) NOT NULL,
profile_icon_id INT,

FOREIGN KEY (user_level) REFERENCES user_level(ID) ON DELETE SET NULL
);


CREATE TABLE user_in_apartment(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_ID INT,
user_ID INT,

FOREIGN KEY (apartment_ID) REFERENCES apartments(ID) ON DELETE CASCADE,
FOREIGN KEY (user_ID) REFERENCES users (ID) ON DELETE CASCADE,

CONSTRAINT user_apartment UNIQUE (apartment_ID, user_ID)
);


CREATE TABLE task_type(
ID INT AUTO_INCREMENT PRIMARY KEY,
task_type NVARCHAR(100) NOT NULL UNIQUE,
icon_path NVARCHAR(254) UNIQUE
);



CREATE TABLE tasks(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_ID INT NOT NULL,
creator_ID INT NOT NULL,
task_type INT DEFAULT 1, -- default to general task
create_time DATETIME,
expiration_date DATETIME,
title NVARCHAR(100),
note NVARCHAR(100),

FOREIGN KEY (apartment_ID) REFERENCES apartments(ID) ON DELETE CASCADE,
FOREIGN KEY (creator_ID) REFERENCES users (ID) ,
FOREIGN KEY (task_type) REFERENCES task_type(ID)
);


CREATE TABLE tasks_per_user(
ID INT AUTO_INCREMENT PRIMARY KEY,
task_ID INT NOT NULL,
user_ID INT NOT NULL,
FOREIGN KEY (task_ID) REFERENCES tasks(ID) ON DELETE CASCADE,
FOREIGN KEY (user_ID) REFERENCES users (ID)
);



CREATE TABLE expense_type(
ID INT AUTO_INCREMENT PRIMARY KEY,
expense_type NVARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE expenses
(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_ID INT NOT NULL,
UserThatUploadID INT NOT NULL,
title NVARCHAR(100) NOT NULL,
expense_type INT DEFAULT 1, -- default to general expense
payment_date DATE,
amount DOUBLE,
upload_date DATETIME, -- when it's uploaded
note NVARCHAR(100),

FOREIGN KEY (apartment_ID) REFERENCES apartments(ID) ON DELETE CASCADE,
FOREIGN KEY (UserThatUploadID) REFERENCES users (ID),
FOREIGN KEY (expense_type) REFERENCES expense_type(ID)
);


CREATE TABLE payments(
ID INT AUTO_INCREMENT PRIMARY KEY,
expense_ID INT,
user_ID INT,
amount DOUBLE,
pay_date DATE,

FOREIGN KEY(user_id)REFERENCES users(ID),
FOREIGN KEY(expense_ID) REFERENCES expenses (ID)
);

CREATE TABLE messaging(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_ID INT NOT NULL,
sender_ID INT NOT NULL,
msg_data TEXT NOT NULL,
send_time DATETIME,

FOREIGN KEY (apartment_ID) REFERENCES apartments(ID) ON DELETE CASCADE ,
FOREIGN KEY (sender_ID) REFERENCES users (ID)
);

INSERT INTO user_level VALUES
(1, 'basic user'),
(2, 'apartment owner'),
(3, 'admin');

INSERT INTO task_type (ID, task_type) VALUES
(1, 'general task');

INSERT INTO expense_type VALUES
(1, 'general expense');




