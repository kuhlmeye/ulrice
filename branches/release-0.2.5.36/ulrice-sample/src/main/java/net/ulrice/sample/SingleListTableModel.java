package net.ulrice.sample;

import java.util.List;

import net.ulrice.databinding.bufferedbinding.impl.TableAM;


public class SingleListTableModel <T> extends TableAMBuilder {
    private TableAM attributeModel;
    private List<T> data;

    public SingleListTableModel(Class<T> modelRowClass) {
        init (this, "data", modelRowClass);
    }

    public List<T> getData() {
        return data;
    }
    
    public void setData(List<T> data) {
        this.data = data;
    }
    
    @Override
    public TableAM build() {
        attributeModel = super.build();
        return attributeModel;
    }
    
    public TableAM getAttributeModel() {
        if (attributeModel == null) {
            attributeModel = super.build();
        }
        return attributeModel;
    }
}
