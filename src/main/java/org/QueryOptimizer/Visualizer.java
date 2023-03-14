package org.QueryOptimizer;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class Visualizer extends JPanel {
    private final Tree tree;

    public Visualizer(Tree tr) {
        this.tree = tr;
        setPreferredSize(new Dimension(1000, 400));
        this.setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Serif", Font.ITALIC, 10));
        int x=getWidth()/2,y=50;
        if(tree.getRoot().getData().contains("π")){
            g.drawString(tree.getRoot().getData(), x- 12, y + 12);
            if(tree.getRoot().getLeft()!=null) {
                g.drawLine(x+5, y+20, x+5, y+40);
                drawNode(tree.getRoot().getLeft(), x, y+40, g);
            }
        } else drawNode(tree.getRoot(), x-10, y + 10,g);


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

    /*static int drawListOfTrees(Map<String,Tree> trees, Estimator estimator, JFrame frame){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(trees.values().size(), 1));
        int i=0;
        for (Map.Entry<String, Tree> tree : trees.entrySet()) {

            i++;
            Visualizer subPanel = new Visualizer(tree.getValue());
            JPanel p=new JPanel(new BorderLayout());
            HashSet<Double> ls = estimator.calculateCosts(tree.getValue().getRoot());

            double minCost = ls.stream().min(Double::compare).orElse(Double.NaN);
            double maxCost=ls.stream().max(Double::compare).orElse(Double.NaN);
            String phisi="Physical costs: minCost : ("+minCost+"ms) MaxCost: ("+maxCost+"ms) "+ "count physical tree: "+ls.size();
            StringBuilder s= new StringBuilder(" costs: [");
            for(Double cost: ls){
                s.append("").append(cost).append("ms, ");
            };


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

            l.setBackground(Color.lightGray);
            String key=tree.getKey();
             key = key.substring(0, key.length()-2);
            JLabel ll=new JLabel("Regle appliquee  : "+ key);
            ll.setFont(new Font("Serif", Font.BOLD, 13));
            ll.setHorizontalAlignment(JLabel.CENTER);
            ll.setVerticalAlignment(JLabel.CENTER);
            ll.setForeground(Color.RED);
            ll.setOpaque(true);
            ll.setBackground(Color.lightGray);



            JTextArea cos2=new JTextArea(costs);
            cos2.setSize(new Dimension(1200,200));
            cos2.setLineWrap(true);
            cos2.setFont(new Font("Serif", Font.ITALIC, 16));
            JPanel pan=new JPanel(new FlowLayout(FlowLayout.LEFT));

            pan.add(l);pan.add(ll);pan.add(cos2);
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
   /* static int drawListOfTrees(Map<String, Tree> trees, Estimator estimator, JFrame frame) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        int i = 1;
        for (Map.Entry<String, Tree> tree : trees.entrySet()) {
            Tree currentTree = tree.getValue();
            Visualizer subPanel = new Visualizer(currentTree);
            // Create panel for displaying costs
            JPanel costPanel = new JPanel();
            costPanel.setLayout(new BoxLayout(costPanel, BoxLayout.Y_AXIS));

            // Calculate costs
            HashSet<Double> costs = estimator.calculateCosts(currentTree.getRoot());
            double minCost = Collections.min(costs);
            double maxCost = Collections.max(costs);
            int numPhysicalTrees = costs.size();

            // Create labels for displaying cost information
            JLabel treeLabel = new JLabel("Tree " + i);
            treeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            treeLabel.setFont(new Font("Serif", Font.BOLD, 16));

            JLabel ruleLabel = new JLabel("Rule applied: " + tree.getKey().substring(0, tree.getKey().length() - 2));
            ruleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            ruleLabel.setFont(new Font("Serif", Font.PLAIN, 14));

            JLabel physicalCostsLabel = new JLabel("Physical costs: min = " + minCost + " ms, max = " + maxCost + " ms, count = " + numPhysicalTrees);
            physicalCostsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            physicalCostsLabel.setFont(new Font("Serif", Font.PLAIN, 14));

            // Create text area for displaying all costs
            JTextArea allCostsTextArea = new JTextArea();
            allCostsTextArea.setText("Costs:\n");
            allCostsTextArea.setEditable(false);
            allCostsTextArea.setFont(new Font("Serif", Font.PLAIN, 14));
            allCostsTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add each cost to the text area
            for (Double cost : costs) {
                allCostsTextArea.append(cost + " ms\n");
            }

            // Add components to the cost panel
            costPanel.add(Box.createVerticalStrut(20));
            costPanel.add(treeLabel);
            costPanel.add(Box.createVerticalStrut(10));
            costPanel.add(ruleLabel);
            costPanel.add(Box.createVerticalStrut(10));
            costPanel.add(physicalCostsLabel);
            costPanel.add(Box.createVerticalStrut(10));
            //costPanel.add(allCostsTextArea);
            costPanel.add(Box.createVerticalStrut(20));

            // Create panel for displaying the tree and cost information
            JPanel treePanel = new JPanel();
            treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.X_AXIS));
            treePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            treePanel.add(Box.createHorizontalStrut(10));
            treePanel.add(subPanel);
            treePanel.add(Box.createHorizontalStrut(10));
            treePanel.add(costPanel);
            treePanel.add(Box.createHorizontalStrut(10));
            // Add the tree panel to the main panel
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(treePanel);
            mainPanel.add(Box.createVerticalStrut(20));

            i++;
            subPanel.setPreferredSize(new Dimension(800, 600)); // set the size to 800x600 pixels

        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        return mainPanel.getHeight();
    }*/



    static int drawListOfTrees(Map<String, Tree> trees, Estimator estimator, JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/////

        Map.Entry<String, Tree> maintree = (Map.Entry<String, Tree>) trees.entrySet().toArray()[0];
        Visualizer mainsubPanel = new Visualizer(maintree.getValue());

        // Calculate costs


        // Create labels
        JLabel maintreeLabel = new JLabel("Main Tree ");
        JTextArea maindescrArea = new JTextArea();
        maindescrArea.setEditable(false);
        maindescrArea.setLineWrap(true);
        maindescrArea.setWrapStyleWord(true);
        maindescrArea.setFont(new Font("Arial", Font.PLAIN, 14));
        maindescrArea.setForeground(new Color(102, 102, 102));
        String text="yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";
        maindescrArea.setText(text);

        maintreeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        maintreeLabel.setForeground(Color.red);
        maintreeLabel.setOpaque(true);
        maintreeLabel.setBackground(Color.white);
        maintreeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JPanel maintopPanel = new JPanel(new BorderLayout());
        maintopPanel.add(maintreeLabel, BorderLayout.NORTH);
        maintopPanel.add(maindescrArea, BorderLayout.SOUTH);


        JPanel maintreePanel = new JPanel(new BorderLayout());
        maintreePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 102, 102), 1));
        maintreePanel.add(maintopPanel, BorderLayout.NORTH);
        maintreePanel.add(mainsubPanel, BorderLayout.CENTER);

        panel.add(maintreePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));        ////
        for (int i = 0; i < trees.size(); i++) {
            Map.Entry<String, Tree> tree = (Map.Entry<String, Tree>) trees.entrySet().toArray()[i];
            Visualizer subPanel = new Visualizer(tree.getValue());

            // Calculate costs
            HashSet<Double> ls = estimator.calculateCosts(tree.getValue().getRoot());
            double minCost = ls.stream().min(Double::compare).orElse(Double.NaN);
            double maxCost = ls.stream().max(Double::compare).orElse(Double.NaN);

            // Create labels
            JLabel treeLabel = new JLabel("Tree " + (i + 1));
            treeLabel.setFont(new Font("Arial", Font.BOLD, 16));
            treeLabel.setForeground(Color.WHITE);
            treeLabel.setOpaque(true);
            treeLabel.setBackground(Color.lightGray);
            treeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel ruleLabel = new JLabel("Rule applied: " + tree.getKey().substring(0, tree.getKey().length() - 2));
            ruleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            ruleLabel.setForeground(new Color(102, 102, 102));

            JLabel costLabel = new JLabel("Physical costs: minCost: " + minCost + "ms, maxCost: " + maxCost + "ms, count physical tree: " + ls.size());
            costLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            costLabel.setForeground(new Color(102, 102, 102));

            JTextArea costArea = new JTextArea();
            costArea.setEditable(false);
            costArea.setLineWrap(true);
            costArea.setWrapStyleWord(true);
            costArea.setFont(new Font("Arial", Font.PLAIN, 14));
            costArea.setForeground(new Color(102, 102, 102));
            JButton showCosts   = new JButton("Show costs");
            JScrollPane scrollPane = new JScrollPane(showCosts);
            scrollPane.add(costArea);





            StringBuilder s = new StringBuilder("Costs: [");
            for (Double cost : ls) {
                s.append(cost).append("ms, ");
            }
            String costs = String.valueOf(s).substring(0, s.length() - 2) + "]";
            costArea.setText(costs);
            showCosts.addActionListener(e -> {
                if (costArea.isShowing()) {
                    costArea.setVisible(false);
                    showCosts.setText("Show costs");
                } else {
                    costArea.setVisible(true);
                    showCosts.setText("Hide costs");

                }
            });
            JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 5));
            labelPanel.add(ruleLabel);
            labelPanel.add(costLabel);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(treeLabel, BorderLayout.NORTH);
            topPanel.add(labelPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel treePanel = new JPanel(new BorderLayout());
            treePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 102, 102), 1));
            treePanel.add(topPanel, BorderLayout.NORTH);
            treePanel.add(subPanel, BorderLayout.CENTER);
            treePanel.add(bottomPanel, BorderLayout.SOUTH);

            panel.add(treePanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        frame.add(scrollPane, BorderLayout.CENTER);

        return panel.getPreferredSize().height;
    }






        }
