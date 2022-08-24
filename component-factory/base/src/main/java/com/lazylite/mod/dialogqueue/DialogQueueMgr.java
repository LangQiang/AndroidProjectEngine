package com.lazylite.mod.dialogqueue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DialogQueueMgr {

    private final ConcurrentLinkedQueue<IDialogQueue> dialogQueues = new ConcurrentLinkedQueue<>();

    private final OnDialogQueueDismissListener onDialogQueueDismissListener = this::removeDialog;

    public static DialogQueueMgr getInstance() {
        return Inner.INSTANCE;
    }

    static class Inner {
        private static final DialogQueueMgr INSTANCE = new DialogQueueMgr();
    }

    public void showDialog(IDialogQueue dialogQueue) {
        dialogQueue.setOnDialogQueueDismissListener(onDialogQueueDismissListener);
        if (dialogQueues.isEmpty()) {
            dialogQueue.showDialog();
        }
        dialogQueues.offer(dialogQueue);
    }

    public void removeDialog() {
        dialogQueues.poll();
        IDialogQueue peek = dialogQueues.peek();
        if (peek != null) {
            peek.showDialog();
        }
    }
}
