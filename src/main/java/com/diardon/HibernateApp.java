package com.diardon;

import java.util.ArrayList;

public class HibernateApp
{
	public static void main(String[] args)
	{
		System.out.println("----- ADD/GET ALL -----");
		UserManager.clearUsers();
		UserManager.addUser("Juan", "juan@empresa.com");
		UserManager.addUser("Ana", "ana@empresa.com");
		UserManager.addUser("Pedro", "pedro@empresa.com");
		UserManager.addUser("Maria", "maria@empresa.com");
		showUsers();

		System.out.println("----- GET/UPDATE -----");
		User user = UserManager.getUser("maria@empresa.com");
		UserManager.updateUser(user.getId(), user.getName(), "maria2@empresa.com");
		showUsers();

		System.out.println("----- DELETE -----");
		user = UserManager.getUser("juan@empresa.com");
		UserManager.deleteUser(user.getId());
		UserManager.deleteUser("Ana", "ana@empresa.com");
		showUsers();
	}
	public static void showUsers()
	{
		System.out.println("----- USUARIOS -----");
		ArrayList<User> users = UserManager.getAllUsers();
		for(User user : users)
		{
			System.out.println(user);
		}
	}
}
