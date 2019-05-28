
package model.data;

import com.badlogic.gdx.Gdx;
import model.GameModel;
import model.being.AbstractEnemy;
import model.being.AbstractPlayer;
import model.collectable.AbstractCollectable;

import java.io.*;
import java.util.Base64;
import java.util.List;


/**
 * Created by Dan Ko on 9/19/2017.
 */
public class GameStateTransactionHandler {
    private GameStateRepository repository;

    public GameStateTransactionHandler() {
        repository = new GameStateRepository();
    }

    public boolean save(GameModel model) {
        GameState newState = new GameState(
                Gdx.app.getPreferences(
                        "save" + repository.latestSaveNum() //always a unique name and should stay dynamic + consistent throughout repo operations
                ));

        if(writeQuery(model, newState)) {
            commit(newState);
            return true;
        }
        return false;
    }

    private boolean writeQuery(GameModel model, GameState newState) {

        /* update the newState with validated data, otherwise signal failed save */
        if(!validateAndUpdatePlayer(newState, model.getPlayer())) {
            return false;
        }
        if(!validateAndUpdateEnemies(newState, model.getEnemies())) {
            return false;
        }
        if(!validateAndUpdateCollectables(newState, model.getCollectables())) {
            return false;
        }
        return true;
    }

    /** sends signal to repo to pull the latestSaveNum state and wrap it in a StateQuery to the model
     *
     * @return
     */
    public StateQuery load() {
        GameState latest = repository.pullSoft(); //TODO is pullSoft necessary?

        if(!latest.containsPlayer() || !latest.containsEnemies()) {
            repository.pullHard(); //cleanse the repo of the bad state
            throw new InvalidTransactionException("Corrupted state");
        }

        try {
            AbstractPlayer validatedPlayer = validateAndReturnPlayer(latest);
            List<AbstractEnemy> validatedEnemies = validateAndReturnEnemies(latest);
            List<AbstractCollectable> validatedCollectables = validateAndReturnCollectables(latest);

            //if all data is valid, remove it from stack finally
            repository.pullHard();
            return new StateQuery(validatedPlayer, validatedEnemies, validatedCollectables); //give the model a loader object to directly call validated data

        } catch (InvalidTransactionException e) {
            repository.pullHard(); //remove corrupted data
            throw new InvalidTransactionException(e.getMessage());
        }
    }


    private void commit(GameState newState) {
        repository.push(newState);
    }
    
    
    /** Serialise the AbstractPlayer obtained from the model and store it
     * into the buffer GameState. Checks if the data to be stored exists in the right type
     * and can be stored correctly
     * @param newState
     * @param newPlayer
     * @return
     */
    @SuppressWarnings("Duplicates")
    public boolean validateAndUpdatePlayer(GameState newState, AbstractPlayer newPlayer) {
        if(newPlayer == null || ! (newPlayer instanceof AbstractPlayer)) {
            return false;
        }
        
        //now serialise the Player object and add to Preferences

        String playerSer = "";
        try {
            playerSer = serializeInBase64(newPlayer);
        
            newState.setEnemiesInPref(playerSer);
            return true;
        
        } catch(IOException e) {
            return false;
        }
    }
    
    /** Serialise the List of enemies obtained from the model and store it
     * into the buffer GameState. Checks if the data to be stored exists in the right type
     * and can be stored correctly
     * @param newState
     * @param newFoes
     * @return
     */
    @SuppressWarnings("Duplicates")
    private boolean validateAndUpdateEnemies(GameState newState, List<AbstractEnemy> newFoes) {
        if(newFoes == null ) {
            return false;
        }
    
        //now serialise the Enemies List and add to Preferences
        String enemiesSer = "";
        try {
            enemiesSer = serializeInBase64(newFoes);
            
            newState.setEnemiesInPref(enemiesSer);
            return true;
            
        } catch(IOException e) {
            return false;
        }
    }

    /** Serialise the List of collectables obtained from the model and store it
     * into the buffer GameState. Checks if the data to be stored exists in the right type
     * and can be stored correctly
     * @param newState
     * @param newItems
     * @return
     */
    @SuppressWarnings("Duplicates")
    private boolean validateAndUpdateCollectables(GameState newState, List<AbstractCollectable> newItems) {
        if(newItems == null ) {
            return false;
        }

        //now serialise the Enemies List and add to Preferences
        String itemsSer = "";
        try {
            itemsSer = serializeInBase64(newItems);

            newState.setEnemiesInPref(itemsSer);
            return true;

        } catch(IOException e) {
            return false;
        }
    }

