DROP DATABASE IF EXISTS Room8;
CREATE DATABASE Room8;

USE Room8;

CREATE TABLE apartments(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_name NVARCHAR(100),
number_of_people TINYINT
);


CREATE TABLE user_level(
ID INT AUTO_INCREMENT PRIMARY KEY,
level_name NVARCHAR(100)
);


CREATE TABLE Users(
ID INT AUTO_INCREMENT PRIMARY KEY,
user_name NVARCHAR(100),
email NVARCHAR(100),
user_level INT,
user_password NVARCHAR(256),
monthly_payment DOUBLE,
profile_icon_path NVARCHAR(254),

FOREIGN KEY (user_level) REFERENCES user_level(ID)
);


CREATE TABLE user_in_apartment(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_ID INT,
user_ID INT,

FOREIGN KEY (apartment_ID) REFERENCES apartments(ID),
FOREIGN KEY (user_ID) REFERENCES Users(ID)
);


CREATE TABLE task_type(
ID INT AUTO_INCREMENT PRIMARY KEY,
task_type NVARCHAR(100),
icon_path NVARCHAR(254)
);



CREATE TABLE tasks(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_ID INT,
creator_ID INT,
task_type INT,
create_time DATETIME,
expiration_date DATETIME,
title NVARCHAR(100),
note NVARCHAR(100),

FOREIGN KEY (apartment_ID) REFERENCES apartments(ID),
FOREIGN KEY (creator_ID) REFERENCES Users(ID),
FOREIGN KEY (task_type) REFERENCES task_type(ID)
);


CREATE TABLE tasks_per_user(
ID INT AUTO_INCREMENT PRIMARY KEY,
task_ID INT,
user_ID INT,
FOREIGN KEY (task_ID) REFERENCES tasks(ID),
FOREIGN KEY (user_ID) REFERENCES Users(ID)
);



CREATE TABLE expense_type(
ID INT AUTO_INCREMENT PRIMARY KEY,
expense_type NVARCHAR(100)
);

CREATE TABLE Expenses(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_ID INT,
title NVARCHAR(100),
expense_type INT,
UserThatUploadID INT,
payment_date DATE,
amount DOUBLE,
upload_date DATETIME, -- when it's uploaded
note NVARCHAR(100),

FOREIGN KEY (apartment_ID) REFERENCES apartments(ID),
FOREIGN KEY (UserThatUploadID) REFERENCES Users(ID),
FOREIGN KEY (expense_type) REFERENCES expense_type(ID)
);


CREATE TABLE payments(
ID INT AUTO_INCREMENT PRIMARY KEY,
expense_ID INT,
user_id INT,
amount_that_paid DOUBLE,
pay_timestamp DATE,

FOREIGN KEY(user_id)REFERENCES Users(ID),
FOREIGN KEY(expense_ID) REFERENCES Expenses(ID)
);

CREATE TABLE messaging(
ID INT AUTO_INCREMENT PRIMARY KEY,
apartment_ID INT,
sender_ID INT,
msg_data TEXT,
send_time DATETIME,

FOREIGN KEY (apartment_ID) REFERENCES apartments(ID),
FOREIGN KEY (sender_ID) REFERENCES Users (ID)
);


