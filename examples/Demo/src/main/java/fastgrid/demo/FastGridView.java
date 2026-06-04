package fastgrid.demo;

import fastgrid.Cell;
import fastgrid.Rect;
import fastui.component.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FastGridView extends Component {

    public float columns       = 5f;
    public float targetColumns  = 5f;
    public float gapScale      = 1f;
    public final float baseGap  = 10f;
    public float scrollY        = 0f;

    // Loading callback: (loaded, total)
    public java.util.function.BiConsumer<Integer, Integer> onProgress;
    
    // FPS callback
    public java.util.function.Consumer<Integer> onFpsUpdate;

    public final LayoutController layout;
    public final InteractionController input;
    public final AnimationController anim;

    public FastGridView() {
        this.setHitTestable(true);
        this.layout = new LayoutController(this);
        this.anim   = new AnimationController(this, layout);
        this.input  = new InteractionController(this, layout, anim);
    }

    /**
     * Finds all images recursively in the directory.
     * Extracts their true aspect ratio to create perfectly fitting Cells.
     */
    public void loadImages(File imageDir) {
        if (!imageDir.exists() || !imageDir.isDirectory()) return;

        List<File> allFiles = new ArrayList<>();
        findImagesRecursively(imageDir, allFiles);

        System.out.println("Found " + allFiles.size() + " image files in " + imageDir.getAbsolutePath());
        if (allFiles.isEmpty()) return;

        Thread loader = new Thread(() -> {
            int MULTIPLIER = 10;
            List<Cell> newCells = new ArrayList<>(allFiles.size() * MULTIPLIER);
            List<CellComponent> newComponents = new ArrayList<>(allFiles.size() * MULTIPLIER);

            int index = 0;
            for (int m = 0; m < MULTIPLIER; m++) {
                for (File f : allFiles) {
                    try {
                        BufferedImage raw = ImageIO.read(f);
                        if (raw != null) {
                            float rw = raw.getWidth();
                            float rh = raw.getHeight();

                            // Cap width at 600px to save Heap & VRAM
                            float scale = Math.min(1f, 600f / rw);
                            int tw = (int) (rw * scale);
                            int th = (int) (rh * scale);

                            // Convert to hardware-accelerated VRAM compatible image
                            GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                    .getDefaultScreenDevice().getDefaultConfiguration();
                            BufferedImage img = gc.createCompatibleImage(tw, th, Transparency.TRANSLUCENT);
                            Graphics2D g = img.createGraphics();
                            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            g.drawImage(raw, 0, 0, tw, th, null);
                            g.dispose();

                            Cell cell = new Cell(rw, rh);
                            cell.index = index++;

                            CellComponent comp = new CellComponent(cell);
                            comp.image = img;

                            newCells.add(cell);
                            newComponents.add(comp);

                            // Progress callback
                            if (onProgress != null) {
                                int loadedCount = newCells.size();
                                int totalCount = allFiles.size() * MULTIPLIER;
                                EventQueue.invokeLater(() -> onProgress.accept(loadedCount, totalCount));
                            }

                            // Progressive load: update UI every 5 images
                            if (newCells.size() % 5 == 0) {
                                List<Cell> batchCells = new ArrayList<>(newCells);
                                List<CellComponent> batchComps = new ArrayList<>(newComponents);
                                EventQueue.invokeLater(() -> {
                                    if (this.children != null) this.children.clear();
                                    for (CellComponent c : batchComps) this.add(c);
                                    layout.setCells(batchCells);
                                    repaint();
                                });
                            }
                        }
                    } catch (IOException ignored) {}
                }
            }
            
            System.out.println("Successfully loaded " + newCells.size() + " images.");

            EventQueue.invokeLater(() -> {
                if (this.children != null) this.children.clear();
                for (CellComponent comp : newComponents) this.add(comp);
                layout.setCells(newCells);
                
                if (onProgress != null) {
                    int totalCount = allFiles.size() * MULTIPLIER;
                    onProgress.accept(totalCount, totalCount);
                }
                
                repaint();
            });
        }, "image-loader");

        loader.setDaemon(true);
        loader.start();
    }

    private void findImagesRecursively(File dir, List<File> result) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                findImagesRecursively(f, result);
            } else {
                String lower = f.getName().toLowerCase();
                if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp")) {
                    result.add(f);
                }
            }
        }
    }

    public void scroll(float dy) {
        scrollY += dy;
        if (scrollY > 0) scrollY = 0;

        float contentHeight = (float) layout.getPreferredSize().getHeight();
        float viewHeight    = root != null ? root.getHeight() : 610f;
        float minScroll     = contentHeight > viewHeight ? -(contentHeight - viewHeight) : 0f;
        if (scrollY < minScroll) scrollY = minScroll;

        repaint();
    }

    // FPS tracking
    private long lastFpsTime = 0;
    private int frames = 0;
    private int currentFps = 0;

    @Override
    public void onRender(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        long now = System.nanoTime();
        frames++;
        if (now - lastFpsTime >= 1_000_000_000L) {
            currentFps = frames;
            frames = 0;
            lastFpsTime = now;
            if (onFpsUpdate != null) {
                int fps = currentFps;
                EventQueue.invokeLater(() -> onFpsUpdate.accept(fps));
            }
        }

        if (root != null) {
            if (this.width != root.getWidth()) {
                layout.invalidate();
            }
            this.width  = root.getWidth();
            this.height = root.getHeight();
        }

        Rect[] rects = layout.getRects();
        List<Cell> cells = layout.getCells();

        if (this.children == null || this.children.size() != cells.size()) return;

        for (int i = 0; i < cells.size(); i++) {
            Rect r = rects[i];
            Cell c = cells.get(i);

            float drawY = r.y + scrollY;
            c.outer.set(r.x, drawY, r.w, r.h);

            Component child = this.children.get(i);
            child.setBounds(r.x, drawY, r.w, r.h);
        }
    }
}
