package org.QueryOptimizer;

import java.awt.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
public class Optimizer {
    private final Estimator estimator;
    private final Translator t;
    private final Transformer tr;
    public Optimizer(String script){
        t = new Translator(script);
        tr = new Transformer(t.getFirstTree());
        estimator = new Estimator();
    }
    public Estimator getEstimator() {
        return estimator;
    }

    public Translator getT() {
        return t;
    }

    public Transformer getTr() {
        return tr;
    }


    public static final String INDEX_PR="2nd index";
    public static final String INDEX_SC="1st index";
    public static final String HASH="hash";
    public static final String FULL_SCAN="F-scan";
    public Set<Node> physiquesTree(Node tree) {
        Set<Node> treeSet = new HashSet<>();
        treeSet.add(tree);
        Node Ar = Node.cloneTree(tree);
        treeSet.add(Ar);
        this.change(Ar, "BIB", 2).forEach((n1) -> this.selectPhysicalVars(treeSet, n1));
        this.change(Ar, "BII", 2).forEach((n1) -> this.selectPhysicalVars(treeSet, n1));
        this.change(Ar, "JH", 2).forEach((n1) -> this.selectPhysicalVars(treeSet, n1));
        this.change(Ar, "PJ", 2).forEach((n1) -> this.selectPhysicalVars(treeSet, n1));
        this.change(Ar, "JTF", 2).forEach((n1) -> this.selectPhysicalVars(treeSet, n1));
        this.removeGarbage(treeSet);
        return treeSet;
    }
    public Map<Node, Set<Node>> allPhysicalTrees(Set<Node>logical){
        Map<Node, Set<Node>> map=new HashMap<>();
       logical.forEach(l->map.put(l, physiquesTree(l)));
       return map;
    }
    public Node optimalTree(Map<Node,Set<Node>> allVariants){
        double min =Double.MAX_VALUE;
        Node optimal=null;
        for(Node n:allVariants.keySet()){
            double calculated=estimator.minCostsOneLogTree(allVariants.get(n),n);
            if(min>calculated){
                min=calculated;
                optimal=n;
            }
        }
        return optimal;
    }
    private void selectPhysicalVars(Set<Node> treeSet, Node n1) {
        treeSet.addAll(change(n1, FULL_SCAN,1));
        treeSet.addAll(change(n1,  INDEX_SC,1));
        treeSet.addAll(change(n1, INDEX_PR,1));
        treeSet.addAll(change(n1,  HASH,1));
    }

    private void removeGarbage(Set<Node> treeSet) {
        treeSet.removeIf(this::downReader);
    }

    private boolean downReader(Node n) {
        if (n==null)return false;
        if(n.getData().matches("(σ\\w+\\s*\\.\\s*\\w+\\s*[=><]\\s*'[^']*')|(⋈\\w+\\.\\w+\\s*=\\s*\\w+\\.\\w+)")) {
            System.out.println(n.getData());
            return true;
        }
        return downReader(n.getLeft())||downReader(n.getRight());
    }


    private Set<Node> change(Node tree, String nouveau,int type) {
        Set<Node> treeSet = new HashSet<>();
        Node tmp;
        if(type==1)  tmp = change1(Node.cloneTree(tree), nouveau);
        else   tmp = change2(Node.cloneTree(tree), nouveau);
        treeSet.add(tmp);
        return treeSet;
    }
    private Node change1(Node a, String nouveau) {
        if (a == null) {
            return null;
        }
        if(a.getType().equals(Node.S)){

            String match="";
            String pattern = "\\w+\\s*\\.\\s*\\w+\\s*";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(a.getData());

            if (m.find()) match = m.group(); // Extract the matched substring
            System.out.println(a.getData()+ "matcher: "+match);
            String table=match.split("\\.")[0].trim();
            String col=match.split("\\.")[1].trim();
            if(nouveau.contains(HASH)){
                if(!estimator.getParser().isUnique(table,col)) nouveau=FULL_SCAN;

            }else if(nouveau.contains(INDEX_PR)){
                if(!estimator.getParser().isPrimaryKey(table,col))  nouveau=FULL_SCAN;
            }else if(nouveau.contains(INDEX_SC)){
                if(!estimator.getParser().isIndexed(table,col))  nouveau=FULL_SCAN;
            }

            a.setData("("+nouveau+")" );
        }
        if (a.getRight() != null) change1(a.getRight(), nouveau);
        if (a.getLeft() != null) change1(a.getLeft(), nouveau);
        return a;

    }
    private Node change2(Node a, String nouveau) {
        if (a == null) {
            return null;
        }
        if (a.getType().equals(Node.J)) {
            a.setType(Node.J);
            a.setData("(" + nouveau + ")");
        }
        if (a.getRight() != null) change2(a.getRight(), nouveau);
        if (a.getLeft() != null) change2(a.getLeft(), nouveau);
        return a;

    }

    public static void main(String[] args) {
//select A.a, B.b from A,B,C where A.a=B.a AND A.a='2' AND A.z='3' and C.c='3' OR A.a<'7' AND A.a>'89' OR C.e='45' OR C.j='35' AND B.b=C.b
       JFrame frame = new JFrame("Optimizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        final JScrollPane[] pan = new JScrollPane[1];

        JTextField script = new JTextField(50);
        script.setFont(new Font("Arial", Font.PLAIN, 14));
        script.setPreferredSize(new Dimension(400, 30));

        JButton execute = new JButton("Execute");
        execute.setFont(new Font("Arial", Font.PLAIN, 14));
        execute.setBackground(Color.BLUE);
        execute.setForeground(Color.WHITE);
        execute.setPreferredSize(new Dimension(100, 30));
        execute.addActionListener(evt -> {

            Optimizer optimizer = new Optimizer(script.getText());
            if (frame.getContentPane().getComponents().length > 1) {
                frame.remove(pan[0]);
            }
            pan[0] =Visualizer.drawListOfTrees(optimizer,script.getText());
            frame.add(pan[0], BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();

            frame.pack();
        });

        ImageIcon icon = new ImageIcon("src/main/java/org/QueryOptimizer/dictionary/result.gif");
        JLabel label = new JLabel(icon);
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(script, BorderLayout.CENTER);
        p.add(execute, BorderLayout.EAST);
        frame.add(p, BorderLayout.NORTH);
        JPanel panel=new JPanel(new BorderLayout());
        panel.add(label,BorderLayout.CENTER);
        pan[0]=new JScrollPane(panel);
        panel.setBackground(Color.WHITE);
        frame.add(pan[0],BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(new Dimension(700, 500));
        frame. setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);
    }
}
