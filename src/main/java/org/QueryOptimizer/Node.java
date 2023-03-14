
package org.QueryOptimizer;

import java.util.Objects;

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

	public boolean isLeaf(){
		return this.left==null && this.right==null;
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


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Node node = (Node) o;
		return isEqual(this, node);
	}

	private boolean isEqual(Node node1, Node node2) {
		if (node1 == null && node2 == null) {
			return true;
		}
		if (node1 == null || node2 == null) {
			return false;
		}
		if (!Objects.equals(node1.getData(), node2.getData())) {
			return false;
		}
		return isEqual(node1.getLeft(), node2.getLeft()) &&
				isEqual(node1.getRight(), node2.getRight());
	}


	@Override
	public int hashCode() {
		return Objects.hash(data, left, right);
	}





}
