package org.QueryOptimizer;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class Visualizer extends JPanel {
    private final Node tree;

    public Visualizer(Node tr) {
        this.tree = tr;
        setPreferredSize(new Dimension(800, 400));
        this.setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.ITALIC, 10));
        int x=getWidth()/2,y=50;
        if(tree.getData().contains("π")){
            g.drawString(tree.getData(), x- 12, y + 12);
            if(tree.getLeft()!=null) {
                g.drawLine(x+5, y+20, x+5, y+40);
                drawNode(tree.getLeft(), x, y+40, g);
            }
        } else drawNode(tree, x-10, y + 10,g);


    }

    private void drawNode(Node Node, int x, int y, Graphics g) {
        g.drawString(Node.getData(), x - 12, y + 12);


        if (Node.getLeft() != null) {
            int x1 = x - 15;
            int y1 = y + 15;
            int x2 = (int) (x1 - Math.pow(2, getHeight(Node.getRight())) * 10 + 0.5);
            int y2 = y + 40;
            g.drawLine(x1, y1, x2, y2);
            drawNode(Node.getLeft(), x2, y2, g);
        }
        if (Node.getRight() != null) {
            int x1 = x + 15;
            int y1 = y + 15;
            int x2 = (int) (x1 + Math.pow(2, getHeight(Node.getRight())) * 10 + 0.5);
            int y2 = y + 40;
            g.drawLine(x1, y1, x2, y2);
            drawNode(Node.getRight(), x2, y2, g);
        }
    }

    private int getHeight(Node Node) {
        if (Node == null) {
            return 0;
        }
        return Math.max(getHeight(Node.getLeft()), getHeight(Node.getRight())) + 1;
    }

    static int drawListOfTrees(Set<Node> trees, Estimator estimator, JFrame frame){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(trees.size(), 1));
        int i=0;
        for (Node tree : trees) {

            i++;
            Visualizer subPanel = new Visualizer(tree);
            JPanel p=new JPanel(new BorderLayout());
           // HashSet<Double> ls = estimator.calculateCosts(tree);

          //  double minCost = ls.stream().min(Double::compare).orElse(Double.NaN);
           // double maxCost=ls.stream().max(Double::compare).orElse(Double.NaN);
           // String phisi="Physical costs: minCost : ("+minCost+"ms) MaxCost: ("+maxCost+"ms) "+ "count physical tree: "+ls.size();
            String phisi="Physical costs: not yet";
            StringBuilder s= new StringBuilder(" costs: [");
           // for(Double cost: ls){s.append("").append(cost).append("ms, ");};


            String costs=String.valueOf(s).substring(0,s.length()-2)+"]";
            JLabel l;
            if(i==1){
                l=new JLabel("                                                                     Main Tree: logical tree count: "+trees.size()+"   " );
            }else  l=new JLabel("                                                                               Tree "+ i );
            l.setFont(new Font("Serif", Font.BOLD, 14));
            l.setHorizontalAlignment(JLabel.CENTER);
            l.setVerticalAlignment(JLabel.CENTER);
            l.setForeground(Color.RED);
            l.setOpaque(true);



            JTextArea cos2=new JTextArea(costs);
            cos2.setSize(new Dimension(1200,200));
            cos2.setLineWrap(true);
            cos2.setFont(new Font("Serif", Font.ITALIC, 16));
            JPanel pan=new JPanel(new FlowLayout(FlowLayout.LEFT));

            pan.add(l);pan.add(cos2);
            pan.setPreferredSize(new Dimension(1200,200));

            JLabel minmax=new JLabel(phisi);
            minmax.setFont(new Font("Serif", Font.ITALIC, 14));
            minmax.setForeground(Color.RED);


            p.add(pan,BorderLayout.NORTH);
            p.add(subPanel,BorderLayout.CENTER);
            // Add components to subPanel
            panel.add(p);

        }
        JScrollPane scrollPane = new JScrollPane(panel);

        frame.add(scrollPane, BorderLayout.CENTER);
        //frame.setSize(800, 400);
        return panel.getHeight();
    }

}
