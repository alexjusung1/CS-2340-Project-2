package com.example.spotifywrapped.activities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifywrapped.data.TimeRange;

public class TimeRangeViewModel extends ViewModel {
    private final MutableLiveData<TimeRange> currentTimeRange = new MutableLiveData<>();

    public LiveData<TimeRange> getTimeRangeObserver() {
        return currentTimeRange;
    }

    public void setCurrentTimeRange(TimeRange timeRange) {
        currentTimeRange.setValue(timeRange);
    }
}
