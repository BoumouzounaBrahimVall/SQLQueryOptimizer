package org.example;

import java.util.ArrayList;
import java.util.List;

public class Transformer {
	public Transformer() {}

	public static boolean notAlreadyAdded(List<Node> L,Node n){
		for (Node nd:L)
			if(Node.sameTree(nd,n)==Node.nbrNodes(n)) return false;
		return true;
	}

	public static List<Node> onlyJoin1stVariants(Node mainRoot){
		List<Node> join1stVariantList=new ArrayList<>();
		join1stVariantList.add(mainRoot);
		int count=Node.joinCount(mainRoot);
		for(int i=0;i<count;i++)
			for (int j=1;j<=count;j++){
				Node tmp=joinSwitcher(Node.cloneTree(mainRoot),i,0,j);
				if(notAlreadyAdded(join1stVariantList, tmp))join1stVariantList.add(tmp);
			}
		return join1stVariantList;
	}

	public static List<Node> onlySelectionVariants(Node mainRoot){
		List<Node> onlySelectionVariantsList=new ArrayList<>();
		onlySelectionVariantsList.add(mainRoot);
		for(int i=0;i<2;i++){
			for (int j=0;j<=2;j++){
				Node tmp=andUnionSelection1(Node.cloneTree(mainRoot),i,new int[]{0},j);
				Node tmp2=andUnionSelection2(Node.cloneTree(mainRoot),i,new int[]{0},j);
				if(notAlreadyAdded(onlySelectionVariantsList, tmp))onlySelectionVariantsList.add(tmp);
				if(notAlreadyAdded(onlySelectionVariantsList, tmp2))onlySelectionVariantsList.add(tmp2);

			}
		}
	return onlySelectionVariantsList;
	}
	public static List<Node> onlyOrSelectionVariants(Node mainRoot){
		List<Node> onlyOrSelectionVariantsList=new ArrayList<>();
		onlyOrSelectionVariantsList.add(mainRoot);
		for(int i=0;i<2;i++){
			for (int j=0;j<=2;j++){
				Node tmp=orUnionSelection(Node.cloneTree(mainRoot),i,new int[]{0},j);
				if(notAlreadyAdded(onlyOrSelectionVariantsList, tmp))onlyOrSelectionVariantsList.add(tmp);
			}
		}
		return onlyOrSelectionVariantsList;
	}

	public static List<Node> allVariants(Node mainRoot){
		List<Node> joinVlist=onlyJoin1stVariants(mainRoot);
		List<Node> sel1Vlist=onlySelectionVariants(mainRoot);
		List<Node> sel2Vlist=onlyOrSelectionVariants(mainRoot);
		List<Node> joinAndSel =new ArrayList<>();
		joinVlist.remove(0);// delete main
		sel1Vlist.remove(0);
		sel2Vlist.remove(0);
		joinAndSel.add(mainRoot);
		// treat joins
		for(Node nj:joinVlist){
			for(Node n: onlySelectionVariants(nj)){ //  selection of join variants
				if(notAlreadyAdded(joinAndSel,n)) joinAndSel.add(n);
			}
			for(Node n: onlyOrSelectionVariants(nj)){ //  OR selection of joins
				if(notAlreadyAdded(joinAndSel,n)) joinAndSel.add(n);
			}
		}
		//treat selection
		for(Node n1: sel1Vlist){
			for(Node n: onlyOrSelectionVariants(n1)){ //  OR selection variant of sel variants
				if(notAlreadyAdded(joinAndSel,n)) joinAndSel.add(n);
			}
			for(Node n: onlyJoin1stVariants(n1)){ //  OR selection variant of sel variants
				if(notAlreadyAdded(joinAndSel,n)) joinAndSel.add(n);
			}
		}

		//treat OR selection
		for(Node n1: sel2Vlist){
			for(Node n: onlySelectionVariants(n1)){ //  OR selection variant of sel variants
				if(notAlreadyAdded(joinAndSel,n)) joinAndSel.add(n);
			}
			for(Node n: onlyJoin1stVariants(n1)){ //  OR selection variant of sel variants
				if(notAlreadyAdded(joinAndSel,n)) joinAndSel.add(n);
			}
		}
		return joinAndSel;
	}

