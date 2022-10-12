package com.example.bledemoapp.api.model.retrofit;

import java.util.List;

import okhttp3.internal.concurrent.Task;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TaskService {
    @GET("/tasks")
    Call<List<Task>> getTasks();
}
