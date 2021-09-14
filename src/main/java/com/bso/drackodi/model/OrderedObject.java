package com.bso.drackodi.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
public class OrderedObject {
    private final Object implementation;
    private final boolean primary;

    @Setter
    private int order = -1;

    public OrderedObject(Object implementation, boolean primary) {
        this.implementation = implementation;
        this.primary = primary;
        if (primary) {
            order = 0;
        }
    }

    public static class OrderedObjectComparator implements Comparator<OrderedObject> {
        @Override
        public int compare(OrderedObject o1, OrderedObject o2) {
            int primaryResult = o1.isPrimary() ? -1 : 1;
            return o1.isPrimary() == o2.isPrimary() ? 0 : primaryResult;
        }
    }
}
