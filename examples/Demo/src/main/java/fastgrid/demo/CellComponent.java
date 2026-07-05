package fastgrid.demo;

import fastgrid.Cell;
import fastgrid.Rect;
import fastproportion.Proportion;
import fastproportion.ProportionMode;
import fastui.component.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CellComponent extends Component {

    public final Cell cell;
    public final FastGridView view;
    public BufferedImage image;  // optional — set after loading

    private final java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform();
    private final java.awt.geom.Rectangle2D.Float fallbackRect = new java.awt.geom.Rectangle2D.Float();
    private final java.awt.geom.Rectangle2D.Float clipRect = new java.awt.geom.Rectangle2D.Float();

    private final Proportion proportion = new Proportion(0, 0, 0, 0);
    private final float[] pOut = new float[4];

    public CellComponent(FastGridView view, Cell cell) {
        this.view = view;
        this.cell = cell;
    }

    private float lastOuterW = -1;
    private float lastOuterH = -1;
    private ProportionMode lastMode = null;
    private float offsetX = 0;
    private float offsetY = 0;

    @Override
    public void onRender(Graphics2D g) {
        Rect outer = cell.outer;
        Rect inner = cell.inner;

        // If animation is running (proportionT < 1f), we must lerp
        if (view.proportionT < 1f) {
            // Calculate current mode
            proportion.x = 0f;
            proportion.y = 0f;
            proportion.width = outer.w;
            proportion.height = outer.h;
            proportion.contentWidth = cell.aspect;
            proportion.contentHeight = 1f;

            proportion.compute(view.proportionMode, pOut);
            float startOffsetX = pOut[0];
            float startOffsetY = pOut[1];
            float startW = pOut[2];
            float startH = pOut[3];

            // Calculate target mode
            proportion.compute(view.targetProportionMode, pOut);
            float targetOffsetX = pOut[0];
            float targetOffsetY = pOut[1];
            float targetW = pOut[2];
            float targetH = pOut[3];

            // Lerp
            offsetX = startOffsetX + (targetOffsetX - startOffsetX) * view.proportionT;
            offsetY = startOffsetY + (targetOffsetY - startOffsetY) * view.proportionT;
            inner.w = startW + (targetW - startW) * view.proportionT;
            inner.h = startH + (targetH - startH) * view.proportionT;

            // reset cache so it recomputes properly when done
            lastMode = null;
        } else {
            // Proportion Math Caching: Only recompute if width, height or mode changed.
            if (outer.w != lastOuterW || outer.h != lastOuterH || view.targetProportionMode != lastMode) {
                lastOuterW = outer.w;
                lastOuterH = outer.h;
                lastMode = view.targetProportionMode;

                // Calculate with a relative origin (0,0)
                proportion.x = 0f;
                proportion.y = 0f;
                proportion.width = outer.w;
                proportion.height = outer.h;
                proportion.contentWidth = cell.aspect;
                proportion.contentHeight = 1f;

                proportion.compute(view.targetProportionMode, pOut);
                offsetX = pOut[0];
                offsetY = pOut[1];
                inner.w = pOut[2];
                inner.h = pOut[3];
            }
        }

        // Apply absolute position based on current layout/scroll
        inner.x = outer.x + offsetX;
        inner.y = outer.y + offsetY;

        // Apply clipping to the outer bounds so COVER / FIT overflowing doesn't leak out of the cell
        Shape oldClip = g.getClip();
        clipRect.setRect(outer.x, outer.y, outer.w, outer.h);
        g.clip(clipRect);

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

        // Restore original clip
        g.setClip(oldClip);
    }
}
