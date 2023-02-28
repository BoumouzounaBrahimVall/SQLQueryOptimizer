package org.example;

import javax.swing.*;
import javax.swing.text.TabableView;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 Brahim vall please remember that even if there are somme tokens left from the join extraction
 they won't affect the subtree creation because they will be ignored in the condition
 */



public class Translator {
	private Node Tree;

	//s:nimporte quel espace
	private static final String SELECT_REGEX = "^SELECT\\s+(.*)\\s+FROM\\s+(.*)$";
	private static final String PROJECTION_REGEX = "^(.*),(.*)$";
	private static final Pattern CONDITIONS_PATTERN = Pattern.compile("\\w+\\s*\\.\\s*\\w+\\s*[=><]\\s*'[^']*'|AND|OR|\\b\\w+\\.\\w+\\b\\s*=\\s*\\b\\w+\\.\\w+\\b");

	//listes
	private final List<String> projections;
	private final List<String> whereTokens; // without joins
	private final List<String> Joins;
	private final List<String> tables;
	public Node getTree() {return Tree;}
	public void setTree(Node tree) {Tree = tree;}
	public List<String> getWhereTokens() {
		return whereTokens;
	}
	public List<String> getProjections() {return projections;}
	public List<String> getTables() {return tables;}
	public Translator(String query) {
		query=query.toUpperCase();
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
			//System.out.println("token:" +token);
			if(token.matches("\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+"))	 // jointure
			{
				this.Joins.add(token);
			}else tokens.add(token);
		}
		this.projections=projectionList;
		this.tables=tab;
		this.whereTokens=tokens;
	}
	public  void parseQuery( ) {
			mergSubTrees();
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

	private   Node createSubTree(List<String> tabConditions,String tabName){
		Node tabTree=null;
		for(String str: tabConditions){
			if(!str.equals("AND")) tabTree=addSubTreeNode(tabTree,str);
		}
		if(tabTree==null) return new Node(tabName);
		return addTable(tabTree,tabName);
	}
	private   Node addTable(Node root,String tabName){
		Node nv;
		nv= new Node(tabName);
		if(root==null) return root;//arbre vide
		if(root.getRight()!=null) root.setRight( addTable(root.getRight(),tabName));
		if (root.getLeft()!=null) root.setLeft(addTable(root.getLeft(),tabName));
		else root.setLeft(nv);
		return root;

	}
	private   Map<String,Node> createAllSubTrees(){
		Map<String,Node>subtrees=new HashMap<>(); // use to store subTrees a.k.a tables trees
		for(String tab:this.tables) { // for each table

			List<String> tmp=new ArrayList<>();// used for collecting  conditions for one table
			if(!this.whereTokens.isEmpty()) {
				// the first element doesn't have a previous operator
				if (this.whereTokens.get(0).matches("\\w+\\s*\\.\\s*\\w+\\s*[=><]\\s*'[^']*'") && this.whereTokens.get(0).split("\\.")[0].equals(tab)) {
					System.out.println("table " + tab + " token: " + this.whereTokens.get(0));
					tmp.add("σ"+this.whereTokens.get(0));// so we add it only without its prev operator
				}

				for (int i = 1; i < this.whereTokens.size(); i++) {// for the rest of tokens its guarantied that it have a prev operator,so we store it and its prev operator
					String privOper = this.whereTokens.get(i - 1);
					String token = this.whereTokens.get(i);
					System.out.println("spleted : " + token.split("\\.")[0]);
					if (token.matches("\\w+\\s*\\.\\s*\\w+\\s*[=><]\\s*'[^']*'") && token.split("\\.")[0].equals(tab)) { // if the token belongs to the table (ex: table.atr='sth')
						System.out.println("table " + tab + " token: " + token + " prevOper: " + privOper);
						tmp.add(privOper);
						tmp.add("σ"+token);
					}
				}
				System.out.println(tmp);
			}
			subtrees.put(tab,createSubTree(tmp,tab)); // create the subtree of the table
		}
		subtrees.forEach((k,v)->{ Node.affch(v,0);System.out.println("\n-----------------");});//TODO:: trace subtrees

		return subtrees; // return the fucking tree
	}
	private void mergSubTrees(){
		if(this.whereTokens.isEmpty()&& this.Joins.isEmpty() ){
			this.Tree=new Node(tables.get(0));
			return;
		}
		Map<String,Node>subtrees=createAllSubTrees(); // use to store subTrees a.k.a tables trees
		if (Joins.isEmpty()){ // one table
			this.Tree=subtrees.get(tables.get(0));
		}
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
	private   Node addSubTreeNode(Node root,String token){
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
			nv.setLeft(root);
			root=nv;
		}
		return root;
	}
	public static JPanel DrawTree(Node th)
	{
		JFrame frame = new JFrame("Arbre binaire");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TreeVisualizer panel = new TreeVisualizer(th);
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel panGen=new JPanel();
		panGen.add(scrollPane);
		frame.add(scrollPane);
		frame.setVisible(true);
		return panGen;
	}
	public static void addDrawnTree(JPanel p,Node th){
		TreeVisualizer panel = new TreeVisualizer(th);
		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		p.add(scrollPane);
	}
}
