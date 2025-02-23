package co.resume.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerWithEmail(
        email: String,
        password: String,
        name: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            onComplete(false, "Name, Email, and Password must not be empty")
            return
        }

        firestoreDb.collection(Constants.FIREBASE_STORE_USERS)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    onComplete(false, "Email already exists. Please log in.")
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                                saveUserData(
                                    userId,
                                    name,
                                    email,
                                    password,
                                    Constants.EMAIL,
                                    onComplete
                                )
                            } else {
                                onComplete(false, task.exception?.localizedMessage)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error checking email: ${e.localizedMessage}")
            }
    }

    // Save user details to Firestore
    private fun saveUserData(
        userId: String,
        name: String,
        email: String,
        password: String,
        loginType: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userMap = hashMapOf(
            "userId" to userId,
            "name" to name,
            "email" to email,
            "password" to password,
            "loginType" to loginType,
        )

        firestoreDb.collection(Constants.FIREBASE_STORE_USERS).document(userId).set(userMap)
            .addOnSuccessListener {
                onComplete(true, "Registration Successful & Data Saved")
            }
            .addOnFailureListener { e ->
                onComplete(false, e.localizedMessage)
            }
    }

    fun checkIfEmailExists(email: String, onComplete: (Boolean) -> Unit) {
        firestoreDb.collection(Constants.FIREBASE_STORE_USERS)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                onComplete(!documents.isEmpty)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    // Login with email and password
    fun loginWithEmail(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, "Login Successful")
                } else {
                    onComplete(false, task.exception?.localizedMessage)
                }
            }
    }

    fun checkIfUserExists(email: String, onComplete: (Boolean) -> Unit) {
        firestoreDb.collection(Constants.FIREBASE_STORE_USERS)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                onComplete(!documents.isEmpty)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun firebaseAuthWithGoogle(idToken: String, onComplete: (Boolean, String?, Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userEmail = auth.currentUser?.email ?: ""
                    checkIfUserExists(userEmail) { exists ->
                        if (exists) {
                            onComplete(true, "Google Sign-In Successful", false)
                        } else {
                            val userName = auth.currentUser?.displayName ?: "New User"
                            saveUserDataToFirestore(
                                userEmail,
                                userName,
                                Constants.GOOGLE
                            ) { success, message ->
                                onComplete(success, message, true)
                            }
                        }
                    }
                } else {
                    onComplete(false, task.exception?.localizedMessage, false)
                }
            }
    }

    fun saveUserDataToFirestore(
        email: String,
        name: String,
        loginType: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userMap = hashMapOf(
            "name" to name,
            "email" to email,
            "loginType" to loginType,
        )

        firestoreDb.collection(Constants.FIREBASE_STORE_USERS)
            .add(userMap)
            .addOnSuccessListener {
                onComplete(true, "New User Registered with Google")
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error saving user data: ${e.localizedMessage}")
            }
    }

    fun fetchUserData(onComplete: (Boolean, String?, String?, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onComplete(false, "User not logged in", null, null)
            return
        }

        firestoreDb.collection(Constants.FIREBASE_STORE_USERS).document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name")
                    val email = document.getString("email")
                    val password = document.getString("password")
                    onComplete(true, name, email, password)
                } else {
                    onComplete(false, "User data not found", null, null)
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error fetching user data: ${e.localizedMessage}", null, null)
            }
    }

    fun googleLogout(context: Context,onComplete: () -> Unit) {
        auth.signOut()
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            .addOnCompleteListener {
                onComplete()
            }
    }

    fun firebaseLogout(onComplete: () -> Unit) {
        auth.signOut()
        onComplete()
    }

    fun changePasswordWithVerification(
        currentPassword: String,
        newPassword: String,
        currentUserEmail: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onComplete(false, "User not logged in")
            return
        }

        if (currentUserEmail.isEmpty()) {
            onComplete(false, "Email not found for the logged-in user")
            return
        }

        val credential = EmailAuthProvider.getCredential(currentUserEmail, currentPassword)
        currentUser.reauthenticate(credential)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    currentUser.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                updatePasswordInFirestore(currentUser.uid, newPassword, onComplete)
                            } else {
                                onComplete(false, updateTask.exception?.localizedMessage)
                            }
                        }
                } else {
                    onComplete(false, "Current password is incorrect")
                }
            }
    }

    private fun updatePasswordInFirestore(
        userId: String,
        newPassword: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val userRef = firestoreDb.collection(Constants.FIREBASE_STORE_USERS).document(userId)

        userRef.update("password", newPassword)
            .addOnSuccessListener {
                onComplete(true, "Password updated successfully")
            }
            .addOnFailureListener { e ->
                onComplete(false, "Error updating password in Firestore: ${e.localizedMessage}")
            }
    }

    fun deleteUserAccount(onComplete: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (userId == null) {
            onComplete(false, "User not logged in")
            return
        }

        // Delete data from Firestore first
        firestoreDb.collection(Constants.FIREBASE_STORE_USERS).document(userId)
            .delete()
            .addOnSuccessListener {
                // Then delete the user from Firebase Authentication
                currentUser.delete()
                    .addOnSuccessListener {
                        onComplete(true, "User account deleted successfully")
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, "Failed to delete from Firebase Auth: ${e.localizedMessage}")
                    }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Failed to delete Firestore data: ${e.localizedMessage}")
            }
    }


}
