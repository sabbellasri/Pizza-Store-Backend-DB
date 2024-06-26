-- Srinivasa Reddy Sabbella  

  

   

  

USE PIZZERIA;   

  

   

  

SET SQL_SAFE_UPDATES = 0;   

  

INSERT INTO customer values(-1, ' INSTORE Customer ', '0000000000','None','None','None','None');  

  

   

  

INSERT INTO  topping(ToppingName,ToppingCostToCus,ToppingCostToBusi,ToppingCurr_Inv,ToppingMin_Inv,ToppingAmount_P,ToppingAmount_M,ToppingAmount_L,ToppingAmount_XL)   

  

VALUES    

  

   

  

  ('Pepperoni', 1.25, 0.2, 100,50,2,2.75, 3.5,4.5),    

  

   

  

  ('Sausage',1.25,0.15,100,50,2.5,3,3.5,4.25 ),    

  

   

  

  ('Ham',1.5,0.15,  78, 25, 2, 2.5, 3.25, 4),    

  

   

  

   ('Chicken', 1.75, 0.25, 56, 25, 1.5, 2, 2.25, 3),    

  

   ('Green Pepper',0.5,0.02, 79, 25,1,1.5, 2,  2.5),    

  

   ('Onion', 0.5, 0.02, 85, 25, 1, 1.5, 2, 2.75),    

  

   

  

  ('Roma Tomato', 0.75, 0.03,86,10, 2,3, 3.5, 4.5),    

  

   

  

   ('Mushrooms', 0.75, 0.1, 52, 50, 1.5, 2, 2.5, 3),    

  

   

  

    

  

   

  

   ( 'Black Olives',0.6,0.1,39,25, 0.75, 1,1.5, 2),    

  

   

  

    

  

   

  

    ('Pineapple', 1, 0.25, 15, 0, 1, 1.25, 1.75, 2),    

  

   

  

    

  

   

  

    ( 'Jalapenos',0.5,0.05,64,0,0.5,0.75,  1.25, 1.75 ),    

  

   

  

    

  

   

  

    ('Banana Peppers',0.5,0.05,  36,  0, 0.6,1,1.3,1.75 ),    

  

   

  

    

  

   

  

    ('Regular Cheese',0.5,  0.12, 250,50,  2,  3.5,5,7),    

  

   

  

    

  

   

  

    ('Four Cheese Blend',1,0.15,150, 25, 2, 3.5, 5,7  ),    

  

   

  

    

  

   

  

    ('Feta Cheese', 1.5,0.18,  75, 0, 1.75, 3, 4, 5.5),    

  

   

  

    

  

   

  

    ('Goat Cheese',1.5,0.2, 54, 0, 1.6, 2.75, 4, 5.5),    

  

   

  

    

  

   

  

    ('Bacon', 1.5, 0.25, 89, 0, 1, 1.5, 2, 3);    

  

   

  

    

  

   

  

    

  

   

  

INSERT INTO     

  

   

  

    

  

   

  

    discount(DiscountName,DiscountPercent,DiscountAmountInDollar)   

  

   

  

    

  

   

  

VALUES    

  

   

  

    

  

   

  

    ('Employee',15,NULL),   

  

   

  

    ('Lunch Special Medium',NULL,1.00),    

  

   

  

    

  

   

  

    ('Lunch Special Large',NULL,2.00),    

  

   

  

    

  

   

  

    ('Specialty Pizza',NULL,1.50),    

  

   

  

    

  

   

  

    ('Happy Hour',10,NULL),    

  

   

  

    

  

   

  

    ('Gameday Special',20,NULL);   

  

   

  

INSERT INTO   

  

   

  

    base   

  

   

  

