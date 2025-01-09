package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


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
		config.addAnnotatedClass(Dish.class);
		config.addAnnotatedClass(Ingredient.class);
		config.addAnnotatedClass(PersonalPreference.class);

		var serviceRegistry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		return config.buildSessionFactory(serviceRegistry);
	}

	public SimpleServer(int port) throws HibernateException {
		super(port);
		try {
			SessionFactory sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
			session.beginTransaction();

			// Generate sample meals
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
	private static void generateSampleMeals() {
		Ingredient cheese = new Ingredient();
		cheese.setName("Cheese");

		Ingredient tomato = new Ingredient();
		tomato.setName("Tomato");

		Ingredient beef = new Ingredient();
		beef.setName("Beef");

		Ingredient lettuce = new Ingredient();
		lettuce.setName("Lettuce");

		Ingredient bread = new Ingredient();
		bread.setName("Bread");

		Ingredient chicken = new Ingredient();
		chicken.setName("Chicken");

		List<Ingredient> burgerIngredients = Arrays.asList(beef, cheese, lettuce, tomato, bread);
		Dish burger = new Dish();
		burger.setName("Classic Burger");
		burger.setPrice(50);
		burger.setCouldBeDelivered(true);
		burger.setIngredients(burgerIngredients);
		//burger.setPreferences(new PersonalPreference());


		List<Ingredient> pizzaIngredients = Arrays.asList(cheese, tomato);
		Dish pizza = new Dish();
		pizza.setName("Cheese Pizza");
		pizza.setPrice(40);
		pizza.setCouldBeDelivered(true);
		pizza.setIngredients(pizzaIngredients);

		List<Ingredient> saladIngredients = Arrays.asList(lettuce, tomato);
		Dish salad = new Dish();
		salad.setName("Fresh Salad");
		salad.setPrice(30);
		salad.setCouldBeDelivered(false);
		salad.setIngredients(saladIngredients);

		List<Ingredient> sandwichIngredients = Arrays.asList(chicken, lettuce, bread);
		Dish sandwich = new Dish();
		sandwich.setName("Chicken Sandwich");
		sandwich.setPrice(35);
		sandwich.setCouldBeDelivered(true);
		sandwich.setIngredients(sandwichIngredients);

		List<Ingredient> steakIngredients = Arrays.asList(beef);
		Dish steak = new Dish();
		steak.setName("Grilled Steak");
		steak.setPrice(80);
		steak.setCouldBeDelivered(false);
		steak.setIngredients(steakIngredients);

		// Save to database
		session.save(cheese);
		session.save(tomato);
		session.save(beef);
		session.save(lettuce);
		session.save(bread);
		session.save(chicken);

		session.save(burger);
		session.save(pizza);
		session.save(salad);
		session.save(sandwich);
		session.save(steak);

		session.flush();
	}
	private static void printAllDishes() {
		// Query and print all dishes
		List<Dish> dishes = session.createQuery("from Dish", Dish.class).getResultList();
		System.out.println("Dishes retrieved from the database:");
		for (Dish dish : dishes) {
			System.out.println("Dish Name: " + dish.getName() + ", Price: " + dish.getPrice() +
					", Can be delivered: " + dish.isCouldBeDelivered());
		}
	}
	@Override
	protected synchronized void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String msgString = msg.toString();
		System.out.println( "SimpleServer" + " " + msgString);
		if (msgString.startsWith("#warning")) {
			Warning warning = new Warning("Warning from server!");
			try {
				client.sendToClient(warning);
				System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (msgString.startsWith("add client")) {
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
		} else if (msgString.startsWith("remove client")) {
			if (!SubscribersList.isEmpty())	{
				for (SubscribedClient subscribedClient : SubscribersList) {
					if (subscribedClient.getClient().equals(client)) {
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		} else if (msgString.startsWith("GetDishNames")) {

			var dishNames = getDishNames();
			System.out.println("dishNames.getClass()");
			try {
				client.sendToClient(dishNames);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (msgString.startsWith("GetDishInfo")) {
			String dishName = msgString.substring("GetDishInfo:".length());
			var dish = getDish(dishName);
			try {
				client.sendToClient(dish);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (msg instanceof Dish) {
			try {
				session.beginTransaction();

				var dish = (Dish) msg;
				System.out.println(dish.getPrice());
				var dish2 = session.byNaturalId(Dish.class).using("name", dish.getName()).load();

				dish2.setPrice(dish.getPrice());
				System.out.println("image ");
				session.getTransaction().commit(); // Save everything
				printAllDishes();
				sendToAllClients(new MenuUpdateEvent(dish));
			} catch (Exception exception) {
				if (session != null) {
					session.getTransaction().rollback();
				}
				System.err.println("An error occurred, changes have been rolled back.");
				exception.printStackTrace();
			}

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

