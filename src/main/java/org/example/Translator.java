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
		if(!this.projections.isEmpty()){
			String proj="π"+this.projections;
			Node head= new Node(proj);
			head.setLeft(Tree);
			Tree=head;
		}


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
	public static Node subTreeCreate(List<String> tabConditions,String tabName){
		Node tabTree=null;
		for(String str: tabConditions){
			if(!str.equals("AND")) tabTree=addSubTreeNode(tabTree,str);
		}
		tabTree=addTable(tabTree,tabName);
		return tabTree;
	}
	public static Node addTable(Node root,String tabName){
		Node nv;
		nv= new Node(tabName);
		if(root==null) return root;//arbre vide
		if(root.getRight()!=null) root.setRight( addTable(root.getRight(),tabName));
		if (root.getLeft()!=null) root.setLeft(addTable(root.getLeft(),tabName));
		else root.setLeft(nv);
		return root;

	}
	public static void main(String[] args) {
		List<String>L =new ArrayList<>();
		L.add("AND");
		L.add("a=2");
		L.add("AND");
		L.add("h=3");
		L.add("AND");
		L.add("z=7");
		L.add("OR");
		L.add("s=2");
		L.add("OR");
		L.add("ss=dd");
		L.add("AND");
		L.add("dfgf=45");
		System.out.println(L);
		Node.affch(subTreeCreate(L,"TAB"),0);
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




	/**
	 * 
	 */
	public String queryScrpt;

}
