package org.QueryOptimizer;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Transformer {

	public List<Tree> joinRule; // Commutativity the joins (JC)
	public List<Tree> selectionRule; // Eclatement d'une sélection conjonctive (SE)
	public List<Tree> orSelectionRule; //
	public List<Tree> regleCommutSelection;//commutativiteSelection
	public List<Tree> assocJoinRule; //association jointure
	private final Tree firstTree;
	private List<Tree> allVariants;
	private List<Tree> switchJoinSelection;

	int i=0;
	public Transformer(Tree tree)
	{
		this.firstTree=tree;
		joinRule =jointureCommutativite(firstTree);
		selectionRule =selectionConjonctive(firstTree);
		orSelectionRule =onlyOrSelectionVariants(firstTree);
		regleCommutSelection=commutativiteSelection(firstTree);
		assocJoinRule =joinAssociativite(firstTree);
		buildAllVariants();


	}

	public List<Tree> getAllVariants() {
		return allVariants;
	}

	private void buildAllVariants(){

		List<Tree> tousTrees =new ArrayList<>();
		joinRule.remove(0);// delete main
		selectionRule.remove(0);
		orSelectionRule.remove(0);
		regleCommutSelection.remove(0);
		tousTrees.add(firstTree);

		for(Tree n1: joinRule){
			for(Tree n: selectionConjonctive(n1)){ //  selection of join variants
				if(notAlreadyAdded( tousTrees,n)) tousTrees.add(n);
			}
			for(Tree n: onlyOrSelectionVariants(n1)){ //  OR selection of joins
				if(notAlreadyAdded( tousTrees,n))  tousTrees.add(n);
			}
			for(Tree n: commutativiteSelection(n1))// commutativiteSelection
				if(notAlreadyAdded( tousTrees,n)) tousTrees.add(n);

			for(Tree n:joinAssociativite(n1)) //association jointure
				if(notAlreadyAdded( tousTrees,n)) tousTrees.add(n);
		}

		for(Tree n1: selectionRule){
			for(Tree n: onlyOrSelectionVariants(n1)){ //  OR selection variant of sel variants
				if(notAlreadyAdded( tousTrees,n))  tousTrees.add(n);
			}
			joinCumitVars(tousTrees, n1);
		}

		for(Tree n1: orSelectionRule){
			for(Tree n: selectionConjonctive(n1)){ //   selection variant
				if(notAlreadyAdded( tousTrees,n))   tousTrees.add(n);
			}
			joinCumitVars(tousTrees, n1);
		}


		for(Tree n1: regleCommutSelection){
			assoSelVars(tousTrees, n1,"commutativiteSelection  "+i++);

			for(Tree n:joinAssociativite(n1)) //association jointure
				if(notAlreadyAdded( tousTrees,n)) tousTrees.add(n);
		}

		for(Tree n1: assocJoinRule){
			assoSelVars(tousTrees, n1,"joinAssociativite"+i++);

			for(Tree n: commutativiteSelection(n1))// commutativiteSelection
				if(notAlreadyAdded( tousTrees,n))  tousTrees.add(n);

		}
		this.allVariants= tousTrees;
	}

	private void joinCumitVars(List<Tree> tousTrees, Tree n1) {
		for(Tree n: jointureCommutativite(n1)){ //  jointure
			if(notAlreadyAdded( tousTrees,n))   tousTrees.add(n);
		}
		for(Tree n: commutativiteSelection(n1))// commutativiteSelection
			if(notAlreadyAdded( tousTrees,n))  tousTrees.add(n);

		for(Tree n:joinAssociativite(n1)) //association jointure
			if(notAlreadyAdded( tousTrees,n)) tousTrees.add(n);
	}

	private void assoSelVars(List<Tree> tousTrees, Tree n1,String request) {
		for(Tree n: selectionConjonctive(n1)){ //  selection variant
			if(notAlreadyAdded( tousTrees,n))   tousTrees.add(n);
		}
		for(Tree n: jointureCommutativite(n1)){ //  jointure
			if(notAlreadyAdded( tousTrees,n))   tousTrees.add(n);
		}
		for(Tree n:onlyOrSelectionVariants(n1))//Or selection
			if(notAlreadyAdded( tousTrees,n))  tousTrees.add(n);

	}


	//// -------------------------  Jointurr --------------------------------------------------
	private  List<Tree> jointureCommutativite(Tree tree){
		List<Tree> join1stVariantList=new ArrayList<>();
		join1stVariantList.add(tree);
		int count= Tree.joinCount(tree.getRoot());
		//System.out.println("nbr join ::::::::::::: "+count);
		for(int i=0;i<count;i++)
			for (int j=1;j<=count;j++){
				Node tmp=joinCommutateur(Tree.cloneTree(tree.getRoot()),i,0,j);
				Tree ar=new Tree();
				ar.setRoot(tmp);
				if(notAlreadyAdded(join1stVariantList, ar))join1stVariantList.add(ar);
			}

		return join1stVariantList;
	}


	public static boolean notAlreadyAdded(Collection<Tree> L, Tree n){
		for (Tree t : L) {
			if (Tree.sameTree(t.getRoot(), n.getRoot())) {
				return false;
			}
		}
		return true;
	}


	private Node joinCommutateur(Node a, int initial, int counter, int maxCount){
		if (a == null) {
			return null;
		}
		if(a.getData().contains("⋈")){
			if(counter>=initial) {
				Node tmp=a.getLeft();
				a.setLeft(a.getRight());
				a.setRight(tmp);
				List<String> attrs=new ArrayList<>();
				Pattern p=Pattern.compile("\\w+");
				Matcher matcher = p.matcher(a.getData());
				while (matcher.find()) attrs.add(matcher.group());
				a.setData("⋈"+attrs.get(2)+"."+attrs.get(3)+"="+attrs.get(0)+"."+attrs.get(1));
			}
			counter++;
		}
		if(counter==maxCount) return a;
		if(a.getLeft()!=null) joinCommutateur(a.getLeft(),initial,counter, maxCount);
		if (a.getRight()!=null) joinCommutateur(a.getRight(),initial,counter, maxCount);
		return a;
	}




/// ------------------------------- Selection --------------------------------------------------


	private List<Tree> selectionConjonctive(Tree tree){
		List<Tree> onlySelectionVariantsList=new ArrayList<>();
		onlySelectionVariantsList.add(tree);
		for(int i=0;i<2;i++){
			for (int j=0;j<=2;j++){
				Node tmp=andUnionSelection1(Tree.cloneTree(tree.getRoot()),i,new int[]{0},j);
				Node tmp2=andUnionSelection2(Tree.cloneTree(tree.getRoot()),i,new int[]{0},j);
				Tree ar1,ar2;
				ar1=new Tree();
				ar2=new Tree();

				ar1.setRoot(tmp);
				ar2.setRoot(tmp2);
				if(notAlreadyAdded(onlySelectionVariantsList, ar1))onlySelectionVariantsList.add(ar1);
				if(notAlreadyAdded(onlySelectionVariantsList, ar2))onlySelectionVariantsList.add(ar2);

			}
		}

		return onlySelectionVariantsList;
	}
	private Node andUnionSelection1(Node a , int initial, int[] counter, int maxCount){
		a=andUnionSelection(a,initial,counter);
		if(counter[0]==maxCount) return a;
		if (a.getRight()!=null) andUnionSelection1(a.getRight(),initial,counter, maxCount);
		if(a.getLeft()!=null) andUnionSelection1(a.getLeft(),initial,counter, maxCount);
		return a;
	}
	private Node andUnionSelection(Node a , int initial, int[] counter){
		if (a == null) return null;
		if(a.getData().contains("σ") && a.getLeft().getData().contains("σ") ){

			if(counter[0]>=initial) {
				Node tmp = a.getLeft().getLeft();
				a.setData(a.getData() + " &&\n " + a.getLeft().getData());
				a.setLeft(tmp);
			}
			counter[0]++;
		}
		return a;
	}

	private Node andUnionSelection2(Node a , int initial, int[] counter, int maxCount){
		a=andUnionSelection(a,initial,counter);
		if(counter[0]==maxCount) return a;

		if(a.getLeft()!=null) andUnionSelection2(a.getLeft(),initial,counter, maxCount);
		if (a.getRight()!=null) andUnionSelection2(a.getRight(),initial,counter, maxCount);
		return a;

	}

/// -------------------------------------- Or selection ------------------------------

	private List<Tree> onlyOrSelectionVariants(Tree tree){
		List<Tree> onlyOrSelectionVariantsList=new ArrayList<>();
		onlyOrSelectionVariantsList.add(tree);
		for(int i=0;i<2;i++){
			for (int j=0;j<=2;j++){
				Node tmp=orUnionSelection(Tree.cloneTree(tree.getRoot()),i,new int[]{0},j);
				Tree ar=new Tree();
				ar.setRoot(tmp);

				if(notAlreadyAdded(onlyOrSelectionVariantsList, ar))onlyOrSelectionVariantsList.add(ar);
			}
		}

		return onlyOrSelectionVariantsList;
	}



	private Node orUnionSelection(Node a, int initial, int[] counter, int maxCount){
		if (a == null) {
			return null;
		}
		if(a.getData().equals("OR") ){
			if(counter[0]>=initial) {
				Node left = a.getLeft();//concatSelection

				String nodeContent = concatSelection(a.getLeft(), "") + " OR " + concatSelection(a.getRight(), "");
				a.setData(nodeContent);
				a.setLeft(getTable(left));
				a.setRight(null);
			}
			counter[0]++;
		}
		if(counter[0]==maxCount) return a;
		if (a.getRight()!=null) orUnionSelection(a.getRight(), initial, counter, maxCount);
		if(a.getLeft()!=null) orUnionSelection(a.getLeft(), initial, counter, maxCount);
		return a;

	}




	private String concatSelection(Node a, String str){
		if (a == null) {
			return "";
		}
		if(a.getData().contains("σ") ){
			return a.getData()+concatSelection(a.getLeft(),str);
		}
		return str;
	}
	private Node getTable(Node a){
		if (a == null) {
			return null;
		}
		if(a.getLeft()==null&&a.getRight()==null) return a;
		if(a.getLeft()!=null) return getTable(a.getLeft());
		if (a.getRight()!=null) return getTable(a.getRight());
		return a;
	}


///-------------------------------------- Commutativité de la sélection ----------------------------------------------------------


	private List<Tree> commutativiteSelection(Tree tree){
		List<Tree> commutativiteSelection=new ArrayList<>();
		commutativiteSelection.add(tree);
		for(int i=0;i<2;i++){
			for (int j=0;j<=2;j++){
				Node tmp=commuterSel1(Tree.cloneTree(tree.getRoot()),i,new int[]{0},j);
				Node tmp2=commuterSel2(Tree.cloneTree(tree.getRoot()),i,new int[]{0},j);
				Tree ar1=new Tree();
				Tree ar2=new Tree();
				ar1.setRoot(tmp);
				ar2.setRoot(tmp2);
				if(notAlreadyAdded(commutativiteSelection, ar1))commutativiteSelection.add(ar1);
				if(notAlreadyAdded(commutativiteSelection, ar2))commutativiteSelection.add(ar2);

			}
		}


		return commutativiteSelection;
	}


	private Node commuterSel1(Node a , int initial, int[] counter, int maxCount){
		if (a == null) {
			return null;
		}

		if(a.getData().contains("σ") && a.getLeft().getData().contains("σ") ){
			if(counter[0]>=initial) {
				Node tmp = a.getLeft().getLeft();
				String aux=a.getData();
				System.out.println("1111111111"+aux);
				a.setData(a.getLeft().getData());
				a.getLeft().setData(aux);
				a.getLeft().setLeft(tmp);
			}
			counter[0]++;
		}
		if(counter[0]==maxCount) return a;
		if (a.getRight()!=null) commuterSel1(a.getRight(),initial,counter, maxCount);
		if(a.getLeft()!=null) commuterSel1(a.getLeft(),initial,counter, maxCount);
		return a;

	}
	private Node commuterSel2(Node a , int initial, int[] counter, int maxCount){
		if (a == null) {
			return null;
		}

		if(a.getData().contains("σ") && a.getLeft().getData().contains("σ") ){

			if(counter[0]>=initial) {
				Node tmp = a.getLeft().getLeft();
				String aux=a.getData();
				System.out.println("222222222"+aux);
				a.setData(a.getLeft().getData());
				a.getLeft().setData(aux);
				a.getLeft().setLeft(tmp);
			}
			counter[0]++;
		}
		if(counter[0]==maxCount) return a;

		if(a.getLeft()!=null) commuterSel2(a.getLeft(),initial,counter, maxCount);
		if (a.getRight()!=null) commuterSel2(a.getRight(),initial,counter, maxCount);
		return a;
	}

///------------------------------------------- Associativité de la jointure (JA)-----------------------------

	private  List<Tree> joinAssociativite(Tree tree){
		List<Tree> joinAssocitivite=new ArrayList<>();
		joinAssocitivite.add(tree);
		int count= Tree.joinCount(tree.getRoot());
		//System.out.println("nbr join ::::::::::::: "+count);
		for(int i=0;i<count;i++)
			for (int j=1;j<=count;j++){
				Node tmp=joinAssoc(Tree.cloneTree(tree.getRoot()),i,0,j);
				Tree ar=new Tree();
				ar.setRoot(tmp);
				if(notAlreadyAdded(joinAssocitivite, ar))joinAssocitivite.add(ar);
			}

		return joinAssocitivite;
	}


	private Node joinAssoc(Node a, int initial, int counter, int maxCount){
		if (a == null) {
			return null;
		}
		if(a.getData().contains("⋈") && a.getLeft()!=null && a.getLeft().getData().contains("⋈")){
			if(counter>=initial) {
				Node tmp=a.getLeft();
				Node tmp1=a.getRight();
				a.setLeft(tmp.getLeft());
				Node sw=switchjoins(tmp);
				sw.setRight(tmp1);
				a.setRight(sw);
			}
			counter++;
		}
		if(counter==maxCount) return a;
		if(a.getLeft()!=null) joinAssoc(a.getLeft(),initial,counter, maxCount);
		if (a.getRight()!=null) joinAssoc(a.getRight(),initial,counter, maxCount);
		return a;
	}

	private Node switchjoins(Node A){
		Node aux=A.getLeft();
		A.setLeft(A.getRight());
		A.setRight(aux);
		return A;
	}
	public static Node switchjoinselection(Node a){
		if (a == null) {
			return null;
		}




		if(a.getLeft()!=null) switchjoinselection(a.getLeft());
		if (a.getRight()!=null) switchjoinselection(a.getRight());
		return a;
	}

	public static void main(String[] args) {

	/*JFrame frame = new JFrame();frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTextField script =new JTextField(100);
		JButton execute=new JButton("Execute");
		execute.addActionListener(evt -> {
			Translator t=new Translator(script.getText());
			Transformer tr=new  Transformer(t.getFirstTree());
			int h=Visualizer.drawListOfTrees(tr.getAllVariants(),frame);
			frame.setSize(new Dimension(frame.getWidth(),h));
			frame.pack();
			frame.setVisible(true);
		});
		JPanel p=new JPanel();
		p.add(script);
		p.add(execute);
		frame.add(p,BorderLayout.NORTH);
		// Translator t=  new Translator("select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' AND A.z='3' and C.c='3' OR A.a<'7' AND A.a>'89' OR C.e='45' OR C.j='35' AND B.b=C.b");
		//Translator t=new Translator("Select t.t From T1,T2,T3 where T1.a=T2.a AND T2.b=T3.b");
		//  Transformer tr=new  Transformer(t.getFirstTree());
		//SELECT nom,age,prenom FROM Client,Voiture,Location WHERE Client.id_client=Location.id_client AND Voiture.id_voiture=Location.id_voiture AND Client.age='40' AND Voiture.km='1000' AND Voiture.marque='Mercedes'
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);*/
		//Node a=Transformer.switchjoinselection();
		Translator tr=new Translator("SELECT CLIENT.ID,CLIENT.NOM,PROJET.TITRE FROM CLIENT,PROJET WHERE CLIENT.ID=PROJET.ID AND PROJET.TITRE='VAL'");
		tr.getFirstTree().showTree();
		System.out.println("\n\n________________________==================__________________");
		Tree tmp=new Tree();
		tmp.setRoot(Transformer.switchjoinselection(tr.getFirstTree().getRoot()));
		tmp.showTree();

	}
}

