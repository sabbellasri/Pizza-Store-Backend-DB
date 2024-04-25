-- Srinivasa Reddy
USE PIZZERIA; 
CREATE VIEW ToppingPopularity AS
select ToppingName Topping, IF(sum(val) > 0, sum(val), 0) ToppingCount from topping
left join (select Pizza_Has_ToppingToppingID, IF(Pizza_Has_ToppingExtra=true, 2, 1) val from pizza_has_topping) x
on ToppingID=Pizza_Has_ToppingToppingID
group by ToppingName
order by ToppingCount desc;




CREATE VIEW ProfitByPizza AS
select Size,Crust,round(sum(profit),2) as Profit, Order_Month1 as 'Order Month' from (select distinct t1.Size,t1.Crust,t1.Profit,Order_Month1 from(SELECT PizzaSize 'Size', PizzaCrustType 'Crust', ROUND(SUM(PRICE_PIZZ_DIS)-SUM(PizzaCostToBusi),2) Profit, 
DATE_FORMAT(OrderTime, '%c/%Y') Order_Month
from orders
right join (SELECT PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,DIA,DP,CASE WHEN DIA IS NULL AND DP IS NOT NULL THEN PizzaPriceToCust-PizzaPriceToCust*DP/100 WHEN DIA IS NOT NULL AND DP IS NULL THEN PizzaPriceToCust-DIA WHEN DP IS NOT  NULL AND DIA IS  NOT NULL THEN PizzaPriceToCust-DIA-(PizzaPriceToCust*DP/100) ELSE PizzaPriceToCust END AS PRICE_PIZZ_DIS FROM(
select PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,SUM(DiscountAmountInDollar) AS DIA,SUM(DiscountPercent) AS DP  from orders right join pizza on OrderID=PizzaOrderID left join pizza_discount ON Pizza_Discount_Pizza_ID=PizzaID left join order_discount ON Order_Discount_OrderID=OrderId left join discount ON  Pizza_Discount_Discount_ID=DiscountId or Order_Discount_DiscountID=DiscountId GROUP BY PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi)F ) T 
 on OrderID=T.PizzaOrdERID
group by PizzaSize, PizzaCrustType,DATE_FORMAT(OrderTime, '%c/%Y')
order by Profit asc) t1 left join(select Size,Crust,Profit,case when profit1!=profit then null else Order_Month end as Order_Month1 ,profit1 from (select Size,Crust,Profit,Order_Month,max(Profit) over( partition by size,crust) as profit1 from(SELECT PizzaSize 'Size', PizzaCrustType 'Crust', ROUND(SUM(PRICE_PIZZ_DIS)-SUM(PizzaCostToBusi),2) Profit, 
DATE_FORMAT(OrderTime, '%c/%Y') Order_Month
from orders
right join (SELECT PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,DIA,DP,CASE WHEN DIA IS NULL AND DP IS NOT NULL THEN PizzaPriceToCust-PizzaPriceToCust*DP/100 WHEN DIA IS NOT NULL AND DP IS NULL THEN PizzaPriceToCust-DIA WHEN DP IS NOT  NULL AND DIA IS  NOT NULL THEN PizzaPriceToCust-DIA-(PizzaPriceToCust*DP/100) ELSE PizzaPriceToCust END AS PRICE_PIZZ_DIS FROM(
select PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,SUM(DiscountAmountInDollar) AS DIA,SUM(DiscountPercent) AS DP  from orders right join pizza on OrderID=PizzaOrderID left join pizza_discount ON Pizza_Discount_Pizza_ID=PizzaID left join order_discount ON Order_Discount_OrderID=OrderId left join discount ON  Pizza_Discount_Discount_ID=DiscountId or Order_Discount_DiscountID=DiscountId GROUP BY PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi)F ) T 
 on OrderID=T.PizzaOrdERID
group by PizzaSize, PizzaCrustType,DATE_FORMAT(OrderTime, '%c/%Y')
order by Profit asc) t2 ) t4)t3 on t1.Size=t3.Size and t1.Crust=t3.Crust and Order_Month1 is not null) t5 group by Size,Crust,Order_Month1 order by profit asc  ;

