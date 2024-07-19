//package dev.netanelbcn.kinderkit.Uilities;
//
//
//import android.net.Uri;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.gson.Gson;
//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.UUID;
//
//import dev.netanelbcn.kinderkit.Interfaces.DataLoadCallback;
//import dev.netanelbcn.kinderkit.Models.ImmunizationRecord;
//import dev.netanelbcn.kinderkit.Models.Kid;
//import dev.netanelbcn.kinderkit.Models.KidEvent;
//import dev.netanelbcn.kinderkit.Models.MyPhoto;
//
//
//public class FBmanager {
//    private DatabaseReference ref;
//    private FirebaseDatabase firebaseRTDatabase;
//    private FirebaseUser user;
//    private ArrayList<Kid> kids;
//
//    public FBmanager(FirebaseUser user) {
//        this.firebaseRTDatabase = FirebaseDatabase.getInstance();
//        this.user = user;
//        this.kids = new ArrayList<>();
//        ref = firebaseRTDatabase.getReference(this.user.getUid());
//    }
//
//    public ArrayList<Kid> getKids() {
//        return kids;
//    }
//
////    public void LoadDataFromFBRTDB(DataLoadCallback callback) {
////        DatabaseReference myRef = firebaseRTDatabase.getReference(this.user.getUid());
////        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                for (DataSnapshot kidSnapshot : dataSnapshot.getChildren()) {
////                    Kid myKid = buildKidBasicInfo(kidSnapshot);
////                    Map<String, ImmunizationRecord> myIR = (Map<String, ImmunizationRecord>) kidSnapshot.child("Immunizations").getValue();
////                    myKid.setImmunizationRecords(myKid.convertIRtoArrayList(myIR));
////                    Map<String, KidEvent> myKE = (Map<String, KidEvent>) kidSnapshot.child("Events").getValue();
////                    myKid.setEvents(myKid.convertKEtoArrayList(myKE));
////                    Map<String, String> photosUri = (Map<String, String>) kidSnapshot.child("photosUri").getValue();
////                    myKid.setPhotosUri(myKid.convertMapToMyPhotoArrayList(photosUri));
////                    kids.add(myKid);
////
////                }
////                callback.onDataLoaded();
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////                // Handle errors
////                Log.w("RecipeData", "Failed to read value.", databaseError.toException());
////            }
////        });
////    }
//
//    private Kid buildKidBasicInfo(String data) {
//        Kid kid = new Gson().fromJson(data, Kid.class);
//        String kidId = kid.getkId();
//        String fName = kid.getfName();
//        String lName = kid.getfName();
//        int day =  kid.getBirthDate().getDay();
//        int month =  kid.getBirthDate().getMonth();
//        int year =  kid.getBirthDate().getYear();
//        String profilePhotoUri =  kid.getProfilePhotoUri()==null?"":kid.getProfilePhotoUri().toString();
//        return new Kid(kidId).setBirthDate(day, month, year).setfName(fName).setlName(lName).setProfilePhotoUri(Uri.parse(profilePhotoUri)).setKidMail(user.getEmail());
//    }
//
//    public void addKidToDB(Kid kid) {
//        addKidBaseInfoToDB(kid);
//        addKidIRsToDB(kid);
//        addKidEventsToDB(kid);
//    }
//
//    public void addImmunizationRecordToDB(ImmunizationRecord iR, Kid kid) {
//        ref.child(kid.getkId() + "").child("Immunizations").child(iR.getIrID()).setValue(iR);
//    }
//
//    public void addKidEventToDB(KidEvent kEvent, Kid kid) {
//        ref.child(kid.getkId() + "").child("Events").child(kEvent.geteId()).setValue(kEvent);
//    }
//
//    public void addPhotoUriToDB(Uri uri, Kid kid) {
//        ref.child(kid.getkId() + "").child("photosUri").child(UUID.randomUUID().toString()).setValue(uri.toString());
//        int x=10;
//    }
//
//
//    private void addKidIRsToDB(Kid kid) {
//        ref.child(kid.getkId() + "").child("Immunizations").setValue(kid.getIRMap());
//    }
//
//    private void addKidEventsToDB(Kid kid) {
//        ref.child(kid.getkId() + "").child("Events").setValue(kid.getKEMap());
//    }
//    private void addKidBaseInfoToDB(Kid kid) {
//        ref.child(kid.getkId() + "").child("name").child("fName").setValue(kid.getfName());
//        ref.child(kid.getkId() + "").child("name").child("lName").setValue(kid.getlName());
//        ref.child(kid.getkId() + "").child("birthDate").setValue(kid.getBirthDate());
//        ref.child(kid.getkId() + "").child("age").setValue(kid.getAge());
//        if (kid.getPhotosUri() != null)
//            ref.child(kid.getkId() + "").child("photosUri").setValue(kid.getPhotosUriMap());
//        int x = 10;
//        if (kid.getProfilePhotoUri() != null)
//            ref.child(kid.getkId() + "").child("profilePhotoUri").setValue(kid.getProfilePhotoUri().toString());
//    }
//    public void removeImmunizationRecordFB(ImmunizationRecord iR, Kid kid) {
//        ref.child(kid.getkId()).child("Immunizations").child(iR.getIrID()).removeValue();
//    }
//    public void removeKidEventFB(KidEvent kEvent, Kid kid) {
//        ref.child(kid.getkId() + "").child("Events").child(kEvent.geteId()).removeValue();
//    }
//    public void removeKidFromDB(String kId) {
//        ref.child(kId).removeValue();
//    }
//
//    public void setProfilePhotoUriToDB(Uri uri, Kid kid) {
//        ref.child(kid.getkId() + "").child("profilePhotoUri").setValue(uri.toString());
//    }
//    public void removePhotoUriFB(MyPhoto photo, Kid myKid) {
//        ref.child(myKid.getkId() + "").child("photosUri").child(photo.getpId()).removeValue();
//    }
//    public void deleteUser() {
//        ref.removeValue();
//    }
//}