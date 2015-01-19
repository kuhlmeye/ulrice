package net.ulrice.module;

/**
 * Created by christof on 18.12.14.
 */
public class ModuleParam {

    private final String key;
    private final Object param;

    public ModuleParam(String key, Object param) {
        this.key = key;
        this.param = param;
    }

    public String getKey() {
        return key;
    }

    public Object getParam() {
        return param;
    }
}
