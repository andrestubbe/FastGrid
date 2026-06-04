package fastgrid.demo;

import fastgrid.FastGridEngine;
import fastui.Container;
import fastui.component.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Demo extends JFrame {

    private static final int ITEM_COUNT = 50;
    private static final int COLUMNS = 5;
    private static final float GAP = 10f;
    private static final float TARGET_ROW_HEIGHT = 150f;

    private static final float[] itemAspectRatios = new float[ITEM_COUNT];
    private static final float[] outBounds = new float[ITEM_COUNT * 4];
    private static final float[] scratchColumnHeights = new float[COLUMNS];

    private static int currentMode = 0; // 0=Grid, 1=Masonry, 2=Gallery
    private final Container root = new Container();

    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.opengl.fbobject", "true");
        System.setProperty("sun.java2d.d3d", "false");
        SwingUtilities.invokeLater(Demo::new);
    }

    public Demo() {
        super("FastGrid Demo - Zero Allocation");

        // Initialize random aspect ratios for our images
        Random rand = new Random(42);
        for (int i = 0; i < ITEM_COUNT; i++) {
            itemAspectRatios[i] = 0.5f + rand.nextFloat() * 1.5f; // Portrait to Landscape
        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 800);
        this.setLocationRelativeTo(null);
        
        root.setBackground(new Color(30, 30, 30));

        Component canvas = new Component() {
            @Override
            public void onRender(Graphics2D g) {
                float containerWidth = getWidth();
                
                if (containerWidth <= 0) return;

                if (currentMode == 0) {
                    FastGridEngine.computeGridLayout(ITEM_COUNT, containerWidth, COLUMNS, GAP, outBounds);
                } else if (currentMode == 1) {
                    FastGridEngine.computeMasonryLayout(ITEM_COUNT, containerWidth, COLUMNS, GAP, itemAspectRatios, scratchColumnHeights, outBounds);
                } else {
                    FastGridEngine.computeGalleryLayout(ITEM_COUNT, containerWidth, TARGET_ROW_HEIGHT, GAP, itemAspectRatios, outBounds);
                }

                // Render the computed primitive bounds
                for (int i = 0; i < ITEM_COUNT; i++) {
                    int idx = i * 4;
                    float x = outBounds[idx];
                    float y = outBounds[idx + 1];
                    float w = outBounds[idx + 2];
                    float h = outBounds[idx + 3];

                    // Draw Item Background
                    g.setColor(new Color(70, 130, 180));
                    g.fillRect((int) x, (int) y, (int) w, (int) h);

                    // Draw Item Border
                    g.setColor(new Color(100, 150, 200));
                    g.drawRect((int) x, (int) y, (int) w, (int) h);

                    // Draw Text
                    g.setColor(Color.WHITE);
                    g.drawString("Item " + i, (int) x + 5, (int) y + 15);
                }

                // Overlay Status Text
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                String modeStr = currentMode == 0 ? "GRID" : (currentMode == 1 ? "MASONRY" : "GALLERY");
                g.drawString("Current Mode: " + modeStr + " (Press SPACE to switch)", 20, 30);
            }
        };

        root.add(canvas);

        root.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    currentMode = (currentMode + 1) % 3;
                    root.repaint();
                }
            }
        });

        setContentPane(root);
        setVisible(true);

        SwingUtilities.invokeLater(root::requestFocusInWindow);
    }
}
