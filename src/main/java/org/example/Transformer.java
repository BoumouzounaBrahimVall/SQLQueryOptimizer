package org.example;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public final class Transformer {

	public List<Tree> joinRule; // Commutativity the joins (JC)
	public List<Tree> selectionRule; // Eclatement d'une sélection conjonctive (SE)
	public List<Tree> orSelectionRule; //
	public List<Tree> regleCommutSelection;//commutativiteSelection
	public List<Tree> assocJoinRule; //association jointure
	private final Tree firstTree;
	private List<Tree> allVariants;



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
				if(notAlreadyAdded(tousTrees,n)) tousTrees.add(n);
			}
			for(Tree n: onlyOrSelectionVariants(n1)){ //  OR selection of joins
				if(notAlreadyAdded(tousTrees,n))  tousTrees.add(n);
			}
			for(Tree n: commutativiteSelection(n1))// commutativiteSelection
				if(notAlreadyAdded(tousTrees,n)) tousTrees.add(n);

			for(Tree n:joinAssociativite(n1)) //association jointure
				if(notAlreadyAdded(tousTrees,n)) tousTrees.add(n);
		}

		for(Tree n1: selectionRule){
			for(Tree n: onlyOrSelectionVariants(n1)){ //  OR selection variant of sel variants
				if(notAlreadyAdded(tousTrees,n))  tousTrees.add(n);
			}
			joinCumitVars(tousTrees, n1);
		}

		for(Tree n1: orSelectionRule){
			for(Tree n: selectionConjonctive(n1)){ //   selection variant
				if(notAlreadyAdded(tousTrees,n))  tousTrees.add(n);
			}
			joinCumitVars(tousTrees, n1);
		}


		for(Tree n1: regleCommutSelection){
			assoSelVars(tousTrees, n1);

			for(Tree n:joinAssociativite(n1)) //association jointure
				if(notAlreadyAdded(tousTrees,n)) tousTrees.add(n);
		}

		for(Tree n1: assocJoinRule){
			assoSelVars(tousTrees, n1);

			for(Tree n: commutativiteSelection(n1))// commutativiteSelection
				if(notAlreadyAdded(tousTrees,n)) tousTrees.add(n);

		}
		this.allVariants= tousTrees;
	}

	private void joinCumitVars(List<Tree> tousTrees, Tree n1) {
		for(Tree n: jointureCommutativite(n1)){ //  jointure
			if(notAlreadyAdded(tousTrees,n))   tousTrees.add(n);
		}
		for(Tree n: commutativiteSelection(n1))// commutativiteSelection
			if(notAlreadyAdded(tousTrees,n)) tousTrees.add(n);


		for(Tree n:joinAssociativite(n1)) //association jointure
			if(notAlreadyAdded(tousTrees,n)) tousTrees.add(n);
	}

	private void assoSelVars(List<Tree> tousTrees, Tree n1) {
		for(Tree n: selectionConjonctive(n1)){ //  selection variant
			if(notAlreadyAdded(tousTrees,n))  tousTrees.add(n);
		}
		for(Tree n: jointureCommutativite(n1)){ //  jointure
			if(notAlreadyAdded(tousTrees,n))  tousTrees.add(n);
		}
		for(Tree n:onlyOrSelectionVariants(n1))//Or selection
			if(notAlreadyAdded(tousTrees,n))  tousTrees.add(n);
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

	public  boolean notAlreadyAdded(List<Tree> L, Tree n){
		for (Tree nd:L)
			if(Tree.sameTree(nd.getRoot(),n.getRoot())== Tree.nbrNodes(n.getRoot())) return false;
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

}

