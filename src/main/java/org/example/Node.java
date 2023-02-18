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
}