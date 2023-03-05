package org.QueryOptimizer;


import java.util.List;

class Column{
	private String columnName;
	private boolean PK;
	private boolean indexed;
	private  boolean unique;
	private int cardinality;
	private double minVal;
	private double maxVall;
	private int orderMoy;


}
class Table{
	private int lineSize;
	private int lineCount;
	private  double FB;
	private String tableName;
	private List<Column> columns;

}
public class Dictionary {
	private  double transTime;
	private int nbrPos;
	private double tpd;
	private List<Table> tables;

}