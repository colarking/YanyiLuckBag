/*
 * Copyright 2013 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanyi.luckbag.adapter;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A true {@link ArrayList} adapter providing access to all ArrayList methods.
 */
public abstract class AmayaAdapter<T> extends BaseAdapter {

    protected List<T> mItems;

    public AmayaAdapter() {
        this(null);
    }

    public AmayaAdapter(List<T> items) {
        if (items == null) {
            if (mItems == null) mItems = new ArrayList<T>();
        } else if (mItems == null) {
            mItems = items;
        } else {
            mItems.addAll(items);
        }
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(T item) {
        if (item == null) return;
        mItems.add(item);
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified element at the specified position in the list.
     */
    public void add(int position, T item) {
        if (item == null) return;
//        if(position >= mItems.size()) throw new RuntimeException("position is bigger");
        mItems.add(position, item);
        notifyDataSetChanged();
    }

    /**
     * Appends all of the elements in the specified collection to the end of the
     * list, in the order that they are returned by the specified collection's
     * Iterator.
     */
    public void addAll(Collection<? extends T> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addAll(T... items) {
        Collections.addAll(mItems, items);
        notifyDataSetChanged();
    }

    public void addAll(boolean clear, T... items) {
        if (clear) mItems.clear();
        Collections.addAll(mItems, items);
        notifyDataSetChanged();
    }

    public void addAll(int position, Collection<? extends T> items) {
        mItems.addAll(position, items);
        notifyDataSetChanged();
    }

    public void addAll(int position, T... items) {
        for (int i = position; i < (items.length + position); i++) {
            mItems.add(i, items[i]);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     */
    public void set(int position, T item) {
        mItems.set(position, item);
        notifyDataSetChanged();
    }

    /**
     * Removes the specified element from the list
     */
    public void remove(T item) {
        mItems.remove(item);
        notifyDataSetChanged();
    }

    /**
     * Removes the element at the specified position in the list
     */
    public void remove(int position) {
        mItems.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Removes all elements at the specified positions in the list
     */
    public void removePositions(Collection<Integer> positions) {
        ArrayList<Integer> positionsList = new ArrayList<Integer>(positions);
        Collections.sort(positionsList);
        Collections.reverse(positionsList);
        for (int position : positionsList) {
            mItems.remove(position);
        }
        notifyDataSetChanged();
    }

    /**
     * Removes all of the list's elements that are also contained in the
     * specified collection
     */
    public void removeAll(Collection<T> items) {
        mItems.removeAll(items);
        notifyDataSetChanged();
    }

    /**
     * Retains only the elements in the list that are contained in the specified
     * collection
     */
    public void retainAll(Collection<T> items) {
        mItems.retainAll(items);
        notifyDataSetChanged();
    }

    /**
     * Returns the position of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element. More
     * formally, returns the lowest position <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such position.
     */
    public int indexOf(T item) {
        return mItems.indexOf(item);
    }


    public List<T> getItems() {
        return mItems;
    }

    public void addAll(List<T> beans, boolean clear) {
        if (clear) clear();
        addAll(beans);
        notifyDataSetChanged();
    }


}
