package fastproportion.demo;

import fastproportion.Proportion;
import fastproportion.ProportionMode;
import fasttheme.FastTheme;
import fastui.behaviour.BehaviourDragMove;
import fastui.component.Component;
import fastui.component.ClipContainer;
import fastui.component.Image;
import fastanimation.FastAnimation;
import fastanimation.AnimationEngine.HeartbeatMode;
import fasttween.FastTween;
import fasttween.Ease;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * A highly optimized, single-file visual demonstration of FastProportion integrated with FastUI.
 * <p>
 * This class showcases:
 * <ul>
 *     <li>Zero-allocation layout updates during drag and resize interactions.</li>
 *     <li>Smooth interpolation between ProportionModes via FastTween.</li>
 *     <li>Proper z-index layering and bounds clipping via FastUI Component composition.</li>
 *     <li>Native window styling via FastTheme (dark mode, glass transparency).</li>
 * </ul>
 */
public class Demo extends JFrame {

    private static final int WINDOW_WIDTH = 1173;
    private static final int WINDOW_HEIGHT = 610;
    private static final int WINDOW_OPACITY = 224;

    private static final int START_WIDTH = 500;
    private static final int START_HEIGHT = 500;
    private static final int CONTENT_WIDTH = 1000;
    private static final int CONTENT_HEIGHT = 2134;
    private static final int MIN_SIZE = 50;

    private static final int ICON_SIZE = 64;
    private static final int ICON_OFFSET = 4;
    private static final int ICON_DRAW_SIZE = 56;

    private static final int HANDLE_SIZE = 12;
    private static final int HANDLE_OFFSET = HANDLE_SIZE / 2;
    private static final int ANIMATION_DURATION_MS = 300;

