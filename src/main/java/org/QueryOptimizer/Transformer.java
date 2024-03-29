package org.QueryOptimizer;

import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Transformer {

	public Set<Node> joinRule; // Commutativity the joins (JC)
	public Set<Node> selectionRule; // Emplacement of (SE)
	public Set<Node> orSelectionRule; //
	public Set<Node> regleCommutSelection;//commutativiteSelection
	public Set<Node> assocJoinRule; //association jointure
	private final Node firstTree;
	private Set<Node> allVariants;
	public   Map<Node,String> reglenames;
	String jointureCommutativite="T1 ⋈ T2 = T2 ⋈ T1";
	String selectionConjonctive="σ e1 ET e2 (T) = σ e1 (σ e2 (T))";
	String onlyOrSelectionVariants="Or selection";
	String commutativiteSelection="σ e1 (σ e2 (T)) = σ e2 (σ e1 (T))";
	String joinAssociativite="T1 ⋈ (T2 ⋈ T3) = (T1 ⋈ T2) ⋈ T3";
	public Transformer(Node tree)
	{
		reglenames=new HashMap<>();
		this.firstTree=tree;
		joinRule =jointureCommutativite(firstTree);
		selectionRule = selectionConjunctive(firstTree);
		orSelectionRule =onlyOrSelectionVariants(firstTree);
		regleCommutSelection=commutativiteSelection(firstTree);
		assocJoinRule =joinAssociativite(firstTree);
		buildAllVariants();


	}

	public Set<Node> getAllVariants() {
		return allVariants;
	}

	private void buildAllVariants(){
		Set<Node> tousTrees = new HashSet<>();
		joinRule.remove(0);// delete main
		selectionRule.remove(0);
		orSelectionRule.remove(0);
		regleCommutSelection.remove(0);
		tousTrees.add(firstTree);

		for(Node n1: joinRule){
			//  selection of join variants
			Set<Node> tmp= selectionConjunctive(n1);
			tousTrees.addAll(tmp);
			for (Node node : tmp) {
				reglenames.put(node, jointureCommutativite+" -- "+selectionConjonctive );
			}

			//  OR selection of joins
			tmp=onlyOrSelectionVariants(n1);
			tousTrees.addAll(tmp);
			for (Node node : tmp) {
				reglenames.put(node, jointureCommutativite+" -- "+onlyOrSelectionVariants );
			}
			// commutativitySelection
			tmp=commutativiteSelection(n1);
			tousTrees.addAll(tmp);
			for (Node node : tmp) {
				reglenames.put(node, jointureCommutativite+" -- "+commutativiteSelection );
			}

			//association jointures
			tmp=joinAssociativite(n1);
			tousTrees.addAll(tmp);
			for (Node node : tmp) {
				reglenames.put(node, jointureCommutativite+" -- "+joinAssociativite );
			}
		}

		for(Node n1: selectionRule){
			//  OR selection variant of sel variants
			Set<Node> tmp;
			tmp=onlyOrSelectionVariants(n1);
			tousTrees.addAll(tmp);
			for(Node n2: tmp)
				reglenames.put(n2, selectionConjonctive+" -- "+onlyOrSelectionVariants  );
			joinCumitVars(tousTrees, n1,selectionConjonctive);
		}

		for(Node n1: orSelectionRule){
			//   selection variant
			Set<Node> tmp;
			tmp= selectionConjunctive(n1);
			tousTrees.addAll(tmp);
			for(Node n2: tmp)
				reglenames.put(n2, onlyOrSelectionVariants+" -- "+selectionConjonctive  );
			joinCumitVars(tousTrees, n1,onlyOrSelectionVariants);
		}


		for(Node n1: regleCommutSelection){
			assoSelVars(tousTrees, n1,commutativiteSelection   );
			//association jointures
			tousTrees.addAll(joinAssociativite(n1));
			for(Node n2: joinAssociativite(n1))
				reglenames.put(n2, commutativiteSelection+" -- "+joinAssociativite );
			for(Node n2: joinAssociativite(n1))
				reglenames.put(n2, commutativiteSelection+" -- "+joinAssociativite  );
		}

		for(Node n1: assocJoinRule){
			assoSelVars(tousTrees, n1,joinAssociativite );
			// commutativitySelection
			tousTrees.addAll(commutativiteSelection(n1));
			for(Node n2: commutativiteSelection(n1))
				reglenames.put(n2, joinAssociativite+" -- "+commutativiteSelection  );

		}
		this.allVariants= tousTrees;
	}

	private void joinCumitVars(Set<Node> tousTrees, Node n1,String request) {
		//  jointures
		Set<Node> tmp;
		tmp=jointureCommutativite(n1);
		tousTrees.addAll(tmp);
		for(Node n2: tmp)
			reglenames.put(n2, request+" -- "+jointureCommutativite  );
		// commutativitySelection
		tmp=commutativiteSelection(n1);
		tousTrees.addAll(tmp);
		for(Node n2: tmp)
			reglenames.put(n2, request+" -- "+commutativiteSelection );
		//association jointures
		tmp=joinAssociativite(n1);
		tousTrees.addAll(tmp);
		for(Node n2: tmp)
			reglenames.put(n2, request+" -- "+joinAssociativite  );
	}

	private void assoSelVars(Set<Node> tousTrees, Node n1,String request) {
		//  selection variant
		Set<Node> tmp;
		tmp= selectionConjunctive(n1);
		tousTrees.addAll(tmp);
		for(Node n2: tmp)
			reglenames.put(n2, request+" -- "+selectionConjonctive  );
		//  join
		tmp=jointureCommutativite(n1);
		tousTrees.addAll(tmp);
		for(Node n2: tmp)
			reglenames.put(n2, request+" -- "+jointureCommutativite  );
		//Or selection
		tmp=onlyOrSelectionVariants(n1);
		tousTrees.addAll(tmp);
for(Node n2: tmp)
			reglenames.put(n2, request+" -- "+onlyOrSelectionVariants  );

	}


	////   Join
	private  Set<Node> jointureCommutativite(Node tree){
		Set<Node> join1stVariantList=new HashSet<>();
		join1stVariantList.add(tree);
		int count= Node.joinCount(tree);
		for(int i=0;i<count;i++)
			for (int j=1;j<=count;j++){
				Node tmp= joinCommutator(Node.cloneTree(tree),i,0,j);
				join1stVariantList.add(tmp);
			}

		return join1stVariantList;
	}



	private Node joinCommutator(Node a, int initial, int counter, int maxCount){
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
		if(a.getLeft()!=null) joinCommutator(a.getLeft(),initial,counter, maxCount);
		if (a.getRight()!=null) joinCommutator(a.getRight(),initial,counter, maxCount);
		return a;
	}




///  Selection


	private Set<Node> selectionConjunctive(Node tree){
		Set<Node> onlySelectionVariantsList=new HashSet<>();
		onlySelectionVariantsList.add(tree);
		for(int i=0;i<2;i++){
			for (int j=0;j<=2;j++){
				Node tmp=andUnionSelection1(Node.cloneTree(tree),i,new int[]{0},j);
				Node tmp2=andUnionSelection2(Node.cloneTree(tree),i,new int[]{0},j);
				onlySelectionVariantsList.add(tmp);
				onlySelectionVariantsList.add(tmp2);

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

///  Or selection

	private Set<Node> onlyOrSelectionVariants(Node tree){
		Set<Node> onlyOrSelectionVariantsList=new HashSet<>();
		onlyOrSelectionVariantsList.add(tree);
		for(int i=0;i<2;i++){
			for (int j=0;j<=2;j++){
				Node tmp=orUnionSelection(Node.cloneTree(tree),i,new int[]{0},j);
				onlyOrSelectionVariantsList.add(tmp);
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
				a.setType(Node.S);
				String nodeContent = concatSelection(a.getLeft()) + " OR " + concatSelection(a.getRight());
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




	public String concatSelection(Node node) {
		if (node == null) {
			return "";
		}
		String result = "";
		if (node.getData().contains("σ")) {
			result += node.getData();
		}
		result += concatSelection(node.getLeft());
		result += concatSelection(node.getRight());
		return result;
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


///Commutativité de la sélection


	private Set<Node> commutativiteSelection(Node tree){
		Set<Node> commutativiteSelection=new HashSet<>();
		commutativiteSelection.add(tree);
		for(int i=0;i<2;i++){
			for (int j=0;j<=2;j++){
				Node tmp=commuterSel1(Node.cloneTree(tree),i,new int[]{0},j);
				Node tmp2=commuterSel2(Node.cloneTree(tree),i,new int[]{0},j);
				commutativiteSelection.add(tmp);
				commutativiteSelection.add(tmp2);

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

/// Associativité de la jointure

	private  Set<Node> joinAssociativite(Node tree){
		Set<Node> joinAssocitivite=new HashSet<>();
		joinAssocitivite.add(tree);
		int count= Node.joinCount(tree);
		for(int i=0;i<count;i++)
			for (int j=1;j<=count;j++){
				Node tmp=joinAssoc(Node.cloneTree(tree),i,0,j);
				joinAssocitivite.add(tmp);
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

