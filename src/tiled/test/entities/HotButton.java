package tiled.test.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Shape;

public class HotButton extends GameObject {
	private boolean isPressed;
	private String triggeredEntity;
	
	public HotButton(Shape s, Color color, String triggeredEntity)
	{
		super(s, color);
		isPressed = false;
		this.triggeredEntity = triggeredEntity;
	}

	@Override
	public void update(long delta) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void draw(Graphics g) {
		super.draw(g);
	}

	public boolean isPressed() { return this.isPressed; }
	public void press()
	{
		if (!isPressed)
		{
			isPressed = true;
			//search for triggered Entity....
		}
	}
}
