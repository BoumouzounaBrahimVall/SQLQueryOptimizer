package org.example;

public class Optimizer {

	public Optimizer() {
	}

	public static void main(String[] args) {

		String query = "SELECT nom,Titre FROM Employee,Projet,Traveaux WHERE Employee.eid=Traveaux.eid AND Projet.pid=Traveaux.pid and Projet.b > '2'";
		String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
		String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
		Translator parsedTranslator = new Translator("select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' and C.c='3' AND B.b>'1' OR A.a<'7' AND A.a>'89' AND B.b=C.b"); //"SELECT CLIENT.ID FROM CLIENT WHERE CLIENT.ID='12'" select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' OR C.c='3' AND B.b>'1' OR A.a<'7' AND B.b=C.b
		parsedTranslator.parseQuery();
		parsedTranslator.DrawTree();
		//System.out.println("Projections: " + parsedQuery.getProjections());
		//System.out.println("Tables: " + parsedQuery.getTables());
		//System.out.println("Where Tokens: " + parsedQuery.getWhereTokens());
		Node.affch(parsedTranslator.getTree(),0);
	}



}