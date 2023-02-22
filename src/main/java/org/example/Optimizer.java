package org.example;

import java.util.*;

/**
 * 
 */
public class Optimizer {

	/**
	 * Default constructor
	 */
	public Optimizer() {
	}

	public static void main(String[] args) {

		String query = "SELECT Enom,Titre FROM Employee,Projet,Traveaux WHERE Projet.budget = '250' AND Employee.eid=Traveaux.eid AND Projet.pid=Traveaux.pid";
		String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
		String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
		Query parsedQuery = new Query(query); //"SELECT CLIENT.ID FROM CLIENT WHERE CLIENT.ID='12'"
		parsedQuery.parseQuery();
		parsedQuery.DrawTree();
		//System.out.println("Projections: " + parsedQuery.getProjections());
		//System.out.println("Tables: " + parsedQuery.getTables());
		//System.out.println("Where Tokens: " + parsedQuery.getWhereTokens());
		Node.affch(parsedQuery.getTree(),0);
	}



}