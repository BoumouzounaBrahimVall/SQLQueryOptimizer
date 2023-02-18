package org.example;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */


	public class Query {

		//s:nimporte quel espace
		private static final String SELECT_REGEX = "^SELECT\\s+(.*)\\s+FROM\\s+(.*)$";
		/*commancer par nimporte quel caractere sauf retour a la ligne
		 */
		private static final String PROJECTION_REGEX = "^(.*),(.*)$";
		//liste des projections
		private final List<String> projections;
		//liste des selections
		private final List<String> whereTokens;
		private final List<String> tables;

	public List<String> getWhereTokens() {
		return whereTokens;
	}

	public Query(List<String> projections, List<String> tables, List<String> tokens) {
			this.projections = projections;
			this.tables = tables;
			this.whereTokens=tokens;
		}
		public List<String> getProjections() {return projections;}
		public List<String> getTables() {return tables;}
		public static Query parseQuery(String query) {
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


			return new Query(projectionList, tab,null);

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
		Node nv; Node tmp=null;
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


	public static void main(String[] args) {


			String query = "SELECT nom,villename FROM Personne,Ville WHERE Personne.idville = Ville.idville AND region = 'case-settat' AND a='b' OR z='z' AND s>s";
			String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
			String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
			Query parsedQuery = parseQuery(query);
			System.out.println("Projections: " + parsedQuery.getProjections());
			System.out.println("Tables: " + parsedQuery.getTables());
			System.out.println("Where Tokens: " + parsedQuery.getWhereTokens());




		}





	/**
	 * 
	 */
	public String queryScrpt;

}
