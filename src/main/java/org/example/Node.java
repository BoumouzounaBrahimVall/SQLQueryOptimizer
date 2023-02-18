package org.example;

import java.util.*;

/**
 * 
 */
public abstract class Node {

	/**
	 * Default constructor
	 */
	public Node() {
	}
	private Content Cont;
	private Node fils_gauche;
	private Node fils_droit;
	private boolean is_feuille;

	public Node(Content C, Node fils_gauche, Node fils_droit) {
		this.Cont = C;
		this.fils_gauche = fils_gauche;
		this.fils_droit = fils_droit;
		this.is_feuille=false;
	}

	public Node(Content C) {
		this.Cont = C;
		this.fils_droit=null;
		this.fils_droit=null;
		this.is_feuille=true;
	}

}