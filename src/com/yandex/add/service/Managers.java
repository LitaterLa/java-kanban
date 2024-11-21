package com.yandex.add.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yandex.add.server.DurationAdapter;
import com.yandex.add.server.LocalDateTimeAdapter;
import com.yandex.add.service.history.HistoryManager;
import com.yandex.add.service.history.InMemoryHistoryManager;
import com.yandex.add.service.taskmanager.InMemoryTaskManager;
import com.yandex.add.service.taskmanager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }
}
