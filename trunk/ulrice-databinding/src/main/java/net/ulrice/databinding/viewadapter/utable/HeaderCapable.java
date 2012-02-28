package net.ulrice.databinding.viewadapter.utable;

public interface HeaderCapable {

    /**
     * Header row flag for collapse rows with UTableVAFilter
     */
    public abstract boolean isHeader();

    /**
     * Is the 'virtual node' from the header row collapsed
     * 
     * @return
     */
    public abstract boolean isCollapsed();

}
