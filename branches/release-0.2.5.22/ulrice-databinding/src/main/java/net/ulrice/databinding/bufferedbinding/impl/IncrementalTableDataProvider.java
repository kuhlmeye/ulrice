package net.ulrice.databinding.bufferedbinding.impl;

import java.util.List;

public interface IncrementalTableDataProvider {

    boolean isBlocking();
    int getUpperBound();
    int getChunkSize();
    int getNumRows();
    List<?> getNextChunk(int start, int len);
}