	public static void main(String[] args) {

		String query = "SELECT nom,Titre FROM Employee,Projet,Traveaux WHERE Employee.eid=Traveaux.eid AND Projet.pid=Traveaux.pid and Projet.b > '2'";
		String query2="SELECT nom, age FROM personnes, clients WHERE personnes.id=clients.id AND personnes.ville = Paris AND clients.age<50 OR personnes.ville=Cabablanca AND clients.age> 20 ";
		String query3="SELECT Ename Titre FROM Employe,Projet,Traveaux WHERE Budget>250 AND Employe.Eid=Traveaux.Eid AND Projet.Pid=Traveaux.Pid";
		Translator parsedTranslator = new Translator("select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' AND A.z='3' and C.c='3' AND B.b>'1' OR A.a<'7' AND A.a>'89' OR C.e='45' AND B.b=C.b"); //"SELECT CLIENT.ID FROM CLIENT WHERE CLIENT.ID='12'" select A.a, B.b from A,B,C where A.a=B.b AND A.a='2' OR C.c='3' AND B.b>'1' OR A.a<'7' AND B.b=C.b
		parsedTranslator.parseQuery();

		new NodeGUI(allVariants(parsedTranslator.getTree()));
	}// AND A.a='2' and C.c='3' AND B.b>'1' OR A.a<'7' AND A.a>'89'




	private static Node joinSwitcher(Node a,int initial,int counter,int maxCount){
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
		if(a.getLeft()!=null) joinSwitcher(a.getLeft(),initial,counter, maxCount);
		if (a.getRight()!=null) joinSwitcher(a.getRight(),initial,counter, maxCount);
		return a;
	}
	// TODO needs more work 1 and 2
	private static Node andUnionSelection1(Node a ,int initial,int[] counter,int maxCount){
		if (a == null) {
			return null;
		}
		System.out.println(a.getData());
		if(a.getData().contains("σ") && a.getLeft().getData().contains("σ") ){
			System.out.println("la je suis"+counter[0]);
			if(counter[0]>=initial) {
				Node tmp = a.getLeft().getLeft();
				a.setData(a.getData() + " &&\n " + a.getLeft().getData());
				a.setLeft(tmp);
			}
			counter[0]++;
		}else System.out.println("la pas");
		if(counter[0]==maxCount) return a;
		if (a.getRight()!=null) andUnionSelection1(a.getRight(),initial,counter, maxCount);
		if(a.getLeft()!=null) andUnionSelection1(a.getLeft(),initial,counter, maxCount);
		return a;

	}
	private static Node andUnionSelection2(Node a ,int initial,int[] counter,int maxCount){
		if (a == null) {
			return null;
		}
		System.out.println(a.getData());
		if(a.getData().contains("σ") && a.getLeft().getData().contains("σ") ){
			System.out.println("la je suis"+counter[0]);
			if(counter[0]>=initial) {
				Node tmp = a.getLeft().getLeft();
				a.setData(a.getData() + " &&\n " + a.getLeft().getData());
				a.setLeft(tmp);
			}
			counter[0]++;
		}else System.out.println("la pas");
		if(counter[0]==maxCount) return a;
		if(a.getLeft()!=null) andUnionSelection2(a.getLeft(),initial,counter, maxCount);
		if (a.getRight()!=null) andUnionSelection2(a.getRight(),initial,counter, maxCount);
		return a;

	}
	private static Node orUnionSelection(Node a,int initial,int[] counter,int maxCount){
		if (a == null) {
			return null;
		}
		if(a.getData().equals("OR") ){
			if(counter[0]>=initial) {
				Node left = a.getLeft();//concatSelection
				System.out.println("left : " + concatSelection(left, ""));
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
	private static String concatSelection(Node a,String str){
		if (a == null) {
			return "";
		}
		if(a.getData().contains("σ") ){
			return a.getData()+concatSelection(a.getLeft(),str);
		}
		return str;
	}
	private static Node getTable(Node a){
		if (a == null) {
			return null;
		}
		if(a.getLeft()==null&&a.getRight()==null) return a;
		if(a.getLeft()!=null) return getTable(a.getLeft());
		if (a.getRight()!=null) return getTable(a.getRight());
		return a;
	}

}







//JPanel p=Translator.DrawTree(parsedTranslator.getTree());

//Node.affch(parsedTranslator.getTree(),0);
//Translator.DrawTree(join1stVariant(parsedTranslator.getTree()));
//	allVariants(parsedTranslator.getTree()).forEach(Translator::DrawTree);
// onlyOrSelectionVariants(parsedTranslator.getTree()).forEach(Translator::DrawTree);
//onlyJoin1stVariants(parsedTranslator.getTree()).forEach(Translator::DrawTree);
//onlySelectionVariants(parsedTranslator.getTree()).forEach(n->{Node.affch(n,0 );System.out.println("-----------------------------");});
//	Translator.DrawTree(orUnionSelection(parsedTranslator.getTree()));
//System.out.println("vini :"+Node.joinCount(parsedTranslator.getTree()));