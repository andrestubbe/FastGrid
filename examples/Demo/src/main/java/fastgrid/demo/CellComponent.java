package fastgrid.demo;

import fastgrid.Cell;
import fastgrid.Rect;
import fastgrid.RatioUtil;
import fastui.component.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CellComponent extends Component {

    public final Cell cell;
    public BufferedImage image;  // optional — set after loading

    private final java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform();
    private final java.awt.geom.Rectangle2D.Float fallbackRect = new java.awt.geom.Rectangle2D.Float();

    public CellComponent(Cell cell) {
        this.cell = cell;
    }

    @Override
    public void onRender(Graphics2D g) {
        Rect outer = cell.outer;
        Rect inner = cell.inner;

        // Recompute inner fit using precomputed aspect — zero new objects
        RatioUtil.fit(outer, inner, cell.aspect);

        // Frustum culling / clipping: if not visible, skip drawing
        if (root != null) {
            float viewHeight = root.getHeight();
            if (inner.y + inner.h < 0 || inner.y > viewHeight) {
                return;
            }
        }

        if (image != null) {
            transform.setToIdentity();
            transform.translate(inner.x, inner.y);
            transform.scale(inner.w / image.getWidth(), inner.h / image.getHeight());
            g.drawImage(image, transform, null);
        } else {
            // Fallback: white rectangle
            g.setColor(Color.WHITE);
            fallbackRect.setRect(inner.x, inner.y, inner.w, inner.h);
            g.fill(fallbackRect);
        }
    }
}
