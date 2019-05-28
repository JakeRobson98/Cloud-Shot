package model.collectable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import model.GameModel;
import model.GameObjectInterface;


import model.being.AbstractPlayer;


import view.CustomSprite;
import view.MovingSprite;


public abstract class AbstractCollectable implements GameObjectInterface, java.io.Serializable{
	

	protected Vector2 pos;
	protected Rectangle boundingBox;
	protected boolean pickedUp = false;
	
	public final float COLLECTABLE_WIDTH = 10;
	public final float COLLECTABLE_HIEGHT = 10;

	
	public AbstractCollectable(Vector2 position, float width, float height){
		this.pos = new Vector2(position.x/GameModel.PPM, position.y/GameModel.PPM);
		this.boundingBox = new Rectangle(pos.x, pos.y, width/GameModel.PPM, height/GameModel.PPM);
	}
	
	public boolean checkCollide(AbstractPlayer p){
		if(this.isPickedUp()){return false;}
		if(p.getBoundingBox().overlaps(this.getBoundingBox())){
			this.pickedUp = true;
			this.pickedUp(p);
			return true;
		}
		return false;
	}
			
	/**
	 * @return the pickedUp
	 */
	public boolean isPickedUp() {
		return pickedUp;
	}

	/**
	 * @param pickedUp the pickedUp to set
	 */
	public void setPickedUp(boolean pickedUp) {
		this.pickedUp = pickedUp;
	}

	public Rectangle getBoundingBox() {
		return boundingBox;
	}


	public void setBoundingBox(Rectangle boundingBox) {
		this.boundingBox = boundingBox;
	}

	
	@Override
	public float getX() {
		return (float)pos.x;
	}

	@Override
	public float getY() {
		return (float)pos.y;
	}
	
	@Override
	public abstract CustomSprite getImage();

	public abstract void pickedUp(AbstractPlayer p);

	

}
