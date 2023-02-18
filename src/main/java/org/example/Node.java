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
		System.out.println("");
		for (esp = 0; esp < Niv; esp++) System.out.print("\t");

		System.out.print(node.getData());

		//print the left child
		affch(node.getLeft(),Niv + 1);


	}
}