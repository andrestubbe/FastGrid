package fastgrid;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class CellRenderer {

    public static void render(Graphics2D g, Cell c) {
        g.setColor(Color.WHITE);
        Rectangle2D.Float r = c.inner;
        g.fill(new Rectangle2D.Float(r.x, r.y, r.width, r.height));
    }
}

