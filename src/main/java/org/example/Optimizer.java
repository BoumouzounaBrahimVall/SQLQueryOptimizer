package org.example;

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

		String query = "SELECT nom,Titre FROM Employee,Projet,Traveaux WHERE Projet.b = '2' AND Employee.eid=Traveaux.eid AND Projet.pid=Traveaux.pid";
		String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
		String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
		Translator parsedTranslator = new Translator(query); //"SELECT CLIENT.ID FROM CLIENT WHERE CLIENT.ID='12'"
		parsedTranslator.parseQuery();
		parsedTranslator.DrawTree();
		//System.out.println("Projections: " + parsedQuery.getProjections());
		//System.out.println("Tables: " + parsedQuery.getTables());
		//System.out.println("Where Tokens: " + parsedQuery.getWhereTokens());
		Node.affch(parsedTranslator.getTree(),0);
	}



}