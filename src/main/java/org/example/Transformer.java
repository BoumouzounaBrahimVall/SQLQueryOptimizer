package org.example;

import java.util.ArrayList;
import java.util.List;

public class Transformer {

	public static boolean alreadyAdded(List<Node> L,Node n){
		for (Node nd:L)
			if(Node.sameTree(nd,n)==Node.nbrNodes(n)) return false;
		return true;
	}
	public Transformer() {
	}
	public static List<Node> onlyJoin1stVariant(Node mainRoot){
		List<Node> join1stVariantList=new ArrayList<>();
		int[] channged = new int[1];
		join1stVariantList.add(mainRoot);
		for(int i=0;i<2;i++)
		{
			for (int j=1;j<=2;j++){
				channged[0]=0;
				Node tmp=joinSwitcher(cloneTree(mainRoot),i,0,j,channged);
				if(alreadyAdded(join1stVariantList, tmp))join1stVariantList.add(tmp);

			}
		}

		return join1stVariantList;
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

	public static int hauteurTree(Node node) {
		if (node == null) {
			return 0;
		} else {
			int hauteurGauche = hauteurTree(node.getLeft());
			int hauteurDroit = hauteurTree(node.getRight());
			return 1 + Math.max(hauteurGauche, hauteurDroit);
		}
	}
	public static List<Node> onlySelectionConjonctivre(Node mainRoot){
		List<Node> onlySelectionConjonctivreList=new ArrayList<>();
		int[] channged = new int[1];
		onlySelectionConjonctivreList.add(mainRoot);
		for(int i=0;i<2;i++)
		{
			for (int j=1;j<=2;j++){
				channged[0]=0;
				Node tmp=andUnionSelection(cloneTree(mainRoot),i,0,j,channged);
				if(alreadyAdded(onlySelectionConjonctivreList, tmp))onlySelectionConjonctivreList.add(tmp);
				//

			}
		}

		return onlySelectionConjonctivreList;
	}
	public static Node joinSwitcher(Node a,int initial,int counter,int maxCount,int[] hasChanged){
		if (a == null) {
			return null;
		}
		if(a.getData().contains("⋈")){
			if(counter>=initial) {
				Node tmp=a.getLeft();
				a.setLeft(a.getRight());
				a.setRight(tmp);
				hasChanged[0]=1;
			}
			counter++;
		}
		if(counter==maxCount) return a;
		if(a.getLeft()!=null) joinSwitcher(a.getLeft(),initial,counter, maxCount,hasChanged);
		if (a.getRight()!=null) joinSwitcher(a.getRight(),initial,counter, maxCount,hasChanged);
		return a;
	}

	public static Node andUnionSelection(Node a ,int initial,int counter,int maxCount,int[] hasChanged){
		if (a == null) {
			return null;
		}
		if(a.getData().contains("σ") && a.getLeft().getData().contains("σ") ){
			if(counter>=initial) {

				Node tmp = a.getLeft().getLeft();
				a.setData(a.getData() + " && " + a.getLeft().getData());
				a.setLeft(tmp);
				hasChanged[0]=1;
			}
			counter++;

		}
		if(counter==maxCount) return a;
		if(a.getLeft()!=null) a.setLeft(andUnionSelection(a.getLeft(),initial,counter, maxCount,hasChanged));
		if (a.getRight()!=null) a.setRight(andUnionSelection(a.getRight(),initial,counter, maxCount,hasChanged));
		return a;

	}
	public static String concatSelection(Node a,String str){
		if (a == null) {
			return "";
		}
		if(a.getData().contains("σ") ){
			return a.getData()+concatSelection(a.getLeft(),str);
		}
		return str;
	}
	public static Node orUnionSelection(Node a){
		if (a == null) {
			return null;
		}
		if(a.getData().equals("OR") ){
			Node left=a.getLeft();//concatSelection
			System.out.println("left : "+concatSelection(left,""));
			String nodeContent=concatSelection(a.getLeft(),"")+" OR "+concatSelection(a.getRight(),"");
			a.setData(nodeContent);
			a.setLeft(getTable(left));
			a.setRight(null);
		}
		if(a.getLeft()!=null) a.setLeft(orUnionSelection(a.getLeft()));
		if (a.getRight()!=null) a.setRight(orUnionSelection(a.getRight()));
		return a;

	}
	public static Node getTable(Node a){
		if (a == null) {
			return null;
		}
		if(a.getLeft()==null&&a.getRight()==null) return a;
		if(a.getLeft()!=null) return getTable(a.getLeft());
		if (a.getRight()!=null) return getTable(a.getRight());
		return a;
	}

	public static void main(String[] args) {

		String query = "SELECT nom,Titre FROM Employee,Projet,Traveaux WHERE Employee.eid=Traveaux.eid AND Projet.pid=Traveaux.pid and Projet.b > '2'";
		String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
		String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
		Translator parsedTranslator = new Translator("select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' AND A.z='3' and C.c='3' AND B.b>'1' OR A.a<'7' AND A.a>'89' AND B.b=C.b"); //"SELECT CLIENT.ID FROM CLIENT WHERE CLIENT.ID='12'" select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' OR C.c='3' AND B.b>'1' OR A.a<'7' AND B.b=C.b
		parsedTranslator.parseQuery();
		//JPanel p=Translator.DrawTree(parsedTranslator.getTree());
		//System.out.println("Projections: " + parsedQuery.getProjections());
		//System.out.println("Tables: " + parsedQuery.getTables());
		//System.out.println("Where Tokens: " + parsedQuery.getWhereTokens());
		//Node.affch(parsedTranslator.getTree(),0);
		//Translator.DrawTree(join1stVariant(parsedTranslator.getTree()));
		onlySelectionConjonctivre(parsedTranslator.getTree()).forEach(Translator::DrawTree);
		onlySelectionConjonctivre(parsedTranslator.getTree()).forEach(n->{
			Node.affch(n,0 );
			System.out.println("-----------------------------");
		});
		//Translator.DrawTree(orUnionSelection(parsedTranslator.getTree()));
	}// AND A.a='2' and C.c='3' AND B.b>'1' OR A.a<'7' AND A.a>'89'



}