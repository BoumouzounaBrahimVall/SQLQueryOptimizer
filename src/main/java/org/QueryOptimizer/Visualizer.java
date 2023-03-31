package org.QueryOptimizer;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;


import javax.swing.*;

public class Visualizer extends JPanel {
    private final Node tree;

    public Visualizer(Node tr,Color col) {
        this.tree = tr;
        setPreferredSize(new Dimension(1000, 400));
        this.setBackground(col);
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

    private void drawNode(Node node, int x, int y, Graphics g) {
        String str= node.getData();
        if(str.contains("&&")){
            String[] parts=str.split("&&");
            g.drawString(parts[0], x-20, y + 12);
            g.drawString("&&", x-12, y + 24);
            int i=y+24;
            for (int j=1;j<parts.length-1;j++) {
                g.drawString(parts[j], x - 20, i += 12);
                g.drawString("&&", x - 12, i += 12);

            }
            g.drawString(parts[parts.length-1], x - 20, i += 12);
            y=i;
        }else if(str.contains("OR")&&str.length()>2){
            String[] parts=str.split("OR");
            g.drawString(parts[0], x-20, y + 12);
            g.drawString("OR", x-12, y + 24);
            int i=y+24;
            for (int j=1;j<parts.length-1;j++) {
                g.drawString(parts[j], x - 20, i += 12);
                g.drawString("OR", x - 12, i += 12);
            }
            g.drawString(parts[parts.length-1], x - 20, i += 12);
            y=i;
        }

        else if(node.getType().equals(Node.T)){
            g.drawString(node.getData(), x-9, y + 12);
        }
        else g.drawString(node.getData(), x-20, y + 12);//x - 30
        //

        if (node.getLeft() != null) {
            int x1 = x - 15;
            int y1 = y + 15;

            int x2 = (int) (x1 - Math.pow(2, getHeight(node.getLeft())) * 8 + 0.5);
            if(node.getType().equals(org.QueryOptimizer.Node.S)) x2=x1=x;
            int y2 = y + 40;
            g.drawLine(x1, y1, x2, y2);
            drawNode(node.getLeft(), x2, y2, g);
        }
        if (node.getRight() != null) {
            int x1 = x + 15;
            int y1 = y + 15;
            int x2 = (int) (x1 + Math.pow(2, getHeight(node.getRight())) * 8 + 0.5);
            int y2 = y + 40;
            g.drawLine(x1, y1, x2, y2);
            drawNode(node.getRight(), x2, y2, g);
        }
    }

    private int getHeight(Node Node) {
        if (Node == null) {
            return 0;
        }
        return Math.max(getHeight(Node.getLeft()), getHeight(Node.getRight())) + 1;
    }






    static JScrollPane drawListOfTrees(Optimizer op,String script) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Set<Node> variants=op.getTr().getAllVariants();

        panel.add(drawnTree(op.getT().getFirstTree(),-1*variants.size(),"Main Tree",new Color(14, 28, 162),op));
        Node optimal=op.optimalTree(op.allPhysicalTrees(variants));
        panel.add(drawnTree(optimal, op.getEstimator().minCostsOneLogTree(op.physiquesTree(optimal),optimal),script,new Color(13, 122, 18),op));

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        int i=1;
        for (Node tree : variants) {

            Visualizer subPanel = new Visualizer(tree,Color.DARK_GRAY);
            JLabel treeLabel = new JLabel("Tree " + (i++));
            treeLabel.setFont(new Font("Arial", Font.BOLD, 16));
            treeLabel.setForeground(Color.WHITE);
            treeLabel.setOpaque(true);
            treeLabel.setBackground(Color.lightGray);
            treeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel ruleLabel = new JLabel("Rule applied: "+ op.getTr().reglenames.get(tree) );
            ruleLabel.setForeground(new Color(102, 102, 102));

            JLabel costLabel = new JLabel(" " );
            costLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            costLabel.setForeground(new Color(102, 102, 102));

            JTextArea costArea = new JTextArea();
            costArea.setEditable(false);
            costArea.setLineWrap(true);
            costArea.setWrapStyleWord(true);
            costArea.setFont(new Font("Arial", Font.PLAIN, 14));
            costArea.setForeground(new Color(102, 102, 102));
            JButton showCosts   = new JButton("Show Physical Trees");
            showCosts.setFont(new Font("Arial", Font.PLAIN, 14));
            showCosts.setBackground(new Color(115, 112, 234));
            showCosts.setForeground(Color.WHITE);
            JScrollPane scrollPane = new JScrollPane(showCosts);
            scrollPane.add(costArea);


            StringBuilder s = new StringBuilder("Costs: [");
            String costs = String.valueOf(s).substring(0, s.length() - 2) + "]";
            costArea.setText(costs);
            showCosts.addActionListener(e -> {

                    Set<Node>  treeCosts=op.physiquesTree(tree);
                    createWindowWithPanels(treeCosts,tree,op.getEstimator());
            });
            JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 5));
            labelPanel.add(ruleLabel);
            labelPanel.add(costLabel);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(treeLabel, BorderLayout.NORTH);
            topPanel.add(labelPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            var treePanel=bottomSection(subPanel, topPanel, bottomPanel, scrollPane);

            panel.add(treePanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(40);
        return scrollPane;
    }

    private static JPanel drawnTree(Node node, double cost, String script, Color col,Optimizer op) {

        Visualizer mainSubPanel = new Visualizer(node, col);
        JPanel maintopPanel = new JPanel();
        maintopPanel.setLayout(new BoxLayout(maintopPanel, BoxLayout.Y_AXIS));

        String titre = script.equals("Main Tree") ? script : "Optimal Tree";
        // Create labels
        JLabel mainTreeLabel = new JLabel(titre);
        JTextArea mainDescrArea = new JTextArea();
        mainDescrArea.setEditable(false);
        mainDescrArea.setLineWrap(true);
        mainDescrArea.setWrapStyleWord(true);
        mainDescrArea.setForeground(col);
        mainDescrArea.setFont(new Font("", Font.BOLD, 13));
        String text = cost > 0 ? "\n- Cost: " + cost + "ms  \n" : "";
        if (!script.equals("Main Tree")) {
            String optimal_script = Node.extractScript(Node.cloneTree(node.getLeft()));

            optimal_script=script.toUpperCase().split("WHERE")[0] + (script.contains("WHERE")?" WHERE " + optimal_script:"") + " }";
            optimal_script= optimal_script.replaceAll("\n","");
            text += "- tip: It would be better if you write the Query this way :\n  { " + optimal_script+"\n";
        }else{
            text="\n- logical trees count : "+((int)-cost)+"\n";
        }

        mainDescrArea.setText(text);

        mainTreeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainTreeLabel.setForeground(col);
        mainTreeLabel.setOpaque(true);
        mainTreeLabel.setBackground(Color.white);
        mainTreeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        maintopPanel.add(mainTreeLabel);
        maintopPanel.add(mainDescrArea);

        // Set the width of the JTextArea to match the width of the returned panel
        maintopPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = maintopPanel.getWidth();
                mainDescrArea.setPreferredSize(new Dimension(width, mainDescrArea.getPreferredSize().height));
            }
        });

        JTextArea costArea = new JTextArea();
        costArea.setEditable(false);
        costArea.setLineWrap(true);
        costArea.setWrapStyleWord(true);
        costArea.setFont(new Font("Arial", Font.PLAIN, 14));
        costArea.setForeground(new Color(102, 102, 102));
        JButton showCosts   = new JButton("Show Physical Trees");
        showCosts.setFont(new Font("Arial", Font.PLAIN, 14));
        showCosts.setBackground(new Color(115, 112, 234));
        showCosts.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(showCosts);
        scrollPane.add(costArea);
        showCosts.addActionListener(e -> {

            Set<Node>  treeCosts=op.physiquesTree(node);
            createWindowWithPanels(treeCosts,node,op.getEstimator());
        });
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel mainTreePanel = new JPanel(new BorderLayout());
        mainTreePanel.setBorder(BorderFactory.createLineBorder(col, 1));
        mainTreePanel.add(maintopPanel, BorderLayout.NORTH);
        mainTreePanel.add(mainSubPanel, BorderLayout.CENTER);
        if (!script.equals("Main Tree")) {
            mainTreePanel.add(bottomPanel, BorderLayout.SOUTH);}
        return mainTreePanel;
    }

    public static void createWindowWithPanels(Set<Node>  trees,Node tr,Estimator es) {
        // Creation of the main window
        JFrame frame = new JFrame("Physical trees   ");

        //Setting window size and position
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Creation of the main panel which will contain all the other panels
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));
        int i = 1;
        for (Node tree :trees){
        // Creation of panels with title and input field
        JPanel panel1 = createPanelWithTitleAndInput(es.uniCosts(tree,tr),tree,i);
        i++;

            // Add panels to the main panel
        mainPanel.add(panel1);
    }

        // Add the main panel to a vertical scrollbar
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Add the scroll bar to the main window
        frame.add(scrollPane, BorderLayout.CENTER);

        // Display of the main window
        frame.setVisible(true);
    }


    private static JPanel createPanelWithTitleAndInput(String cost,Node tree,int i) {
        // Create the panel with a title and an input field
        Visualizer subPanel = new Visualizer(tree,Color.DARK_GRAY);

        JLabel treeLabel = new JLabel(" Physical Tree " + (i + 1)+cost);
        treeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        treeLabel.setForeground(Color.WHITE);
        treeLabel.setOpaque(true);
        treeLabel.setBackground(Color.lightGray);
        treeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(treeLabel, BorderLayout.NORTH);
        topPanel.add(treeLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        JPanel treePanel = bottomSection(subPanel, topPanel, bottomPanel, scrollPane);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(treePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));




        treePanel.add(topPanel, BorderLayout.NORTH);
        treePanel.add(subPanel, BorderLayout.CENTER);
        treePanel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private static JPanel bottomSection(Visualizer subPanel, JPanel topPanel, JPanel bottomPanel, JScrollPane scrollPane) {
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 102, 102), 1));
        treePanel.add(topPanel, BorderLayout.NORTH);
        treePanel.add(subPanel, BorderLayout.CENTER);
        treePanel.add(bottomPanel, BorderLayout.SOUTH);
        return treePanel;
    }


}
