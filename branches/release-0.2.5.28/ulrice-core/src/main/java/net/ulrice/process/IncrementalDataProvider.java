package net.ulrice.process;

import java.util.List;


public interface IncrementalDataProvider <T> {
    List<T> getData (int firstRow, int maxNumRows) throws Exception;
    int getNumRows() throws Exception;
    
    void onChunkLoaded (List<T> chunk, int firstRow) throws Exception;
    void onFinished () throws Exception;
}
