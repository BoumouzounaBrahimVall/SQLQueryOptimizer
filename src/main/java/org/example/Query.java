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
		private final List<String> selectionsCondition;
		private final List<String> selectionsJointure;
		private final List<String> tables;
		private static List<String> AndSelections=new ArrayList<>();
	 	private static List<String> OrSelections=new ArrayList<>();
		public Query(List<String> projections, List<String> selectionsCondition, List<String> tables,List<String> selectionsJointure) {
			this.projections = projections;
			this.selectionsCondition = selectionsCondition;
			this.tables = tables;
			this.selectionsJointure=selectionsJointure;
		}
		public List<String> getProjections() {return projections;}
		public List<String> getSelections() {return selectionsCondition;}
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
			String[] arrtables=tables.split(" ");
			Collections.addAll(tab, arrtables);
			String selections = tablesAndConditions.length > 1 ? tablesAndConditions[1] : "";
			List<String> selCondition = new ArrayList<>();
			List<String> selJointure = new ArrayList<>();
			/*Pattern pattern = Pattern.compile("(?i)(AND|OR)\\s+(\\w+)\\s*([=|>|<])\\s*'([^']*)'");
			Matcher matcher = pattern.matcher(selections);
			while (matcher.find()) {
				String operator = matcher.group(1);
				String column = matcher.group(2);
				String operation=matcher.group(3);
				String value = matcher.group(4);
				if (operator.equalsIgnoreCase("AND")) {
					AndSelections.add(column + ""+operation + value );
				} else if (operator.equalsIgnoreCase("OR")) {
					OrSelections.add(column + ""+operation + value );
				}
			}*/
			String[] andSplit = selections.split("(?i)AND");
			for (String andCondition : andSplit) {
				String[] orSplit = andCondition.split("(?i)OR");
				if (orSplit.length == 1) {
					AndSelections.add(andCondition.trim());
				} else {
					for (String orCondition : orSplit) {
						OrSelections.add(orCondition.trim());
					}
				}
			}

			if(tablesAndConditions.length > 1)
			{
				String[] arrsel=selections.split("AND|OR");
				for(String s:arrsel) {
					Pattern patternJ = Pattern.compile("\\b\\w+\\.\\w+\\b\\s*=\\s*\\b\\w+\\.\\w+\\b",Pattern.CASE_INSENSITIVE);
					Matcher matcherJ = patternJ.matcher(s);
					if(matcherJ.find())
						selJointure.add(s);
					else
						selCondition.add(s);

				}
			}



			return new Query(projectionList, selCondition, tab,selJointure);

		}


		public static void main(String[] args) {

			String query = "SELECT nom,villename FROM Personne,Ville WHERE Personne.idville = Ville.idville AND region = 'case-settat' AND a='b' OR z='z' AND s>s";
			String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
			String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
			Query parsedQuery = parseQuery(query);

			System.out.println("Projections: " + parsedQuery.getProjections());
			System.out.println("Selections Condition: " + parsedQuery.getSelections());
			System.out.println("Selections Jointure: " + parsedQuery.getSelectionsJointure());
			System.out.println("Tables: " + parsedQuery.getTables());
			System.out.println("AND SELECTIONS :"+AndSelections);
			System.out.println("OR SELECTIONS :"+OrSelections);


		}


		public List<String> getSelectionsJointure() {
			return selectionsJointure;
		}


	/**
	 * 
	 */
	public String queryScrpt;

}