CREATE VIEW ProfitByOrderType AS 
SELECT ordertype as'CustomerType',DATE_FORMAT(ABC, '%c/%Y')as 'Order Month',Round(Sum(TotalOrderPrice),2) TotalOrderPrice,Round(sum(TotalOrderCost),2) as TotalOrderCost,Round(sum(profit),2) as Profit from(SELECT T2.ordertype,ABC,A as TotalOrderPrice,B as TotalOrderCost ,A-B AS Profit from(select ordertype,m,sum(total) A,sum(OrderCostToBusiness) B from(
SELECT orderid,ordertype,OrderTime as m, OrderCostToBusiness,SUM(PRICE_PIZZ_DIS) total 
from orders
right join (SELECT PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,DIA,DP,CASE WHEN DIA IS NULL AND DP IS NOT NULL THEN PizzaPriceToCust-PizzaPriceToCust*DP/100 WHEN DIA IS NOT NULL AND DP IS NULL THEN PizzaPriceToCust-DIA WHEN DP IS NOT  NULL AND DIA IS  NOT NULL THEN PizzaPriceToCust-DIA-(PizzaPriceToCust*DP/100) ELSE PizzaPriceToCust END AS PRICE_PIZZ_DIS FROM(
select PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,SUM(DiscountAmountInDollar) AS DIA,SUM(DiscountPercent) AS DP  from orders right join pizza on OrderID=PizzaOrderID left join pizza_discount ON Pizza_Discount_Pizza_ID=PizzaID left join order_discount ON Order_Discount_OrderID=OrderId left join discount ON  Pizza_Discount_Discount_ID=DiscountId or Order_Discount_DiscountID=DiscountId GROUP BY PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi)F ) T 
 on OrderID=T.PizzaOrdERID
group by orderid,ordertype,OrderCostToBusiness,m ) T1 group by ordertype,m)T2 LEFT JOIN 
(SELECT ORDERTYPE,CASE WHEN  TOTALORDERPRICE=AB THEN M ELSE NULL END ABC FROM  ( SELECT ordertype,m,A as TotalOrderPrice,B as TotalOrderCost,max(A) over (partition by ordertype) AB ,A-B AS Profit from(select ordertype,m,sum(total) A,sum(OrderCostToBusiness) B from(
SELECT orderid,ordertype,OrderTime as m, OrderCostToBusiness,SUM(PRICE_PIZZ_DIS) total 
from orders
right join (SELECT PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,DIA,DP,CASE WHEN DIA IS NULL AND DP IS NOT NULL THEN PizzaPriceToCust-PizzaPriceToCust*DP/100 WHEN DIA IS NOT NULL AND DP IS NULL THEN PizzaPriceToCust-DIA WHEN DP IS NOT  NULL AND DIA IS  NOT NULL THEN PizzaPriceToCust-DIA-(PizzaPriceToCust*DP/100) ELSE PizzaPriceToCust END AS PRICE_PIZZ_DIS FROM(
select PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,SUM(DiscountAmountInDollar) AS DIA,SUM(DiscountPercent) AS DP  from orders right join pizza on OrderID=PizzaOrderID left join pizza_discount ON Pizza_Discount_Pizza_ID=PizzaID left join order_discount ON Order_Discount_OrderID=OrderId left join discount ON  Pizza_Discount_Discount_ID=DiscountId or Order_Discount_DiscountID=DiscountId GROUP BY PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi)F ) T 
 on OrderID=T.PizzaOrdERID
group by orderid,ordertype,OrderCostToBusiness,m ) T1 group by ordertype,m)T2 ) T3 WHERE CASE WHEN  TOTALORDERPRICE=AB THEN M ELSE NULL END IS NOT NULL)T4 ON T4.ORDERTYPE=T2.ORDERTYPE ) 
T5 group by ordertype,DATE_FORMAT(ABC, '%c/%Y')
union
SELECT NULL,'Grand Total',Round(Sum(TotalOrderPrice),2) TotalOrderPrice,Round(sum(TotalOrderCost),2) as TotalOrderCost,Round(sum(profit),2) as profit from(SELECT ordertype as'CustomerType',DATE_FORMAT(ABC, '%c/%Y')as 'Order Month',Round(Sum(TotalOrderPrice),2) TotalOrderPrice,Round(sum(TotalOrderCost),2) as TotalOrderCost,Round(sum(profit),2) as profit from(SELECT T2.ordertype,ABC,A as TotalOrderPrice,B as TotalOrderCost ,A-B AS Profit from(select ordertype,m,sum(total) A,sum(OrderCostToBusiness) B from(
SELECT orderid,ordertype,OrderTime as m, OrderCostToBusiness,SUM(PRICE_PIZZ_DIS) total 
from orders
right join (SELECT PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,DIA,DP,CASE WHEN DIA IS NULL AND DP IS NOT NULL THEN PizzaPriceToCust-PizzaPriceToCust*DP/100 WHEN DIA IS NOT NULL AND DP IS NULL THEN PizzaPriceToCust-DIA WHEN DP IS NOT  NULL AND DIA IS  NOT NULL THEN PizzaPriceToCust-DIA-(PizzaPriceToCust*DP/100) ELSE PizzaPriceToCust END AS PRICE_PIZZ_DIS FROM(
select PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,SUM(DiscountAmountInDollar) AS DIA,SUM(DiscountPercent) AS DP  from orders right join pizza on OrderID=PizzaOrderID left join pizza_discount ON Pizza_Discount_Pizza_ID=PizzaID left join order_discount ON Order_Discount_OrderID=OrderId left join discount ON  Pizza_Discount_Discount_ID=DiscountId or Order_Discount_DiscountID=DiscountId GROUP BY PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi)F ) T 
 on OrderID=T.PizzaOrdERID
group by orderid,ordertype,OrderCostToBusiness,m ) T1 group by ordertype,m)T2 LEFT JOIN 
(SELECT ORDERTYPE,CASE WHEN  TOTALORDERPRICE=AB THEN M ELSE NULL END ABC FROM  ( SELECT ordertype,m,A as TotalOrderPrice,B as TotalOrderCost,max(A) over (partition by ordertype) AB ,A-B AS Profit from(select ordertype,m,sum(total) A,sum(OrderCostToBusiness) B from(
SELECT orderid,ordertype,OrderTime as m, OrderCostToBusiness,SUM(PRICE_PIZZ_DIS) total 
from orders
right join (SELECT PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,DIA,DP,CASE WHEN DIA IS NULL AND DP IS NOT NULL THEN PizzaPriceToCust-PizzaPriceToCust*DP/100 WHEN DIA IS NOT NULL AND DP IS NULL THEN PizzaPriceToCust-DIA WHEN DP IS NOT  NULL AND DIA IS  NOT NULL THEN PizzaPriceToCust-DIA-(PizzaPriceToCust*DP/100) ELSE PizzaPriceToCust END AS PRICE_PIZZ_DIS FROM(
select PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi,SUM(DiscountAmountInDollar) AS DIA,SUM(DiscountPercent) AS DP  from orders right join pizza on OrderID=PizzaOrderID left join pizza_discount ON Pizza_Discount_Pizza_ID=PizzaID left join order_discount ON Order_Discount_OrderID=OrderId left join discount ON  Pizza_Discount_Discount_ID=DiscountId or Order_Discount_DiscountID=DiscountId GROUP BY PizzaID,PizzaOrderID,PizzaSize,PizzaCrustType,PizzaPriceToCust,PizzaCostToBusi)F ) T 
 on OrderID=T.PizzaOrdERID
group by orderid,ordertype,OrderCostToBusiness,m ) T1 group by ordertype,m)T2 ) T3 WHERE CASE WHEN  TOTALORDERPRICE=AB THEN M ELSE NULL END IS NOT NULL)T4 ON T4.ORDERTYPE=T2.ORDERTYPE ) 
T5 group by ordertype,DATE_FORMAT(ABC, '%c/%Y')) T6;


select * from ToppingPopularity;

select * from ProfitByPizza;

select * from ProfitByOrderType;

select * from orders;
