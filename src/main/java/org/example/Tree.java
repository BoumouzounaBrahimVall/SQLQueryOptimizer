package org.example;



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

	public static int joinCount( Node racine){
		if(racine==null) return 0;
		if(racine.getData().contains("⋈"))  return 1;
		return 1+ joinCount(racine.getLeft()) + joinCount(racine.getRight());
	}


	public static Node cloneTree(Node root) {
		if (root == null) {
			return null;
		}
		Node newNode =new Node(root.getData());
		newNode.setLeft(cloneTree(root.getLeft()) );
		newNode.setRight(cloneTree(root.getRight()) );
		return newNode;
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

}



