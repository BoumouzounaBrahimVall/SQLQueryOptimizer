package org.example;

import java.util.*;

/**
 * 
 */
public class Optimizer {

	/**
	 * Default constructor
	 */
	public Optimizer() {
	}
	public static void main(String[] args) {


		String query = "SELECT nom,villename FROM Personne,Ville WHERE Personne.idville = Ville.idville AND Ville.region = 'case-settat' AND Personne.a='b' OR Personne.z='z' AND Ville.s='s'";
		Query req=new Query(query);

		req.remplirArbre();
		req.DessinerArbre();


		//req.parseQuery();

      /* System.out.println(req.getProjectionsList());
       System.out.println(req.getTables());
       System.out.println(req.getWhereTokens());*/

	}

}