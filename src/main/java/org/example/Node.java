package org.example;

import java.util.*;

/**
 * 
 */
public  class Node {

	private String data;
	private Node left ;
	private Node right;

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public Node(String S) {
		this.data=S;
		left=right=null;}



	public Node getLeft() {
		return left;
	}
	public void setLeft(Node left) {
		this.left = left;
	}
	public Node getRight() {
		return right;
	}
	public void setRight(Node right) {
		this.right = right;
	}
	public static void affch(Node node, int Niv)
	{
		int esp; //for printing spaces
		if (node==null)return;

		//print the right child
		affch(node.getRight(), Niv + 1);
		System.out.println();
		for (esp = 0; esp < Niv; esp++) System.out.print("\t");

		System.out.print(node.getData());

		//print the left child
		affch(node.getLeft(),Niv + 1);

	}
	public static int nbrNodes(Node n){
		if(n==null) return 0;
		return 1+nbrNodes(n.getLeft()) + nbrNodes(n.getRight());
	}
	public static int sameTree(Node root1, Node root2) {
		// If both trees are null, they are the same
		if (root1 == null && root2 == null) {
			return 0;
		}
		// If only one tree is null, they are different
		if (root1 == null || root2 == null) {
			return 1;
		}
		// If the values of the current nodes are different, they are different
		if (!root1.getData().equals(root2.getData())) {
			return 1;
		}
		// Recursively compare the left and right subtrees
		return 1+sameTree(root1.getLeft(), root2.getLeft()) + sameTree(root1.getRight(), root2.getRight());
	}
	public static int joinCount(Node n){
		if(n==null) return 0;
		if(n.getData().contains("⋈"))  return 1;
		return 1+ joinCount(n.getLeft()) + joinCount(n.getRight());
	}
	public static Node cloneTree(Node root) {
		if (root == null) {
			return null;
		}
		Node newNode =new Node(root.getData());
		newNode.setLeft( cloneTree(root.getLeft()) );
		newNode.setRight( cloneTree(root.getRight()) );
		return newNode;
	}
	public static int depth(Node node) {
		if (node == null) {
			return 0;
		} else {
			int hauteurGauche = depth(node.getLeft());
			int hauteurDroit = depth(node.getRight());
			return 1 + Math.max(hauteurGauche, hauteurDroit);
		}
	}
}