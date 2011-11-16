package net.ulrice.databinding.bufferedbinding.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ulrice.databinding.validation.ValidationError;

public class UniqueConstraint implements ElementLifecycleListener {

	private String[] columnIds;

	private Map<List<?>, Set<String>> uniqueMap = new HashMap<List<?>, Set<String>>();
	
	private Map<List<?>, Set<String>> uniqueDeleteMap = new HashMap<List<?>, Set<String>>();

	private Map<String, List<?>> keyMap = new HashMap<String, List<?>>();
	
	private Map<String, List<?>> keyDeleteMap = new HashMap<String, List<?>>();

	private Map<List<?>, ValidationError> currentErrorMap = new HashMap<List<?>, ValidationError>();

	public UniqueConstraint(String... columnIds) {
		this.columnIds = columnIds;
	}

	@Override
	public void elementChanged(TableAM table, Element element, String columnId) {
		checkUniqueConstraint(table, element);
	}

	@Override
	public void elementAdded(TableAM table, Element element) {
		checkUniqueConstraint(table, element);
	}

	@Override
	public void elementRemoved(TableAM table, Element element) {
		handleKey(table, element.getUniqueId(), null);
	}

	@Override
	public void tableCleared(TableAM table) {
		uniqueMap.clear();
		keyMap.clear();
	}

	private void checkUniqueConstraint(TableAM table, Element element) {
		if (columnIds == null) {
			return;
		}

		List<?> key = buildKey(element);
		if (handleKey(table, element.getUniqueId(), key)) {
			if (uniqueMap.containsKey(key)) {
				Set<String> uniqueIdSet = uniqueMap.get(key);
				uniqueIdSet.add(element.getUniqueId());
				if (uniqueIdSet.size() > 1) {
					ValidationError uniqueConstraintError = new ValidationError(
							table, "Unique key constraint error", null);
					currentErrorMap.put(key, uniqueConstraintError);
					for (String uniqueId : uniqueIdSet) {
						table.getElementById(uniqueId)
								.addElementValidationError(
										uniqueConstraintError);
					}
				}
			} else {
				Set<String> uniqueIdSet = new HashSet<String>();
				uniqueIdSet.add(element.getUniqueId());
				uniqueMap.put(key, uniqueIdSet);
			}
		}
	}

	private boolean handleKey(TableAM table, String uniqueId, List<?> key) {
		List<?> oldKey = keyMap.get(uniqueId);
		if (oldKey == null && key != null) {
			String oldUniqueId = checkForOldUniqueId(key, table, uniqueId);
			keyMap.put(uniqueId, key);
			return true;
		}

		if (key == null || !oldKey.equals(key)) {
			if (oldKey != null) {
				Set<String> uniqueKeySet = uniqueMap.get(oldKey);
				uniqueKeySet.remove(uniqueId);
				// should not happen
				if (uniqueDeleteMap.containsKey(oldKey)) {
					Set<String> uniqueDeleteKeySet = uniqueDeleteMap.get(oldKey);
					uniqueDeleteKeySet.add(uniqueId);
				}
				else {
					Set<String> uniqueIdSet = new HashSet<String>();
					uniqueIdSet.add(uniqueId);
					uniqueDeleteMap.put(oldKey, uniqueIdSet);
				}
				if (uniqueKeySet.size() <= 1
						&& currentErrorMap.containsKey(oldKey)) {
					ValidationError validationError = currentErrorMap
							.remove(oldKey);
					table.getElementById(uniqueId)
							.removeElementValidationError(validationError);
					for (String uniqueElementId : uniqueKeySet) {
						table.getElementById(uniqueElementId)
								.removeElementValidationError(validationError);
					}

				}
				keyDeleteMap.put(uniqueId, oldKey);
				keyMap.put(uniqueId, key);
			}
			return true;
		}
		return false;
	}

	private List<?> buildKey(Element element) {
	    List<Object> key = new ArrayList<Object>(columnIds.length);
	    for (String columnId : columnIds) {
	        key.add(element.getValueAt(columnId));
		}
	    return key;
	}
	
	private String checkForOldUniqueId(List<?> key, TableAM table, String newUniqueId) {
		String oldUniqueId = null;
		if (keyDeleteMap.containsValue(key)) {
			for (Entry<String, List<?>> entry : keyDeleteMap.entrySet()) {
				if (key.equals(entry.getValue())) {
					oldUniqueId = entry.getKey();
				}
			}
		}
		return oldUniqueId;
	}

    @Override
    public void elementStateChanged(TableAM table, Element element) {
        // TODO Auto-generated method stub
        
    }
}
