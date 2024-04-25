package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the methods for each of the menu options.
 * 
 * This file should not need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove methods as you see necessary. But you MUST have all of the menu methods (including exit!)
 * 
 * Simply removing menu methods because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 */

public class Menu {

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws SQLException, IOException {

		System.out.println(DBNinja.findToppingByName("Green Pepper"));
		System.out.println(DBNinja.findToppingByName("green pepper"));

		System.out.println("Welcome to Pizzas-R-Us!");

		
		int menu_option = 0;


		// present a menu of options and take their selection
		
		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{

		/*
		 * EnterOrder should do the following:
		 * 
		 * Ask if the order is delivery, pickup, or dinein
		 *   if dine in....ask for table number
		 *   if pickup...
		 *   if delivery...
		 * 
		 * Then, build the pizza(s) for the order (there's a method for this)
		 *  until there are no more pizzas for the order
		 *  add the pizzas to the order
		 *
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * return to menu
		 * 
		 * make sure you use the prompts below in the correct order!
		 */

		 // User Input Prompts...
		ArrayList<Pizza> pizzas=new ArrayList<>();;
		Order order=null;
		System.out.println("Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the number of your choice:");
		String o_type=reader.readLine();
		String customerAddress="";
		String cust_id="";
		int customer_id=-1;
		String onlyDeliveryAddress="";
		if(Integer.parseInt(o_type)!=1) {
			System.out.println("Is this order for an existing customer? Answer y/n: ");
			String new_cust=reader.readLine();
			if(new_cust.equals("n") && Integer.parseInt(o_type)==3)
			{
				EnterCustomer1();
				customer_id=DBNinja.getLatestCustomerId();
			}
			else if(new_cust.equals("y") && Integer.parseInt(o_type)==3)
			{
				//EnterCustomer2();
				System.out.println("Here's a list of the current customers: ");
				viewCustomers();
				System.out.println("Which customer is this order for? Enter ID Number:");
				cust_id=reader.readLine();
				customer_id=Integer.parseInt(cust_id);
				onlyDeliveryAddress=EnterCustomer2(customer_id);

			}
			else if(new_cust.equals("y")&& Integer.parseInt(o_type)==2)
			{
				System.out.println("Here's a list of the current customers: ");
				viewCustomers();
				System.out.println("Which customer is this order for? Enter ID Number:");
				cust_id=reader.readLine();
				customer_id=Integer.parseInt(cust_id);


			}
			else if(new_cust.equals("n")&& Integer.parseInt(o_type)==2)
			{

				EnterCustomer();
				customer_id=DBNinja.getLatestCustomerId();


			}
			else
			{
				System.out.println("ERROR: I don't understand your input for: Is this order an existing customer?");
				return;
			}
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = now.format(formatter);
			ArrayList<Customer> customers=DBNinja.getCustomerList();
			for (Customer customer1 : customers) {
				if (customer1.getCustID() == customer_id) {
					customerAddress = customer1.getAddress();
				}
			}
			if(Integer.parseInt(o_type)==2) {
				order = new PickupOrder(0, customer_id, formattedDateTime, 0, 0, 0, 0);
			}
			if(Integer.parseInt(o_type)==3 && new_cust.equals("n"))  {
				order = new DeliveryOrder(0, customer_id, formattedDateTime, 0, 0, 0, customerAddress);
                ((DeliveryOrder) order).setAddress(customerAddress);
			}
			else if(Integer.parseInt(o_type)==3 && new_cust.equals("y"))
			{
				order = new DeliveryOrder(0, customer_id, formattedDateTime, 0, 0, 0, onlyDeliveryAddress);

			}

		}


		if(Integer.parseInt(o_type)==1) {
			System.out.println("What is the table number for this order?");
			String dineIn= reader.readLine();
			int tableNumber=Integer.parseInt(dineIn);
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = now.format(formatter);
			order = new DineinOrder(0, -1, formattedDateTime, 0, 0, 0, tableNumber);
		}
		System.out.println("Let's build a pizza!");
		pizzas.add(buildPizza(-1));
		while(true) {
			System.out.println("Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
			String pizza_stop = reader.readLine();
			if (pizza_stop.equals( "-1")) {
				break;
			}
			pizzas.add(buildPizza(-1));
		}
		String discountName="";
		Discount discount;
		ArrayList<String> discountNames=new ArrayList<>();
		double o_cust_price=0;
		double o_bus_price=0;
		for(Pizza pizza1:pizzas)
		{
			o_cust_price+=pizza1.getCustPrice();
			o_bus_price+=pizza1.getBusPrice();
		}
        assert order != null;
        order.setBusPrice(o_bus_price);
		order.setCustPrice(o_cust_price);
		DBNinja.addOrder(order);
		order.setOrderID(DBNinja.getLatestOrderId());
		while (true) {
			System.out.println("Do you want to add discounts to this Order? Enter y/n?");
			String discountChoice = reader.readLine();
			if (discountChoice.equalsIgnoreCase("n")) {
				break;
			}
			if (discountChoice.equalsIgnoreCase("y")) {
				ArrayList<Discount> di=DBNinja.getDiscountList();
				for(Discount dis:di)
				{
					System.out.println(dis);
				}
				System.out.println("Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				String discountIdStr = reader.readLine();
				int discountId = Integer.parseInt(discountIdStr);
				if (discountId == -1) {
					break;
				}

				ArrayList<Discount> discounts=DBNinja.getDiscountList();
				for (Discount discount1 : discounts) {
					if (discount1.getDiscountID() == discountId) {
						discountName = discount1.getDiscountName();
					}
				}
				discount = DBNinja.findDiscountByName(discountName);
				if (discount != null) {

					order.addDiscount(discount);
					discountNames.add(discountName);
					DBNinja.useOrderDiscount(order,discount);
				} else {
					System.out.println("Invalid discount ID. Please try again.");
				}
			}
		}


		for(Pizza pizza1:pizzas)
		{
			pizza1.setOrderID(order.getOrderID());
			DBNinja.addPizza(pizza1);
			pizza1.setPizzaID(DBNinja.getLatestPizzaId());
			//pizza1.setOrderID(order.getOrderID());
			//DBNinja.addPizza(pizza1);
			ArrayList<Topping> toppings=pizza1.getToppings();
			for(Topping t:toppings)
			{
				DBNinja.useTopping(pizza1, t,pizza1.getIsDoubleArray()[t.getTopID()] );
			}
			ArrayList<Discount> discounts=pizza1.getDiscounts();
			for(Discount d: discounts)
			{
				DBNinja.usePizzaDiscount(pizza1,d);
			}
		}




		System.out.println("Finished adding order...Returning to menu...");
	}
	
	
	public static void viewCustomers() throws SQLException, IOException 
	{
		/*
		 * Simply print out all of the customers from the database. 
		 */

		ArrayList<Customer> cus=DBNinja.getCustomerList();
		for(Customer customers : cus)
		{
			System.out.println(customers);
		}
		

		
		
		
	}
	

	// Enter a new customer in the database
	public static void EnterCustomer1() throws SQLException, IOException
	{
		/*
		 * Ask for the name of the customer:
		 *   First Name <space> Last Name
		 * 
		 * Ask for the  phone number.
		 *   (##########) (No dash/space)
		 * 
		 * Once you get the name and phone number, add it to the DB
		 */
		
		// User Input Prompts...
		 System.out.println("What is this customer's name (first <space> last");
		String name = reader.readLine();
		String[] nameParts = name.split(" ");
		String firstName = nameParts[0];
		String lastName = nameParts.length > 1 ? nameParts[1] : "";
		 System.out.println("What is this customer's phone number (##########) (No dash/space)");
		String phone = reader.readLine();
		 System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
		String houseNumber = reader.readLine();
		 System.out.println("What is the Street for this order? (e.g., Smile Street)");
		String street = reader.readLine();
		 street =street + " " + houseNumber;
		 System.out.println("What is the City for this order? (e.g., Greenville)");
		String city = reader.readLine();
		 System.out.println("What is the State for this order? (e.g., SC)");
		String state = reader.readLine();
		System.out.println("What is the Zip Code for this order? (e.g., 20605)");
		String zipCode = reader.readLine();
		Customer newCustomer = new Customer(-1, firstName, lastName, phone);
		newCustomer.setAddress( street, city, state, zipCode);

		// Add the customer to the database
		DBNinja.addCustomer(newCustomer);




	}
	public static String EnterCustomer2(int cust_id) throws SQLException, IOException
	{
		/*
		 * Ask for the name of the customer:
		 *   First Name <space> Last Name
		 *
		 * Ask for the  phone number.
		 *   (##########) (No dash/space)
		 *
		 * Once you get the name and phone number, add it to the DB
		 */

		// User Input Prompts...
		String name=DBNinja.getCustomerName(cust_id);
		String phone="None";
		String[] nameParts = name.split(" ");
		String firstName = nameParts[0];
		String lastName = nameParts.length > 1 ? nameParts[1] : "";
		System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
		String houseNumber = reader.readLine();
		System.out.println("What is the Street for this order? (e.g., Smile Street)");
		String street = reader.readLine();
		street =street + " " + houseNumber;
		System.out.println("What is the City for this order? (e.g., Greenville)");
		String city = reader.readLine();
		System.out.println("What is the State for this order? (e.g., SC)");
		String state = reader.readLine();
		System.out.println("What is the Zip Code for this order? (e.g., 20605)");
		String zipCode = reader.readLine();
		String Address = street + "/n" + city + "/n" + state + "/n" + zipCode;

	    return Address;




	}
	public static void EnterCustomer() throws SQLException, IOException
	{
		/*
		 * Ask for the name of the customer:
		 *   First Name <space> Last Name
		 *
		 * Ask for the  phone number.
		 *   (##########) (No dash/space)
		 *
		 * Once you get the name and phone number, add it to the DB
		 */

		// User Input Prompts...
		System.out.println("What is this customer's name (first <space> last");
		String name = reader.readLine();
		String[] nameParts = name.split(" ");
		String firstName = nameParts[0];
		String lastName = nameParts.length > 1 ? nameParts[1] : "";
		System.out.println("What is this customer's phone number (##########) (No dash/space)");
		String phone = reader.readLine();

		Customer newCustomer = new Customer(-1, firstName, lastName, phone);

		newCustomer.setAddress( "None", "None", "None", "None");
		// Add the customer to the database
		DBNinja.addCustomer(newCustomer);




	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException {
		/*
		 * This method allows the user to select between three different views of the Order history:
		 * The program must display:
		 * a.	all open orders
		 * b.	all completed orders
		 * c.	all the orders (open and completed) since a specific date (inclusive)
		 *
		 * After displaying the list of orders (in a condensed format) must allow the user to select a specific order for viewing its details.
		 * The details include the full order type information, the pizza information (including pizza discounts), and the order discounts.
		 *
		 */


		// User Input Prompts...
		ArrayList<Order> orders = null;
		System.out.println("Would you like to:\n(a) display all orders [open or closed]\n(b) display all open orders\n(c) display all completed [closed] orders\n(d) display orders since a specific date");
		String option = reader.readLine();
		switch (option) {
			case "a":
				orders = DBNinja.getAllOrders();
				if (orders.isEmpty()) {
					System.out.println("No orders to display, returning to menu.");
				} else {
					for (Order ord : orders) {
						System.out.println(ord.toSimplePrint());
					}

						System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
						String order_id = reader.readLine();

						int order_id1 = Integer.parseInt(order_id);
						if (order_id1 == -1) {
							break;
						}

						System.out.print(DBNinja.orderFullInfo(order_id1));

				}

				break;
			case "b":
				orders = DBNinja.getOrders(false);
				if (orders.isEmpty()) {
					System.out.println("No orders to display, returning to menu.");
				} else {
					for (Order ord : orders) {
						System.out.println(ord.toSimplePrint());
					}

						System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
						String order_id = reader.readLine();
						int order_id1 = Integer.parseInt(order_id);
						if (order_id1 == -1) {
							break;
						}

						System.out.println(DBNinja.orderFullInfo(order_id1));

				}
				break;
			case "c":
				orders = DBNinja.getOrders(true);
				if (orders.isEmpty()) {
					System.out.println("No orders to display, returning to menu.");
				} else {
					for (Order ord : orders) {
						System.out.println(ord.toSimplePrint());
					}

						System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
						String order_id = reader.readLine();
						int order_id1 = Integer.parseInt(order_id);
						if (order_id1 == -1) {
							break;
						}

						System.out.println(DBNinja.orderFullInfo(order_id1));

				}
				break;
			case "d":
				System.out.println("What is the date you want to restrict by? (FORMAT= YYYY-MM-DD)");

				String dateString = reader.readLine();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate date = LocalDate.parse(dateString, formatter);
				if(!DBNinja.checkDate(date.getYear(),date.getMonthValue(),date.getDayOfMonth(),dateString))
				{
					break;
				}
				orders = DBNinja.getOrdersByDate(dateString);
				if (orders.isEmpty()) {
					System.out.println("No orders to display, returning to menu.");
				} else {
					for (Order ord : orders) {
						System.out.println(ord.toSimplePrint());
					}

						System.out.println("Which order would you like to see in detail? Enter the number (-1 to exit): ");
						String order_id = reader.readLine();
						int order_id1 = Integer.parseInt(order_id);
						if (order_id1 == -1) {
							break;
						}

						System.out.println(DBNinja.orderFullInfo(order_id1));

				}

				break;
			default:
				System.out.println("I don't understand that input, returning to menu");
				break;


			//System.out.println("Incorrect entry, returning to menu.");


		}
	}

	
	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException 
	{
		/*
		 * All orders that are created through java (part 3, not the orders from part 2) should start as incomplete
		 * 
		 * When this method is called, you should print all of the "opoen" orders marked
		 * and allow the user to choose which of the incomplete orders they wish to mark as complete
		 * 
		 */
		
		
		
		// User Input Prompts...
		ArrayList<Order> orders=null;
		orders=DBNinja.getOrders(true);
		if(orders.isEmpty()) {
			System.out.println("There are no open orders currently... returning to menu...");
		}
		else
		{
			for (Order ord : orders) {
				System.out.println(ord.toSimplePrint());
			}
		}
		System.out.println("Which order would you like mark as complete? Enter the OrderID: ");
			String orderId= reader.readLine();
			int orderId1=Integer.parseInt(orderId);
			boolean flag=false;
		for (Order ord : orders) {
		  if(orderId1==ord.getOrderID())
		  {
			  flag=true;
			  DBNinja.completeOrder(ord);
		  }
		}
		if(!flag) {
			System.out.println("Incorrect entry, not an option");
		}
		
		

	}

	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		/*
		 * Print the inventory. Display the topping ID, name, and current inventory
		*/
		ArrayList<Topping> t=null;
		t=DBNinja.getToppingList();
		System.out.printf("%-10s%-15s%-10s%n","ID",  "Name", "currINVT");
		for(Topping top:t)
		{
			System.out.printf("%-10s%-15s%-10d%n", top.getTopID(),top.getTopName(),top.getCurINVT());
		}
		
		
		
	}


	public static void AddInventory() throws SQLException, IOException 
	{
		/*
		 * This should print the current inventory and then ask the user which topping (by ID) they want to add more to and how much to add
		 */
		
		
		// User Input Prompts...
		ViewInventoryLevels();
		Topping topping=null;
		System.out.println("Which topping do you want to add inventory to? Enter the number: ");
		String toppingId= reader.readLine();
		int toppingId1 = Integer.parseInt(toppingId);
		boolean flag=true;
			ArrayList<Topping> toppings=DBNinja.getToppingList();
			for (Topping topping1 : toppings) {

				if (topping1.getTopID() == toppingId1) {
					flag=false;
					topping = topping1;
				}
			}



			if(flag) {
				System.out.println("Incorrect entry, not an option");

			}
			else {
				System.out.println("How many units would u want to add?");
				String units= reader.readLine();
				double units1 = Double.parseDouble(units);
				DBNinja.addToInventory(topping,units1);
			}
	
		
		
		
	}

	// A method that builds a pizza. Used in our add new order method
	public static Pizza buildPizza(int orderID) throws SQLException, IOException 
	{
		
		/*
		 * This is a helper method for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */

		 Pizza ret = null;
		
		// User Input Prompts...
		System.out.println("What size is the pizza?");
		System.out.println("1."+DBNinja.size_s);
		System.out.println("2."+DBNinja.size_m);
		System.out.println("3."+DBNinja.size_l);
		System.out.println("4."+DBNinja.size_xl);
		System.out.println("Enter the corresponding number: ");
		int sizeOption = Integer.parseInt(reader.readLine());
		String size;
		if (sizeOption == 1) {
			size = DBNinja.size_s;
		} else if (sizeOption == 2) {
			size = DBNinja.size_m;
		} else if (sizeOption == 3) {
			size = DBNinja.size_l;
		} else if (sizeOption == 4) {
			size = DBNinja.size_xl;
		} else {
			System.out.println("Invalid size option. Defaulting to medium size.");
			size = DBNinja.size_m;
		}


		System.out.println("What crust for this pizza?");
		System.out.println("1."+DBNinja.crust_thin);
		System.out.println("2."+DBNinja.crust_orig);
		System.out.println("3."+DBNinja.crust_pan);
		System.out.println("4."+DBNinja.crust_gf);
		System.out.println("Enter the corresponding number: ");
		String crustType;
		int crustOption = Integer.parseInt(reader.readLine());
		if (crustOption == 1) {
			crustType = DBNinja.crust_thin;
		} else if (crustOption == 2) {
			crustType = DBNinja.crust_orig;
		} else if (crustOption == 3) {
			crustType = DBNinja.crust_pan;
		} else if (crustOption == 4) {
			crustType = DBNinja.crust_gf;
		} else {
			System.out.println("Invalid crust option. Defaulting to original crust.");
			crustType = DBNinja.crust_orig;
		}
		double custPrice=DBNinja.getBaseCustPrice(size,crustType);
		double busPrice=DBNinja.getBaseBusPrice(size,crustType);
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedDateTime = now.format(formatter);
		ret = new Pizza(0, size, crustType, orderID, "Complete", formattedDateTime, custPrice,busPrice);
		ViewInventoryLevels();
		boolean flag=true;
		String toppingName="";
		Topping topping;
		while(flag) {

			System.out.println("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings: ");
			String toppingId = reader.readLine();
			int toppingId1 = Integer.parseInt(toppingId);
			if (toppingId1 == -1) {
				break;
			} else {
				ArrayList<Topping> toppings=DBNinja.getToppingList();
				for (Topping topping1 : toppings) {
					if (topping1.getTopID() == toppingId1) {
						toppingName = topping1.getTopName();
					}
				}
				 topping = DBNinja.findToppingByName(toppingName);
				if (topping != null) {
					if(topping.getCurINVT()-1<0) {
						System.out.println("We don't have enough of that topping to add it...");
					}
				} else {
					System.out.println("Invalid topping ID. Please try again.");
				}
			}
			System.out.println("Available Toppings:");

			ViewInventoryLevels();
			System.out.println("Do you want to add extra topping? Enter y/n");
			String toppingChoice = reader.readLine();
			boolean toppingChoice1=false;
			if (toppingChoice.equals("n")) {
				ret.addToppings(topping,false);
			}
			if (toppingChoice.equals("y")) {

				if(topping.getCurINVT()-2<0) {
					System.out.println("We don't have enough of that topping to add it...");
				}
				else
				{
					ret.addToppings(topping,true);
					toppingChoice1=true;
				}
			}
			ret.modifyDoubledArray(topping.getTopID(),toppingChoice1);
			System.out.println("Available Toppings:");
			ViewInventoryLevels();
		}


		Discount discount;
		String discountName="";
		int poi=0;
		while (true) {
			if(poi==0) {
				System.out.println("Do you want to add discounts to this Pizza? Enter y/n?");
			}
			else {
				System.out.println("Do you want to add more discounts to this Pizza? Enter y/n?");
			}
			String discountChoice = reader.readLine();
			if (discountChoice.equalsIgnoreCase("n")) {
				break;
			}
			if (discountChoice.equalsIgnoreCase("y")) {
				ArrayList<Discount> di=DBNinja.getDiscountList();
				for(Discount dis:di)
				{
					System.out.println(dis);
				}
				System.out.println("Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");

				String discountIdStr = reader.readLine();
				int discountId = Integer.parseInt(discountIdStr);
				if (discountId == -1) {
					break;
				}
				ArrayList<Discount> discounts=DBNinja.getDiscountList();
				for (Discount discount1 : discounts) {
					if (discount1.getDiscountID() == discountId) {
						discountName = discount1.getDiscountName();

					}
				}
				discount = DBNinja.findDiscountByName(discountName);

				if (discount != null) {
					if(discount.isPercent()==true)
					{
						discount.setAmount(discount.getAmount()/100);
					}
					ret.addDiscounts(discount);
				} else {
					System.out.println("Invalid discount ID. Please try again.");
				}
			}
			poi++;
		}

		return ret;
	}
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		/*
		 * This method asks the use which report they want to see and calls the DBNinja method to print the appropriate report.
		 * 
		 */

		// User Input Prompts...
		System.out.println("Which report do you wish to print? Enter\n(a) ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");
		String reportChoice = reader.readLine();

		switch (reportChoice.toLowerCase()) {
			case "a":
				System.out.println("Topping Popularity Report:");
				DBNinja.printToppingPopReport();
				break;
			case "b":
				System.out.println("Profit By Pizza Report:");
				DBNinja.printProfitByPizzaReport();
				break;
			case "c":
				System.out.println("Profit By Order Type Report:");
				DBNinja.printProfitByOrderType();
				break;
			default:
				System.out.println("I don't understand that input... returning to menu...");
				break;
		}



	}

	//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
	// DO NOT EDIT ANYTHING BELOW HERE, THIS IS NEEDED TESTING.
	// IF YOU EDIT SOMETHING BELOW, IT BREAKS THE AUTOGRADER WHICH MEANS YOUR GRADE WILL BE A 0 (zero)!!

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	/*
	 * autograder controls....do not modify!
	 */

	public final static String autograder_seed = "6f1b7ea9aac470402d48f7916ea6a010";

	
	private static void autograder_compilation_check() {

		try {
			Order o = null;
			Pizza p = null;
			Topping t = null;
			Discount d = null;
			Customer c = null;
			ArrayList<Order> alo = null;
			ArrayList<Discount> ald = null;
			ArrayList<Customer> alc = null;
			ArrayList<Topping> alt = null;
			double v = 0.0;
			String s = "";

			DBNinja.addOrder(o);
			DBNinja.addPizza(p);
			DBNinja.useTopping(p, t, false);
			DBNinja.usePizzaDiscount(p, d);
			DBNinja.useOrderDiscount(o, d);
			DBNinja.addCustomer(c);
			DBNinja.completeOrder(o);
			alo = DBNinja.getOrders(false);
			o = DBNinja.getLastOrder();
			alo = DBNinja.getOrdersByDate("01/01/1999");
			ald = DBNinja.getDiscountList();
			d = DBNinja.findDiscountByName("Discount");
			alc = DBNinja.getCustomerList();
			c = DBNinja.findCustomerByPhone("0000000000");
			alt = DBNinja.getToppingList();
			t = DBNinja.findToppingByName("Topping");
			DBNinja.addToInventory(t, 1000.0);
			v = DBNinja.getBaseCustPrice("size", "crust");
			v = DBNinja.getBaseBusPrice("size", "crust");
			DBNinja.printInventory();
			DBNinja.printToppingPopReport();
			DBNinja.printProfitByPizzaReport();
			DBNinja.printProfitByOrderType();
			s = DBNinja.getCustomerName(0);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}


}


