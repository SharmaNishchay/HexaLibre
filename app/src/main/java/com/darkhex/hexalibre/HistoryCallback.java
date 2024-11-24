package com.darkhex.hexalibre;

import java.util.List;

public interface HistoryCallback {
    void onHistoryFetched(List<History>histories);
    void onHistory_notFetched();
}
