package fastgrid;

import fasttheme.FastTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {

    private static final int WIDTH = 1173;
    private static final int HEIGHT = 610;

    public static void main(String[] args) {

        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.opengl.fbobject", "true");
        System.setProperty("sun.java2d.d3d", "false");

        JFrame frame = new JFrame("Gallery");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(createRoundIcon());


        GalleryPanel panel = new GalleryPanel();
        JScrollPane scroll = new JScrollPane(panel);

        scroll.setBorder(null);
        scroll.setViewportBorder(null);
        scroll.getViewport().setBorder(null);
        panel.setBorder(null);

        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scroll.setWheelScrollingEnabled(true);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(Color.BLACK);

        frame.setContentPane(scroll);

        frame.addNotify();

        long hwnd = FastTheme.getWindowHandle(frame);
        if (hwnd != 0) {
            FastTheme.setTitleBarColor(hwnd, 0, 0, 0);
            FastTheme.setTitleBarTextColor(hwnd, 255, 255, 255);
            FastTheme.setWindowTransparency(hwnd, 230);
        }

        frame.setVisible(true);

        SwingUtilities.invokeLater(panel::requestFocusInWindow);
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
