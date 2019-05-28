package model.mapObject.terrain;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import view.CustomSprite;
import view.MovingSprite;
import view.StaticSprite;

/**
 * AbstractTerrain implementation.
 * The difference between Ground and Platform is that ground extends up from the very bottom of a window.
 */
public class Ground extends AbstractTerrain {

    private Rectangle groundPiece;

    public Ground(int startingX, int width, int height) {
        groundPiece = new Rectangle(startingX, 0, width, height);
    }

    @Override
    public float getX() {
        return groundPiece.getX();
    }

    @Override
    public float getY() {
        return groundPiece.getY();
    }

    @Override
    public Rectangle getBoundingbox() {
        return groundPiece;
    }

    @Override
    public CustomSprite getImage() {
        return new StaticSprite("ground.png",(int)groundPiece.getWidth(),(int)groundPiece.getHeight());
    }
}
