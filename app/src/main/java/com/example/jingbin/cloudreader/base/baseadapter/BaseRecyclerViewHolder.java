package com.example.jingbin.cloudreader.base.baseadapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by jingbin on 2016/11/25
 */
public abstract class BaseRecyclerViewHolder<T, D extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public D binding;

    /**
     * 封装，通过 itemView 获得 binding
     */
    public BaseRecyclerViewHolder(ViewGroup viewGroup, int layoutId) {
        // 注意要依附 viewGroup，不然显示item不全!!!!!!!!!!!!!!!!!!!!!!!!
        super(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), layoutId, viewGroup, false).getRoot());// 这个 super 的参数就是 itemView
        // 得到这个View绑定的Binding
        binding = DataBindingUtil.getBinding(this.itemView);// getBinding ； itemView
        // RecyclerView.ViewHolder的子类都可以通过ViewHolder的public的成员变量itemView来访问每个条目的根视图，所以ViewHolder子类中不需要在保存这个视图。
    }

    /**
     * 强制子类实现
     */
    public abstract void onBindViewHolder(T object, final int position);

    /**
     * 当数据改变时，binding会在下一帧去改变数据，如果我们需要立即改变，就去调用executePendingBindings方法。
     */
    void onBaseBindViewHolder(T object, final int position) {
        onBindViewHolder(object, position);// 调用抽象方法，实际上是调用子类的方法

        // 当变量的值更新的时候，binding 对象将在下个更新周期中更新。这样就会有一点时间间隔，如果你像立刻更新，则可以使用 executePendingBindings 函数。
        binding.executePendingBindings();
    }
}
