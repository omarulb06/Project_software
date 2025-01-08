package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Dish;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

public class SimpleServer extends AbstractServer {

	private static Session session;
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	private static SessionFactory getSessionFactory() throws HibernateException {
		var config = new Configuration();
		config.addAnnotatedClass(Dish.class);
		var serviceRegistry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
		return config.buildSessionFactory(serviceRegistry);
	}

	public SimpleServer(int port) throws HibernateException {
		super(port);
		try {
			var sessionFactory = getSessionFactory();
			session = sessionFactory.openSession();
		} catch (HibernateException e) {
			if (session != null)
				session.close();
			throw e;
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
		} else if (msg instanceof Menu) {
			session.beginTransaction();
			for (var dish : ((Menu) msg).dishes) {
				session.merge(dish);
			}
			session.getTransaction().commit();
			sendToAllClients(msg);
		}
	}

	private static List<String> getDishNames() {
		session.beginTransaction();
		var builder = session.getCriteriaBuilder();
		var query = builder.createQuery(String.class);
		var root = query.from(Dish.class);
		query.select(root.get("name"));
		var dishNames = session.createQuery(query).getResultList();
		session.getTransaction().commit();
		return dishNames;
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

