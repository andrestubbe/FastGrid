package fastgrid.demo;

import fasttheme.FastTheme;
import fastui.Container;
import fastgrid.LayoutMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class Demo {

    private static final int WIDTH  = 1173;
    private static final int HEIGHT = 610;

    public static void main(String[] args) {

        System.setProperty("sun.java2d.uiScale",         "1.0");
        System.setProperty("sun.java2d.opengl",          "true");
        System.setProperty("sun.java2d.opengl.fbobject", "true");
        System.setProperty("sun.java2d.d3d",             "false");

        JFrame frame = new JFrame("FastGrid + FastUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(createRoundIcon());

        Container root = new Container();
        root.setBackground(Color.BLACK);

        FastGridView view = new FastGridView();
        root.add(view);

        // Resolve image directory safely
        String userDir = System.getProperty("user.dir");
        File imageDir = new File(userDir.contains("examples") ? 
                                 "src/main/resources/images" : 
                                 "examples/Demo/src/main/resources/images");
                                 
        int[] stats = new int[]{0, 0, 0}; // loaded, total, fps
        Runnable updateTitle = () -> {
            frame.setTitle(String.format("FastGrid + FastUI [%d/%d] - %d FPS", stats[0], stats[1], stats[2]));
        };
                                 
        view.onProgress = (loaded, total) -> {
            stats[0] = loaded;
            stats[1] = total;
            updateTitle.run();
        };
        
        view.onFpsUpdate = (fps) -> {
            stats[2] = fps;
            updateTitle.run();
        };
        
        view.loadImages(imageDir);

        // ── Keyboard shortcuts ──────────────────────────────────
        InputMap  im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke("1"), "grid");
        im.put(KeyStroke.getKeyStroke("2"), "masonry");
        im.put(KeyStroke.getKeyStroke("3"), "gallery");

        am.put("grid",    new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { view.anim.animateLayout(LayoutMode.GRID); }
        });
        am.put("masonry", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { view.anim.animateLayout(LayoutMode.MASONRY); }
        });
        am.put("gallery", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { view.anim.animateLayout(LayoutMode.GALLERY); }
        });

        // ── Mouse (scroll, zoom, gap) ────────────────────────────
        root.addMouseListener(view.input.mouseListener);
        root.addMouseMotionListener(view.input.mouseMotionListener);
        root.addMouseWheelListener(view.input.wheelListener);

        frame.setContentPane(root);
        frame.addNotify();

        long hwnd = FastTheme.getWindowHandle(frame);
        if (hwnd != 0) {
            FastTheme.setTitleBarColor(hwnd, 0, 0, 0);
            FastTheme.setTitleBarTextColor(hwnd, 255, 255, 255);
            // FastTheme.setWindowTransparency(hwnd, 230);
        }

        frame.setVisible(true);
        SwingUtilities.invokeLater(root::requestFocusInWindow);
    }

    private static BufferedImage createRoundIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillOval(4, 4, 56, 56);
        g.dispose();
        return icon;
    }
}
