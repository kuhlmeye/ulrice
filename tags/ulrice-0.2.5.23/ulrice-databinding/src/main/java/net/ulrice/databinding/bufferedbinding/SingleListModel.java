package net.ulrice.databinding.bufferedbinding;

import java.util.ArrayList;
import java.util.List;

import net.ulrice.databinding.bufferedbinding.impl.TableAM;


public class SingleListModel <T> extends TableAMBuilder {
    private TableAM attributeModel;
    private List<T> listData;

    public SingleListModel(Class<T> modelRowClass) {
        super();
        init(this, "listData", modelRowClass, new DefaultTableAMBuilderCallback());
    }

    public List<T> getData() {
        return listData;
    }
    
    public void setData(List<T> data) {
        this.listData = data;
    }
    
    public void addData(List<T> data) {
        if (this.listData == null) {
            this.listData = new ArrayList<T>(data.size());
        }
        this.listData.addAll(data);
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
