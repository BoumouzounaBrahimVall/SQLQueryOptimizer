package org.example;

public class Optimizer {
	public static Node root1;
	public Optimizer() {
	}
	public static Node join1stVariant(Node mainRoot){
		return joinSwitcher(cloneTree(mainRoot));
	}
	public static Node joinSwitcher(Node a){
		if (a == null) {
			return null;
		}
		if(a.getData().contains("⋈")){
			Node tmp=a.getLeft();
			a.setLeft(a.getRight());
			a.setRight(tmp);
		}
		if(a.getLeft()!=null) joinSwitcher(a.getLeft());
		if (a.getRight()!=null) joinSwitcher(a.getRight());
		return a;
	}
	public static Node cloneTree(Node root) {
		if (root == null) {
			return null;
		}
		Node newNode =new Node(root.getData());
		newNode.setLeft( cloneTree(root.getLeft()) );
		newNode.setRight( cloneTree(root.getRight()) );
		return newNode;
	}
	public static void main(String[] args) {

		String query = "SELECT nom,Titre FROM Employee,Projet,Traveaux WHERE Employee.eid=Traveaux.eid AND Projet.pid=Traveaux.pid and Projet.b > '2'";
		String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
		String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
		Translator parsedTranslator = new Translator("select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' and C.c='3' AND B.b>'1' OR A.a<'7' AND A.a>'89' AND B.b=C.b"); //"SELECT CLIENT.ID FROM CLIENT WHERE CLIENT.ID='12'" select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' OR C.c='3' AND B.b>'1' OR A.a<'7' AND B.b=C.b
		parsedTranslator.parseQuery();
		Translator.DrawTree(parsedTranslator.getTree());
		//System.out.println("Projections: " + parsedQuery.getProjections());
		//System.out.println("Tables: " + parsedQuery.getTables());
		//System.out.println("Where Tokens: " + parsedQuery.getWhereTokens());
		//Node.affch(parsedTranslator.getTree(),0);
		Translator.DrawTree(join1stVariant(parsedTranslator.getTree()));

	}// AND A.a='2' and C.c='3' AND B.b>'1' OR A.a<'7' AND A.a>'89'



}