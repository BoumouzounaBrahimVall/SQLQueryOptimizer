
package org.QueryOptimizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Translator {
	private static final String CONDITION_PATTERN="\\w+\\s*\\.\\s*\\w+\\s*[=><]\\s*'[^']*'";
	private static final String JOIN_PATTERN="\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+";
	private Node firstTree;
	private final String query;
	private final List<String> listProjections;
	private final List<String> listConditions; // condition selections only
	private final List<String> listJoins; // join condition
	private final List<String> listTables;


	public Translator(String txt) {

		//firstTree =new Node();
		this.listProjections=new ArrayList<>();
		this.listTables= new ArrayList<>();
		this.listJoins=new ArrayList<>();
		this.listConditions =new ArrayList<>();
		query =txt.toUpperCase();

		this.parseQuery();
		this.addProjections();
		//this.firstTree.DrawTree();

	}

	public  void parseQuery() {

		Matcher selectMatcher = SELECT_Pattern.matcher(query);
		if (!selectMatcher.find()) {
			throw new IllegalArgumentException("Wrong Query : " + query);
		}
		String projections = selectMatcher.group(1);
		String tablesAndSelections = selectMatcher.group(2);

		if(!(projections.equals("*"))){
			Matcher projectionMatcher =PROJECTION_Pattern.matcher(projections);
			while (projectionMatcher.find()) {
				this.listProjections.add(projectionMatcher.group(1));
				projections = projectionMatcher.group(2);
				projectionMatcher = PROJECTION_Pattern.matcher(projections);
			}
			this.listProjections.add(projections);
		}

		String[] tablesAndConditions = tablesAndSelections.split("\\s+WHERE\\s+");

		/// Tables
		String tablesTmp = tablesAndConditions[0];
		String[] arrTables=tablesTmp.split(",");
		for (String table : arrTables) {this.listTables.add(table.trim());}

		/// Tables
		String selections = tablesAndConditions.length > 1 ? tablesAndConditions[1] : "";

		Matcher matcher = CONDITIONS_PATTERN.matcher(selections);


		while (matcher.find()) {
			String token = matcher.group();
			if(token.matches(JOIN_PATTERN))	 // join
			{
				this.listJoins.add(token);
			}else {
				System.err.println(token);
				this.listConditions.add(token);
			}
		}

	}
	public  void addProjections() {
		mergSubTrees();

		if(!this.listProjections.isEmpty()){
			String proj="π"+this.listProjections;
			Node head= new Node(proj,Node.P);
			head.setLeft(this.firstTree);
			this.firstTree=head;
		}
	}

	private void mergSubTrees(){

		if(this.listConditions.isEmpty()&& this.listJoins.isEmpty() ){
			this.firstTree=new Node(listTables.get(0),Node.T);
			return;
		}

		Map<String, Node>subtrees=createAllSubTrees(); // use to store subTrees a.k.a listTables trees
		if (listJoins.isEmpty()){ // one table
			this.firstTree=subtrees.get(listTables.get(0));
		}

		for(String join: this.listJoins){
			Node joinNode =new Node("⋈"+join,Node.J);
			Pattern p=Pattern.compile("\\w+");
			Matcher matcher = p.matcher(join);
			String tab1="",tab2="";
			if (matcher.find()) tab1=matcher.group(); // extract the first table of the join

			if (matcher.find() &&matcher.find()) tab2=matcher.group(); //extract the second table of the join

			if(subtrees.containsKey(tab1) && subtrees.containsKey(tab2)){ // the first node the main tree
				joinNode.setLeft(subtrees.get(tab1)); //
				joinNode.setRight(subtrees.get(tab2));
				subtrees.remove(tab1);// remove the table subtree to avoid redolence
				subtrees.remove(tab2);//same here

			}else { // means that one of the listTables has already been used in a join and added
				joinNode.setLeft(this.firstTree);
				if(subtrees.containsKey(tab1)){ // if tab 1 not added yet
					joinNode.setRight(subtrees.get(tab1));
					subtrees.remove(tab1);
				}else {  // if tab 2 not added yet
					joinNode.setRight(subtrees.get(tab2));
					subtrees.remove(tab2);
				}
			}
			this.firstTree=joinNode; // always keep the join node as the root cause we're building from down to up

		}
	}
	private   Map<String, Node> createAllSubTrees(){

		Map<String, Node>subtrees=new HashMap<>(); // use to store subTrees a.k.a listTables trees
		for(String tab:this.listTables) { // for each table
			List<String> tmp=new ArrayList<>();// used for collecting  listConditions for one table
			if(!this.listConditions.isEmpty()) {
				// the first element doesn't have a previous operator
				if (this.listConditions.get(0).matches(CONDITION_PATTERN) && this.listConditions.get(0).split("\\.")[0].equals(tab)) {
					tmp.add("σ"+this.listConditions.get(0));// so we add it only without its prev operator
				}
				for (int i = 1; i < this.listConditions.size(); i++) {// for the rest of tokens its guarantied that it have a prev operator,so we store it and its prev operator
					String privOper = this.listConditions.get(i - 1);
					String token = this.listConditions.get(i);
					if (token.matches(CONDITION_PATTERN) && token.split("\\.")[0].matches(tab)) { // if the token belongs to the table (ex: table.atr='sth')
						tmp.add(privOper);
						tmp.add("σ"+token);
					}
				}
			}
			subtrees.put(tab,createSubTree(tmp,tab)); // create the subtree of the table
		}


		return subtrees;
	}



	private Node createSubTree(List<String> tabConditions, String tabName){
		Node tabTree=null;
		for(String str: tabConditions){
			if(!str.equals("AND")) tabTree=addSubTreeNode(tabTree,str);
		}
		if(tabTree==null) return new Node(tabName,Node.T);
		return addTables(tabTree,tabName);
	}

	private Node addSubTreeNode(Node root, String token){
		Node nv;
		nv= new Node(token,Node.S);
		if(root==null) return nv;
		if(!isOperator(token))
		{// si la racine n'as pas de fils droit
			if(root.getData().equals("OR")){
				if(root.getRight()==null) root.setRight(nv); // nv devient fils dt
				else //sinon il est inserer etant le fils le plus a droite
				{
					root.setRight(addSubTreeNode(root.getRight(),token));
				}
			}
			else{
				nv.setLeft(root);
				root=nv;

			}
		}else
		{   // if root is OR
			nv.setType("O");
			nv.setLeft(root);
			root=nv;
		}
		return root;
	}

	private boolean isOperator(String elem) {
		return elem.equals("OR")|| elem.equals("AND");
	}

	private Node addTables(Node root, String tabName){
		Node nv;
		nv= new Node(tabName,Node.T);
		if(root==null) return null;//arbre vide
		if(root.getRight()!=null) root.setRight(addTables(root.getRight(),tabName));
		if (root.getLeft()!=null) root.setLeft(addTables(root.getLeft(),tabName));
		else root.setLeft(nv);
		return root;

	}

	public Node getFirstTree() {
		return firstTree;
	}



	private static final Pattern SELECT_Pattern = Pattern.compile( "^SELECT\\s+(.*)\\s+FROM\\s+(.*)$");
	private static final Pattern PROJECTION_Pattern = Pattern.compile("^(.*),(.*)$");
	private static final Pattern CONDITIONS_PATTERN = Pattern.compile("\\w+\\s*\\.\\s*\\w+\\s*[=><]\\s*'[^']*'|AND|OR|\\b\\w+\\.\\w+\\b\\s*=\\s*\\b\\w+\\.\\w+\\b");

}