VALUES   

  

   

  

    ('Small', 'Thin', 3, 0.5),   

  

   

  

    ('Small', 'Original', 3, 0.75),   

  

   

  

    ('Small', 'Pan', 3.5, 1),   

  

   

  

    ('Small', 'Gluten-Free', 4, 2),   

  

   

  

    ('Medium', 'Thin', 5, 1),   

  

   

  

    ('Medium', 'Original', 5, 1.5),   

  

   

  

    ('Medium', 'Pan', 6, 2.25),   

  

   

  

    ('Medium', 'Gluten-Free', 6.25, 3),   

  

   

  

    ('Large', 'Thin', 8, 1.25),   

  

   

  

    ('Large', 'Original', 8, 2),   

  

   

  

    ('Large', 'Pan', 9, 3),   

  

   

  

    ('Large', 'Gluten-Free', 9.5, 4),   

  

   

  

    ('XLarge', 'Thin', 10, 2),   

  

   

  

    ('XLarge', 'Original', 10, 3),   

  

   

  

    ('XLarge', 'Pan', 11.5, 4.5),   

  

   

  

    ('XLarge', 'Gluten-Free', 12.5, 6);   

  

   

  

   

  

   

  

   

  

   

  

    

  

   

  

    

  

   

  

-- order 1   

  

   

  

-- todo customer ID    

  

   

  

-- todo remove hard code ids   

  

   

  

-- todo discount   

  

   

  

INSERT INTO orders (OrderCustID, OrderTime, OrderType, OrderCostToCustomer, OrderCostToBusiness, OrderState)   

  

   

  

VALUES   

  

   

  

    (NULL, "2024-03-05 12:03:00", "dinein", 20.75, 3.68, "COMPLETED");   

  

   

  

    

  

   

  

INSERT INTO dinein (DineInOrderID, DineInTableNumber)   

  

   

  

SELECT LAST_INSERT_ID(), "21";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT DineInOrderID, "Thin", "Large", "COMPLETED", 3.68, 20.75   

  

   

  

FROM dinein   

  

   

  

where DineInTableNumber="21";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID,(Select ToppingID FROM topping where ToppingName= "Pepperoni"), false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Thin";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Regular Cheese") , true   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Thin";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Sausage") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Thin";   

  

   

  

    

  

   

  

INSERT INTO pizza_discount    

  

   

  

SELECT (select  DiscountID  from discount where DiscountName ="Lunch Special Large"), PizzaID    

  

   

  

from pizza p JOIN orders o ON p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=3.68 AND (OrderTime = '2024-03-05 12:03:00');   

  

   

  

    

  

   

  

-- order 2   

  

   

  

INSERT INTO orders (OrderCustID, OrderTime, OrderType, OrderCostToCustomer, OrderCostToBusiness, OrderState)   

  

   

  

VALUES   

  

   

  

    (NULL, "2024-04-03 12:05:00", "dinein", 19.78, 4.63, "COMPLETED");   

  

   

  

    

  

   

  

INSERT INTO dinein (DineInOrderID, DineInTableNumber)   

  

   

  

SELECT LAST_INSERT_ID(), "4";   

  

   

  

    

  

   

  

-- first pizza for order 2   

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT DineInOrderID, "Pan", "Medium", "COMPLETED", 3.23, 12.85   

  

   

  

FROM dinein   

  

   

  

where DineInTableNumber="4";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Feta Cheese") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Pan";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Black Olives") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Pan";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Roma Tomato") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Pan";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Mushrooms") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Pan";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Banana Peppers") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Pan";   

  

   

  

    

  

   

  

INSERT INTO pizza_discount    

  

   

  

SELECT  (select  DiscountID  from discount where DiscountName = "Specialty Pizza") , PizzaID    

  

   

  

from pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=3.23 and (OrderTime = '2024-04-03 12:05:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_discount    

  

   

  

SELECT (select  DiscountID  from discount where DiscountName = "Lunch Special Medium") , PizzaID    

  

   

  

from pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=3.23 and (OrderTime = '2024-04-03 12:05:00');   

  

   

  

    

  

   

  

-- second pizza for order 2   

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT DineInOrderID, "Original", "Small", "COMPLETED", 1.40, 6.93   

  

   

  

FROM dinein   

  

   

  

WHERE DineInTableNumber="4";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Banana Peppers") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Original";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Regular Cheese") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Original";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Chicken") , false   

  

   

  

FROM pizza   

  

   

  

where PizzaCrustType="Original";   

  

   

  

    

  

   

  

-- andrew customer   

  

   

  

INSERT INTO customer (CustomerName, CustomerPhone)   

  

   

  

VALUES ("Andrew Wilkes-Krier", "864-254-5861");   

  

   

  

    

  

   

  

-- andrews 6 pizzas   

  

   

  

INSERT INTO orders (OrderCustID, OrderTime, OrderType, OrderCostToCustomer, OrderCostToBusiness, OrderState)   

  

   

  

SELECT CustomerID, "2024-03-03 21:30:00", "pickup", 14.88*6, 3.30*6, "COMPLETED"   

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-254-5861";   

  

   

  

    

  

   

  

INSERT INTO pickup   

  

   

  

SELECT LAST_INSERT_ID(), CustomerID   

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-254-5861";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "Large", "COMPLETED", 3.30, 14.88   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-03-03 21:30:00";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "Large", "COMPLETED", 3.30, 14.88   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-03-03 21:30:00";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "Large", "COMPLETED", 3.30, 14.88   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-03-03 21:30:00";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "Large", "COMPLETED", 3.30, 14.88   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-03-03 21:30:00";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "Large", "COMPLETED", 3.30, 14.88   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-03-03 21:30:00";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "Large", "COMPLETED", 3.30, 14.88   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-03-03 21:30:00";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Regular Cheese"),  false   

  

   

  

FROM pizza    

  

   

  

where PizzaCrustType = "Original" and PizzaSize = "Large";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Pepperoni"),  false   

  

   

  

FROM pizza    

  

   

  

where PizzaCrustType = "Original" and PizzaSize = "Large";   

  

   

  

    

  

   

  

UPDATE customer   

  

   

  

SET CustomerStreet="115 Party Blvd", CustomerCity="Anderson", CustomerState="SC", CustomerZipCode=29621   

  

   

  

where CustomerName="Andrew Wilkes-Krier";   

  

   

  

    

  

   

  

INSERT INTO orders (OrderCustID, OrderTime, OrderType, OrderCostToCustomer, OrderCostToBusiness, OrderState)   

  

   

  

SELECT CustomerID, "2024-04-20 19:11:00", "delivery", 27.94+31.5+26.75, 9.19+6.25+8.18,"COMPLETED"   

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-254-5861";   

  

   

  

    

  

   

  

INSERT INTO delivery   

  

   

  

SELECT LAST_INSERT_ID(), CustomerID ,null  

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-254-5861";   

  

   

  

    

  

   

  

INSERT INTO order_discount(Order_Discount_DiscountID,Order_Discount_OrderID)   

  

   

  

SELECT (select DiscountID from discount  where DiscountName ="Gameday Special"), OrderID   

  

   

  

FROM orders o join delivery d on o.OrderID = d.DeliveryOrderID   

  

   

  

join customer c on c.CustomerID = d.DeliveryCustomerID   

  

   

  

WHERE (o.OrderTime = '2024-04-20 19:11:00');   

  

   

  

    

  

   

  

-- pepperoni + sausage pizza   

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "XLarge", "COMPLETED", 9.19, 27.94   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-04-20 19:11:00";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Pepperoni") , false   

  

   

  

FROM pizza    

  

   

  

where PizzaCrustType = "Original" and PizzaSize = "XLarge";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Sausage") , false   

  

   

  

FROM pizza    

  

   

  

where PizzaCrustType = "Original" and PizzaSize = "XLarge";   

  

   

  

    

  

   

  

