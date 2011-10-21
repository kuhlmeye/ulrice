package net.ulrice.remotecontrol;

import java.io.Serializable;

public class ComponentTableDataEntry implements Serializable
{

	private static final long serialVersionUID = 7609497754833112047L;

	private Object value;
	private boolean selected;

	public ComponentTableDataEntry(Object value, boolean selected)
	{
		super();
		this.value = value;
		this.selected = selected;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	@Override
	public String toString()
	{
		return (value != null) ? value.toString() : "";
	}

}
