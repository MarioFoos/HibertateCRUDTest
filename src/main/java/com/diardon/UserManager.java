package com.diardon;

import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserManager
{
	private static SessionFactory sessionFactory = null;

	public static SessionFactory getSessionFactory()
	{
		if(sessionFactory == null)
		{
			try
			{
				// Crear la configuración y construir el SessionFactory
				sessionFactory = new Configuration()
						.addAnnotatedClass(User.class) // Agregamos las entidades
						.buildSessionFactory();
			}
			catch(Throwable e)
			{
	            throw new ExceptionInInitializerError(e);
	        }
		}
		return sessionFactory;
	}
	/**
	 * Borrar todos los usuarios
	 */
	public static void clearUsers()
	{
		ArrayList<User> users = getAllUsers();
		for(User user : users)
		{
			deleteUser(user.getId());
		}
	}
	/**
	 * Borrar usuario por nombre y correo
	 * @param name Nombre
	 * @param email Correo
	 * @return true si se borró o no existe
	 */
	public static void deleteUser(String name, String email)
	{
        User user = getUser(name, email);
        if(user != null)
        {
        	deleteUser(user.getId());
        }
    }
	/**
	 * Borrar usuario por ID
	 * @param id ID
	 */
	public static void deleteUser(Long id)
	{
        Transaction transaction = null;
        User user = null;
        try(Session session = getSessionFactory().openSession())
        {
            transaction = session.beginTransaction();
            user = session.get(User.class, id);
            if(user != null)
            {
            	session.remove(user);
            }
            transaction.commit();
    		session.close();
        }
        catch(Exception e)
        {
            if(transaction != null)
            {
            	transaction.rollback();
            }
            e.printStackTrace();
		}
    }
	/**
	 * Actualizar datos del usuario
	 * @param id ID
	 * @param name Nombre
	 * @param email Correo
	 * @return Usuario
	 */
    public static User updateUser(Long id, String name, String email)
    {
        Transaction transaction = null;
        User user = null;
        try(Session session = getSessionFactory().openSession())
        {
            transaction = session.beginTransaction();
            user = session.get(User.class, id);
            if(user != null)
            {
            	user.setName(name);
            	user.setEmail(email);
            	session.persist(user);
            }
            transaction.commit();
    		session.close();
        }
        catch(Exception e)
        {
            if(transaction != null)
            {
            	transaction.rollback();
            }
            e.printStackTrace();
		}
        return user;
    }
	/**
	 * Agregar usuario
	 * @param user Usuario
	 * @return Usuario agregado
	 */
	public static User addUser(String name, String email)
	{
		User user = getUser(email);
		if(user == null)
		{
			Transaction transaction = null;
			try(Session session = getSessionFactory().openSession())
			{
	            transaction = session.beginTransaction();
	            user = new User(name, email);
	            session.persist(user);
	            transaction.commit();
	    		session.close();
				System.out.println("Added: " + user);
			}
			catch(Exception e)
			{
	            if(transaction != null)
	            {
	            	transaction.rollback();			
	            }
	        	e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Already exists: " + user);
		}
		return user;
	}
	/**
	 * Obtener un usuario por su nombre y correo electrónico
	 * @param name Nombre
	 * @param email EMail
	 * @return Usuario o null
	 */
	public static User getUser(String name, String email)
	{
		ArrayList<User> results = new ArrayList<User>();

		try(Session session = getSessionFactory().openSession())
		{
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<User> cr = cb.createQuery(User.class);

			Root<User> root = cr.from(User.class);
			// Agrego múltiples criterios
			Predicate[] predicates = new Predicate[2];
			predicates[0] = cb.equal(root.get("email"), email);
			predicates[1] = cb.equal(root.get("name"), name);
			cr.select(root).where(predicates);

			// Ejecuto la query
			Query<User> query = session.createQuery(cr);
    		results.addAll(query.getResultList());
    		session.close();
		}
		catch(Exception e)
		{
        	e.printStackTrace();
		}
		return results.isEmpty() ? null : results.get(0);
	}
	/**
	 * Obtener el usuario por email 
	 * @param id ID
	 * @return Usuario
	 */
	public static User getUser(Long id)
	{
		ArrayList<User> results = new ArrayList<User>();

		try(Session session = getSessionFactory().openSession())
        {
    		CriteriaBuilder cb = session.getCriteriaBuilder();
    		CriteriaQuery<User> cr = cb.createQuery(User.class);

    		Root<User> root = cr.from(User.class);
    		// Agrego el criterio de igualdad de id
    		Predicate idEqual = cb.equal(root.get("id"), id);
    		cr.select(root).where(idEqual);

    		// Ejecuto la query
    		Query<User> query = session.createQuery(cr);
    		results.addAll(query.getResultList());
    		session.close();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
		return results.isEmpty() ? null : results.get(0);
	}
	/**
	 * Obtener el usuario por email 
	 * @param email Email
	 * @return Usuario
	 */
	public static User getUser(String email)
	{
		ArrayList<User> results = new ArrayList<User>();

		try(Session session = getSessionFactory().openSession())
        {
    		CriteriaBuilder cb = session.getCriteriaBuilder();
    		CriteriaQuery<User> cr = cb.createQuery(User.class);

    		Root<User> root = cr.from(User.class);
    		// Agrego el criterio de igualdad de email
    		Predicate emailEqual = cb.equal(root.get("email"), email);
    		cr.select(root).where(emailEqual);

    		// Ejecuto la query
    		Query<User> query = session.createQuery(cr);
    		results.addAll(query.getResultList());
    		session.close();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
		return results.isEmpty() ? null : results.get(0);
	}
	/**
	 * Obtener todos los usuarios
	 * @return Usuario
	 */
	public static ArrayList<User> getAllUsers()
	{
		ArrayList<User> results = new ArrayList<User>();

        try(Session session = getSessionFactory().openSession())
        {

    		CriteriaBuilder builder = session.getCriteriaBuilder();
    		CriteriaQuery<User> criteria = builder.createQuery(User.class);

    		// Hago un from sin filtrar nada
    		Root<User> root = criteria.from(User.class);
    		criteria.select(root);

    		// Ejecuto la query
    		Query<User> query = session.createQuery(criteria);
    		results.addAll(query.getResultList());
    		session.close();;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
		return results;
	}	
}