    /** Deserialize the Player from the GameState and validate that the data is correct
     *
     * @param latest
     * @return validated AbstractPlayer to be loaded into model
     */
    private AbstractPlayer validateAndReturnPlayer(GameState latest) throws InvalidTransactionException{
        try {
            Object p = deserializeFromBase64(latest.getPref().getString("Player"));
            if(!(p instanceof AbstractPlayer))
                throw new InvalidTransactionException("Deserialized player object isn't an AbstractPlayer");

            AbstractPlayer player = (AbstractPlayer) p; //TODO any more checks on this player?

            return player;
        } catch(IOException | ClassNotFoundException e) {
            throw new InvalidTransactionException(e.getMessage());
        }
    }

    /** Deserialize the List of enemies from the GameState and validate that the data is correct
     *
     * @param latest
     * @return validated List of AbstractEnemies to be loaded into model
     */
    private List<AbstractEnemy> validateAndReturnEnemies(GameState latest) throws InvalidTransactionException{
        try {
            Object e = deserializeFromBase64(latest.getPref().getString("Enemies"));

            if(!(e instanceof List))
                throw new InvalidTransactionException("Deserialized enemies object isn't a List");

            List<AbstractEnemy> enemies = (List<AbstractEnemy>) e; 

            for(int i = 0; i < enemies.size(); i++) {
                if(!(enemies.get(i) instanceof AbstractEnemy))
                    throw new InvalidTransactionException("Deserialized enemy list doesn't contain AbstractEnemies");
            }

            return enemies;
        } catch(IOException | ClassNotFoundException e) {
            throw new InvalidTransactionException(e.getMessage());
        }
    }

    /** Deserialize the List of Collectables from the GameState and validate that the data is correct
     *
     * @param latest
     * @return validated List of AbstractCollectables to be loaded into model
     */
    private List<AbstractCollectable> validateAndReturnCollectables(GameState latest) throws InvalidTransactionException{
        try {
            Object c = deserializeFromBase64(latest.getPref().getString("Collectables"));

            if(!(c instanceof List))
                throw new InvalidTransactionException("Deserialized collectables object isn't a List");

            List<AbstractCollectable> collectables = (List<AbstractCollectable>) c;
            
            //check each element in the list
            for(int i = 0; i < collectables.size(); i++) {
                if(!(collectables.get(i) instanceof AbstractCollectable))
                    throw new InvalidTransactionException("Deserialized enemy list doesn't contain Abstractcollectables");
            }

            return collectables;
        } catch(IOException | ClassNotFoundException e) {
            throw new InvalidTransactionException(e.getMessage());
        }
    }


    /* ========================================================================================
       ============================= SERIALIZATION ============================================
       ========================================================================================
     */



    /** Serialise an object and return its bytecode String
     *
     * @param o
     * @return
     * @throws IOException
     */
    private String serializeInBase64(Object o) throws IOException {
        String ser = "";
        try {
            //serialize our object
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(o);
            so.flush(); //persist
        
            ser = new String(Base64.getEncoder().encode(bo.toByteArray())); //convert the bytecode to a character String

            return ser;
        } catch(IOException e) {
            throw new IOException(e.getMessage());
        }
    }


    private Object deserializeFromBase64(String ser) throws IOException, ClassNotFoundException {
        try {
            byte b[] = Base64.getDecoder().decode(ser.getBytes()); //decode the Base64 string into bytecode

            //deserialize the bytecode back into an Object
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);

            return (Object) si.readObject(); //return the object (will be casted in the parent method)
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            if(e instanceof IOException)
                throw new IOException(e.getMessage());
            else
                throw new ClassNotFoundException();
        }
    }


    /** Upon invalid or missing data, this exception will be thrown to rollback all changes
     * done upon the database
     */
    public class InvalidTransactionException extends RuntimeException {
        public InvalidTransactionException(String msg) {
            super(msg);
        }
    }
}
