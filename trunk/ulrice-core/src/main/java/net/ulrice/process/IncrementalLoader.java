package net.ulrice.process;

import java.util.List;

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
public class IncrementalLoader<T> extends AbstractProcess<T, Object>{

    //TODO cancellation support?
    private final int chunkSize;
    private final int upperBound;
    private final IncrementalDataProvider<T> provider;
    
    private int totalNumRows;
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
        
        while (numLoaded < max) {
            final List<T> chunk = provider.getData (numLoaded, chunkSize);
            provider.onChunkLoaded (chunk, numLoaded);
            numLoaded += chunk.size();
            setProgress(Math.round(100.0f / totalNumRows * numLoaded));
            fireProgressChanged();
            
            if(chunk.size() == 0) {
                break;
            }
        }
        provider.onFinished();
        return null;
    }

    @Override
    protected void finished(T result) {
        //TODO cleanup?
    }
}






