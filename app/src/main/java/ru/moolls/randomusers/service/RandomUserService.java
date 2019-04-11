package ru.moolls.randomusers.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.moolls.randomusers.service.entity.User;

public class RandomUserService {

    private static final String TEST_ENDPOINT = "https://randomuser.me/api/?results=100&seed=LUCKY";

    private final Gson gson;
    private final OkHttpClient httpClient;
    private final Type userListTypeToken = new TypeToken<List<User>>() {}.getType();

    public RandomUserService() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(userListTypeToken, new UserSerializer());
        this.gson = builder.create();
        this.httpClient = new OkHttpClient();
    }

    public Observable<User> getAllUsers() {

        Request request = new Request.Builder()
                .url(TEST_ENDPOINT)
                .build();

        return Observable.create(emitter -> httpClient
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String jsonResponse = response.body().string();
                            List<User> users = gson.fromJson(jsonResponse, userListTypeToken);
                            for (User user : users) {
                                emitter.onNext(user);
                            }
                            emitter.onComplete();
                            response.close();

                        } else {
                            emitter.onComplete();
                        }
                    }
                }));
    }

    class UserSerializer implements JsonDeserializer<List<User>>{

        @Override
        public List<User> deserialize(JsonElement json, Type typeOfT,
                                      JsonDeserializationContext context) throws JsonParseException {

            List<User> resultUsers = new ArrayList<>();
            JsonArray resultJsonArray = json.getAsJsonObject().getAsJsonArray("results");
            for (JsonElement jsonElement : resultJsonArray) {
                resultUsers.add(convertJsonObjectToUser(jsonElement));
            }
            return resultUsers;
        }

        private User convertJsonObjectToUser(JsonElement jsonElement){
            User user = new User();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            user.setFirstName(jsonObject.get("name").getAsJsonObject().get("first").getAsString());
            user.setLastName(jsonObject.get("name").getAsJsonObject().get("last").getAsString());
            user.setPhone(jsonObject.get("phone").getAsString());
            user.setEmail(jsonObject.get("email").getAsString());
            user.setNational(jsonObject.get("nat").getAsString());
            user.setCity(jsonObject.get("location").getAsJsonObject().get("city").getAsString());
            user.setAddress(jsonObject.get("location").getAsJsonObject().get("street").getAsString());
            user.setPhotoUrl(jsonObject.get("picture").getAsJsonObject().get("large").getAsString());
            return user;
        }

    }
}