    private static final Color COLOR_BG = Color.BLACK;
    private static final Color COLOR_FRAME = new Color(4, 4, 4);
    private static final Color COLOR_CONTENT_BG = Color.WHITE;
    private static final Color COLOR_CONTENT_LINES = Color.BLACK;



    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.opengl.fbobject", "true");
        System.setProperty("sun.java2d.d3d", "false");
        SwingUtilities.invokeLater(Demo::new);
    }

    private final Proportion p = new Proportion(START_WIDTH, START_HEIGHT, CONTENT_WIDTH, CONTENT_HEIGHT);
    private float animX, animY, animW, animH;
    
    private ProportionMode currentMode = ProportionMode.CONTAIN;
    private ProportionMode targetMode = ProportionMode.CONTAIN;
    
    private final fastui.Container root = new fastui.Container();
    private final float[] tempFrom = new float[4];
    private final float[] tempTo = new float[4];
    private BufferedImage contentImage;

    public Demo() {
        super("FastProportion Demo");
        
        // Setup Window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setBackground(COLOR_BG);

        try {
            contentImage = javax.imageio.ImageIO.read(Demo.class.getResourceAsStream("/image.png"));
            if (contentImage != null) {
                p.contentWidth = contentImage.getWidth();
                p.contentHeight = contentImage.getHeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Setup Icon
        BufferedImage icon = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gi = icon.createGraphics();
        gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gi.setColor(Color.WHITE);
        gi.fillOval(ICON_OFFSET, ICON_OFFSET, ICON_DRAW_SIZE, ICON_DRAW_SIZE);
        gi.dispose();
        this.setIconImage(icon);

        this.addNotify();
        long hwnd = FastTheme.getWindowHandle(this);
        if (hwnd != 0) {
            FastTheme.setTitleBarColor(hwnd, 0, 0, 0);
            FastTheme.setTitleBarTextColor(hwnd, 255, 255, 255);
            FastTheme.setWindowTransparency(hwnd, WINDOW_OPACITY);
        }

        // Initial fallback position
        p.x = 336;
        p.y = 55;

        FastAnimation.setHeartbeatMode(HeartbeatMode.JAVA);
        root.setBackground(COLOR_BG);

        // ==========================================
        // UI COMPONENTS HIERARCHY
        // ==========================================

        // 1. Frame Component (The gray boundary box)
        Component frame = new Component() {
            @Override
            public void onRender(Graphics2D g) {
                g.setColor(COLOR_FRAME);
                g.fillRect((int)getAbsoluteX(), (int)getAbsoluteY(), (int)getWidth(), (int)getHeight());
            }
        };

        // 2. Content Component (The white inner area with the cross)
        Component content = new Component() {
            @Override
            public void onRender(Graphics2D g) {
                int ax = (int)getAbsoluteX(), ay = (int)getAbsoluteY();
                int w = (int)getWidth(), h = (int)getHeight();
                
                if (contentImage != null) {
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g.drawImage(contentImage, ax, ay, w, h, null);
                } else {
                    g.setColor(COLOR_CONTENT_BG);
                    g.fillRect(ax, ay, w, h);
                    g.setColor(COLOR_CONTENT_LINES);
                    g.drawLine(ax, ay, ax + w, ay + h);
                    g.drawLine(ax + w, ay, ax, ay + h);
                }
            }
        };

        // 3. View Container (The root anchor for Frame and Content)
        // This component syncs the absolute bounds before FastUI renders its children.
        // It uses a ClipContainer to ensure the Content never overflows the Frame visually.
        ClipContainer clip = new ClipContainer();
        clip.add(content);
        
        Component view = new Component() {
            @Override
            public void onRender(Graphics2D g) {
                frame.setBounds(p.x, p.y, p.width, p.height);
                clip.setBounds(p.x, p.y, p.width, p.height);
                content.setBounds(animX - p.x, animY - p.y, animW, animH);
            }
            @Override
            public boolean contains(float mx, float my) {
                return true; // Let interactions pass through to children
            }
        };
        view.add(frame);
        view.add(clip);
        root.add(view);

        // 4. Handles (Draggable circular buttons)
        // These utilize FastUI's ImageSwappable and BehaviorButton3x3 logic for 
        // extremely fast visual state transitions (hover/press) without memory leaks.
        BufferedImage imgBase = createHandleImage(HANDLE_SIZE, false, 0, true);
        BufferedImage imgHover = createHandleImage(HANDLE_SIZE, true, 127, true);
        BufferedImage imgPressed = createHandleImage(HANDLE_SIZE, true, 255, true);

        Image moveBtn = new Image(imgBase);
        moveBtn.addBehavior(new fastui.behaviour.BehaviorButton3x3(imgBase, imgHover, imgPressed));
        moveBtn.addBehavior(new BehaviourDragMove((dx, dy) -> {
            p.x += dx;
            p.y += dy;
            switchMode(targetMode);
        }));

        Image resizeBtn = new Image(imgBase);
        resizeBtn.addBehavior(new fastui.behaviour.BehaviorButton3x3(imgBase, imgHover, imgPressed));
        resizeBtn.addBehavior(new BehaviourDragMove((dx, dy) -> {
            p.width = Math.max(MIN_SIZE, p.width + dx);
            p.height = Math.max(MIN_SIZE, p.height + dy);
            switchMode(targetMode);
        }));

        Component handlesLayer = new Component() {
            @Override
            public void onRender(Graphics2D g) {
                moveBtn.setBounds(p.x - HANDLE_OFFSET, p.y - HANDLE_OFFSET, HANDLE_SIZE, HANDLE_SIZE);
                resizeBtn.setBounds(p.x + p.width - HANDLE_OFFSET, p.y + p.height - HANDLE_OFFSET, HANDLE_SIZE, HANDLE_SIZE);
            }
            @Override
            public boolean contains(float mx, float my) {
                return true; // Let interactions pass through to handles
            }
        };
        handlesLayer.add(moveBtn);
        handlesLayer.add(resizeBtn);
        root.add(handlesLayer);

        // 5. Keyboard Controls (Mode switching)
        // Pressing 1,2,3,4 triggers a 300ms FastTween animation that linearly interpolates
        // the Proportion scaling bounds into the new mode.
        root.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                ProportionMode newMode = switch (e.getKeyChar()) {
                    case '1' -> ProportionMode.FIT_HORIZONTAL;
                    case '2' -> ProportionMode.FIT_VERTICAL;
                    case '3' -> ProportionMode.CONTAIN;
                    case '4' -> ProportionMode.COVER;
                    default -> null;
                };
                if (newMode != null && newMode != targetMode) {
                    currentMode = targetMode;
                    targetMode = newMode;
                    animateMode(currentMode, targetMode);
                }
            }
        });

        switchMode(currentMode);
        setContentPane(root);
        setVisible(true);
        
        // Dynamically center the content based on true window metrics after layout
        SwingUtilities.invokeLater(() -> {
            p.x = (root.getWidth() - p.width) / 2f;
            p.y = (root.getHeight() - p.height) / 2f;
            switchMode(currentMode);
            root.requestFocusInWindow();
        });
    }

    private void switchMode(ProportionMode target) {
        updateProgress(1f, target, target);
    }

    private void animateMode(ProportionMode from, ProportionMode to) {
        updateProgress(0f, from, to);
        FastAnimation.parallel(
            FastTween.to(0f, 1f, ANIMATION_DURATION_MS)
                .ease(Ease.LINEAR)
                .onUpdate(t -> updateProgress(t, from, to))
        ).start();
    }

    private void updateProgress(float t, ProportionMode from, ProportionMode to) {
        p.compute(from, tempFrom);
        p.compute(to, tempTo);
        
        animX = tempFrom[0] + (tempTo[0] - tempFrom[0]) * t;
        animY = tempFrom[1] + (tempTo[1] - tempFrom[1]) * t;
        animW = tempFrom[2] + (tempTo[2] - tempFrom[2]) * t;
        animH = tempFrom[3] + (tempTo[3] - tempFrom[3]) * t;
        
        root.repaint();
    }

    private BufferedImage createHandleImage(int size, boolean fill, int fillAlpha, boolean border) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (fill) {
            g.setColor(new Color(255, 255, 255, fillAlpha));
            g.fillOval(1, 1, size - 2, size - 2);
        }
        if (border) {
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(1, 1, size - 3, size - 3);
        }
        
        g.dispose();
        return img;
    }
}
