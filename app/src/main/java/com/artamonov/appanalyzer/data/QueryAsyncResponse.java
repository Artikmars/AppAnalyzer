package com.artamonov.appanalyzer.data;

import com.artamonov.appanalyzer.data.database.AppList;
import java.util.List;

public interface QueryAsyncResponse {
    void processFinish(List<AppList> output);
}
