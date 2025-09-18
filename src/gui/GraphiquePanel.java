package gui;

import data.Entreprise;
import gestion.Bourse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphiquePanel extends JPanel {
    
    private Bourse bourse;
    private JTabbedPane chartTabbedPane;
    private JComboBox<String> sectorComboBox;
    private HashMap<String, ArrayList<Double>> priceHistory;
    private int currentTurn = 0;
    
    public GraphiquePanel(Bourse bourse) {
        this.bourse = bourse;
        this.priceHistory = new HashMap<>();
        
        setLayout(new BorderLayout());
        for (Entreprise e : bourse.getEntreprises().values()) {
            ArrayList<Double> history = new ArrayList<>();
            history.add(e.getPrixDepart());
            priceHistory.put(e.getID(), history);
        }
        JPanel filterPanel = new JPanel();
        JLabel sectorLabel = new JLabel("Secteur:");
        sectorComboBox = new JComboBox<>();
        sectorComboBox.addItem("Tous les secteurs");
        Map<String, Entreprise> entreprises = bourse.getEntreprises();
        for (Entreprise e : entreprises.values()) {
            String secteur = e.getSecteur();
            if (!containsItem(sectorComboBox, secteur)) {
                sectorComboBox.addItem(secteur);
            }
        }
        sectorComboBox.addActionListener(new SectorChangeListener());
        filterPanel.add(sectorLabel);
        filterPanel.add(sectorComboBox);
        chartTabbedPane = new JTabbedPane();
        addLineChartTab();
        addSectorPerformanceTab();
        JLabel titleLabel = new JLabel("Analyse Graphique", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);
        add(filterPanel, BorderLayout.SOUTH);
        add(chartTabbedPane, BorderLayout.CENTER);
        updateGraphs();
    }
    
    private class SectorChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateGraphs();
        }
    }
    
    private boolean containsItem(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }
    
    private void addLineChartTab() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart( "Évolution des prix des actions","Tour","Prix (€)",dataset,PlotOrientation.VERTICAL,true,true,false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);       
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartTabbedPane.addTab("Évolution des prix", chartPanel);
    }
    
    private void addSectorPerformanceTab() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart("Performance par secteur","Secteur","Variation moyenne (%)",dataset,PlotOrientation.VERTICAL,true,true,false);
        chart.setBackgroundPaint(Color.WHITE);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartTabbedPane.addTab("Performance par secteur", chartPanel);
    }
    
    public void updateGraphs() {
        currentTurn++;
        for (Entreprise e : bourse.getEntreprises().values()) {
            String id = e.getID();
            ArrayList<Double> history = priceHistory.get(id);
            if (history != null) {
                history.add(e.getPrixDepart());
            } else {
                history = new ArrayList<>();
                history.add(e.getPrixDepart());
                priceHistory.put(id, history);
            }
        }
        updatePriceChart();
        updateSectorPerformanceChart();
    }
    
    private void updatePriceChart() {
        ChartPanel chartPanel = (ChartPanel) chartTabbedPane.getComponentAt(0);
        JFreeChart chart = chartPanel.getChart();
        XYPlot plot = chart.getXYPlot();
        String selectedSector = (String) sectorComboBox.getSelectedItem();
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (String id : priceHistory.keySet()) {
            ArrayList<Double> prix = priceHistory.get(id);
            Entreprise e = bourse.getEntreprises().get(id);
            if (selectedSector.equals("Tous les secteurs") || e.getSecteur().equals(selectedSector)) {
                XYSeries courbe = new XYSeries(e.getNom());
                
                for (int i = 0; i < prix.size(); i++) {
                    courbe.add(i, prix.get(i));
                }
                
                dataset.addSeries(courbe);
            }
        }
        plot.setDataset(dataset);
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesShapesVisible(i, true);
            renderer.setSeriesLinesVisible(i, true);
        }
        
        plot.setRenderer(renderer);
    }
    
    private void updateSectorPerformanceChart() {
        ChartPanel chartPanel = (ChartPanel) chartTabbedPane.getComponentAt(1);
        JFreeChart chart = chartPanel.getChart();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        HashMap<String, Double> sectorPerformanceSum = new HashMap<>();
        HashMap<String, Integer> sectorCount = new HashMap<>();
        
        for (Entreprise e : bourse.getEntreprises().values()) {
            String secteur = e.getSecteur();
            double variation = e.getPourcentage();
            
            sectorPerformanceSum.put(secteur, sectorPerformanceSum.getOrDefault(secteur, 0.0) + variation);
            sectorCount.put(secteur, sectorCount.getOrDefault(secteur, 0) + 1);
        }
        for (String secteur : sectorPerformanceSum.keySet()) {
            double totalVariations = sectorPerformanceSum.get(secteur);
            int nombreDEntreprises = sectorCount.get(secteur);
            double moyenneVariation = totalVariations / nombreDEntreprises;
            dataset.addValue(moyenneVariation, "Variation moyenne", secteur);
        }
        chart.getCategoryPlot().setDataset(dataset);
    }
}