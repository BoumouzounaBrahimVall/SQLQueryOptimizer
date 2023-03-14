package org.QueryOptimizer;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

public class Visualizer extends JPanel {
    private final Node tree;

    public Visualizer(Node tr) {
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





    static int drawListOfTrees(Set<Node> trees, Estimator estimator, JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
/////

        Node maintree =  trees.iterator().next();
        Visualizer mainsubPanel = new Visualizer(maintree);

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
        panel.add(Box.createRigidArea(new Dimension(0, 10)));////
        int i=0;
        for (Node tree : trees) {

            Visualizer subPanel = new Visualizer(tree);

            // Calculate costs
            HashSet<Double> ls = estimator.calculateCosts(tree);
            double minCost = ls.stream().min(Double::compare).orElse(Double.NaN);
            double maxCost = ls.stream().max(Double::compare).orElse(Double.NaN);

            // Create labels
            JLabel treeLabel = new JLabel("Tree " + (i + 1));i++;
            treeLabel.setFont(new Font("Arial", Font.BOLD, 16));
            treeLabel.setForeground(Color.WHITE);
            treeLabel.setOpaque(true);
            treeLabel.setBackground(Color.lightGray);
            treeLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel ruleLabel = new JLabel("Rule applied: " );
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
                    createWindowWithPanels(trees);
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


    public static void createWindowWithPanels(Set<Node> trees) {
        // Création de la fenêtre principale
        JFrame frame = new JFrame("Ma fenêtre avec plusieurs panneaux");

        // Définition de la taille et de la position de la fenêtre
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Création du panneau principal qui contiendra tous les autres panneaux
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(0, 1));
        int i = 1;
        for (Node tree :trees){
        // Création des panneaux avec titre et champ de saisie
        JPanel panel1 = createPanelWithTitleAndInput("Physical Tree " + i,tree,i);
        i++;

        // Ajout des panneaux au panneau principal
        mainPanel.add(panel1);
    }

        // Ajout du panneau principal à une barre de défilement verticale
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // Ajout de la barre de défilement à la fenêtre principale
        frame.add(scrollPane, BorderLayout.CENTER);

        // Affichage de la fenêtre principale
        frame.setVisible(true);
    }

    /**
     * Crée un panneau avec un titre et un champ de saisie.
     * @param title Le titre du panneau.
     * @return Le panneau créé.
     */
    private static JPanel createPanelWithTitleAndInput(String title,Node tree,int i) {
        // Création du panneau avec un titre et un champ de saisie
        Visualizer subPanel = new Visualizer(tree);

        JLabel treeLabel = new JLabel(" Physical Tree " + (i + 1));
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
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createLineBorder(new Color(102, 102, 102), 1));
        treePanel.add(topPanel, BorderLayout.NORTH);
        treePanel.add(subPanel, BorderLayout.CENTER);
        treePanel.add(bottomPanel, BorderLayout.SOUTH);
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



        }
