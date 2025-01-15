package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SimpleServer extends AbstractServer {

	private static Session session;
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	private static SessionFactory getSessionFactory() throws HibernateException {
		var config = new Configuration();
		Scanner userInput = new Scanner(System.in);
		System.out.print("Enter the database password: ");
		String password = userInput.nextLine();
		config.setProperty("hibernate.connection.password", password);

		config.addAnnotatedClass(Dish.class);


		var serviceRegistry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		return config.buildSessionFactory(serviceRegistry);
	}
	private static void generateSampleMeals() {
		// Define dishes
		Dish burger = new Dish();
		burger.setName("Classic Burger");
		burger.setPrice(50);
		burger.setCouldBeDelivered(true);
		burger.setIngredientsString("Beef, Cheese, Lettuce, Tomato, Bread");
		burger.setPreferencesLevelOfSpiciness("Mild");
		burger.setPreferencesKindOfBread("Whole Wheat");


		Dish pizza = new Dish();
		pizza.setName("Cheese Pizza");
		pizza.setPrice(40);
		pizza.setCouldBeDelivered(true);
		pizza.setIngredientsString("Cheese, Tomato");
		pizza.setPreferencesLevelOfSpiciness("Medium");
		pizza.setPreferencesKindOfBread("Thin Crust");

		Dish salad = new Dish();
		salad.setName("Fresh Salad");
		salad.setPrice(30);
		salad.setCouldBeDelivered(false);
		salad.setIngredientsString("Lettuce, Tomato");
		salad.setPreferencesLevelOfSpiciness("None");
		salad.setPreferencesKindOfBread("None");

		Dish sandwich = new Dish();
		sandwich.setName("Chicken Sandwich");
		sandwich.setPrice(35);
		sandwich.setCouldBeDelivered(true);
		sandwich.setIngredientsString("Chicken, Lettuce, Bread");
		sandwich.setPreferencesLevelOfSpiciness("Spicy");
		sandwich.setPreferencesKindOfBread("Ciabatta");


		// Save to database
		session.save(burger);
		session.save(pizza);
		session.save(salad);
		session.save(sandwich);


		session.flush();
	}

	private static void printAllDishes() {
		// Query and print all dishes
		List<Dish> dishes = session.createQuery("from Dish", Dish.class).getResultList();

		// Print the details of all dishes
		System.out.println("Dishes retrieved from the database:");
		for (Dish dish : dishes) {
			System.out.println("Dish Name: " + dish.getName() +
					", Price: " + dish.getPrice() +
					", Can be delivered: " + dish.isCouldBeDelivered() +
					", Ingredients: " + dish.getIngredientsString() +
					", Spiciness Level: " + dish.getPreferencesLevelOfSpiciness() +
					", Bread Type: " + dish.getPreferencesKindOfBread());
		}
	}
	public SimpleServer(int port) throws HibernateException {
		super(port);
		try {
			SessionFactory sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();


			generateSampleMeals();
			printAllDishes();


			session.getTransaction().commit(); // Save everything
		} catch (Exception exception) {
			if (session != null) {
				session.getTransaction().rollback();
			}
			System.err.println("An error occurred, changes have been rolled back.");
			exception.printStackTrace();
		}
	}

	@Override
	protected synchronized void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		System.out.println(msgString);
		if(msgString.startsWith("add client")){
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
			try {
				client.sendToClient("client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if (msgString.startsWith("remove client")) {
			if (!SubscribersList.isEmpty())	{
				for (SubscribedClient subscribedClient : SubscribersList) {
					if (subscribedClient.getClient().equals(client)) {
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}
		else if (msgString.startsWith("send menu")) {
			System.out.println("hello load menu");
			List<Dish> menu = getDishDetails();
			try {
				client.sendToClient(menu);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (msgString.startsWith("GetDishInfo")) {
			String dishName = msgString.substring("GetDishInfo:".length());
			var dish = getDish(dishName);
			try {
				client.sendToClient(dish);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (msg instanceof Dish) {
			try {
				session.beginTransaction();

				var dish = (Dish) msg;
				System.out.println(dish.getPrice());
				var dish2 = session.byNaturalId(Dish.class).using("name", dish.getName()).load();

				dish2.setPrice(dish.getPrice());
				System.out.println("image ");
				session.getTransaction().commit(); // Save everything


				client.sendToClient("Meal has been changed");

			} catch (Exception exception) {
				if (session != null) {
					session.getTransaction().rollback();
				}
				System.err.println("An error occurred, changes have been rolled back.");
				exception.printStackTrace();
			}

		}
		else if(msgString.startsWith("Meal name is")) {
			handlePriceChangeRequest(msgString,client);
		}
	}
	public void handlePriceChangeRequest(String message, ConnectionToClient client) {
		// Parse the message
		try {
			// Extract meal name and new price
			String[] parts = message.split("new price is:");
			if (parts.length != 2) {
				System.err.println("Invalid message format: " + message);
				return;
			}

			String mealName = parts[0].split("Meal name is:")[1].trim();
			int newPrice = Integer.parseInt(parts[1].trim());

			// Validate the price
			if (newPrice <= 0) {
				List<Dish> Menu = getDishDetails();
				client.sendToClient(Menu);
				return;
			}

			// Update the database using Hibernate
			session.beginTransaction();
			Dish dish = session.createQuery("FROM Dish d WHERE d.name = :name", Dish.class)
					.setParameter("name", mealName)
					.uniqueResult();

			if (dish != null) {
				dish.setPrice(newPrice); // Update the price
				session.saveOrUpdate(dish); // Save the changes
				session.getTransaction().commit();
				client.sendToClient("Success: The price for " + mealName + " was updated to " + newPrice + ".");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			session.getTransaction().rollback();
			e.printStackTrace();
		}
	}

	private static CompactMenu getDishNames() {
		session.beginTransaction();
		System.out.println("Ronny");
		List<String> dishNames = session.createQuery("SELECT d.name FROM Dish d", String.class).getResultList();
		session.getTransaction().commit();
		System.out.println("Omar");
		return new CompactMenu(dishNames);
	}

	private static List<Dish> getDishDetails() {
		session.beginTransaction();
		System.out.println("hello get dishes details1");
		// Query to get dishes with their names, prices, ingredients, and preferences
		List<Dish> dishes = session.createQuery("FROM Dish d", Dish.class).getResultList();
		System.out.println("hello get dishes details2");
		session.getTransaction().commit();
		System.out.println("hello get dishes details3");
		return dishes;
	}
	private static Dish getDish(String dishName) {
		session.beginTransaction();
		var dish = session.byNaturalId(Dish.class).using("name", dishName).load();
		assert dish != null;
		session.getTransaction().commit();
		return dish;
	}

	public void sendToAllClients(String message) {
		try {
			for (SubscribedClient subscribedClient : SubscribersList) {
				subscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}

