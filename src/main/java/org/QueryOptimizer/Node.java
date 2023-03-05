
package org.QueryOptimizer;

public class Node {

	private String data;
	private Node left;
	private Node right;

	public Node(String data) {
		this.data = data;
		left =null;
		right =null;
	}

	public Node(String data, Node left, Node right) {
		this.data = data;
		this.left = left;
		this.right = right;
	}

	public void setData(String data) {
		this.data = data;
	}








	public String getData() {
		return data;
	}

	public Node getLeft() {
		return left;
	}

	public Node getRight() {
		return right;
	}


	public static void show(Node node, int Niv)
	{
		int esp;
		if (node==null)return;
		show(node.getRight(), Niv + 1);
		System.out.println();
		for (esp = 0; esp < Niv; esp++) System.out.print("\t");

		System.out.print(node.getData());

		show(node.getLeft(),Niv + 1);


	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public void setRight(Node right) {
		this.right = right;
	}








}
