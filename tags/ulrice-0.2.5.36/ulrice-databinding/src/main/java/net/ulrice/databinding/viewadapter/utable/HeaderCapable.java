package net.ulrice.databinding.viewadapter.utable;

public interface HeaderCapable {

    /**
     * Header row flag for collapse rows with UTableVAFilter
     */
    boolean isHeader();

    /**
     * Is the 'virtual node' from the header row collapsed
     * 
     * @return
     */
    boolean isCollapsed();

}
