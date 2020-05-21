package com.sciome.bmdexpress2.util;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class ShapeCreator {
	private final static double SQRT2 = Math.sqrt(2);
	/**
	 * Creates a diagonal cross shape.
	 *
	 * @param l
	 *            the length of each 'arm'.
	 * @param t
	 *            the thickness.
	 *
	 * @return A diagonal cross shape.
	 */
	public static Shape createDiagonalCross(final double l, final double t) {
		final GeneralPath p0 = new GeneralPath();
		p0.moveTo(-l - t, -l + t);
		p0.lineTo(-l + t, -l - t);
		p0.lineTo(0.0f, -t * SQRT2);
		p0.lineTo(l - t, -l - t);
		p0.lineTo(l + t, -l + t);
		p0.lineTo(t * SQRT2, 0.0f);
		p0.lineTo(l + t, l - t);
		p0.lineTo(l - t, l + t);
		p0.lineTo(0.0f, t * SQRT2);
		p0.lineTo(-l + t, l + t);
		p0.lineTo(-l - t, l - t);
		p0.lineTo(-t * SQRT2, 0.0f);
		p0.closePath();
		return p0;
	}
	
	public static Shape createDiamond(final double width, final double height) {
		final GeneralPath path = new GeneralPath();
        path.moveTo(0, height / 2);
        path.lineTo(width / 2, 0);
        path.lineTo(width, height / 2);
        path.lineTo(width / 2, height);
        path.closePath();
        return path;
    }
}
