package org.example;

import javax.swing.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */


	public class Query {
		private Node Tree;
		//s:nimporte quel espace
		private static final String SELECT_REGEX = "^SELECT\\s+(.*)\\s+FROM\\s+(.*)$";
		/*commancer par nimporte quel caractere sauf retour a la ligne
		 */
		private static final String PROJECTION_REGEX = "^(.*),(.*)$";
		private static final Pattern CONDITIONS_PATTERN = Pattern.compile("\\w+\\s*\\.\\s*\\w+=\\s*'[^']*'|AND|OR|\\b\\w+\\.\\w+\\b\\s*=\\s*\\b\\w+\\.\\w+\\b");

	//liste des projections
		private List<String> projections;
		//liste des selections
		private final List<String> whereTokens;
		private final List<String> tables;

	public List<String> getWhereTokens() {
		return whereTokens;
	}
	public List<String> getProjections() {return projections;}
	public List<String> getTables() {return tables;}
	public Query(String query) {
		Matcher selectMatcher = Pattern.compile(SELECT_REGEX).matcher(query);
		if (!selectMatcher.find()) {
			throw new IllegalArgumentException("Invalid query: " + query);
		}


		String projections = selectMatcher.group(1);
		String tablesAndSelections = selectMatcher.group(2);
		List<String> projectionList = new ArrayList<>();

		if(!(projections.equals("*"))){
			Matcher projectionMatcher = Pattern.compile(PROJECTION_REGEX).matcher(projections);
			while (projectionMatcher.find()) {
				projectionList.add(projectionMatcher.group(1));
				projections = projectionMatcher.group(2);
				projectionMatcher = Pattern.compile(PROJECTION_REGEX).matcher(projections);
			}
			projectionList.add(projections);
		}
		String[] tablesAndConditions = tablesAndSelections.split("\\s+WHERE\\s+");
		String tables = tablesAndConditions[0];

		List<String> tab = new ArrayList<>();
		String[] arrtables=tables.split(",");
		Collections.addAll(tab, arrtables);

		String selections = tablesAndConditions.length > 1 ? tablesAndConditions[1] : "";
		//todo:: create the tree
		Matcher matcher = CONDITIONS_PATTERN.matcher(selections);// "w.b=k.g AND k.a='1' AND k.b='1' OR w.c='1' AND w.d='1' AND k.e='1' OR k.f='1' AND k.g='1'"
		List<String> tokens = new ArrayList<>();
		while (matcher.find()) {
			String token = matcher.group();
			tokens.add(token);
		}
		this.projections=projectionList;
		this.tables=tab;
		this.whereTokens=tokens;
	}
	public  void parseQuery( ) {
		Tree= null;
		for (String token : this.whereTokens) {
			Tree=inserer_exp_arbre(Tree,token,tables);
		}
		Node head= new Node("π");
		head.setLeft(Tree);
		Tree=head;

		}

	private static boolean isOperator(String elem) {
		if(elem.equals("OR")|| elem.equals("AND") ) return true;
		return false;
	}
	private static Node rootBeLeft(Node arb,Node nv)
	{
		nv.setLeft(arb);//arb devient fils gauche de nv
		return nv;//nv devient racine
	}
	public static Node inserer_exp_arbre(Node arb,String elem,List<String> tab)
	{
		Node nv, tmp;
		nv= new Node(elem);

		if(elem.matches("\\w+\\s*\\.\\s*\\w+=\\s*'[^']*'")) { // selection condition
			nv.setData("σ"+elem);
			nv.setLeft(new Node(tab.get(tab.indexOf( elem.split("\\s*\\.")[0]))));
		}

		else if(elem.matches("\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+"))	 // jointure
		{


			Pattern p=Pattern.compile("\\w+");
			Matcher matcher = p.matcher(elem);
			if (matcher.find()) nv.setLeft(new Node(tab.get(tab.indexOf(matcher.group()))));
			matcher.find();
			if (matcher.find()) nv.setRight(new Node(tab.get(tab.indexOf(matcher.group()))));
			nv.setData("⋈"); // jointure


		}
		if(arb==null) return nv;//arbre vide
		//nv est une condition
		if(!isOperator(elem))
		{// si la racine n'as pas de fils froit


			if(arb.getRight()==null) arb.setRight(nv); // nv devient fils dt
			else //sinon il est inserer etant le fils le plus a droite
			{


				tmp=arb.getRight();
				tmp.setRight(nv);
			} //(ceci sera a gauche si un opperateur le suive)
		}
		else// si nv est un opperateur
		{   // si la racine est OR et nv AND
			if ( arb.getData().equals("OR")&&nv.getData().equals("AND") )
			{
				tmp=arb.getRight(); // garder le fils droit de la racine
				arb.setRight(nv); // le fils droit devient  nv
				nv.setLeft(tmp); // le fils gauche de nv recoit tmp
			}
			//sinon il devient racine et l'arbre son fils gauche
			else arb=rootBeLeft(arb,nv);
		}
		return arb;
	}

	public void DrawTree()
	{
		JFrame frame = new JFrame("Arbre binaire");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TreeVisualizer panel = new TreeVisualizer(this.Tree);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public static void main(String[] args) {


			String query = "SELECT nom,villename FROM Personne,Ville WHERE Personne.idville = Ville.idville AND Ville.region = 'case-settat' AND Personne.a='b' OR Personne.z='z' AND Ville.s='s'";
			String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
			String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
			Query parsedQuery = new Query(query);
			parsedQuery.parseQuery();
			//System.out.println("Projections: " + parsedQuery.getProjections());
			//System.out.println("Tables: " + parsedQuery.getTables());
			//System.out.println("Where Tokens: " + parsedQuery.getWhereTokens());
		Node.affch(parsedQuery.Tree,0);




		}





	/**
	 * 
	 */
	public String queryScrpt;

}
