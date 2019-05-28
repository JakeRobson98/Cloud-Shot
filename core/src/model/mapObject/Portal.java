package model.mapObject;

import model.GameObjectInterface;
import view.CustomSprite;
import view.MovingSprite;

public class Portal implements GameObjectInterface {

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public CustomSprite getImage() {
        return null;
    }
}
