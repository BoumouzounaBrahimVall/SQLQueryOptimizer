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


		String query = "SELECT nom,villename FROM Personne,Ville WHERE Personne.idville = Ville.idville AND Ville.region = 'case-settat' AND Personne.a='b' OR Personne.z='z' AND Ville.s='s'";
		String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
		String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
		Query parsedQuery = new Query(query);
		parsedQuery.parseQuery();
		parsedQuery.DrawTree();
		//System.out.println("Projections: " + parsedQuery.getProjections());
		//System.out.println("Tables: " + parsedQuery.getTables());
		//System.out.println("Where Tokens: " + parsedQuery.getWhereTokens());
		Node.affch(parsedQuery.getTree(),0);

	}

}