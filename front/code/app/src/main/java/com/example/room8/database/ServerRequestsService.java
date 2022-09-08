package com.example.room8.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.room8.model.Apartment;
import com.example.room8.model.Expense;
import com.example.room8.model.Roommate;
import com.example.room8.model.Task;
import com.example.room8.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Consumer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ServerRequestsService {

    // Address
    public static final String SERVER_IP_ADDRESS = "192.168.1.158";
    public static final int PORT = 3000;
    public static final String SERVER_BASE_URL = SERVER_IP_ADDRESS + ":" + PORT;
    public static final String HTTP_URL = "http://" + SERVER_BASE_URL;

    //Paths
    public static final String AUTH_PATH = "/auth";
    public static final String USERS_PATH = "/users";
    public static final String APARTMENTS_PATH = "/apartments";
    public static final String TASKS_PATH = "/tasks";
    public static final String EXPENSES_PATH = "/expenses";

    //JSON keys
    public static final String SUCCESS_KEY = "success"; // if the request succeeded
    public static final String MESSAGE_KEY = "msg"; // response message
    public static final String DATA_KEY = "data"; // the data
    public static final String TOKEN_HEADER_KEY = "x-auth-token";
    public static final String ACCESS_TOKEN_KEY = "jwtToken";
    public static final String REFRESH_TOKEN_KEY = "refreshJwtToken";

    // formatters for the date and time
    @SuppressLint("SimpleDateFormat") // for parse date time from the server
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @SuppressLint("SimpleDateFormat") // for format date object to time string
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat DATE_FORMAT_FOR_REQUEST = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat") // for format date object to date string
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    static { // for initial the timezone
        DATE_TIME_FORMAT.setTimeZone(TimeZone.getDefault());
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        DATE_FORMAT_FOR_REQUEST.setTimeZone(TimeZone.getDefault());
        TIME_FORMAT.setTimeZone(TimeZone.getDefault());
    }


    @SuppressLint("StaticFieldLeak")
    public static volatile ServerRequestsService instance;
    private static final Object lock = new Object();

    public static ServerRequestsService getInstance() {
        ServerRequestsService r = instance;
        if (r == null) {
            synchronized (lock) {    // While we were waiting for the lock, another
                r = instance;        // thread may have instantiated the object.
                if (r == null) {
                    r = new ServerRequestsService();
                    instance = r;
                }
            }
        }
        return r;
    }

    private ServerRequestsService() {
        client = new OkHttpClient();
        accessesToken = SharedPreferenceHandler.getInstance().getAccessJwt();
    }

    private final OkHttpClient client;
    private Activity activity;
    private String accessesToken;
    private String refreshToken;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void showToast(String msg) {
        if (msg == null) {
            msg = "null";
        }
        String finalMsg = msg;
        activity.runOnUiThread(() -> Toast.makeText(activity, finalMsg, Toast.LENGTH_SHORT).show());
    }


    private Callback createCallback(String failMsg, Consumer<JSONObject> successAction, Consumer<JSONObject> failAction){
        return new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast(failMsg);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody responseBody = response.body();

                String stringBody = responseBody != null ? responseBody.string() : null;

                if (stringBody == null) {
                    showToast(failMsg);
                    showToast("responseBody is null");
                    System.err.println(failMsg);
                    System.err.println("responseBody is null");
                    return;
                }
                try {
                    JSONObject responseJOSN = new JSONObject(stringBody);


                    if (responseJOSN.has(SUCCESS_KEY)) {
                        if(responseJOSN.getBoolean(SUCCESS_KEY)){
                            if (successAction != null) successAction.accept(responseJOSN);
                        }else {
                            if (failAction != null) failAction.accept(responseJOSN);
                            handleUnsuccessfulReq(failMsg, response.code(), responseJOSN);
                        }
                        return;
                    }

                    if (!response.isSuccessful()) {
                        handleUnsuccessfulReq(failMsg, response.code(), responseJOSN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Callback createCallback(String failMsg, Consumer<JSONObject> successAction) {
        this.createCallback(failMsg,successAction,null);
    }

    private Callback createCallback(String failMsg) {
        this.createCallback(failMsg,null,null);
    }

    private void handleUnsuccessfulReq(String failMsg, int responseCode, JSONObject responseJOSN) {
        showToast(failMsg);
        showToast("response code: " + responseCode);
        try {
            showToast("response body: " + responseJOSN.getString(MESSAGE_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.err.println(failMsg);
        System.err.println("response code: " + responseCode);
        System.err.println("msg: " + responseJOSN);
    }


    public void login(String email, String password, Consumer<Boolean> navigateFunction) {
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(HTTP_URL + AUTH_PATH + "/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(createCallback("login failed", jsonObject -> {
            try {
                SharedPreferenceHandler sp = SharedPreferenceHandler.getInstance();

                String token = jsonObject.getString(ACCESS_TOKEN_KEY);
                String refToken = jsonObject.getString(REFRESH_TOKEN_KEY);
                sp.saveJwtAccessToken(token);
                sp.saveJwtRefreshToken(refToken);
                this.accessesToken = token;
                this.refreshToken = refToken;

                User.getInstance().setId(jsonObject.getInt("userId"));
                if (jsonObject.has("apartmentId") && !jsonObject.isNull("apartmentId")) {
                    User.getInstance().setApartmentId(jsonObject.getInt("apartmentId"));
                    sp.setIsInApartment(true);
                    navigateFunction.accept(true);
                } else {
                    sp.setIsInApartment(false);
                    navigateFunction.accept(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                handleUnsuccessfulReq("failed when try to parse", 0, jsonObject);
            }
        }));
    }

    public void register(String username, String email, String password, Runnable navigateFunction) {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(HTTP_URL + AUTH_PATH + "/register")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(createCallback("register failed", jsonObject -> {
            try {
                SharedPreferenceHandler sp = SharedPreferenceHandler.getInstance();

                String token = jsonObject.getString(ACCESS_TOKEN_KEY);
                sp.saveJwtAccessToken(token);
                this.accessesToken = token;

                sp.setIsInApartment(false);

                navigateFunction.run();
            } catch (JSONException e) {
                e.printStackTrace();
                handleUnsuccessfulReq("register failed when parsing the data", 0, jsonObject);
            }
        }));
    }

    public boolean isServerUp() {
        Request request = new Request.Builder()
                .url(HTTP_URL + "/isAlive")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void createApartment(String name, Runnable navigateFunction) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("name", name);


        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/create")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback("create apartment failed", jsonObject -> {
            try {
                if (jsonObject.has(DATA_KEY)) {
                    JSONObject data = jsonObject.getJSONObject(DATA_KEY);
                    if (data.has("apartmentId")) {
                        Apartment.getInstance().setId(data.getInt("apartmentId"));
                        SharedPreferenceHandler.getInstance().setIsInApartment(true);
                        String accessToken = data.getString(ACCESS_TOKEN_KEY);
                        SharedPreferenceHandler.getInstance().saveJwtAccessToken(accessToken);
                        this.accessesToken = accessToken;
                        navigateFunction.run();
                    }
                } else {
                    showToast((jsonObject.getString(MESSAGE_KEY)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                handleUnsuccessfulReq("create apartment failed failed when try to parse the data", 0, jsonObject);

            }
        }));
    }

    public void getUserData() {
        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/findById")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .get()
                .build();

        client.newCall(request).enqueue(createCallback("fetch data failed", jsonObject -> {
            try {
                User.parseDataFromJson(jsonObject.getJSONObject(DATA_KEY));
            } catch (JSONException e) {
                e.printStackTrace();
                handleUnsuccessfulReq("fetch data failed when try to parse the data", 0, jsonObject);
            }
        }));
    }

    public void getAllTask(Runnable notifyMethod) {
        Request request = new Request.Builder()
                .url(HTTP_URL + TASKS_PATH + "/all")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .get()
                .build();

        client.newCall(request).enqueue(createCallback("fetch tasks failed", jsonObject -> {
            try {
                JSONArray tasks = jsonObject.getJSONArray(DATA_KEY);
                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject task = tasks.getJSONObject(i);
                    Apartment.getInstance().addTask(new Task(task));
                    activity.runOnUiThread(notifyMethod);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                handleUnsuccessfulReq("fetch tasks failed when try to parse the data", 0, jsonObject);

            }
        }));
    }

    public void getRoom8s() {
        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/room8")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .get()
                .build();
        client.newCall(request).enqueue(createCallback("fetch room8s data failed", jsonObject -> {
            try {
                JSONArray room8 = jsonObject.getJSONArray(DATA_KEY);
                for (int i = 0; i < room8.length(); i++) {
                    JSONObject roommate = room8.getJSONObject(i);
                    Apartment.getInstance().addRoommate(new Roommate(roommate));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                handleUnsuccessfulReq("fetch room8s failed when try to parse the data", 0, jsonObject);

            }
        }));
    }

    public void addTask(Task task) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (task.getTaskType() != null)
            formBody.add("taskType", task.getTaskType());
        if (task.getExpirationDate() != null)
            formBody.add("expirationDate", DATE_TIME_FORMAT.format(task.getExpirationDate()));
        if (task.getTitle() != null)
            formBody.add("title", task.getTitle());
        if (task.getNote() != null)
            formBody.add("note", task.getNote());
        if (task.getExecutorsIds() != null) {
            formBody.add("executorsIds", task.getExecutorsIds().toString());
        }


        Request request = new Request.Builder()
                .url(HTTP_URL + TASKS_PATH + "/add")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback("add task failed", jsonObject -> {
            try {
                if (jsonObject.getJSONObject(DATA_KEY).has("insertedID") && !jsonObject.getJSONObject(DATA_KEY).isNull("insertedID"))
                    task.setApartmentId(Apartment.getInstance().getId());
                task.setCreateDate(new Date());
                task.setCreatorId(User.getInstance().getId());
                task.setId(jsonObject.getJSONObject(DATA_KEY).getInt("insertedID"));
                Apartment.getInstance().getTasks().add(task);
                showToast("The task has been add successfully");
            } catch (JSONException e) {
                e.printStackTrace();
                handleUnsuccessfulReq("add task failed when try to parse the data", 0, jsonObject);
            }
        }));
    }

    public void updateTask(Task task) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (task.getTaskType() != null)
            formBody.add("taskType", task.getTaskType());
        if (task.getExpirationDate() != null)
            formBody.add("expirationDate", DATE_TIME_FORMAT.format(task.getExpirationDate()));
        if (task.getTitle() != null)
            formBody.add("title", task.getTitle());
        if (task.getNote() != null)
            formBody.add("note", task.getNote());
        if (task.getExecutorsIds() != null)
            formBody.add("executorsIds", task.getExecutorsIds().toString());

        Request request = new Request.Builder()
                .url(HTTP_URL + TASKS_PATH + "/" + task.getId())
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .put(formBody.build())
                .build();

        String failMsg = "update task " + task.getId() + " failed";
        client.newCall(request).enqueue(createCallback(failMsg, jsonObject ->
                showToast("The task has been updated successfully")
        ));
    }

    public void deleteTask(int taskId) {
        Request request = new Request.Builder()
                .url(HTTP_URL + TASKS_PATH + "/" + taskId)
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        String failMsg = "delete task " + taskId + " failed";
        client.newCall(request).enqueue(createCallback(failMsg, jsonObject -> {
            Apartment.getInstance().getTasks().removeIf(task -> task.getId() == taskId);
            showToast("The task has been delete successfully");
        }));
    }

    public void changePassword(String password) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("password", password);


        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/password")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .put(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback("change password failed", jsonObject -> showToast("change password successfully")));
    }


    public void removeRoom8(int id) {
        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/removeUserFromApartment/" + id)
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        client.newCall(request).enqueue(createCallback("remove user failed", jsonObject -> {
            Apartment.getInstance().getRoommates().removeIf(r -> id == r.getId());
            showToast("remove room8 successfully");
        }));
    }

    public void leave(Runnable navigate) {
        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/removeUserFromApartment/" + User.getInstance().getId())
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        client.newCall(request).enqueue(createCallback("remove user failed", jsonObject -> {
            navigate.run();
            showToast("remove room8 successfully");
        }));
    }


    public void getExpenses() {
        Request request = new Request.Builder()
                .url(HTTP_URL + EXPENSES_PATH)
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .get()
                .build();
        client.newCall(request).enqueue(createCallback("fetch expenses failed", jsonObject -> {
            try {
                JSONArray expenses = jsonObject.getJSONArray(DATA_KEY);
                for (int i = 0; i < expenses.length(); i++) {
                    JSONObject expense = expenses.getJSONObject(i);
                    Apartment.getInstance().addExpense(new Expense(expense));
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                handleUnsuccessfulReq("fetch expense failed when try to parse the data", 0, jsonObject);

            }
        }));

    }

    public void updateExpense(Expense expense) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("title", expense.getTitle());
        formBody.add("expensesType", expense.getType());
        formBody.add("paymentDate", DATE_FORMAT_FOR_REQUEST.format(expense.getPaymentDate()));
        formBody.add("amount", String.valueOf(expense.getAmount()));
        formBody.add("note", expense.getNote());

        Request request = new Request.Builder()
                .url(HTTP_URL + EXPENSES_PATH + "/" + expense.getId())
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .put(formBody.build())
                .build();
        client.newCall(request).enqueue(createCallback("update expense failed", jsonObject -> showToast("The expense has been updated successfully")));
    }

    public void createExpense(Expense expense) {
        expense.setUploadDate(new Date());

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("title", expense.getTitle());
        formBody.add("expensesType", expense.getType());
        formBody.add("paymentDate", DATE_FORMAT_FOR_REQUEST.format(expense.getPaymentDate()));
        formBody.add("amount", String.valueOf(expense.getAmount()));
        formBody.add("uploadDate", DATE_FORMAT_FOR_REQUEST.format(expense.getUploadDate()));
        formBody.add("note", expense.getNote());


        Request request = new Request.Builder()
                .url(HTTP_URL + EXPENSES_PATH + "/add")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback("create expense went wrong", jsonObject -> {
            showToast("create expense successfully");
            if (jsonObject.has(DATA_KEY)) {
                try {
                    expense.setId(jsonObject.getInt(DATA_KEY));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Apartment.getInstance().addExpense(expense);
        }));
    }

    public void deleteExpense(int expenseId) {
        
        Request request = new Request.Builder()
                .url(HTTP_URL + EXPENSES_PATH + "/" + expenseId)
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        client.newCall(request).enqueue(createCallback("delete expense failed", jsonObject -> {
            Apartment.getInstance().getExpenses().removeIf(e->e.id==expenseId);
            showToast("The expense has been deleted successfully");
            }));
    }

    public void forgotPassword(String email) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("email", email);

        Request request = new Request.Builder()
                .url(HTTP_URL + AUTH_PATH + "/forgotPassword")
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback("reset password went wrong", jsonObject -> showToast("Check your email")));

    }


    public void getJoinReq(Consumer<JsonArray> displayDialogFunction){
         Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/joinReq")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .get()
                .build();

        client.newCall(request).enqueue(createCallback("fetch join request went wrong", jsonObject ->{ 
                if(!jsonObject.has(DATA_KEY))RETURN

                JsonArray data = jsonObject.getJSONArray(DATA_KEY);
                if(data.length() == 0) return;
                
                displayDialogFunction.accept(data);
            }));
    }


}
