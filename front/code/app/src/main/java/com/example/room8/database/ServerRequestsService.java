package com.example.room8.database;

import android.annotation.SuppressLint;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.room8.MainActivity;
import com.example.room8.model.Apartment;
import com.example.room8.model.Expense;
import com.example.room8.model.Roommate;
import com.example.room8.model.Task;
import com.example.room8.model.User;

import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
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

/**
 * For handle HTTP requests to the server using the okhttp3 lib
 */
public class ServerRequestsService {

    // Address
    public static final String SERVER_IP_ADDRESS = "10.113.5.82";
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
    public static final String TOKEN_EXPIRED_KEY = "expired"; // if the request succeeded
    public static final String MESSAGE_KEY = "msg"; // response message
    public static final String DATA_KEY = "data"; // the data
    public static final String TOKEN_HEADER_KEY = "x-auth-token";
    public static final String ACCESS_TOKEN_KEY = "jwtToken";
    public static final String REFRESH_TOKEN_KEY = "refreshJwtToken";


    // formatters for the date and time
    public static final FastDateFormat DATE_TIME_PARSER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"));
    public static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("dd-MM-yy HH:mm:ss", TimeZone.getDefault());
    public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("dd/MM/yyyy", TimeZone.getDefault());
    public static final FastDateFormat DATE_FORMAT_FOR_REQUEST = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getDefault());
    public static final FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("HH:mm", TimeZone.getDefault());


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
        refreshToken = SharedPreferenceHandler.getInstance().getRefreshJwt();
    }

    private final OkHttpClient client;
    private MainActivity activity;
    private String accessesToken;
    private String refreshToken;
    private Date refreshTimeOut;

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    private void showToast(String msg) {
        activity.runOnUiThread(() -> Toast.makeText(activity, msg != null ? msg : "null", Toast.LENGTH_SHORT).show());
    }


    private Callback createCallback(Request originalReq, String failMsg, Consumer<JSONObject> successAction, Consumer<JSONObject> failAction) {
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
                    System.err.println(failMsg);
                    System.err.println("responseBody is null");
                    return;
                }
                try {
                    JSONObject responseJOSN = new JSONObject(stringBody);
                    System.out.println(responseJOSN);
                    if (responseJOSN.has(SUCCESS_KEY)) {
                        if (responseJOSN.getBoolean(SUCCESS_KEY)) {
                            if (successAction != null) successAction.accept(responseJOSN);

                        } else if (responseJOSN.has(TOKEN_EXPIRED_KEY) && responseJOSN.getBoolean(TOKEN_EXPIRED_KEY)) {
                            refreshToken(originalReq, failMsg, successAction, failAction);
                        } else {
                            if (failAction != null) {
                                failAction.accept(responseJOSN);
                                return;
                            }
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

    private Callback createCallback(Request originalReq, String failMsg, Consumer<JSONObject> successAction) {
        return this.createCallback(originalReq, failMsg, successAction, null);
    }

    private Callback createCallback(String failMsg, Consumer<JSONObject> successAction, Consumer<JSONObject> failAction) {
        return this.createCallback(null, failMsg, successAction, failAction);
    }

    private Callback createCallback(String failMsg, Consumer<JSONObject> successAction) {
        return this.createCallback(failMsg, successAction, null);
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

    public synchronized void refreshToken(Request originalReq, String failMsg, Consumer<JSONObject> successAction, Consumer<JSONObject> failAction) {
        if (refreshToken == null)
            return;
        if (refreshTimeOut != null && Calendar.getInstance().toInstant().isBefore(refreshTimeOut.toInstant())) {
            if (originalReq != null) {
                if (Objects.equals(originalReq.header(TOKEN_HEADER_KEY), this.accessesToken)) {
                    activity.forceLogout();
                    return;
                }

                Request newRequest = new Request.Builder()
                        .url(originalReq.url())
                        .addHeader(TOKEN_HEADER_KEY, this.accessesToken)
                        .headers(originalReq.headers())
                        .method(originalReq.method(), originalReq.body())
                        .build();

                client.newCall(newRequest).enqueue(createCallback(failMsg, successAction, failAction));
            }
            return;
        }
        refreshTimeOut = new Date(Calendar.getInstance().getTimeInMillis() + ((10 * 60 * 5))); // expired in 5 minutes

        Request request = new Request.Builder()
                .url(HTTP_URL + AUTH_PATH + "/refresh")
                .addHeader(TOKEN_HEADER_KEY, refreshToken)
                .get()
                .build();

        client.newCall(request).enqueue(createCallback("",
                jsonObject -> {
                    if (jsonObject.has(ACCESS_TOKEN_KEY) && !jsonObject.isNull(ACCESS_TOKEN_KEY)) {
                        try {
                            String newToken = jsonObject.getString(ACCESS_TOKEN_KEY);
                            this.accessesToken = newToken;
                            SharedPreferenceHandler.getInstance().saveJwtAccessToken(newToken);

                            if (originalReq != null) {
                                Request newRequest = new Request.Builder()
                                        .url(originalReq.url())
                                        .addHeader(TOKEN_HEADER_KEY, accessesToken)
                                        .headers(originalReq.headers())
                                        .method(originalReq.method(), originalReq.body())
                                        .build();

                                client.newCall(newRequest).enqueue(createCallback(failMsg, successAction, failAction));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                jsonObject -> {
                    System.out.println(jsonObject);
                    activity.forceLogout();
                }));
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

    public void register(String username, String email, String password, Runnable navigateFunction, Consumer<String> displayError) {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(HTTP_URL + AUTH_PATH + "/register")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(createCallback(
                "register failed",
                jsonObject -> {
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
                },
                jsonObject -> {
                    try {
                        String msg = jsonObject.getString(MESSAGE_KEY);
                        displayError.accept(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ));
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

        client.newCall(request).enqueue(createCallback(request, "create apartment failed", jsonObject -> {
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

        client.newCall(request).enqueue(createCallback(request, "fetch data failed", jsonObject -> {
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

        client.newCall(request).enqueue(createCallback(request, "fetch tasks failed", jsonObject -> {
            try {
                JSONArray tasks = jsonObject.getJSONArray(DATA_KEY);
                for (int i = 0; i < tasks.length(); i++) {
                    JSONObject task = tasks.getJSONObject(i);
                    Apartment.getInstance().addTask(new Task(task));
                    if (notifyMethod != null)
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
        client.newCall(request).enqueue(createCallback(request, "fetch room8s data failed", jsonObject -> {
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

    public void addTask(Task task, Runnable notifyFunction) {
        FormBody.Builder formBody = new FormBody.Builder();
        if (task.getTaskType() != null)
            formBody.add("taskType", task.getTaskType());
        if (task.getExpirationDate() != null)
            formBody.add("expirationDate", DATE_TIME_PARSER.format(task.getExpirationDate()));
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

        client.newCall(request).enqueue(createCallback(request, "add task failed", jsonObject -> {
            try {
                if (jsonObject.getJSONObject(DATA_KEY).has("insertedID") && !jsonObject.getJSONObject(DATA_KEY).isNull("insertedID"))
                    task.setId(jsonObject.getJSONObject(DATA_KEY).getInt("insertedID"));
                task.setApartmentId(Apartment.getInstance().getId());
                task.setCreateDate(new Date());
                task.setCreatorId(User.getInstance().getId());
                task.setApartmentId(Apartment.getInstance().getId());
                Apartment.getInstance().getTasks().add(task);
                notifyFunction.run();
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
        client.newCall(request).enqueue(createCallback(request, failMsg, jsonObject ->
                showToast("The task has been updated successfully")
        ));
    }

    public void deleteTask(int taskId, Runnable notifyFunction) {
        Request request = new Request.Builder()
                .url(HTTP_URL + TASKS_PATH + "/" + taskId)
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        String failMsg = "delete task " + taskId + " failed";
        client.newCall(request).enqueue(createCallback(request, failMsg, jsonObject -> {
            showToast("The task has been delete successfully");
            notifyFunction.run();
        }));
    }

    public void changePassword(String prevPassword, String password, Runnable successAction, Consumer<String> showError) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("prevPassword", prevPassword);
        formBody.add("password", password);


        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/password")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .put(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback(request, "change password failed", jsonObject -> {
            showToast("change password successfully");
            successAction.run();
        }, jsonObject -> {
            if (jsonObject.has(MESSAGE_KEY)) {
                try {
                    showError.accept(jsonObject.getString(MESSAGE_KEY));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));
    }


    public void removeRoom8(int id) {
        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/removeUserFromApartment/" + id)
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        client.newCall(request).enqueue(createCallback(request, "remove user failed", jsonObject -> {
            Apartment.getInstance().getRoommates().removeIf(r -> id == r.getId());
            showToast("remove room8 successfully");
        }));
    }

    public void leave(Runnable navigate) {
        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/leave")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        client.newCall(request).enqueue(createCallback(request, "remove user failed", jsonObject -> {
            SharedPreferenceHandler sp = SharedPreferenceHandler.getInstance();
            sp.setIsInApartment(false);
            try {
                sp.saveJwtAccessToken(jsonObject.getString(ACCESS_TOKEN_KEY));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        client.newCall(request).enqueue(createCallback(request, "fetch expenses failed", jsonObject -> {
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
        if ((expense.getPaymentDate() != null))
            formBody.add("paymentDate", DATE_FORMAT_FOR_REQUEST.format(expense.getPaymentDate()));
        formBody.add("amount", String.valueOf(expense.getAmount()));
        formBody.add("note", expense.getNote());

        Request request = new Request.Builder()
                .url(HTTP_URL + EXPENSES_PATH + "/" + expense.getId())
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .put(formBody.build())
                .build();
        client.newCall(request).enqueue(createCallback(request, "update expense failed", jsonObject -> showToast("The expense has been updated successfully")));
    }

    public void createExpense(Expense expense) {
        expense.setUploadDate(new Date());

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("title", expense.getTitle());
        formBody.add("expensesType", expense.getType());
        if (expense.getPaymentDate() != null)
            formBody.add("paymentDate", DATE_FORMAT_FOR_REQUEST.format(expense.getPaymentDate()));
        formBody.add("amount", String.valueOf(expense.getAmount()));
        formBody.add("uploadDate", DATE_FORMAT_FOR_REQUEST.format(expense.getUploadDate()));
        formBody.add("note", expense.getNote());


        Request request = new Request.Builder()
                .url(HTTP_URL + EXPENSES_PATH + "/add")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback(request, "create expense went wrong", jsonObject -> {
            showToast("create expense successfully");
            try {
                if (jsonObject.has(DATA_KEY) && jsonObject.getJSONObject(DATA_KEY).has("insertedID")) {
                    expense.setId(jsonObject.getJSONObject(DATA_KEY).getInt("insertedID"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Apartment.getInstance().addExpense(expense);
        }));
    }

    public void deleteExpense(int expenseId, Runnable notifyFunction) {

        Request request = new Request.Builder()
                .url(HTTP_URL + EXPENSES_PATH + "/" + expenseId)
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        client.newCall(request).enqueue(createCallback(request, "delete expense failed", jsonObject -> {
            Apartment.getInstance().getExpenses().removeIf(e -> e.getId() == expenseId);
            notifyFunction.run();
            showToast("The expense has been deleted successfully");
        }));
    }

    public void forgotPassword(String email,Runnable navigateToLogin) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("email", email);

        Request request = new Request.Builder()
                .url(HTTP_URL + AUTH_PATH + "/forgotPassword")
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback("reset password went wrong", jsonObject -> {
            showToast("Check your email");
            navigateToLogin.run();
        }));
    }


    public void getJoinReq(Consumer<JSONArray> displayDialogFunction) {
        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/joinReq")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .get()
                .build();

        client.newCall(request).enqueue(createCallback(request, "fetch join request went wrong", jsonObject -> {
            if (!jsonObject.has(DATA_KEY)) return;

            try {
                JSONArray data = jsonObject.getJSONArray(DATA_KEY);
                if (data.length() == 0) return;
                displayDialogFunction.accept(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }));
    }

    public void sendJoinReq(String emailOrUsername, Consumer<String> displayErrorFunction) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("identify", emailOrUsername);

        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/sendJoinReq")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback(request, "send join request went wrong",
                jsonObject -> showToast("send request successfully"),
                jsonObject -> {
                    try {
                        if (displayErrorFunction != null && jsonObject.has(MESSAGE_KEY)) {
                            displayErrorFunction.accept(jsonObject.getString(MESSAGE_KEY));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));
    }

    public void handleJoinReq(int apartmentId, boolean join, Runnable navigateFunction) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("apartmentId", String.valueOf(apartmentId));
        formBody.add("join", String.valueOf(join));

        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/handleJoinReq")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .post(formBody.build())
                .build();

        SharedPreferenceHandler sp = SharedPreferenceHandler.getInstance();
        client.newCall(request).enqueue(createCallback(request, "fetch join request went wrong",
                jsonObject -> {
                    if (join) {
                        try {
                            if (jsonObject.has(ACCESS_TOKEN_KEY)) {
                                String token = jsonObject.getString(ACCESS_TOKEN_KEY);
                                sp.saveJwtAccessToken(token);
                                this.accessesToken = token;
                            } else {
                                showToast(jsonObject.getString(MESSAGE_KEY));
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        User.getInstance().setApartmentId(apartmentId);
                        sp.setIsInApartment(true);

                        navigateFunction.run();
                    }
                },
                jsonObject -> {
                    try {
                        if (jsonObject.has(ACCESS_TOKEN_KEY)) {
                            String token = jsonObject.getString(ACCESS_TOKEN_KEY);
                            sp.saveJwtAccessToken(token);
                            this.accessesToken = token;

                            User.getInstance().setApartmentId(apartmentId);
                            sp.setIsInApartment(true);

                            navigateFunction.run();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));
    }


    public void setRole(int userId, int roleNum, Runnable displayChangeFunction, Consumer<String> displayErrorFunction) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("roleNum", String.valueOf(roleNum));
        formBody.add("userId", String.valueOf(userId));

        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/changeRole")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .put(formBody.build())
                .build();
        client.newCall(request).enqueue(createCallback(
                request,
                "fetch join request went wrong",
                jsonObject -> {
                    displayChangeFunction.run();
                    showToast("change role successfully");
                },
                jsonObject -> {
                    try {
                        if (displayErrorFunction != null && jsonObject.has(MESSAGE_KEY)) {
                            displayErrorFunction.accept(jsonObject.getString(MESSAGE_KEY));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }));
    }


    public void deleteUser(Runnable navigateFunction) {
        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/delete")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .delete()
                .build();

        client.newCall(request).enqueue(createCallback(request, "delete data failed", jsonObject -> navigateFunction.run()));
    }

    public void getApartmentId(Runnable onFinish) {
        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/apartmentId")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .get()
                .build();

        client.newCall(request).enqueue(createCallback(request, "fetch data failed", jsonObject -> {
            try {
                SharedPreferenceHandler sp = SharedPreferenceHandler.getInstance();
                if (jsonObject.has("apartmentId")) {
                    if (jsonObject.isNull("apartmentId")) {
                        User.getInstance().setApartmentId(0);
                        sp.setIsInApartment(false);
                    } else {
                        User.getInstance().setApartmentId(jsonObject.getInt("apartmentId"));
                        sp.setIsInApartment(true);
                    }
                }

                if (jsonObject.has(ACCESS_TOKEN_KEY)) {
                    String token = jsonObject.getString(ACCESS_TOKEN_KEY);
                    sp.saveJwtAccessToken(token);
                    this.accessesToken = token;
                }

                if (onFinish != null) onFinish.run();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }));
    }

    public void getApartmentData() {
        Request request = new Request.Builder()
                .url(HTTP_URL + APARTMENTS_PATH + "/data")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .get()
                .build();

        client.newCall(request).enqueue(createCallback(request, "fetch data failed", jsonObject -> {
            Apartment a = Apartment.getInstance();

            try {
                if (!jsonObject.has(DATA_KEY) || jsonObject.isNull(DATA_KEY)) return;
                jsonObject = jsonObject.getJSONObject(DATA_KEY);

                if (jsonObject.has("ID") && !jsonObject.isNull("ID"))
                    a.setId(jsonObject.getInt("ID"));

                if (jsonObject.has("apartment_name")) {
                    if (jsonObject.isNull("apartment_name"))
                        a.setName("");
                    else
                        a.setName(jsonObject.getString("apartment_name"));
                }

                if (jsonObject.has("number_of_people") && !jsonObject.isNull("number_of_people"))
                    a.setNumberOfPeople(jsonObject.getInt("number_of_people"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }));
    }


    public void ChangeProfileImg(int iconId, Runnable onSuccess, Runnable onFailure) {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("iconId", String.valueOf(iconId));

        Request request = new Request.Builder()
                .url(HTTP_URL + USERS_PATH + "/changeProfileImg")
                .addHeader(TOKEN_HEADER_KEY, accessesToken)
                .put(formBody.build())
                .build();

        client.newCall(request).enqueue(createCallback(request, "change profile icon failed", jsonObject -> onSuccess.run(), jsonObject -> onFailure.run()));
    }
}