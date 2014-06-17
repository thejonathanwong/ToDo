package com.jwong.todo.todo.adapters;

/**
 * Created by jonathan on 6/4/2014.
 */

import android.view.View;
import android.view.ViewGroup;

public interface IAdapterViewInflater<T>
{
    public View inflate(BaseInflaterAdapter<T> adapter, int pos, View convertView, ViewGroup parent);
}
