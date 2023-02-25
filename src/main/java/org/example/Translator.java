package org.example;

import javax.swing.*;
import javax.swing.text.TabableView;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */


	public class Translator {
		private Node Tree;

	public Node getTree() {
		return Tree;
	}

	public void setTree(Node tree) {
		Tree = tree;
	}

	//s:nimporte quel espace
		private static final String SELECT_REGEX = "^SELECT\\s+(.*)\\s+FROM\\s+(.*)$";
		/*commancer par nimporte quel caractere sauf retour a la ligne
		 */
		private static final String PROJECTION_REGEX = "^(.*),(.*)$";
		private static final Pattern CONDITIONS_PATTERN = Pattern.compile("\\w+\\s*\\.\\s*\\w+\\s*=\\s*'[^']*'|AND|OR|\\b\\w+\\.\\w+\\b\\s*=\\s*\\b\\w+\\.\\w+\\b");

	//liste des projections
		private List<String> projections;
		//liste des selections
		private final List<String> whereTokens;
		private final List<String> Joins;
		private final List<String> tables;

	public List<String> getWhereTokens() {
		return whereTokens;
	}
	public List<String> getProjections() {return projections;}
	public List<String> getTables() {return tables;}
	public Translator(String query) {
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
		this.Joins=new ArrayList<>();
		while (matcher.find()) {
			String token = matcher.group();
			System.out.println("token:" +token);
			if(token.matches("\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+"))	 // jointure
			{
				this.Joins.add(token);
			}else tokens.add(token);
		}
		this.projections=projectionList;
		this.tables=tab;

		for(String str:tokens ){

		}
		this.whereTokens=tokens;
	}
	public  void parseQuery( ) {
		mergSubTrees();
/*
		for (String token : this.whereTokens) {
			Tree=inserer_exp_arbre(Tree,token,tables);
		}*/
		if(!this.projections.isEmpty()){
			String proj="π"+this.projections;
			Node head= new Node(proj);
			head.setLeft(Tree);
			Tree=head;
		}


		}

	private static boolean isOperator(String elem) {
		return(elem.equals("OR")|| elem.equals("AND") ) ;
	}
	private static Node rootBeLeft(Node arb,Node nv)
	{
		nv.setLeft(arb);//arb devient fils gauche de nv
		return nv;//nv devient racine
	}
	public  Node inserer_exp_arbre(Node arb,String elem,List<String> tab)
	{
		Node nv, tmp;
		nv= new Node(elem);

		if(elem.matches("\\w+\\s*\\.\\s*\\w+\\s*=\\s*'[^']*'")) { // selection condition
			nv.setData("σ("+elem+")");
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
	public  Node createSubTree(List<String> tabConditions,String tabName){
		Node tabTree=null;
		for(String str: tabConditions){
			if(!str.equals("AND")) tabTree=addSubTreeNode(tabTree,str);
		}
		if(tabTree==null) return new Node(tabName);
		return addTable(tabTree,tabName);
	}
	public  Node addTable(Node root,String tabName){
		Node nv;
		nv= new Node(tabName);
		if(root==null) return root;//arbre vide
		if(root.getRight()!=null) root.setRight( addTable(root.getRight(),tabName));
		if (root.getLeft()!=null) root.setLeft(addTable(root.getLeft(),tabName));
		else root.setLeft(nv);
		return root;

	}
	public  Map<String,Node> createAllSubTrees(){
		Map<String,Node>subtrees=new HashMap<>(); // use to store subTrees a.k.a tables trees
		for(String tab:this.tables) { // for each table
			List<String> tmp=new ArrayList<>();// used for collecting  conditions for one table
			// the first element doesn't have a previous operator
			if (this.whereTokens.get(0).contains(tab)) tmp.add(this.whereTokens.get(0));// so we add it only without its prev operator

			for (int i = 1; i < this.whereTokens.size(); i++) {// for the rest of tokens its guarantied that it have a prev operator,so we store it and its prev operator
				String privOper=this.whereTokens.get(i-1);
				String token=this.whereTokens.get(i);
				if (token.contains(tab)) { // if the token belongs to the table (ex: table.atr='sth')
					tmp.add(privOper);
					tmp.add(token);
				}
			}

			/**
			    Brahim vall please remember that even if there are somme tokens left from the join extraction
				they won't affect the subtree creation because they will be ignored in the condition
			 */

			System.out.println(tmp);
			subtrees.put(tab,createSubTree(tmp,tab)); // create the subtree of the table
		}
		subtrees.forEach((k,v)->{ Node.affch(v,0);System.out.println("\n-----------------");});//TODO:: trace subtrees

		return subtrees; // return the fucking tree
	}
	private void mergSubTrees(){
		Map<String,Node>subtrees=createAllSubTrees(); // use to store subTrees a.k.a tables trees
		for(String join: this.Joins){
			Node joinNode =new Node("⋈"); // create the join
			Pattern p=Pattern.compile("\\w+");//pattern of tables extraction
			Matcher matcher = p.matcher(join);
			String tab1="",tab2="";// to store the tables
			if (matcher.find()) tab1=matcher.group(); // extract the first table of the join
			matcher.find(); // ignore the first table attribute
			if (matcher.find()) tab2=matcher.group();; //extract the second table of the join
			System.out.println("table1: "+tab1+" table2: "+tab2); //TODO:: trace join tables
			if(subtrees.containsKey(tab1) && subtrees.containsKey(tab2)){ // the first node the main tree
				joinNode.setLeft(subtrees.get(tab1)); //
				joinNode.setRight(subtrees.get(tab2));
				subtrees.remove(tab1);// remove the table subtree to avoid redolence
				subtrees.remove(tab2);//same here

			}else { // means that one of the tables has already been used in a join and added
				joinNode.setLeft(this.Tree);
				if(subtrees.containsKey(tab1)){ // if tab 1 not added yet
					joinNode.setRight(subtrees.get(tab1));
					subtrees.remove(tab1);
				}else {  // if tab 2 not added yet
					joinNode.setRight(subtrees.get(tab2));
					subtrees.remove(tab2);
				}
			}
			this.Tree=joinNode; // always keep the join node as the root cause we're building from down to up

		}
	}
	public static Node addSubTreeNode(Node root,String token){
		Node nv,tmp;
		nv= new Node(token);
		if(root==null) return nv;//arbre vide
		if(!isOperator(token))
		{// si la racine n'as pas de fils froit
			if(root.getData().equals("OR")){
				if(root.getRight()==null) root.setRight(nv); // nv devient fils dt
				else //sinon il est inserer etant le fils le plus a droite
				{
					root.setRight(addSubTreeNode(root.getRight(),token));
				} //(ceci sera a gauche si un opperateur le suive)
			}
			else{
				nv.setLeft(root);
					root=nv;

					//root.setLeft(addSubTreeNode(root.getLeft(),token));
			}
		}else// si nv est un opperateur
		{   // si la racine est OR
			 root=rootBeLeft(root,nv);
		}
		return root;
	}
	public void DrawTree()
	{
		JFrame frame = new JFrame("Arbre binaire");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TreeVisualizer panel = new TreeVisualizer(this.Tree);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		frame.add(scrollPane);
		frame.setVisible(true);
	}
}
