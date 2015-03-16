package net.ulrice.databinding.viewadapter.utable;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.Painter;

public class UTableVAHeaderPainter implements Painter<UTableVAHeader> {

    public UTableVAHeaderPainter() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.Painter#paint(java.awt.Graphics2D, java.lang.Object, int, int)
     */
    @Override
    public void paint(Graphics2D g, UTableVAHeader c, int width, int height) {
        g.setColor(Color.WHITE);
        g.drawLine(0, height - 3, width, height - 3);
        g.drawLine(0, height - 1, width, height - 1);

        g.setColor(new Color(0xc0c0c0));
        g.drawLine(0, height - 2, width, height - 2);
    }

}