-- pizza 2 ham + pineapple   

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "XLarge", "COMPLETED", 6.25, 31.50   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-04-20 19:11:00";   

  

   

  

    

  

   

  

INSERT INTO pizza_discount    

  

   

  

SELECT (select  DiscountID  from discount where DiscountName = "Specialty Pizza") , PizzaID    

  

   

  

from pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=6.25 and (OrderTime = '2024-04-20 19:11:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID,  Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Ham") , true   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=6.25 and (OrderTime = '2024-04-20 19:11:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Pineapple"),  true   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=6.25 and (OrderTime = '2024-04-20 19:11:00');   

  

   

  

   

  

   

  

    

  

   

  

-- pizza 3 chix + bacon   

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Original", "XLarge", "COMPLETED", 8.18, 26.75   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-04-20 19:11:00";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Bacon"), false   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=8.18  and (OrderTime = '2024-04-20 19:11:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Chicken") , false   

  

   

  

FROM pizza join orders o on PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=8.18  and (OrderTime = '2024-04-20 19:11:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Four Cheese Blend"), false   

  

   

  

FROM pizza    

  

   

  

where PizzaOrderID in (select OrderID FROM orders where OrderTime="2024-04-20 19:11:00");   

  

   

  

    

  

   

  

-- matt engers order   

  

   

  

INSERT INTO customer (CustomerName, CustomerPhone)   

  

   

  

VALUES ("Matt Engers", "864-474-9953");   

  

   

  

    

  

   

  

INSERT INTO orders (OrderCustID, OrderTime, OrderType, OrderCostToCustomer, OrderCostToBusiness, OrderState)   

  

   

  

SELECT CustomerID, "2024-03-02 17:30:00", "pickup", 27.45, 7.88,"COMPLETED"   

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-474-9953";   

  

   

  

    

  

   

  

INSERT INTO pickup   

  

   

  

SELECT LAST_INSERT_ID(), CustomerID   

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-474-9953";   

  

   

  

    

  

   

  

-- Pizza with Green Pepper,   

  

   

  

-- Onion, Roma Tomatoes, Mushrooms, and Black Olives on it. He wants the Goat Cheese   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Gluten-Free", "XLarge", "COMPLETED", 7.88, 27.45   

  

   

  

FROM orders   

  

   

  

where OrderTime='2024-03-02 17:30:00';   

  

   

  

    

  

   

  

INSERT INTO pizza_discount    

  

   

  

SELECT  (select  DiscountID  from discount where DiscountName = "Specialty Pizza") , PizzaID    

  

   

  

from pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=7.88 and (OrderTime = '2024-03-02 17:30:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Black Olives") , false   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=7.88 and (OrderTime = '2024-03-02 17:30:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Goat Cheese") , false   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=7.88 and (OrderTime = '2024-03-02 17:30:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Green Pepper") , false   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=7.88 and (OrderTime = '2024-03-02 17:30:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Onion") , false   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=7.88 and (OrderTime = '2024-03-02 17:30:00');   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Roma Tomato") , false   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=7.88 and (OrderTime = '2024-03-02 17:30:00');   

  

   

  

   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Mushrooms") , false   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=7.88 and (OrderTime = '2024-03-02 17:30:00');   

  

   

  

   

  

   

  

    

  

   

  

-- frank turner   

  

   

  

INSERT INTO customer (CustomerName, CustomerPhone, CustomerStreet, CustomerState, CustomerCity, CustomerZipCode)   

  

   

  

VALUES ("Frank Turner", "864-232-8944", "6745 Wessex St", "SC", "Anderson", "29621");   

  

   

  

    

  

   

  

INSERT INTO orders (OrderCustID, OrderTime, OrderType, OrderCostToCustomer, OrderCostToBusiness, OrderState)   

  

   

  

SELECT CustomerID, "2024-03-02 18:17:00", "delivery", 20.81, 3.19,"COMPLETED"   

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-232-8944";   

  

   

  

    

  

   

  

INSERT INTO delivery   

  

   

  

SELECT LAST_INSERT_ID(), CustomerID ,null  

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-232-8944";   

  

   

  

    

  

   

  

-- One large pizza with Chicken, Green   

  

   

  

-- Peppers, Onions, and Mushrooms. He wants the Four Cheese Blend (extra) and thin crust    

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Thin", "Large", "COMPLETED", 3.19, 20.81  

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-03-02 18:17:00";   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Chicken") , false   

  

   

  

FROM pizza    

  

   

  

where PizzaCostToBusi=3.19;   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Green Pepper") , false   

  

   

  

FROM pizza    

  

   

  

where PizzaCostToBusi=3.19;   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Onion"), false   

  

   

  

FROM pizza    

  

   

  

where PizzaCostToBusi=3.19;   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Mushrooms") , false   

  

   

  

FROM pizza    

  

   

  

where PizzaCostToBusi=3.19;   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Four Cheese Blend") , true   

  

   

  

FROM pizza    

  

   

  

where PizzaCostToBusi=3.19;   

  

   

  

    

  

   

  

-- milo auckerman   

  

   

  

INSERT INTO customer (CustomerName, CustomerPhone, CustomerStreet, CustomerState, CustomerCity, CustomerZipCode)   

  

   

  

VALUES ("Milo Auckerman", "864-878-5679", "8879 Suburban Home", "SC", "Anderson", "29621");   

  

   

  

    

  

   

  

INSERT INTO orders (OrderCustID, OrderTime, OrderType, OrderCostToCustomer, OrderCostToBusiness, OrderState)   

  

   

  

SELECT CustomerID, "2024-04-13 20:32:00", "delivery", 13.00+19.25, 2.00+3.25, "COMPLETED"   

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-878-5679";   

  

   

  

    

  

   

  

INSERT INTO order_discount(Order_Discount_DiscountID,Order_Discount_OrderID)   

  

   

  

SELECT (select DiscountID from discount  where DiscountName = 'Employee')  ,OrderID   

  

   

  

FROM orders o    

  

   

  

WHERE OrderTime = "2024-04-13 20:32:00";   

  

   

  

    

  

   

  

INSERT INTO delivery   

  

   

  

SELECT LAST_INSERT_ID(), CustomerID ,null  

  

   

  

FROM customer   

  

   

  

where CustomerPhone="864-878-5679";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Thin", "Large", "COMPLETED", 2.00, 13.00   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-04-13 20:32:00";   

  

   

  

    

  

   

  

INSERT INTO pizza (PizzaOrderID, PizzaCrustType, PizzaSize, PizzaState, PizzaCostToBusi, PizzaPriceToCust)   

  

   

  

SELECT OrderID, "Thin", "Large", "COMPLETED", 3.25, 19.25   

  

   

  

FROM orders   

  

   

  

where OrderTime="2024-04-13 20:32:00";   

  

   

  

    

  

   

  

-- one had the Four Cheese   

  

   

  

-- Blend on it (extra) (Price: $18.00, Cost: $2.75), the other was Regular Cheese and Pepperoni (extra)   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Four Cheese Blend") , true   

  

   

  

FROM pizza p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=2.00 and (OrderTime = "2024-04-13 20:32:00");   

  

   

  

    

  

   

  

-- pizza 2    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Regular Cheese") , false    

  

   

  

FROM pizza  p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=3.25 and (OrderTime = "2024-04-13 20:32:00");   

  

   

  

   

  

    

  

   

  

INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra)   

  

   

  

SELECT PizzaID, (Select ToppingID FROM topping where ToppingName= "Pepperoni"), true   

  

   

  

FROM pizza  p join orders o on p.PizzaOrderID = o.OrderID   

  

   

  

where PizzaCostToBusi=3.25 and (OrderTime = "2024-04-13 20:32:00");  
update orders set orderState='Complete' where orderState='COMPLETED';

  

