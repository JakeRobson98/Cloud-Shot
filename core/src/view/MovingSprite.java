package view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import model.GameModel;
import view.screens.GameScreen;

/**
 * MovingSprite handles all the logic to create and return a TextureRegion based on the time elapsed and frame
 * rate of the animation. The TextureRegion is to be rendered by GameScreen.java
 * @author Yi Sian Lim
 */
public class MovingSprite extends CustomSprite {

    /**
     * Stores all the TextureRegion of the frames for animation.
     */
    private TextureRegion[] animationFrames;

    /**
     * Animation will store the frame rates and animation frames to animate.
     */
    private Animation animation;

    /**
     * Number of rows and columns in the sprite image.
     */
    private final int ROWS;
    private final int COLS;

    public MovingSprite(String imageName, int rows, int cols){
        super(imageName);
        this.ROWS = rows;
        this.COLS = cols;
        loadSprite();
    }

    /**
     * createSprite takes the single sprite image and cuts it all up so that each frame consist of one TextureRegion.
     * animation is initialised by storing all the TextureRegion frames and frame rate.
     * The frame rate for the MovingSprite is initialised in the GameScreen class.
     */
    private void loadSprite(){
        // Get the sprite image and split it into TextureRegions consisting of the image during that frame.
        TextureRegion[][] tmpFrames = TextureRegion.split(spriteImage, spriteImage.getWidth() / COLS,
                spriteImage.getHeight() / ROWS);

        // Initialise animationFrames to create the animation.
        animationFrames = new TextureRegion[ROWS * COLS];
        int index = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                animationFrames[index++] = tmpFrames[i][j];
            }
        }

        // Create the animation.
        animation = new Animation(GameScreen.FRAME_RATE, animationFrames);
    }

    /**
     * Return a TextureRegion of the image that should be displayed based on the elapsedTime.
     * @param elapsedTime
     *          Time elapsed so far.
     * @return
     */
    public TextureRegion getFrameFromTime(float elapsedTime){
        TextureRegion r = (TextureRegion) animation.getKeyFrame(elapsedTime, true);

        if(resize) {
            r.setRegionHeight((int) ((float)spriteImage.getHeight() / GameModel.PPM));
            r.setRegionWidth((int) ((float)spriteImage.getWidth() / GameModel.PPM));
        }

//        System.out.println("NAME: " + name);
//        System.out.println("WIDTH: " +  r.getRegionWidth());

        r.flip(horizontal, vertical);
        return r;
    }

}
