package com.mycompany.horseracesimulator_javamultithreading;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * This class represents a graphical horse race simulator using Java Swing and multithreading.
 * It creates a GUI to simulate a horse race with progress bars representing horse movements.
 * The race is executed in parallel using threads for each horse, with results displayed in real time.
 * 
 * @author Ornella Gigante
 */

public class HorseRaceSimulator_JavaMultithreading extends JFrame {
    private Map<String, HorsePanel> horsePanels = new HashMap<>();
    private JTextArea resultArea;
    private static final Color[] COLORS = {Color.RED, Color.BLUE, Color.GREEN};
    private String[] horseNames = new String[3];
    private int[] horseTimes = {5, 2, 3};
    private CountDownLatch finishLatch;

    public HorseRaceSimulator_JavaMultithreading() {
        setTitle("Horse Race Simulator");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel racePanel = new JPanel(new GridLayout(3, 1, 0, 10));
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);

        add(racePanel, BorderLayout.CENTER);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
    }

    private void getHorseNames() {
        for (int i = 0; i < 3; i++) {
            String name = JOptionPane.showInputDialog(this, "Insert the name of the horse: " + (i+1) + ":");
            if (name == null || name.trim().isEmpty()) {
                name = "Caballo " + (i+1);
            }
            horseNames[i] = name;
            addHorse(name, i);
        }
    }

    public void addHorse(String name, int colorIndex) {
        HorsePanel horsePanel = new HorsePanel(name, COLORS[colorIndex]);
        horsePanels.put(name, horsePanel);
        ((JPanel)getContentPane().getComponent(0)).add(horsePanel);
        revalidate();
    }

    public void updateHorseProgress(String name, int progress) {
        SwingUtilities.invokeLater(() -> {
            HorsePanel panel = horsePanels.get(name);
            if (panel != null) {
                panel.updateProgress(progress);
            }
        });
    }

    public void addResult(String result) {
        SwingUtilities.invokeLater(() -> {
            resultArea.append(result + "\n");
        });
        System.out.println(result); // Imprime en la consola también
    }

    public void startRace() {
        finishLatch = new CountDownLatch(horseNames.length);
        for (int i = 0; i < horseNames.length; i++) {
            final int index = i;
            new Thread(() -> {
                String name = horseNames[index];
                int time = horseTimes[index];
                addResult("Sale " + name);
                for (int j = 0; j <= 100; j++) {
                    try {
                        Thread.sleep(time * 10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateHorseProgress(name, j);
                }
                addResult("The horse " + name + " took " + time + " seconds");
                finishLatch.countDown();
            }).start();
        }

        new Thread(() -> {
            try {
                finishLatch.await(); // Espera a que todos los caballos terminen
                // Determinar el ganador
                int winnerIndex = 0;
                for (int i = 1; i < horseTimes.length; i++) {
                    if (horseTimes[i] < horseTimes[winnerIndex]) {
                        winnerIndex = i;
                    }
                }
                String winner = horseNames[winnerIndex];
                addResult("And the winner is " + winner + "!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HorseRaceSimulator_JavaMultithreading gui = new HorseRaceSimulator_JavaMultithreading();
            gui.getHorseNames();
            gui.setVisible(true);
            gui.startRace();
        });
    }

    // Clase interna para el panel personalizado del caballo
    private class HorsePanel extends JPanel {
        private JProgressBar progressBar;
        private JLabel horseLabel;
        private String horseName;

        public HorsePanel(String name, Color color) {
            this.horseName = name;
            setLayout(new BorderLayout());
            
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setForeground(color);
            
            // Crear una representación simple del caballo
            BufferedImage horseImage = createHorseImage(color);
            horseLabel = new JLabel(new ImageIcon(horseImage));
            
            add(progressBar, BorderLayout.CENTER);
            add(horseLabel, BorderLayout.WEST);
        }

        private BufferedImage createHorseImage(Color color) {
            BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(color);
            // Dibujar un simple "caballo" (un círculo y un triángulo)
            g2d.fillOval(5, 5, 20, 20);
            g2d.fillPolygon(new int[]{25, 30, 25}, new int[]{10, 15, 20}, 3);
            g2d.dispose();
            return image;
        }

        public void updateProgress(int progress) {
            progressBar.setValue(progress);
            progressBar.setString(horseName + ": " + progress + "%");
            int labelPosition = (int) ((progress / 100.0) * (getWidth() - horseLabel.getWidth()));
            horseLabel.setLocation(labelPosition, horseLabel.getY());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            horseLabel.setLocation(progressBar.getValue() * (getWidth() - horseLabel.getWidth()) / 100, 0);
        }
    }
}