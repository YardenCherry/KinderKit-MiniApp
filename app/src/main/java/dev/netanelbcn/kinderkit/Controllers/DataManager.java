package dev.netanelbcn.kinderkit.Controllers;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.MiniAppCommandBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.UserBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.utils.CommandId;
import dev.netanelbcn.kinderkit.ExternalModels.utils.CreatedBy;
import dev.netanelbcn.kinderkit.ExternalModels.utils.ObjectId;
import dev.netanelbcn.kinderkit.ExternalModels.utils.RetrofitClient;
import dev.netanelbcn.kinderkit.ExternalModels.utils.Role;
import dev.netanelbcn.kinderkit.ExternalModels.utils.TargetObject;
import dev.netanelbcn.kinderkit.ExternalModels.utils.UserId;
import dev.netanelbcn.kinderkit.Interfaces.BabysitterService;
import dev.netanelbcn.kinderkit.Interfaces.EventService;
import dev.netanelbcn.kinderkit.Interfaces.UserService;
import dev.netanelbcn.kinderkit.Models.Babysitter;
import dev.netanelbcn.kinderkit.Models.BabysittingEvent;
import dev.netanelbcn.kinderkit.Models.ImmunizationRecord;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.Models.KidEvent;
import dev.netanelbcn.kinderkit.Models.MyPhoto;
import dev.netanelbcn.kinderkit.Models.Parent;
import dev.netanelbcn.kinderkit.Uilities.DBmanager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataManager {


    public static DBmanager db;
    private static DataManager instance;
    private final String superapp = "2024b.yarden.cherry";
    private final EventService eventService;
    private final BabysitterService babysitterService;
    private final UserService userService;
    private Parent parent;


    private DataManager() {
        db = DBmanager.getInstance();
        // firebaseRTDatabase = FirebaseDatabase.getInstance();
        RetrofitClient database = RetrofitClient.getInstance();
        this.eventService = database.getClient().create(EventService.class);
        this.babysitterService = database.getClient().create(BabysitterService.class);

        parent = new Parent(); // need to set details after login
        this.userService = database.getClient().create(UserService.class);
    }

    //private FirebaseDatabase firebaseRTDatabase;

    //private FBmanager fbmanager;

    // public FBmanager getFbmanager() {
    // return fbmanager;
    // }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public String getSuperapp() {
        return superapp;
    }

    public ObjectBoundary toBoundary() {
        ObjectBoundary boundary = new ObjectBoundary();
        Map<String, Object> map = new HashMap<>();
        map.put("kids", parent.getKids());
        boundary.setType(parent.getClass().getSimpleName());
        boundary.setActive(true);
        boundary.setObjectDetails(map);
        return boundary;
    }

    public void toKids(ObjectBoundary o) {
        this.parent.setKids((ArrayList<Kid>) o.getObjectDetails().get("kids"));
    }

    @Override
    public String toString() {
        return "DataManager{" +
                "kids=" + parent.getKids() +
                '}';
    }

    public String getUid() {
        return parent.getUid();
    }

