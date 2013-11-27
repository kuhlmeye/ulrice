package net.ulrice.databinding.bufferedbinding.impl;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ulrice.databinding.validation.ValidationError;

public class UniqueConstraint implements ElementLifecycleListener {

	private String[] columnIds;

	private Map<List<?>, TLongSet> uniqueMap = new HashMap<List<?>, TLongSet>();
	
	private Map<List<?>, TLongSet> uniqueDeleteMap = new HashMap<List<?>, TLongSet>();

	private TLongObjectMap<List<?>> keyMap = new TLongObjectHashMap<List<?>>();
	
	private TLongObjectMap<List<?>> keyDeleteMap = new TLongObjectHashMap<List<?>>();

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

        List< ?> key = buildKey(element);
        if (handleKey(table, element.getUniqueId(), key)) {
            if (uniqueMap.containsKey(key)) {
                TLongSet uniqueIdSet = uniqueMap.get(key);
                uniqueIdSet.add(element.getUniqueId());
                if (uniqueIdSet.size() > 1) {
                    ValidationError uniqueConstraintError = new ValidationError(table, "Unique key constraint error", null);
                    currentErrorMap.put(key, uniqueConstraintError);
                    for (long uniqueId : uniqueIdSet.toArray()) {
                        table.getElementById(uniqueId).addElementValidationError(uniqueConstraintError);
                    }
                }
            }
            else {
                TLongSet uniqueIdSet = new TLongHashSet();
                uniqueIdSet.add(element.getUniqueId());
                uniqueMap.put(key, uniqueIdSet);
            }
        }
    }

    private boolean handleKey(final TableAM table, final Long uniqueId, final List< ?> key) {
        final List< ?> oldKey = keyMap.get(uniqueId);
        if (oldKey == null && key != null) {
            keyMap.put(uniqueId, key);
            return true;
        }

        if (key == null || !key.equals(oldKey)) {
            if (oldKey != null) {
                final TLongSet uniqueKeySet = uniqueMap.get(oldKey);
                uniqueKeySet.remove(uniqueId);
                // should not happen
                if (uniqueDeleteMap.containsKey(oldKey)) {
                    final TLongSet uniqueDeleteKeySet = uniqueDeleteMap.get(oldKey);
                    uniqueDeleteKeySet.add(uniqueId);
                }
                else {
                    final TLongSet uniqueIdSet = new TLongHashSet();
                    uniqueIdSet.add(uniqueId);
                    uniqueDeleteMap.put(oldKey, uniqueIdSet);
                }
                if (uniqueKeySet.size() <= 1 && currentErrorMap.containsKey(oldKey)) {
                    final ValidationError validationError = currentErrorMap.remove(oldKey);
                    table.getElementById(uniqueId).removeElementValidationError(validationError);
                    for (final long uniqueElementId : uniqueKeySet.toArray()) {
                        table.getElementById(uniqueElementId).removeElementValidationError(validationError);
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

    @Override
    public void elementStateChanged(TableAM table, Element element) {
    }
}
