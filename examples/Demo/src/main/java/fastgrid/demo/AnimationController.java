package fastgrid.demo;

import fastgrid.*;

import fastanimation.FastAnimation;
import fastanimation.Animation;
import fasttween.FastTween;
import fasttween.Tween;

public class AnimationController {

    private final FastGridView panel;
    private final LayoutController layout;

    private Animation layoutAnim;
    private Animation columnAnim;

    public AnimationController(FastGridView panel, LayoutController layout) {
        this.panel = panel;
        this.layout = layout;
    }

    public void animateLayout(LayoutMode target) {
        if (layoutAnim != null && layoutAnim.isRunning()) {
            layoutAnim.stop();
        }

        // setMode snapshots current visual state and resets t=0 internally
        layout.setMode(target);

        Tween tween = FastTween.to(0f, 1f, 400)
                .ease(fasttween.Ease.CUBIC_OUT)
                .onUpdate(val -> {
                    layout.setLayoutT(val);
                    panel.repaint();
                })
                .onComplete(() -> {
                    layout.setLayoutT(1f);
                    panel.repaint();
                });

        layoutAnim = FastAnimation.sequence(tween).start();
    }

    public void snapColumns(float currentColumns) {
        float snapped = Math.max(2f, Math.round(currentColumns));
        float start = currentColumns;
        float end   = snapped;

        if (columnAnim != null && columnAnim.isRunning()) {
            columnAnim.stop();
        }

        Tween tween = FastTween.to(start, end, 300)
                .ease(fasttween.Ease.CUBIC_OUT)
                .onUpdate(val -> {
                    panel.columns = val;
                    panel.targetColumns = val;
                    layout.invalidate();
                    panel.repaint();
                })
                .onComplete(() -> {
                    panel.columns = end;
                    panel.targetColumns = end;
                    layout.invalidate();
                    panel.repaint();
                });

        columnAnim = FastAnimation.sequence(tween).start();
    }

    private Animation proportionAnim;

    public void animateProportion() {
        if (proportionAnim != null && proportionAnim.isRunning()) {
            proportionAnim.stop();
        }

        Tween tween = FastTween.to(0f, 1f, 400)
                .ease(fasttween.Ease.CUBIC_OUT)
                .onUpdate(val -> {
                    panel.proportionT = val;
                    panel.repaint();
                })
                .onComplete(() -> {
                    panel.proportionT = 1f;
                    panel.proportionMode = panel.targetProportionMode;
                    panel.repaint();
                });

        proportionAnim = FastAnimation.sequence(tween).start();
    }
}
