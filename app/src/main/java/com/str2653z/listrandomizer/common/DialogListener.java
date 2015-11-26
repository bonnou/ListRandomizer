package com.str2653z.listrandomizer.common;

import java.util.EventListener;

/**
 * Created by str2653z on 2015/11/25.
 */
public interface DialogListener extends EventListener {

    /**
     * OKボタンが押されたイベントを通知
     */
    public void doPositiveClick();

    /**
     * Cancelボタンが押されたイベントを通知
     */
    public void doNegativeClick();
}