package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";


	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	public static int getLatestCustomerId() throws SQLException, IOException {
		connect_to_db();
		int latestCustomerId = -1;

		try (
				Statement statement = conn.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT MAX(CustomerID) AS CustomerID FROM customer");
		) {
			if (resultSet.next()) {
				latestCustomerId = resultSet.getInt("CustomerID");
			}
		} catch (SQLException e) {
			System.out.println("Error while fetching the latest customer ID: " + e.getMessage());
			throw e; // Rethrow the exception to handle it in the calling code
		} finally {
			if(conn!=null) {
				conn.close();
			}// Assuming this method closes the database connection
		}

		return latestCustomerId;
	}

	public static int getLatestOrderId() throws SQLException, IOException {
		connect_to_db();

		int latestOrderId = -1;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(OrderID) AS LatestOrderId FROM orders");

			if (rs.next()) {
				latestOrderId = rs.getInt("LatestOrderId");
			}
		} finally {
			if(conn!=null) {
				conn.close();
			}
		}

		return latestOrderId;
	}

	public static int getLatestPizzaId() throws SQLException, IOException {
		connect_to_db();

		int latestPizzaId = -1;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT MAX(PizzaID) AS LatestPizzaId FROM pizza");

			if (rs.next()) {
				latestPizzaId = rs.getInt("LatestPizzaId");
			}
		} finally {
			if(conn!=null) {
				conn.close();
			}
		}

		return latestPizzaId;
	}

	public static void addOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 *
		 */


		try {
			// Start a transaction

			// Insert into orders table
			String insertOrderQuery = "INSERT INTO orders (OrderCustID, OrderType, OrderState, OrderCostToBusiness, OrderTime, OrderCostToCustomer) " +
					"VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setInt(1, o.getCustID());
				pstmt.setString(2, o.getOrderType());
				pstmt.setString(3, "Pending"); // Assuming the initial state is pending
				pstmt.setDouble(4, o.getBusPrice());
				pstmt.setString(5, o.getDate());
				pstmt.setDouble(6, o.getCustPrice());
				int affectedRows = pstmt.executeUpdate();


				// Retrieve the generated OrderID
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						o.setOrderID(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating order failed, no ID obtained.");
					}
				}
			}

			// Insert into specific order type table
			if (o instanceof PickupOrder) {
				String insertPickupQuery = "INSERT INTO pickup (PickupOrderID, PickupCustomerID) VALUES (?, ?)";
				try (PreparedStatement pstmt = conn.prepareStatement(insertPickupQuery)) {
					pstmt.setInt(1, o.getOrderID());
					pstmt.setInt(2, o.getCustID());
					pstmt.executeUpdate();
				}
			} else if (o instanceof DeliveryOrder) {
				DeliveryOrder deliveryOrder = (DeliveryOrder) o;
				String insertDeliveryQuery = "INSERT INTO delivery (DeliveryOrderID, DeliveryCustomerID,Address) VALUES (?, ?, ?)";
				try (PreparedStatement pstmt = conn.prepareStatement(insertDeliveryQuery)) {
					pstmt.setInt(1, deliveryOrder.getOrderID());
					pstmt.setInt(2, deliveryOrder.getCustID());
					pstmt.setString(3, deliveryOrder.getAddress().replace("/n", " "));
					pstmt.executeUpdate();
				}
			} else if (o instanceof DineinOrder) {
				// Insert into dine-in specific table or handle as needed
				DineinOrder dineinOrder = (DineinOrder) o;
				String insertDeliveryQuery = "INSERT INTO dinein (DineInOrderID, DineInTableNumber) VALUES (?, ?)";
				try (PreparedStatement pstmt = conn.prepareStatement(insertDeliveryQuery)) {
					pstmt.setInt(1, dineinOrder.getOrderID());
					pstmt.setInt(2, dineinOrder.getTableNum());
					pstmt.executeUpdate();
				}
			}

			// Commit the transaction
			
		} finally {
			// Reset auto-commit to true and close connection
			if(conn!=null) {
				conn.close();
			}
		}
	}

	public static void addPizza(Pizza p) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Add the code needed to insert the pizza into  the database.
		 * Keep in mind adding pizza discounts and toppings associated with the pizza,
		 * there are other methods below that may help with that process.
		 *
		 */
		try {
			// Insert into pizza table
			String insertPizzaQuery = "INSERT INTO pizza (PizzaOrderID, PizzaSize, PizzaCrustType, PizzaPriceToCust, PizzaCostToBusi, PizzaState) " +
					"VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(insertPizzaQuery, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setInt(1, p.getOrderID());
				pstmt.setString(2, p.getSize());
				pstmt.setString(3, p.getCrustType());
				pstmt.setDouble(4, p.getCustPrice());
				pstmt.setDouble(5, p.getBusPrice());
				pstmt.setString(6, p.getPizzaState());
				int affectedRows = pstmt.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Creating pizza failed, no rows affected.");
				}

				// Retrieve the generated PizzaID
				try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						p.setPizzaID(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating pizza failed, no ID obtained.");
					}
				}
			}

			// Insert into pizza_has_topping table



		} catch (SQLException e) {
			// Rollback the transaction if an error occurs
			throw e;
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this method will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();
		/*
		 * This method should do 2 two things.
		 * - update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * - connect the topping to the pizza
		 *   What that means will be specific to your implementation.
		 *
		 * Ideally, you shouldn't let toppings go negative....but this should be dealt with BEFORE calling this method.
		 *
		 */

		try {
			// Update topping inventory
			String updateInventoryQuery = "UPDATE topping SET ToppingCurr_Inv = ? WHERE ToppingID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(updateInventoryQuery)) {
				int currentInventory = t.getCurINVT();
				int usedInventory = isDoubled ? 2 : 1; // If the topping is doubled, we use double the amount
				pstmt.setInt(1, currentInventory - usedInventory);
				pstmt.setInt(2, t.getTopID());
				pstmt.executeUpdate();
			}

			// Connect the topping to the pizza in the pizza_has_topping table
			String insertPizzaToppingQuery = "INSERT INTO pizza_has_topping (Pizza_Has_ToppingPizzaID, Pizza_Has_ToppingToppingID, Pizza_Has_ToppingExtra) VALUES (?, ?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(insertPizzaToppingQuery)) {
				pstmt.setInt(1, p.getPizzaID());
				pstmt.setInt(2, t.getTopID());
				pstmt.setBoolean(3, isDoubled);
				pstmt.executeUpdate();
			}

			// Commit the transaction

		} catch (SQLException e) {
			// Rollback the transaction if an error occurs

			throw e;
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This method connects a discount with a Pizza in the database.
		 *
		 * What that means will be specific to your implementatinon.
		 */
		try {
			// Connect the discount with the pizza in the pizza_discount table
			String insertPizzaDiscountQuery = "INSERT INTO pizza_discount (Pizza_Discount_Discount_ID, Pizza_Discount_Pizza_ID) VALUES (?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(insertPizzaDiscountQuery)) {
				pstmt.setInt(1, d.getDiscountID());
				pstmt.setInt(2, p.getPizzaID());
				pstmt.executeUpdate();
			}

			// Commit the transaction
			
		} catch (SQLException e) {
			// Rollback the transaction if an error occurs
			
			throw e;
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException {
		connect_to_db();
		/*
		 * This method connects a discount with an order in the database
		 *
		 * You might use this, you might not depending on where / how to want to update
		 * this information in the database
		 */
		try {
			// Connect the discount with the order in the order_discount table
			String insertOrderDiscountQuery = "INSERT INTO order_discount (Order_Discount_DiscountID, Order_Discount_OrderID) VALUES (?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(insertOrderDiscountQuery)) {
				pstmt.setInt(1, d.getDiscountID());
				pstmt.setInt(2, o.getOrderID());
				pstmt.executeUpdate();
			}

			// Commit the transaction

		} catch (SQLException e) {
			// Rollback the transaction if an error occurs

			throw e;
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
		/*r
		 * This method adds a new customer to the database.
		 *
		 */

		try {
			// Insert the new customer into the database
			String insertCustomerQuery = "INSERT INTO customer (CustomerName, CustomerPhone, CustomerStreet, CustomerCity, CustomerState, CustomerZipCode) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = conn.prepareStatement(insertCustomerQuery, Statement.RETURN_GENERATED_KEYS)) {
				pstmt.setString(1, c.getFName() + " " + c.getLName());
				pstmt.setString(2, c.getPhone());
				pstmt.setString(3, c.getAddress().split("/n")[0]); // Street
				pstmt.setString(4, c.getAddress().split("/n")[1]); // City
				pstmt.setString(5, c.getAddress().split("/n")[2]); // State
				pstmt.setString(6, c.getAddress().split("/n")[3]); // Zip Code
				pstmt.executeUpdate();

				// Retrieve the auto-generated customer ID
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					int customerId = rs.getInt(1);
					c.setCustID(customerId);
				}
			}

			// Commit the transaction
			
		} catch (SQLException e) {
			// Rollback the transaction if an error occurs
			;
			throw e;
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void completeOrder(Order o) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Find the specifed order in the database and mark that order as complete in the database.
		 *
		 */

		try {
			// Update the order state to mark the order as complete
			String updateOrderQuery = "UPDATE orders SET OrderState = ? WHERE OrderID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(updateOrderQuery)) {
				pstmt.setString(1, "Complete"); // Assuming "Complete" is the state indicating the order is complete
				pstmt.setInt(2, o.getOrderID());
				pstmt.executeUpdate();
			}

			// Commit the transaction
			
		} catch (SQLException e) {
			// Rollback the transaction if an error occurs
			;
			throw e;
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 *
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */


		ArrayList<Order> orders = new ArrayList<>();

		try {
			// Construct the SQL query based on the value of openOnly
			String query = "SELECT o.OrderID, o.OrderCustID, o.OrderType, o.OrderTime, o.OrderCostToCustomer, " +
					"o.OrderCostToBusiness, o.OrderState, d.DineInTableNumber " +
					"FROM orders o LEFT JOIN dinein d ON o.OrderID = d.DineInOrderID";

			if (openOnly) {
				query += " WHERE o.OrderState != 'Complete'";
			}
			else {
				query += " WHERE o.OrderState = 'Complete'";
			}

			// Execute the query
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				ResultSet rs = pstmt.executeQuery();

				// Iterate through the result set and create Order objects
				while (rs.next()) {
					int orderID = rs.getInt("OrderID");
					int custID = rs.getInt("OrderCustID");
					String orderType = rs.getString("OrderType");
					String date = rs.getString("OrderTime");
					double custPrice = rs.getDouble("OrderCostToCustomer");
					double busPrice = rs.getDouble("OrderCostToBusiness");
					int isComplete;
					if (rs.getString("OrderState").equalsIgnoreCase("Complete")) isComplete = 1;
					else isComplete = 0;

					// Depending on the order type, create the appropriate Order subclass
					if (orderType.equalsIgnoreCase("DineIn")) {
						int tableNumber = rs.getInt("DineInTableNumber");
						orders.add(new DineinOrder(orderID, custID, date, custPrice, busPrice, isComplete, tableNumber));
					} else if (orderType.equalsIgnoreCase("Delivery")) {
						String address = null;
						String query1 = "SELECT DeliveryCustomerID,Address FROM delivery WHERE DeliveryOrderID = ?";
						try (PreparedStatement pstmt1 = conn.prepareStatement(query1)) {
							pstmt1.setInt(1, orderID);
							ResultSet rs1 = pstmt1.executeQuery();
							if (rs1.next()) {
								int customerID = rs1.getInt("DeliveryCustomerID");
								address = rs1.getString("Address");

							}
						}
						orders.add(new DeliveryOrder(orderID, custID, date, custPrice, busPrice, isComplete, address));
					} else if (orderType.equalsIgnoreCase("Pickup")) {
						orders.add(new PickupOrder(orderID, custID, date, custPrice, busPrice, 0, isComplete));
					}
				}
			}
		} finally {
			// Close the connection
			if (conn != null) {
				conn.close();
			}
		}

		return orders;
		//DO NOT FORGET TO CLOSE YOUR CONNECTION

	}

	public static ArrayList<Order> getAllOrders() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 *
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */


		ArrayList<Order> orders = new ArrayList<>();

		try {
			// Construct the SQL query based on the value of openOnly
			String query =  "SELECT o.OrderID, o.OrderCustID, o.OrderType, o.OrderTime, o.OrderCostToCustomer, " +
			"o.OrderCostToBusiness, o.OrderState, d.DineInTableNumber " +
					"FROM orders o LEFT JOIN dinein d ON o.OrderID = d.DineInOrderID";;


			// Execute the query
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				ResultSet rs = pstmt.executeQuery();

				// Iterate through the result set and create Order objects
				while (rs.next()) {
					int orderID = rs.getInt("OrderID");
					int custID = rs.getInt("OrderCustID");
					String orderType = rs.getString("OrderType");
					String date = rs.getString("OrderTime");
					double custPrice = rs.getDouble("OrderCostToCustomer");
					double busPrice = rs.getDouble("OrderCostToBusiness");
					int isComplete;
					if (rs.getString("OrderState").equalsIgnoreCase("Complete")) isComplete = 1;
					else isComplete = 0;

					// Depending on the order type, create the appropriate Order subclass
					if (orderType.equalsIgnoreCase("DineIn")) {
						int tableNumber = rs.getInt("DineInTableNumber");
						orders.add(new DineinOrder(orderID, custID, date, custPrice, busPrice, isComplete, tableNumber));
					} else if (orderType.equalsIgnoreCase("Delivery")) {
						String address = null;
						String query1 = "SELECT DeliveryCustomerID,Address FROM delivery WHERE DeliveryOrderID = ?";
						try (PreparedStatement pstmt1 = conn.prepareStatement(query1)) {
							pstmt1.setInt(1, orderID);
							ResultSet rs1 = pstmt1.executeQuery();
							if (rs1.next()) {
								int customerID = rs1.getInt("DeliveryCustomerID");
								address = rs1.getString("Address");

							}
						}
						orders.add(new DeliveryOrder(orderID, custID, date, custPrice, busPrice, isComplete, address));
					} else if (orderType.equalsIgnoreCase("Pickup")) {
						orders.add(new PickupOrder(orderID, custID, date, custPrice, busPrice, 0, isComplete));
					}
				}
			}
		} finally {
			// Close the connection
			if (conn != null) {
				conn.close();
			}
		}

		return orders;
		//DO NOT FORGET TO CLOSE YOUR CONNECTION

	}

	public static Order getLastOrder() throws SQLException, IOException {
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there should ALWAYS be a "last order"!
		 */
		connect_to_db();
		Order lastOrder = null;
		try {
			String query = "SELECT * FROM orders  ORDER BY OrderID DESC LIMIT 1";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					int orderID = rs.getInt("OrderID");
					int custID = rs.getInt("OrderCustID");
					String orderType = rs.getString("OrderType");
					String date = rs.getString("OrderTime");
					double custPrice = rs.getDouble("OrderCostToCustomer");
					double busPrice = rs.getDouble("OrderCostToBusiness");
					String isComplete1 = rs.getString("OrderState");
					int isComplete=0;
					if(isComplete1.equalsIgnoreCase("Complete"))
					{
						isComplete=1;
					}

					// Check the order type to instantiate the appropriate subclass
					switch (orderType) {
						case DBNinja.dine_in:
							int DineInTableNumber=0;
							String query2 = "SELECT * FROM dinein WHERE DineInOrderID = ?";
							try (PreparedStatement pstmt2 = conn.prepareStatement(query2)) {
								pstmt2.setInt(1, orderID);
								ResultSet rs2 = pstmt2.executeQuery();
								if (rs2.next()) {
									DineInTableNumber = rs2.getInt("DineInTableNumber");
								}
								lastOrder = new DineinOrder(orderID, custID, date, custPrice, busPrice, isComplete, rs2.getInt("DineInTableNumber"));
							}
							break;
						case DBNinja.delivery:
							String address = null;
							String query1 = "SELECT DeliveryCustomerID,Address FROM delivery WHERE DeliveryOrderID = ?";
							try (PreparedStatement pstmt1 = conn.prepareStatement(query1)) {
								pstmt1.setInt(1, orderID);
								ResultSet rs1 = pstmt.executeQuery();
								if (rs1.next()) {
									int customerID = rs1.getInt("DeliveryCustomerID");
									address = rs1.getString("Address");
								}
								lastOrder = new DeliveryOrder(orderID, custID, date, custPrice, busPrice, isComplete, address);
							}
							break;
						case DBNinja.pickup:
							// Assuming pickup orders don't have additional attributes specific to the PickupOrder class
							lastOrder = new Order(orderID, custID, orderType, date, custPrice, busPrice, isComplete);
							break;
						default:
							// Handle unexpected order types
							throw new IllegalArgumentException("Unexpected order type: " + orderType);
					}
				}
			}
		}
		// Close the connection
    finally {
			if(conn!=null) {
				conn.close();
			}
		}

		return lastOrder;
	}

	public static ArrayList<Order> getOrdersByDate(String date) throws SQLException, IOException {
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *
		 */

		connect_to_db();

		ArrayList<Order> orders = new ArrayList<>();
		try {
			String query = "SELECT * FROM orders  join dinein on orders.OrderID=dinein.DineInOrderID WHERE DATE(OrderTime) >= ?";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				pstmt.setString(1, date);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					int orderID = rs.getInt("OrderID");
					int custID = rs.getInt("OrderCustID");
					String orderType = rs.getString("OrderType");
					double custPrice = rs.getDouble("OrderCostToCustomer");
					double busPrice = rs.getDouble("OrderCostToBusiness");
					 String isComplete1 = rs.getString("OrderState");
					 int isComplete=0;
					 if(isComplete1.equalsIgnoreCase("complete"))
					 {
						 isComplete=1;
					 }

					// Check the order type to instantiate the appropriate subclass
					switch (orderType) {
						case DBNinja.dine_in:
							orders.add(new DineinOrder(orderID, custID, date, custPrice, busPrice, isComplete, rs.getInt("DineInTableNumber")));
							break;
						case DBNinja.delivery:
							String address = null;
							String query1 = "SELECT DeliveryCustomerID FROM delivery WHERE DeliveryOrderID = ?";
							try (PreparedStatement pstmt1 = conn.prepareStatement(query1)) {
								pstmt1.setInt(1, orderID);
								ResultSet rs1 = pstmt.executeQuery();
								if (rs1.next()) {
									int customerID = rs.getInt("DeliveryCustomerID");
									address = rs.getString("Address");
								}
								orders.add(new DeliveryOrder(orderID, custID, date, custPrice, busPrice, isComplete, address));
							}
							break;
						case DBNinja.pickup:
							// Assuming pickup orders don't have additional attributes specific to the PickupOrder class
							orders.add(new Order(orderID, custID, orderType, date, custPrice, busPrice, isComplete));
							break;
						default:
							// Handle unexpected order types
							throw new IllegalArgumentException("Unexpected order type: " + orderType);
					}
				}
			}
		}
		// Close the connection

		finally {
			if (conn != null) {
				conn.close();
			}
		}

		return orders;
	}

	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database for all the available discounts and
		 * return them in an arrayList of discounts.
		 *
		 */
		ArrayList<Discount> discounts = new ArrayList<>();
		try {
			String query = "SELECT * FROM discount";
			try (PreparedStatement pstmt = conn.prepareStatement(query)) {
				Discount discount = null;
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					int discountID = rs.getInt("DiscountID");
					String discountName = rs.getString("DiscountName");
					int discountPercent = rs.getInt("DiscountPercent");
					double discountAmount = rs.getDouble("DiscountAmountInDollar");
					boolean flag = true;
					if (discountPercent == 0) {
						flag = false;
						discount = new Discount(discountID, discountName, discountAmount, flag);
					} else {
						discount = new Discount(discountID, discountName, discountPercent, flag);
					}

					// Create a Discount object and add it to the list

					discounts.add(discount);
				}
			}
		}

		// Close the connection
		finally {
			if(conn!=null) {
				conn.close();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return discounts;
	}

	public static Discount findDiscountByName(String name) throws SQLException, IOException {
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *
		 */
		connect_to_db();

		String query = "SELECT * FROM discount WHERE DiscountName = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			Discount dis=null;
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int discountID = rs.getInt("DiscountID");
				int discountPercent = rs.getInt("DiscountPercent");
				double discountAmount = rs.getDouble("DiscountAmountInDollar");

				// Create a Discount object and return it
				boolean flag = true;
				if (discountPercent == 0) {
					flag = false;
				}
				if(flag==false) {
					dis=new Discount(discountID, name, discountAmount, flag);
				}
				else if(flag==true)
				{
					dis= new Discount(discountID, name, discountPercent, flag);
				}
			} else {
				// Discount with the given name was not found
				return null;
			}
			return dis;
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		/*
		 * Query the data for all the customers and return an arrayList of all the customers.
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */

		connect_to_db();

		ArrayList<Customer> customerList = new ArrayList<>();

		String query = "SELECT * FROM customer ORDER BY CustomerName"; // Assuming you want to order customers by name
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int customerId = rs.getInt("CustomerID");
				String customerName = rs.getString("CustomerName");
				String customerPhone = rs.getString("CustomerPhone");
				String customerStreet = rs.getString("CustomerStreet");
				String customerCity = rs.getString("CustomerCity");
				String customerState = rs.getString("CustomerState");
				String customerZipCode = rs.getString("CustomerZipCode");

				// Split the customer name into first name and last name
				String[] nameParts = customerName.split(" ", 2);
				String firstName = nameParts[0];
				String lastName = nameParts.length > 1 ? nameParts[1] : ""; // If there's no last name, set it to an empty string

				// Create a Customer object and add it to the list
				Customer customer = new Customer(customerId, firstName, lastName, customerPhone);
				customer.setAddress(customerStreet, customerCity, customerState, customerZipCode);
				customerList.add(customer);
			}
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}

		return customerList;


		//DO NOT FORGET TO CLOSE YOUR CONNECTION

	}

	public static Customer findCustomerByPhone(String phoneNumber) throws SQLException, IOException {
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *
		 */
		connect_to_db();

		String query = "SELECT * FROM customer WHERE CustomerPhone = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, phoneNumber);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int customerId = rs.getInt("CustomerID");
				String customerName = rs.getString("CustomerName");
				String customerPhone = rs.getString("CustomerPhone");
				String customerStreet = rs.getString("CustomerStreet");
				String customerCity = rs.getString("CustomerCity");
				String customerState = rs.getString("CustomerState");
				String customerZipCode = rs.getString("CustomerZipCode");

				// Split the customer name into first name and last name
				String[] nameParts = customerName.split(" ", 2);
				String firstName = nameParts[0];
				String lastName = nameParts.length > 1 ? nameParts[1] : "";

				// Create a Customer object and return it
				Customer customer = new Customer(customerId, firstName, lastName, customerPhone);
				customer.setAddress(customerStreet, customerCity, customerState, customerZipCode);
				return customer;
			}
		} finally {
			// Close the connection

			if(conn!=null) {
				conn.close();
			}
		}

		return null; // Customer not found


	}


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
		/*
		 * Query the database for the available toppings and
		 * return an arrayList of all the available toppings.
		 * Don't forget to order the data coming from the database appropriately.
		 *
		 */


		connect_to_db();

		ArrayList<Topping> toppingList = new ArrayList<>();

		String query = "SELECT * FROM topping ORDER BY ToppingName";
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int toppingId = rs.getInt("ToppingID");
				String toppingName = rs.getString("ToppingName");
				double toppingCostToCustomer = rs.getDouble("ToppingCostToCus");
				double toppingCostToBusiness = rs.getDouble("ToppingCostToBusi");
				int currentInventory = rs.getInt("ToppingCurr_Inv");
				int minInventory = rs.getInt("ToppingMin_Inv");
				double amount_P = rs.getDouble("ToppingAmount_P");
				double amount_M = rs.getDouble("ToppingAmount_M");
				double amount_L = rs.getDouble("ToppingAmount_L");
				double amount_XL = rs.getDouble("ToppingAmount_XL");

				// Create a Topping object and add it to the list
				Topping topping = new Topping(toppingId, toppingName, amount_P, amount_M, amount_L, amount_XL, toppingCostToCustomer, toppingCostToBusiness,
						minInventory, currentInventory);
				toppingList.add(topping);
			}
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}

		return toppingList;


		//DO NOT FORGET TO CLOSE YOUR CONNECTION

	}

	public static Topping findToppingByName(String name) throws SQLException, IOException {
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *
		 */
		connect_to_db();

		//Topping topping = new Topping(null,null,null,null,null,null,null,null,null,null);
        Topping topping=null;
		String query = "SELECT * FROM topping WHERE BINARY ToppingName = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				int topID = rs.getInt("ToppingID");
				String topName = rs.getString("ToppingName");
				double perAMT = rs.getDouble("ToppingAmount_P");
				double medAMT = rs.getDouble("ToppingAmount_M");
				double lgAMT = rs.getDouble("ToppingAmount_L");
				double xlAMT = rs.getDouble("ToppingAmount_XL");
				double custPrice = rs.getDouble("ToppingCostToCus");
				double busPrice = rs.getDouble("ToppingCostToBusi");
				int minINVT = rs.getInt("ToppingMin_Inv");
				int curINVT = rs.getInt("ToppingCurr_Inv");

				// Create a Topping object
				topping = new Topping(topID, topName, perAMT, medAMT, lgAMT, xlAMT, custPrice, busPrice, minINVT, curINVT);
			}

		}
		finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}

		return topping;


	}


	public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 *
		 * */


		try {
			// Update the current inventory quantity of the topping
			String updateQuery = "UPDATE topping SET ToppingCurr_Inv = ToppingCurr_Inv + ? WHERE ToppingID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
				pstmt.setDouble(1, quantity);
				pstmt.setInt(2, t.getTopID());
				pstmt.executeUpdate();
			}

			// Commit the transaction
			
		} catch (SQLException e) {
			// Rollback the transaction if an error occurs
			;
			throw e;
		} finally {
			// Close the connection
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database fro the base customer price for that size and crust pizza.
		 *
		 */


		double baseCustPrice = 0.0;
		String query = "SELECT BasePriceToCust FROM base WHERE BaseSize = ? AND BaseCrustType = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, size);
			pstmt.setString(2, crust);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				baseCustPrice = rs.getDouble("BasePriceToCust");
			}
		} finally {
			if(conn!=null) {
				conn.close();
			}
		}

		return baseCustPrice;


		//DO NOT FORGET TO CLOSE YOUR CONNECTION

	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database fro the base business price for that size and crust pizza.
		 *
		 */


		double baseBusPrice = 0.0;
		String query = "SELECT BasePriceToBus FROM base WHERE BaseSize = ? AND BaseCrustType = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setString(1, size);
			pstmt.setString(2, crust);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				baseBusPrice = rs.getDouble("BasePriceToBus");
			}
		} finally {
			if(conn!=null) {
				conn.close();
			}
		}

		return baseBusPrice;

		//DO NOT FORGET TO CLOSE YOUR CONNECTION

	}

	public static void printInventory() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Queries the database and prints the current topping list with quantities.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */


		String query = "SELECT ToppingID, ToppingName, ToppingCurr_Inv FROM topping ORDER BY ToppingID";

		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			System.out.printf("%-10s%-20s%-15s%n", "ID", "Name", "CurINVT");
			while (rs.next()) {
				int toppingID = rs.getInt("ToppingID");
				String toppingName = rs.getString("ToppingName");
				int currentInventory = rs.getInt("ToppingCurr_Inv");
				System.out.printf("%-10d%-20s%-15d%n", toppingID, toppingName, currentInventory);
			}
		} finally {
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION


	}

	public static void printToppingPopReport() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */


		String query = "SELECT Topping, ToppingCount FROM ToppingPopularity ORDER BY ToppingCount DESC";

		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();


			System.out.printf("%-20s%-15s%n", "Topping", "ToppingCount");

			while (rs.next()) {
				String topping = rs.getString("Topping");
				int toppingCount = rs.getInt("ToppingCount");
				System.out.printf("%-20s%-15d%n", topping, toppingCount);
			}
		} finally {
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printProfitByPizzaReport() throws SQLException, IOException {
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */

		connect_to_db();

		String query = "SELECT Size, Crust, Profit, `Order Month` FROM ProfitByPizza ORDER BY Profit ASC";

		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			System.out.printf("%-10s%-15s%-10s%-15s%n", "Size", "Crust", "Profit", "Order Month");
			while (rs.next()) {
				String size = rs.getString("Size");
				String crust = rs.getString("Crust");
				double profit = rs.getDouble("Profit");
				String orderMonth = rs.getString("Order Month");
				System.out.printf("%-10s%-15s%-10.2f%-15s%n", size, crust, profit, orderMonth);
			}
		} finally {
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void printProfitByOrderType() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 *
		 * The result should be readable and sorted as indicated in the prompt.
		 *
		 */
		String query = "SELECT CustomerType, `Order Month`, TotalOrderPrice, TotalOrderCost, Profit FROM ProfitByOrderType ORDER BY `Order Month` ASC";

		try (PreparedStatement pstmt = conn.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			System.out.printf("%-15s%-15s%-20s%-20s%-15s%n", "Customer Type", "Order Month", "Total Order Price", "Total Order Cost", "Profit");
			while (rs.next()) {
				String customerType = rs.getString("CustomerType");
				String orderMonth = rs.getString("Order Month");
				double totalOrderPrice = rs.getDouble("TotalOrderPrice");
				double totalOrderCost = rs.getDouble("TotalOrderCost");
				double profit = rs.getDouble("Profit");
				System.out.printf("%-15s%-15s%-20.2f%-20.2f%-15.2f%n", customerType, orderMonth, totalOrderPrice, totalOrderCost, profit);
			}
		} finally {
			if(conn!=null) {
				conn.close();
			}
		}


		//DO NOT FORGET TO CLOSE YOUR CONNECTION	
	}


	public static String getCustomerName(int CustID) throws SQLException, IOException {
		/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with
		 * your database from Java.  It's used in the model solution for this project...so the code works!
		 *
		 * OF COURSE....this code would only work in your application if the table & field names match!
		 *
		 */

		connect_to_db();

		/*
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 *
		 */
		String cname1 = "";
		try {

			String query = "Select CustomerName From customer WHERE CustomerID=" + CustID + ";";
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(query);

			while (rset.next()) {
				cname1 = rset.getString(1);
			}

			/*
			 * an example of the same query using a prepared statement...
			 *
			 */

		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
		return cname1; // OR cname2
	}

	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object.
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0, 4));
	}

	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}

	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder) {
		if (getYear(dateOfOrder) > year)
			return true;
		else if (getYear(dateOfOrder) < year)
			return false;
		else {
			if (getMonth(dateOfOrder) > month)
				return true;
			else if (getMonth(dateOfOrder) < month)
				return false;
			else {
				if (getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}

	public static String orderFullInfo(int orderId) throws SQLException, IOException {
		connect_to_db();
		StringBuilder result = new StringBuilder();


		try {
			// Retrieve order information
			String orderQuery = "SELECT * FROM orders WHERE OrderID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(orderQuery)) {
				pstmt.setInt(1, orderId);
				ResultSet orderResultSet = pstmt.executeQuery();
				if (orderResultSet.next()) {
					int customerId = orderResultSet.getInt("OrderCustID");
					String orderType = orderResultSet.getString("OrderType");
					String orderState = orderResultSet.getString("OrderState");
					double custPrice = orderResultSet.getDouble("OrderCostToCustomer");
					double busPrice = orderResultSet.getDouble("OrderCostToBusiness");
					Timestamp orderTime = orderResultSet.getTimestamp("OrderTime");

					result.append("OrderID=").append(orderId).append(" | For customer: ").append(getCustomerName(customerId)).append(" | OrderType ").append(orderType)
							.append(", Placed on: ").append(orderTime).append(" | CustPrice= ").append(custPrice).append(", BusPrice= ").append(busPrice).append(" | ");

					// Append order specific information
					conn.close();
					connect_to_db();
					if (orderType.equalsIgnoreCase("DineIn")) {
						String dineInQuery = "SELECT DineInTableNumber FROM dinein WHERE DineInOrderID = ?";
						try (PreparedStatement pstmtDineIn = conn.prepareStatement(dineInQuery)) {
							pstmtDineIn.setInt(1, orderId);
							ResultSet dineInResultSet = pstmtDineIn.executeQuery();
							if (dineInResultSet.next()) {
								int tableNumber = dineInResultSet.getInt("DineInTableNumber");
								result.append("Customer was sat at table number ").append(tableNumber).append(" ");
							}
						}
					} else if (orderType.equalsIgnoreCase("Delivery")) {
						String deliveryQuery = "SELECT Address FROM delivery WHERE DeliveryOrderID = ?";
						try (PreparedStatement pstmtDelivery = conn.prepareStatement(deliveryQuery)) {
							pstmtDelivery.setInt(1, orderId);
							ResultSet deliveryResultSet = pstmtDelivery.executeQuery();
							if (deliveryResultSet.next()) {
								String address = deliveryResultSet.getString("Address");
								result.append("Delivery address: ").append(address).append(" ");
							}
						}
					}
					String orderDiscountQuery = "SELECT od.Order_Discount_DiscountID, d.DiscountName, d.DiscountPercent, d.DiscountAmountInDollar " +
							"FROM order_discount od JOIN discount d ON od.Order_Discount_DiscountID = d.DiscountID " +
							"WHERE od.Order_Discount_OrderID = ?";

					try (PreparedStatement pstmtOrderDiscount = conn.prepareStatement(orderDiscountQuery)) {
						pstmtOrderDiscount.setInt(1, orderId);
						ResultSet orderDiscountResultSet = pstmtOrderDiscount.executeQuery();
						while (orderDiscountResultSet.next()) {
							int discountId = orderDiscountResultSet.getInt("Order_Discount_DiscountID");
							String discountName = orderDiscountResultSet.getString("DiscountName");
							int discountPercent = orderDiscountResultSet.getInt("DiscountPercent");
							double discountAmount = orderDiscountResultSet.getDouble("DiscountAmountInDollar");
							boolean flag = true;
							if (discountPercent == 0) {
								flag = false;
							}
							Discount orderDiscount = new Discount(discountId, discountName, discountAmount, flag);

							result.append("ORDER DISCOUNT: ").append(orderDiscount.toString()).append("\n");
						}


					}
				}
				// Append pizza information
				String pizzaQuery = "SELECT p.PizzaID, p.PizzaSize, p.PizzaCrustType, p.PizzaPriceToCust, p.PizzaCostToBusi, p.PizzaState, pd.Pizza_Discount_Discount_ID " +
						"FROM pizza p LEFT JOIN pizza_discount pd ON p.PizzaID = pd.Pizza_Discount_Pizza_ID " +
						"WHERE PizzaOrderID = ?";
				try (PreparedStatement pstmtPizza = conn.prepareStatement(pizzaQuery)) {
					pstmtPizza.setInt(1, orderId);
					ResultSet pizzaResultSet = pstmtPizza.executeQuery();
					while (pizzaResultSet.next()) {
						int pizzaId = pizzaResultSet.getInt("PizzaID");
						String pizzaSize = pizzaResultSet.getString("PizzaSize");
						String pizzaCrustType = pizzaResultSet.getString("PizzaCrustType");
						double pizzaPriceToCust = pizzaResultSet.getDouble("PizzaPriceToCust");
						double pizzaCostToBusi = pizzaResultSet.getDouble("PizzaCostToBusi");
						String pizzaState = pizzaResultSet.getString("PizzaState");
						int discountId = pizzaResultSet.getInt("Pizza_Discount_Discount_ID");

						StringBuilder pizzaInfo = new StringBuilder();
						pizzaInfo.append("PizzaID=").append(pizzaId);
						pizzaInfo.append(" | CrustType=").append(pizzaCrustType).append(", Size ").append(pizzaSize);
						pizzaInfo.append(" | For order ").append(orderId);
						pizzaInfo.append(" | Pizza Status: ").append(pizzaState);
						pizzaInfo.append(" | Customer Price ").append(pizzaPriceToCust);
						pizzaInfo.append(" | Business Price= ").append(pizzaCostToBusi);

						// Get pizza discounts from discount table
						String pizzaDiscountQuery = "SELECT * FROM discount WHERE DiscountID = ?";
						try (PreparedStatement pstmtPizzaDiscount = conn.prepareStatement(pizzaDiscountQuery)) {
							pstmtPizzaDiscount.setInt(1, discountId);
							ResultSet pizzaDiscountResultSet = pstmtPizzaDiscount.executeQuery();
							if (pizzaDiscountResultSet.next()) {
								String discountName = pizzaDiscountResultSet.getString("DiscountName");
								int discountPercent = pizzaDiscountResultSet.getInt("DiscountPercent");
								double discountAmount = pizzaDiscountResultSet.getDouble("DiscountAmountInDollar");

								pizzaInfo.append(" PIZZA DISCOUNTS: ").append(discountName).append(" ").append(pizzaSize);
							}
						}

						return result.append("\n").append(pizzaInfo.toString()).append("\n").toString();
					}
				}


			}
		} finally {
			// Close the connection
			if (conn != null) {
				conn.close();
			}
		}

    return result.toString();
	}




}