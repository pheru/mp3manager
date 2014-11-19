package de.eru.mp3manager.cdi;

import de.eru.mp3manager.data.Mp3FileData;

/**
 *
 * @author Philipp Bruckner
 */
public class CurrentTitleEvent {
    
    private final Mp3FileData newCurrentTitle;
    private final int newCurrentTitleIndex;
    
    public CurrentTitleEvent(Mp3FileData newCurrentTitle, int newCurrentTitleIndex){
        this.newCurrentTitle = newCurrentTitle;
        this.newCurrentTitleIndex = newCurrentTitleIndex;
    }
    
    public int getNewCurrentTitleIndex(){
        return newCurrentTitleIndex;
    }
    public Mp3FileData getNewCurrentTitle(){
        return newCurrentTitle;
    }
    
}
