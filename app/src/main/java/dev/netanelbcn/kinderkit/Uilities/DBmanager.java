package dev.netanelbcn.kinderkit.Uilities;

import android.util.Log;

import androidx.annotation.NonNull;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.NewUserBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.UserBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.utils.CreatedBy;
import dev.netanelbcn.kinderkit.ExternalModels.utils.RetrofitClient;
import dev.netanelbcn.kinderkit.ExternalModels.utils.Role;
import dev.netanelbcn.kinderkit.ExternalModels.utils.UserId;
import dev.netanelbcn.kinderkit.Interfaces.UserService;
import dev.netanelbcn.kinderkit.Models.BasicUser;
import dev.netanelbcn.kinderkit.Models.Kid;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DBmanager {
    private static DBmanager instance;
    private final String superapp = "2024b.yarden.cherry";
    private final UserService userService;


    private DBmanager() {
        RetrofitClient database = RetrofitClient.getInstance();
        this.userService = database.getClient().create(UserService.class);
    }


    public static DBmanager getInstance() {
        if (instance == null) {
            instance = new DBmanager();
        }
        return instance;
    }


    public void addKidToDB(Kid kid) {


    }

    public void updateUserRole(String email, Role role, DataManager.OnUserUpdateListener listener) {
        userService.getUserById(superapp, email).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(@NonNull Call<UserBoundary> call, @NonNull Response<UserBoundary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserBoundary userBoundary = response.body();
                    userBoundary.setRole(role);
                    userService.updateUser(userBoundary.getUserId().getSuperapp(), email, userBoundary).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                listener.onSuccess();
                            } else {
                                listener.onFailure(new Exception("Failed to update user role: " + getErrorMessage(response)));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            listener.onFailure(new Exception("Failed to update user role: " + t.getMessage()));
                        }
                    });
                } else {
                    listener.onFailure(new Exception("Failed to fetch user for role update: " + getErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserBoundary> call, @NonNull Throwable t) {
                listener.onFailure(new Exception("Network error during role update: " + t.getMessage()));
            }
        });
    }


    private NewUserBoundary createNewUserBoundary(String email, Role role, String
            userName, String avatar) {
        NewUserBoundary user = new NewUserBoundary();
        user.setEmail(email);
        user.setRole(role);
        user.setUsername(userName);
        user.setAvatar(avatar);
        return user;
    }

    public void createUser(String email, BasicUser user, DataManager.OnUserCreationListener listenerCreate, DataManager.OnDataSavedListener listenerSave, DataManager.OnUserUpdateListener listenerUpdate) {
        NewUserBoundary newUser = createNewUserBoundary(email, Role.SUPERAPP_USER, email, user.getPassword());
        userService.createUser(newUser).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(@NonNull Call<UserBoundary> call, @NonNull Response<UserBoundary> response) {
                if (response.isSuccessful()) {
                    UserBoundary userBoundary = response.body();
                    if (userBoundary != null) {
                        CreatedBy createdBy = new CreatedBy(new UserId(superapp, email));
                        ObjectBoundary userData = user.toBoundary();
                        userData.setCreatedBy(createdBy);
                        listenerCreate.onUserCreated(email);
                        saveData(user, userData, userBoundary, listenerSave, listenerUpdate);
                    } else {
                        listenerCreate.onFailure(new Exception("Failed to create user: response body is null"));
                    }
                } else {
                    Log.e("error", "errorOccured");
                    //logError(response, "createUser");
                    // listenerCreate.onFailure(new Exception("Failed to create user: " + getErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserBoundary> call, @NonNull Throwable t) {
                listenerCreate.onFailure(new Exception("Failed to create user: " + t.getMessage()));
                Log.e("DataManager", "Error in createUser: " + t.getMessage());
            }
        });
    }


    private void saveData(BasicUser user, ObjectBoundary userData, UserBoundary userBoundary, DataManager.OnDataSavedListener listenerSave, DataManager.OnUserUpdateListener listenerUpdate) {
        userService.saveUserData(userData).enqueue(new Callback<ObjectBoundary>() {
            @Override
            public void onResponse(@NonNull Call<ObjectBoundary> call, @NonNull Response<ObjectBoundary> response) {
                if (response.isSuccessful()) {
                    listenerSave.onSuccess(response.body());
                    userBoundary.setUsername(response.body().getObjectId().getId());
                    userBoundary.setRole(Role.MINIAPP_USER);
                    userData.getObjectId().setId(response.body().getObjectId().getId());
                    userData.getObjectId().setSuperapp((response.body().getObjectId().getSuperapp()));
                    userData.getObjectDetails().put("uid", response.body().getObjectId().getId());
                    //setMyUserData(userData);
                    Log.d("userdata", userData.toString());
                    updateObject(userData, listenerUpdate, user);
                    updateUser(userBoundary, listenerUpdate);

                } else {
                    logError(response, "saveUserData");
                    listenerSave.onFailure(new Exception("Failed to save user data"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ObjectBoundary> call, @NonNull Throwable t) {
                listenerSave.onFailure(new Exception("Failed to save user data: " + t.getMessage()));
            }
        });
    }


    public void updateUser(UserBoundary userBoundary, DataManager.OnUserUpdateListener listenerUpdate) {
        userService.updateParentUser(userBoundary.getUserId().getSuperapp(), userBoundary.getUserId().getEmail(), userBoundary).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    listenerUpdate.onSuccess();
                } else {
                    logError(response, "updateUser");
                    listenerUpdate.onFailure(new Exception("Failed to update user: " + getErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                listenerUpdate.onFailure(new Exception("Failed to update user: " + t.getMessage()));
            }
        });
    }

    public void updateObject(ObjectBoundary objectBoundary, DataManager.OnUserUpdateListener listenerUpdate, BasicUser user) {
        if (user.getClass().getSimpleName().equals("Parent"))
            Log.d("Da", "Updating object with id: " + objectBoundary.toString());
        userService.updateObject(objectBoundary.getObjectId().getId(), objectBoundary.getObjectId().getSuperapp(), objectBoundary.getObjectId().getSuperapp(), objectBoundary.getCreatedBy().getUserId().getEmail(), objectBoundary)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            listenerUpdate.onSuccess();
                        } else {
                            logError(response, "updateUser");
                            listenerUpdate.onFailure(new Exception("Failed to update user: " + getErrorMessage(response)));
                            Log.d("DataManager", "Error in updateObject: " + getErrorMessage(response));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        listenerUpdate.onFailure(new Exception("Failed to update user: " + t.getMessage()));
                        Log.d("12w2", "Error in updateObject: " + t.getMessage());
                    }
                });
    }

    public void refreshUserInDB(BasicUser basic) {
        DataManager manager = DataManager.getInstance();
        ObjectBoundary basicB = basic.toBoundary();
        basicB.getObjectId().setId(basic.getUid());
        basicB.getObjectId().setSuperapp(manager.getSuperapp());
        basicB.getCreatedBy().getUserId().setSuperapp(manager.getSuperapp());
        Log.d("kid1", basic.toString());

        manager.db.updateUserRole(basic.getMail(), Role.SUPERAPP_USER, new DataManager.OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                Log.d("kidB", basicB.toString());

                manager.db.updateObject(basicB, new DataManager.OnUserUpdateListener() {
                    @Override
                    public void onSuccess() {
                        //Toast.makeText(AddEventActivity.this, "Event Added successful", Toast.LENGTH_SHORT).show();
                        Log.d("kid2", basic.toString());
                        manager.db.updateUserRole(basic.getMail(), Role.MINIAPP_USER, new DataManager.OnUserUpdateListener() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                //    Toast.makeText(AddEventActivity.this, "failed Role1 " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("problt", exception.getMessage());
                            }
                        });

                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e("errr", exception.getMessage());
                        // Toast.makeText(AddEventActivity.this, "failed update KID " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, basic);

            }

            @Override
            public void onFailure(Exception exception) {
                //  Toast.makeText(AddEventActivity.this, "failed Role2 " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("problt", exception.getMessage());

            }
        });

    }

    public String getErrorMessage(Response<?> response) {
        try {
            return response.errorBody() != null ? response.errorBody().string() : "Unknown error";
        } catch (Exception e) {
            return "Could not read error body";
        }
    }

    public void logError(Response<?> response, String methodName) {
        try {
            Log.e("DataManager", "Error in " + methodName + ": " + response.errorBody().string() + " | HTTP Status Code: " + response.code());
        } catch (Exception e) {
            Log.e("DataManager", "Error in " + methodName + ": Could not read error body", e);
        }
    }


}
