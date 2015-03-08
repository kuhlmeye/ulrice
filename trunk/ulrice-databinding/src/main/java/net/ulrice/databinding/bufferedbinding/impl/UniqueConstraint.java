package net.ulrice.databinding.bufferedbinding.impl;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.ulrice.databinding.validation.ValidationError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniqueConstraint implements ElementLifecycleListener {

	private String[] columnIds;

	private Map<List<?>, LongSet> uniqueMap = new HashMap<>();
	
	private Map<List<?>, LongSet> uniqueDeleteMap = new HashMap<>();

	private Long2ObjectMap<List<?>> keyMap = new Long2ObjectOpenHashMap<>();
	
	private Long2ObjectMap<List<?>> keyDeleteMap = new Long2ObjectOpenHashMap<>();

	private Map<List<?>, ValidationError> currentErrorMap = new HashMap<>();

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
                LongSet uniqueIdSet = uniqueMap.get(key);
                uniqueIdSet.add(element.getUniqueId());
                if (uniqueIdSet.size() > 1) {
                    ValidationError uniqueConstraintError = new ValidationError(table, "Unique key constraint error", null);
                    currentErrorMap.put(key, uniqueConstraintError);
                    for (long uniqueId : uniqueIdSet) {
                        table.getElementById(uniqueId).addElementValidationError(uniqueConstraintError);
                    }
                }
            }
            else {
                LongSet uniqueIdSet = new LongOpenHashSet();
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
                final LongSet uniqueKeySet = uniqueMap.get(oldKey);
                uniqueKeySet.remove(uniqueId);
                // should not happen
                if (uniqueDeleteMap.containsKey(oldKey)) {
                    final LongSet uniqueDeleteKeySet = uniqueDeleteMap.get(oldKey);
                    uniqueDeleteKeySet.add(uniqueId);
                }
                else {
                    final LongSet uniqueIdSet = new LongOpenHashSet();
                    uniqueIdSet.add(uniqueId);
                    uniqueDeleteMap.put(oldKey, uniqueIdSet);
                }
                if (uniqueKeySet.size() <= 1 && currentErrorMap.containsKey(oldKey)) {
                    final ValidationError validationError = currentErrorMap.remove(oldKey);
                    table.getElementById(uniqueId).removeElementValidationError(validationError);
                    for (final long uniqueElementId : uniqueKeySet) {
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
	    List<Object> key = new ArrayList<>(columnIds.length);
	    for (String columnId : columnIds) {
	        key.add(element.getValueAt(columnId));
		}
	    return key;
	}

    @Override
    public void elementStateChanged(TableAM table, Element element) {
    }
}