//    public FirebaseDatabase getFirebaseRTDatabase() {
//        return firebaseRTDatabase;
//    }
//
//    public DataManager setFirebaseRTDatabase(FirebaseDatabase firebaseRTDatabase) {
//        this.firebaseRTDatabase = firebaseRTDatabase;
//        return this;
//    }


    public Parent getParent() {
        return parent;
    }

    public ArrayList<Kid> getKids() {
        return parent.getKids();
    }

    public DataManager setKids(ArrayList<Kid> kids) {
        parent.setKids(kids);
        return this;
    }

    public void addImmunizationRecord(ImmunizationRecord record, int kidPos) {
        this.parent.getKids().get(kidPos).getImmunizationRecords().add(record);
        //fbmanager.addImmunizationRecordToDB(record, parent.getKids().get(kidPos));
    }

    public void setProfilePhotoUri(Uri uri, int kidPos) {
        parent.getKids().get(kidPos).setProfilePhotoUri(uri.toString());
        Kid kid = parent.getKids().get(kidPos);
        db.refreshUserInDB(kid);
        db.refreshUserInDB(parent);

        // fbmanager.setProfilePhotoUriToDB(uri, parent.getKids().get(kidPos));
    }

    public void addPhotoUri(Uri uri, Kid kid) {
        MyPhoto myPhoto = new MyPhoto().setPhotoUri(uri.toString());
        kid.getPhotosUri().add(myPhoto);
        Log.d("photos", kid.getPhotosUri().toString());
        db.refreshUserInDB(kid);
        db.refreshUserInDB(parent);


        // fbmanager.addPhotoUriToDB(uri,parent.getKids().get(kidPos));
    }

    public void addKidEvent(KidEvent event, int kidPos) {
        parent.getKids().get(kidPos).getEvents().add(event);
        Kid kid = getParent().getKids().get(kidPos);
        db.refreshUserInDB(kid);
        // db.refreshUserInDB(parent);
        //fbmanager.addKidEventToDB(event, parent.getKids().get(kidPos));
    }

    public void removeKid(Kid kid) {
        //fbmanager.removeKidFromDB(kid.getkId());
        kid.setPhone("-1");
        db.refreshUserInDB(kid);
        parent.removeKid(kid);
        db.refreshUserInDB(parent);
    }

    public void removeImmunizationRecord(ImmunizationRecord iR, int pos) {
        parent.getKids().get(pos).getImmunizationRecords().remove(iR);
        Kid kid = parent.getKids().get(pos);
        db.refreshUserInDB(kid);
        db.refreshUserInDB(parent);
        //   fbmanager.removeImmunizationRecordFB(iR,   parent.getKids().get(pos));
    }

    public void removeKidEvent(KidEvent kEvent, int pos) {
        parent.getKids().get(pos).getEvents().remove(kEvent);
        Kid kid = parent.getKids().get(pos);
        if(kEvent.getEventTitle().contains("Babysitter Event")){
            BabysittingEvent babysittingEvent = new BabysittingEvent();
            babysittingEvent.setMessageId(kEvent.geteId());
            babysittingEvent.setMessageText(kEvent.getEventTitle());
            babysittingEvent.setBabysitterUid("-1");
            updateEvent(kEvent.geteId(), babysittingEvent, new OnUserUpdateListener() {
                @Override
                public void onSuccess() {
                    Log.d("eran", "KidEvent updated successfully");
                }
                @Override
                public void onFailure(Exception exception) {
                    Log.e("eran", "Failed to update KidEvent: " + exception.getMessage());
                }
            });
        }
        db.refreshUserInDB(kid);
        db.refreshUserInDB(parent);

        // fbmanager.removeKidEventFB(kEvent,  parent.getKids().get(pos));
    }

    public void loadAllKids(OnKidsLoadedListener listener) {
        ArrayList<Kid> kids = getParent().getKids();
        ArrayList<Kid> loadedKids = new ArrayList<>();
        if (kids.size() == 0) {
            listener.onKidsLoaded(kids);
            Log.d("KidLoaded 196: ", kids.toString());
            return;
        }

        final int totalKids = kids.size();
        final int[] completedRequests = {0}; // Use an array to hold the counter for completed requests

        for (Kid kid : kids) {
            userService.getObjectById(kid.getUid(), getSuperapp(), getSuperapp(), getParent().getMail()).enqueue(new Callback<ObjectBoundary>() {
                @Override
                public void onResponse(Call<ObjectBoundary> call, Response<ObjectBoundary> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Kid loadedKid = kid.fromBoundary(response.body());
                        Log.d("KidLoaded 204: ", loadedKid.toString());
                        loadedKids.add(loadedKid);
                        // Increment the counter and check if all requests are completed
                        completedRequests[0]++;
                        if (completedRequests[0] == totalKids) {
                            getParent().setKids(loadedKids);
                            getParent().getKids().sort(Comparator.comparingInt(Kid::getAge).reversed());
                            Log.d("KidLoaded 216: ", getParent().getKids().toString());
                            listener.onKidsLoaded(getParent().getKids());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ObjectBoundary> call, Throwable t) {
                    listener.onFailure(new Exception("Failed to load kids: " + t.getMessage()));
                }
            });
        }
    }


    public void sortBabysittersByDistance(OnBabysittersLoadedListener listener) {
        userService.getUserById(superapp, getParent().getMail()).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(Call<UserBoundary> call, Response<UserBoundary> userResponse) {
                if (userResponse.isSuccessful() && userResponse.body() != null) {
                    UserBoundary user = userResponse.body();
                    fetchUserLocation(user, listener);
                } else {
                    listener.onFailure(new Exception("Failed to fetch user"));
                }
            }

            @Override
            public void onFailure(Call<UserBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Failed to fetch user: " + t.getMessage()));
            }
        });
    }

    public MiniAppCommandBoundary createCommand(String command, UserBoundary user, String...
            args) {
        MiniAppCommandBoundary miniappCommand = new MiniAppCommandBoundary();
        CommandId commandId = new CommandId(user.getUserId().getSuperapp(), args[1], "1");
        miniappCommand.setCommandId(commandId);
        miniappCommand.setCommand(command);
        miniappCommand.setInvokedBy(new CreatedBy(new UserId(user.getUserId().getSuperapp(), user.getUserId().getEmail())));
        miniappCommand.setTargetObject(new TargetObject(new ObjectId(user.getUserId().getSuperapp(), user.getUsername())));
        miniappCommand.setCommandAttributes(new HashMap<>());

        if (args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                miniappCommand.getCommandAttributes().put(args[i], args[i + 1]);
                Log.d("DataManager", "Command attribute: " + args[i] + " = " + args[i + 1]);
            }
        } else {
            throw new IllegalArgumentException("Args should be key-value pairs");
        }
        Log.d("DataManager", "Command: " + miniappCommand);
        return miniappCommand;
    }

    public void loadAllBabysitters(OnBabysittersLoadedListener listener) {
        babysitterService.loadAllBabysitters(Babysitter.class.getSimpleName(), superapp, parent.getMail()).enqueue(new Callback<List<ObjectBoundary>>() {
            @Override
            public void onResponse(Call<List<ObjectBoundary>> call, Response<List<ObjectBoundary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Babysitter> babysitters = convertObjectBoundaryToBabysitters(response.body());
                    listener.onBabysittersLoaded(babysitters);
                } else {
                    listener.onFailure(new Exception("Failed to load babysitters"));
                }
            }

            @Override
            public void onFailure(Call<List<ObjectBoundary>> call, Throwable t) {
                listener.onFailure(new Exception("Failed to load babysitters: " + t.getMessage()));
            }
        });
    }

    private List<Babysitter> convertObjectBoundaryToBabysitters
            (List<ObjectBoundary> objects) {
        List<Babysitter> babysitters = new ArrayList<>();

        for (ObjectBoundary object : objects) {
            Babysitter babysitter = new Gson().fromJson(new Gson().toJson(object.getObjectDetails()), Babysitter.class);
            babysitters.add(babysitter);
        }

        return babysitters;
    }

    private List<BabysittingEvent> convertObjectBoundaryToBabysittingEvents
            (List<ObjectBoundary> objects) {
        List<BabysittingEvent> events = new ArrayList<>();

        for (ObjectBoundary object : objects) {
            BabysittingEvent event = new Gson().fromJson(new Gson().toJson(object.getObjectDetails()), BabysittingEvent.class);
            events.add(event);
        }

        return events;
    }

    private void fetchBabysittersByDistance(UserBoundary user, double latitude,
                                            double longitude, OnBabysittersLoadedListener listener) {
        MiniAppCommandBoundary command = createCommand(
                "GetAllObjectsByTypeAndLocationAndActive",
                user,
                "type", Babysitter.class.getSimpleName(),
                "latitude", String.valueOf(latitude),
                "longitude", String.valueOf(longitude));
        babysitterService.loadAllBabysittersByDistance(Babysitter.class.getSimpleName(), command)
                .enqueue(new Callback<List<Object>>() {
                    @Override
                    public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Babysitter> babysitters = convertObjectsToBabysitters(response.body());
                            listener.onBabysittersLoaded(babysitters);
                        } else {
                            db.logError(response, "fetchBabysittersByDistance");
                            listener.onFailure(new Exception("Failed to load babysitters"));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Object>> call, Throwable t) {
                        listener.onFailure(new Exception("Failed to load babysitters: " + t.getMessage()));
                    }
                });
    }

    private void fetchUserLocation(UserBoundary user, OnBabysittersLoadedListener listener) {
        userService.getObjectById(user.getUsername(), superapp, user.getUserId().getSuperapp(), user.getUserId().getEmail()).enqueue(new Callback<ObjectBoundary>() {
            @Override
            public void onResponse(Call<ObjectBoundary> call, Response<ObjectBoundary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ObjectBoundary objectBoundary = response.body();
                    double latitude = objectBoundary.getLocation().getLat();
                    double longitude = objectBoundary.getLocation().getLng();
                    fetchBabysittersByDistance(user, latitude, longitude, listener);
                } else {
                    listener.onFailure(new Exception("Failed to fetch user location"));
                }
            }

            @Override
            public void onFailure(Call<ObjectBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Failed to fetch user location: " + t.getMessage()));
            }
        });
    }

    public DataManager InitGeneralData() {
        KidEvent e1 = new KidEvent().setEventTitle("Birthday").setEDate(9, 11, 2020);
        KidEvent e12 = new KidEvent().setEventTitle("TAKE FROM TRIP").setEDate(13, 4, 2021);

        KidEvent e2 = new KidEvent().setEventTitle("Birthday").setEDate(13, 11, 2021);
        KidEvent e3 = new KidEvent().setEventTitle("Birthday").setEDate(28, 12, 2021);
        KidEvent e4 = new KidEvent().setEventTitle("Birthday").setEDate(7, 1, 2021);
        KidEvent e5 = new KidEvent().setEventTitle("Birthday").setEDate(15, 4, 2021);

        e1.initEId();
        e12.initEId();
        e2.initEId();
        e3.initEId();
        e4.initEId();
        e5.initEId();


        ImmunizationRecord i1 = new ImmunizationRecord().setVaccineName("HBV").setDoseNumber(1).setVaccinatorName("Sartori Ofira").setHMOName("Clalit").setvdate(12, 9, 2020).setCreatorName("Phizer");
        ImmunizationRecord i1a = new ImmunizationRecord().setVaccineName("HBV").setDoseNumber(2).setVaccinatorName("Sartori Ofira").setHMOName("Clalit").setvdate(12, 9, 2021).setCreatorName("Phizer");
        ImmunizationRecord i2 = new ImmunizationRecord().setVaccineName("IPV").setDoseNumber(1).setVaccinatorName("Anat Weiner").setHMOName("Macabi").setvdate(8, 12, 2020).setCreatorName("Phizer");
        ImmunizationRecord i3 = new ImmunizationRecord().setVaccineName("DTaP").setDoseNumber(1).setVaccinatorName("Dr. Cohen").setHMOName("Leumit").setvdate(6, 7, 2008).setCreatorName("Phizer");
        ImmunizationRecord i4 = new ImmunizationRecord().setVaccineName("covid19").setDoseNumber(1).setVaccinatorName("Dr. Cohen").setHMOName("Leumit").setvdate(19, 1, 2000).setCreatorName("Phizer");

        i1.initIrID();
        i1a.initIrID();
        i2.initIrID();
        i3.initIrID();
        i4.initIrID();


        Kid Eliya = new Kid().setBirthDate(9, 11, 2018).setfName("Eliya").setlName("Cohen").setProfilePhotoUri(
                "https://firebasestorage.googleapis.com/v0/b/kinderkit-68d4c.appspot.com/o/eliya.jpg?alt=media&token=9625f48e-5a77-47da-84f1-6130fe6658d5");
        Kid Ariel = new Kid().setBirthDate(13, 11, 2022).setfName("Ariel").setlName("Niazov").setProfilePhotoUri("https://firebasestorage.googleapis.com/v0/b/kinderkit-68d4c.appspot.com/o/Ariel.jpg?alt=media&token=3a1bc07a-b643-4a44-9769-b62b2eb7001b");
        Kid Tamar = new Kid().setBirthDate(28, 12, 2020).setfName("tamar").setlName("Niazov").setProfilePhotoUri("https://firebasestorage.googleapis.com/v0/b/kinderkit-68d4c.appspot.com/o/Tamar.jpg?alt=media&token=4d636048-1601-44a0-9250-71a1a0df94ef");
        Kid Daniel = new Kid().setBirthDate(7, 1, 2020).setfName("daniel").setlName("Niazov").setProfilePhotoUri("https://firebasestorage.googleapis.com/v0/b/kinderkit-68d4c.appspot.com/o/Daniel.jpg?alt=media&token=9715d152-ca07-4822-ad0d-8e1efcd759e1");
        Kid Ilay = new Kid().setBirthDate(15, 4, 2021).setfName("Ilay").setlName("Cohen").setProfilePhotoUri("https://firebasestorage.googleapis.com/v0/b/kinderkit-68d4c.appspot.com/o/Ilay.jpg?alt=media&token=3f7ea009-c34d-4692-b015-9b08fc5468a3");
        Eliya.getImmunizationRecords().add(i1);
        Eliya.getImmunizationRecords().add(i1a);
        Ilay.getImmunizationRecords().add(i2);
        Ilay.getImmunizationRecords().add(i3);
        Ilay.getImmunizationRecords().add(i4);

        Eliya.getEvents().add(e1);
        Eliya.getEvents().add(e12);
        Ariel.getEvents().add(e2);
        Tamar.getEvents().add(e3);
        Daniel.getEvents().add(e4);
        Ilay.getEvents().add(e5);

        parent.addNewKid(Eliya);
        parent.addNewKid(Ariel);
        parent.addNewKid(Tamar);
        parent.addNewKid(Daniel);
        parent.addNewKid(Ilay);
        parent.getKids().sort(Comparator.comparingInt(Kid::getAge).reversed());


        return this;
    }

    private List<Babysitter> convertObjectsToBabysitters(List<Object> objects) {
        List<Babysitter> babysitters = new ArrayList<>();
        String json = new Gson().toJson(objects);
        ArrayList<ObjectBoundary> allObjects = new Gson().fromJson(json, new TypeToken<ArrayList<ObjectBoundary>>() {
        }.getType());

        for (Object object : allObjects) {
            ObjectBoundary objectBoundary = new Gson().fromJson(new Gson().toJson(object), ObjectBoundary.class);
            Babysitter babysitter = new Gson().fromJson(new Gson().toJson(objectBoundary.getObjectDetails()), Babysitter.class);
            babysitters.add(babysitter);
        }

        return babysitters;
    }

    public void removePhotoUri(MyPhoto photo, Kid myKid) {
        myKid.getPhotosUri().remove(photo);
        db.refreshUserInDB(myKid);
        db.refreshUserInDB(parent);

        // fbmanager.removePhotoUriFB(photo, myKid);
    }

    public void deleteUser() {
        // fbmanager.deleteUser();

    }

    public void setCurrentUserEmail(String mail) {
        parent.setMail(mail);
    }

    public void loginUser(String email, String password, OnLoginListener listener) {
        setCurrentUserEmail(email);
        userService.getUserById(superapp, email).enqueue(new Callback<UserBoundary>() {
            @Override
            public void onResponse(Call<UserBoundary> call, Response<UserBoundary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserBoundary user = response.body();
                    fetchUserPassword(user, password, listener);
                } else {
                    listener.onFailure(new Exception("User not found"));
                }
            }

            @Override
            public void onFailure(Call<UserBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Network error during user fetch: " + t.getMessage()));
            }
        });
    }


    private void fetchUserPassword(UserBoundary user, String password, OnLoginListener listener) {
        userService.getObjectById(user.getUsername(), superapp, user.getUserId().getSuperapp(), user.getUserId().getEmail()).enqueue(new Callback<ObjectBoundary>() {
            @Override
            public void onResponse(Call<ObjectBoundary> call, Response<ObjectBoundary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ObjectBoundary object = response.body();
                    if (object.getCreatedBy().getUserId().getEmail().equals(user.getUserId().getEmail()) && object.getAlias().equals(password)) {
                        Parent parent = new Gson().fromJson(new Gson().toJson(object.getObjectDetails()), Parent.class);
                        listener.onSuccess(parent);

                    } else {
                        listener.onFailure(new Exception("Incorrect password"));
                    }
                } else {
                    listener.onFailure(new Exception("Password fetch failed"));
                }
            }

            ;

            @Override
            public void onFailure(Call<ObjectBoundary> call, Throwable t) {
                listener.onFailure(new Exception("Network error during password fetch: " + t.getMessage()));
            }
        });
    }

    private BabysittingEvent createBabysittingEvent(String message, String date, String
            babysitterId, String parentId) {
        BabysittingEvent babysittingEvent = new BabysittingEvent();

        babysittingEvent.setMessageId(message);
        babysittingEvent.setMessageText(message);
        babysittingEvent.setBabysitterUid(babysitterId);
        babysittingEvent.setSelectedDate(date);
        babysittingEvent.setMailParent(getParent().getMail());
        babysittingEvent.setParentUid(parentId);

        return babysittingEvent;
    }

    public void createEvent(String message, String date, String
            babysitterId, OnDataSavedListener listenerSave) {
        Log.d("Mail", "Mail: " + getParent().getMail());
        updateUserRole(getParent().getMail(), Role.SUPERAPP_USER, new OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                userService.getUserById(superapp, getParent().getMail()).enqueue(new Callback<UserBoundary>() {
                    @Override
                    public void onResponse(Call<UserBoundary> call, Response<UserBoundary> userResponse) {
                        if (userResponse.isSuccessful() && userResponse.body() != null) {
                            String parentId = userResponse.body().getUsername();
                            BabysittingEvent babysittingEvent = createBabysittingEvent(message, date, babysitterId, parentId);
                            ObjectBoundary objectBoundary = babysittingEvent.toBoundary();
                            objectBoundary.getCreatedBy().getUserId().setSuperapp(superapp);
                            Log.d("DataManager", "Event: " + objectBoundary);
                            eventService.createEvent(objectBoundary).enqueue(new Callback<ObjectBoundary>() {
                                @Override
                                public void onResponse(@NonNull Call<ObjectBoundary> call, @NonNull Response<ObjectBoundary> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.d("DataManager", "Event saved successfully: " + response.body());
                                        listenerSave.onSuccess(objectBoundary);
                                        BabysittingEvent savedEvent = babysittingEvent;
                                        savedEvent.setMessageId(response.body().getObjectId().getId());
                                        updateEvent(response.body().getObjectId().getId(), savedEvent, new OnUserUpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                KidEvent event = savedEvent.toKidEvent();
                                                Log.d("eran", "KidEvent saved successfully: " + event);
                                                for (int i = 0; i < getParent().getKids().size(); i++) {
                                                    addKidEvent(event, i);
                                                    db.refreshUserInDB(parent);
                                                }

                                                Log.d("DataManager", "Message id updated successfully");
                                                db.updateUserRole(getParent().getMail(), Role.MINIAPP_USER, new OnUserUpdateListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        Log.d("DataManager", "User role updated to MINIAPP_USER successfully");
                                                    }

                                                    @Override
                                                    public void onFailure(Exception exception) {
                                                        Log.e("DataManager", "Failed to update user role to MINIAPP_USER: " + exception.getMessage());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(Exception exception) {
                                                Log.e("DataManager", "Failed to update Message2: " + exception.getMessage());

                                            }
                                        });

                                        Log.d("DataManager", "Event saved successfully");
                                    } else {
                                        db.logError(response, "createEvent");
                                        listenerSave.onFailure(new Exception("Failed to save event data"));
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<ObjectBoundary> call, @NonNull Throwable t) {
                                    listenerSave.onFailure(new Exception("Failed to save event data: " + t.getMessage()));
                                    Log.e("DataManager", "Failed to save event data: " + t.getMessage());
                                }
                            });
                        } else {
                            listenerSave.onFailure(new Exception("Failed to fetch parent ID"));
                            Log.e("DataManager", "Failed to fetch parent ID");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserBoundary> call, Throwable t) {
                        listenerSave.onFailure(new Exception("Network error during parent ID fetch: " + t.getMessage()));
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("DataManager", "Failed to update user role to SUPERAPP_USER: " + exception.getMessage());
            }
        });
    }

    public void updateEvent(String id, BabysittingEvent babysittingEvent, OnUserUpdateListener
            listenerUpdate) {
        babysittingEvent.setMessageId(id);
        ObjectBoundary objectBoundary = babysittingEvent.toBoundary();
        objectBoundary.getObjectId().setSuperapp(superapp);
        objectBoundary.getCreatedBy().getUserId().setEmail(getParent().getMail());
        objectBoundary.getCreatedBy().getUserId().setSuperapp(DataManager.getInstance().getSuperapp());
        Log.d("DataManager444", "Event: " + objectBoundary);
        // Step 1: Update the user role to SUPERAPP_USER
        updateUserRole(objectBoundary.getCreatedBy().getUserId().getEmail(), Role.SUPERAPP_USER, new OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                // Step 2: Update the event object
                updateObject(objectBoundary, new OnUserUpdateListener() {
                    @Override
                    public void onSuccess() {
                        // Step 3: Update the user role back to MINIAPP_USER
                        updateUserRole(objectBoundary.getCreatedBy().getUserId().getEmail(), Role.MINIAPP_USER, new OnUserUpdateListener() {
                            @Override
                            public void onSuccess() {
                                // Notify the original listener that the whole operation was successful
                                listenerUpdate.onSuccess();
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Log.e("DataManager1111", "Failed to update user role to MINIAPP_USER: " + exception.getMessage());
                                // Notify the original listener about the failure in the final step
                                listenerUpdate.onFailure(exception);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.e("DataManager222", "Failed to update event object: " + exception.getMessage());
                        // Notify the original listener about the failure in the event update step
                        listenerUpdate.onFailure(exception);
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("DataManager333", "Failed to update user role: " + exception.getMessage());
                // Notify the original listener about the failure in the initial user role update step
                listenerUpdate.onFailure(exception);
            }
        });
    }

    private void updateUserRole(String email, Role role, OnUserUpdateListener listener) {
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
                                listener.onFailure(new Exception("Failed to update user role: " + db.getErrorMessage(response)));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            listener.onFailure(new Exception("Failed to update user role: " + t.getMessage()));
                        }
                    });
                } else {
                    listener.onFailure(new Exception("Failed to fetch user for role update: " + db.getErrorMessage(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserBoundary> call, @NonNull Throwable t) {
                listener.onFailure(new Exception("Network error during role update: " + t.getMessage()));
            }
        });
    }

    public void updateObject(ObjectBoundary objectBoundary, OnUserUpdateListener
            listenerUpdate) {
        userService.updateObject(objectBoundary.getObjectId().getId(), objectBoundary.getObjectId().getSuperapp(), objectBoundary.getObjectId().getSuperapp(), objectBoundary.getCreatedBy().getUserId().getEmail(), objectBoundary)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            listenerUpdate.onSuccess();
                        } else {
                            db.logError(response, "updateUser");
                            listenerUpdate.onFailure(new Exception("Failed to update user: " + db.getErrorMessage(response)));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        listenerUpdate.onFailure(new Exception("Failed to update user: " + t.getMessage()));
                    }
                });
    }

    public void loadAllBabysittingEvents(int page, int size, OnEventsLoadedListener listener) {
        updateUserRole(getParent().getMail(), Role.MINIAPP_USER, new OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                userService.getUserById(superapp, getParent().getMail()).
                        enqueue(new Callback<UserBoundary>() {
                            @Override
                            public void onResponse
                                    (Call<UserBoundary> call, Response<UserBoundary> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    eventService.loadAllBabysittingEvents(BabysittingEvent.class.getSimpleName(), superapp, getParent().getMail())
                                            .enqueue(new Callback<List<ObjectBoundary>>() {
                                                @Override
                                                public void onResponse(Call<List<ObjectBoundary>> call, Response<List<ObjectBoundary>> response) {
                                                    if (response.isSuccessful() && response.body() != null) {
                                                        List<BabysittingEvent> events = convertObjectBoundaryToBabysittingEvents(response.body());
                                                        listener.onEventsLoaded(events);
                                                    } else {
                                                        listener.onFailure(new Exception("Failed to load messages"));
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<List<ObjectBoundary>> call, Throwable t) {
                                                    listener.onFailure(new Exception("Failed to load messages: " + t.getMessage()));
                                                }
                                            });

                                } else {
                                    Log.e("DataManager", "Failed to fetch parent ID");
                                    listener.onFailure(new Exception("Failed to save event data"));
                                }
                            }

                            @Override
                            public void onFailure(Call<UserBoundary> call, Throwable t) {
                                listener.onFailure(new Exception("Network error during parent ID fetch: " + t.getMessage()));
                            }
                        });
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("DataManager", "Failed to update user role to SUPERAPP_USER: " + exception.getMessage());
            }
        });
    }


    private List<BabysittingEvent> convertObjectsToEvents(List<Object> objects) {
        List<BabysittingEvent> events = new ArrayList<>();
        String json = new Gson().toJson(objects);
        ArrayList<ObjectBoundary> allObjects = new Gson().fromJson(json, new TypeToken<ArrayList<ObjectBoundary>>() {
        }.getType());

        for (Object object : allObjects) {
            ObjectBoundary objectBoundary = new Gson().fromJson(new Gson().toJson(object), ObjectBoundary.class);
            BabysittingEvent event = new Gson().fromJson(new Gson().toJson(objectBoundary.getObjectDetails()), BabysittingEvent.class);
            events.add(event);
            Log.d("DataManager", "Event: " + event);
        }

        return events;
    }


    public interface OnLogoutListener {
        void onLogoutSuccess();

        void onLogoutFailure(Exception exception);
    }

    public interface OnKidsLoadedListener {
        void onKidsLoaded(ArrayList<Kid> kids);

        void onFailure(Exception exception);
    }

    public interface OnBabysittersLoadedListener {
        void onBabysittersLoaded(List<Babysitter> babysitters);

        void onFailure(Exception exception);
    }

    public interface OnUserCreationListener {
        void onUserCreated(String email);

        void onFailure(Exception exception);
    }

    public interface OnDataSavedListener {
        void onSuccess(ObjectBoundary objectBoundary);

        void onFailure(Exception exception);
    }

    public interface OnUserUpdateListener {
        void onSuccess();

        void onFailure(Exception exception);
    }

    public interface OnEventsLoadedListener {
        void onEventsLoaded(List<BabysittingEvent> events);

        void onFailure(Exception exception);
    }


    public interface OnLoginListener {
        void onSuccess(Parent parent);

        void onFailure(Exception exception);
    }

}