package com.eventhub.eventhub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventhub.eventhub.model.User;
import com.eventhub.eventhub.security.JwtUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    // REGISTER API
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        try {

            Firestore db = FirestoreClient.getFirestore();

            db.collection("users")
                    .document(user.getEmail())
                    .set(user)
                    .get();

            return "User Registered Successfully";

        } catch (Exception e) {

            e.printStackTrace();
            return e.getMessage();
        }
    }

    // LOGIN API
    @PostMapping("/login")
    public String login(@RequestBody User user) {

        try {

            Firestore db = FirestoreClient.getFirestore();

            DocumentSnapshot document =
                    db.collection("users")
                            .document(user.getEmail())
                            .get()
                            .get();

            if (!document.exists()) {
                return "User Not Found";
            }

            User savedUser = document.toObject(User.class);

            if (savedUser == null) {
                return "User Data Not Found";
            }

            if (!savedUser.getPassword().equals(user.getPassword())) {
                return "Invalid Password";
            }

            return jwtUtil.generateToken(user.getEmail());

        } catch (Exception e) {

            e.printStackTrace();
            return e.getMessage();
        }
    }

    // GET ALL USERS API
   @GetMapping("/users")
public List<User> getAllUsers() throws Exception {

    Firestore db = FirestoreClient.getFirestore();

    List<QueryDocumentSnapshot> documents =
            db.collection("users")
                    .get()
                    .get()
                    .getDocuments();

    return documents.stream()
            .map(doc -> doc.toObject(User.class))
            .toList();
}
    }