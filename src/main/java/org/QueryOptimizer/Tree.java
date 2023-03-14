package org.QueryOptimizer;



public class Tree {

	private Node root;

	public boolean isEmpty()
	{
		return root ==null;
	}

	public Tree() {
		root =null;
	}


	public void showTree()
	{
		Node.show(root, 0);
	}


	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}


	/*
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
*/









}



