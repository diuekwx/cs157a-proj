CREATE TABLE Restaurant (
RestaurantID INT AUTO_INCREMENT PRIMARY KEY,
Address VARCHAR(100),
Name VARCHAR(100)
);

CREATE TABLE Person(
UserID INT AUTO_INCREMENT PRIMARY KEY,
Email VARCHAR(100),
Name VARCHAR(100)
);

CREATE TABLE PersonRestaurantOwnership (
OwnerID INT,
RestaurantID INT,
PRIMARY KEY (OwnerID, RestaurantID),
FOREIGN KEY (OwnerID) REFERENCES Person(UserID) ON DELETE CASCADE,
FOREIGN KEY (RestaurantID) REFERENCES Restaurant(RestaurantID) ON DELETE CASCADE
);



CREATE TABLE Category(
CategoryID INT AUTO_INCREMENT PRIMARY KEY,
Cuisine VARCHAR(50)
);

CREATE TABLE RestaurantCategory (
RestaurantID INT,
CategoryID INT,
PRIMARY KEY (RestaurantID, CategoryID),
FOREIGN KEY (RestaurantID) REFERENCES Restaurant(RestaurantID) ON DELETE CASCADE,
FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID) ON DELETE CASCADE
);

CREATE TABLE Review(
	ReviewID INT AUTO_INCREMENT PRIMARY KEY,
	Rating INT,
	RestaurantID INT,
	CustomerID INT,
	Comment VARCHAR(150),
	Created_at DATETIME,
	Updated_at DATETIME,
FOREIGN KEY (RestaurantID) REFERENCES Restaurant(RestaurantID) ON DELETE CASCADE,
FOREIGN KEY (CustomerID) REFERENCES Person(UserID) ON DELETE SET NULL
);


CREATE TABLE Menu(
	MenuID INT AUTO_INCREMENT PRIMARY KEY,
	Last_updated DATETIME,
	RestaurantID INT,
	FOREIGN KEY (RestaurantID) REFERENCES Restaurant(RestaurantID) ON DELETE  CASCADE
);

CREATE TABLE MenuItem(
	Name VARCHAR(99),
	Price DECIMAL(8,2),
	MenuID INT,
	PRIMARY KEY (Name, MenuID),
	FOREIGN KEY (MenuID) REFERENCES Menu(MenuID) ON DELETE CASCADE
);


INSERT INTO Restaurant (Address, Name) VALUES
('123 First St, San Jose', 'La Bictorias Burritos'),
('456 Second St, San Jose', 'Happy Orange'),
('78 Third St, San Jose', 'Lees Gravelwiches'),
('911 Fourth St, San Jose', 'Bob''s Tomatoes'),
('920 Fifth St, San Jose', 'Pho King #1');

INSERT INTO Person (Email, Name) VALUES
('alice@example.com', 'Alice Johnson'),
('bob@example.com', 'Bob Smith'),
('carol@example.com', 'Carol Lee'),
('sem@sjsu.edu', 'Sem Victory'),
('yeri@thegoat.com', 'Jerison Dubs'),
('steve@awesome.net', 'Steve Soul'),
('yipyip@bossing.com', 'Yip yip'),
('david@example.com', 'David Kim');

INSERT INTO PersonRestaurantOwnership (OwnerID, RestaurantID) VALUES
(1, 1), -- Alice owns La Bictorias Burritos
(2, 2), -- Bob owns Happy Orange
(3, 3); -- Carol owns Lees Gravelwiches

INSERT INTO Category (Cuisine) VALUES
('Italian'),
('Seafood'),
('American'),
('Mexican'),
('Tex-Mex'),
('Tea'),
('Asian');

INSERT INTO RestaurantCategory (RestaurantID, CategoryID) VALUES
(1, 4), -- La Bictorias Burritos is Mexican
(5, 7), -- Pho King #1 is Asian
(3, 3), -- Lees Gravelwiches is American
(3, 7); -- Lees Gravelwiches is also Asian

INSERT INTO Review (Rating, RestaurantID, CustomerID, Comment, Created_at, Updated_at) VALUES
(5, 1, 4, 'Fantastic food and great service!', '2025-01-10 12:00:00', '2025-01-10 12:00:00'),
(4, 2, 1, 'Loved the seafood platter.', '2025-02-05 18:30:00', '2025-02-05 18:30:00'),
(3, 3, 2, 'Decent burgers, nice view.', '2025-03-15 13:45:00', '2025-03-15 13:45:00');
INSERT INTO Menu (Last_updated, RestaurantID) VALUES
('2025-01-01 10:00:00', 1),
('2025-02-01 11:30:00', 2),
('2025-03-01 09:45:00', 3);

INSERT INTO MenuItem (Name, Price, MenuID) VALUES
('Super Burrito', 14.99, 1),
('Super Quesadilla', 11.49, 1),
('Popcorn Chicken Platter', 19.99, 2),
('Matcha Milk Tea w/ Boba', 9.99, 2),
('Sandwich w/ Pork', 10.49, 3),
('Small Sandwich', 8.99, 3);
