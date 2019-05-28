package model.collectable;

import com.badlogic.gdx.math.Vector2;

import model.being.AbstractPlayer;
import view.CustomSprite;

public class SlowPack extends AbstractBuff {

	public SlowPack(Vector2 position, int width, int height) {
		super(position, width, height);
	}

	@Override
	public CustomSprite getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void pickedUp(AbstractPlayer p) {
		Vector2 oldVelocity = p.getVelocity();
		p.setVelocity(oldVelocity.scl((float) 0.5));
	}

}
