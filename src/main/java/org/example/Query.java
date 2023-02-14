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
		public Query(List<String> projections, List<String> selectionsCondition, List<String> tables,List<String> selectionsJointure) {
			this.projections = projections;
			this.selectionsCondition = selectionsCondition;
			this.tables = tables;
			this.selectionsJointure=selectionsJointure;
		}
		public List<String> getProjections() {
			return projections;
		}

		public List<String> getSelections() {
			return selectionsCondition;
		}

		public List<String> getTables() {
			return tables;
		}

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
			String query = "SELECT nom,villename FROM Personne,Ville WHERE Personne.idville = Ville.idville AND region = case-settat OR a=b";
			Query parsedQuery = parseQuery(query);
			System.out.println("Projections: " + parsedQuery.getProjections());
			System.out.println("Selections Condition: " + parsedQuery.getSelections());
			System.out.println("Selections Jointure: " + parsedQuery.getSelectionsJointure());
			System.out.println("Tables: " + parsedQuery.getTables());
		}

		public List<String> getSelectionsJointure() {
			return selectionsJointure;
		}

	}
