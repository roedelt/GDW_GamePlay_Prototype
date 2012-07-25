package tiled.test.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Shape;

public class ColorBucket extends GameObject
{
	public ColorBucket(Shape s, Color color) {
		super(s, color);
	}
	
	@Override
	public void update(long delta) {
		// TODO Auto-generated method stub
		
	}
	
	public Color getColor() { return color; }
}