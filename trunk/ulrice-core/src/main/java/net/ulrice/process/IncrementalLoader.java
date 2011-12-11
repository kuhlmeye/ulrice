package net.ulrice.process;

import java.util.List;

import net.ulrice.Ulrice;
import net.ulrice.module.IFController;


/**
 * This class loads data incrementally in a background thread. (replacement for N4IncrementalLoadWorker)
 * 
 * new IncrementalLoader (new IncrementalDataProvoder <AreaFullTO> () {
 *   public int getNumRows() {
 *     return areaService.getNumAreas (salesCompany);
 *   }
 *   ...
 * }
 * 
 * @author ehaasec
 */
public class IncrementalLoader<T> extends AbstractProcess<T, List<T>>{

    //TODO cancellation support?
    private final int chunkSize;
    private final int upperBound;
    private final IncrementalDataProvider<T> provider;
    
    private int totalNumRows;
    private int numPublished = 0;
    private int numLoaded = 0;
    
    public IncrementalLoader (IFController owner, boolean blockController, IncrementalDataProvider<T> provider) {
        this (owner, blockController, 100, Integer.MAX_VALUE, provider);
    }
        
    public IncrementalLoader (IFController owner, boolean blockController, int chunkSize, int upperBound, IncrementalDataProvider<T> provider) {
        super(owner, blockController);
        this.chunkSize = chunkSize;
        this.upperBound = upperBound;
        this.provider = provider;
    }
    
    @Override
    protected T work() throws Exception {
        totalNumRows = provider.getNumRows();
        final int max = Math.min (upperBound, totalNumRows);
        
        while (numLoaded < max && !isCancelled()) {
            final List<T> chunk = provider.getData (numLoaded, chunkSize);
            publish(chunk);
            numLoaded += chunk.size();
            int loadedPercent = Math.round(100.0f / totalNumRows * numLoaded);
            setProgress(loadedPercent > 100 ? 100 : loadedPercent);
            fireProgressChanged();
            
            if(chunk.size() == 0) {
                break;
            }
        }
        return null;
    }
    
    @Override
    protected void process(List<List<T>> chunkList) {        
        super.process(chunkList);        
        if(chunkList != null)  {
            for(List<T> chunk : chunkList) { 
                if(chunk != null) {
                    try {
                        provider.onChunkLoaded(chunk, numPublished);
                    }
                    catch (Exception e) {
                        Ulrice.getMessageHandler().handleException(getOwningController(), e);
                    }
                    numPublished += chunk.size();
                }
            }
        }
    }

    @Override
    protected void finished(T result) {
        try {
            provider.onFinished();
        }
        catch (Exception e) {
            Ulrice.getMessageHandler().handleException(getOwningController(), e);
        }
    }
    
    @Override
    protected void failed(Throwable t) {
        Ulrice.getMessageHandler().handleException(getOwningController(), t);
    }

    @Override
    public boolean hasProgressInformation() {
        return true;
    }
    
    @Override
    public boolean supportsCancel() {
        return true;
    }
    
    @Override
    public void cancelProcess() {
        cancel(true);
    }
}






