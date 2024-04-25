-- Srinivasa Reddy Sabbella

DROP SCHEMA IF EXISTS PIZZERIA;

CREATE SCHEMA IF NOT EXISTS PIZZERIA;

USE PIZZERIA;

CREATE TABLE customer (
  CustomerID INT  AUTO_INCREMENT,
  CustomerName VARCHAR(40),
  CustomerPhone VARCHAR(20),
  CustomerStreet VARCHAR(100) NOT NULL DEFAULT 'None',
  CustomerCity VARCHAR(20) NOT NULL DEFAULT 'None',
  CustomerState VARCHAR(30) NOT NULL DEFAULT 'None',
  CustomerZipCode VARCHAR(10) NOT NULL DEFAULT 'None',
  PRIMARY KEY (CustomerID)
);
CREATE TABLE base(
  BaseSize VARCHAR(25) NOT NULL,
  BaseCrustType VARCHAR(25) NOT NULL,
  BasePriceToCust DECIMAL(6,2) NOT NULL,
  BasePriceToBus DECIMAL(6,2) NOT NULL,
  PRIMARY KEY (BaseSize,BaseCrustType )
);

CREATE TABLE orders( 
  OrderID INT NOT NULL AUTO_INCREMENT,
  OrderCustID INT,
  OrderType VARCHAR(15) NOT NULL,
  OrderState VARCHAR(20) NOT NULL,
  OrderCostToBusiness DECIMAL(6,2) NOT NULL,
  OrderTime TIMESTAMP NOT NULL,
  OrderCostToCustomer DECIMAL(6,2) NOT NULL,
  PRIMARY KEY (OrderID),
  FOREIGN KEY (OrderCustID) REFERENCES customer(CustomerID)
);


CREATE TABLE dinein (
  DineInOrderID INT,
  DineInTableNumber VARCHAR(15) NOT NULL,
  PRIMARY KEY(DineinOrderID),
  FOREIGN KEY (DineinOrderID) REFERENCES orders(OrderID)
);

CREATE TABLE pickup (
  PickupOrderID INT ,
  PickupCustomerID INT NOT NULL,
  PRIMARY KEY (PickupOrderID),
  FOREIGN KEY (PickupOrderID) REFERENCES orders(OrderID),
  FOREIGN KEY (PickupCustomerID) REFERENCES customer(CustomerID)
);
CREATE TABLE delivery (
  DeliveryOrderID INT ,
  DeliveryCustomerID INT NOT NULL,
  Address varchar(200),
  PRIMARY KEY(DeliveryOrderID),
  FOREIGN KEY (DeliveryOrderID) REFERENCES orders(OrderID),
  FOREIGN KEY (DeliveryCustomerID) REFERENCES customer(CustomerID)
);


CREATE TABLE pizza (
  PizzaID INT  AUTO_INCREMENT,
  PizzaOrderID INT NOT NULL,
  PizzaSize VARCHAR(25) NOT NULL,
  PizzaCrustType VARCHAR(25) NOT NULL,
  PizzaPriceToCust DECIMAL(6,2) NOT NULL,
  PizzaCostToBusi DECIMAL(6,2) NOT NULL,
  PizzaState VARCHAR(15) NOT NULL,
  PRIMARY KEY(PizzaID),
  FOREIGN KEY (PizzaSize,PizzaCrustType) REFERENCES base(BaseSize,BaseCrustType),
  FOREIGN KEY (PizzaOrderID) REFERENCES orders(OrderID)
);


CREATE TABLE topping (
  ToppingID INT NOT NULL AUTO_INCREMENT,
  ToppingName VARCHAR(20) ,
  ToppingCostToCus DECIMAL(6,2) NOT NULL,
  ToppingCostToBusi DECIMAL(6,2) NOT NULL,
  ToppingCurr_Inv INT NOT NULL,
  ToppingMin_Inv INT NOT NULL,
  ToppingAmount_P DECIMAL(6,2) NOT NULL,
  ToppingAmount_M DECIMAL(6,2) NOT NULL,
  ToppingAmount_L DECIMAL(6,2) NOT NULL,
  ToppingAmount_XL DECIMAL(6,2) NOT NULL,
  PRIMARY KEY(ToppingId)
);

CREATE TABLE pizza_has_topping (
  Pizza_Has_ToppingPizzaID INT NOT NULL,
  Pizza_Has_ToppingToppingID INT  NOT NULL,
  Pizza_Has_ToppingExtra BOOL,
  PRIMARY KEY (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID),
  FOREIGN KEY (Pizza_Has_ToppingPizzaID) REFERENCES pizza(PizzaID),
  FOREIGN KEY (Pizza_Has_ToppingToppingID) REFERENCES topping(ToppingID)
);


CREATE TABLE discount (
  DiscountID int PRIMARY KEY AUTO_INCREMENT,
  DiscountName varchar(40),
  DiscountPercent INT,
  DiscountAmountInDollar DECIMAL(6,2)
);


CREATE TABLE pizza_discount (
  Pizza_Discount_Discount_ID INT NOT NULL,
  Pizza_Discount_Pizza_ID INT NOT NULL,
  PRIMARY KEY (Pizza_Discount_Discount_ID, Pizza_Discount_Pizza_ID),
  FOREIGN KEY (Pizza_Discount_Pizza_ID) REFERENCES pizza(PizzaID),
  FOREIGN KEY (Pizza_Discount_Discount_ID) REFERENCES discount(DiscountID)
);


CREATE TABLE order_discount (
  Order_Discount_DiscountID INT NOT NULL,
  Order_Discount_OrderID INT NOT NULL,
  PRIMARY KEY (Order_Discount_DiscountID, Order_Discount_OrderID),
  FOREIGN KEY (Order_Discount_OrderID) REFERENCES orders(OrderID),
  FOREIGN KEY (Order_Discount_DiscountID) REFERENCES discount(DiscountID)
);

